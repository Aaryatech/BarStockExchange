package com.ats.barstockexchange.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ats.barstockexchange.R;
import static com.ats.barstockexchange.activity.HomeActivity.tvTitle;

public class TermsConditionFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terms_condition, container, false);
        tvTitle.setText("Terms and Conditions");
        return view;
    }

}
