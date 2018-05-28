package com.ats.barstockexchange.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.barstockexchange.R;
import com.ats.barstockexchange.bean.ErrorMessage;
import com.ats.barstockexchange.bean.OrderDisplay;
import com.ats.barstockexchange.bean.OrderItem;
import com.ats.barstockexchange.bean.OrdersByTable;
import com.ats.barstockexchange.bean.OrdersSortByTable;
import com.ats.barstockexchange.util.CheckNetwork;
import com.ats.barstockexchange.util.InterfaceApi;
import com.ats.barstockexchange.util.OrderApproveInterface;
import com.ats.barstockexchange.util.OrderRejectInterface;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.ats.barstockexchange.activity.HomeActivity.tvTitle;

public class ViewOrdersFragment extends Fragment {

    private ViewPager viewPager;
    private TabLayout tab;
    FragmentPagerAdapter adapterViewPager;

    int userId;
    String userType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_orders, container, false);
        tvTitle.setText("Orders");

        try {
            SharedPreferences pref = getContext().getSharedPreferences(InterfaceApi.MY_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            userId = pref.getInt("UserId", 0);
            userType = pref.getString("UserType", "");

        } catch (Exception e) {
        }

        viewPager = view.findViewById(R.id.viewPager);
        tab = view.findViewById(R.id.tab);

        adapterViewPager = new ViewPagerAdapter(getChildFragmentManager(), getContext());
        viewPager.setAdapter(adapterViewPager);
        tab.post(new Runnable() {
            @Override
            public void run() {
                try {
                    tab.setupWithViewPager(viewPager);
                } catch (Exception e) {
                }
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    OrderApproveInterface fragment = (OrderApproveInterface) adapterViewPager.instantiateItem(viewPager, position);
                    if (fragment != null) {
                        fragment.fragmentBecameVisible();
                    }
                } else if (position == 1) {
                    OrderRejectInterface fragmentInterested = (OrderRejectInterface) adapterViewPager.instantiateItem(viewPager, position);
                    if (fragmentInterested != null) {
                        fragmentInterested.fragmentBecameVisible();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return view;
    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter {


        private Context mContext;

        public ViewPagerAdapter(FragmentManager fm, Context mContext) {
            super(fm);
            this.mContext = mContext;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new OrderApproveFragment();
            } else {
                return new OrderRejectFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Approved/Pending";
                case 1:
                    return "Rejected";
                default:
                    return null;
            }
        }
    }
}
