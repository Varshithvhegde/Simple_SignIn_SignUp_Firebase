package com.example.signinsignupfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {
    private EditText et_email,et_pass;
    private Button btn_login;
    private TextView tv_signup;
    private String email="",password="";
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    FirebaseUser user;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //Initialise start
        et_email=(EditText) findViewById(R.id.et_email1);
        et_pass=(EditText)findViewById(R.id.et_password);
        btn_login =(Button)findViewById(R.id.btn_login);
        tv_signup=(TextView)findViewById(R.id.tv_signup);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        //Initialise end

        if(firebaseAuth.getCurrentUser() != null )
        {
            //Checks whether the user is already if yes then goes to profile page
            startActivity(new Intent(SignIn.this,MainActivity.class));
        }

        //If user doesnot have account he can go to register activity
        tv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignIn.this,SignUp.class));
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //local validation
                email = et_email.getText().toString().trim();
                password = et_pass.getText().toString().trim();
                if(email.isEmpty() || password.isEmpty())
                {
                    Toast.makeText(SignIn.this,"both fields are compulsury",Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.setMessage("Logging In..");
                    progressDialog.show();
                    userAuthentication(email, password);
                }
            }
        });
    }

    private void userAuthentication(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {

                    progressDialog.dismiss();
                    checkuserexist();
                    String temp = firebaseAuth.getCurrentUser().getUid().toString();

                }else {
                    progressDialog.dismiss();
                    Toast.makeText(SignIn.this,"Incorrect email or password..",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //checking whether given email and password is matching with database
    private void checkuserexist() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("user info");
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        final String userid = firebaseAuth.getUid().toString();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(userid)){

                    Intent intent = new Intent(SignIn.this, MainActivity.class);
                    intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);


                }else {
                    Toast.makeText(SignIn.this,"you need to sign up",Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                    //Nothing now
            }
        });
    }

}