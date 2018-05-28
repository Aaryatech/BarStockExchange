package com.ats.barstockexchange.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.barstockexchange.R;
import com.ats.barstockexchange.bean.Admin;
import com.ats.barstockexchange.bean.ErrorMessage;
import com.ats.barstockexchange.bean.UserData;
import com.ats.barstockexchange.util.CheckNetwork;
import com.ats.barstockexchange.util.InterfaceApi;
import com.ats.barstockexchange.util.ShowPopupMenuIcon;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.ats.barstockexchange.activity.HomeActivity.tvTitle;

public class UserMasterFragment extends Fragment {

    private FloatingActionButton fab;
    private ArrayList<Admin> userArray = new ArrayList<>();
    private UserDataAdapter adapter;
    private ListView lvUserMaster;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_master, container, false);
        tvTitle.setText("User");

        lvUserMaster = view.findViewById(R.id.lvUserMaster);

        fab = view.findViewById(R.id.fabUserMaster);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, new AddUserFragment(), "UserMaster");
                ft.commit();
            }
        });

        /*Admin admin = new Admin(1, "manager", "manager123", "manager", 0);
        Admin admin2 = new Admin(2, "captain", "captain123", "captain", 0);
        userArray.add(admin);
        userArray.add(admin2);
        adapter = new UserDataAdapter(getContext(), userArray);
        lvUserMaster.setAdapter(adapter);*/

        getUserData();
        return view;
    }

    public void edittextListener(){

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
            Call<UserData> userDataCall = api.getAllUser();


            final Dialog progressDialog = new Dialog(getContext());
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setCancelable(false);
            progressDialog.setContentView(R.layout.loading_progress_layout);
            progressDialog.show();


            userDataCall.enqueue(new Callback<UserData>() {
                @Override
                public void onResponse(Call<UserData> call, Response<UserData> response) {
                    try {
                        if (response.body() != null) {
                            UserData data = response.body();
                            if (data.getErrorMessage().getError()) {
                                progressDialog.dismiss();
                                Log.e("ON RESPONSE : ", " ERROR : " + data.getErrorMessage().getMessage());
                                Toast.makeText(getContext(), "unable to fetch data", Toast.LENGTH_SHORT).show();
                            } else {
                                userArray.clear();
                                for (int i = 0; i < data.getAdmin().size(); i++) {
                                    userArray.add(i, data.getAdmin().get(i));
                                }
                                Log.e("RESPONSE : ", " DATA : " + userArray);
                                //setAdapterData();
                                adapter = new UserDataAdapter(getContext(), userArray);
                                lvUserMaster.setAdapter(adapter);
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
                public void onFailure(Call<UserData> call, Throwable t) {
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

    public class UserDataAdapter extends BaseAdapter implements Filterable {

        Context context;
        private ArrayList<Admin> originalValues;
        private ArrayList<Admin> displayedValues;
        LayoutInflater inflater;

        public UserDataAdapter(Context context, ArrayList<Admin> userArrayList) {
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
            v = inflater.inflate(R.layout.custom_user_master_layout, null);

            TextView tvType = v.findViewById(R.id.tvUserMaster_Type);
            TextView tvUserName = v.findViewById(R.id.tvUserMaster_Username);
            TextView tvPassword = v.findViewById(R.id.tvUserMaster_Password);
            ImageView ivpopup = v.findViewById(R.id.ivUserMaster_Popup);


            tvType.setText(" " + displayedValues.get(position).getType() + " ");
            tvUserName.setText("" + displayedValues.get(position).getUsername());
            tvPassword.setText("" + displayedValues.get(position).getPassword());

            ivpopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(context, view);
                    popupMenu.getMenuInflater().inflate(R.menu.popup_user_master, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            if (menuItem.getItemId() == R.id.item_user_edit) {

                                Fragment adf = new EditUserFragment();
                                Bundle args = new Bundle();
                                args.putInt("Id", displayedValues.get(position).getAdminId());
                                args.putString("Username", "" + displayedValues.get(position).getUsername());
                                args.putString("Password", displayedValues.get(position).getPassword());
                                args.putString("Type", displayedValues.get(position).getType());
                                adf.setArguments(args);
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, adf, "UserMaster").commit();
                            } else if (menuItem.getItemId() == R.id.item_user_delete) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
                                builder.setTitle("Confirm Action");
                                builder.setMessage("Do you really want to delete user?");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteAdminUser(displayedValues.get(position).getAdminId());
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
                                // Toast.makeText(getContext(), "deleted", Toast.LENGTH_SHORT).show();
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
                    ArrayList<Admin> filteredArrayList = new ArrayList<Admin>();

                    if (originalValues == null) {
                        originalValues = new ArrayList<Admin>(displayedValues);
                    }

                    if (charSequence == null || charSequence.length() == 0) {
                        results.count = originalValues.size();
                        results.values = originalValues;
                    } else {
                        charSequence = charSequence.toString().toLowerCase();
                        for (int i = 0; i < originalValues.size(); i++) {
                            String username = originalValues.get(i).getUsername();
                            String pass = originalValues.get(i).getPassword();
                            String type = originalValues.get(i).getType();
                            if (username.toLowerCase().startsWith(charSequence.toString()) || username.toLowerCase().contains(charSequence.toString()) || pass.toLowerCase().startsWith(charSequence.toString()) || pass.toLowerCase().contains(charSequence.toString()) || type.toLowerCase().startsWith(charSequence.toString())) {
                                filteredArrayList.add(new Admin(originalValues.get(i).getAdminId(), originalValues.get(i).getUsername(), originalValues.get(i).getPassword(), originalValues.get(i).getType(), originalValues.get(i).getDelStatus()));
                            }
                        }
                        results.count = filteredArrayList.size();
                        results.values = filteredArrayList;
                    }

                    return results;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    displayedValues = (ArrayList<Admin>) filterResults.values;
                    notifyDataSetChanged();
                }
            };

            return filter;
        }
    }

    public void deleteAdminUser(int id) {

        if (CheckNetwork.isInternetAvailable(getContext())) {

            Retrofit retrofit = new Retrofit.Builder().baseUrl(InterfaceApi.URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            final InterfaceApi api = retrofit.create(InterfaceApi.class);

            Call<ErrorMessage> errorMessageCall = api.deleteAdminUser(id);

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

                            } else {
                                progressDialogDelete.dismiss();
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
                                builder.setTitle("Success");
                                builder.setCancelable(false);
                                builder.setMessage("Account deleted successfully.");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        userArray.clear();
                                        getUserData();
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }

                        } else {
                            progressDialogDelete.dismiss();
                            Toast.makeText(getContext(), "Unable to delete account!", Toast.LENGTH_SHORT).show();
                            Log.e("ON RESPONSE : ", "NO DATA");
                        }
                    } catch (Exception e) {
                        progressDialogDelete.dismiss();
                        Log.e("Exception : ", "" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<ErrorMessage> call, Throwable t) {
                    progressDialogDelete.dismiss();
                    Toast.makeText(getContext(), "Unable to delete account!", Toast.LENGTH_SHORT).show();
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
}
