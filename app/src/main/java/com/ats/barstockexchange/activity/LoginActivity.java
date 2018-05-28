package com.ats.barstockexchange.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.barstockexchange.R;
import com.ats.barstockexchange.bean.LoginData;
import com.ats.barstockexchange.util.CheckNetwork;
import com.ats.barstockexchange.util.InterfaceApi;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edUsername, edPassword;
    private TextView tvLogin, tvCreateAcc, tvForgetPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edUsername = findViewById(R.id.edUsername);
        edPassword = findViewById(R.id.edPassword);
        tvLogin = findViewById(R.id.tvLoginButton);

        tvLogin.setOnClickListener(this);

        edUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    edUsername.setBackgroundResource(R.drawable.edittext_border);
                } else {
                    edUsername.setBackgroundResource(R.drawable.edittext_border_layout);
                }
            }
        });
        edPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    edPassword.setBackgroundResource(R.drawable.edittext_border);
                } else {
                    edPassword.setBackgroundResource(R.drawable.edittext_border_layout);
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tvLoginButton) {
            // progressDialog();

            String username = edUsername.getText().toString().trim();
            String password = edPassword.getText().toString().trim();


            if (username.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please Enter Username", Toast.LENGTH_SHORT).show();
                edUsername.requestFocus();
            } else if (password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                edPassword.requestFocus();
            } else {

                if (CheckNetwork.isInternetAvailable(getApplicationContext())) {

                      OkHttpClient client = new OkHttpClient.Builder()
                            .addInterceptor(new Interceptor() {
                                @Override
                                public okhttp3.Response intercept(Chain chain) throws IOException {
                                    Request original = chain.request();
                                    Request request = original.newBuilder()
                                            .header("Accept", "application/json")
                                            .method(original.method(), original.body())
                                            .build();

                                    okhttp3.Response response = chain.proceed(request);

                                    return response;
                                }
                            })
                            .readTimeout(10000, TimeUnit.SECONDS)
                            .connectTimeout(10000, TimeUnit.SECONDS)
                            .writeTimeout(10000, TimeUnit.SECONDS)
                            .build();

                    Retrofit retrofit = new Retrofit.Builder().baseUrl(InterfaceApi.URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(client)
                            .build();
                    InterfaceApi api = retrofit.create(InterfaceApi.class);
                    Call<LoginData> loginCall = api.doLogin(username, password);

                    final Dialog dialog = new Dialog(LoginActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.loading_progress_layout);
                    dialog.show();

                    loginCall.enqueue(new Callback<LoginData>() {
                        @Override
                        public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                            if (response.body() != null) {
                                LoginData data = response.body();

                                if (data.getErrorMessage().getError()) {
                                    Log.e("USERDATA : ", "ERROR : " + data.getErrorMessage().getMessage());
                                    dialog.dismiss();

                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.AlertDialogTheme);
                                    builder.setTitle("Alert");
                                    builder.setCancelable(false);
                                    builder.setMessage("" + data.getErrorMessage().getMessage());
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();

                                } else {
                                    Log.e("USERDATA : ", "DATA : " + data);

                                    // if (data.getType().equalsIgnoreCase("superadmin")) {
                                    SharedPreferences pref = getApplicationContext().getSharedPreferences(InterfaceApi.MY_PREF, MODE_PRIVATE);
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putInt("UserId", data.getId());
                                    editor.putString("UserType", data.getType());
                                    editor.putString("UserName", data.getName());
                                    editor.apply();

                                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                    dialog.dismiss();
                                    finish();

                                    //  } else {
//                                        dialog.dismiss();
//
//                                        Log.e("USER :  ", "NOT ADMIN");
//                                        SharedPreferences pref = getApplicationContext().getSharedPreferences(InterfaceApi.MY_PREF, MODE_PRIVATE);
//                                        SharedPreferences.Editor editor = pref.edit();
//                                        editor.putInt("UserId", data.getId());
//                                        editor.putString("UserType", data.getType());
//                                        editor.apply();
                                    //  }
                                }
                            } else {
                                dialog.dismiss();
                                Log.e("LoginActivity->", " onResponse : No Data");
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginData> call, Throwable t) {
                            Log.e("ON FAILURE : ", "ERROR : " + t.getMessage());
                            dialog.dismiss();

                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.AlertDialogTheme);
                            builder.setTitle("Caution");
                            builder.setCancelable(false);
                            builder.setMessage("Unable To Login");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.AlertDialogTheme);
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
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void progressDialog() {

        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.loading_progress_layout);
        dialog.show();
    }

}
