package com.example.payingguest.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.payingguest.R;
import com.example.payingguest.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;


public class SignUpActivity extends AppCompatActivity {

    private MaterialEditText etphoneno, etusername, etpassword, etcpassword;
    private Button bsignup;
    private ProgressDialog progressDialog;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usertable;
    String verificationCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etphoneno= findViewById(R.id.et_phoneno);
        etusername = findViewById(R.id.et_name);
        etpassword = findViewById(R.id.et_password);
        etcpassword =  findViewById(R.id.et_cpassword);
        bsignup = findViewById(R.id.btn_signup);

        progressDialog = new ProgressDialog(SignUpActivity.this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        usertable = firebaseDatabase.getReference("User");

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

                                    progressDialog.dismiss();
                                    User user = new User(etusername.getText().toString(), etpassword.getText().toString(), etphoneno.getText().toString());
                                    usertable.child(etphoneno.getText().toString()).setValue(user);
                                    Intent main = new Intent(SignUpActivity.this, SignInActivity.class);
                                    startActivity(main);
                                    Toast.makeText(SignUpActivity.this, "Sign Up Successfully !", Toast.LENGTH_SHORT).show();
                                    finish();

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

    private void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed try again Please check your phone number and password!", Toast.LENGTH_LONG).show();

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
