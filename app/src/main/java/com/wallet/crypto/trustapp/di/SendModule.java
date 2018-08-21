package com.wallet.crypto.trustapp.di;

import com.wallet.crypto.trustapp.interact.FindDefaultNetworkInteract;
import com.wallet.crypto.trustapp.interact.FindDefaultWalletInteract;
import com.wallet.crypto.trustapp.interact.GetDefaultWalletBalance;
import com.wallet.crypto.trustapp.repository.EthereumNetworkRepositoryType;
import com.wallet.crypto.trustapp.repository.WalletRepositoryType;
import com.wallet.crypto.trustapp.router.ConfirmationRouter;
import com.wallet.crypto.trustapp.viewmodel.SendViewModelFactory;

import dagger.Module;
import dagger.Provides;

@Module
class SendModule {
    @Provides
    SendViewModelFactory provideSendViewModelFactory(ConfirmationRouter confirmationRouter,  GetDefaultWalletBalance getDefaultWalletBalance,
                                                     FindDefaultNetworkInteract findDefaultNetworkInteract, FindDefaultWalletInteract findDefaultWalletInteract) {
        return new SendViewModelFactory(confirmationRouter, getDefaultWalletBalance, findDefaultNetworkInteract, findDefaultWalletInteract);
    }

    @Provides
    ConfirmationRouter provideConfirmationRouter() {
        return new ConfirmationRouter();
    }

    @Provides
    GetDefaultWalletBalance provideGetDefaultWalletBalance(
            WalletRepositoryType walletRepository, EthereumNetworkRepositoryType ethereumNetworkRepository) {
        return new GetDefaultWalletBalance(walletRepository, ethereumNetworkRepository);
    }

    @Provides
    FindDefaultNetworkInteract provideFindDefaultNetworkInteract(
            EthereumNetworkRepositoryType ethereumNetworkRepositoryType) {
        return new FindDefaultNetworkInteract(ethereumNetworkRepositoryType);
    }

    @Provides
    FindDefaultWalletInteract provideFindDefaultWalletInteract(WalletRepositoryType walletRepository) {
        return new FindDefaultWalletInteract(walletRepository);
    }
}
