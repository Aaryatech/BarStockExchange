package com.ats.barstockexchange.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.ats.barstockexchange.bean.Table;
import com.ats.barstockexchange.bean.TableData;
import com.ats.barstockexchange.util.CheckNetwork;
import com.ats.barstockexchange.util.InterfaceApi;
import com.ats.barstockexchange.util.OrderRejectInterface;
import com.ats.barstockexchange.util.PrintHelper;
import com.ats.barstockexchange.util.PrintReceiptType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderRejectFragment extends Fragment implements OrderRejectInterface {

    private ListView lvTable, lvOrder;

    private ArrayList<Integer> tableArray = new ArrayList<>();

    private ArrayList<OrdersByTable> ordersByTablesArray = new ArrayList<>();
    private ArrayList<OrderDisplay> orderDisplayArray;

    private ArrayList<OrderDisplay> allOrderDisplayArray = new ArrayList<>();
    private ArrayList<Table> tableNameArray = new ArrayList<>();

    TableDataAdapter adapter;
    OrderDataAdapter orderAdapter;

    int userId;
    String userType,userName;
    String ip = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_reject, container, false);

        try {
            SharedPreferences pref = getContext().getSharedPreferences(InterfaceApi.MY_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            userId = pref.getInt("UserId", 0);
            userType = pref.getString("UserType", "");
            userName=pref.getString("UserName","");
            ip = pref.getString("IP", "");
        } catch (Exception e) {
        }

        lvTable = view.findViewById(R.id.lvOrderRejected_TableList);
        lvOrder = view.findViewById(R.id.lvOrderRejected_OrderList);

        Log.e("Order Reject ", "-- onCreate");

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
            Call<OrdersSortByTable> orderListDataCall;
/*            if (userType.equalsIgnoreCase("Manager") || userType.equalsIgnoreCase("superadmin")) {
                orderListDataCall = api.getAllOrders1(5);
            } else {
                orderListDataCall = api.getAllOrdersFrom1to5();
            }*/
            orderListDataCall = api.getAllOrders1(5);


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
                                allOrderDisplayArray.clear();
                                if (lvTable.getAdapter() == null) {
                                    adapter = new TableDataAdapter(getContext(), tableArray);
                                    lvTable.setAdapter(adapter);
                                } else {
                                    adapter.notifyDataSetChanged();
                                }


                                if (lvOrder.getAdapter() == null) {
                                    orderAdapter = new OrderDataAdapter(getContext(), allOrderDisplayArray);
                                    lvOrder.setAdapter(orderAdapter);
                                } else {
                                    orderAdapter.notifyDataSetChanged();
                                }
                            } else {
                                // Log.e("Else block", "---------------");
                                tableArray.clear();
                                ordersByTablesArray.clear();
                                allOrderDisplayArray.clear();
                                tableArray.add(0);

                                for (int i = 0; i < data.getOrdersByTable().size(); i++) {

                                    ordersByTablesArray.add(data.getOrdersByTable().get(i));
                                    for (int j = 0; j < data.getOrdersByTable().get(i).getOrderDisplay().size(); j++) {
                                        if (data.getOrdersByTable().get(i).getOrderDisplay().get(j).getOrder().getBillStatus() == 3 || data.getOrdersByTable().get(i).getOrderDisplay().get(j).getOrder().getBillStatus() == 5) {
                                            tableArray.add(data.getOrdersByTable().get(i).getTableNo());
                                            allOrderDisplayArray.add(data.getOrdersByTable().get(i).getOrderDisplay().get(j));

                                        }
                                    }
                                }
                                Set<Integer> set = new HashSet<Integer>(tableArray);
                                tableArray.clear();
                                tableArray.addAll(set);

                                Log.e("RESPONSE : ", " DATA : " + tableArray);
                                adapter = new TableDataAdapter(getContext(), tableArray);
                                lvTable.setAdapter(adapter);
                                progressDialog.dismiss();

                                orderAdapter = new OrderDataAdapter(getContext(), allOrderDisplayArray);
                                lvOrder.setAdapter(orderAdapter);

                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "No Orders Found", Toast.LENGTH_SHORT).show();
                            Log.e("RESPONSE : ", " NO DATA");
                        }
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "No Orders Found", Toast.LENGTH_SHORT).show();
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

    @Override
    public void fragmentBecameVisible() {
        Log.e("Order Approve ", "-- onVisible");
        getTableData();
    }

    class TableDataAdapter extends BaseAdapter {

        Context context;
        private ArrayList<Integer> originalValues;
        private ArrayList<Integer> displayedValues;
        LayoutInflater inflater;

        public TableDataAdapter(Context context, ArrayList<Integer> catArray) {
            this.context = context;
            this.originalValues = catArray;
            this.displayedValues = catArray;
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
        public View getView(final int position, View v, final ViewGroup parent) {
            v = inflater.inflate(R.layout.custom_table_master_layout, null);
            final LinearLayout llBack = v.findViewById(R.id.llCustomTableMaster_back);
            TextView tvName = v.findViewById(R.id.tvCustomTableMaster_no);
            llBack.setBackgroundResource(R.mipmap.ic_beer_orange);
            if (displayedValues.get(position) == 0) {
                tvName.setText("All");
            } else {

                if (tableNameArray.size()>0){
                    for (int i=0;i<tableNameArray.size();i++){
                        if (displayedValues.get(position)==tableNameArray.get(i).getTableNo()){
                            tvName.setText("" + tableNameArray.get(i).getTableName());
                        }
                    }
                }
            }

            orderDisplayArray = new ArrayList<>();

            llBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    orderDisplayArray.clear();
                    if (displayedValues.get(position) == 0) {


                        for (int i = 0; i < ordersByTablesArray.size(); i++) {
                            for (int j = 0; j < ordersByTablesArray.get(i).getOrderDisplay().size(); j++) {
                                if (ordersByTablesArray.get(i).getOrderDisplay().get(j).getOrder().getBillStatus() == 3 || ordersByTablesArray.get(i).getOrderDisplay().get(j).getOrder().getBillStatus() == 5) {
                                    orderDisplayArray.add(ordersByTablesArray.get(i).getOrderDisplay().get(j));
                                }
                            }
                        }
                        orderAdapter = new OrderDataAdapter(getContext(), orderDisplayArray);
                        lvOrder.setAdapter(orderAdapter);

                    } else {
                        for (int i = 0; i < ordersByTablesArray.size(); i++) {
                            if (displayedValues.get(position) == ordersByTablesArray.get(i).getTableNo()) {
                                for (int j = 0; j < ordersByTablesArray.get(i).getOrderDisplay().size(); j++) {
                                    if (ordersByTablesArray.get(i).getOrderDisplay().get(j).getOrder().getBillStatus() == 3 || ordersByTablesArray.get(i).getOrderDisplay().get(j).getOrder().getBillStatus() == 5) {
                                        orderDisplayArray.add(ordersByTablesArray.get(i).getOrderDisplay().get(j));
                                    }

                                }
                            }
                        }
                        orderAdapter = new OrderDataAdapter(getContext(), orderDisplayArray);
                        lvOrder.setAdapter(orderAdapter);

                    }


                }
            });
            return v;
        }
    }

    class OrderDataAdapter extends BaseAdapter {

        Context context;
        private ArrayList<OrderDisplay> originalValues;
        private ArrayList<OrderDisplay> displayedValues;
        LayoutInflater inflater;

        public OrderDataAdapter(Context context, ArrayList<OrderDisplay> orderDisplays) {
            this.context = context;
            this.originalValues = orderDisplays;
            this.displayedValues = orderDisplays;
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
            v = inflater.inflate(R.layout.custom_order_display_layout, null);

            TextView tvOrder = v.findViewById(R.id.tvOrderDisp_orderId);
            TextView tvTable = v.findViewById(R.id.tvOrderDisp_tableNo);
            TextView tvBillStatus = v.findViewById(R.id.tvOrderDisp_billStatus);
            ImageView ivYes = v.findViewById(R.id.ivOrderItems_yes);
            ImageView ivNo = v.findViewById(R.id.ivOrderItems_no);
            ListView lvItem = v.findViewById(R.id.lvOrderDisp_Items);

            tvOrder.setText("" + displayedValues.get(position).getOrder().getOrderId());
            //if (tableNameArray.size())
            tvTable.setText("" + displayedValues.get(position).getOrder().getTableNo());

            ivNo.setVisibility(View.VISIBLE);
            ivYes.setVisibility(View.VISIBLE);
            if (displayedValues.get(position).getOrder().getBillStatus() == 3) {
                tvBillStatus.setText(" Order Rejected  ");
            } else if (displayedValues.get(position).getOrder().getBillStatus() == 5) {
                tvBillStatus.setText(" Order Rejected  ");
            }

            final ArrayList<OrderItem> orderItemArray = new ArrayList<>();
            for (int i = 0; i < displayedValues.get(position).getOrderItems().size(); i++) {
                orderItemArray.add(displayedValues.get(position).getOrderItems().get(i));
            }

            ivYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userType.equalsIgnoreCase("Captain")) {
                        updateBill(displayedValues.get(position).getOrder().getOrderId(), 4);//2
                        PrintHelper printHelper = new PrintHelper(getActivity(), ip, 9, displayedValues.get(position), PrintReceiptType.KOT,userName,tableNameArray);
                        printHelper.runPrintReceiptSequence();

                    } else if (userType.equalsIgnoreCase("Manager") || userType.equalsIgnoreCase("superadmin")) {
                        updateBill(displayedValues.get(position).getOrder().getOrderId(), 4);//4
                        PrintHelper printHelper = new PrintHelper(getActivity(), ip, 9, displayedValues.get(position), PrintReceiptType.KOT,userName,tableNameArray);
                        printHelper.runPrintReceiptSequence();
                    }
                }
            });

            ivNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteOrderById(displayedValues.get(position).getOrder().getOrderId());
                }
            });

            //  Log.e("OrderItem Array : ", "-------" + orderItemArray);

            ArrayAdapter<OrderItem> adapter = new ArrayAdapter<OrderItem>(getContext(), android.R.layout.simple_list_item_1, orderItemArray) {
                @NonNull
                @Override
                public View getView(int itemPosition, @Nullable View convertView, @NonNull ViewGroup parent) {
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View v = inflater.inflate(R.layout.custom_order_items_display_layout, null);

                    TextView tvTitle = (TextView) v.findViewById(R.id.tvOrderItems_name);
                    TextView tvQty = (TextView) v.findViewById(R.id.tvOrderItems_qty);
                    TextView tvRate = (TextView) v.findViewById(R.id.tvOrderItems_rate);

                    tvTitle.setText("" + orderItemArray.get(itemPosition).getItemName());
                    tvQty.setText("" + orderItemArray.get(itemPosition).getQuantity());
                    tvRate.setText("" + orderItemArray.get(itemPosition).getRate());

                    return v;
                }
            };
            lvItem.setAdapter(adapter);
            setListViewHeightBasedOnChildren(lvItem);

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

    public void updateBill(int orderId, int status) {

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

            Call<ErrorMessage> errorMessageCall = api.updateBillStatus(orderId, status);

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
                                Toast.makeText(getContext(), "Unable To Process", Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                                orderAdapter.notifyDataSetChanged();
                                adapter.notifyDataSetChanged();
                                getOrders();

                            }

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Unable To Process", Toast.LENGTH_SHORT).show();
                            Log.e("ON RESPONSE : ", "NO DATA");
                        }
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Unable To Process", Toast.LENGTH_SHORT).show();
                        Log.e("Exception : ", "" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<ErrorMessage> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Unable To Process", Toast.LENGTH_SHORT).show();
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

    public void getOrdersOnResume() {
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
            Call<OrdersSortByTable> orderListDataCall;
            /*if (userType.equalsIgnoreCase("Manager") || userType.equalsIgnoreCase("superadmin")) {
                orderListDataCall = api.getAllOrders1(5);
            } else {
                orderListDataCall = api.getAllOrdersFrom1to5();
            }*/

            orderListDataCall = api.getAllOrders1(5);

            orderListDataCall.enqueue(new Callback<OrdersSortByTable>() {
                @Override
                public void onResponse(Call<OrdersSortByTable> call, Response<OrdersSortByTable> response) {
                    try {
                        if (response.body() != null) {
                            OrdersSortByTable data = response.body();
                            if (data.getErrorMessage().getError()) {
                                tableArray.clear();
                                allOrderDisplayArray.clear();
                                if (lvTable.getAdapter() == null) {
                                    adapter = new TableDataAdapter(getContext(), tableArray);
                                    lvTable.setAdapter(adapter);
                                } else {
                                    adapter.notifyDataSetChanged();
                                }


                                if (lvOrder.getAdapter() == null) {
                                    orderAdapter = new OrderDataAdapter(getContext(), allOrderDisplayArray);
                                    lvOrder.setAdapter(orderAdapter);
                                } else {
                                    orderAdapter.notifyDataSetChanged();
                                }
                            } else {
                                tableArray.clear();
                                ordersByTablesArray.clear();
                                allOrderDisplayArray.clear();
                                tableArray.add(0);

                                for (int i = 0; i < data.getOrdersByTable().size(); i++) {
                                    ordersByTablesArray.add(data.getOrdersByTable().get(i));
                                    for (int j = 0; j < data.getOrdersByTable().get(i).getOrderDisplay().size(); j++) {
                                        if (data.getOrdersByTable().get(i).getOrderDisplay().get(j).getOrder().getBillStatus() == 3 || data.getOrdersByTable().get(i).getOrderDisplay().get(j).getOrder().getBillStatus() == 5) {
                                            tableArray.add(data.getOrdersByTable().get(i).getTableNo());
                                            allOrderDisplayArray.add(data.getOrdersByTable().get(i).getOrderDisplay().get(j));

                                        }
                                    }
                                }

                                Set<Integer> set = new HashSet<Integer>(tableArray);
                                tableArray.clear();
                                tableArray.addAll(set);

                                if (lvTable.getAdapter() == null) {
                                    adapter = new TableDataAdapter(getContext(), tableArray);
                                    lvTable.setAdapter(adapter);
                                } else {
                                    adapter.notifyDataSetChanged();
                                }


                                if (lvOrder.getAdapter() == null) {
                                    orderAdapter = new OrderDataAdapter(getContext(), allOrderDisplayArray);
                                    lvOrder.setAdapter(orderAdapter);
                                } else {
                                    orderAdapter.notifyDataSetChanged();
                                }

                            }
                        } else {
                        }
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onFailure(Call<OrdersSortByTable> call, Throwable t) {
                }
            });


        } else {
        }
    }

    public void deleteOrderById(int orderId) {

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

            Call<ErrorMessage> errorMessageCall = api.deleteOrder(orderId);

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
                                Toast.makeText(getContext(), "Unable To Delete", Toast.LENGTH_SHORT).show();

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                                //orderAdapter.notifyDataSetChanged();
                                // adapter.notifyDataSetChanged();
                                getOrders();
                            }

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Unable To Delete", Toast.LENGTH_SHORT).show();
                            Log.e("ON RESPONSE : ", "NO DATA");
                        }
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Log.e("Exception : ", "" + e.getMessage());
                        Toast.makeText(getContext(), "Unable To Delete", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ErrorMessage> call, Throwable t) {
                    progressDialog.dismiss();
                    Log.e("ON FAILURE : ", "ERROR : " + t.getMessage());
                    Toast.makeText(getContext(), "Unable To Delete", Toast.LENGTH_SHORT).show();
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

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                //call function
                try {
                    getOrdersOnResume();
                } catch (Exception e) {
                }
                handler.postDelayed(this, 10000);
            }
        }, 10000);

    }

}
