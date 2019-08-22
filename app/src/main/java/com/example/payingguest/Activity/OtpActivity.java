package com.example.payingguest.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.payingguest.Model.User;
import com.example.payingguest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OtpActivity extends AppCompatActivity {

    EditText etotp;
    Button btnverify;
    FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usertable;
    private ProgressDialog progressDialog;
    String codeSent;
    String username;
    String password;
    String phonenumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        etotp= findViewById(R.id.et_otp);
        btnverify= findViewById(R.id.btn_verify_otp);

        progressDialog = new ProgressDialog(OtpActivity.this);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usertable = firebaseDatabase.getReference("User");

        Intent intent = getIntent();
        phonenumber = intent.getStringExtra("phonenumber");
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");
        codeSent = intent.getStringExtra("code");

        btnverify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifySignInCode();
            }
        });

    }

    private void verifySignInCode() {
        String code = etotp.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            usertable.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    progressDialog.dismiss();

                                    User user = new User(username, password, phonenumber);
                                    usertable.child(phonenumber).setValue(user);
                                    Toast.makeText(getApplicationContext(),
                                            "Login Successful", Toast.LENGTH_LONG).show();
                                    Intent in = new Intent(OtpActivity.this, SignInActivity.class);
                                    in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(in);
                                    finish();
                                }
                                    @Override
                                    public void onCancelled (@NonNull DatabaseError databaseError){
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(),
                                                databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                        }
                        else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(),
                                        "Incorrect Verification Code ", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }
}
