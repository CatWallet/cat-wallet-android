package com.wallet.crypto.trustapp.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.wallet.crypto.trustapp.C;
import com.wallet.crypto.trustapp.R;
import com.wallet.crypto.trustapp.entity.GasSettings;
import com.wallet.crypto.trustapp.entity.NetworkInfo;
import com.wallet.crypto.trustapp.entity.Wallet;
import com.wallet.crypto.trustapp.service.TrustWalletTickerService;
import com.wallet.crypto.trustapp.ui.barcode.BarcodeCaptureActivity;
import com.wallet.crypto.trustapp.util.BalanceUtils;
import com.wallet.crypto.trustapp.util.QRURLParser;
import com.wallet.crypto.trustapp.viewmodel.SendViewModel;
import com.wallet.crypto.trustapp.viewmodel.SendViewModelFactory;

import org.ethereum.geth.Address;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class SendActivity extends BaseActivity {

    @Inject
    SendViewModelFactory sendViewModelFactory;
    SendViewModel viewModel;

    private static final int BARCODE_READER_REQUEST_CODE = 1;
    private static final int INPUT_DISPLAY_EDITTEXT_AMOUNT_SCALE = 15;

    private EditText toAddressText;
    private EditText amountText;
    private EditText currencyAmountText;

    // In case we're sending tokens
    private boolean sendingTokens = false;
    private String contractAddress;
    private int decimals;
    private String symbol;
    private String currencySymbol;
    private TextInputLayout toInputLayout;
    private TextInputLayout amountInputLayout;
    private TextInputLayout currencyInputLayout;

    NetworkInfo networkInfo;

    private String MaxTransferETH;
    private String MaxTransferUSD;
    private String GasNum;
    private String GasPrice;
    private GasSettings gasSettings;
    private String tickerPrice;
    private BigDecimal networkFee;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_send);
        toolbar();


        viewModel = ViewModelProviders.of(this, sendViewModelFactory)
                .get(SendViewModel.class);

        toInputLayout = findViewById(R.id.to_input_layout);
        toAddressText = findViewById(R.id.send_to_address);
        amountInputLayout = findViewById(R.id.amount_input_layout);
        amountText = findViewById(R.id.send_amount);
        currencyInputLayout = findViewById(R.id.currency_amount_input_layout);
        currencyAmountText = findViewById(R.id.send_amount_currency);

        contractAddress = getIntent().getStringExtra(C.EXTRA_CONTRACT_ADDRESS);
        decimals = getIntent().getIntExtra(C.EXTRA_DECIMALS, C.ETHER_DECIMALS);
        symbol = getIntent().getStringExtra(C.EXTRA_SYMBOL);
        symbol = symbol == null ? C.ETH_SYMBOL : symbol;
        currencySymbol = getIntent().getStringExtra(C.USD_SYMBOL);
        currencySymbol = currencySymbol == null ? C.USD_SYMBOL : currencySymbol;
        sendingTokens = getIntent().getBooleanExtra(C.EXTRA_SENDING_TOKENS, false);

        //setTitle(getString(R.string.title_send) + " " + symbol);
        amountInputLayout.setHint(getString(R.string.hint_amount));
        currencyInputLayout.setHint(getString(R.string.hint_currency_amount));


        // Populate to address if it has been passed forward
        String toAddress = getIntent().getStringExtra(C.EXTRA_ADDRESS);
        if (toAddress != null) {
            toAddressText.setText(toAddress);
        }

        ImageButton scanBarcodeButton = findViewById(R.id.scan_barcode_button);
        scanBarcodeButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
            startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
        });

        Button max_amount_button = findViewById(R.id.max_button);



        BalanceUtils.changeDisplayBalance("","",findViewById(android.R.id.content));


        viewModel.defaultWalletBalance().observe(this, this::onBalanceChanged);
        viewModel.gasSettings().observe(this, this::getGasSettings);
        viewModel.defaultWalletBalance().observe(this, this::setMaxTransferAmount);

        amountText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(getCurrentFocus().getId() == amountText.getId()) {
                    if(s.length() != 0) {
                        updateFromInputETH(amountText.getText().toString());
                    }else{
                        updateFromInputETH("0");
                    }
                }


            }
        });

        currencyAmountText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(getCurrentFocus().getId() == currencyAmountText.getId()) {
                    if(s.length() != 0){
                        updateFromInputUSD(currencyAmountText.getText().toString());
                    }else{
                        updateFromInputUSD("0");
                    }

                }
            }
        });
    }

    //when click max_button
    public void setToMaxTransferAmount(View view){
        updateFromInputETH(MaxTransferETH);
        updateFromInputUSD(MaxTransferUSD);
    }

    //when lick paste_button
    public void pasteFromClipboard(View view){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        String pasteData = "";

        // If it does contain data, decide if you can handle the data.
        if (!(clipboard.hasPrimaryClip())) {

        } else if (!(clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))) {

            // since the clipboard has data but it is not plain text

        } else {

            //since the clipboard contains plain text.
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);

            // Gets the clipboard as text.
            pasteData = item.getText().toString();
            toAddressText.setText(pasteData);
        }
    }

    //when click clear button
    public void clearAmount(View view){
        amountText.setText("");
        currencyAmountText.setText("");
    }

    public void getGasSettings(GasSettings gasSettings){
        this.gasSettings = gasSettings;
        networkFee = BalanceUtils.weiToEth(gasSettings.gasPrice.multiply(gasSettings.gasLimit));// + " " + C.ETH_SYMBOL
    }
    public void setMaxTransferAmount(Map<String, String> balance){
        networkInfo = viewModel.defaultNetwork().getValue();
        tickerPrice = balance.get(symbol+"To"+currencySymbol);
        //String gasPrice = BalanceUtils.weiToGwei(gasSettings.gasPrice);// + " " + C.GWEI_UNIT

        MaxTransferETH = new BigDecimal(balance.get(networkInfo.symbol)).subtract(networkFee).toPlainString();
        MaxTransferUSD = BalanceUtils.ethToUsd(tickerPrice, MaxTransferETH, INPUT_DISPLAY_EDITTEXT_AMOUNT_SCALE);
        //network fee may larger than users balance. Here We decide to display it directly.
        //if(networkFee.compareTo(new BigDecimal(balance.get(networkInfo.symbol))) < 0){
        //      MaxTransferETH = new BigDecimal(balance.get(networkInfo.symbol)).subtract(networkFee).toPlainString();
        //      MaxTransferUSD = BalanceUtils.ethToUsd(tickerPrice, MaxTransferETH, INPUT_DISPLAY_EDITTEXT_AMOUNT_SCALE);
        //  }else{
        //      MaxTransferETH = new BigDecimal(balance.get(networkInfo.symbol)).toPlainString();
        //      MaxTransferUSD = BalanceUtils.ethToUsd(tickerPrice, MaxTransferETH, INPUT_DISPLAY_EDITTEXT_AMOUNT_SCALE);
        //  }
    }




    public void updateFromInputETH(String ETHAmount){
        if(ETHAmount != null && !ETHAmount.equals(".") &&ETHAmount.length()>0 && new BigDecimal(ETHAmount).compareTo(new BigDecimal("0")) > 0 ){
            String USDAmount =  BalanceUtils.ethToUsd(tickerPrice, ETHAmount, INPUT_DISPLAY_EDITTEXT_AMOUNT_SCALE);
            currencyAmountText.setText(USDAmount);
        }else{
            currencyAmountText.setText("0");
        }

    }

    public void updateFromInputUSD(String USDAmount){
        if(USDAmount != null && !USDAmount.equals(".") && USDAmount.length()>0 && new BigDecimal(USDAmount).compareTo(new BigDecimal("0")) > 0 ) {
            String ETHAmount = BalanceUtils.usdToEth(tickerPrice, USDAmount, INPUT_DISPLAY_EDITTEXT_AMOUNT_SCALE);
            amountText.setText(ETHAmount);
        }else{
            amountText.setText("0");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_next: {
                onNext();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);

                    QRURLParser parser = QRURLParser.getInstance();
                    String extracted_address = parser.extractAddressFromQrString(barcode.displayValue);
                    if (extracted_address == null) {
                        Toast.makeText(this, R.string.toast_qr_code_no_address, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Point[] p = barcode.cornerPoints;
                    toAddressText.setText(extracted_address);
                }
            } else {
                Log.e("SEND", String.format(getString(R.string.barcode_error_format),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void onNext() {
        // Validate input fields
        boolean inputValid = true;
        final String to = toAddressText.getText().toString();
        if (!isAddressValid(to)) {
            toInputLayout.setError(getString(R.string.error_invalid_address));
            inputValid = false;
        }
        final String amount = amountText.getText().toString();
        if (!isValidAmount(amount)) {
            amountInputLayout.setError(getString(R.string.error_invalid_amount));
            inputValid = false;
        }

        final String currencyAmount = currencyAmountText.getText().toString();
        if (!isValidAmount(currencyAmount)) {
            currencyInputLayout.setError(getString(R.string.error_invalid_amount));
            if(!isValidAmount(amount)){
                inputValid = false;
            }
        }


        if (!inputValid) {
            return;
        }

        toInputLayout.setErrorEnabled(false);
        amountInputLayout.setErrorEnabled(false);

        BigInteger amountInSubunits = BalanceUtils.baseToSubunit(amount, decimals);
        viewModel.openConfirmation(this, to, amountInSubunits, contractAddress, decimals, symbol, sendingTokens);
    }

    boolean isAddressValid(String address) {
        try {
            new Address(address);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    boolean isValidAmount(String eth) {
        try {
            String wei = BalanceUtils.EthToWei(eth);
            return wei != null;
        } catch (Exception e) {
            return false;
        }
    }

    private void onBalanceChanged(Map<String, String> balance) {
        ActionBar actionBar = getSupportActionBar();
        NetworkInfo networkInfo = viewModel.defaultNetwork().getValue();
        Wallet wallet = viewModel.defaultWallet().getValue();
        if (actionBar == null || networkInfo == null || wallet == null) {
            return;
        }
        if (TextUtils.isEmpty(balance.get(C.USD_SYMBOL))) {
            //actionBar.setTitle(balance.get(networkInfo.symbol) + " " + networkInfo.symbol);
            //actionBar.setSubtitle("");
            BalanceUtils.changeDisplayBalance(balance.get(networkInfo.symbol) + " " + networkInfo.symbol, "", findViewById(android.R.id.content));
        } else {
            //actionBar.setTitle("$" + balance.get(C.USD_SYMBOL));
            //actionBar.setSubtitle(balance.get(networkInfo.symbol) + " " + networkInfo.symbol);
            BalanceUtils.changeDisplayBalance("$" + balance.get(C.USD_SYMBOL), balance.get(networkInfo.symbol) + " " + networkInfo.symbol, findViewById(android.R.id.content));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        BalanceUtils.changeDisplayBalance(getString(R.string.unknown_balance_without_symbol),"",findViewById(android.R.id.content));
        viewModel.prepare();
    }

}
