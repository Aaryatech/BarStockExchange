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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ats.barstockexchange.R;
import com.ats.barstockexchange.bean.ErrorMessage;
import com.ats.barstockexchange.bean.Table;
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

public class EditTableFragment extends Fragment implements View.OnClickListener {

    private Button btnUpdate;
    private EditText edTableNo, edTableName;
    int userId, tId, tNo, tActive;
    String tName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_table, container, false);
        tvTitle.setText("Edit Table");

        try {
            SharedPreferences pref = getContext().getSharedPreferences(InterfaceApi.MY_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            userId = pref.getInt("UserId", 0);
            String userType = pref.getString("UserType", "");

            tId = getArguments().getInt("TableId");
            tNo = getArguments().getInt("TableNo");
            tName = getArguments().getString("TableName");
            tActive = getArguments().getInt("TableActive");


        } catch (Exception e) {
        }

        btnUpdate = view.findViewById(R.id.btnEditTable_Update);
        edTableNo = view.findViewById(R.id.edEditTable_No);
        edTableName = view.findViewById(R.id.edEditTable_Name);
        btnUpdate.setOnClickListener(this);

        edTableNo.setText("" + tNo);
        edTableName.setText("" + tName);


        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnEditTable_Update) {
            if (edTableNo.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Please Enter Table Number", Toast.LENGTH_SHORT).show();
                edTableNo.requestFocus();
            } else if (edTableName.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Please Enter Table Name", Toast.LENGTH_SHORT).show();
                edTableName.requestFocus();
            } else {
                int no = Integer.parseInt(edTableNo.getText().toString());
                String name = edTableName.getText().toString();
                editTable(tId, no, name, userId, tActive);
            }
        }

    }


    public void editTable(int tableId, int tableNo, String tableName, int userId, int active) {
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

            Table table = new Table(tableId, tableNo, tableName, 0, active, userId, "");
            Call<ErrorMessage> errorMessageCall = api.editTable(table);

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
                                ft.replace(R.id.content_frame, new TableMasterFragment(), "HomeFragment");
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
