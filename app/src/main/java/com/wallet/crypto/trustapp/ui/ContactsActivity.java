package com.wallet.crypto.trustapp.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.wallet.crypto.trustapp.R;
import com.wallet.crypto.trustapp.entity.Contact;
import com.wallet.crypto.trustapp.router.AddContactRouter;
import com.wallet.crypto.trustapp.router.SendRouter;

import java.util.ArrayList;

public class ContactsActivity extends BaseActivity{

    public String address;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Contact[] contactInfo;

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

//        Button btnEdit = findViewById(R.id.btn_edit);
//        address = "0x9f8284ce2cf0c8ce10685f537b1fff418104a317";
//        btnEdit.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                new SendRouter().open(getApplicationContext(), address);
//            }
//        });

        contactInfo = initContactList();
        mRecyclerView = (RecyclerView) findViewById(R.id.contact_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)

        mAdapter = new ContactRecycleAdapter(contactInfo);
        mRecyclerView.setAdapter(mAdapter);

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

    private Contact[] initContactList(){
        ArrayList<Contact> contactList = new ArrayList<Contact>();
        for(int i = 0; i < 10;i++){
            Contact con = new Contact();
            con.setName("contact"+i);
            contactList.add(con);
        }
        return (Contact[])contactList.toArray();
    }
}
