package com.wallet.crypto.trustapp.router;

import android.content.Context;
import android.content.Intent;

import com.wallet.crypto.trustapp.ui.ContactsActivity;

public class ContactsRouter {
    public void open(Context context) {
        Intent intent = new Intent(context, ContactsActivity.class);
        context.startActivity(intent);
    }
}
