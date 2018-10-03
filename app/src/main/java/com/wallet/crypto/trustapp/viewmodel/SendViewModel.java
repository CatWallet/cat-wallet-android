package com.wallet.crypto.trustapp.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.wallet.crypto.trustapp.R;
import com.wallet.crypto.trustapp.entity.CurrencyInfo;
import com.wallet.crypto.trustapp.entity.GasSettings;
import com.wallet.crypto.trustapp.entity.NetworkInfo;
import com.wallet.crypto.trustapp.entity.Transaction;
import com.wallet.crypto.trustapp.entity.Wallet;
import com.wallet.crypto.trustapp.interact.FetchGasSettingsInteract;
import com.wallet.crypto.trustapp.interact.FindDefaultNetworkInteract;
import com.wallet.crypto.trustapp.interact.FindDefaultWalletInteract;
import com.wallet.crypto.trustapp.interact.GetDefaultWalletBalance;
import com.wallet.crypto.trustapp.router.ConfirmationRouter;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class SendViewModel extends BaseViewModel {
    private final MutableLiveData<Wallet> defaultWallet = new MutableLiveData<>();
    private final ConfirmationRouter confirmationRouter;
    private final GetDefaultWalletBalance getDefaultWalletBalance;
    private Disposable balanceDisposable;
    private static final long GET_BALANCE_INTERVAL = 8;
    private final MutableLiveData<NetworkInfo> defaultNetwork = new MutableLiveData<>();
    private final MutableLiveData<CurrencyInfo> defaultCurrency= new MutableLiveData<>();
    private final MutableLiveData<Map<String, String>> defaultWalletBalance = new MutableLiveData<>();
    private final FindDefaultNetworkInteract findDefaultNetworkInteract;
    private final FindDefaultWalletInteract findDefaultWalletInteract;
    private final FetchGasSettingsInteract fetchGasSettingsInteract;
    private final MutableLiveData<GasSettings> gasSettings = new MutableLiveData<>();


    SendViewModel(ConfirmationRouter confirmationRouter, GetDefaultWalletBalance getDefaultWalletBalance,
                  FindDefaultNetworkInteract findDefaultNetworkInteract, FindDefaultWalletInteract findDefaultWalletInteract,
                  FetchGasSettingsInteract fetchGasSettingsInteract) {
        this.confirmationRouter = confirmationRouter;
        this.findDefaultNetworkInteract = findDefaultNetworkInteract;
        this.getDefaultWalletBalance = getDefaultWalletBalance;
        this.findDefaultWalletInteract = findDefaultWalletInteract;
        this.fetchGasSettingsInteract = fetchGasSettingsInteract;
    }

    public LiveData<NetworkInfo> defaultNetwork() {
        return defaultNetwork;
    }
    public LiveData<CurrencyInfo> defaultCurrency() {
        return defaultCurrency;
    }

    public LiveData<Wallet> defaultWallet() {
        return defaultWallet;
    }

    public LiveData<Map<String, String>> defaultWalletBalance() {
        return defaultWalletBalance;
    }

    public void openConfirmation(Context context, String to, BigInteger amount, String contractAddress, int decimals, String symbol, boolean sendingTokens) {
        confirmationRouter.open(context, to, amount, contractAddress, decimals, symbol, sendingTokens);
    }

    public void prepare() {
        progress.postValue(true);
        disposable = findDefaultNetworkInteract
                .find()
                .subscribe(this::onDefaultNetwork, this::onError);
        findDefaultNetworkInteract
                .findCurrency()
                .subscribe(this::onDefaultCurrency, this::onError);
    }

    public void getBalance() {
        balanceDisposable = Observable.interval(0, GET_BALANCE_INTERVAL, TimeUnit.SECONDS)
                .doOnNext(l -> getDefaultWalletBalance
                        .get(defaultWallet.getValue())
                        .subscribe(defaultWalletBalance::postValue, t -> {}))
                .subscribe();
    }

    private void onDefaultNetwork(NetworkInfo networkInfo) {
        defaultNetwork.postValue(networkInfo);
        disposable = findDefaultWalletInteract
                .find()
                .subscribe(this::onDefaultWallet, this::onError);
    }

    private void onDefaultCurrency(CurrencyInfo currencyInfo) {
        defaultCurrency.postValue(currencyInfo);
        disposable = findDefaultWalletInteract
                .find()
                .subscribe(this::onDefaultWallet, this::onError);
    }

    private void onDefaultWallet(Wallet wallet) {
        defaultWallet.setValue(wallet);
        if (gasSettings.getValue() == null) {
            onGasSettings(fetchGasSettingsInteract.fetch(false));
        }
        getBalance();
    }


    private void onGasSettings(GasSettings gasSettings) {
        this.gasSettings.setValue(gasSettings);
    }

    public MutableLiveData<GasSettings> gasSettings() {
        return gasSettings;
    }

}
