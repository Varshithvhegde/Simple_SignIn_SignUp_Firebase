package com.example.signinsignupfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    TextView tv_email,tv_username,tv_uid,tv_gender;
    Button btn_logout;
    String password,email;
    static String get_uid;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_email =(TextView)findViewById(R.id.tv_email);
        tv_username=(TextView)findViewById(R.id.tv_username);
        btn_logout=(Button)findViewById(R.id.btn_logout);
        tv_uid =(TextView)findViewById(R.id.tv_uid);
        tv_gender= findViewById(R.id.tv_gender);
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth = FirebaseAuth.getInstance();
        get_uid = user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("user info");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String uid = dataSnapshot.child(get_uid).child("userid").getValue(String.class);
                String fullname = dataSnapshot.child(get_uid).child("name").getValue(String.class);
                String email = dataSnapshot.child(get_uid).child("email").getValue(String.class);
                String gender = dataSnapshot.child(get_uid).child("gender").getValue(String.class);
                tv_email.setText( "  " + email);
                tv_uid.setText( "  " +  uid );
                tv_username.setText( "  "  + fullname);
                tv_gender.setText("  "+ gender);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this,"something went wrong..",Toast.LENGTH_SHORT).show();
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,SignIn.class);
                firebaseAuth.signOut();
                startActivity(i);
                finish();

            }
        });
    }

    public void onBackPressed() {

        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}