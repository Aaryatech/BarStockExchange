package com.ats.barstockexchange.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.barstockexchange.R;
import com.ats.barstockexchange.activity.HomeActivity;
import com.ats.barstockexchange.bean.ErrorMessage;
import com.ats.barstockexchange.bean.Item;
import com.ats.barstockexchange.fragment.EditItemFragment;
import com.ats.barstockexchange.fragment.ItemMasterFragment;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.ats.barstockexchange.activity.HomeActivity.edDataList;

/**
 * Created by MAXADMIN on 25/1/2018.
 */

public class ExpandableItemMasterAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<Item>> listDataChild;

    public ExpandableItemMasterAdapter(Context context, List<String> listDataHeader,
                                       HashMap<String, List<Item>> listChildData) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
        Log.e("ExpandableListAdapter", "-------------" + listDataHeader);
        Log.e("ExpandableListAdapter", "-------------" + listDataChild);
    }


    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return this.listDataChild.get(this.listDataHeader.get(i))
                .size();
    }

    @Override
    public Object getGroup(int i) {
        return this.listDataHeader.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return this.listDataChild.get(this.listDataHeader.get(i))
                .get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int i, boolean b, View view, ViewGroup viewGroup) {
        String headerTitle = (String) getGroup(i);
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.custom_expandable_item_master_header, null);
            ExpandableListView mExpandableListView = (ExpandableListView) viewGroup;
            mExpandableListView.expandGroup(i);
        }

        TextView lblListHeader = (TextView) view
                .findViewById(R.id.tvExpHeaderItem_title);
        lblListHeader.setText(headerTitle);

        TextView tvUpdate = (TextView) view
                .findViewById(R.id.tvExpHeaderItem_Update);

        tvUpdate.setText("" + getChildrenCount(i) + " items");

        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(context, "Clicked : " + i, Toast.LENGTH_SHORT).show();

                Log.e("CLICKED : -------", "" + getChildrenCount(i));
                Log.e("LIST : -----", "" + edDataList);

            }
        });


        return view;
    }

    @Override
    public View getChildView(int i, final int i1, boolean b, View view, ViewGroup viewGroup) {

        Log.e("i : ", "--------------" + i);
        Log.e("i1 : ", "--------------" + i1);

        Log.e("child : --------", "" + listDataChild);
        Log.e("getChild()--------", "" + getChild(i, i1));

        final Item item = (Item) getChild(i, i1);

        try {
            final String childText = (String) getChild(i, i1);

        } catch (Exception e) {
        }


        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.custom_item_master_layout, null);
        }

        TextView tvTitle = view.findViewById(R.id.tvItemMaster_Title);
        TextView tvDesc = view.findViewById(R.id.tvItemMaster_Desc);
        TextView tvStock = view.findViewById(R.id.tvItemMaster_Stock);
        ImageView ivImage = view.findViewById(R.id.ivItemMaster_Image);
        ImageView ivpopup = view.findViewById(R.id.ivItemMaster_popup);
        LinearLayout llItemDetails = view.findViewById(R.id.llItemMaster_View);

        tvTitle.setText("" + item.getItemName());
        tvDesc.setText("" + item.getItemDesc());
        tvStock.setText("" + item.getCurrentStock());

        try {
            Picasso.with(context)
                    .load(InterfaceApi.IMAGE_PATH + "" + item.getItemImage())
                    .placeholder(R.drawable.bottle_a)
                    .error(R.drawable.bottle_a)
                    .into(ivImage);
        } catch (Exception e) {
        }

        llItemDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemDetailsDialog(item.getItemName(), item.getItemDesc(), item.getCurrentStock(), item.getMinStock(), item.getMinRate(), item.getMaxRate(), item.getMrpGame(), item.getMrpSpecial(), item.getMrpRegular(), item.getSgst(), item.getCgst(), item.getItemImage());
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

                            HomeActivity activity = (HomeActivity) context;

                            Fragment adf = new EditItemFragment();
                            Bundle args = new Bundle();
                            args.putInt("ItemId", item.getItemId());
                            args.putInt("CatId", item.getCatId());
                            args.putString("ItemName", item.getItemName());
                            args.putString("ItemDesc", item.getItemDesc());
                            args.putString("ItemImage", item.getItemImage());
                            args.putInt("IsMixer", item.getIsMixerApplicable());
                            args.putInt("CurrentStock", item.getCurrentStock());
                            args.putInt("MinStock", item.getMinStock());
                            args.putFloat("OpenRate", item.getOpeningRate());
                            args.putFloat("GameRate", item.getMrpGame());
                            args.putFloat("RegRate", item.getMrpRegular());
                            args.putFloat("SpecialRate", item.getMrpSpecial());
                            args.putFloat("MinRate", item.getMinRate());
                            args.putFloat("MaxRate", item.getMaxRate());
                            args.putFloat("SGST", item.getSgst());
                            args.putFloat("CGST", item.getCgst());
                            adf.setArguments(args);
                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, adf, "ItemMaster").commit();
                        } else if (menuItem.getItemId() == R.id.item_delete) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
                            builder.setTitle("Confirm Action");
                            builder.setMessage("Do you really want to delete Item?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteItem(item.getItemId());
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

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }


    public void itemDetailsDialog(String title, String desc, int currStock, int minStock, float minRate, float maxRate, float gameRate, float spRate, float regRate, float sgst, float cgst, String image) {

        final Dialog dialog = new Dialog(context, android.R.style.Theme_Light_NoTitleBar);
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
            Picasso.with(context)
                    .load(InterfaceApi.IMAGE_PATH + "" + image)
                    .placeholder(R.drawable.bottle_a)
                    .error(R.drawable.bottle_a)
                    .into(ivImage);
        } catch (Exception e) {
        }

        dialog.show();
    }

    public void deleteItem(int itemId) {

        if (CheckNetwork.isInternetAvailable(context)) {

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

            final Dialog progressDialogDelete = new Dialog(context);
            progressDialogDelete.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialogDelete.setCancelable(false);
            progressDialogDelete.setContentView(R.layout.loading_progress_layout);
            progressDialogDelete.show();

            errorMessageCall.enqueue(new Callback<ErrorMessage>() {
                @Override
                public void onResponse(final Call<ErrorMessage> call, Response<ErrorMessage> response) {
                    try {
                        if (response.body() != null) {
                            ErrorMessage data = response.body();
                            if (data.getError()) {
                                progressDialogDelete.dismiss();
                                Log.e("ON RESPONSE : ", "ERROR : " + data.getMessage());

                            } else {
                                progressDialogDelete.dismiss();
                                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
                                builder.setTitle("Success");
                                builder.setCancelable(false);
                                builder.setMessage("Item Deleted Successfully.");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        HomeActivity activity = (HomeActivity) context;
                                        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                                        ft.replace(R.id.content_frame, new ItemMasterFragment(), "HomeFragment");
                                        ft.commit();
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }

                        } else {
                            progressDialogDelete.dismiss();
                            Toast.makeText(context, "Unable To delete!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(context, "Unable To Delete!", Toast.LENGTH_SHORT).show();
                    Log.e("ON FAILURE : ", "ERROR : " + t.getMessage());
                }
            });


        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
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
