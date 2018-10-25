package com.wallet.crypto.trustapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.Preference;
import android.util.DisplayMetrics;

import java.util.Locale;

public class LanguageUtils {

    public static String getLanguage(){
        return Locale.getDefault().getDisplayLanguage();
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
