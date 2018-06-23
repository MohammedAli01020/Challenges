package com.example.mohamed.challengeapp.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohamed.challengeapp.R;
import com.example.mohamed.challengeapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SectionDetailsActivity extends AppCompatActivity {

    private TextView mSectionTitleTextView;
    private TextView mSectionDetailsTextView;
    private ImageView mSectionImageView;
    private Button mOpenChatButton;
    private String mChatPath;
    private String mIntentAction;

    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private DatabaseReference mUserDatabaseReference;
    private ImageView image;
    private View headerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_details);

        mUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        mSectionTitleTextView = findViewById(R.id.tv_section_title);
        mSectionDetailsTextView = findViewById(R.id.tv_section_details);
        mSectionImageView = findViewById(R.id.img_section_image);
        mOpenChatButton = findViewById(R.id.bt_open_chat);
        mNavigationView = findViewById(R.id.navigation);

        headerLayout = mNavigationView.inflateHeaderView(R.layout.navigation_header);
        image = headerLayout.findViewById(R.id.img_profile_image);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        }


        mDrawerLayout = findViewById(R.id.drawer_layout);


        Intent secIntent = getIntent();

        mIntentAction = secIntent.getAction();

        if (mIntentAction == null)
            throw new IllegalArgumentException("intent not found!");

        updateUi(mIntentAction);
        updateProfile(FirebaseAuth.getInstance().getCurrentUser().getUid());

        mOpenChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChat();
            }
        });


        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.action_nav_chat: {
                        openChat();
                        mDrawerLayout.setSelected(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }

                    case R.id.action_nav_map: {
                        Intent intent = new Intent(SectionDetailsActivity.this, MapActivity.class);
                        mDrawerLayout.setSelected(true);
                        mDrawerLayout.closeDrawers();
                        startActivity(intent);
                        return true;
                    }

                    default:
                        return false;
                }
            }
        });

        headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SectionDetailsActivity.this, UserProfileActivity.class));
            }
        });

    }

    private void updateUi(String sectionName) {
        switch (sectionName) {
            case "action-fitness": {
                mSectionTitleTextView.setText(getText(R.string.chat_section_title_fitness));
                mSectionDetailsTextView.setText(getText(R.string.chat_section_details_fitness));
                mSectionImageView.setImageResource(R.drawable.ic_fitness1);
                mChatPath = "chat_fitness";
                setTitle("Fitness");
                return;
            }

            case "action-sport": {
                mSectionTitleTextView.setText(getText(R.string.chat_section_title_sport));
                mSectionDetailsTextView.setText(getText(R.string.chat_section_details_sport));
                mSectionImageView.setImageResource(R.drawable.ic_sport);
                mChatPath = "chat_sport";
                setTitle("Sport");
                return;
            }

            case "action-health": {
                mSectionTitleTextView.setText(getText(R.string.chat_section_title_health));
                mSectionDetailsTextView.setText(getText(R.string.chat_section_details_health));
                mSectionImageView.setImageResource(R.drawable.ic_health);
                mChatPath = "chat_health";
                setTitle("Health");
                return;
            }


            case "action-heavyTalks": {
                mSectionTitleTextView.setText(getText(R.string.chat_section_title_heavy_talks));
                mSectionDetailsTextView.setText(getText(R.string.chat_section_details_heavy_talks));
                mSectionImageView.setImageResource(R.drawable.ic_heavy_talks);
                mChatPath = "chat_heavy_talks";
                setTitle("Heavy Talks");
                return;
            }


            case "action-mountainSun": {
                mSectionTitleTextView.setText(getText(R.string.chat_section_title_mountain_sun));
                mSectionDetailsTextView.setText(getText(R.string.chat_section_details_mountain_sun));
                mSectionImageView.setImageResource(R.drawable.ic_mountain_sun);
                mChatPath = "chat_mountain_sun";
                setTitle("Mountain Sun");
                return;
            }

            case "action-belovedMother": {
                mSectionTitleTextView.setText(getText(R.string.chat_section_title_beloved_mother));
                mSectionDetailsTextView.setText(getText(R.string.chat_section_details_beloved_mother));
                mSectionImageView.setImageResource(R.drawable.ic_beloved_mother);
                mChatPath = "chat_beloved_mother";
                setTitle("Beloved Mother");
                return;
            }

            default:
                Toast.makeText(this, "something wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProfile(String uid) {
        View headerLayout =
                mNavigationView.inflateHeaderView(R.layout.navigation_header);

        final TextView username = headerLayout.findViewById(R.id.tv_profile_username);
        final TextView email = headerLayout.findViewById(R.id.tv_profile_email);

        mUserDatabaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);

                if (currentUser != null) {
                    username.setText(usernameFromEmail(currentUser.getUsername()));
                    email.setText(currentUser.getEmail());

                    if (!TextUtils.isEmpty(currentUser.getPhotoUrl())) {
                        Picasso.get().load(currentUser.getPhotoUrl()).into(image);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    private void openChat() {
        Intent chatSectionIntent = new Intent(SectionDetailsActivity.this, FitnessActivity.class);
        chatSectionIntent.putExtra("chatPath", mChatPath);
        chatSectionIntent.setAction(mIntentAction);
        startActivity(chatSectionIntent);
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home: {
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }

            case R.id.action_log_out: {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(SectionDetailsActivity.this, SignInActivity.class));
                finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
