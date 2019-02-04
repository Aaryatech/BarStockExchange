package com.ats.barstockexchange.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.barstockexchange.R;
import com.ats.barstockexchange.bean.ErrorMessage;
import com.ats.barstockexchange.bean.Item;
import com.ats.barstockexchange.bean.ItemData;
import com.ats.barstockexchange.bean.OrderDetailsList;
import com.ats.barstockexchange.bean.OrderEntry;
import com.ats.barstockexchange.bean.OrderItem;
import com.ats.barstockexchange.bean.Table;
import com.ats.barstockexchange.bean.TableData;
import com.ats.barstockexchange.bean.TempDataBean;
import com.ats.barstockexchange.bean.User;
import com.ats.barstockexchange.bean.UserMasterData;
import com.ats.barstockexchange.fragment.ItemMasterFragment;
import com.ats.barstockexchange.util.CheckNetwork;
import com.ats.barstockexchange.util.InterfaceApi;
import com.ats.barstockexchange.util.PrintHelper;
import com.ats.barstockexchange.util.PrintReceiptType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderReviewActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnTable, btnConfirm, btnUser;
    private ArrayAdapter<Integer> arrayIntAdapter;
    private ArrayAdapter<String> arrayStrAdapter;
    private ListView lvOrders;
    private TextView tvTotalPrice, tvTableNo, tvUserId;

    UserDialogAdapter userDialogAdpt;
    Dialog dialog;

    String ip = "", userName;

    private ArrayList<User> userArray = new ArrayList<>();
    int userId;

    ArrayList<TempDataBean> tempList = new ArrayList<>();
    private ArrayList<Table> tableArrayList = new ArrayList<>();
    private ArrayList<Item> itemArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_review);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnTable = findViewById(R.id.btnOrderReview_table);
        btnConfirm = findViewById(R.id.btnOrderReview_confirm);
        btnUser = findViewById(R.id.btnOrderReview_User);
        lvOrders = findViewById(R.id.lvOrderReview);
        tvTotalPrice = findViewById(R.id.tvOrderReviewActivity_TotalPrice);
        tvTableNo = findViewById(R.id.tvOrderReview_TableNo);
        tvUserId = findViewById(R.id.tvOrderReview_UserId);

        btnTable.setOnClickListener(this);
        btnUser.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);

        try {
            SharedPreferences pref = getApplicationContext().getSharedPreferences(InterfaceApi.MY_PREF, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            userId = pref.getInt("UserId", 0);
            ip = pref.getString("IP", "");
            userName = pref.getString("UserName", "");
        } catch (Exception e) {
        }


        try {
            Bundle bundle = getIntent().getExtras();
            String tempString = bundle.getString("TempDataArray");

            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<TempDataBean>>() {
            }.getType();
            tempList = gson.fromJson(tempString, type);

            OrderReviewAdapter orderAdapter = new OrderReviewAdapter(OrderReviewActivity.this, tempList);
            lvOrders.setAdapter(orderAdapter);

            float total = 0;
            for (TempDataBean data : tempList) {
                Log.e("Temp Data", "-----------" + data);

//                float totalTax = data.getSgst() + data.getCgst();
//                float percent = (data.getPrice() * totalTax) / 100;
//                float result = data.getPrice() + percent;

                float result = data.getPrice() * data.getQty();

                total = total + result;
            }

            tvTotalPrice.setText("\u20B9 " + total);


        } catch (Exception e) {
        }

        getUserData(userId);
        getTableList();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnOrderReview_table) {

            final Dialog dialog = new Dialog(OrderReviewActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_quantity_dialog);

            final ListView lvQty = (ListView) dialog.findViewById(R.id.lvQtyDialog);
            TextView tvTitle = dialog.findViewById(R.id.tvQtyTitle);
            tvTitle.setText("Select Table");

            final ArrayList<Integer> intArray = new ArrayList<>();
            final ArrayList<String> tblArray = new ArrayList<>();

            for (int i = 0; i < tableArrayList.size(); i++) {
                intArray.add(tableArrayList.get(i).getTableNo());
                tblArray.add(tableArrayList.get(i).getTableName());
            }


/*
            arrayIntAdapter = new ArrayAdapter<Integer>(OrderReviewActivity.this, android.R.layout.simple_expandable_list_item_1, intArray) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    LayoutInflater inflater1 = OrderReviewActivity.this.getLayoutInflater();
                    View view = inflater1.inflate(R.layout.custom_quantity_item_layout, parent, false);
                    TextView tvQty = view.findViewById(R.id.tvQtyItem);
                    tvQty.setText("" + intArray.get(position));
                    return view;
                }
            };
*/

            ArrayAdapter<String> arrayStrAdapter = new ArrayAdapter<String>(OrderReviewActivity.this, android.R.layout.simple_expandable_list_item_1, tblArray) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    LayoutInflater inflater1 = OrderReviewActivity.this.getLayoutInflater();
                    View view = inflater1.inflate(R.layout.custom_quantity_item_layout, parent, false);
                    TextView tvQty = view.findViewById(R.id.tvQtyItem);
                    tvQty.setText("" + tblArray.get(position));
                    return view;
                }
            };

            lvQty.setAdapter(arrayStrAdapter);

            lvQty.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //  Toast.makeText(AddMixerActivity.this, "" + (i + 1), Toast.LENGTH_SHORT).show();
                    btnTable.setText("Table : " + tblArray.get(i));
                    tvTableNo.setText("" + intArray.get(i));
                    dialog.dismiss();
                }
            });

            dialog.show();

        } else if (view.getId() == R.id.btnOrderReview_confirm) {

            try {
                if (tempList.size() > 0) {

                    ArrayList<OrderDetailsList> detailsListsArray = new ArrayList<>();
                    for (int i = 0; i < tempList.size(); i++) {
                        OrderDetailsList orderDetailsList = new OrderDetailsList(0, 0, tempList.get(i).getId(), tempList.get(i).getQty(), tempList.get(i).getPrice(), 0, tempList.get(i).getIsMixer());
                        detailsListsArray.add(orderDetailsList);
                    }

                    if (tvTableNo.getText().toString().isEmpty()) {
                        Toast.makeText(this, "Please Select Table", Toast.LENGTH_SHORT).show();
                        btnTable.requestFocus();
                    } else if (tvUserId.getText().toString().isEmpty()) {
                        Toast.makeText(this, "Please Select User", Toast.LENGTH_SHORT).show();
                        btnUser.requestFocus();
                    } else {
                        int tableNo = Integer.parseInt(tvTableNo.getText().toString());
                        int user_id = Integer.parseInt(tvUserId.getText().toString());

                        OrderEntry entry = new OrderEntry(0, user_id, tableNo, 1, "0", 0, detailsListsArray);
                        placeOrder(entry);


                    }

                } else {
                    Toast.makeText(this, "Please Select Dirnks First!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
            }
        } else if (view.getId() == R.id.btnOrderReview_User) {

            showDialog(userArray);
           /* final Dialog dialog = new Dialog(OrderReviewActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_quantity_dialog);

            final ListView lvQty = (ListView) dialog.findViewById(R.id.lvQtyDialog);
            TextView tvTitle = dialog.findViewById(R.id.tvQtyTitle);
            tvTitle.setText("Select User");

            final ArrayList<String> strArray = new ArrayList<>();
            final ArrayList<Integer> idArray = new ArrayList<>();

            for (int i = 0; i < userArray.size(); i++) {
                strArray.add(userArray.get(i).getFirstname());
                idArray.add(userArray.get(i).getUserId());
            }
            Log.e("STR ARRAY : ", "-----------" + strArray);


            arrayStrAdapter = new ArrayAdapter<String>(OrderReviewActivity.this, android.R.layout.simple_expandable_list_item_1, strArray) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    LayoutInflater inflater1 = OrderReviewActivity.this.getLayoutInflater();
                    View view = inflater1.inflate(R.layout.custom_quantity_item_layout, parent, false);
                    TextView tvQty = view.findViewById(R.id.tvQtyItem);
                    tvQty.setText("" + strArray.get(position));
                    Log.e("User : ", "---------- " + position + "  : " + strArray.get(position));
                    return view;
                }
            };
            lvQty.setAdapter(arrayStrAdapter);

            lvQty.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //  Toast.makeText(AddMixerActivity.this, "" + (i + 1), Toast.LENGTH_SHORT).show();
                    btnTable.setText("" + strArray.get(i));
                    tvUserId.setText("" + idArray.get(i));
                    dialog.dismiss();
                }
            });

            dialog.show();*/
        }
    }

    public class OrderReviewAdapter extends BaseAdapter {

        private ArrayList<TempDataBean> originalValues;
        private ArrayList<TempDataBean> displayedValues;
        LayoutInflater inflater;

        public OrderReviewAdapter(Context context, ArrayList<TempDataBean> stringArrayList) {
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
            v = inflater.inflate(R.layout.custom_order_review_layout, null);
            TextView tvName = v.findViewById(R.id.tvOrderReview_ItemName);
            TextView tvQty = v.findViewById(R.id.tvOrderReview_ItemQty);
            TextView tvPrice = v.findViewById(R.id.tvOrderReview_ItemPrice);
            ImageView ivCancel = v.findViewById(R.id.ivCancelItem);

            tvName.setText("" + displayedValues.get(position).getName());
            tvQty.setText("" + displayedValues.get(position).getQty());
            tvPrice.setText("\u20B9 " + displayedValues.get(position).getPrice());

            LinearLayout linearLayout1 = (LinearLayout) v.findViewById(R.id.llOrderReview_ImageView);
            for (int x = 0; x < displayedValues.get(position).getQty(); x++) {
                ImageView image = new ImageView(OrderReviewActivity.this);
                image.setBackgroundResource(R.mipmap.glass_icon);
                linearLayout1.addView(image);
            }

            ivCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < tempList.size(); i++) {
                        if (displayedValues.get(position).getId() == tempList.get(i).getId()) {
                            tempList.remove(tempList.get(i));
                            notifyDataSetChanged();
                        }
                    }

                    float total = 0;
                    for (int i = 0; i < tempList.size(); i++) {
                        float result = tempList.get(i).getPrice() * tempList.get(i).getQty();
                        total = total + result;
                    }

                    tvTotalPrice.setText("\u20B9 " + total);

                }
            });


            return v;
        }
    }


    public void placeOrder(final OrderEntry orderEntry) {
        if (CheckNetwork.isInternetAvailable(OrderReviewActivity.this)) {

            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(10000, TimeUnit.SECONDS)
                    .connectTimeout(10000, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder().baseUrl(InterfaceApi.URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
            InterfaceApi api = retrofit.create(InterfaceApi.class);

            Call<ErrorMessage> errorMessageCall = api.placeUserOrder(orderEntry);

            final Dialog progressDialog = new Dialog(OrderReviewActivity.this);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setCancelable(false);
            progressDialog.setContentView(R.layout.loading_progress_layout);
            progressDialog.show();

            errorMessageCall.enqueue(new Callback<ErrorMessage>() {
                @Override
                public void onResponse(Call<ErrorMessage> call, retrofit2.Response<ErrorMessage> response) {
                    try {
                        if (response.body() != null) {
                            ErrorMessage data = response.body();
                            if (data.getError()) {
                                progressDialog.dismiss();
                                Log.e("ON RESPONSE : ", "ERROR : " + data.getMessage());
                                Toast.makeText(OrderReviewActivity.this, "Sorry, Unable To Place Order", Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(OrderReviewActivity.this, "Success", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(OrderReviewActivity.this, HomeActivity.class);
                                intent.putExtra("FcmTag", 2);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }

                        } else {
                            progressDialog.dismiss();
                            Log.e("ON RESPONSE : ", "NO DATA");
                            Toast.makeText(OrderReviewActivity.this, "Sorry, Unable to Place Order", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Log.e("Exception : ", "" + e.getMessage());
                        Toast.makeText(OrderReviewActivity.this, "Sorry, Unable to Place Order", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ErrorMessage> call, Throwable t) {
                    progressDialog.dismiss();
                    Log.e("ON FAILURE : ", "ERROR : " + t.getMessage());
                    Toast.makeText(OrderReviewActivity.this, "Sorry, Unable to Place Order", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(OrderReviewActivity.this, R.style.AlertDialogTheme);
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

    public void getTableList() {
        if (CheckNetwork.isInternetAvailable(OrderReviewActivity.this)) {

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

            final Dialog progressDialog = new Dialog(OrderReviewActivity.this);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setCancelable(false);
            progressDialog.setContentView(R.layout.loading_progress_layout);
            progressDialog.show();

            tableDataCall.enqueue(new Callback<TableData>() {
                @Override
                public void onResponse(Call<TableData> call, retrofit2.Response<TableData> response) {
                    try {
                        if (response.body() != null) {
                            TableData data = response.body();
                            if (data.getErrorMessage().getError()) {
                                progressDialog.dismiss();
                                Log.e("ON RESPONSE : ", "ERROR : " + data.getErrorMessage().getMessage());
                                //Toast.makeText(OrderReviewActivity.this, "Sorry, Unable To Place Order", Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();

                                tableArrayList.clear();
                                for (int i = 0; i < data.getTable().size(); i++) {
                                    tableArrayList.add(data.getTable().get(i));
                                }


                                //Toast.makeText(OrderReviewActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            progressDialog.dismiss();
                            Log.e("ON RESPONSE : ", "NO DATA");
                            // Toast.makeText(OrderReviewActivity.this, "Sorry, Unable to Place Order", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Log.e("Exception : ", "" + e.getMessage());
                        //Toast.makeText(OrderReviewActivity.this, "Sorry, Unable to Place Order", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<TableData> call, Throwable t) {
                    progressDialog.dismiss();
                    Log.e("ON FAILURE : ", "ERROR : " + t.getMessage());
                    //Toast.makeText(OrderReviewActivity.this, "Sorry, Unable to Place Order", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(OrderReviewActivity.this, R.style.AlertDialogTheme);
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

    public void getUserData(int userId) {
        if (CheckNetwork.isInternetAvailable(this)) {

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

//            final Dialog progressDialog = new Dialog(this);
//            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            progressDialog.setCancelable(false);
//            progressDialog.setContentView(R.layout.loading_progress_layout);
//            progressDialog.show();

            userDataCall.enqueue(new Callback<UserMasterData>() {
                @Override
                public void onResponse(Call<UserMasterData> call, Response<UserMasterData> response) {
                    try {
                        if (response.body() != null) {
                            UserMasterData data = response.body();
                            if (data.getErrorMessage().getError()) {
                                // progressDialog.dismiss();
                                Log.e("ON RESPONSE : ", " ERROR : " + data.getErrorMessage().getMessage());
                                // Toast.makeText(getContext(), "Unable To Fetch Data", Toast.LENGTH_SHORT).show();
                            } else {
                                userArray.clear();
                                for (int i = 0; i < data.getUser().size(); i++) {
                                    userArray.add(i, data.getUser().get(i));
                                }
                                Log.e("RESPONSE : ", " DATA : " + userArray);


                                // progressDialog.dismiss();
                            }
                        } else {
                            // progressDialog.dismiss();
                            //Toast.makeText(getContext(), "Unable To Fetch Data", Toast.LENGTH_SHORT).show();
                            Log.e("RESPONSE : ", " NO DATA");
                        }
                    } catch (Exception e) {
                        //  progressDialog.dismiss();
                        Log.e("Exception : ", "" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<UserMasterData> call, Throwable t) {
                    //Toast.makeText(getContext(), "unable To Fetch Data", Toast.LENGTH_SHORT).show();
                    // progressDialog.dismiss();
                    Log.e("ON FAILURE : ", " ERROR : " + t.getMessage());
                }
            });


        } else {
            Log.e("Connection - ", "Failed");
        }
    }

    public void showDialog(final ArrayList<User> userList) {
        dialog = new Dialog(this, R.style.AlertDialogTheme);
        LayoutInflater li = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.custom_user_selection_layout, null, false);
        dialog.setContentView(v);
        dialog.setCancelable(true);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        EditText edSearch = v.findViewById(R.id.edUserDialog_Search);
        ListView lvUsers = v.findViewById(R.id.lvUserDialog);
        Button btnDefault = v.findViewById(R.id.btnDefault);

        userDialogAdpt = new UserDialogAdapter(this, userList);
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

        btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("UserList","------------------------------"+userList);
                int pos=userList.size()-1;
                if (pos<0){
                    pos=0;
                }
                tvUserId.setText("" + userList.get(pos).getUserId());
                btnUser.setText("" + userList.get(pos).getFirstname());
                Log.e("Default User","------------------------------"+userList.get(pos));
                dialog.dismiss();
            }
        });

        dialog.show();
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
            ViewHolder holder = null;

            if (v == null) {
                v = inflater.inflate(R.layout.user_dialog_item_layout, null);
                holder = new ViewHolder();
                holder.tvId = v.findViewById(R.id.tvUserDialogItem_Id);
                holder.tvName = v.findViewById(R.id.tvUserDialogItem_Name);
                holder.tvMobile = v.findViewById(R.id.tvUserDialogItem_Mobile);
                holder.llUser = v.findViewById(R.id.llUserDialogItem);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }

            holder.tvId.setText("" + displayedValues.get(position).getUserId());
            holder.tvName.setText("" + displayedValues.get(position).getFirstname());
            holder.tvMobile.setText("" + displayedValues.get(position).getMobile());

            holder.llUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvUserId.setText("" + displayedValues.get(position).getUserId());
                    btnUser.setText("" + displayedValues.get(position).getFirstname());
                    dialog.dismiss();
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

    public void getItemData() {
        if (CheckNetwork.isInternetAvailable(OrderReviewActivity.this)) {

            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder().baseUrl(InterfaceApi.URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();

            InterfaceApi api = retrofit.create(InterfaceApi.class);
            Call<ItemData> itemDataCall = api.getAllItem();


            final Dialog progressDialog = new Dialog(OrderReviewActivity.this);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setCancelable(false);
            progressDialog.setContentView(R.layout.loading_progress_layout);
            progressDialog.show();


            itemDataCall.enqueue(new Callback<ItemData>() {
                @Override
                public void onResponse(Call<ItemData> call, Response<ItemData> response) {
                    try {
                        if (response.body() != null) {
                            ItemData data = response.body();
                            if (data.getErrorMessage().getError()) {
                                progressDialog.dismiss();
                                Log.e("ON RESPONSE : ", " ERROR : " + data.getErrorMessage().getMessage());
                            } else {
                                itemArray.clear();
                                for (int i = 0; i < data.getItem().size(); i++) {
                                    itemArray.add(i, data.getItem().get(i));
                                }
                                Log.e("RESPONSE : ", " DATA : " + itemArray);
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
                public void onFailure(Call<ItemData> call, Throwable t) {
                    progressDialog.dismiss();
                    Log.e("ON FAILURE : ", " ERROR : " + t.getMessage());
                }
            });


        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(OrderReviewActivity.this, R.style.AlertDialogTheme);
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
}
