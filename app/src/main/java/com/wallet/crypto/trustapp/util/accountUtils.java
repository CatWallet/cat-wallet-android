package com.wallet.crypto.trustapp.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.wallet.crypto.trustapp.R;
import com.wallet.crypto.trustapp.ui.BaseActivity;
import com.wallet.crypto.trustapp.ui.MobileLoginActivity;
import com.wallet.crypto.trustapp.ui.SettingsActivity;

import java.util.HashMap;

public class accountUtils extends BaseActivity {


    public static void linkSuccessDialog(Context context){
        // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // 2. Chain together various setter methods to set the dialog characteristics
        //builder.setTitle(R.string.link_phone_success);
        builder.setMessage(R.string.link_phone_success);
        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Intent intent;
                try{
                    intent = new Intent(context, SettingsActivity.class);
                    context.startActivity(intent);
                }catch (Exception e){
                    Log.e("link Success Error",e.getMessage());
                }
            }
        });
        // 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public static void sendCodeFromParse(String accountType, String account, Context context){
        HashMap<String, String> params = new HashMap();
        params.put(accountType, account);

        ParseCloud.callFunctionInBackground("sendCode", params, new FunctionCallback<String>() {
            @Override
            public void done(String ret, ParseException e) {
                if (e == null) {
                    Log.i("Link Parse", "Send Code Success");
                    Toast.makeText(context, accountType+" Code has been send...", Toast.LENGTH_LONG).show();
                }else{
                    Log.e("Link Parse", e.getMessage());
                    Toast.makeText(context, accountType+" Code send failed, please check your internet connection", Toast.LENGTH_LONG).show();
                }

            }
        });
    }


    public static boolean isPhoneValid(String phone) {
        //TODO: Replace this with your own logic
        return phone.matches("^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$");
    }

    public static boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return true;
    }
}
