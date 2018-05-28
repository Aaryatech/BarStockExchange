package com.ats.barstockexchange.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ats.barstockexchange.R;
import com.ats.barstockexchange.bean.Item;
import com.ats.barstockexchange.bean.ItemListData;
import com.ats.barstockexchange.bean.TempDataBean;
import com.ats.barstockexchange.util.CheckNetwork;
import com.ats.barstockexchange.util.InterfaceApi;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddMixerActivity extends AppCompatActivity {

    private ListView lvAddMixer;
    private Button btnProceed;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayAdapter<Integer> arrayIntAdapter;

    private ArrayList<TempDataBean> tempDataArray = new ArrayList<>();


    private ArrayList<String> nameArray = new ArrayList<>();

    ArrayList<Item> itemArrayList = new ArrayList<>();

    MixerDataAdapter mixerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mixer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lvAddMixer = findViewById(R.id.lvAddMixer);
        btnProceed = findViewById(R.id.btnAddMixer_proceed);

        try {
            Bundle bundle = getIntent().getExtras();
            int itemId=bundle.getInt("ItemId");
            String itemName = bundle.getString("ItemName");
            float itemPrice = bundle.getFloat("ItemPrice");
            int itemQty = bundle.getInt("ItemQuantity");
            float sgst = bundle.getFloat("ItemSgst");
            float cgst = bundle.getFloat("ItemCgst");

            Log.e("Item Id", "----------" + itemId);
            Log.e("Item Name", "----------" + itemName);
            Log.e("Item Price", "----------" + itemPrice);
            Log.e("Item QTY", "----------" + itemQty);
            Log.e("Item SGST", "----------" + sgst);
            Log.e("Item CGST", "----------" + cgst);

            TempDataBean data = new TempDataBean(itemId, itemName, itemPrice, itemQty, sgst, cgst,0);
            tempDataArray.add(data);

        } catch (Exception e) {

        }


        getMixerData();

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e("DATA : ", "-----------------" + tempDataArray);

                Gson gson = new Gson();
                String jsonTempArray = gson.toJson(tempDataArray);
                Intent intent = new Intent(AddMixerActivity.this, OrderReviewActivity.class);
                intent.putExtra("TempDataArray", jsonTempArray);
                startActivity(intent);

            }
        });
    }

    public void getMixerData() {
        if (CheckNetwork.isInternetAvailable(getApplicationContext())) {

            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder().baseUrl(InterfaceApi.URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();

            InterfaceApi api = retrofit.create(InterfaceApi.class);
            Call<ItemListData> itemDataCall = api.getItemsByCategory(13);

            final Dialog progressDialog = new Dialog(AddMixerActivity.this);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setCancelable(false);
            progressDialog.setContentView(R.layout.loading_progress_layout);
            progressDialog.show();

            itemDataCall.enqueue(new Callback<ItemListData>() {
                @Override
                public void onResponse(Call<ItemListData> call, Response<ItemListData> response) {
                    try {
                        if (response.body() != null) {
                            ItemListData data = response.body();
                            if (data.getErrorMessage().getError()) {
                                Log.e("ON RESPONSE : ", " ERROR : " + data.getErrorMessage().getMessage());
                                progressDialog.dismiss();
                            } else {
                                itemArrayList.clear();

                                for (int i = 0; i < data.getItem().size(); i++) {
                                    itemArrayList.add(data.getItem().get(i));
                                }

                                Log.e("RESPONSE : ", " DATA : " + itemArrayList);

                                mixerAdapter = new MixerDataAdapter(AddMixerActivity.this, itemArrayList);
                                lvAddMixer.setAdapter(mixerAdapter);
                                progressDialog.dismiss();
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
                public void onFailure(Call<ItemListData> call, Throwable t) {
                    Log.e("ON FAILURE : ", " ERROR : " + t.getMessage());
                }
            });
        } else {
            Log.e("No Connection", "-----");
        }
    }

    public class MixerDataAdapter extends BaseAdapter {
        private ArrayList<Item> originalValues;
        private ArrayList<Item> displayedValues;
        LayoutInflater inflater;

        public MixerDataAdapter(Context context, ArrayList<Item> stringArrayList) {
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
            v = inflater.inflate(R.layout.custom_add_mixer_layout, null);
            TextView tvItem = v.findViewById(R.id.tvAddMixerLayout_item);
            TextView tvPrice = v.findViewById(R.id.tvAddMixerLayout_price);
            final TextView tvQty = v.findViewById(R.id.tvAddMixerLayout_qty);
            final ImageView ivCancel = v.findViewById(R.id.ivAddMixerLayout_cancel);
            LinearLayout llItem = v.findViewById(R.id.llAddMixerLayout_item);

            tvItem.setText("" + displayedValues.get(position).getItemName());
            tvPrice.setText("" + displayedValues.get(position).getOpeningRate());

            llItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final Dialog dialog = new Dialog(AddMixerActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.custom_quantity_dialog);

                    final ListView lvQty = (ListView) dialog.findViewById(R.id.lvQtyDialog);

                    final ArrayList<Integer> intArray = new ArrayList<>();
                    for (int i = 1; i <= 100; i++) {
                        intArray.add(i);
                    }

                    arrayIntAdapter = new ArrayAdapter<Integer>(AddMixerActivity.this, android.R.layout.simple_expandable_list_item_1, intArray) {

                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            LayoutInflater inflater1 = AddMixerActivity.this.getLayoutInflater();
                            View view = inflater1.inflate(R.layout.custom_quantity_item_layout, parent, false);
                            TextView tvQty = view.findViewById(R.id.tvQtyItem);
                            tvQty.setText("" + intArray.get(position));
                            return view;
                        }
                    };
                    lvQty.setAdapter(arrayIntAdapter);

                    lvQty.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            //  Toast.makeText(AddMixerActivity.this, "" + (i + 1), Toast.LENGTH_SHORT).show();
                            tvQty.setText("" + (i + 1));
                            dialog.dismiss();

                            boolean flag = false;

                            for (int k = 0; k < tempDataArray.size(); k++) {
                                if (tempDataArray.get(k).getId() == displayedValues.get(position).getItemId()) {
                                    flag = true;
                                    tempDataArray.remove(tempDataArray.get(k));
                                    TempDataBean temp = new TempDataBean(displayedValues.get(position).getItemId(), displayedValues.get(position).getItemName(), displayedValues.get(position).getOpeningRate(), Integer.parseInt(tvQty.getText().toString()), displayedValues.get(position).getSgst(), displayedValues.get(position).getCgst(),1);
                                    tempDataArray.add(temp);
                                    break;
                                } else {
                                    flag = false;
                                }
                            }
                            if (flag == false) {
                                TempDataBean temp = new TempDataBean(displayedValues.get(position).getItemId(), displayedValues.get(position).getItemName(), displayedValues.get(position).getOpeningRate(), Integer.parseInt(tvQty.getText().toString()), displayedValues.get(position).getSgst(), displayedValues.get(position).getCgst(),1);
                                tempDataArray.add(temp);
                            }


                            if (tvQty.getText().toString().isEmpty()) {
                                ivCancel.setVisibility(View.GONE);
                            } else {
                                ivCancel.setVisibility(View.VISIBLE);
                            }


                        }
                    });

                    ivCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tvQty.setText("");
                            ivCancel.setVisibility(View.GONE);

                            for (int i = 0; i < tempDataArray.size(); i++) {
                                if (tempDataArray.get(i).getId() == displayedValues.get(position).getItemId()) {
                                    tempDataArray.remove(tempDataArray.get(i));
                                }
                            }

                        }
                    });
                    dialog.show();
                }
            });
            return v;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
