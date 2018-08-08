package com.wallet.crypto.trustapp.service;

public interface TokenListService {
    void fetchTokens(String query, int[] networks, TrustWalletTokenListService.CallBack callBack);
}
