package com.wallet.crypto.trustapp.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.wallet.crypto.trustapp.R;
import com.wallet.crypto.trustapp.router.SettingsRouter;
import com.wallet.crypto.trustapp.util.MyCountDownTimer;
import com.wallet.crypto.trustapp.util.accountUtils;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class MobileLoginActivity extends BaseActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    private static final int SEND_CODE_WAIT_TIME = 25000;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mMobileView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button mSendCode;
    private EditText mPhoneNumber;
    private String phone;
    private String phoneAccountNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_login);
        setupActionBar();

        SharedPreferences prefs = getSharedPreferences("phoneAccount", MODE_PRIVATE);
        phoneAccountNum = prefs.getString("phone", null);
        if(phoneAccountNum != null && !phoneAccountNum.equals("")){
            accountUtils.linkSuccessDialog(MobileLoginActivity.this);
            //linkSuccessDialog(MobileLoginActivity.this);
        }
        //toolbar();
        // Set up the login form.
        mMobileView = (AutoCompleteTextView) findViewById(R.id.phone_number);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.activation_code);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mLinkButton = (Button) findViewById(R.id.mobile_link_button);
        mLinkButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mSendCode = (Button) findViewById(R.id.fetchActivationCode);
        mPhoneNumber = (EditText) findViewById(R.id.phone_number);

        mPhoneNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                phone = mPhoneNumber.getText().toString();
            }
        });

        final MyCountDownTimer myCountDownTimer = new MyCountDownTimer(mSendCode, SEND_CODE_WAIT_TIME,1000);
        mSendCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MobileLoginActivity.this, "click send code", Toast.LENGTH_SHORT).show();
                /***
                 * To do: add concurrency (May use RxJava) for send code button parse work and counting remain time
                 */

                if(checkValidInputPhone()){
                    Log.i("debug phone","check pass");
                    accountUtils.sendCodeFromParse("phone", phone, MobileLoginActivity.this);
                    myCountDownTimer.start();
                }
                Log.i("debug phone","end");
            }

        });

    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mMobileView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                new SettingsRouter().open(this);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mMobileView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String phoneNumber = mMobileView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !accountUtils.isPasswordValid(password)) {
            //mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid phone number.
        if (TextUtils.isEmpty(phoneNumber)) {
            mMobileView.setError(getString(R.string.error_field_required));
            focusView = mMobileView;
            cancel = true;
        } else if (!accountUtils.isPhoneValid(phoneNumber)) {
            mMobileView.setError(getString(R.string.error_invalid_phone_number));
            focusView = mMobileView;
            cancel = true;
        }



        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("code", mPasswordView.getText().toString());
            params.put("phone", phone);
            mAuthTask = new UserLoginTask(params);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean checkValidInputPhone(){

        View focusView = null;
        boolean isValid = true;
        if(TextUtils.isEmpty(this.phone)) {
            mMobileView.setError(getString(R.string.error_field_required));
            focusView = mMobileView;
            isValid = false;
        }else if (!accountUtils.isPhoneValid(this.phone)) {
            mMobileView.setError(getString(R.string.error_invalid_phone_number));
            focusView = mMobileView;
            isValid = false;
        }
        if(!isValid){
            focusView.requestFocus();
        }

        return isValid;
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

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only phone number.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Phone
                .CONTENT_ITEM_TYPE},

                // Show primary phone number first. Note that there won't be
                // a primary phone number if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> phones = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            phones.add(cursor.getString(ProfileQuery.NUMBER));
            cursor.moveToNext();
        }

        addPhoneNumberToAutoComplete(phones);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addPhoneNumberToAutoComplete(List<String> phoneNumberCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(MobileLoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, phoneNumberCollection);

        mMobileView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.IS_PRIMARY,
        };

        int NUMBER= 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        HashMap<String, String> params;
        boolean LoginSuccess;

        UserLoginTask(HashMap<String, String> params) {
           this.params = params;
           LoginSuccess = false;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try{
//                ParseCloud.callFunctionInBackground("logIn", this.params, new FunctionCallback<String>() {
//                    @Override
//                    public void done(String ret, ParseException e) {
//                        if (e == null) {
//                            Log.i("Link Parse", "Login Success");
//                            Toast.makeText(MobileLoginActivity.this, "Link Success...", Toast.LENGTH_LONG).show();
//                            LoginSuccess = true;
//                        }else{
//                            log.e("link parse", e.getmessage());
//                            toast.maketext(mobileloginactivity.this, "link failed, please check your code and internet connection", toast.length_long).show();
//                        }
//                    }
//                });
                ParseCloud.callFunction("logIn", this.params);
                LoginSuccess = true;
                Log.i("Link Parse", "Login Success");
            } catch(Exception e){
                Log.e("Link Parse", e.getMessage());
                LoginSuccess = false;
            }

            return LoginSuccess;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                SharedPreferences.Editor phoneAccountEditor = getSharedPreferences("phoneAccount", Context.MODE_PRIVATE).edit();
                phoneAccountEditor.putString("phone", phone)
                                  .apply();
                accountUtils.linkSuccessDialog(MobileLoginActivity.this);
                //finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

}

