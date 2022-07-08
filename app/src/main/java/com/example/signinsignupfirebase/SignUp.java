package com.example.signinsignupfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    EditText et_uid,et_fullname,et_email,et_password;
    Button btn_signup;
    String fullname,email,pass,uid,gender;
    ProgressDialog progressDialog;
    TextView tv_resend;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        et_email=(EditText)findViewById(R.id.et_email);
        et_fullname =(EditText)findViewById(R.id.et_fullname);
        et_password=(EditText)findViewById(R.id.et_password);
        et_uid=(EditText)findViewById(R.id.et_uid);
        btn_signup=(Button)findViewById(R.id.btn_signup);
        tv_resend= findViewById(R.id.tv_resend);
        final RadioButton btn_male = (RadioButton)findViewById(R.id.rbtn_male);
        final RadioButton btn_female = (RadioButton)findViewById(R.id.rbtn_female);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("user info");
        progressDialog = new ProgressDialog(this);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullname =et_fullname.getText().toString().trim();
                pass =et_password.getText().toString().trim();
                uid =et_uid.getText().toString().trim();
                email =et_email.getText().toString().toLowerCase().trim();
                if(btn_male.isChecked())
                {
                    gender="male";
                }else if(btn_female.isChecked())
                {
                    gender="female";
                }
                if(fullname.isEmpty() || pass.isEmpty() || uid.isEmpty() || email.isEmpty() || gender.isEmpty())
                {
                    Toast.makeText(SignUp.this ,"please fill all the fields", Toast.LENGTH_SHORT).show();
                }
                else {
                    insert_user_info(email, pass);
                }
            }
        });
    }

    public void insert_user_info(final String email1, final String pass1)
    {
        progressDialog.setMessage("registering user");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email1 , pass1 ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    final FirebaseUser user = firebaseAuth.getCurrentUser();
                    sendemail(user);


                }
                else
                {
                    Toast.makeText(SignUp.this ,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
//                        Toast.makeText(SignUp.this ,"log in if you have already sign up..",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            private void sendemail(final FirebaseUser fuser) {
                fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        //Toast.makeText(SignUp.this ,"Verification E-mail has been sent,please verify..",Toast.LENGTH_LONG).show();
                        Intent i = new Intent(SignUp.this, SignIn.class);
                        String id = firebaseAuth.getCurrentUser().getUid();

                        //send data to database with the unique random id :key

                        DatabaseReference key = databaseReference.child(id);
                        DatabaseReference femail = key.child("email");
                        femail.setValue(email1);
                        DatabaseReference fgender = key.child("gender");
                        fgender.setValue(gender);
                        DatabaseReference fpassword = key.child("password");
                        fpassword.setValue(pass);
                        DatabaseReference userid = key.child("userid");
                        userid.setValue(uid);
                        DatabaseReference nameref = key.child("name");
                        nameref.setValue(fullname);
                        //checkEmailVerification(fuser);
                        startActivity(i);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUp.this ,"Failed to sign up!!",Toast.LENGTH_SHORT).show();
                    }
                });
            }

//            private void checkEmailVerification(final FirebaseUser user) {
//                FirebaseUser fuser = firebaseAuth.getCurrentUser();
//
//                if(fuser.isEmailVerified()) {
//                    Toast.makeText(SignUp.this, "sign up successfully..", Toast.LENGTH_SHORT).show();
//
//                    finish();
//                }
//                else
//                {
//                    progressDialog.dismiss();
//                    tv_resend.setVisibility(View.VISIBLE);
//                    tv_resend.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            sendemail(user);
//                        }
//                    });
//                }
//
//            }
        });

    }
}