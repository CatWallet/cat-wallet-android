package com.wallet.crypto.trustapp.entity;

public class CurrencyInfo {
    public final String name;
    public final String abbreviation;
    public final String currencySymbol;
    public final boolean isMainCurrency;
    //public final String backendUrl; //save for later

    public CurrencyInfo(
            String name,
            String abbreviation,
            String currencySymbol,
            boolean isMainCurrency) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.currencySymbol = currencySymbol;
        this.isMainCurrency = isMainCurrency;
    }

}
