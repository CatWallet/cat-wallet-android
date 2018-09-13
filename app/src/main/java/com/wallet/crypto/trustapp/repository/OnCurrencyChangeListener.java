package com.wallet.crypto.trustapp.repository;

import com.wallet.crypto.trustapp.entity.CurrencyInfo;

public interface OnCurrencyChangeListener {
	void onCurrencyChanged(CurrencyInfo currencyInfo);
}
