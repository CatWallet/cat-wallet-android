package com.wallet.crypto.trustapp.entity;

public class CurrencyInfo {
    public String name = "US Dollar";
    public String abbreviation = "USD";
    public String currencySymbol = "$";
    public boolean isMainCurrency = true;
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
