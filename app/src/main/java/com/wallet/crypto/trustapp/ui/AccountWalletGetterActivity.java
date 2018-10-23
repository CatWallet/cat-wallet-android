package com.wallet.crypto.trustapp.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.Preference;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.wallet.crypto.trustapp.C;
import com.wallet.crypto.trustapp.R;
import com.wallet.crypto.trustapp.router.ImportWalletRouter;

import org.web3j.crypto.Hash;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountWalletGetterActivity extends BaseActivity{

    private AutoCompleteTextView mKeyStore;
    private Button mCopyToClipBoard;
    private Button mGoClaimWallet;
    private View mKeystoreGetterView;
    private View mProgressView;
    private String sendAccountType;
    private String sendAccountAddress;
    private String code;

    public ParseUser user;
    private UserLoginGetUser mAuthTask = null;

    String keystoreContent = "";

    final private String KEY_STORE_ADDRESS = "key_store";
    final private String TAG = "Link Parse aWalletGet";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_wallet_getter);
        mKeystoreGetterView = findViewById(R.id.keyStoreGetterView);
        mProgressView = findViewById(R.id.login_progress);
        mKeyStore = findViewById(R.id.key_store);
        mKeyStore.setEnabled(false);
        sendAccountType = getIntent().getStringExtra(C.SEND_ACCOUNT_TYPE);
        sendAccountAddress = getIntent().getStringExtra(C.SEND_ACCOUNT_ADDRESS);
        toolbar();

        mCopyToClipBoard =  (Button) findViewById(R.id.clip_to_keyboard);
        mCopyToClipBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(getApplicationContext().CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(KEY_STORE_ADDRESS, mKeyStore.getText().toString());
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                }
                Toast.makeText(AccountWalletGetterActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
        HashMap<String, String> params = new HashMap<String, String>();

        SharedPreferences prefs = getSharedPreferences(sendAccountType+"Account", MODE_PRIVATE);
        code = prefs.getString(sendAccountType+"Code", null);
        params.put(sendAccountType, sendAccountAddress);
        params.put("code", code);
        showProgress(true);

        ParseUser.becomeInBackground(prefs.getString(sendAccountType+"Token", null), new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // The current user is now set to user.
                    keystoreContent = user.getString("keyStore");
                    Log.i(TAG, keystoreContent);
                } else {
                    // The token could not be validated.
                    keystoreContent = "User not found";
                    Log.e(TAG, e.getMessage());
                }
                mKeyStore.setText(keystoreContent);
                showProgress(false);
            }
        });

        mGoClaimWallet = (Button)findViewById(R.id.go_claim_wallet);
        mGoClaimWallet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(keystoreContent != null && !keystoreContent.equals("User not found") && !keystoreContent.equals("")){
                    goClaimWallet();
                }else{
                    Toast.makeText(AccountWalletGetterActivity.this, "Error: User not found", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public class UserLoginGetUser extends AsyncTask<Void, Void, Boolean> {

        HashMap<String, String> params;

        UserLoginGetUser(HashMap<String, String> params) {
            this.params = params;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

                ParseCloud.callFunctionInBackground("logIn", this.params, new FunctionCallback<String>() {

                    @Override
                    public void done(String ret, ParseException e) {
                        if(e == null){
                            Log.i(TAG, "in");
                            keystoreContent = user.getString("keyStore");
                            keystoreContent = "User not found";
                        } else{
                        //canntretrivee file
                            Log.e("Link Parse", e.getMessage());
                        }
                    }
                });
                return true;
        }



        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            if (success) {
                mKeyStore.setText(keystoreContent);
                //finish();
            } else {
                mKeyStore.setError(getString(R.string.error_find_keystore));
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mKeystoreGetterView.setVisibility(show ? View.GONE : View.VISIBLE);
            mKeystoreGetterView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mKeystoreGetterView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mKeystoreGetterView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void goClaimWallet(){
        ImportWalletRouter importWalletRouter = new ImportWalletRouter();
        importWalletRouter.openToClaimWalletViaKeyStore(this, mKeyStore.getText().toString());
    }

}
