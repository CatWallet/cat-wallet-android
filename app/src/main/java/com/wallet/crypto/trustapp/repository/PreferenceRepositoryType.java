package com.wallet.crypto.trustapp.repository;

import com.wallet.crypto.trustapp.entity.GasSettings;

public interface PreferenceRepositoryType {
	String getCurrentWalletAddress();
	void setCurrentWalletAddress(String address);

	String getDefaultNetwork();
	void setDefaultNetwork(String netName);

	String getDefaultNetworkSymbol();
	void setDefaultNetworkSymbol(String networkSymbol);

	GasSettings getGasSettings(boolean forTokenTransfer);
	void setGasSettings(GasSettings gasPrice);

	String getDefaultCurrency();
	void setDefaultCurrency(String currency);

	String getDefaultCurrencySymbol();
	void setDefaultCurrencySymbol(String currencySymbol);

	String getDefaultCurrencyAbbr();
	void setDefaultCurrencyAbbr(String currencyAbbr);

}
