package com.ats.barstockexchange.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.barstockexchange.R;
import com.ats.barstockexchange.bean.ErrorMessage;
import com.ats.barstockexchange.bean.User;
import com.ats.barstockexchange.bean.UserMasterData;
import com.ats.barstockexchange.util.CheckNetwork;
import com.ats.barstockexchange.util.InterfaceApi;
import com.ats.barstockexchange.util.ShowPopupMenuIcon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.ats.barstockexchange.activity.HomeActivity.tvTitle;

public class CustomerMasterFragment extends Fragment implements View.OnClickListener {

    private EditText edSearch;
    private ListView lvCustomer;
    private FloatingActionButton fab;

    private ArrayList<User> userArray = new ArrayList<>();
    int userId;

    CustomerDataAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_master, container, false);
        tvTitle.setText("Customers");

        try {
            SharedPreferences pref = getContext().getSharedPreferences(InterfaceApi.MY_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            userId = pref.getInt("UserId", 0);
            String userType = pref.getString("UserType", "");

        } catch (Exception e) {
        }

        fab = view.findViewById(R.id.fabCustomerMaster);
        edSearch = view.findViewById(R.id.edCustomerMaster_Search);
        lvCustomer = view.findViewById(R.id.lvCustomerMaster);
        fab.setOnClickListener(this);

        getUserData(userId);

        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fabCustomerMaster) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new AddCustomerFragment(), "CustomerMaster");
            ft.commit();
        }
    }

//    public void getUserData(int userId) {
//        if (CheckNetwork.isInternetAvailable(getContext())) {
//
//            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                    .readTimeout(60, TimeUnit.SECONDS)
//                    .connectTimeout(60, TimeUnit.SECONDS)
//                    .build();
//
//            Retrofit retrofit = new Retrofit.Builder().baseUrl(InterfaceApi.URL)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .client(okHttpClient)
//                    .build();
//            InterfaceApi api = retrofit.create(InterfaceApi.class);
//            final Call<UserMasterData> userDataCall = api.getAllUserByEnterBy(userId);
//
//            final Dialog progressDialog = new Dialog(getContext());
//            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            progressDialog.setCancelable(false);
//            progressDialog.setContentView(R.layout.loading_progress_layout);
//            progressDialog.show();
//
//            userDataCall.enqueue(new Callback<UserMasterData>() {
//                @Override
//                public void onResponse(Call<UserMasterData> call, Response<UserMasterData> response) {
//                    try {
//                        if (response.body() != null) {
//                            UserMasterData data = response.body();
//                            if (data.getErrorMessage().getError()) {
//                                progressDialog.dismiss();
//                                Log.e("ON RESPONSE : ", " ERROR : " + data.getErrorMessage().getMessage());
//                                Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
//                            } else {
//                                userArray.clear();
//                                for (int i = 0; i < data.getUser().size(); i++) {
//                                    userArray.add(i, data.getUser().get(i));
//                                }
//                                Log.e("RESPONSE : ", " DATA : " + userArray);
//                                adapter = new CustomerDataAdapter(getContext(), userArray);
//                                lvCustomer.setAdapter(adapter);
//
//                                progressDialog.dismiss();
//                            }
//                        } else {
//                            progressDialog.dismiss();
//                            Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
//                            Log.e("RESPONSE : ", " NO DATA");
//                        }
//                    } catch (Exception e) {
//                        progressDialog.dismiss();
//                        Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
//                        Log.e("Exception : ", "" + e.getMessage());
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<UserMasterData> call, Throwable t) {
//                    Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
//                    progressDialog.dismiss();
//                    Log.e("ON FAILURE : ", " ERROR : " + t.getMessage());
//                    t.printStackTrace();
//                }
//            });
//
//
//        } else {
//            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
//            builder.setTitle("Check Connectivity");
//            builder.setCancelable(false);
//            builder.setMessage("Please Connect To Internet");
//            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });
//            AlertDialog dialog = builder.create();
//            dialog.show();
//        }
//    }

    public void getUserData(int userId) {
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
                                Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
                            } else {
                                userArray.clear();
                                for (int i = 0; i < data.getUser().size(); i++) {
                                    userArray.add(i, data.getUser().get(i));
                                }
                                Log.e("RESPONSE : ", " DATA : " + userArray);
                                adapter = new CustomerDataAdapter(getContext(), userArray);
                                lvCustomer.setAdapter(adapter);

                                progressDialog.dismiss();
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
                            Log.e("RESPONSE : ", " NO DATA");
                        }
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
                        Log.e("Exception : ", "" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<UserMasterData> call, Throwable t) {
                    Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    Log.e("ON FAILURE : ", " ERROR : " + t.getMessage());
                    t.printStackTrace();
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

    public class CustomerDataAdapter extends BaseAdapter implements Filterable {

        Context context;
        private ArrayList<User> originalValues;
        private ArrayList<User> displayedValues;
        LayoutInflater inflater;

        public CustomerDataAdapter(Context context, ArrayList<User> userArrayList) {
            this.context = context;
            this.originalValues = userArrayList;
            this.displayedValues = userArrayList;
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
            v = inflater.inflate(R.layout.custom_customer_master_layout, null);

            TextView tvId = v.findViewById(R.id.tvCustomerMaster_Id);
            TextView tvUserName = v.findViewById(R.id.tvCustomerMaster_Username);
            TextView tvPassword = v.findViewById(R.id.tvCustomerMaster_Password);
            TextView tvMobile = v.findViewById(R.id.tvCustomerMaster_Mobile);
            TextView tvDOB = v.findViewById(R.id.tvCustomerMaster_Dob);
            TextView tvEmail = v.findViewById(R.id.tvCustomerMaster_Email);
            ImageView ivpopup = v.findViewById(R.id.ivCustomerMaster_Popup);

            tvId.setText(" " + displayedValues.get(position).getUserId() + " ");
            tvUserName.setText("" + displayedValues.get(position).getFirstname());
            tvPassword.setText("" + displayedValues.get(position).getPassword());
            tvMobile.setText("" + displayedValues.get(position).getMobile());

            if (displayedValues.get(position).getDob() == null || displayedValues.get(position).getDob().isEmpty()) {
                tvDOB.setVisibility(View.GONE);
            } else {


                try {
                    String date = displayedValues.get(position).getDob();
                    int yr = Integer.parseInt(date.substring(0, 4));
                    int mn = Integer.parseInt(date.substring(6, 7));
                    int dy = Integer.parseInt(date.substring(9, 10));

                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.YEAR, yr);
                    cal.set(Calendar.MONTH, mn);
                    cal.set(Calendar.DAY_OF_MONTH, dy);

                    tvDOB.setText("" + displayedValues.get(position).getDob() + "  ( " + getAge(cal) + " years)");


                } catch (Exception e) {
                    tvDOB.setText("" + displayedValues.get(position).getDob());
                }

            }

            if (displayedValues.get(position).getEmail() == null || displayedValues.get(position).getEmail().isEmpty()) {
                tvEmail.setVisibility(View.GONE);
            } else {
                tvEmail.setText("" + displayedValues.get(position).getEmail());
            }

            ivpopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(context, view);
                    popupMenu.getMenuInflater().inflate(R.menu.popup_customer_master, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            if (menuItem.getItemId() == R.id.item_customer_edit) {

/*                                Fragment adf = new EditUserFragment();
                                Bundle args = new Bundle();
                                args.putInt("Id", displayedValues.get(position).getAdminId());
                                args.putString("Username", "" + displayedValues.get(position).getUsername());
                                args.putString("Password", displayedValues.get(position).getPassword());
                                args.putString("Type", displayedValues.get(position).getType());
                                adf.setArguments(args);
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, adf, "UserMaster").commit();*/

                            } else if (menuItem.getItemId() == R.id.item_customer_delete) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
                                builder.setTitle("Confirm Action");
                                builder.setMessage("Do You Really Want To Delete User?");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteUser(displayedValues.get(position).getUserId());
                                    }
                                });
                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                            return true;
                        }
                    });
                    ShowPopupMenuIcon.setForceShowIcon(popupMenu);
                    popupMenu.show();
                }
            });

            return v;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    FilterResults results = new FilterResults();
                    ArrayList<User> filteredArrayList = new ArrayList<User>();

                    if (originalValues == null) {
                        originalValues = new ArrayList<User>(displayedValues);
                    }

                    if (charSequence == null || charSequence.length() == 0) {
                        results.count = originalValues.size();
                        results.values = originalValues;
                    } else {
                        charSequence = charSequence.toString().toLowerCase();
                        for (int i = 0; i < originalValues.size(); i++) {
                            String username = originalValues.get(i).getFirstname();
                            String id = "" + originalValues.get(i).getUserId();
                            String mobile = originalValues.get(i).getMobile();
                            if (username.toLowerCase().startsWith(charSequence.toString()) || username.toLowerCase().contains(charSequence.toString()) || id.toLowerCase().startsWith(charSequence.toString()) || id.toLowerCase().contains(charSequence.toString()) || mobile.toLowerCase().startsWith(charSequence.toString())) {
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
    }

    public void deleteUser(final int id) {

        if (CheckNetwork.isInternetAvailable(getContext())) {

            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder().baseUrl(InterfaceApi.URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();

            final InterfaceApi api = retrofit.create(InterfaceApi.class);

            Call<ErrorMessage> errorMessageCall = api.deleteUserByCaptain(id);

            final Dialog progressDialogDelete = new Dialog(getContext());
            progressDialogDelete.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialogDelete.setCancelable(false);
            progressDialogDelete.setContentView(R.layout.loading_progress_layout);
            progressDialogDelete.show();

            errorMessageCall.enqueue(new Callback<ErrorMessage>() {
                @Override
                public void onResponse(Call<ErrorMessage> call, Response<ErrorMessage> response) {
                    try {
                        if (response.body() != null) {
                            ErrorMessage data = response.body();
                            if (data.getError()) {
                                progressDialogDelete.dismiss();
                                Log.e("ON RESPONSE : ", "ERROR : " + data.getMessage());
                                Toast.makeText(getContext(), "Unable To Delete", Toast.LENGTH_SHORT).show();

                            } else {
                                progressDialogDelete.dismiss();
                                Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                                userArray.clear();
                                getUserData(userId);
                                adapter.notifyDataSetChanged();

                            }

                        } else {
                            progressDialogDelete.dismiss();
                            Toast.makeText(getContext(), "Unable To Delete!", Toast.LENGTH_SHORT).show();
                            Log.e("ON RESPONSE : ", "NO DATA");
                        }
                    } catch (Exception e) {
                        progressDialogDelete.dismiss();
                        Toast.makeText(getContext(), "Unable To Delete", Toast.LENGTH_SHORT).show();
                        Log.e("Exception : ", "" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<ErrorMessage> call, Throwable t) {
                    progressDialogDelete.dismiss();
                    Toast.makeText(getContext(), "Unable To Delete!", Toast.LENGTH_SHORT).show();
                    Log.e("ON FAILURE : ", "ERROR : " + t.getMessage());
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

    public static int getAge(Calendar dob) throws Exception {
        Calendar today = Calendar.getInstance();
        int curYear = today.get(Calendar.YEAR);
        int dobYear = dob.get(Calendar.YEAR);
        int age = curYear - dobYear;

        // if dob is month or day is behind today's month or day
        // reduce age by 1
        int curMonth = today.get(Calendar.MONTH);
        int dobMonth = dob.get(Calendar.MONTH);
        if (dobMonth > curMonth) { // this year can't be counted!
            age--;
        } else if (dobMonth == curMonth) { // same month? check for day
            int curDay = today.get(Calendar.DAY_OF_MONTH);
            int dobDay = dob.get(Calendar.DAY_OF_MONTH);
            if (dobDay > curDay) { // this year can't be counted!
                age--;
            }
        }

        return age;
    }

}
