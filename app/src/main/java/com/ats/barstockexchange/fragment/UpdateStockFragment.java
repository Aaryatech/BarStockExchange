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
import com.ats.barstockexchange.bean.Bill;
import com.ats.barstockexchange.bean.CategoryItemList;
import com.ats.barstockexchange.bean.ErrorMessage;
import com.ats.barstockexchange.bean.Item;
import com.ats.barstockexchange.bean.ReportDisplayBean;
import com.ats.barstockexchange.bean.UpdateStock;
import com.ats.barstockexchange.util.CheckNetwork;
import com.ats.barstockexchange.util.ExpandableListAdapter;
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
import static com.ats.barstockexchange.activity.HomeActivity.tvTitle;

public class UpdateStockFragment extends Fragment implements View.OnClickListener {

    private Button btnUpdate;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    ListView lvStock;

    List<String> listDataHeader1;
    HashMap<String, List<Item>> listDataChild1;

    private ArrayList<CategoryItemList> categoryItemLists = new ArrayList<>();
    int userId;

    public static Map<Integer, Integer> map = new HashMap<Integer, Integer>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_stock, container, false);
        tvTitle.setText("Update Stock");

        try {
            SharedPreferences pref = getContext().getSharedPreferences(InterfaceApi.MY_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            userId = pref.getInt("UserId", 0);
            String userType = pref.getString("UserType", "");

        } catch (Exception e) {
        }

        btnUpdate = view.findViewById(R.id.btnUpdateStock_update);
        btnUpdate.setOnClickListener(this);
        expListView = view.findViewById(R.id.expList);
        expListView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                return true;
            }
        });

        lvStock = view.findViewById(R.id.lvUpdateStock);

        getAllCatAndItems();

        //  prepareListData();
        // listAdapter = new ExpandableListAdapter(getContext(), listDataHeader1, listDataChild1);
        // expListView.setAdapter(listAdapter);


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
       /* List<String> top250 = new ArrayList<String>();
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
        comingSoon.add("Sula Brut Btl");*/

        // listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
        //listDataChild.put(listDataHeader.get(1), nowShowing);
        //listDataChild.put(listDataHeader.get(2), comingSoon);
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
//                                listAdapter = new ExpandableListAdapter(getContext(), listDataHeader1, listDataChild1);
//                                expListView.setAdapter(listAdapter);

                                HeaderAdapter headerAdapter = new HeaderAdapter(getContext(), categoryItemLists);
                                lvStock.setAdapter(headerAdapter);

                                //setAdapterData();
//                                adapter = new ItemMasterFragment.ItemDataAdapter(getContext(), itemArray);
//                                lvItem.setAdapter(adapter);
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
        if (v.getId() == R.id.btnUpdateStock_update) {
            Log.e("STOCK UPDATE :-", "-------------" + edDataList);

            ArrayList<Integer> intArray = new ArrayList<>();
            ArrayList<String> strArray = new ArrayList<>();

            for (Map.Entry<Integer, String> entry : edDataList.entrySet()) {
                intArray.add(entry.getKey());
                strArray.add(entry.getValue());
            }
            Log.e("int Array : ", "---------" + intArray);
            Log.e("str Array : ", "---------" + strArray);

            ArrayList<UpdateStock> updateStockArrayList = new ArrayList<>();
            for (int i = 0; i < strArray.size(); i++) {
                if (strArray.get(i).equals("")) {
                    Log.e("blank___ : ", "-----------------");
                } else {
                    UpdateStock updateStock = new UpdateStock(intArray.get(i), Integer.parseInt(strArray.get(i)), userId);
                    updateStockArrayList.add(updateStock);
                }
            }
            Log.e("Updated Stock Array : ", "-----------------" + updateStockArrayList);

            updateItemStock(updateStockArrayList);
        }

    }

    public void updateItemStock(ArrayList<UpdateStock> updateStocks) {
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

            Call<ErrorMessage> errorMessageCall = api.updateItemStock(updateStocks);

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
                                builder.setMessage("Stock updated successfully.");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        map.clear();
                                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                        ft.replace(R.id.content_frame, new UpdateStockFragment(), "HomeFragment");
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

    @Override
    public void onResume() {
        super.onResume();
        edDataList.clear();
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
            final Holder holder;
            View rowView = convertView;

            if (rowView == null) {
                holder = new Holder();
                LayoutInflater inflater = LayoutInflater.from(context);
                rowView = inflater.inflate(R.layout.custom_report_header_item, null);

                holder.tvName = rowView.findViewById(R.id.tvReportHeader_Name);
                holder.lvBillItems = rowView.findViewById(R.id.lvReportHeader);

                rowView.setTag(holder);

            } else {
                holder = (Holder) rowView.getTag();
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
        Boolean isTouched = false;


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
            EditText edStock;
        }

        @Override
        public View getView(final int position, final View convertView, ViewGroup parent) {
            final Holder holder;
            View rowView = convertView;

            if (rowView == null) {
                holder = new Holder();
                LayoutInflater inflater = LayoutInflater.from(context);
                rowView = inflater.inflate(R.layout.custom_update_stock_item, null);

                holder.tvName = rowView.findViewById(R.id.tvUpdateStock_Name);
                holder.edStock = rowView.findViewById(R.id.edUpdateStock_Stock);

                rowView.setTag(holder);

            } else {
                holder = (Holder) rowView.getTag();
            }

            holder.tvName.setText("" + displayedValues.get(position).getItemName());
            holder.edStock.setHint("" + displayedValues.get(position).getCurrentStock());

            try {
                int value = map.get(displayedValues.get(position).getItemId());
                holder.edStock.setText("" + value);
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
                    return false;
                }
            });

            holder.edStock.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if (isTouched) {
                        try {
                            map.put(displayedValues.get(position).getItemId(), Integer.valueOf(charSequence.toString()));
                            edDataList.put(displayedValues.get(position).getItemId(), charSequence.toString());
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
