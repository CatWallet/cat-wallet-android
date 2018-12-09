package com.wallet.crypto.trustapp.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.wallet.crypto.trustapp.R;

public class ContactsActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        toolbar();
        setTitle(R.string.title_contacts);

    }
}
