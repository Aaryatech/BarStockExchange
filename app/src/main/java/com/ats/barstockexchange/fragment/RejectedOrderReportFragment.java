package com.ats.barstockexchange.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.barstockexchange.R;
import com.ats.barstockexchange.bean.BillData;
import com.ats.barstockexchange.bean.CustomBillHeader;
import com.ats.barstockexchange.bean.CustomBillItems;
import com.ats.barstockexchange.bean.Order;
import com.ats.barstockexchange.bean.OrderDisplay;
import com.ats.barstockexchange.bean.OrderItem;
import com.ats.barstockexchange.bean.RejectedOrder;
import com.ats.barstockexchange.bean.Table;
import com.ats.barstockexchange.bean.TableData;
import com.ats.barstockexchange.util.CheckNetwork;
import com.ats.barstockexchange.util.InterfaceApi;
import com.ats.barstockexchange.util.PrintHelper;
import com.ats.barstockexchange.util.PrintReceiptType;

import java.util.ArrayList;
import java.util.Calendar;
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

import static com.ats.barstockexchange.activity.HomeActivity.tvTitle;

public class RejectedOrderReportFragment extends Fragment implements View.OnClickListener {

    private TextView tvFromDate, tvToDate;
    private EditText edFromDate, edToDate;
    private ImageView ivSearch;
    private ListView lvOrders;

    int fromDD, fromMM, fromYYYY, toDD, toMM, toYYYY, dd, mm, yyyy;
    long fromMillis, toMillis;

    private ArrayList<Table> tableNameArray = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rejected_order_report, container, false);
        tvTitle.setText("Rejected Orders Report");

        tvFromDate = view.findViewById(R.id.tvFromDate);
        tvToDate = view.findViewById(R.id.tvToDate);
        edFromDate = view.findViewById(R.id.edFromDate);
        edToDate = view.findViewById(R.id.edToDate);
        ivSearch = view.findViewById(R.id.ivSearch);
        lvOrders = view.findViewById(R.id.lvOrders);

        edFromDate.setOnClickListener(this);
        edToDate.setOnClickListener(this);
        ivSearch.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        yyyy = calendar.get(Calendar.YEAR);
        mm = calendar.get(Calendar.MONTH);
        dd = calendar.get(Calendar.DAY_OF_MONTH);
        edFromDate.setText(dd + "-" + (mm + 1) + "-" + yyyy);
        edToDate.setText(dd + "-" + (mm + 1) + "-" + yyyy);

        tvFromDate.setText(yyyy + "-" + (mm + 1) + "-" + dd);
        tvToDate.setText(yyyy + "-" + (mm + 1) + "-" + dd);

        fromMillis = calendar.getTimeInMillis();
        toMillis = calendar.getTimeInMillis();

        String fromDate = yyyy + "-" + (mm + 1) + "-" + dd + " 00:00:00";
        String toDate = yyyy + "-" + (mm + 1) + "-" + dd + " 23:59:59";

        getTableData(fromDate, toDate);

        return view;
    }


    private DatePickerDialog.OnDateSetListener fromDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            fromYYYY = year;
            fromMM = month + 1;
            fromDD = dayOfMonth;
            edFromDate.setText(fromDD + "-" + fromMM + "-" + fromYYYY);
            tvFromDate.setText(fromYYYY + "-" + fromMM + "-" + fromDD + " 00:00:00");

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            fromMillis = calendar.getTimeInMillis();
        }
    };

    private DatePickerDialog.OnDateSetListener toDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            toYYYY = year;
            toMM = month + 1;
            toDD = dayOfMonth;
            edToDate.setText(toDD + "-" + toMM + "-" + toYYYY);
            tvToDate.setText(toYYYY + "-" + toMM + "-" + toDD + " 23:59:59");

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            toMillis = calendar.getTimeInMillis();
        }
    };

    public void getTableData(final String fromDate, final String toDate) {
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
                                getRejectedOrders(fromDate, toDate);

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


    public void getRejectedOrders(String fromDate, String toDate) {
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
            Call<ArrayList<RejectedOrder>> arrayListCall = api.getRejectedOrderList(fromDate, toDate);

            final Dialog progressDialog = new Dialog(getContext());
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setCancelable(false);
            progressDialog.setContentView(R.layout.loading_progress_layout);
            progressDialog.show();

            arrayListCall.enqueue(new Callback<ArrayList<RejectedOrder>>() {
                @Override
                public void onResponse(Call<ArrayList<RejectedOrder>> call, Response<ArrayList<RejectedOrder>> response) {
                    try {
                        Log.e("REJECTED ORDERS : ", "----------------------------" + response.body());
                        if (response.body() != null) {
                            ArrayList<RejectedOrder> data = response.body();
                            if (data == null) {
                                progressDialog.dismiss();
                            } else {
                                progressDialog.dismiss();

                                if (data.size() > 0) {

                                    ArrayList<OrderDisplay> orderDisplayArrayList = new ArrayList<>();

                                    for (int i = 0; i < data.size(); i++) {
                                        OrderDisplay orderDisplay = new OrderDisplay();

                                        Order order = new Order(data.get(i).getOrderId(), data.get(i).getTableNo(), data.get(i).getBillStatus(), data.get(i).getOrderDate());
                                        orderDisplay.setOrder(order);

                                        int tempOrderId = data.get(i).getOrderId();
                                        int count = 0;
                                        ArrayList<OrderItem> orderItemArray = new ArrayList<>();
                                        for (int j = 0; j < data.size(); j++) {
                                            if (tempOrderId == data.get(j).getOrderId()) {
                                                OrderItem item = new OrderItem(data.get(j).getItemId(), data.get(j).getItemName(), data.get(j).getQuantity(), data.get(j).getRate());
                                                orderItemArray.add(item);
                                            }
                                        }
                                        orderDisplay.setOrderItems(orderItemArray);

                                        if (orderDisplayArrayList.size() > 0) {
                                            int status = 0;
                                            for (int k = 0; k < orderDisplayArrayList.size(); k++) {
                                                if (orderDisplay.getOrder().getOrderId() == orderDisplayArrayList.get(k).getOrder().getOrderId()) {
                                                    status = 1;
                                                }
                                            }

                                            if (status == 0) {
                                                orderDisplayArrayList.add(orderDisplay);
                                            }
                                        } else {
                                            orderDisplayArrayList.add(orderDisplay);
                                        }
                                    }

                                    Set<OrderDisplay> orderDisplaySet = new HashSet<>();
                                    orderDisplaySet.addAll(orderDisplayArrayList);
                                    orderDisplayArrayList.clear();
                                    orderDisplayArrayList.addAll(orderDisplaySet);

                                    OrderDataAdapter adapter = new OrderDataAdapter(getContext(), orderDisplayArrayList);
                                    lvOrders.setAdapter(adapter);

                                }

                                Log.e("REJECTED ORDERS : ", "" + data);

                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "No Orders Found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "No Orders Found", Toast.LENGTH_SHORT).show();
                        Log.e("Exception : ", "" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<RejectedOrder>> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "No Orders Found", Toast.LENGTH_SHORT).show();
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
    public void onClick(View view) {
        if (view.getId() == R.id.edFromDate) {
            int yr, mn, dt;

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(fromMillis);
            yr = cal.get(Calendar.YEAR);
            mn = cal.get(Calendar.MONTH);
            dt = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(getActivity(), R.style.AlertDialogTheme1, fromDateListener, yr, mn, dt);
            dialog.show();
        } else if (view.getId() == R.id.edToDate) {
            int yr, mn, dt;

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(toMillis);
            yr = cal.get(Calendar.YEAR);
            mn = cal.get(Calendar.MONTH);
            dt = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(getActivity(), R.style.AlertDialogTheme1, toDateListener, yr, mn, dt);
            dialog.show();
        } else if (view.getId() == R.id.ivSearch) {
            String fromDate = tvFromDate.getText().toString();
            String toDate = tvToDate.getText().toString();
            getRejectedOrders(fromDate, toDate);
        }
    }


    class OrderDataAdapter extends BaseAdapter {

        Context context;
        private ArrayList<OrderDisplay> displayedValues;
        LayoutInflater inflater;

        public OrderDataAdapter(Context context, ArrayList<OrderDisplay> orderDisplays) {
            this.context = context;
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
            ListView lvItem = v.findViewById(R.id.lvOrderDisp_Items);

            tvOrder.setText("" + displayedValues.get(position).getOrder().getOrderId());

            if (tableNameArray.size() > 0) {
                for (int i = 0; i < tableNameArray.size(); i++) {
                    if (displayedValues.get(position).getOrder().getTableNo() == tableNameArray.get(i).getTableNo()) {
                        tvTable.setText("" + tableNameArray.get(i).getTableName());
                    }
                }
            }

            final ArrayList<OrderItem> orderItemArray = new ArrayList<>();
            for (int i = 0; i < displayedValues.get(position).getOrderItems().size(); i++) {
                orderItemArray.add(displayedValues.get(position).getOrderItems().get(i));
            }


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

}
