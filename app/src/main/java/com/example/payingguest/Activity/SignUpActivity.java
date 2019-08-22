package com.example.payingguest.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.payingguest.R;
import com.example.payingguest.Model.User;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.concurrent.TimeUnit;


public class SignUpActivity extends AppCompatActivity {

    private MaterialEditText etphoneno, etusername, etpassword, etcpassword;
    private Button bsignup;
    private TextView tvsignin;
    private ProgressDialog progressDialog;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usertable;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    String verification_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etphoneno= findViewById(R.id.et_phoneno);
        etusername = findViewById(R.id.et_name);
        etpassword = findViewById(R.id.et_password);
        etcpassword =  findViewById(R.id.et_cpassword);
        bsignup = findViewById(R.id.btn_sign_up);
        tvsignin = findViewById(R.id.tv_Sign_In);
        progressDialog = new ProgressDialog(SignUpActivity.this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        usertable = firebaseDatabase.getReference("User");

        tvsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
        bsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validate()) {
                    onLoginFailed();
                    return;
                } else {
                     if (etpassword.getText().toString().equals(etcpassword.getText().toString())) {

                        progressDialog.setMessage("Please wait...");
                        progressDialog.show();

                        usertable.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child(etphoneno.getText().toString()).exists()) {

                                    progressDialog.dismiss();
                                    Toast.makeText(SignUpActivity.this, "Phone Number already registered", Toast.LENGTH_SHORT).show();

                                } else {
                                   sendOTP();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    } else {
                        Toast.makeText(SignUpActivity.this, "Please check your password", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }
    private void sendOTP(){
        String phone = etphoneno.getText().toString().trim();

        if (phone.isEmpty()) {
            etphoneno.setError("Phone number is required!");
            etphoneno.requestFocus();
            return;
        }

        if (phone.length() < 10) {
            etphoneno.setError("Please enter a valid phone number!");
            etphoneno.requestFocus();
            return;
        }


        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+ phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                }
                @Override
                public void onVerificationFailed(FirebaseException e) {
                    progressDialog.dismiss();
                    Log.e("fail", "onVerificationFailed: " + e.getMessage());
                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);

                    verification_code = s;
                    Toast.makeText(SignUpActivity.this, "Verification code send", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SignUpActivity.this, OtpActivity.class);
                    intent.putExtra("phonenumber", etphoneno.getText().toString());
                    intent.putExtra("password", etpassword.getText().toString().trim());
                    intent.putExtra("username", etusername.getText().toString().trim());
                    intent.putExtra("code",verification_code);
                    startActivity(intent);
                }
            };



    private void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed. Please check your phone number and password!", Toast.LENGTH_LONG).show();

        bsignup.setEnabled(true);
    }

    private boolean validate() {

        boolean valid = true;

        String upassword = etpassword.getText().toString();
        String phone = etphoneno.getText().toString();

        if (upassword.isEmpty() || upassword.length() < 5 || upassword.length() > 5) {
            etpassword.setError("5 alphanumeric characters");
            valid = false;
        } else {
            etpassword.setError(null);
        }

        if (phone.isEmpty() || phone.length() < 10 || phone.length() > 10) {
            etphoneno.setError("10 alphanumeric characters");
            valid = false;
        } else {
            etphoneno.setError(null);
        }

        return valid;
    }


}
