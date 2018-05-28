package com.ats.barstockexchange.util;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.barstockexchange.R;
import com.ats.barstockexchange.bean.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ats.barstockexchange.activity.HomeActivity.edDataList;

/**
 * Created by maxadmin on 1/11/17.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<Item>> listDataChild;

    private Boolean isTouched = false;
    public static Map<Integer, Integer> map = new HashMap<Integer, Integer>();

    //  private static HashMap<Integer, String> edDataList = new HashMap<>();

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<Item>> listChildData) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
        Log.e("ExpandableListAdapter", "-------------" + listDataHeader);
        Log.e("ExpandableListAdapter", "-------------" + listDataChild);
    }


    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return this.listDataChild.get(this.listDataHeader.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return this.listDataHeader.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return this.listDataChild.get(this.listDataHeader.get(i)).get(i1);
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
            view = infalInflater.inflate(R.layout.expandable_header, null);
            ExpandableListView mExpandableListView = (ExpandableListView) viewGroup;
            mExpandableListView.expandGroup(i);
        }

        TextView lblListHeader = (TextView) view
                .findViewById(R.id.tvExpHeader_title);
        lblListHeader.setText(headerTitle);

        TextView tvUpdate = (TextView) view
                .findViewById(R.id.tvExpHeader_Update);

        tvUpdate.setText("" + getChildrenCount(i) + " items");

        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(context, "Clicked : " + i, Toast.LENGTH_SHORT).show();

                Log.e("CLICKED : -------", "" + getChildrenCount(i));
                Log.e("LIST : -----", "" + edDataList);

            }
        });


        return view;
    }

    public class Holder {
        TextView txtListChild;
        EditText edStock;
    }

    @Override
    public View getChildView(int i, final int i1, boolean b, View view, ViewGroup viewGroup) {
        final Holder holder;

        if (view == null) {
            holder = new Holder();
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.expandable_items, null);

            holder.txtListChild = view.findViewById(R.id.tvExpItems_title);
            holder.edStock = view.findViewById(R.id.edExpItems_stock);

            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

//        Log.e("i : ", "--------------" + i);
//        Log.e("i1 : ", "--------------" + i1);

//        Log.e("child : --------", "" + listDataChild);
//        Log.e("getChild()--------", "" + getChild(i, i1));

        final Item item = (Item) getChild(i, i1);

        holder.txtListChild.setText("" + item.getItemName());
        holder.edStock.setHint("" + item.getCurrentStock());

        try {
            int value = map.get(item.getItemId());
            holder.edStock.setText("" + value);
            Log.e("Value-----", "Item : " + item.getItemId() + "------------ Value : " + value);
        } catch (Exception e) {
            Log.e("Exception", "----------------" + e.getMessage());
            e.printStackTrace();
            holder.edStock.setText("");
        }

        isTouched = false;
        holder.edStock.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                isTouched = true;
                Log.e("isTouched", "-----------TRUE");


                return false;
            }
        });

        holder.edStock.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isTouched) {
                    Log.e("Edittext", "-----------" + editable.toString());
                    try {
                        map.put(item.getItemId(), Integer.valueOf(editable.toString()));
                    } catch (Exception e) {
                    }
                }
            }
        });


        return view;


        //  final EditText edStock = (EditText) view.findViewById(R.id.edExpItems_stock);
        //  edStock.setHintTextColor(Color.argb(255, 163, 160, 160));
        //   edStock.setTextColor(Color.argb(255, 246, 165, 17));

        //  txtListChild.setText("" + item.getItemName());
        //   edStock.setHint("" + item.getCurrentStock());



        /*edStock.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isTouched) {
                    map.put(item.getItemId(), Integer.valueOf(s.toString()));
                    edDataList.put(item.getItemId(), s.toString());
                }
            }
        });*/

    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

}
