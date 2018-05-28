package com.ats.barstockexchange.fragment;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.barstockexchange.R;
import com.ats.barstockexchange.bean.Bill;
import com.ats.barstockexchange.bean.BillData;
import com.ats.barstockexchange.bean.Category;
import com.ats.barstockexchange.bean.CategoryData;
import com.ats.barstockexchange.bean.CustomBillHeader;
import com.ats.barstockexchange.bean.CustomBillItems;
import com.ats.barstockexchange.bean.ReportBean;
import com.ats.barstockexchange.bean.ReportDisplayBean;
import com.ats.barstockexchange.util.CheckNetwork;
import com.ats.barstockexchange.util.InterfaceApi;
import com.ats.barstockexchange.util.PrintHelper;
import com.ats.barstockexchange.util.PrintReceiptType;
import com.ats.barstockexchange.util.ShowPopupMenuIcon;
import com.squareup.picasso.Picasso;

import java.io.File;
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

import static android.content.Context.MODE_PRIVATE;
import static com.ats.barstockexchange.activity.HomeActivity.tvTitle;

public class ReportsFragment extends Fragment implements View.OnClickListener {

    private ImageView ivSearch, ivPrint;
    private EditText edDate;
    private ListView lvReport;
    private TextView tvTotal;

    private ArrayList<Bill> billArrayList = new ArrayList<>();
    private ArrayList<Category> categoryArrayList = new ArrayList<>();
    private ArrayList<ReportDisplayBean> reportDisplayBeanArrayList = new ArrayList<>();

    int yyyy, mm, dd;
    long millis;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);
        tvTitle.setText("Reports");

        ivSearch = view.findViewById(R.id.ivReports_Search);
        ivPrint = view.findViewById(R.id.ivReports_Print);
        edDate = view.findViewById(R.id.edReports_Date);
        lvReport = view.findViewById(R.id.lvReports);
        tvTotal = view.findViewById(R.id.tvReport_Total);
        ivSearch.setOnClickListener(this);
        ivPrint.setOnClickListener(this);
        edDate.setOnClickListener(this);

        Calendar cal = Calendar.getInstance();
        yyyy = cal.get(Calendar.YEAR);
        mm = cal.get(Calendar.MONTH)+1;
        dd = cal.get(Calendar.DAY_OF_MONTH);
        edDate.setText(dd + "-" + (mm ) + "-" + yyyy);
        millis=cal.getTimeInMillis();

        getCategoryData(edDate.getText().toString());

        return view;
    }


    public void getCategoryData(final String date) {
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
            Call<CategoryData> categoryDataCall = api.getAllCategory();


            final Dialog progressDialog = new Dialog(getContext());
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setCancelable(false);
            progressDialog.setContentView(R.layout.loading_progress_layout);
            progressDialog.show();


            categoryDataCall.enqueue(new Callback<CategoryData>() {
                @Override
                public void onResponse(Call<CategoryData> call, Response<CategoryData> response) {
                    try {
                        if (response.body() != null) {
                            CategoryData data = response.body();
                            if (data.getErrorMessage().getError()) {
                                progressDialog.dismiss();
                                Log.e("ON RESPONSE : ", " ERROR : " + data.getErrorMessage().getMessage());
                                Toast.makeText(getContext(), "unable to fetch data", Toast.LENGTH_SHORT).show();
                            } else {
                                categoryArrayList.clear();
                                for (int i = 0; i < data.getCategory().size(); i++) {
                                    categoryArrayList.add(i, data.getCategory().get(i));
                                    Log.e("CATEGORY ID : ", "---" + categoryArrayList.get(i).getCatId() + "\n");
                                    Log.e("CATEGORY NAME : ", "---" + categoryArrayList.get(i).getCatName() + "\n");
                                }
                                Log.e("RESPONSE : ", " DATA : " + categoryArrayList);

                                getReport(date);

                                progressDialog.dismiss();

                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "unable to fetch data", Toast.LENGTH_SHORT).show();
                            Log.e("RESPONSE : ", " NO DATA");
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "unable to fetch data", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        Log.e("Exception : ", "" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<CategoryData> call, Throwable t) {
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

    public void getReport(String date) {
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
            Call<ReportBean> reportBeanCall = api.getReport(date);


            final Dialog progressDialog = new Dialog(getContext());
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setCancelable(false);
            progressDialog.setContentView(R.layout.loading_progress_layout);
            progressDialog.show();


            reportBeanCall.enqueue(new Callback<ReportBean>() {
                @Override
                public void onResponse(Call<ReportBean> call, Response<ReportBean> response) {
                    try {
                        if (response.body() != null) {
                            ReportBean data = response.body();
                            if (data.getErrorMessage().getError()) {
                                progressDialog.dismiss();
                                Log.e("ON RESPONSE : ", " ERROR : " + data.getErrorMessage().getMessage());
                                Toast.makeText(getContext(), "No Report Found", Toast.LENGTH_SHORT).show();

                                ivPrint.setVisibility(View.GONE);

                            } else {
                                progressDialog.dismiss();

                                billArrayList.clear();
                                reportDisplayBeanArrayList.clear();
                                ArrayList<Integer> catIdArray = new ArrayList<>();

                                double total = 0;

                                if (data.getBill().size() > 0) {

                                    ivPrint.setVisibility(View.VISIBLE);

                                    for (int i = 0; i < data.getBill().size(); i++) {
                                        catIdArray.add(data.getBill().get(i).getCatId());
                                        billArrayList.add(data.getBill().get(i));
                                        total = total + data.getBill().get(i).getTotal();

                                    }

                                    Set<Integer> uniqueCatId = new HashSet<Integer>(catIdArray);
                                    catIdArray.clear();
                                    catIdArray.addAll(uniqueCatId);

                                    Log.e("CATEGORY : ", "----------" + catIdArray);


                                    for (int i = 0; i < catIdArray.size(); i++) {

                                        ReportDisplayBean reportDisplayBean = new ReportDisplayBean();
                                        reportDisplayBean.setCatId(catIdArray.get(i));

                                        String catName = "";
                                        for (int j = 0; j < categoryArrayList.size(); j++) {
                                            if (catIdArray.get(i) == categoryArrayList.get(j).getCatId()) {
                                                catName = categoryArrayList.get(j).getCatName();
                                                reportDisplayBean.setCatName(catName);
                                                break;
                                            }
                                        }


                                        ArrayList<Bill> billArray = new ArrayList<>();
                                        for (int k = 0; k < billArrayList.size(); k++) {
                                            if (catIdArray.get(i) == billArrayList.get(k).getCatId()) {
                                                billArray.add(billArrayList.get(k));
                                            }
                                        }

                                        reportDisplayBean.setBillList(billArray);
                                        reportDisplayBeanArrayList.add(reportDisplayBean);
                                    }

                                    tvTotal.setText("" + String.format("%.1f",total));

                                    Log.e("REPORT", " : " + reportDisplayBeanArrayList);

                                    ReportAdapter reportAdapter = new ReportAdapter(getContext(), reportDisplayBeanArrayList);
                                    lvReport.setAdapter(reportAdapter);


                                } else {
                                    Toast.makeText(getContext(), "No Report Found", Toast.LENGTH_SHORT).show();
                                    ivPrint.setVisibility(View.GONE);
                                }

                            }
                        } else {
                            ivPrint.setVisibility(View.GONE);
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "No Report Found", Toast.LENGTH_SHORT).show();
                            Log.e("RESPONSE : ", " NO DATA");
                        }
                    } catch (Exception e) {
                        ivPrint.setVisibility(View.GONE);
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "No Report Found", Toast.LENGTH_SHORT).show();
                        Log.e("Exception : ", "" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<ReportBean> call, Throwable t) {
                    ivPrint.setVisibility(View.GONE);
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "No Report Found", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.edReports_Date) {

            int yr,mn,dt;

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(millis);
            yr = cal.get(Calendar.YEAR);
            mn = cal.get(Calendar.MONTH);
            dt = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(getActivity(), R.style.AlertDialogTheme1, fromDateListener, yr, mn, dt);
            dialog.show();
        } else if (view.getId() == R.id.ivReports_Search) {
            if (edDate.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Please Select Date", Toast.LENGTH_SHORT).show();
                edDate.requestFocus();
            } else {
                getCategoryData(edDate.getText().toString());
            }
        } else if (view.getId() == R.id.ivReports_Print) {
            SharedPreferences pref = getActivity().getSharedPreferences(InterfaceApi.MY_PREF, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            String ip = pref.getString("IP", "");

            try {
                PrintHelper printHelper = new PrintHelper(getActivity(), ip, 9, reportDisplayBeanArrayList, edDate.getText().toString(), PrintReceiptType.REPORT);
                printHelper.runPrintReceiptSequence();
            } catch (Exception e) {
            }
        }
    }

    private DatePickerDialog.OnDateSetListener fromDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            yyyy = year;
            mm = month + 1;
            dd = dayOfMonth;
            edDate.setText(dd + "-" + mm + "-" + yyyy);

            Calendar calendar=Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH,dd);
            calendar.set(Calendar.MONTH,(mm-1));
            calendar.set(Calendar.YEAR,yyyy);
            millis=calendar.getTimeInMillis();
            Log.e("CAL : ","----------------"+calendar.getTime());
        }
    };


    public class ReportAdapter extends BaseAdapter {

        ArrayList<ReportDisplayBean> displayedValues;
        Context context;


        public ReportAdapter(Context context, ArrayList<ReportDisplayBean> catArray) {
            this.context = context;
            this.displayedValues = catArray;
        }

        @Override
        public int getCount() {
            return displayedValues.size();
        }

        @Override
        public Object getItem(int position) {
            return displayedValues.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        public class Holder {
            TextView tvName;
            ListView lvBillItems;
        }

        @Override
        public View getView(final int position, final View convertView, ViewGroup parent) {
            final Holder holder;
            View rowView = convertView;

            if (rowView == null) {
                holder = new Holder();
                LayoutInflater inflater = LayoutInflater.from(context);
                rowView = inflater.inflate(R.layout.custom_report_header_item, null);

                holder.tvName = rowView.findViewById(R.id.tvReportHeader_Name);
                holder.lvBillItems = rowView.findViewById(R.id.lvReportHeader);

                rowView.setTag(holder);

            } else {
                holder = (Holder) rowView.getTag();
            }


            holder.tvName.setText("" + displayedValues.get(position).getCatName());

            ReportItemsAdapter itemAdapter = new ReportItemsAdapter(context, (ArrayList<Bill>) displayedValues.get(position).getBillList());
            holder.lvBillItems.setAdapter(itemAdapter);

            setListViewHeightBasedOnChildren(holder.lvBillItems);

            return rowView;
        }
    }

    public class ReportItemsAdapter extends BaseAdapter {

        ArrayList<Bill> displayedValues;
        Context context;


        public ReportItemsAdapter(Context context, ArrayList<Bill> catArray) {
            this.context = context;
            this.displayedValues = catArray;
        }

        @Override
        public int getCount() {
            return displayedValues.size();
        }

        @Override
        public Object getItem(int position) {
            return displayedValues.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        public class Holder {
            TextView tvName, tvQty, tvRate;
        }

        @Override
        public View getView(final int position, final View convertView, ViewGroup parent) {
            final Holder holder;
            View rowView = convertView;

            if (rowView == null) {
                holder = new Holder();
                LayoutInflater inflater = LayoutInflater.from(context);
                rowView = inflater.inflate(R.layout.custom_report_item_layout, null);

                holder.tvName = rowView.findViewById(R.id.tvReportItem_Name);
                holder.tvQty = rowView.findViewById(R.id.tvReportItem_Qty);
                holder.tvRate = rowView.findViewById(R.id.tvReportItem_Rate);

                rowView.setTag(holder);

            } else {
                holder = (Holder) rowView.getTag();
            }


            holder.tvName.setText("" + displayedValues.get(position).getItemName());
            holder.tvQty.setText("" + displayedValues.get(position).getQuantity());
            holder.tvRate.setText("" + displayedValues.get(position).getTotal());


            return rowView;
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
