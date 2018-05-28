package com.ats.barstockexchange.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ats.barstockexchange.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by maxadmin on 2/11/17.
 */

public class ExpandableUserHomeListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private Activity activity;
    private List<String> listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> listDataChild;
    private ArrayAdapter<Integer> arrayAdapter;

    public ExpandableUserHomeListAdapter(Activity activity, Context context, List<String> listDataHeader,
                                         HashMap<String, List<String>> listChildData) {
        this.activity = activity;
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
    }


    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return this.listDataChild.get(this.listDataHeader.get(i))
                .size();
    }

    @Override
    public Object getGroup(int i) {
        return this.listDataHeader.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return this.listDataChild.get(this.listDataHeader.get(i))
                .get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int i, boolean b, View view, ViewGroup viewGroup) {
        view = null;
        String headerTitle = (String) getGroup(i);
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.expandable_user_home_header, null);
            ExpandableListView mExpandableListView = (ExpandableListView) viewGroup;
            mExpandableListView.expandGroup(i);
        }

        TextView lblListHeader = (TextView) view
                .findViewById(R.id.tvExpUserHeader_title);
        lblListHeader.setText(headerTitle);

        TextView tvUpdate = (TextView) view
                .findViewById(R.id.tvExpUserHeader_Count);


        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        final String childText = (String) getChild(i, i1);

        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.expandable_user_home_items, null);
        }

        LinearLayout llItem = (LinearLayout) view.findViewById(R.id.llExpUserList);

        TextView txtListChild = (TextView) view
                .findViewById(R.id.tvExpUserItems_title);


        txtListChild.setText(childText);


        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }


}
