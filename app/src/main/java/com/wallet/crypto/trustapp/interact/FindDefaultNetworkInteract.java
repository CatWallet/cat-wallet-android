package com.wallet.crypto.trustapp.interact;

import com.wallet.crypto.trustapp.entity.CurrencyInfo;
import com.wallet.crypto.trustapp.entity.NetworkInfo;
import com.wallet.crypto.trustapp.repository.EthereumNetworkRepositoryType;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class FindDefaultNetworkInteract {

    private final EthereumNetworkRepositoryType ethereumNetworkRepository;

    public FindDefaultNetworkInteract(EthereumNetworkRepositoryType ethereumNetworkRepository) {
        this.ethereumNetworkRepository = ethereumNetworkRepository;
    }

    public Single<NetworkInfo> find() {
        //findCurrency();
        return Single.just(ethereumNetworkRepository.getDefaultNetwork())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<CurrencyInfo> findCurrency() {
        return Single.just(ethereumNetworkRepository.getDefaultCurrency())
                .observeOn(AndroidSchedulers.mainThread());
    }

}