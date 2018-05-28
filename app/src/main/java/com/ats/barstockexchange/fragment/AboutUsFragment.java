package com.ats.barstockexchange.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ats.barstockexchange.R;

import static com.ats.barstockexchange.activity.HomeActivity.tvTitle;

public class AboutUsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);
        tvTitle.setText("About Us");

        return view;
    }

}
