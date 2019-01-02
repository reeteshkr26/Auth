package com.example.divyanshu.auth;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {
    private static final int CHOOSE_IMAGE = 101;
    TextView userEmail;
     Button buttonSave;
     FirebaseAuth firebaseAuth;
     Toolbar toolbar;
     EditText editText;
     ImageView imageView;
     Uri uriProfileImage;
     ProgressBar progressbar;
    StorageReference profileImageReference;
     Uri profileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser user=firebaseAuth.getCurrentUser();

        userEmail=findViewById(R.id.signEmail);
        userEmail.setText("Welcome:"+user.getEmail());

        toolbar=findViewById(R.id.menuToolbar);
        setSupportActionBar(toolbar);

        editText=findViewById(R.id.name);
        progressbar=findViewById(R.id.progressBar);

        imageView=findViewById(R.id.profile_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        loadUserInfo();

        buttonSave=findViewById(R.id.saveInfo);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo();

            }
        });





    }

    private void loadUserInfo() {
        FirebaseUser user=firebaseAuth.getCurrentUser();

        if(user != null){
            if(user.getPhotoUrl() !=null){
                Glide.with(this).load(user.getPhotoUrl().toString()).into(imageView);
            }
            if (user.getDisplayName() !=null){
                editText.setText(user.getDisplayName());
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }
    }

    private void saveUserInfo() {
        String displayName = editText.getText().toString();
        FirebaseUser user= firebaseAuth.getCurrentUser();

        if(displayName.isEmpty()){
            Toast.makeText(this, "Name required..", Toast.LENGTH_SHORT).show();
            return;
        }

        if( user != null && profileImageUrl !=null){
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(displayName).setPhotoUri(profileImageUrl).build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Profile Updated SuccessFully....", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data !=null && data.getData() !=null){
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                imageView.setImageBitmap(bitmap);

                uploadImage();

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    private void uploadImage() {
        profileImageReference = FirebaseStorage.getInstance().getReference("profilepics/"+System.currentTimeMillis()+".jpg");
        if(uriProfileImage != null){
            progressbar.setVisibility(View.VISIBLE);
           profileImageReference.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
               @Override
               public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   progressbar.setVisibility(View.GONE);
                   profileImageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                       @Override
                       public void onSuccess(Uri uri) {
                           profileImageUrl = uri;

                       }
                   });

               }
           })
           .addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   progressbar.setVisibility(View.GONE);
                   Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
               }
           });

        }
    }

    private void showImageChooser(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select ProfileImage"),CHOOSE_IMAGE);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuLogout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(this,LoginActivity.class));

                break;
        }
        return true;
    }


}
