package com.ats.barstockexchange.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ExpandableListView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.barstockexchange.R;
import com.ats.barstockexchange.bean.AllCategoryAndItemsData;
import com.ats.barstockexchange.bean.CategoryItemList;
import com.ats.barstockexchange.bean.ErrorMessage;
import com.ats.barstockexchange.bean.Item;
import com.ats.barstockexchange.bean.ItemData;
import com.ats.barstockexchange.util.CheckNetwork;
import com.ats.barstockexchange.util.ExpandableItemMasterAdapter;
import com.ats.barstockexchange.util.ExpandableListAdapter;
import com.ats.barstockexchange.util.InterfaceApi;
import com.ats.barstockexchange.util.ShowPopupMenuIcon;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.ats.barstockexchange.activity.HomeActivity.tvTitle;

public class ItemMasterFragment extends Fragment implements View.OnClickListener {

    private FloatingActionButton fab;
    private EditText edSearch;
    private ListView lvItem;
    private ArrayList<Item> itemArray = new ArrayList<>();
    ItemDataAdapter adapter;
    private ArrayList<CategoryItemList> categoryItemLists = new ArrayList<>();

    //-------------------------------------------------
    ExpandableListView expListView;

    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    List<String> listDataHeader1;
    HashMap<String, List<Item>> listDataChild1;

    ExpandableItemMasterAdapter listAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_master, container, false);
        tvTitle.setText("Item");

        fab = view.findViewById(R.id.fabItemMaster);
        lvItem = view.findViewById(R.id.lvItemMaster);
        edSearch = view.findViewById(R.id.edItemMaster_Search);
        fab.setOnClickListener(this);


        expListView = view.findViewById(R.id.expItemList);


        // getItemData();

        getAllCatAndItems();


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
    public void onClick(View view) {
        if (view.getId() == R.id.fabItemMaster) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new AddItemFragment(), "ItemMaster");
            ft.commit();
        }
    }

    public void itemDetailsDialog(String title, String desc, int currStock, int minStock, float minRate, float maxRate, float gameRate, float spRate, float regRate, float sgst, float cgst, String image) {

        final Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Light_NoTitleBar);
        dialog.setContentView(R.layout.item_detail_layout);
        dialog.setTitle("Item Details");
        TextView tvTitle = dialog.findViewById(R.id.tvItemDetails_Title);
        TextView tvDesc = dialog.findViewById(R.id.tvItemDetails_Desc);
        TextView tvCurrStock = dialog.findViewById(R.id.tvItemDetails_CurrStock);
        TextView tvMinStock = dialog.findViewById(R.id.tvItemDetails_MinStock);
        TextView tvMinRate = dialog.findViewById(R.id.tvItemDetails_MinRate);
        TextView tvMaxRate = dialog.findViewById(R.id.tvItemDetails_MaxRate);
        TextView tvGameRate = dialog.findViewById(R.id.tvItemDetails_GameRate);
        TextView tvSpRate = dialog.findViewById(R.id.tvItemDetails_SpRate);
        TextView tvRegRate = dialog.findViewById(R.id.tvItemDetails_RegRate);
        TextView tvSGST = dialog.findViewById(R.id.tvItemDetails_SGST);
        TextView tvCGST = dialog.findViewById(R.id.tvItemDetails_CGST);
        ImageView ivImage = dialog.findViewById(R.id.ivItemDetails_viewImage);

        tvTitle.setText("" + title);
        tvDesc.setText("" + desc);
        tvCurrStock.setText("" + currStock);
        tvMinStock.setText("" + minStock);
        tvMinRate.setText("" + minRate);
        tvMaxRate.setText("" + maxRate);
        tvGameRate.setText("" + gameRate);
        tvSpRate.setText("" + spRate);
        tvRegRate.setText("" + regRate);
        tvSGST.setText("" + sgst);
        tvCGST.setText("" + cgst);

        try {
            Picasso.with(getContext())
                    .load(InterfaceApi.IMAGE_PATH + "" + image)
                    .placeholder(R.drawable.bottle_a)
                    .error(R.drawable.bottle_a)
                    .into(ivImage);
        } catch (Exception e) {
        }

        dialog.show();
    }

    public void getItemData() {
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
            Call<ItemData> itemDataCall = api.getAllItem();


            final Dialog progressDialog = new Dialog(getContext());
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
                                Toast.makeText(getContext(), "unable to fetch data", Toast.LENGTH_SHORT).show();
                            } else {
                                itemArray.clear();
                                for (int i = 0; i < data.getItem().size(); i++) {
                                    itemArray.add(i, data.getItem().get(i));
                                }
                                Log.e("RESPONSE : ", " DATA : " + itemArray);
                                //setAdapterData();
                                adapter = new ItemDataAdapter(getContext(), itemArray);
                                lvItem.setAdapter(adapter);
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
                public void onFailure(Call<ItemData> call, Throwable t) {
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


    class ItemDataAdapter extends BaseAdapter implements Filterable {

        Context context;
        private ArrayList<Item> originalValues;
        private ArrayList<Item> displayedValues;
        LayoutInflater inflater;

        public ItemDataAdapter(Context context, ArrayList<Item> itemArrayList) {
            this.context = context;
            this.originalValues = itemArrayList;
            this.displayedValues = itemArrayList;
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
            v = inflater.inflate(R.layout.custom_item_master_layout, null);

            TextView tvTitle = v.findViewById(R.id.tvItemMaster_Title);
            TextView tvDesc = v.findViewById(R.id.tvItemMaster_Desc);
            TextView tvStock = v.findViewById(R.id.tvItemMaster_Stock);
            ImageView ivImage = v.findViewById(R.id.ivItemMaster_Image);
            ImageView ivpopup = v.findViewById(R.id.ivItemMaster_popup);
            LinearLayout llItemDetails = v.findViewById(R.id.llItemMaster_View);


            tvTitle.setText("" + displayedValues.get(position).getItemName());
            tvDesc.setText("" + displayedValues.get(position).getItemDesc());
            tvStock.setText("" + displayedValues.get(position).getCurrentStock());

            try {
                Picasso.with(getContext())
                        .load(InterfaceApi.IMAGE_PATH + "" + displayedValues.get(position).getItemImage())
                        .placeholder(R.drawable.bottle_a)
                        .error(R.drawable.bottle_a)
                        .into(ivImage);
            } catch (Exception e) {
            }

            llItemDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemDetailsDialog(displayedValues.get(position).getItemName(), displayedValues.get(position).getItemDesc(), displayedValues.get(position).getCurrentStock(), displayedValues.get(position).getMinStock(), displayedValues.get(position).getMinRate(), displayedValues.get(position).getMaxRate(), displayedValues.get(position).getMrpGame(), displayedValues.get(position).getMrpSpecial(), displayedValues.get(position).getMrpRegular(), displayedValues.get(position).getSgst(), displayedValues.get(position).getCgst(), displayedValues.get(position).getItemImage());
                }
            });

            ivpopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(context, view);
                    popupMenu.getMenuInflater().inflate(R.menu.popup_item_master, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            if (menuItem.getItemId() == R.id.item_edit) {

                                Fragment adf = new EditItemFragment();
                                Bundle args = new Bundle();
                                args.putInt("ItemId", displayedValues.get(position).getItemId());
                                args.putInt("CatId", displayedValues.get(position).getCatId());
                                args.putString("ItemName", displayedValues.get(position).getItemName());
                                args.putString("ItemDesc", displayedValues.get(position).getItemDesc());
                                args.putString("ItemImage", displayedValues.get(position).getItemImage());
                                args.putInt("IsMixer", displayedValues.get(position).getIsMixerApplicable());
                                args.putInt("CurrentStock", displayedValues.get(position).getCurrentStock());
                                args.putInt("MinStock", displayedValues.get(position).getMinStock());
                                args.putFloat("OpenRate", displayedValues.get(position).getOpeningRate());
                                args.putFloat("GameRate", displayedValues.get(position).getMrpGame());
                                args.putFloat("RegRate", displayedValues.get(position).getMrpRegular());
                                args.putFloat("SpecialRate", displayedValues.get(position).getMrpSpecial());
                                args.putFloat("MinRate", displayedValues.get(position).getMinRate());
                                args.putFloat("MaxRate", displayedValues.get(position).getMaxRate());
                                args.putFloat("SGST", displayedValues.get(position).getSgst());
                                args.putFloat("CGST", displayedValues.get(position).getCgst());
                                adf.setArguments(args);
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, adf, "ItemMaster").commit();
                            } else if (menuItem.getItemId() == R.id.item_delete) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
                                builder.setTitle("Confirm Action");
                                builder.setMessage("Do you really want to delete Item?");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteItem(displayedValues.get(position).getItemId());
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

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    FilterResults results = new FilterResults();
                    ArrayList<Item> filteredArrayList = new ArrayList<Item>();

                    if (originalValues == null) {
                        originalValues = new ArrayList<Item>(displayedValues);
                    }

                    if (charSequence == null || charSequence.length() == 0) {
                        results.count = originalValues.size();
                        results.values = originalValues;
                    } else {
                        charSequence = charSequence.toString().toLowerCase();
                        for (int i = 0; i < originalValues.size(); i++) {
                            String name = originalValues.get(i).getItemName();
                            String desc = originalValues.get(i).getItemDesc();
                            if (name.toLowerCase().startsWith(charSequence.toString()) || name.toLowerCase().contains(charSequence.toString()) || desc.toLowerCase().startsWith(charSequence.toString()) || desc.toLowerCase().contains(charSequence.toString())) {
                                filteredArrayList.add(new Item(originalValues.get(i).getItemId(), originalValues.get(i).getItemName(), originalValues.get(i).getItemDesc(), originalValues.get(i).getItemImage(), originalValues.get(i).getMrpGame(), originalValues.get(i).getMrpRegular(), originalValues.get(i).getMrpSpecial(), originalValues.get(i).getOpeningRate(), originalValues.get(i).getMaxRate(), originalValues.get(i).getMinRate(), originalValues.get(i).getCurrentStock(), originalValues.get(i).getCatId(), originalValues.get(i).getSgst(), originalValues.get(i).getCgst(), originalValues.get(i).getIsMixerApplicable(), originalValues.get(i).getUserId(), originalValues.get(i).getUpdatedDate(), originalValues.get(i).getDelStatus(), originalValues.get(i).getMinStock()));
                            }
                        }
                        results.count = filteredArrayList.size();
                        results.values = filteredArrayList;
                    }

                    return results;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    displayedValues = (ArrayList<Item>) filterResults.values;
                    notifyDataSetChanged();
                }
            };

            return filter;
        }
    }


    public void deleteItem(int itemId) {

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

            Call<ErrorMessage> errorMessageCall = api.deleteItem(itemId);

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
                                builder.setMessage("Category deleted successfully.");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        itemArray.clear();
                                        getItemData();
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

    private void dispListData(ArrayList<CategoryItemList> arrayList) {
        Log.e("DISPLAY_LIST_DATA", "-------------" + arrayList);

        listDataHeader1 = new ArrayList<String>();
        listDataChild1 = new HashMap<String, List<Item>>();

        for (int i = 0; i < arrayList.size(); i++) {
            listDataHeader1.add(arrayList.get(i).getCategoryName());
            listDataChild1.put(arrayList.get(i).getCategoryName(), arrayList.get(i).getItem());
        }
    }

    public void getAllCatAndItems() {
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
            Call<AllCategoryAndItemsData> itemDataCall = api.getAllCatAndItemsForUpdate();


            final Dialog progressDialog = new Dialog(getContext());
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setCancelable(false);
            progressDialog.setContentView(R.layout.loading_progress_layout);
            progressDialog.show();


            itemDataCall.enqueue(new Callback<AllCategoryAndItemsData>() {
                @Override
                public void onResponse(Call<AllCategoryAndItemsData> call, Response<AllCategoryAndItemsData> response) {
                    try {
                        if (response.body() != null) {
                            AllCategoryAndItemsData data = response.body();
                            if (data.getErrorMessage().getError()) {
                                progressDialog.dismiss();
                                Log.e("ON RESPONSE : ", " ERROR : " + data.getErrorMessage().getMessage());
                                Toast.makeText(getContext(), "unable to fetch data", Toast.LENGTH_SHORT).show();
                            } else {
                                categoryItemLists.clear();

                                Log.e("CAT SIZE : ", "-----------" + data.getCategoryItemList().size());

                                for (int i = 0; i < data.getCategoryItemList().size(); i++) {
                                    categoryItemLists.add(data.getCategoryItemList().get(i));
                                }

                                Log.e("RESPONSE : ", " DATA : " + categoryItemLists);

                                    dispListData(categoryItemLists);
                                listAdapter = new ExpandableItemMasterAdapter(getContext(), listDataHeader1, listDataChild1);
                                expListView.setAdapter(listAdapter);
                                for (int i = 0; i < listAdapter.getGroupCount(); i++) {
                                    expListView.expandGroup(i);
                                }

                                //setAdapterData();
//                                adapter = new ItemMasterFragment.ItemDataAdapter(getContext(), itemArray);
//                                lvItem.setAdapter(adapter);
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
                public void onFailure(Call<AllCategoryAndItemsData> call, Throwable t) {
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
