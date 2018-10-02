package com.wallet.crypto.trustapp;

import android.app.Activity;
import android.support.multidex.MultiDexApplication;

import com.wallet.crypto.trustapp.di.DaggerAppComponent;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import io.realm.Realm;
import com.parse.Parse;

public class App extends MultiDexApplication implements HasActivityInjector {

	@Inject
	DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

	@Override
	public void onCreate() {
		super.onCreate();
        Realm.init(this);
        DaggerAppComponent
				.builder()
				.application(this)
				.build()
				.inject(this);

//		Parse.initialize(new Parse.Configuration.Builder(this)
//				.applicationId("catwallet")
//				// if desired
//				//.clientKey("YOUR_CLIENT_KEY")
//				.server("https://cat-wallet.azurewebsites.net/parse")
//				.build()
//		);
	}

	@Override
	public AndroidInjector<Activity> activityInjector() {
		return dispatchingAndroidInjector;
	}

}
