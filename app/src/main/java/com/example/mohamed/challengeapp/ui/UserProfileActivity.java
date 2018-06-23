package com.example.mohamed.challengeapp.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.mohamed.challengeapp.R;
import com.example.mohamed.challengeapp.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {
    private CircleImageView mUserCircleImageView;
    private TextView mUserNameTextView;
    private TextView mUserEmailTextView;

    private static final int REQUEST_IMAGE_OPEN = 155;
    private StorageReference mUserImagesStorageReference;
    private DatabaseReference mUsersDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mUserImagesStorageReference = FirebaseStorage.getInstance().getReference().child("user_photos");
        mUsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        mUserCircleImageView = findViewById(R.id.img_user_profile_image);
        mUserNameTextView = findViewById(R.id.tv_user_profile_username);
        mUserEmailTextView = findViewById(R.id.tv_user_profile_email);

        mUserCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), REQUEST_IMAGE_OPEN);
            }
        });

        updateProfile();

    }

    private void updateProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }

        mUsersDatabaseReference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user != null) {
                    if (!TextUtils.isEmpty(user.getPhotoUrl())) {
                        Log.d("photoUrl", user.getPhotoUrl());
                        Picasso.get().load(user.getPhotoUrl()).into(mUserCircleImageView);
                    }

                    mUserNameTextView.setText(user.getUsername());
                    mUserEmailTextView.setText(user.getEmail());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_OPEN) {
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();

                if (selectedImageUri != null) {
                    StorageReference photoRef = mUserImagesStorageReference.child(selectedImageUri.getLastPathSegment());
                    photoRef.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            if (user != null) {
                                mUsersDatabaseReference.child(user.getUid()).child("photoUrl").setValue(downloadUrl.toString())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                updateProfile();
                                            }
                                        });
                            }
                        }
                    });
                }

            }
        }
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }
}
