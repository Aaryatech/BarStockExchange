package com.ats.barstockexchange.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ats.barstockexchange.R;
import com.ats.barstockexchange.activity.AddMixerActivity;
import com.ats.barstockexchange.activity.HomeActivity;
import com.ats.barstockexchange.activity.OrderReviewActivity;
import com.ats.barstockexchange.activity.SplashActivity;
import com.ats.barstockexchange.bean.AllCategoryAndItemsData;
import com.ats.barstockexchange.bean.CategoryItemList;
import com.ats.barstockexchange.bean.ErrorMessage;
import com.ats.barstockexchange.bean.Item;
import com.ats.barstockexchange.bean.Settings;
import com.ats.barstockexchange.bean.TempDataBean;
import com.ats.barstockexchange.util.CheckNetwork;
import com.ats.barstockexchange.util.ExpandableUserHomeListAdapter;
import com.ats.barstockexchange.util.InterfaceApi;
import com.ats.barstockexchange.util.SharedPrefManager;
import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;
import static com.ats.barstockexchange.activity.HomeActivity.tvTitle;

public class AdminHomeFragment extends Fragment {

    public static String tag = "AdminHomeFragment";

    File folder = new File(Environment.getExternalStorageDirectory() + File.separator, "BSEData");

    ExpandableUserHomeListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    int userId;
    String userType;

    private GridView gridCat;
    private ListView lvAllItems;

    DisplayAdapter dAdapter;
    MyCategoryAdapter categoryAdapter;

    private ArrayList<CategoryItemList> categoryItemLists = new ArrayList<>();
    private ArrayAdapter<Integer> arrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);
        tvTitle.setText("Home");


        gridCat = view.findViewById(R.id.gridViewCat);
        lvAllItems = view.findViewById(R.id.lvAllItems);

        try {
            SharedPreferences pref = getContext().getSharedPreferences(InterfaceApi.MY_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            userId = pref.getInt("UserId", 0);
            userType = pref.getString("UserType", "");

            Log.e(tag, "UserId : " + userId);
            Log.e(tag, "UserType : " + userType);
        } catch (Exception e) {
            Log.e(tag, "" + e.getMessage());
        }

        createFolder();
        updateUserToken(userId);
        getAllCatAndItems();
        return view;
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
            Call<AllCategoryAndItemsData> itemDataCall = api.getAllCatAndItemsByQuery();


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
                            } else {
                                categoryItemLists.clear();

                                Settings settings = data.getSettings();

                                SharedPreferences pref = getContext().getSharedPreferences(InterfaceApi.MY_PREF, MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                Gson gson = new Gson();
                                String json = gson.toJson(settings);
                                editor.putString("Settings", json);
                                editor.apply();

                                for (int i = 0; i < data.getCategoryItemList().size(); i++) {
                                    categoryItemLists.add(data.getCategoryItemList().get(i));
                                }

                                gridCat.setNumColumns(categoryItemLists.size());

                                categoryAdapter = new MyCategoryAdapter(getContext(), categoryItemLists);
                                gridCat.setAdapter(categoryAdapter);

                                dAdapter = new DisplayAdapter(getContext(), categoryItemLists);
                                lvAllItems.setAdapter(dAdapter);

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }
                                    }
                                }, 2500);


                            }
                        } else {
                            progressDialog.dismiss();
                            Log.e("RESPONSE : ", " NO DATA");
                        }
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Log.e("Exception : ", "" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<AllCategoryAndItemsData> call, Throwable t) {
                    progressDialog.dismiss();
                    Log.e("ON FAILURE : ", " ERROR : " + t.getMessage());
                }
            });


        } else {
            Log.e("No Connection", "-----");
        }
    }


    public class MyCategoryAdapter extends BaseAdapter {

        private ArrayList<CategoryItemList> originalValues;
        private ArrayList<CategoryItemList> displayedValues;
        LayoutInflater inflater;

        public MyCategoryAdapter(Context context, ArrayList<CategoryItemList> stringArrayList) {
            this.originalValues = stringArrayList;
            this.displayedValues = stringArrayList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return displayedValues.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View v, ViewGroup parent) {
            v = inflater.inflate(R.layout.custom_category_adapter_layout, null);
            LinearLayout llBack = v.findViewById(R.id.llCatLayout);
            TextView tvName = v.findViewById(R.id.tvCatName);
            ImageView ivImage = v.findViewById(R.id.ivCatImage);

            // Log.e("Cat Name : ", "-----------" + displayedValues.get(position).getCategoryName());
            tvName.setText("" + displayedValues.get(position).getCategoryName());

            try {
                Picasso.with(getContext())
                        .load(InterfaceApi.IMAGE_PATH + "" + displayedValues.get(position).getImage())
                        .placeholder(R.drawable.bottle_a)
                        .error(R.drawable.bottle_a)
                        .into(ivImage);
            } catch (Exception e) {
            }

            llBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getItemPosition(displayedValues.get(position).getCategoryName());
                    // lvAllItems.smoothScrollToPositionFromTop(pos, 0, 0);
                    smoothScrollToPositionFromTop(lvAllItems, pos);
                }
            });

            gridViewSetting(gridCat);

            return v;
        }
    }

    public int getItemPosition(String name) {
        for (int i = 0; i < categoryItemLists.size(); i++)
            if (categoryItemLists.get(i).getCategoryName().equalsIgnoreCase(name))
                return i;
        return 0;
    }

    private void gridViewSetting(GridView gridview) {

        int size = categoryItemLists.size();
        //Log.e("Size : ", "----------" + size);
        // Calculated single Item Layout Width for each grid element ....
        int width = 90;//400

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        // getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;

        int totalWidth = (int) (width * size * density);
        //  Log.e("Total Width : ", "----------" + totalWidth);
        int singleItemWidth = (int) (width * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                totalWidth, LinearLayout.LayoutParams.MATCH_PARENT);

        gridview.setLayoutParams(params);
        gridview.setColumnWidth(90);
        gridview.setHorizontalSpacing(0);
        gridview.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        gridview.setNumColumns(size);
    }


    public void createFolder() {
        if (!folder.exists()) {
            folder.mkdir();
        }
    }


    public class DisplayAdapter extends BaseAdapter {

        private ArrayList<CategoryItemList> originalValues;
        private ArrayList<CategoryItemList> displayedValues;
        LayoutInflater inflater;

        public DisplayAdapter(Context context, ArrayList<CategoryItemList> stringArrayList) {
            this.originalValues = stringArrayList;
            this.displayedValues = stringArrayList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return displayedValues.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View v, ViewGroup parent) {
            v = inflater.inflate(R.layout.expandable_user_home_header, null);
            TextView tvHead = v.findViewById(R.id.tvExpUserHeader_title);
            TextView tvCount = v.findViewById(R.id.tvExpUserHeader_Count);
            ListView lvItems = v.findViewById(R.id.lvExpandableItems);

            tvHead.setText("" + displayedValues.get(position).getCategoryName());
            tvCount.setText("" + displayedValues.get(position).getItem().size() + " items");

            ArrayList<Item> itemArray = new ArrayList<>();
            for (int i = 0; i < displayedValues.get(position).getItem().size(); i++) {
                itemArray.add(displayedValues.get(position).getItem().get(i));
            }

            DisplayItemsAdapter adapter = new DisplayItemsAdapter(getContext(), itemArray);
            lvItems.setAdapter(adapter);

            setListViewHeightBasedOnChildren(lvItems);


            return v;
        }
    }


    public class DisplayItemsAdapter extends BaseAdapter {

        private ArrayList<Item> originalValues;
        private ArrayList<Item> displayedValues;
        LayoutInflater inflater;

        public DisplayItemsAdapter(Context context, ArrayList<Item> stringArrayList) {
            this.originalValues = stringArrayList;
            this.displayedValues = stringArrayList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return displayedValues.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View v, ViewGroup parent) {
            v = inflater.inflate(R.layout.expandable_user_home_items, null);
            TextView tvItem = v.findViewById(R.id.tvExpUserItems_title);
            TextView tvHigh = v.findViewById(R.id.tvExpUserItems_high);
            TextView tvLow = v.findViewById(R.id.tvExpUserItems_low);
            final TextView tvRate = v.findViewById(R.id.tvExpUserItems_price);
            final ImageView ivArrow = v.findViewById(R.id.ivExpUserItems_arrow);
            LinearLayout llItem = v.findViewById(R.id.llExpUserList);

            SharedPreferences pref = getContext().getSharedPreferences(InterfaceApi.MY_PREF, MODE_PRIVATE);
            Gson gson = new Gson();
            String json2 = pref.getString("Settings", "");
            Settings settings = gson.fromJson(json2, Settings.class);
            // Log.e("Settings Bean : ", "---------------" + settings);
            if (settings != null) {

                if (settings.getAppMode().equalsIgnoreCase("Game")) {
                    tvItem.setText("" + displayedValues.get(position).getItemName());
                    tvHigh.setText("" + String.format("%.0f", displayedValues.get(position).getMaxRate()));
                    tvLow.setText("" + String.format("%.0f", displayedValues.get(position).getMinRate()));
                    tvRate.setText("" + String.format("%.0f", displayedValues.get(position).getOpeningRate()));

                    final float mean = (displayedValues.get(position).getMaxRate() + displayedValues.get(position).getMinRate()) / 2;
                    if (displayedValues.get(position).getOpeningRate() > mean) {
                        ivArrow.setImageResource(R.mipmap.up_icon);
                        tvRate.setBackgroundColor(getResources().getColor(R.color.colorHigh));
                    } else {
                        ivArrow.setImageResource(R.mipmap.down_icon);
                        tvRate.setBackgroundColor(getResources().getColor(R.color.colorLow));
                    }


                } else if (settings.getAppMode().equalsIgnoreCase("Special")) {
                    ivArrow.setVisibility(View.GONE);
                    tvItem.setText("" + displayedValues.get(position).getItemName());
                    tvRate.setText("" + String.format("%.0f", displayedValues.get(position).getMrpSpecial()));
                } else if (settings.getAppMode().equalsIgnoreCase("Regular")) {
                    ivArrow.setVisibility(View.GONE);
                    tvItem.setText("" + displayedValues.get(position).getItemName());
                    tvRate.setText("" + String.format("%.0f", displayedValues.get(position).getMrpSpecial()));
                }
            } else {
                ivArrow.setVisibility(View.GONE);
                tvItem.setText("" + displayedValues.get(position).getItemName());
                tvRate.setText("" + String.format("%.0f", displayedValues.get(position).getMrpSpecial()));
            }

            llItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(getContext());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.custom_quantity_dialog);

                    final ListView lvQty = (ListView) dialog.findViewById(R.id.lvQtyDialog);

                    final ArrayList<Integer> intArray = new ArrayList<>();
                    for (int i = 1; i <= 100; i++) {
                        intArray.add(i);
                    }

                    arrayAdapter = new ArrayAdapter<Integer>(getContext(), android.R.layout.simple_expandable_list_item_1, intArray) {

                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            LayoutInflater inflater1 = getLayoutInflater();
                            View view = inflater1.inflate(R.layout.custom_quantity_item_layout, parent, false);
                            TextView tvQty = view.findViewById(R.id.tvQtyItem);
                            tvQty.setText("" + intArray.get(position));
                            return view;
                        }
                    };
                    lvQty.setAdapter(arrayAdapter);


                    lvQty.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            //Toast.makeText(context, "" + (i + 1), Toast.LENGTH_SHORT).show();


                            SharedPreferences pref = getContext().getSharedPreferences(InterfaceApi.MY_PREF, MODE_PRIVATE);
                            Gson gson = new Gson();
                            String json2 = pref.getString("Settings", "");
                            Settings settings = gson.fromJson(json2, Settings.class);
                            // Log.e("Settings Bean : ", "---------------" + settings);


                            if (userType.equalsIgnoreCase("KOT")) {
                                Log.e("AdminHomeFrag : ", "KOT LOGIN");
                            } else {


                                if (settings != null) {
                                    if (settings.getAppMode().equalsIgnoreCase("Game")) {

                                        if (displayedValues.get(position).getIsMixerApplicable() == 1) {
                                            Intent intent = new Intent(getContext(), AddMixerActivity.class);
                                            intent.putExtra("ItemId", displayedValues.get(position).getItemId());
                                            intent.putExtra("ItemName", displayedValues.get(position).getItemName());
                                            intent.putExtra("ItemPrice", displayedValues.get(position).getOpeningRate());
                                            intent.putExtra("ItemQuantity", intArray.get(i));
                                            intent.putExtra("ItemSgst", displayedValues.get(position).getSgst());
                                            intent.putExtra("ItemCgst", displayedValues.get(position).getCgst());
                                            startActivity(intent);
                                        } else {
                                            ArrayList<TempDataBean> tempDataBeanArrayList = new ArrayList<>();
                                            TempDataBean data = new TempDataBean(displayedValues.get(position).getItemId(), displayedValues.get(position).getItemName(), displayedValues.get(position).getOpeningRate(), intArray.get(i), displayedValues.get(position).getSgst(), displayedValues.get(position).getCgst());
                                            tempDataBeanArrayList.add(data);

                                            Gson gson1 = new Gson();
                                            String jsonTempArray = gson.toJson(tempDataBeanArrayList);
                                            Intent intent = new Intent(getContext(), OrderReviewActivity.class);
                                            intent.putExtra("TempDataArray", jsonTempArray);
                                            startActivity(intent);

                                        }
                                    } else if (settings.getAppMode().equalsIgnoreCase("Special")) {
                                        if (displayedValues.get(position).getIsMixerApplicable() == 1) {
                                            Intent intent = new Intent(getContext(), AddMixerActivity.class);
                                            intent.putExtra("ItemId", displayedValues.get(position).getItemId());
                                            intent.putExtra("ItemName", displayedValues.get(position).getItemName());
                                            intent.putExtra("ItemPrice", displayedValues.get(position).getMrpSpecial());
                                            intent.putExtra("ItemQuantity", intArray.get(i));
                                            intent.putExtra("ItemSgst", displayedValues.get(position).getSgst());
                                            intent.putExtra("ItemCgst", displayedValues.get(position).getCgst());
                                            startActivity(intent);
                                        } else {
                                            ArrayList<TempDataBean> tempDataBeanArrayList = new ArrayList<>();
                                            TempDataBean data = new TempDataBean(displayedValues.get(position).getItemId(), displayedValues.get(position).getItemName(), displayedValues.get(position).getMrpSpecial(), intArray.get(i), displayedValues.get(position).getSgst(), displayedValues.get(position).getCgst());
                                            tempDataBeanArrayList.add(data);

                                            Gson gson1 = new Gson();
                                            String jsonTempArray = gson.toJson(tempDataBeanArrayList);
                                            Intent intent = new Intent(getContext(), OrderReviewActivity.class);
                                            intent.putExtra("TempDataArray", jsonTempArray);
                                            startActivity(intent);
                                        }

                                    } else if (settings.getAppMode().equalsIgnoreCase("Regular")) {

                                        if (displayedValues.get(position).getIsMixerApplicable() == 1) {
                                            Intent intent = new Intent(getContext(), AddMixerActivity.class);
                                            intent.putExtra("ItemId", displayedValues.get(position).getItemId());
                                            intent.putExtra("ItemName", displayedValues.get(position).getItemName());
                                            intent.putExtra("ItemPrice", displayedValues.get(position).getMrpRegular());
                                            intent.putExtra("ItemQuantity", intArray.get(i));
                                            intent.putExtra("ItemSgst", displayedValues.get(position).getSgst());
                                            intent.putExtra("ItemCgst", displayedValues.get(position).getCgst());
                                            startActivity(intent);
                                        } else {
                                            ArrayList<TempDataBean> tempDataBeanArrayList = new ArrayList<>();
                                            TempDataBean data = new TempDataBean(displayedValues.get(position).getItemId(), displayedValues.get(position).getItemName(), displayedValues.get(position).getMrpRegular(), intArray.get(i), displayedValues.get(position).getSgst(), displayedValues.get(position).getCgst());
                                            tempDataBeanArrayList.add(data);

                                            Gson gson1 = new Gson();
                                            String jsonTempArray = gson.toJson(tempDataBeanArrayList);
                                            Intent intent = new Intent(getContext(), OrderReviewActivity.class);
                                            intent.putExtra("TempDataArray", jsonTempArray);
                                            startActivity(intent);
                                        }

                                    }
                                } else {

                                    if (displayedValues.get(position).getIsMixerApplicable() == 1) {
                                        Intent intent = new Intent(getContext(), AddMixerActivity.class);
                                        intent.putExtra("ItemId", displayedValues.get(position).getItemId());
                                        intent.putExtra("ItemName", displayedValues.get(position).getItemName());
                                        intent.putExtra("ItemPrice", displayedValues.get(position).getMrpRegular());
                                        intent.putExtra("ItemQuantity", intArray.get(i));
                                        intent.putExtra("ItemSgst", displayedValues.get(position).getSgst());
                                        intent.putExtra("ItemCgst", displayedValues.get(position).getCgst());
                                        startActivity(intent);
                                    } else {
                                        ArrayList<TempDataBean> tempDataBeanArrayList = new ArrayList<>();
                                        TempDataBean data = new TempDataBean(displayedValues.get(position).getItemId(), displayedValues.get(position).getItemName(), displayedValues.get(position).getMrpRegular(), intArray.get(i), displayedValues.get(position).getSgst(), displayedValues.get(position).getCgst());
                                        tempDataBeanArrayList.add(data);

                                        Gson gson1 = new Gson();
                                        String jsonTempArray = gson.toJson(tempDataBeanArrayList);
                                        Intent intent = new Intent(getContext(), OrderReviewActivity.class);
                                        intent.putExtra("TempDataArray", jsonTempArray);
                                        startActivity(intent);
                                    }
                                }
                            }


                            dialog.dismiss();
                        }
                    });

                    dialog.show();


                }
            });


            return v;
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

    public static void getTotalHeightofRecyclerView(RecyclerView recyclerView) {

        RecyclerView.Adapter mAdapter = recyclerView.getAdapter();

        int totalHeight = 0;

        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            View mView = recyclerView.findViewHolderForAdapterPosition(i).itemView;

            mView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            totalHeight += mView.getMeasuredHeight();
        }

        if (totalHeight > 100) {
            ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
            params.height = 100;
            recyclerView.setLayoutParams(params);
        }
    }

    public void getAllCatAndItemsOnResume() {
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
            Call<AllCategoryAndItemsData> itemDataCall = api.getAllCatAndItemsByQuery();


            itemDataCall.enqueue(new Callback<AllCategoryAndItemsData>() {
                @Override
                public void onResponse(Call<AllCategoryAndItemsData> call, Response<AllCategoryAndItemsData> response) {
                    try {
                        if (response.body() != null) {
                            AllCategoryAndItemsData data = response.body();
                            if (data.getErrorMessage().getError()) {
                                //   Log.e("ON RESPONSE : ", " ERROR : " + data.getErrorMessage().getMessage());
                            } else {
                                categoryItemLists.clear();

                                Settings settings = data.getSettings();

                                SharedPreferences pref = getContext().getSharedPreferences(InterfaceApi.MY_PREF, MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                Gson gson = new Gson();
                                String json = gson.toJson(settings);
                                editor.putString("Settings", json);
                                editor.apply();

                                // Log.e("CAT SIZE : ", "-----------" + data.getCategoryItemList().size());

                                for (int i = 0; i < data.getCategoryItemList().size(); i++) {
                                    categoryItemLists.add(data.getCategoryItemList().get(i));
                                }


                                if (gridCat.getAdapter() == null) {
                                    //Log.e("Adapter", "-----------------NULL");
                                    categoryAdapter = new MyCategoryAdapter(getContext(), categoryItemLists);
                                    gridCat.setAdapter(categoryAdapter);
                                } else {
                                    categoryAdapter.notifyDataSetChanged();
                                    //Log.e("Adapter", "-----------------NOT NULL");
                                }

                                if (lvAllItems.getAdapter() == null) {
                                    DisplayAdapter dAdapter = new DisplayAdapter(getContext(), categoryItemLists);
                                    lvAllItems.setAdapter(dAdapter);
                                } else {
                                    dAdapter.notifyDataSetChanged();
                                }


                            }
                        } else {
                            //  Log.e("RESPONSE : ", " NO DATA");
                        }
                    } catch (Exception e) {
                        //  Log.e("Exception : ", "" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<AllCategoryAndItemsData> call, Throwable t) {
                    //  Log.e("ON FAILURE : ", " ERROR : " + t.getMessage());
                }
            });


        } else {
            //  Log.e("No Connection", "-----");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CheckNetwork.isInternetAvailable(getContext())) {
            final Handler ha = new Handler();
            ha.postDelayed(new Runnable() {

                @Override
                public void run() {
                    try {
                        getAllCatAndItemsOnResume();
                    } catch (Exception e) {
                    }


                    ha.postDelayed(this, 10000);
                }
            }, 10000);
        } else {
        }
    }

    public void updateUserToken(int userId) {
        if (CheckNetwork.isInternetAvailable(getContext())) {

            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder().baseUrl(InterfaceApi.URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();

            final String token = SharedPrefManager.getmInstance(getContext()).getDeviceToken();
            Log.e("TOKEN-----", "-------------------------------------------------------\n" + token);

            InterfaceApi api = retrofit.create(InterfaceApi.class);
            Call<ErrorMessage> errorMessageCall = api.updateToken(userId, token);


            errorMessageCall.enqueue(new Callback<ErrorMessage>() {
                @Override
                public void onResponse(Call<ErrorMessage> call, Response<ErrorMessage> response) {
                    try {
                        if (response.body() != null) {
                            ErrorMessage data = response.body();
                            if (data.getError()) {
                                //   Log.e("ON RESPONSE : ", " ERROR : " + data.getErrorMessage().getMessage());
                            } else {
                                Log.e("ON RESPONSE : ", " Success : " + token);
                            }
                        } else {
                            //  Log.e("RESPONSE : ", " NO DATA");
                        }
                    } catch (Exception e) {
                        //  Log.e("Exception : ", "" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<ErrorMessage> call, Throwable t) {
                    //  Log.e("ON FAILURE : ", " ERROR : " + t.getMessage());
                }
            });


        } else {
            Log.e("No Connection", "-----");
        }
    }


    public static void smoothScrollToPositionFromTop(final AbsListView view, final int position) {
        View child = getChildAtPosition(view, position);
        // There's no need to scroll if child is already at top or view is already scrolled to its end
        if ((child != null) && ((child.getTop() == 0) || ((child.getTop() > 0) && !view.canScrollVertically(1)))) {
            return;
        }

        view.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(final AbsListView view, final int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    view.setOnScrollListener(null);

                    // Fix for scrolling bug
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            view.setSelection(position);
                        }
                    });
                }
            }

            @Override
            public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount,
                                 final int totalItemCount) {
            }
        });

        // Perform scrolling to position
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                view.smoothScrollToPositionFromTop(position, 0);
            }
        });
    }

    public static View getChildAtPosition(final AdapterView view, final int position) {
        final int index = position - view.getFirstVisiblePosition();
        if ((index >= 0) && (index < view.getChildCount())) {
            return view.getChildAt(index);
        } else {
            return null;
        }
    }


}
