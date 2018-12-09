package com.wallet.crypto.trustapp.ui;

import android.os.Bundle;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.wallet.crypto.trustapp.R;
import com.wallet.crypto.trustapp.router.ContactsRouter;


public class AddContactActivity extends BaseActivity{
    private Button saveContact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        setupActionBar();
        setTitle(R.string.add_contact);
        saveContact = findViewById(R.id.save_contact);
        saveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ContactsRouter().open(getApplicationContext());
            }
        });
    }


    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            toolbar();
        }
    }
}
