package com.example.divyanshu.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity  implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private EditText mEmail,mPassword;
    private Button register;
    private TextView signIn;


   private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser()!=null){
            finish();
            startActivity(new Intent(this,ProfileActivity.class));
        }

        progressDialog=new ProgressDialog(this);
        mEmail=(EditText)findViewById(R.id.email);
        mPassword=(EditText)findViewById(R.id.password);
        register=(Button)findViewById(R.id.register);
        signIn=(TextView)findViewById(R.id.sign_in);

        register.setOnClickListener(this);
        signIn.setOnClickListener(this);

    }
    private void userRegister(){
        String email=mEmail.getText().toString().trim();
        String password=mPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(MainActivity.this,"Plz Enter Email...",Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(MainActivity.this,"Plz Enter Password...",Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Registering User....");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    finish();
                    startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                    Toast.makeText(MainActivity.this,"Register successful...",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MainActivity.this,"Registered Error..",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v==register){
            userRegister();
        }
        if(v==signIn){
            //Login Activity
            finish();
            startActivity(new Intent(MainActivity.this,LoginActivity.class));

        }

    }
}
