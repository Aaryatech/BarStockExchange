package com.ats.barstockexchange.fragment;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ats.barstockexchange.R;
import com.ats.barstockexchange.bean.Admin;
import com.ats.barstockexchange.bean.ErrorMessage;
import com.ats.barstockexchange.util.CheckNetwork;
import com.ats.barstockexchange.util.InterfaceApi;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.ats.barstockexchange.activity.HomeActivity.tvTitle;

public class AddUserFragment extends Fragment implements View.OnClickListener {

    private Spinner spType;
    private EditText edUsername, edPassword, edConfirmPassword;
    private Button btnSave, btnReset;

    private ArrayList<String> userTypeArray = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_user, container, false);
        tvTitle.setText("Add User");

        spType = view.findViewById(R.id.spAddUser_Type);
        edUsername = view.findViewById(R.id.edAddUser_username);
        edPassword = view.findViewById(R.id.edAddUser_password);
        edConfirmPassword = view.findViewById(R.id.edAddUser_confirmPassword);

        btnSave = view.findViewById(R.id.btnAddUser_Save);
        btnReset = view.findViewById(R.id.btnAddUser_Reset);
        btnSave.setOnClickListener(this);
        btnReset.setOnClickListener(this);

        userTypeArray.add("Select User Type");
        userTypeArray.add("Captain");
        userTypeArray.add("Manager");

        ArrayAdapter<String> spAdapter = new ArrayAdapter<String>(getContext(), R.layout.custom_spinner_layout, userTypeArray);
        spType.setAdapter(spAdapter);




        return view;
    }


    public void addNewAdminUser(Admin admin) {
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

            Call<ErrorMessage> errorMessageCall = api.addAdminUser(admin);

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
                                Toast.makeText(getContext(), "Unable To Save", Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.content_frame, new UserMasterFragment(), "HomeFragment");
                                ft.commit();
                            }

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Unable To Save", Toast.LENGTH_SHORT).show();
                            Log.e("ON RESPONSE : ", "NO DATA");
                        }
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Unable To Save", Toast.LENGTH_SHORT).show();
                        Log.e("Exception : ", "" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<ErrorMessage> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Unable To Save", Toast.LENGTH_SHORT).show();
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

    public void resetData() {
        edUsername.setText("");
        edPassword.setText("");
        edConfirmPassword.setText("");
        spType.setSelection(0);
        edUsername.setFocusable(false);
        edPassword.setFocusable(false);
        edConfirmPassword.setFocusable(false);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnAddUser_Save) {
            if (spType.getSelectedItemPosition() == 0) {
                Toast.makeText(getContext(), "Select User Type", Toast.LENGTH_SHORT).show();
                spType.requestFocus();
            } else if (edUsername.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Enter Username", Toast.LENGTH_SHORT).show();
                edUsername.requestFocus();
            } else if (edPassword.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Enter Password", Toast.LENGTH_SHORT).show();
                edPassword.requestFocus();
            } else if (edConfirmPassword.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Enter Confirm Password", Toast.LENGTH_SHORT).show();
                edConfirmPassword.requestFocus();
            } else if (!edPassword.getText().toString().equals(edConfirmPassword.getText().toString())) {
                Toast.makeText(getContext(), "Password Not Matched", Toast.LENGTH_SHORT).show();
            } else {
                String username = edUsername.getText().toString();
                String password = edPassword.getText().toString();
                String type = spType.getSelectedItem().toString();

                Admin admin = new Admin(username, password, type, 0);

                addNewAdminUser(admin);
            }
        } else if (view.getId() == R.id.btnAddUser_Reset) {
            resetData();
        }
    }
}
