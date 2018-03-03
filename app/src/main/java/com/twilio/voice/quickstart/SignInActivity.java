package com.twilio.voice.quickstart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.twilio.voice.Call;


public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Call.Listener callListener;
    }
}
