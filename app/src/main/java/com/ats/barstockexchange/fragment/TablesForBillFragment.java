package com.ats.barstockexchange.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.barstockexchange.R;
import com.ats.barstockexchange.bean.OrdersSortByTable;
import com.ats.barstockexchange.bean.Table;
import com.ats.barstockexchange.bean.TableData;
import com.ats.barstockexchange.util.CheckNetwork;
import com.ats.barstockexchange.util.InterfaceApi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TablesForBillFragment extends Fragment {

    private GridView gvList;
    MyAdapter adapter;

    private ArrayList<Table> tableNameArray = new ArrayList<>();

    private ArrayList<Integer> tableArray = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tables_for_bill, container, false);


        gvList = view.findViewById(R.id.gvBillTable);

        // getOrders();
        getTableData();
        return view;
    }

    public void getTableData() {
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
            Call<TableData> tableDataCall = api.getAllTable();


            final Dialog progressDialog = new Dialog(getContext());
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setCancelable(false);
            progressDialog.setContentView(R.layout.loading_progress_layout);
            progressDialog.show();


            tableDataCall.enqueue(new Callback<TableData>() {
                @Override
                public void onResponse(Call<TableData> call, Response<TableData> response) {
                    try {
                        if (response.body() != null) {
                            TableData data = response.body();
                            if (data.getErrorMessage().getError()) {
                                progressDialog.dismiss();
                                Log.e("ON RESPONSE : ", " ERROR : " + data.getErrorMessage().getMessage());
                                Toast.makeText(getContext(), "unable to fetch data", Toast.LENGTH_SHORT).show();
                            } else {
                                tableNameArray.clear();
                                for (int i = 0; i < data.getTable().size(); i++) {
                                    tableNameArray.add(i, data.getTable().get(i));
                                }
                                getOrders();

                                Log.e("RESPONSE : ", " DATA : " + tableNameArray);
                                //setAdapterData();

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
                public void onFailure(Call<TableData> call, Throwable t) {
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


    public class MyAdapter extends BaseAdapter {

        private ArrayList<Integer> originalValues;
        private ArrayList<Integer> displayedValues;
        LayoutInflater inflater;

        public MyAdapter(Context context, ArrayList<Integer> integerArrayList) {
            this.originalValues = integerArrayList;
            this.displayedValues = integerArrayList;
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
            v = inflater.inflate(R.layout.custom_table_master_layout, null);
            LinearLayout llBack = v.findViewById(R.id.llCustomTableMaster_back);
            TextView tvTableNo = v.findViewById(R.id.tvCustomTableMaster_no);

            llBack.setBackgroundResource(R.mipmap.ic_beer_orange);

            if (tableNameArray.size() > 0) {
                for (int i = 0; i < tableNameArray.size(); i++) {
                    if (displayedValues.get(position) == tableNameArray.get(i).getTableNo()) {
                        tvTableNo.setText("" + tableNameArray.get(i).getTableName());
                    }
                }
            }

            llBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment adf = new GenerateBillFragment();
                    Bundle args = new Bundle();
                    args.putInt("TableNo", displayedValues.get(position));
                    adf.setArguments(args);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, adf, "BillTableMaster").commit();
                }
            });

            return v;
        }
    }


    public void getOrders() {
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
            Call<OrdersSortByTable> orderListDataCall = api.getAllOrders1(4);

            final Dialog progressDialog = new Dialog(getContext());
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setCancelable(false);
            progressDialog.setContentView(R.layout.loading_progress_layout);
            progressDialog.show();

            orderListDataCall.enqueue(new Callback<OrdersSortByTable>() {
                @Override
                public void onResponse(Call<OrdersSortByTable> call, Response<OrdersSortByTable> response) {
                    try {
                        Log.e("RESPONSE : ", "" + response.body());
                        if (response.body() != null) {
                            OrdersSortByTable data = response.body();
                            if (data.getErrorMessage().getError()) {
                                progressDialog.dismiss();
                                Log.e("ON RESPONSE : ", " ERROR : " + data.getErrorMessage().getMessage());
                                Toast.makeText(getContext(), "" + data.getErrorMessage().getMessage(), Toast.LENGTH_SHORT).show();
                                tableArray.clear();
                                if (gvList.getAdapter() == null) {
                                    adapter = new MyAdapter(getContext(), tableArray);
                                    gvList.setAdapter(adapter);
                                } else {
                                    adapter.notifyDataSetChanged();
                                }


                            } else {
                                tableArray.clear();

                                for (int i = 0; i < data.getOrdersByTable().size(); i++) {
                                    tableArray.add(data.getOrdersByTable().get(i).getTableNo());
                                }

                                Set<Integer> set = new HashSet<Integer>(tableArray);
                                tableArray.clear();
                                tableArray.addAll(set);

                                Log.e("RESPONSE : ", " DATA : " + tableArray);
                                adapter = new MyAdapter(getContext(), tableArray);
                                gvList.setAdapter(adapter);
                                progressDialog.dismiss();
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Unable To Fetch Data", Toast.LENGTH_SHORT).show();
                            Log.e("RESPONSE : ", " NO DATA");
                        }
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Log.e("Exception : ", "" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<OrdersSortByTable> call, Throwable t) {
                    Toast.makeText(getContext(), "Unable To Fetch Data", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    Log.e("ON FAILURE : ", " ERROR : " + t.getMessage());
                }
            });

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
            builder.setTitle("Check Connectivity");
            builder.setCancelable(false);
            builder.setMessage("Please Connect To Internet");
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
}
