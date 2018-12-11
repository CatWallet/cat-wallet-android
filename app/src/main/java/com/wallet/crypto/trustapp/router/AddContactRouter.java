package com.wallet.crypto.trustapp.router;

import android.content.Context;
import android.content.Intent;

import com.wallet.crypto.trustapp.ui.AddContactActivity;

public class AddContactRouter {
    public void open(Context context) {
        Intent intent = new Intent(context, AddContactActivity.class);
        context.startActivity(intent);
    }
}
