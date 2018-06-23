package com.example.mohamed.challengeapp.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mohamed.challengeapp.R;
import com.example.mohamed.challengeapp.model.ChatMessage;
import com.example.mohamed.challengeapp.model.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class FitnessActivity extends AppCompatActivity {

    private ListView mChatListView;
    private EditText mChatTextEditText;
    private ImageView mSendMessageImageView;
    private ImageView mOpenGallery;
    private DatabaseReference mFitnessChatDatabaseReference;
    private FirebaseListAdapter<ChatMessage> mChatMessageFirebaseListAdapter;
    private StorageReference mChatImagesStorageReference;
    private static final int MESSAGE_MAX_LENGTH = 1000;
    private static final int REQUEST_IMAGE_OPEN = 150;
    private DatabaseReference mUsersDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness);

        mChatListView = findViewById(R.id.lv_chat_list);
        mChatTextEditText = findViewById(R.id.et_text);
        mSendMessageImageView = findViewById(R.id.img_send_message);
        mOpenGallery = findViewById(R.id.img_open_galary);

        Intent intent = getIntent();
        String chatPath = intent.getStringExtra("chatPath");

        if (TextUtils.isEmpty(chatPath))
            throw new IllegalArgumentException("chat path not found");

        mFitnessChatDatabaseReference = FirebaseDatabase.getInstance().getReference().child(chatPath);
        mChatImagesStorageReference = FirebaseStorage.getInstance().getReference().child("chat_photos");
        mUsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");


        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        mSendMessageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = null;
                String uid = null;
                String text = mChatTextEditText.getText().toString();

                if (currentUser != null) {
                    userName = usernameFromEmail(currentUser.getEmail());
                    uid = currentUser.getUid();
                }

                ChatMessage message =
                        new ChatMessage(uid, userName, text, null);

                mFitnessChatDatabaseReference.push().setValue(message);

                mChatTextEditText.setText("");
            }
        });

        mOpenGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), REQUEST_IMAGE_OPEN);
            }
        });

        mChatTextEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().trim().length() > 0) {
                    mSendMessageImageView.setEnabled(true);
                } else {
                    mSendMessageImageView.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mChatTextEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MESSAGE_MAX_LENGTH)});


        Query query = mFitnessChatDatabaseReference.limitToLast(50);

        FirebaseListOptions<ChatMessage> options = new FirebaseListOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class)
                .setLayout(R.layout.message_list_item)
                .build();

        mChatMessageFirebaseListAdapter = new FirebaseListAdapter<ChatMessage>(options) {
            @Override
            protected void populateView(View v, ChatMessage chatMessage, int position) {
                TextView text = v.findViewById(R.id.tv_chat_text);
                TextView author = v.findViewById(R.id.tv_chat_author);
                ImageView chatImageView = v.findViewById(R.id.img_chat_image);
                final CircleImageView profilePhoto = v.findViewById(R.id.img_chat_profile_photo);

                author.setText(chatMessage.getAuthor());

                String filUrl = chatMessage.getFileUri();

                if (TextUtils.isEmpty(filUrl)) {
                    chatImageView.setVisibility(View.GONE);
                    text.setVisibility(View.VISIBLE);
                    text.setText(chatMessage.getText());

                } else {
                    chatImageView.setVisibility(View.VISIBLE);
                    text.setVisibility(View.GONE);
                    Picasso.get().load(filUrl).into(chatImageView);
                }


                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    return;
                }

                mUsersDatabaseReference
                        .child(chatMessage.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        if (user != null) {
                            if (!TextUtils.isEmpty(user.getPhotoUrl())) {
                                Log.d("photoUrl", user.getPhotoUrl());
                                if (!TextUtils.isEmpty(user.getPhotoUrl())) {
                                    Picasso.get().load(user.getPhotoUrl()).into(profilePhoto);
                                }

                            }

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mChatListView.setAdapter(mChatMessageFirebaseListAdapter);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_OPEN) {
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();

                if (selectedImageUri != null) {
                    StorageReference photoRef = mChatImagesStorageReference.child(selectedImageUri.getLastPathSegment());
                    photoRef.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            if (user != null) {
                                ChatMessage message = new ChatMessage(user.getUid(), usernameFromEmail(user.getEmail()), null, downloadUrl.toString());
                                mFitnessChatDatabaseReference.push().setValue(message);
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

    private void writeNewUser(String uid, String username, String email) {
    }

    @Override
    protected void onStart() {
        super.onStart();
        mChatMessageFirebaseListAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mChatMessageFirebaseListAdapter.stopListening();
    }
}
