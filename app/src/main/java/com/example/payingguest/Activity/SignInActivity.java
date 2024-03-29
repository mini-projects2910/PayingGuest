package com.example.payingguest.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.payingguest.Model.User;
import com.example.payingguest.Common.Common;
import com.example.payingguest.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import io.paperdb.Paper;

public class SignInActivity extends AppCompatActivity {

    private MaterialEditText etphone;
    private MaterialEditText etpassword;
    private Button btnsignin;
    private TextView tvsignup;
    private CheckBox checkBox;
    private ProgressDialog progressDialog;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usertable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        etphone = findViewById(R.id.et_phone);
        etpassword = findViewById(R.id.et_password);
        btnsignin = findViewById(R.id.btn_sign_in);
        tvsignup = findViewById(R.id.tv_Sign_Up);
        checkBox = findViewById(R.id.ckbRemeber);
        Paper.init(this);
        progressDialog = new ProgressDialog(SignInActivity.this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        usertable = firebaseDatabase.getReference("User");

        tvsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!validate()) {
                    onLoginFailed();
                    return;
                } else {

                    if (checkBox.isChecked()) {
                        Paper.book().write(Common.USER_KEY, etphone.getText().toString());
                        Paper.book().write(Common.PWD_KEY, etpassword.getText().toString());
                    }
                    progressDialog.setMessage("Please wait...");
                    progressDialog.show();

                    usertable.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            progressDialog.dismiss();
                            if (dataSnapshot.child(etphone.getText().toString().trim()).exists()) {


                                User user = dataSnapshot.child(etphone.getText().toString()).getValue(User.class);
                                if (!etpassword.getText().toString().isEmpty()) {
                                    if (user.getPassword().equals(etpassword.getText().toString().trim())) {

                                        Common.currentUser = user;

                                        Snackbar.make(v, "Sign In Successful !", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                        Intent in = new Intent(SignInActivity.this,Home.class);
                                        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(in);
                                        finish();

                                    } else {

                                        Snackbar.make(v, "Sign In not Successful!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                } else {

                                    Snackbar.make(v, "Phone Number not registered", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            } else {
                                Snackbar.make(v, "Your phone number is not registered!", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    private void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed try again!", Toast.LENGTH_LONG).show();

        btnsignin.setEnabled(true);
    }

    private boolean validate() {
        boolean valid = true;

        String upassword = etpassword.getText().toString();
        String phone = etphone.getText().toString();

        if (upassword.length() != 5) {
            etpassword.setError("5 alphanumeric characters");
            valid = false;
        } else {
            etpassword.setError(null);
        }

        if (phone.length() != 10) {
            etphone.setError("10 alphanumeric characters");
            valid = false;
        } else {
            etphone.setError(null);
        }

        return valid;
    }
}
