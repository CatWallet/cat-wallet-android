package com.wallet.crypto.trustapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import java.util.Locale;

public class LanguageUtils {

    public static String getLanguage(Context context){
        SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(context);
        String retLanguege = pref.getString("pref_language", Locale.getDefault().getDisplayLanguage()); //default language is the same with device language
        return retLanguege;
    }

    public static void setLanguage(Context context, String lang){
        Locale myLocale = new Locale(lang);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }
}
