package com.wallet.crypto.trustapp.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.wallet.crypto.trustapp.entity.NetworkInfo;
import com.wallet.crypto.trustapp.entity.Token;
import com.wallet.crypto.trustapp.entity.Transaction;
import com.wallet.crypto.trustapp.entity.Wallet;
import com.wallet.crypto.trustapp.interact.FindDefaultNetworkInteract;
import com.wallet.crypto.trustapp.interact.FindDefaultWalletInteract;
import com.wallet.crypto.trustapp.router.AddTokenRouter;
import com.wallet.crypto.trustapp.router.MyAddressRouter;
import com.wallet.crypto.trustapp.router.MyBrowserRouter;
import com.wallet.crypto.trustapp.router.MyTokensRouter;
import com.wallet.crypto.trustapp.router.SendRouter;
import com.wallet.crypto.trustapp.router.SendTokenRouter;
import com.wallet.crypto.trustapp.router.TransactionsRouter;

public class BrowserViewModel extends BaseViewModel {

    private final AddTokenRouter addTokenRouter;
    private final SendTokenRouter sendTokenRouter;
    private final TransactionsRouter transactionsRouter;
    private final MyAddressRouter myAddressRouter;
    private final MyTokensRouter myTokensRouter;
    private final SendRouter sendRouter;
    private final MyBrowserRouter myBrowserRouter;
    private final FindDefaultWalletInteract findDefaultWalletInteract;
    private final FindDefaultNetworkInteract findDefaultNetworkInteract;
    public final MutableLiveData<Wallet> defaultWallet = new MutableLiveData<>();
    public final MutableLiveData<NetworkInfo> defaultNetwork = new MutableLiveData<>();

    BrowserViewModel(
            AddTokenRouter addTokenRouter,
            SendTokenRouter sendTokenRouter,
            TransactionsRouter transactionsRouter,
            MyAddressRouter myAddressRouter,
            MyTokensRouter myTokensRouter,
            SendRouter sendRouter,
            MyBrowserRouter myBrowserRouter,
            FindDefaultWalletInteract findDefaultWalletInteract,
            FindDefaultNetworkInteract findDefaultNetworkInteract
    ) {
        this.findDefaultWalletInteract = findDefaultWalletInteract;
        this.findDefaultNetworkInteract = findDefaultNetworkInteract;
        this.myBrowserRouter = myBrowserRouter;
        this.addTokenRouter = addTokenRouter;
        this.sendTokenRouter = sendTokenRouter;
        this.transactionsRouter = transactionsRouter;
        this.myAddressRouter = myAddressRouter;
        this.myTokensRouter = myTokensRouter;
        this.sendRouter = sendRouter;
    }

    public void showAddToken(Context context) {
        addTokenRouter.open(context);
    }

    public void showSendToken(Context context, String address, String symbol, int decimals) {
        sendTokenRouter.open(context, address, symbol, decimals);

    }

    public void showTransactions(Context context, boolean isClearStack) {
        transactionsRouter.open(context, isClearStack);
    }

    public LiveData<Wallet> defaultWallet() {
        return defaultWallet;
    }

    private void onDefaultNetwork(NetworkInfo networkInfo) {
        defaultNetwork.postValue(networkInfo);
        disposable = findDefaultWalletInteract
                .find()
                .subscribe(this::onDefaultWallet, this::onError);
    }

    private void onDefaultWallet(Wallet wallet) {
        defaultWallet.setValue(wallet);
        //fetchTransactions();
    }

    public void showSend(Context context) { sendRouter.open(context); }


    public void showMyAddress(Context context) {
        myAddressRouter.open(context, defaultWallet.getValue());
    }

    public void showTokens(Context context) {
        myTokensRouter.open(context, defaultWallet.getValue());
    }

    public void showBrowser(Context context, boolean isCleanStack){
        myBrowserRouter.open(context, isCleanStack);
    }
}
