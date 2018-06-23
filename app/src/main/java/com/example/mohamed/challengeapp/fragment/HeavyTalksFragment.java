package com.example.mohamed.challengeapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mohamed.challengeapp.R;
import com.example.mohamed.challengeapp.ui.SectionDetailsActivity;


public class HeavyTalksFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_section, container, false);

        TextView title = rootView.findViewById(R.id.tv_fragment_title);
        ImageView image = rootView.findViewById(R.id.img_fragment_image);

        title.setText(getString(R.string.chat_section_title_heavy_talks));
        image.setImageResource(R.drawable.ic_heavy_talks);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fitnessIntent =new Intent(getContext(), SectionDetailsActivity.class);
                fitnessIntent.setAction("action-heavyTalks");
                startActivity(fitnessIntent);
            }
        });

        return rootView;
    }
}
