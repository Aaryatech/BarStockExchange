package com.ats.barstockexchange.util;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.ats.barstockexchange.R;
import com.ats.barstockexchange.bean.Item;
import com.ats.barstockexchange.bean.TempRate;

import java.util.HashMap;
import java.util.List;

import static com.ats.barstockexchange.activity.HomeActivity.edRateDataList;


/**
 * Created by maxadmin on 1/11/17.
 */

public class ExpandableRateListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<Item>> listDataChild;

    public ExpandableRateListAdapter(Context context, List<String> listDataHeader,
                                     HashMap<String, List<Item>> listChildData) {
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
        String headerTitle = (String) getGroup(i);
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.expandable_rate_header, null);
            ExpandableListView mExpandableListView = (ExpandableListView) viewGroup;
            mExpandableListView.expandGroup(i);
        }

        TextView lblListHeader = (TextView) view
                .findViewById(R.id.tvExpRateHeader_title);
        lblListHeader.setText(headerTitle);

        TextView tvUpdate = (TextView) view
                .findViewById(R.id.tvExpRateHeader_Update);

        tvUpdate.setText("" + getChildrenCount(i) + " items");

        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(context, "Clicked : " + i, Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {

        final Item item = (Item) getChild(i, i1);

        try {
            final String childText = (String) getChild(i, i1);
        } catch (Exception e) {
        }

        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.expandable_rate_items, null);
        }

        TextView txtListChild = (TextView) view
                .findViewById(R.id.tvExpRateItems_title);

        final EditText edMinRate = (EditText) view.findViewById(R.id.edExpRateItems_min);
        EditText edMaxRate = (EditText) view.findViewById(R.id.edExpRateItems_max);

        edMinRate.setHintTextColor(Color.argb(255, 163, 160, 160));
        edMinRate.setTextColor(Color.argb(255, 246, 165, 17));
        edMaxRate.setHintTextColor(Color.argb(255, 163, 160, 160));
        edMaxRate.setTextColor(Color.argb(255, 246, 165, 17));

        txtListChild.setText("" + item.getItemName());
        edMinRate.setHint("" + item.getMinRate());
        edMaxRate.setHint("" + item.getMaxRate());

        final TempRate rate = new TempRate();

        edMinRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                rate.setMin(s.toString());
                edRateDataList.put(item.getItemId(), rate);
            }
        });

        edMaxRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                rate.setMax(s.toString());
                edRateDataList.put(item.getItemId(), rate);
            }
        });

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }


}
