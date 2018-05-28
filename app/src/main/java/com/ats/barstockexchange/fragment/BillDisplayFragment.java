package com.ats.barstockexchange.fragment;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.barstockexchange.R;
import com.ats.barstockexchange.bean.Bill;
import com.ats.barstockexchange.bean.BillData;
import com.ats.barstockexchange.bean.CustomBillHeader;
import com.ats.barstockexchange.bean.CustomBillItems;
import com.ats.barstockexchange.bean.Table;
import com.ats.barstockexchange.bean.TableData;
import com.ats.barstockexchange.bean.User;
import com.ats.barstockexchange.bean.UserMasterData;
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

public class BillDisplayFragment extends Fragment implements View.OnClickListener {

    private TextView tvName, tvMobile, tvId;
    private EditText edFromDate, edToDate;
    private Button btnSearch;
    private ListView lvBill;
    private ImageView ivSearch;
    private LinearLayout llUser;

    private ArrayList<User> userArray = new ArrayList<>();
    UserDialogAdapter userDialogAdpt;
    Dialog dialog;

    ArrayList<Bill> billArray = new ArrayList<>();
    ArrayList<CustomBillHeader> billHeaderArray = new ArrayList<>();
    ArrayList<CustomBillItems> billHeaderItems = new ArrayList<>();

    int fromDD, fromMM, fromYYYY, toDD, toMM, toYYYY, dd, mm, yyyy;
    long fromMillis, toMillis;

    String ip = "";

    private ArrayList<Table> tableNameArray = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bill_display, container, false);
        tvTitle.setText("View Bill");

        tvId = view.findViewById(R.id.tvBillSearch_Id);
        tvName = view.findViewById(R.id.tvBillSearch_Name);
        tvMobile = view.findViewById(R.id.tvBillSearch_Mobile);
        edFromDate = view.findViewById(R.id.edBillSearch_FromDate);
        edToDate = view.findViewById(R.id.edBillSearch_ToDate);
        ivSearch = view.findViewById(R.id.ivBillSearch);
        lvBill = view.findViewById(R.id.lvBillSearch_List);
        llUser = view.findViewById(R.id.llBillSearch_User);
        llUser.setOnClickListener(this);
        edFromDate.setOnClickListener(this);
        edToDate.setOnClickListener(this);
        ivSearch.setOnClickListener(this);

        try {
            SharedPreferences pref = getContext().getSharedPreferences(InterfaceApi.MY_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            ip = pref.getString("IP", "");

        } catch (Exception e) {
        }

        Calendar calendar = Calendar.getInstance();
        yyyy = calendar.get(Calendar.YEAR);
        mm = calendar.get(Calendar.MONTH);
        dd = calendar.get(Calendar.DAY_OF_MONTH);
        edFromDate.setText(dd + "-" + (mm + 1) + "-" + yyyy);
        edToDate.setText(dd + "-" + (mm + 1) + "-" + yyyy);

        fromMillis = calendar.getTimeInMillis();
        toMillis = calendar.getTimeInMillis();

        String fromDate = edFromDate.getText().toString();
        String toDate = edToDate.getText().toString();

        getTableData(fromDate, toDate);


        return view;
    }


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
                                getBill(fromDate, toDate);

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

    public void showDialog(ArrayList<User> userList) {
        dialog = new Dialog(getContext(), R.style.AlertDialogTheme);
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.custom_user_selection_layout, null, false);
        dialog.setContentView(v);
        dialog.setCancelable(true);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        EditText edSearch = v.findViewById(R.id.edUserDialog_Search);
        ListView lvUsers = v.findViewById(R.id.lvUserDialog);

        userDialogAdpt = new UserDialogAdapter(getContext(), userList);
        lvUsers.setAdapter(userDialogAdpt);

        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userDialogAdpt.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        dialog.show();
    }

    public void getUserData() {
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
            final Call<UserMasterData> userDataCall = api.getAllCustomers();

            final Dialog progressDialog = new Dialog(getContext());
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setCancelable(false);
            progressDialog.setContentView(R.layout.loading_progress_layout);
            progressDialog.show();

            userDataCall.enqueue(new Callback<UserMasterData>() {
                @Override
                public void onResponse(Call<UserMasterData> call, Response<UserMasterData> response) {
                    try {
                        if (response.body() != null) {
                            UserMasterData data = response.body();
                            if (data.getErrorMessage().getError()) {
                                progressDialog.dismiss();
                                Log.e("ON RESPONSE : ", " ERROR : " + data.getErrorMessage().getMessage());
                                // Toast.makeText(getContext(), "Unable To Fetch Data", Toast.LENGTH_SHORT).show();
                            } else {
                                userArray.clear();
                                for (int i = 0; i < data.getUser().size(); i++) {
                                    userArray.add(i, data.getUser().get(i));
                                }
                                Log.e("RESPONSE : ", " DATA : " + userArray);


                                progressDialog.dismiss();
                            }
                        } else {
                            progressDialog.dismiss();
                            //Toast.makeText(getContext(), "Unable To Fetch Data", Toast.LENGTH_SHORT).show();
                            Log.e("RESPONSE : ", " NO DATA");
                        }
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Log.e("Exception : ", "" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<UserMasterData> call, Throwable t) {
                    //Toast.makeText(getContext(), "unable To Fetch Data", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    Log.e("ON FAILURE : ", " ERROR : " + t.getMessage());
                }
            });


        } else {
            Log.e("Connection - ", "Failed");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.llBillSearch_User) {
            showDialog(userArray);
        } else if (v.getId() == R.id.edBillSearch_FromDate) {

            int yr,mn,dt;

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(fromMillis);
            yr = cal.get(Calendar.YEAR);
            mn = cal.get(Calendar.MONTH);
            dt = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(getActivity(), R.style.AlertDialogTheme1, fromDateListener, yr, mn, dt);
            dialog.show();
        } else if (v.getId() == R.id.edBillSearch_ToDate) {

            int yr,mn,dt;

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(toMillis);
            yr = cal.get(Calendar.YEAR);
            mn = cal.get(Calendar.MONTH);
            dt = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(getActivity(), R.style.AlertDialogTheme1, toDateListener, yr, mn, dt);
            dialog.show();
        } else if (v.getId() == R.id.ivBillSearch) {
            String fromDate = edFromDate.getText().toString();
            String toDate = edToDate.getText().toString();
            getBill(fromDate, toDate);
        }
    }

    public class UserDialogAdapter extends BaseAdapter implements Filterable {

        private ArrayList<User> originalValues;
        private ArrayList<User> displayedValues;
        LayoutInflater inflater;

        public UserDialogAdapter(Context context, ArrayList<User> users) {
            this.originalValues = users;
            this.displayedValues = users;
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
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    FilterResults results = new FilterResults();
                    ArrayList<User> filteredArrayList = new ArrayList<>();
                    if (originalValues == null) {
                        originalValues = new ArrayList<User>(displayedValues);
                    }

                    if (charSequence == null || charSequence.length() == 0) {
                        results.count = originalValues.size();
                        results.values = originalValues;
                    } else {
                        charSequence = charSequence.toString().toLowerCase();
                        for (int i = 0; i < originalValues.size(); i++) {
                            String id = "" + originalValues.get(i).getUserId();
                            String name = originalValues.get(i).getFirstname();
                            String mobile = originalValues.get(i).getMobile();
                            if (id.toLowerCase().startsWith(charSequence.toString()) || name.toLowerCase().startsWith(charSequence.toString()) || name.toLowerCase().contains(charSequence.toString()) || mobile.toLowerCase().startsWith(charSequence.toString())) {
                                filteredArrayList.add(new User(originalValues.get(i).getUserId(), originalValues.get(i).getFirstname(), originalValues.get(i).getMobile(), originalValues.get(i).getPassword(), originalValues.get(i).getIsActive(), originalValues.get(i).getDelStatus(), originalValues.get(i).getEnterBy(), originalValues.get(i).getDeviceToken()));
                            }
                        }
                        results.count = filteredArrayList.size();
                        results.values = filteredArrayList;
                    }

                    return results;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    displayedValues = (ArrayList<User>) filterResults.values;
                    notifyDataSetChanged();
                }
            };

            return filter;
        }

        public class ViewHolder {
            TextView tvName, tvId, tvMobile;
            LinearLayout llUser;
        }

        @Override
        public View getView(final int position, View v, ViewGroup parent) {
            UserDialogAdapter.ViewHolder holder = null;

            if (v == null) {
                v = inflater.inflate(R.layout.user_dialog_item_layout, null);
                holder = new UserDialogAdapter.ViewHolder();
                holder.tvId = v.findViewById(R.id.tvUserDialogItem_Id);
                holder.tvName = v.findViewById(R.id.tvUserDialogItem_Name);
                holder.tvMobile = v.findViewById(R.id.tvUserDialogItem_Mobile);
                holder.llUser = v.findViewById(R.id.llUserDialogItem);
                v.setTag(holder);
            } else {
                holder = (UserDialogAdapter.ViewHolder) v.getTag();
            }

            holder.tvId.setText("" + displayedValues.get(position).getUserId());
            holder.tvName.setText("" + displayedValues.get(position).getFirstname());
            holder.tvMobile.setText("" + displayedValues.get(position).getMobile());

            holder.llUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvName.setText("" + displayedValues.get(position).getFirstname());
                    tvMobile.setText("" + displayedValues.get(position).getMobile());
                    tvId.setText("" + displayedValues.get(position).getUserId());
                    dialog.dismiss();
                }
            });


            return v;
        }
    }

    private DatePickerDialog.OnDateSetListener fromDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            fromYYYY = year;
            fromMM = month + 1;
            fromDD = dayOfMonth;
            edFromDate.setText(fromDD + "-" + fromMM + "-" + fromYYYY);

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

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            toMillis = calendar.getTimeInMillis();
        }
    };


    public void getBill(String fromDate, String toDate) {
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
            Call<BillData> billDataCall = api.getBillData(fromDate, toDate);


            final Dialog progressDialog = new Dialog(getContext());
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setCancelable(false);
            progressDialog.setContentView(R.layout.loading_progress_layout);
            progressDialog.show();


            billDataCall.enqueue(new Callback<BillData>() {
                @Override
                public void onResponse(Call<BillData> call, Response<BillData> response) {
                    try {
                        if (response.body() != null) {
                            BillData data = response.body();
                            if (data.getErrorMessage().getError()) {
                                progressDialog.dismiss();
                                Log.e("ON RESPONSE : ", " ERROR : " + data.getErrorMessage().getMessage());
                            } else {
                                progressDialog.dismiss();

                                if (data.getBill().size() > 0) {

                                    billArray.clear();
                                    billHeaderArray.clear();

                                    ArrayList<Integer> billIdArray = new ArrayList<>();

                                    for (int i = 0; i < data.getBill().size(); i++) {
                                        billIdArray.add(data.getBill().get(i).getBillId());
                                        billArray.add(data.getBill().get(i));
                                    }

                                    Set<Integer> billIdSet = new HashSet<>(billIdArray);
                                    billIdArray.clear();
                                    billIdArray.addAll(billIdSet);

                                    Collections.sort(billIdArray, Collections.<Integer>reverseOrder());

                                    Log.e("Bill Id : ", "-------" + billIdArray);


                                    for (int i = 0; i < billIdArray.size(); i++) {
                                        CustomBillHeader header = new CustomBillHeader();
                                        ArrayList<CustomBillItems> itemsArray = new ArrayList<>();
                                        for (int j = 0; j < data.getBill().size(); j++) {
                                            if (billIdArray.get(i) == data.getBill().get(j).getBillId()) {
                                                CustomBillItems items = new CustomBillItems(data.getBill().get(j).getItemName(), data.getBill().get(j).getQuantity(), data.getBill().get(j).getRate());
                                                itemsArray.add(items);

                                                header.setBillId(data.getBill().get(j).getBillId());
                                                header.setUserId(data.getBill().get(j).getUserId());
                                                header.setBillDate(data.getBill().get(j).getBillDate());
                                                header.setDiscount(data.getBill().get(j).getDiscount());
                                                header.setPayableAmount(data.getBill().get(j).getPayableAmt());
                                                header.setBillNo(data.getBill().get(j).getBillNo());
                                            }
                                        }
                                        header.setCustomBillItems(itemsArray);
                                        billHeaderArray.add(header);
                                    }

                                    Log.e("BILL HEADER : ", "" + billHeaderArray);

                                    BillDataAdapter adpt = new BillDataAdapter(getContext(), billHeaderArray);
                                    lvBill.setAdapter(adpt);

                                } else {
                                    Toast.makeText(getContext(), "No Bills Found", Toast.LENGTH_SHORT).show();
                                }


                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "No Bills Found", Toast.LENGTH_SHORT).show();
                            Log.e("RESPONSE : ", " NO DATA");
                        }
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "No Bills Found", Toast.LENGTH_SHORT).show();
                        Log.e("Exception : ", "" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<BillData> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "No Bills Found", Toast.LENGTH_SHORT).show();
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

    class BillDataAdapter extends BaseAdapter {

        Context context;
        private ArrayList<CustomBillHeader> originalValues;
        private ArrayList<CustomBillHeader> displayedValues;
        LayoutInflater inflater;

        public BillDataAdapter(Context context, ArrayList<CustomBillHeader> catArray) {
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
        public View getView(final int position, View v, ViewGroup parent) {
            v = inflater.inflate(R.layout.custom_bill_display_layout, null);

            TextView tvBillId = v.findViewById(R.id.tvBillDisplay_BillId);
            TextView tvUserId = v.findViewById(R.id.tvBillDisplay_UserId);
            TextView tvBillDate = v.findViewById(R.id.tvBillDisplay_BillDate);
            TextView tvDiscount = v.findViewById(R.id.tvBillDisplay_Discount);
            TextView tvAmount = v.findViewById(R.id.tvBillDisplay_Amount);
            ListView lvItems = v.findViewById(R.id.lvBillDisplay_Item);
            ImageView ivPrint = v.findViewById(R.id.ivBillDisplay_Print);

            tvBillId.setText("Bill No : " + displayedValues.get(position).getBillNo());
            tvUserId.setText("User Id : " + displayedValues.get(position).getUserId());
            tvBillDate.setText("Date : " + displayedValues.get(position).getBillDate());
            tvDiscount.setText("Discount : " + displayedValues.get(position).getDiscount());
            tvAmount.setText("Amount : " + String.format("%.2f", displayedValues.get(position).getPayableAmount()) + "/-");

            ArrayList<CustomBillItems> billItemsArray = new ArrayList<>();
            for (int i = 0; i < displayedValues.get(position).getCustomBillItems().size(); i++) {
                billItemsArray.add(displayedValues.get(position).getCustomBillItems().get(i));
            }


            BillItemsAdapter itemAdpter = new BillItemsAdapter(getContext(), billItemsArray);
            lvItems.setAdapter(itemAdpter);
            setListViewHeightBasedOnChildren(lvItems);

            ivPrint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PrintHelper printHelper = new PrintHelper(getActivity(), ip, 9, displayedValues.get(position), PrintReceiptType.BILL,tableNameArray);
                    printHelper.runPrintReceiptSequence();
                }
            });

            return v;
        }

    }

    public class BillItemsAdapter extends BaseAdapter {

        private ArrayList<CustomBillItems> originalValues;
        private ArrayList<CustomBillItems> displayedValues;
        LayoutInflater inflater;

        public BillItemsAdapter(Context context, ArrayList<CustomBillItems> stringArrayList) {
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
            v = inflater.inflate(R.layout.custom_bill_item_layout, null);
            TextView tvItem = v.findViewById(R.id.tvCustomViewOrderItem_Item);
            TextView tvQty = v.findViewById(R.id.tvCustomViewOrderItem_Qty);
            TextView tvRate = v.findViewById(R.id.tvCustomViewOrderItem_Rate);

            tvItem.setText("" + displayedValues.get(position).getItemName());
            tvQty.setText("" + displayedValues.get(position).getQuantity());
            tvRate.setText("" + displayedValues.get(position).getRate());

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
