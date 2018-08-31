package com.wallet.crypto.trustapp.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import com.wallet.crypto.trustapp.C;
import com.wallet.crypto.trustapp.R;
import com.wallet.crypto.trustapp.entity.ErrorEnvelope;
import com.wallet.crypto.trustapp.entity.NetworkInfo;
import com.wallet.crypto.trustapp.entity.Transaction;
import com.wallet.crypto.trustapp.entity.Wallet;
import com.wallet.crypto.trustapp.ui.widget.adapter.TransactionsAdapter;
import com.wallet.crypto.trustapp.util.RootUtil;
import com.wallet.crypto.trustapp.viewmodel.BaseNavigationActivity;
import com.wallet.crypto.trustapp.viewmodel.BrowserViewModel;
import com.wallet.crypto.trustapp.viewmodel.BrowserViewModelFactory;
import com.wallet.crypto.trustapp.viewmodel.TransactionsViewModel;
import com.wallet.crypto.trustapp.viewmodel.TransactionsViewModelFactory;
import com.wallet.crypto.trustapp.widget.DepositView;
import com.wallet.crypto.trustapp.widget.EmptyTransactionsView;
import com.wallet.crypto.trustapp.widget.SystemView;

import java.util.Map;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

import static com.wallet.crypto.trustapp.C.ETHEREUM_NETWORK_NAME;

public class BrowserActivity extends BaseNavigationActivity {
    @Inject
    BrowserViewModelFactory browserViewModelFactory;

    private static final String DEFAULT_WEB_URL = "https://www.stateofthedapps.com/";

    private BrowserViewModel viewModel;
    private WebView webView;
    private EditText editText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_browser);

        webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                setBrowseText(url);
                view.loadUrl(url);
                return false;
            }
        });
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        toolbar();
        setTitle(getString(R.string.title_browser));
        initBottomNavigation();
        dissableDisplayHomeAsUp();

        viewModel = ViewModelProviders.of(this, browserViewModelFactory)
                .get(BrowserViewModel.class);
        setBottomMenu(R.menu.menu_main_network);

        editText = findViewById(R.id.web_edit_text);
        setBrowseText(DEFAULT_WEB_URL);

        editText.setOnKeyListener((View v, int keyCode, KeyEvent event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                loadEditText();
                return true;
            }
            return false;
        });

        loadEditText();
    }

    private void loadEditText() {
        String text = editText.getText().toString();
        if(!text.startsWith("http")) {
            text = "https://" + text;
        }
        setBrowseText(text);
        webView.loadUrl(editText.getText().toString());
    }

    private void setBrowseText(String text) {
        editText.setText(text);
        editText.setSelection(editText.getText().length());
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkRoot();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_transaction: {
//                viewModel.showTransactions(this, true);
//                return true;
//            }
//            case R.id.action_my_tokens: {
//                viewModel.showAddToken(this);
//                return true;
//            }
//        }
        return false;
    }

    private void checkRoot() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if (RootUtil.isDeviceRooted() && pref.getBoolean("should_show_root_warning", true)) {
            pref.edit().putBoolean("should_show_root_warning", false).apply();
            new AlertDialog.Builder(this)
                    .setTitle(R.string.root_title)
                    .setMessage(R.string.root_body)
                    .setNegativeButton(R.string.ok, (dialog, which) -> {
                    })
                    .show();
        }
    }

}
