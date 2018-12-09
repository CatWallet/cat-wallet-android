package com.wallet.crypto.trustapp.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.wallet.crypto.trustapp.R;
import com.wallet.crypto.trustapp.router.AddContactRouter;
import com.wallet.crypto.trustapp.router.SendRouter;

public class ContactsActivity extends BaseActivity{

    public String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        setupActionBar();
        setTitle(R.string.title_contacts);

        Button gotoContacts = findViewById(R.id.button_new_contact);
        gotoContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AddContactRouter().open(getApplicationContext());
            }
        });

        Button btnEdit = findViewById(R.id.btn_edit);
        address = "0x9f8284ce2cf0c8ce10685f537b1fff418104a317";
        btnEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                new SendRouter().open(getApplicationContext(), address);
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
