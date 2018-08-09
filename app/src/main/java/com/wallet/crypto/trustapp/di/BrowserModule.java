package com.wallet.crypto.trustapp.di;

import com.wallet.crypto.trustapp.router.AddTokenRouter;
import com.wallet.crypto.trustapp.router.SendTokenRouter;
import com.wallet.crypto.trustapp.router.TransactionsRouter;
import com.wallet.crypto.trustapp.viewmodel.BrowserViewModelFactory;

import dagger.Module;
import dagger.Provides;

@Module
public class BrowserModule {

    @Provides
    BrowserViewModelFactory provideBrowserViewModelFactory(
            AddTokenRouter addTokenRouter,
            SendTokenRouter sendTokenRouter,
            TransactionsRouter transactionsRouter) {
        return new BrowserViewModelFactory(addTokenRouter, sendTokenRouter, transactionsRouter);
    }

    @Provides
    AddTokenRouter provideAddTokenRouter() {
        return new AddTokenRouter();
    }

    @Provides
    SendTokenRouter provideSendTokenRouter() {
        return new SendTokenRouter();
    }

    @Provides
    TransactionsRouter provideTransactionsRouter() {
        return new TransactionsRouter();
    }
}
