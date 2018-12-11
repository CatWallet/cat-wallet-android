package com.wallet.crypto.trustapp.router;
import android.content.Context;
import android.content.Intent;

import com.wallet.crypto.trustapp.C;
import com.wallet.crypto.trustapp.ui.AccountWalletGetterActivity;


public class AccountWalletGetterRouter {
    public void open(Context context, String accountType, String accountAddress) {
        Intent intent = new Intent(context, AccountWalletGetterActivity.class);
        intent.putExtra(C.SEND_ACCOUNT_TYPE, accountType);
        intent.putExtra(C.SEND_ACCOUNT_ADDRESS, accountAddress);
        context.startActivity(intent);
    }
}
