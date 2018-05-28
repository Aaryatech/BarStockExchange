package com.ats.barstockexchange.fragment;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.barstockexchange.R;
import com.ats.barstockexchange.bean.ErrorMessage;
import com.ats.barstockexchange.bean.User;
import com.ats.barstockexchange.util.CheckNetwork;
import com.ats.barstockexchange.util.InterfaceApi;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.ats.barstockexchange.activity.HomeActivity.tvTitle;

public class AddCustomerFragment extends Fragment implements View.OnClickListener {

    private EditText edName, edMobile, edDob, edEmail;
    private TextView tvDob;
    private Button btnSave, btnReset;
    int userId;
    int yyyy, mm, dd;
    long dobMillis;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_customer, container, false);
        tvTitle.setText("Add Customer");

        try {
            SharedPreferences pref = getContext().getSharedPreferences(InterfaceApi.MY_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            userId = pref.getInt("UserId", 0);
            String userType = pref.getString("UserType", "");

        } catch (Exception e) {
        }

        edName = view.findViewById(R.id.edAddCustomer_Name);
        edMobile = view.findViewById(R.id.edAddCustomer_Mobile);
        edDob = view.findViewById(R.id.edAddCustomer_dob);
        edEmail = view.findViewById(R.id.edAddCustomer_Email);
        tvDob = view.findViewById(R.id.tvAddCustomer_dob);
        btnSave = view.findViewById(R.id.btnAddCustomer_Save);
        btnReset = view.findViewById(R.id.btnAddCustomer_Reset);
        btnSave.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        edDob.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnAddCustomer_Save) {

            try {

                Calendar todayCal = Calendar.getInstance();
                long millis = todayCal.getTimeInMillis();

                if (edName.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Please Enter Name", Toast.LENGTH_SHORT).show();
                    edName.requestFocus();
                } else if (edMobile.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Please Enter Mobile Number", Toast.LENGTH_SHORT).show();
                    edMobile.requestFocus();
                } else if (edMobile.getText().toString().length() != 10) {
                    Toast.makeText(getActivity(), "Please Enter 10 Digit Mobile Number", Toast.LENGTH_SHORT).show();
                    edMobile.requestFocus();
                } else if (edDob.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Please Select Date Of Birth", Toast.LENGTH_SHORT).show();
                    edMobile.requestFocus();
                } else if (dobMillis > millis) {
                    Toast.makeText(getActivity(), "Date Of Birth Should Not Exceed From Today's Date", Toast.LENGTH_SHORT).show();
                    edDob.requestFocus();
                } else {

                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(dobMillis);

                    final String email = edEmail.getText().toString();

                    if (!email.isEmpty()) {
                        if (isValidEmailAddress(email)) {
                            if (getAge(cal) < 18) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.WarningAlertDialogTheme);
                                LayoutInflater inflater = this.getLayoutInflater();
                                final View dialogView = inflater.inflate(R.layout.custom_warning_dialog, null);
                                builder.setView(dialogView);

                                builder.setCancelable(false);
                                builder.setPositiveButton("PROCEED", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        addUser(edName.getText().toString(), edMobile.getText().toString(), userId, tvDob.getText().toString(), email);
                                    }
                                });
                                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                        ft.replace(R.id.content_frame, new CustomerMasterFragment(), "HomeFragment");
                                        ft.commit();
                                        // finish();
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.show();
                            } else {
                                addUser(edName.getText().toString(), edMobile.getText().toString(), userId, tvDob.getText().toString(), email);
                            }

                        } else {
                            Toast.makeText(getActivity(), "Invalid Email Id", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (getAge(cal) < 18) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.WarningAlertDialogTheme);
                            LayoutInflater inflater = this.getLayoutInflater();
                            final View dialogView = inflater.inflate(R.layout.custom_warning_dialog, null);
                            builder.setView(dialogView);

                            builder.setCancelable(false);
                            builder.setPositiveButton("PROCEED", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    addUser(edName.getText().toString(), edMobile.getText().toString(), userId, tvDob.getText().toString(), email);
                                }
                            });
                            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                    ft.replace(R.id.content_frame, new CustomerMasterFragment(), "HomeFragment");
                                    ft.commit();
                                    // finish();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.show();
                        } else {
                            addUser(edName.getText().toString(), edMobile.getText().toString(), userId, tvDob.getText().toString(), email);
                        }

                    }


                }
            } catch (Exception e) {
                Log.e("Add Customer : ", "----- Exception : " + e.getMessage());
                e.printStackTrace();
            }
        } else if (v.getId() == R.id.btnAddCustomer_Reset) {
            edName.setText("");
            edMobile.setText("");
            edName.requestFocus();
        } else if (v.getId() == R.id.edAddCustomer_dob) {
            int yr, mn, dy;
            if (dobMillis > 0) {
                Calendar purchaseCal = Calendar.getInstance();
                purchaseCal.setTimeInMillis(dobMillis);
                yr = purchaseCal.get(Calendar.YEAR);
                mn = purchaseCal.get(Calendar.MONTH);
                dy = purchaseCal.get(Calendar.DAY_OF_MONTH);
            } else {
                Calendar purchaseCal = Calendar.getInstance();
                yr = 1975;
                mn = 0;
                dy = 1;
            }

            DatePickerDialog.OnDateSetListener toDtListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    yyyy = year;
                    mm = month + 1;
                    dd = dayOfMonth;
                    edDob.setText(dd + "-" + mm + "-" + yyyy);
                    tvDob.setText(yyyy + "-" + mm + "-" + dd);

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(yyyy, mm - 1, dd);
                    calendar.set(Calendar.MILLISECOND, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.HOUR, 0);
                    dobMillis = calendar.getTimeInMillis();
                }
            };

            DatePickerDialog dialog = new DatePickerDialog(getContext(), R.style.DialogTheme, toDtListener, yr, mn, dy);
            dialog.show();
        }
    }


    public void addUser(String name, String mobile, int userId, String dob, String email) {

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

            User user = new User(name, mobile, name, 0, 0, userId, "token",  email,dob);
            Call<ErrorMessage> errorMessageCall = api.addCustomer(user);

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
                                if (data.getMessage().equalsIgnoreCase("Failed")) {
                                    Toast.makeText(getContext(), "Unable To Save", Toast.LENGTH_SHORT).show();
                                } else if (data.getMessage().equalsIgnoreCase("user already exist")) {
                                    Toast.makeText(getContext(), "User Already Exist", Toast.LENGTH_SHORT).show();
                                }


                            } else {
                                progressDialog.dismiss();

                                Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.content_frame, new CustomerMasterFragment(), "HomeFragment");
                                ft.commit();
                            }

                        } else {
                            progressDialog.dismiss();
                            Log.e("ON RESPONSE : ", "NO DATA");
                            Toast.makeText(getContext(), "Unable To Save", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Log.e("Exception : ", "" + e.getMessage());
                        Toast.makeText(getContext(), "Unable To Save", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ErrorMessage> call, Throwable t) {
                    progressDialog.dismiss();
                    Log.e("ON FAILURE : ", "ERROR : " + t.getMessage());
                    Toast.makeText(getContext(), "Unable To Save", Toast.LENGTH_SHORT).show();
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


    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
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
