package com.wallet.crypto.trustapp.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.wallet.crypto.trustapp.C;
import com.wallet.crypto.trustapp.R;
import com.wallet.crypto.trustapp.entity.CurrencyInfo;
import com.wallet.crypto.trustapp.entity.NetworkInfo;
import com.wallet.crypto.trustapp.interact.FindDefaultWalletInteract;
import com.wallet.crypto.trustapp.repository.EthereumNetworkRepositoryType;
import com.wallet.crypto.trustapp.router.ManageWalletsRouter;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    // social
    public static final String TWITTER_USERNAME = "catwallet";
    public static final String TELEGRAM_USERNAME = "catwallet";
    public static final String FACEBOOK_USERNAME = "catwallet";

    @Inject
    EthereumNetworkRepositoryType ethereumNetworkRepository;
    @Inject
    FindDefaultWalletInteract findDefaultWalletInteract;
    @Inject
    ManageWalletsRouter manageWalletsRouter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.fragment_settings);
        final Preference wallets = findPreference("pref_wallet");

        wallets.setOnPreferenceClickListener(preference -> {
            manageWalletsRouter.open(getActivity(), false);
            return false;
        });

        findDefaultWalletInteract
                .find()
                .subscribe(wallet -> {
                    PreferenceManager
                            .getDefaultSharedPreferences(getActivity())
                            .edit()
                            .putString("pref_wallet", wallet.address)
                            .apply();
                    wallets.setSummary(wallet.address);
                });

        final ListPreference listPreference = (ListPreference) findPreference("pref_rpcServer");
        // THIS IS REQUIRED IF YOU DON'T HAVE 'entries' and 'entryValues' in your XML
        setRpcServerPreferenceData(listPreference);
        listPreference.setOnPreferenceClickListener(networkPreference -> {
            setRpcServerPreferenceData(listPreference);
            return false;
        });

        //change currency setting
        final ListPreference listCurrencyPreference = (ListPreference) findPreference("pref_currency");
        // THIS IS REQUIRED IF YOU DON'T HAVE 'entries' and 'entryValues' in your XML
        setCurrencyPreferenceData(listCurrencyPreference  );
        listCurrencyPreference.setOnPreferenceClickListener(currencyPreference -> {
            setCurrencyPreferenceData(listCurrencyPreference);
            return false;
        });

        String versionString = getVersion();
        Preference version = findPreference("pref_version");
        version.setSummary(versionString);
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        preferences
                .registerOnSharedPreferenceChangeListener(SettingsFragment.this);


//        String versionString = getVersion();
//        Preference version = findPreference("pref_version");
//        version.setSummary(versionString);
//        SharedPreferences preferences = PreferenceManager
//                .getDefaultSharedPreferences(getActivity());
//        preferences
//                .registerOnSharedPreferenceChangeListener(SettingsFragment.this);

        final Preference rate = findPreference("pref_rate");
        rate.setOnPreferenceClickListener(preference -> {
            rateThisApp();
            return false;
        });

        final Preference twitter = findPreference("pref_twitter");
        twitter.setOnPreferenceClickListener(preference -> {
            Intent intent;
            try {
                // get the Twitter app if possible
                getActivity().getPackageManager().getPackageInfo("com.twitter.android", 0);
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + TWITTER_USERNAME));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            } catch (Exception e) {
                // no Twitter app, revert to browser
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + TWITTER_USERNAME));
            }
            startActivity(intent);
            return false;
        });

        final Preference telegram = findPreference("pref_telegram");
        telegram.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://telegram.me/" + TELEGRAM_USERNAME));
            startActivity(intent);
            return false;
        });

        final Preference facebook = findPreference("pref_facebook");
        facebook.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + FACEBOOK_USERNAME));
            startActivity(intent);
            return false;
        });

//        final Preference donate = findPreference("pref_donate");
//        donate.setOnPreferenceClickListener(preference -> {
//            Intent intent = new Intent(getActivity(), SendActivity.class);
//            intent.putExtra(C.EXTRA_ADDRESS, C.DONATION_ADDRESS);
//            startActivity(intent);
//            return true;
//        });
        final Preference share = findPreference("pref_share");
        share.setOnPreferenceClickListener(preference -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_message));
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
            return false;
        });

        final Preference email = findPreference("pref_email");
        email.setOnPreferenceClickListener(preference -> {

            Intent mailto = new Intent(Intent.ACTION_SENDTO);
            mailto.setType("message/rfc822"); // use from live device
            mailto.setData(Uri.parse("mailto:support@catwallet.com")
                    .buildUpon()
                    .appendQueryParameter("subject", "Android support question")
                    .appendQueryParameter("body", "Dear CatWallet support,")
                    .build());
            mailto.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] { "support@catwallet.com" });

            startActivity(Intent.createChooser(mailto, "Select email application."));
            return true;
        });
    }

//    final Preference myAddress = findPreference("pref_myAddress");
//    myAddress.setOnPreferenceClickListener(preference -> {
//
//
//    });

    private void rateThisApp() {
        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(
                Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
        }

    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (key.equals("pref_rpcServer")) {
            Preference rpcServerPref = findPreference(key);
            // Set summary
            String selectedRpcServer = sharedPreferences.getString(key, "");
            rpcServerPref.setSummary(selectedRpcServer);
            NetworkInfo[] networks = ethereumNetworkRepository.getAvailableNetworkList();
            for (NetworkInfo networkInfo : networks) {
                if (networkInfo.name.equals(selectedRpcServer)) {
                    ethereumNetworkRepository.setDefaultNetworkInfo(networkInfo);
                    return;
                }
            }
        }

        if (key.equals("pref_currency")) {
            Preference currencyPref = findPreference(key);
            // Set summary
            String selectCurrency = sharedPreferences.getString(key, "");
            currencyPref.setSummary(selectCurrency);
            CurrencyInfo[] currencies = ethereumNetworkRepository.getAvailableCurrencyList();
            for (CurrencyInfo currencyInfo : currencies) {
                if (currencyInfo.name.equals(selectCurrency)) {
                    ethereumNetworkRepository.setDefaultCurrencyInfo(currencyInfo);
                    return;
                }
            }
        }
    }

    private void setRpcServerPreferenceData(ListPreference lp) {
        NetworkInfo[] networks = ethereumNetworkRepository.getAvailableNetworkList();
        CharSequence[] entries = new CharSequence[networks.length];
        for (int ii = 0; ii < networks.length; ii++) {
            entries[ii] = networks[ii].name;
        }

        CharSequence[] entryValues = new CharSequence[networks.length];
        for (int ii = 0; ii < networks.length; ii++) {
            entryValues[ii] = networks[ii].name;
        }

        String currentValue = ethereumNetworkRepository.getDefaultNetwork().name;

        lp.setEntries(entries);
        lp.setDefaultValue(currentValue);
        lp.setValue(currentValue);
        lp.setSummary(currentValue);
        lp.setEntryValues(entryValues);
    }

    private void setCurrencyPreferenceData(ListPreference lp) {
        CurrencyInfo[] currencies = ethereumNetworkRepository.getAvailableCurrencyList();

        CharSequence[] entries = new CharSequence[currencies.length];
        for (int ii = 0; ii < currencies.length; ii++) {
            entries[ii] = currencies[ii].name;
        }

        CharSequence[] entryValues = new CharSequence[currencies.length];
        for (int ii = 0; ii < currencies.length; ii++) {
            entryValues[ii] = currencies[ii].name;
        }

        String currentValue = ethereumNetworkRepository.getDefaultCurrency().name;

        lp.setEntries(entries);
        lp.setDefaultValue(currentValue);
        lp.setValue(currentValue);
        lp.setSummary(currentValue);
        lp.setEntryValues(entryValues);
    }

    public String getVersion() {
        String version = "N/A";
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }
}

