package com.wallet.crypto.trustapp.util;

import android.os.CountDownTimer;
import android.widget.Button;

import com.wallet.crypto.trustapp.R;

//rewrite countDownTimer
public class MyCountDownTimer extends CountDownTimer {

    Button countButton;
    public MyCountDownTimer(Button btn_djs, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.countButton = btn_djs;
    }

    //Counting process
    @Override
    public void onTick(long l) {
        countButton.setClickable(false);
        countButton.setText(l/1000+"s");

    }

    //Finish Counting
    @Override
    public void onFinish() {
        //Reset Button
        countButton.setText(R.string.send_activation_code);
        countButton.setClickable(true);
    }
}
