package com.ats.barstockexchange.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.barstockexchange.R;
import com.ats.barstockexchange.bean.AllCategoryAndItemsData;
import com.ats.barstockexchange.bean.CategoryItemList;
import com.ats.barstockexchange.bean.ErrorMessage;
import com.ats.barstockexchange.bean.Item;
import com.ats.barstockexchange.bean.TempRate;
import com.ats.barstockexchange.bean.UpdateRate;
import com.ats.barstockexchange.util.CheckNetwork;
import com.ats.barstockexchange.util.ExpandableRateListAdapter;
import com.ats.barstockexchange.util.InterfaceApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.ats.barstockexchange.activity.HomeActivity.edDataList;
import static com.ats.barstockexchange.activity.HomeActivity.edRateDataList;
import static com.ats.barstockexchange.activity.HomeActivity.tvTitle;


public class UpdateRateFragment extends Fragment implements View.OnClickListener {


    private Button btnUpdate;
    ExpandableRateListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    List<String> listDataHeader1;
    HashMap<String, List<Item>> listDataChild1;

    private ArrayList<CategoryItemList> categoryItemLists = new ArrayList<>();
    int userId;
    private ListView lvRateList;

    public static Map<Integer, Double> mapMin = new HashMap<Integer, Double>();
    public static Map<Integer, Double> mapMax = new HashMap<Integer, Double>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_rate, container, false);
        tvTitle.setText("Update Rate");
        try {
            SharedPreferences pref = getContext().getSharedPreferences(InterfaceApi.MY_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            userId = pref.getInt("UserId", 0);
            String userType = pref.getString("UserType", "");

        } catch (Exception e) {
        }

        btnUpdate = view.findViewById(R.id.btnUpdateRate_update);
        btnUpdate.setOnClickListener(this);
        expListView = view.findViewById(R.id.expListRate);
        expListView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                return true;
            }
        });

        lvRateList = view.findViewById(R.id.lvUpdateRateList);

        getAllCatAndItems();
//        prepareListData();
//        listAdapter = new ExpandableRateListAdapter(getContext(), listDataHeader, listDataChild);
//        expListView.setAdapter(listAdapter);


        return view;
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("American Irish Whisky");
        listDataHeader.add("Beers");
        listDataHeader.add("Champagne");

        // Adding child data
        List<String> top250 = new ArrayList<String>();
        top250.add("Jack Daniels");
        top250.add("Jameson");
        top250.add("Jim Beam White");

        List<String> nowShowing = new ArrayList<String>();
        nowShowing.add("Bud Magnum");
        nowShowing.add("Budweiser");
        nowShowing.add("Carlsberg Beer");
        nowShowing.add("Heineken Beer");
        nowShowing.add("Hoegaarden");
        nowShowing.add("Kingfisher Beer");
        nowShowing.add("Tuborg Beer");

        List<String> comingSoon = new ArrayList<String>();
        comingSoon.add("Chandon Brut Bottle");
        comingSoon.add("Chandon Brut Rose Btl");
        comingSoon.add("Sula Brut Btl");

        listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
        listDataChild.put(listDataHeader.get(1), nowShowing);
        listDataChild.put(listDataHeader.get(2), comingSoon);
    }

    private void dispListData(ArrayList<CategoryItemList> arrayList) {
        Log.e("DISPLAY_LIST_DATA", "-------------" + arrayList);

        listDataHeader1 = new ArrayList<String>();
        listDataChild1 = new HashMap<String, List<Item>>();

        for (int i = 0; i < arrayList.size(); i++) {
            listDataHeader1.add(arrayList.get(i).getCategoryName());
            listDataChild1.put(arrayList.get(i).getCategoryName(), arrayList.get(i).getItem());
        }
    }

    public void getAllCatAndItems() {
        if (CheckNetwork.isInternetAvailable(getContext())) {

            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder().baseUrl(InterfaceApi.URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();

            InterfaceApi api = retrofit.create(InterfaceApi.class);
            Call<AllCategoryAndItemsData> itemDataCall = api.getAllCatAndItemsForUpdate();


            final Dialog progressDialog = new Dialog(getContext());
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setCancelable(false);
            progressDialog.setContentView(R.layout.loading_progress_layout);
            progressDialog.show();


            itemDataCall.enqueue(new Callback<AllCategoryAndItemsData>() {
                @Override
                public void onResponse(Call<AllCategoryAndItemsData> call, Response<AllCategoryAndItemsData> response) {
                    try {
                        if (response.body() != null) {
                            AllCategoryAndItemsData data = response.body();
                            if (data.getErrorMessage().getError()) {
                                progressDialog.dismiss();
                                Log.e("ON RESPONSE : ", " ERROR : " + data.getErrorMessage().getMessage());
                                Toast.makeText(getContext(), "unable to fetch data", Toast.LENGTH_SHORT).show();
                            } else {
                                categoryItemLists.clear();

                                Log.e("CAT SIZE : ", "-----------" + data.getCategoryItemList().size());

                                for (int i = 0; i < data.getCategoryItemList().size(); i++) {
                                    categoryItemLists.add(data.getCategoryItemList().get(i));
                                }

                                Log.e("RESPONSE : ", " DATA : " + categoryItemLists);

                                dispListData(categoryItemLists);
//                                listAdapter = new ExpandableRateListAdapter(getContext(), listDataHeader1, listDataChild1);
//                                expListView.setAdapter(listAdapter);

                                HeaderAdapter headerAdapter = new HeaderAdapter(getContext(), categoryItemLists);
                                lvRateList.setAdapter(headerAdapter);

                                progressDialog.dismiss();

                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "unable to fetch data", Toast.LENGTH_SHORT).show();
                            Log.e("RESPONSE : ", " NO DATA");
                        }
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Log.e("Exception : ", "" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<AllCategoryAndItemsData> call, Throwable t) {
                    Toast.makeText(getContext(), "unable to fetch data", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    Log.e("ON FAILURE : ", " ERROR : " + t.getMessage());
                }
            });


        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
            builder.setTitle("Check Connectivity");
            builder.setCancelable(false);
            builder.setMessage("Please Connect to Internet");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnUpdateRate_update) {

            ArrayList<Integer> intArray = new ArrayList<>();
            ArrayList<String> minArray = new ArrayList<>();
            ArrayList<String> maxArray = new ArrayList<>();


            for (Map.Entry<Integer, TempRate> entry : edRateDataList.entrySet()) {
                intArray.add(entry.getKey());
                minArray.add(entry.getValue().getMin());
                maxArray.add(entry.getValue().getMax());
            }

            Log.e("Item Id Array : ", "--------------" + intArray);
            Log.e("MinRate Array : ", "--------------" + minArray);
            Log.e("MaxRate Array : ", "--------------" + maxArray);

            ArrayList<UpdateRate> updateRateArrayList = new ArrayList<>();

            for (int i = 0; i < intArray.size(); i++) {
                float min = 0, max = 0;
                if (minArray.get(i) == null || minArray.get(i).equals("")) {
                    Log.e("blank___ : ", "-----------------");
                    min = 0;
                } else {
                    min = Float.parseFloat(minArray.get(i));
                }

                if (maxArray.get(i) == null || maxArray.get(i).equals("")) {
                    Log.e("blank___ : ", "-----------------");
                    max = 0;
                } else {
                    max = Float.parseFloat(maxArray.get(i));
                }

                UpdateRate rate = new UpdateRate(intArray.get(i), min, max, userId);
                Log.e("UpdateRate : ", "------------" + rate);
                updateRateArrayList.add(rate);
            }
            updateMinMaxRated(updateRateArrayList);
        }
    }

    public void updateMinMaxRated(ArrayList<UpdateRate> updateRates) {
        if (CheckNetwork.isInternetAvailable(getContext())) {

            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder().baseUrl(InterfaceApi.URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
            InterfaceApi api = retrofit.create(InterfaceApi.class);

            Call<ErrorMessage> errorMessageCall = api.updateItemMinMaxRate(updateRates);

            final Dialog progressDialog = new Dialog(getContext());
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setCancelable(false);
            progressDialog.setContentView(R.layout.loading_progress_layout);
            progressDialog.show();

            errorMessageCall.enqueue(new Callback<ErrorMessage>() {
                @Override
                public void onResponse(Call<ErrorMessage> call, Response<ErrorMessage> response) {
                    try {
                        if (response.body() != null) {
                            ErrorMessage data = response.body();
                            if (data.getError()) {
                                progressDialog.dismiss();
                                Log.e("ON RESPONSE : ", "ERROR : " + data.getMessage());

                            } else {
                                progressDialog.dismiss();
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
                                builder.setTitle("Success");
                                builder.setCancelable(false);
                                builder.setMessage("Rates updated successfully.");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        mapMin.clear();
                                        mapMax.clear();
                                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                        ft.replace(R.id.content_frame, new UpdateRateFragment(), "HomeFragment");
                                        ft.commit();
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }

                        } else {
                            progressDialog.dismiss();
                            Log.e("ON RESPONSE : ", "NO DATA");
                        }
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Log.e("Exception : ", "" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<ErrorMessage> call, Throwable t) {
                    progressDialog.dismiss();
                    Log.e("ON FAILURE : ", "ERROR : " + t.getMessage());
                }
            });

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
            builder.setTitle("Check Connectivity");
            builder.setCancelable(false);
            builder.setMessage("Please Connect to Internet");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }


    public class HeaderAdapter extends BaseAdapter {

        ArrayList<CategoryItemList> displayedValues;
        Context context;


        public HeaderAdapter(Context context, ArrayList<CategoryItemList> catArray) {
            this.context = context;
            this.displayedValues = catArray;
        }

        @Override
        public int getCount() {
            return displayedValues.size();
        }

        @Override
        public Object getItem(int position) {
            return displayedValues.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        public class Holder {
            TextView tvName;
            ListView lvBillItems;
        }

        @Override
        public View getView(final int position, final View convertView, ViewGroup parent) {
            final HeaderAdapter.Holder holder;
            View rowView = convertView;

            if (rowView == null) {
                holder = new HeaderAdapter.Holder();
                LayoutInflater inflater = LayoutInflater.from(context);
                rowView = inflater.inflate(R.layout.custom_report_header_item, null);

                holder.tvName = rowView.findViewById(R.id.tvReportHeader_Name);
                holder.lvBillItems = rowView.findViewById(R.id.lvReportHeader);

                rowView.setTag(holder);

            } else {
                holder = (HeaderAdapter.Holder) rowView.getTag();
            }


            holder.tvName.setText("" + displayedValues.get(position).getCategoryName());

            ItemsAdapter itemAdapter = new ItemsAdapter(context, (ArrayList<Item>) displayedValues.get(position).getItem());
            holder.lvBillItems.setAdapter(itemAdapter);

            setListViewHeightBasedOnChildren(holder.lvBillItems);

            return rowView;
        }
    }


    public class ItemsAdapter extends BaseAdapter {

        ArrayList<Item> displayedValues;
        Context context;
        Boolean isTouchedMin = false, isTouchedMax = false;


        public ItemsAdapter(Context context, ArrayList<Item> catArray) {
            this.context = context;
            this.displayedValues = catArray;
        }

        @Override
        public int getCount() {
            return displayedValues.size();
        }

        @Override
        public Object getItem(int position) {
            return displayedValues.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        public class Holder {
            TextView tvName;
            EditText edMin, edMax;
        }

        @Override
        public View getView(final int position, final View convertView, ViewGroup parent) {
            final ItemsAdapter.Holder holder;
            View rowView = convertView;

            if (rowView == null) {
                holder = new ItemsAdapter.Holder();
                LayoutInflater inflater = LayoutInflater.from(context);
                rowView = inflater.inflate(R.layout.expandable_rate_items, null);

                holder.tvName = rowView.findViewById(R.id.tvExpRateItems_title);
                holder.edMin = rowView.findViewById(R.id.edExpRateItems_min);
                holder.edMax = rowView.findViewById(R.id.edExpRateItems_max);

                rowView.setTag(holder);

            } else {
                holder = (ItemsAdapter.Holder) rowView.getTag();
            }


            holder.tvName.setText("" + displayedValues.get(position).getItemName());
            holder.edMin.setHint("" + displayedValues.get(position).getMinRate());
            holder.edMax.setHint("" + displayedValues.get(position).getMaxRate());

            try {
                double value = mapMin.get(displayedValues.get(position).getItemId());
                holder.edMin.setText("" + value);
                double valueMax = mapMax.get(displayedValues.get(position).getItemId());
                holder.edMax.setText("" + valueMax);
            } catch (Exception e) {

            }

            isTouchedMin = false;
            isTouchedMax = false;
            holder.edMin.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    isTouchedMin = true;
                    return false;
                }
            });

            holder.edMax.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    isTouchedMax = true;
                    return false;
                }
            });

            final TempRate rate = new TempRate();

            holder.edMin.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if (isTouchedMin) {
                        try {
                            rate.setMin(charSequence.toString());
                            edRateDataList.put(displayedValues.get(position).getItemId(), rate);
                            mapMin.put(displayedValues.get(position).getItemId(), Double.valueOf(charSequence.toString()));
                        } catch (Exception e) {
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            holder.edMax.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if (isTouchedMax) {
                        try {
                            rate.setMax(charSequence.toString());
                            edRateDataList.put(displayedValues.get(position).getItemId(), rate);
                            mapMax.put(displayedValues.get(position).getItemId(), Double.valueOf(charSequence.toString()));
                        } catch (Exception e) {
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });


            return rowView;
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
