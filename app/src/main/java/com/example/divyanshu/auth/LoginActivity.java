package com.example.divyanshu.auth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText userEmail,userPassword;
    private Button userSignIn;
    private TextView userSignup;
    private FirebaseAuth userAuth;
    private ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userAuth=FirebaseAuth.getInstance();

        userEmail=findViewById(R.id.email);
        userPassword=findViewById(R.id.password);
        userSignup=findViewById(R.id.sign_up);
        userSignIn=findViewById(R.id.sign_in);
        progressbar=findViewById(R.id.progressBar);



        userSignIn.setOnClickListener(this);
        userSignup.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(userAuth.getCurrentUser()!=null){
            finish();
            startActivity(new Intent(LoginActivity.this,ProfileActivity.class));
        }
    }

    private void userLogin(){
       String email=userEmail.getText().toString().trim();
       String password=userPassword.getText().toString().trim();
       if(TextUtils.isEmpty(email)){
           Toast.makeText(LoginActivity.this,"Plz Enter Email...",Toast.LENGTH_SHORT).show();
           return;
       }
       if(TextUtils.isEmpty(password)){
           Toast.makeText(LoginActivity.this,"Plz Enter Password...",Toast.LENGTH_SHORT).show();
           return;
       }

       progressbar.setVisibility(View.VISIBLE);
       userAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {
               progressbar.setVisibility(View.GONE);
               if (task.isSuccessful()){
                   finish();
                   startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
               }
               else{
                   Toast.makeText(getApplicationContext(),"UserId and Password is Incorrect...",Toast.LENGTH_SHORT).show();
               }
           }
       });


    }

    @Override
    public void onClick(View v) {
        if(v==userSignIn){
            userLogin();
        }
        if(v==userSignup){
            //SignUp Activity
            finish();
            startActivity(new Intent(this,MainActivity.class));

        }

    }
}
