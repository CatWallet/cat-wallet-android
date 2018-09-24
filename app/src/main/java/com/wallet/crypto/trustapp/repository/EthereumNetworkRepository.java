package com.wallet.crypto.trustapp.repository;

import android.net.Network;
import android.text.TextUtils;
import android.util.Log;

import com.wallet.crypto.trustapp.C;
import com.wallet.crypto.trustapp.entity.CurrencyInfo;
import com.wallet.crypto.trustapp.entity.NetworkInfo;
import com.wallet.crypto.trustapp.entity.Ticker;
import com.wallet.crypto.trustapp.service.TickerService;

import java.util.HashSet;
import java.util.Set;

import io.reactivex.Single;

//Static Variables
import static com.wallet.crypto.trustapp.C.*;


public class EthereumNetworkRepository implements EthereumNetworkRepositoryType {

	private final PreferenceRepositoryType preferences;
	private final TickerService tickerService;
	private NetworkInfo defaultNetwork;
	private CurrencyInfo defaultCurrency;
	private final Set<OnNetworkChangeListener> onNetworkChangedListeners = new HashSet<>();
    private final Set<OnCurrencyChangeListener> onCurrencyChangedListeners = new HashSet<>();


	private final NetworkInfo[] NETWORKS = new NetworkInfo[] {
			new NetworkInfo(ETHEREUM_NETWORK_NAME, ETH_SYMBOL,
                    "https://mainnet.infura.io/TejPKvmSjVukp9t0U4IJ",
                    "https://api.trustwalletapp.com/",
                    "https://etherscan.io/",1, true),
            new NetworkInfo(CLASSIC_NETWORK_NAME, ETC_SYMBOL,
                    "https://etc-geth.0xinfra.com/",
                    "https://classic.trustwalletapp.com",
                    "https://gastracker.io",61, true),
			new NetworkInfo(CALLISTO_NETWORK_NAME, CLO_SYMBOL,
					"https://clo-geth.0xinfra.com",
					"https://callisto.trustwalletapp.com",
					"https://rinkeby.etherscan.io",820, true),
			new NetworkInfo(GOCHAIN_NETWORK_NAME, GO_SYMBOL,
					"https://rpc.gochain.io",
					"https://gochain.trustwalletapp.com",
					"GoChain",60, true),
            new NetworkInfo(POA_NETWORK_NAME, POA_SYMBOL,
                    "https://core.poa.network",
                    "https://poa.trustwalletapp.com","poa", 99, true),
			new NetworkInfo(KOVAN_NETWORK_NAME, ETH_SYMBOL,
                    "https://kovan.infura.io/TejPKvmSjVukp9t0U4IJ",
                    "https://cat-kovan.herokuapp.com/",
                    "https://kovan.etherscan.io", 42, false),
			new NetworkInfo(ROPSTEN_NETWORK_NAME, ETH_SYMBOL,
                    "https://ropsten.infura.io/TejPKvmSjVukp9t0U4IJ",
					"https://cat-ropsten.herokuapp.com/",
                    "https://ropsten.etherscan.io",3, false),
			new NetworkInfo(RINKEBY_NETWORK_NAME, ETH_SYMBOL,
					"https://rinkeby.infura.io/TejPKvmSjVukp9t0U4IJ",
					"https://cat-rinkeby.herokuapp.com/",
					"https://rinkeby.etherscan.io",5, false),
	};

    private final CurrencyInfo[] CURRENCIES = new CurrencyInfo[] {
            new CurrencyInfo(USD_NAME, USD_ABBR, USD_SYMBOL, true),
            new CurrencyInfo(CNY_NAME, CNY_ABBR, CNY_SYMBOL, true),
            new CurrencyInfo(EUR_NAME, EUR_ABBR, EUR_SYMBOL, true),
            new CurrencyInfo(HKD_NAME, HKD_ABBR, HKD_SYMBOL, true),
            new CurrencyInfo(AUD_NAME, AUD_ABBR, AUD_SYMBOL, true),
            new CurrencyInfo(RUB_NAME, RUB_ABBR, RUB_SYMBOL, true),
            new CurrencyInfo(KRW_NAME, KRW_ABBR, KRW_SYMBOL, true),
    };

	public EthereumNetworkRepository(PreferenceRepositoryType preferenceRepository, TickerService tickerService) {
		this.preferences = preferenceRepository;
		this.tickerService = tickerService;
		defaultNetwork = getNetworkInfoByName(preferences.getDefaultNetwork());
        defaultCurrency = getCurrencyInfoByName(preferences.getDefaultCurrency());
		if (defaultNetwork == null) {
			defaultNetwork = NETWORKS[0];
		}
		if(defaultCurrency == null){
		    defaultCurrency = CURRENCIES[0];
        }
	}


	private NetworkInfo getNetworkInfoByName(String name) {

		Log.i("network", name);
		if (!TextUtils.isEmpty(name)) {
			Log.i("network", name);
			for (NetworkInfo NETWORK : NETWORKS) {
				Log.i("network.name",NETWORK.name);
				if (name.equals(NETWORK.name)) {
					return NETWORK;
				}
			}
		}
		return null;
	}

    private CurrencyInfo getCurrencyInfoByName(String name) {
        if (!TextUtils.isEmpty(name)) {
            for (CurrencyInfo CURRENCY : CURRENCIES) {
                if (name.equals(CURRENCY.name) || name.equals(CURRENCY.abbreviation)) {
                    return CURRENCY;
                }
            }
        }
        return null;
    }

	@Override
	public NetworkInfo getDefaultNetwork() {
		return defaultNetwork;
	}


	@Override
	public void setDefaultNetworkInfo(NetworkInfo networkInfo) {
		defaultNetwork = networkInfo;
		C.CURRENT_COIN_SYMBOL = networkInfo.symbol;
		C.CURRENT_NETWORK_NAME = networkInfo.name;
		preferences.setDefaultNetwork(defaultNetwork.name);
		preferences.setDefaultNetworkSymbol(defaultNetwork.symbol);

		for (OnNetworkChangeListener listener : onNetworkChangedListeners) {
		    listener.onNetworkChanged(networkInfo);
        }
	}

	@Override
	public NetworkInfo[] getAvailableNetworkList() {
		return NETWORKS;
	}

	@Override
	public void addOnChangeDefaultNetwork(OnNetworkChangeListener onNetworkChanged) {
        onNetworkChangedListeners.add(onNetworkChanged);
	}

    @Override
    public Single<Ticker> getTicker() {
        return Single.fromObservable(tickerService
                .fetchTickerPrice(getDefaultCurrency().abbreviation, getDefaultNetwork().symbol));
    }


    @Override
    public CurrencyInfo getDefaultCurrency() {
        return defaultCurrency;
    }
    @Override
    public void setDefaultCurrencyInfo(CurrencyInfo currencyInfo){
        defaultCurrency = currencyInfo;
		C.CURRENCT_CURRENCY_ABBR = currencyInfo.abbreviation;
		C.CURRENCT_CURRENCY_SYMBOL = currencyInfo.currencySymbol;
		C.CURRENCT_CURRENCY_Name = currencyInfo.name;
        preferences.setDefaultCurrency(defaultCurrency.name);
		preferences.setDefaultCurrencyAbbr(defaultCurrency.abbreviation);
		preferences.setDefaultCurrencySymbol(defaultCurrency.currencySymbol);
        for(OnCurrencyChangeListener listener: onCurrencyChangedListeners){
            listener.onCurrencyChanged(currencyInfo);
        }
    }

    @Override
    public CurrencyInfo[] getAvailableCurrencyList() {
        return CURRENCIES;
    }

    @Override
    public void addOnChangeDefaultCurrency(OnCurrencyChangeListener onCurrencyChanged) {
        onCurrencyChangedListeners.add(onCurrencyChanged);
    }
}
