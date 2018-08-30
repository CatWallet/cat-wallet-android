package com.wallet.crypto.trustapp.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.wallet.crypto.trustapp.interact.FindDefaultNetworkInteract;
import com.wallet.crypto.trustapp.interact.FindDefaultWalletInteract;
import com.wallet.crypto.trustapp.interact.GetDefaultWalletBalance;
import com.wallet.crypto.trustapp.router.ConfirmationRouter;

import io.reactivex.annotations.NonNull;

public class SendViewModelFactory implements ViewModelProvider.Factory {

    private final ConfirmationRouter confirmationRouter;
    private final GetDefaultWalletBalance getDefaultWalletBalance;
    private final FindDefaultNetworkInteract findDefaultNetworkInteract;
    private final FindDefaultWalletInteract findDefaultWalletInteract;

    public SendViewModelFactory(ConfirmationRouter confirmationRouter,
                                GetDefaultWalletBalance getDefaultWalletBalance,
                                FindDefaultNetworkInteract findDefaultNetworkInteract,
                                FindDefaultWalletInteract findDefaultWalletInteract) {
        this.confirmationRouter = confirmationRouter;
        this.getDefaultWalletBalance = getDefaultWalletBalance;
        this.findDefaultNetworkInteract = findDefaultNetworkInteract;
        this.findDefaultWalletInteract = findDefaultWalletInteract;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new SendViewModel(confirmationRouter, getDefaultWalletBalance, findDefaultNetworkInteract, findDefaultWalletInteract);
    }
}
