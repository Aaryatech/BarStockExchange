package com.ats.barstockexchange.fragment;


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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ats.barstockexchange.R;
import com.ats.barstockexchange.bean.ErrorMessage;
import com.ats.barstockexchange.bean.Settings;
import com.ats.barstockexchange.bean.SettingsData;
import com.ats.barstockexchange.util.CheckNetwork;
import com.ats.barstockexchange.util.InterfaceApi;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.ats.barstockexchange.activity.HomeActivity.tvTitle;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private RadioButton rbGame, rbSpecial, rbRegular;
    private EditText edLatitude, edLongitude, edRadius;
    private TextView tvSave, tvId;
    private ToggleButton toggleBtn;
    private LinearLayout llForm;

    int userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        tvTitle.setText("Settings");

        try {
            SharedPreferences pref = getContext().getSharedPreferences(InterfaceApi.MY_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            userId = pref.getInt("UserId", 0);
            String userType = pref.getString("UserType", "");

        } catch (Exception e) {
        }

        rbGame = view.findViewById(R.id.rbSettings_Game);
        rbRegular = view.findViewById(R.id.rbSettings_Regular);
        rbSpecial = view.findViewById(R.id.rbSettings_Special);

        edLatitude = view.findViewById(R.id.edSettings_Latitude);
        edLongitude = view.findViewById(R.id.edSettings_Longitude);
        edRadius = view.findViewById(R.id.edSettings_Radius);
        tvId = view.findViewById(R.id.tvSettings_Id);
        tvSave = view.findViewById(R.id.tvSettings_Save);
        toggleBtn = view.findViewById(R.id.gameToggleBtn);
        llForm = view.findViewById(R.id.llSettingForm);

        tvSave.setOnClickListener(this);

        getSettingsData();


        toggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    llForm.setVisibility(View.GONE);
                    rbGame.setChecked(true);
                } else {
                    llForm.setVisibility(View.VISIBLE);
                    rbGame.setVisibility(View.GONE);
                    rbRegular.setChecked(true);
                }
            }
        });

        return view;
    }


    public void editSetting(int id, String mode, double latitude, double longitude, int radius, int userId) {
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

            Settings settings = new Settings(id, mode, latitude, longitude, radius, userId, "");
            Call<ErrorMessage> errorMessageCall = api.editSetting(settings);

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
                                builder.setMessage("Settings changed successfully.");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                        ft.replace(R.id.content_frame, new AdminHomeFragment(), "HomeFragment");
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
    public void onClick(View v) {
        if (v.getId() == R.id.tvSettings_Save) {
            String appMode = "";
            if (rbSpecial.isChecked()) {
                appMode = "special";
            } else if (rbRegular.isChecked()) {
                appMode = "regular";
            } else if (rbGame.isChecked()) {
                appMode = "game";
            }

            if (tvId.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "sorry no settings available", Toast.LENGTH_SHORT).show();
                tvId.requestFocus();
            } else if (appMode.equalsIgnoreCase("")) {
                Toast.makeText(getContext(), "please select mode", Toast.LENGTH_SHORT).show();
                rbGame.requestFocus();
            } else if (edLatitude.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "please enter latitude value", Toast.LENGTH_SHORT).show();
                edLatitude.requestFocus();
            } else if (edLongitude.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "please enter longitude value", Toast.LENGTH_SHORT).show();
                edLongitude.requestFocus();
            } else if (edRadius.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "please enter radius value", Toast.LENGTH_SHORT).show();
                edRadius.requestFocus();
            } else {
                double lat = Double.parseDouble(edLatitude.getText().toString());
                double longi = Double.parseDouble(edLongitude.getText().toString());
                int rad = Integer.parseInt(edRadius.getText().toString());
                int sId = Integer.parseInt(tvId.getText().toString());
                editSetting(sId, appMode, lat, longi, rad, userId);
            }
        }
    }


    public void getSettingsData() {
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
            Call<SettingsData> settingsDataCall = api.getSettings();


            final Dialog progressDialog = new Dialog(getContext());
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setCancelable(false);
            progressDialog.setContentView(R.layout.loading_progress_layout);
            progressDialog.show();


            settingsDataCall.enqueue(new Callback<SettingsData>() {
                @Override
                public void onResponse(Call<SettingsData> call, Response<SettingsData> response) {
                    try {
                        if (response.body() != null) {
                            SettingsData data = response.body();
                            if (data.getErrorMessage().getError()) {
                                progressDialog.dismiss();
                                Log.e("ON RESPONSE : ", " ERROR : " + data.getErrorMessage().getMessage());
                                Toast.makeText(getContext(), "unable to fetch data", Toast.LENGTH_SHORT).show();
                            } else {

                                tvId.setText("" + data.getSettings().getSettingId());
                                edLatitude.setText("" + data.getSettings().getLatitude());
                                edLongitude.setText("" + data.getSettings().getLongitude());
                                edRadius.setText("" + data.getSettings().getRadius());

                                String mode = data.getSettings().getAppMode();
                                if (mode.equalsIgnoreCase("game")) {
                                    rbGame.setChecked(true);
                                    toggleBtn.setChecked(true);
                                } else if (mode.equalsIgnoreCase("regular")) {
                                    rbRegular.setChecked(true);
                                    rbGame.setVisibility(View.GONE);
                                } else if (mode.equalsIgnoreCase("special")) {
                                    rbSpecial.setChecked(true);
                                    rbGame.setVisibility(View.GONE);
                                }


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
                public void onFailure(Call<SettingsData> call, Throwable t) {
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
}
