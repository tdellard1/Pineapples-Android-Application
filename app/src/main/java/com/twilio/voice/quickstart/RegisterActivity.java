package com.twilio.voice.quickstart;

import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextPhoneNumber;
    private EditText editTextVerificationCode;
    private Button buttonRegister;
    private Button buttonVerifyCode;
    private Button buttonDatabase;
    private Button buttonMainPage;
    private String mVerificationId;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mToken;
    private FirebaseDatabase Database;
    private FirebaseUser currentUser;
    private String UserID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);




        editTextPhoneNumber      = findViewById(R.id.et_register_number);
        editTextVerificationCode = findViewById(R.id.et_verification_code);
        buttonRegister           = findViewById(R.id.btn_register);
        buttonVerifyCode         = findViewById(R.id.btn_verify_code);
        buttonDatabase           = findViewById(R.id.btn_database);
        buttonMainPage           = findViewById(R.id.btn_main_page);
        Database                 = FirebaseDatabase.getInstance();


        mAuth = FirebaseAuth.getInstance();


        buttonMainPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainPageIntent = new Intent(RegisterActivity.this, VoiceActivity.class);
                startActivity(mainPageIntent);
                finish();
            }
        });

        buttonDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = editTextPhoneNumber.getText().toString();
                addUserToDatabase(phoneNumber);
                Intent mainPageIntent = new Intent(RegisterActivity.this, VoiceActivity.class);
                mainPageIntent.putExtra(Intent.EXTRA_TEXT, UserID);
                startActivity(mainPageIntent);
                finish();
            }
        });


        buttonRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final String phoneNumber  = editTextPhoneNumber.getText().toString();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,60, TimeUnit.SECONDS, RegisterActivity.this, mCallbacks);
            }
        });

        buttonVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = editTextPhoneNumber.getText().toString();
                String code = editTextVerificationCode.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                signInWithPhoneAuthCredential(credential);
                buttonVerifyCode.setVisibility(View.INVISIBLE);
                buttonDatabase.setVisibility(View.VISIBLE);
                //Intent mainPageIntent = new Intent(RegisterActivity.this, VoiceActivity.class);
                //startActivity(mainPageIntent);
                //finish();
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
                addUserToDatabase(editTextPhoneNumber.getText().toString());
                //Intent mainPageIntent = new Intent(RegisterActivity.this, VoiceActivity.class);
                //startActivity(mainPageIntent);
                //finish();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);
                mVerificationId = verificationId;
                mToken = token;
                editTextPhoneNumber.setVisibility(View.INVISIBLE);
                editTextVerificationCode.setVisibility(View.VISIBLE);
                buttonRegister.setVisibility(View.INVISIBLE);
                buttonVerifyCode.setVisibility(View.VISIBLE);

            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            currentUser = task.getResult().getUser();
                            UserID = currentUser.getUid();

                        } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(RegisterActivity.this, "Invalid Verification Code", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Failed To Validate Number", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemThatWasSelected = item.getItemId();
        if (menuItemThatWasSelected == R.id.action_sign_out) {
        } else if (menuItemThatWasSelected == R.id.action_settings) {
            FirebaseAuth.getInstance().signOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addUserToDatabase(final String phoneNumber) {
        DatabaseReference mDatabase = Database.getReference(UserID);
        mDatabase.setValue(phoneNumber);

    }
}