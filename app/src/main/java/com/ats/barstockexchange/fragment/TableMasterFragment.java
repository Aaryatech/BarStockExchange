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
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.barstockexchange.R;
import com.ats.barstockexchange.bean.ErrorMessage;
import com.ats.barstockexchange.bean.Table;
import com.ats.barstockexchange.bean.TableData;
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

public class TableMasterFragment extends Fragment implements View.OnClickListener {

    private GridView gvList;
    MyAdapter adapter;
    private FloatingActionButton fab;

    private ArrayList<Table> tableArray = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_table_master, container, false);
        tvTitle.setText("Table");

        fab = view.findViewById(R.id.fabTableMaster);
        fab.setOnClickListener(this);
        gvList = view.findViewById(R.id.gvTableMaster_grid);

        getTableData();

     /*   ArrayList<String> noList = new ArrayList<>();
        noList.add("1");
        noList.add("2");
        noList.add("3");
        noList.add("4");
        noList.add("5");
        noList.add("6");
        noList.add("7");
        noList.add("8");
        noList.add("9");
        noList.add("10");

        adapter = new MyAdapter(getContext(), noList);
        gvList.setAdapter(adapter);*/

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fabTableMaster) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new AddTableFragment(), "TableMaster");
            ft.commit();
        }
    }


    public class MyAdapter extends BaseAdapter {

        private ArrayList<Table> originalValues;
        private ArrayList<Table> displayedValues;
        LayoutInflater inflater;

        public MyAdapter(Context context, ArrayList<Table> stringArrayList) {
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
            v = inflater.inflate(R.layout.custom_table_master_item, null);
            LinearLayout llBack = v.findViewById(R.id.llCustomTableMasterItem_back);
            TextView tvTableNo = v.findViewById(R.id.tvCustomTableMasterItem_no);
            TextView tvTableName = v.findViewById(R.id.tvCustomTableMasterItem_name);

            llBack.setBackgroundResource(R.mipmap.ic_beer_orange);
            tvTableName.setText("" + displayedValues.get(position).getTableName());
            tvTableNo.setText("No : " + displayedValues.get(position).getTableNo());

/*
            if (displayedValues.get(position).getIsActive() == 1) {
                llBack.setBackgroundResource(R.mipmap.ic_beer_orange);
                tvTableNo.setText("" + displayedValues.get(position).getTableName());
            } else {
                llBack.setBackgroundResource(R.mipmap.ic_beer_black);
                tvTableNo.setText("" + displayedValues.get(position).getTableName());
            }
*/

            llBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(getContext(), view);
                    popupMenu.getMenuInflater().inflate(R.menu.popup_table_master, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            if (menuItem.getItemId() == R.id.item_table_edit) {

                                Fragment adf = new EditTableFragment();
                                Bundle args = new Bundle();
                                args.putInt("TableId", displayedValues.get(position).getTableId());
                                args.putInt("TableNo", displayedValues.get(position).getTableNo());
                                args.putString("TableName", displayedValues.get(position).getTableName());
                                args.putInt("TableActive", displayedValues.get(position).getIsActive());
                                adf.setArguments(args);
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, adf, "TableMaster").commit();

                            } else if (menuItem.getItemId() == R.id.item_table_delete) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
                                builder.setTitle("Confirm Action");
                                builder.setMessage("Do you really want to delete Table?");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteTable(displayedValues.get(position).getTableId());
                                        dialog.dismiss();
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

    }

    public void getTableData() {
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
                                tableArray.clear();
                                for (int i = 0; i < data.getTable().size(); i++) {
                                    tableArray.add(i, data.getTable().get(i));
                                }
                                Log.e("RESPONSE : ", " DATA : " + tableArray);
                                //setAdapterData();
                                adapter = new MyAdapter(getContext(), tableArray);
                                gvList.setAdapter(adapter);
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


    public void deleteTable(int tableId) {

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

            Call<ErrorMessage> errorMessageCall = api.deleteTable(tableId);

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
                                builder.setMessage("Table deleted successfully.");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        tableArray.clear();
                                        getTableData();
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }

                        } else {
                            progressDialogDelete.dismiss();
                            Toast.makeText(getContext(), "Unable to delete!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(), "Unable to delete!", Toast.LENGTH_SHORT).show();
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
