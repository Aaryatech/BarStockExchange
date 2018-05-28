package com.ats.barstockexchange.activity;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.barstockexchange.R;
import com.ats.barstockexchange.bean.ErrorMessage;
import com.ats.barstockexchange.bean.ItemData;
import com.ats.barstockexchange.bean.TempRate;
import com.ats.barstockexchange.fragment.AboutDeveloperFragment;
import com.ats.barstockexchange.fragment.AboutUsFragment;
import com.ats.barstockexchange.fragment.AddCategoryFragment;
import com.ats.barstockexchange.fragment.AddCustomerFragment;
import com.ats.barstockexchange.fragment.AddItemFragment;
import com.ats.barstockexchange.fragment.AddNewsFragment;
import com.ats.barstockexchange.fragment.AddTableFragment;
import com.ats.barstockexchange.fragment.AddUserFragment;
import com.ats.barstockexchange.fragment.AdminHomeFragment;
import com.ats.barstockexchange.fragment.BillDisplayFragment;
import com.ats.barstockexchange.fragment.CategoryMasterFragment;
import com.ats.barstockexchange.fragment.CustomerMasterFragment;
import com.ats.barstockexchange.fragment.EditCategoryFragment;
import com.ats.barstockexchange.fragment.EditItemFragment;
import com.ats.barstockexchange.fragment.EditNewsFragment;
import com.ats.barstockexchange.fragment.EditTableFragment;
import com.ats.barstockexchange.fragment.EditUserFragment;
import com.ats.barstockexchange.fragment.GenerateBillFragment;
import com.ats.barstockexchange.fragment.ItemMasterFragment;
import com.ats.barstockexchange.fragment.NewsMasterFragment;
import com.ats.barstockexchange.fragment.OrderApproveFragment;
import com.ats.barstockexchange.fragment.PrinterIPFragment;
import com.ats.barstockexchange.fragment.RejectedOrderReportFragment;
import com.ats.barstockexchange.fragment.ReportsFragment;
import com.ats.barstockexchange.fragment.SettingsFragment;
import com.ats.barstockexchange.fragment.TableMasterFragment;
import com.ats.barstockexchange.fragment.TablesForBillFragment;
import com.ats.barstockexchange.fragment.TermsConditionFragment;
import com.ats.barstockexchange.fragment.UpdateRateFragment;
import com.ats.barstockexchange.fragment.UpdateStockFragment;
import com.ats.barstockexchange.fragment.UserMasterFragment;
import com.ats.barstockexchange.fragment.ViewOrdersFragment;
import com.ats.barstockexchange.fragment.WaiterFragment;
import com.ats.barstockexchange.util.CheckNetwork;
import com.ats.barstockexchange.util.InterfaceApi;
import com.ats.barstockexchange.util.PermissionsUtil;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity
        implements View.OnClickListener {

    public static TextView tvTitle;
    private TextView tvNavHead;
    public static String tag = "HomeActivity";
    int userId, fcmType, kot;
    String userType, userName;

    public static HashMap<Integer, String> edDataList = new HashMap<>();
    public static HashMap<Integer, TempRate> edRateDataList = new HashMap<>();
    public static HashMap<Integer, String> minRateList = new HashMap<>();
    public static HashMap<Integer, String> maxRateList = new HashMap<>();


    private TextView tvAdminMenu_Home, tvAdminMenu_AddUser, tvAdminMenu_AddCategory, tvAdminMenu_AddItem, tvAdminMenu_AddTable, tvAdminMenu_Settings, tvAdminMenu_UpdateStock, tvAdminMenu_UpdateRate, tvAdminMenu_AddNews, tvAdminMenu_Signout, tvAdminMenu_Terms, tvAdminMenu_AboutDev, tvAdminMenu_AboutUs, tvAdminMenu_RateApp, tvAdminMenu_AddCustomer, tvAdminMenu_ViewOrders, tvAdminMenu_BillGenerate, tvAdminMenu_ViewBill, tvAdminMenu_CallWaiter, tvAdminMenu_PrintIP, tvAdminMenu_BillDayEnd, tvAdminMenu_Reports, tvAdminMenu_RejectedOrderReports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvTitle = findViewById(R.id.tvTitle);

        try {
            SharedPreferences pref = HomeActivity.this.getSharedPreferences(InterfaceApi.MY_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            userId = pref.getInt("UserId", 0);
            userType = pref.getString("UserType", "");
            userName = pref.getString("UserName", "");

            Log.e(tag, "UserId : " + userId);
            Log.e(tag, "UserType : " + userType);
        } catch (Exception e) {
            Log.e(tag, "" + e.getMessage());
        }

        try {
            Log.e("FCM", "-----------------------------------------" + getIntent().getIntExtra("FcmTitle", 0));
            fcmType = getIntent().getIntExtra("FcmTag", 0);
        } catch (Exception e) {
            Log.e("HomeActivity : ", " FCM Exception : " + e.getMessage());
            e.printStackTrace();
            fcmType = 0;
        }


        if (userId <= 0) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        }


        tvNavHead = findViewById(R.id.textView);
        tvNavHead.setText("Welcome " + userName);

        tvAdminMenu_Home = findViewById(R.id.tvAdminMenuHome);
        tvAdminMenu_CallWaiter = findViewById(R.id.tvAdminMenuCallWaiter);
        tvAdminMenu_AddUser = findViewById(R.id.tvAdminMenuAddUser);
        tvAdminMenu_AddCategory = findViewById(R.id.tvAdminMenuAddCategory);
        tvAdminMenu_AddItem = findViewById(R.id.tvAdminMenuAddItem);
        tvAdminMenu_AddTable = findViewById(R.id.tvAdminMenuAddTable);
        tvAdminMenu_Settings = findViewById(R.id.tvAdminMenuSettings);
        tvAdminMenu_UpdateStock = findViewById(R.id.tvAdminMenuUpdateStock);
        tvAdminMenu_UpdateRate = findViewById(R.id.tvAdminMenuUpdateRate);
        tvAdminMenu_AddNews = findViewById(R.id.tvAdminMenuNews);
        tvAdminMenu_Terms = findViewById(R.id.tvAdminMenuTerms);
        tvAdminMenu_AboutDev = findViewById(R.id.tvAdminMenuAboutDev);
        tvAdminMenu_AboutUs = findViewById(R.id.tvAdminMenuAboutUs);
        tvAdminMenu_RateApp = findViewById(R.id.tvAdminMenuRateApp);
        tvAdminMenu_AddCustomer = findViewById(R.id.tvAdminMenuAddCustomer);
        tvAdminMenu_ViewOrders = findViewById(R.id.tvAdminMenuViewOrders);
        tvAdminMenu_BillGenerate = findViewById(R.id.tvAdminMenuBillGenerate);
        tvAdminMenu_ViewBill = findViewById(R.id.tvAdminMenuViewBill);
        tvAdminMenu_Signout = findViewById(R.id.tvAdminMenuSignOut);
        tvAdminMenu_PrintIP = findViewById(R.id.tvAdminMenuPrinterSettings);
        tvAdminMenu_BillDayEnd = findViewById(R.id.tvAdminMenuBillDayEnd);
        tvAdminMenu_Reports = findViewById(R.id.tvAdminMenuReports);
        tvAdminMenu_RejectedOrderReports = findViewById(R.id.tvAdminMenuRejectedOrderReport);

        tvAdminMenu_AboutDev.setVisibility(View.GONE);
        // tvAdminMenu_Reports.setVisibility(View.GONE);

        tvAdminMenu_Home.setOnClickListener(this);
        tvAdminMenu_CallWaiter.setOnClickListener(this);
        tvAdminMenu_AddUser.setOnClickListener(this);
        tvAdminMenu_AddCategory.setOnClickListener(this);
        tvAdminMenu_AddItem.setOnClickListener(this);
        tvAdminMenu_AddTable.setOnClickListener(this);
        tvAdminMenu_Settings.setOnClickListener(this);
        tvAdminMenu_UpdateStock.setOnClickListener(this);
        tvAdminMenu_UpdateRate.setOnClickListener(this);
        tvAdminMenu_AddNews.setOnClickListener(this);
        tvAdminMenu_Terms.setOnClickListener(this);
        tvAdminMenu_AboutDev.setOnClickListener(this);
        tvAdminMenu_AboutUs.setOnClickListener(this);
        tvAdminMenu_RateApp.setOnClickListener(this);
        tvAdminMenu_AddCustomer.setOnClickListener(this);
        tvAdminMenu_ViewOrders.setOnClickListener(this);
        tvAdminMenu_BillGenerate.setOnClickListener(this);
        tvAdminMenu_ViewBill.setOnClickListener(this);
        tvAdminMenu_Signout.setOnClickListener(this);
        tvAdminMenu_PrintIP.setOnClickListener(this);
        tvAdminMenu_BillDayEnd.setOnClickListener(this);
        tvAdminMenu_Reports.setOnClickListener(this);
        tvAdminMenu_RejectedOrderReports.setOnClickListener(this);

        if (userType.equalsIgnoreCase("Manager")) {
            //tvAdminMenu_AddUser.setVisibility(View.GONE);
            // tvAdminMenu_CallWaiter.setVisibility(View.GONE);
            //tvAdminMenu_AddCategory.setVisibility(View.GONE);
            //tvAdminMenu_AddItem.setVisibility(View.GONE);
            //tvAdminMenu_AddTable.setVisibility(View.GONE);
            //tvAdminMenu_Settings.setVisibility(View.GONE);
            //tvAdminMenu_BillGenerate.setVisibility(View.GONE);
            //tvAdminMenu_ViewBill.setVisibility(View.GONE);
            //tvAdminMenu_UpdateStock.setVisibility(View.GONE);
            //tvAdminMenu_UpdateRate.setVisibility(View.GONE);
            //tvAdminMenu_AddNews.setVisibility(View.GONE);
            //tvAdminMenu_AddCustomer.setVisibility(View.GONE);
        } else if (userType.equalsIgnoreCase("Captain")) {
            tvAdminMenu_AddUser.setVisibility(View.GONE);
            tvAdminMenu_AddCategory.setVisibility(View.GONE);
            tvAdminMenu_AddItem.setVisibility(View.GONE);
            tvAdminMenu_AddTable.setVisibility(View.GONE);
            tvAdminMenu_Settings.setVisibility(View.GONE);
            tvAdminMenu_UpdateStock.setVisibility(View.GONE);
            tvAdminMenu_UpdateRate.setVisibility(View.GONE);
            tvAdminMenu_AddNews.setVisibility(View.GONE);
            tvAdminMenu_BillDayEnd.setVisibility(View.GONE);
            tvAdminMenu_Reports.setVisibility(View.GONE);
            tvAdminMenu_RejectedOrderReports.setVisibility(View.GONE);
        } else if (userType.equalsIgnoreCase("Waiter")) {
            tvAdminMenu_AddUser.setVisibility(View.GONE);
            tvAdminMenu_CallWaiter.setVisibility(View.GONE);
            tvAdminMenu_AddCategory.setVisibility(View.GONE);
            tvAdminMenu_AddItem.setVisibility(View.GONE);
            tvAdminMenu_AddTable.setVisibility(View.GONE);
            tvAdminMenu_Settings.setVisibility(View.GONE);
            tvAdminMenu_UpdateStock.setVisibility(View.GONE);
            tvAdminMenu_BillGenerate.setVisibility(View.GONE);
            tvAdminMenu_ViewBill.setVisibility(View.GONE);
            tvAdminMenu_UpdateRate.setVisibility(View.GONE);
            tvAdminMenu_AddNews.setVisibility(View.GONE);
            tvAdminMenu_AddCustomer.setVisibility(View.GONE);
            tvAdminMenu_BillDayEnd.setVisibility(View.GONE);
        } else if (userType.equalsIgnoreCase("KOT")) {
            tvAdminMenu_AddUser.setVisibility(View.GONE);
            tvAdminMenu_CallWaiter.setVisibility(View.GONE);
            tvAdminMenu_AddCategory.setVisibility(View.GONE);
            tvAdminMenu_AddItem.setVisibility(View.GONE);
            tvAdminMenu_AddTable.setVisibility(View.GONE);
            tvAdminMenu_Settings.setVisibility(View.GONE);
            tvAdminMenu_BillGenerate.setVisibility(View.GONE);
            tvAdminMenu_ViewBill.setVisibility(View.GONE);
            tvAdminMenu_UpdateStock.setVisibility(View.GONE);
            tvAdminMenu_UpdateRate.setVisibility(View.GONE);
            tvAdminMenu_AddNews.setVisibility(View.GONE);
            tvAdminMenu_AddCustomer.setVisibility(View.GONE);
        } else {
            tvAdminMenu_CallWaiter.setVisibility(View.GONE);

        }

        if (PermissionsUtil.checkAndRequestPermissions(this)) {

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(R.mipmap.menu_icon);
        toggle.syncState();

        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (fcmType == 0) {
            if (savedInstanceState == null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, new AdminHomeFragment(), "Home");
                ft.commit();
            }
        } else if (fcmType == 1) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new WaiterFragment(), "HomeFragment");
            ft.commit();
        } else if (fcmType == 2) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new ViewOrdersFragment(), "HomeFragment");
            ft.commit();
        }
    }

    @Override
    public void onBackPressed() {
        Fragment home = getSupportFragmentManager().findFragmentByTag("Home");
        Fragment homeFragment = getSupportFragmentManager().findFragmentByTag("HomeFragment");
        Fragment categoryMaster = getSupportFragmentManager().findFragmentByTag("CategoryMaster");
        Fragment userMaster = getSupportFragmentManager().findFragmentByTag("UserMaster");
        Fragment itemMaster = getSupportFragmentManager().findFragmentByTag("ItemMaster");
        Fragment tableMaster = getSupportFragmentManager().findFragmentByTag("TableMaster");
        Fragment newsMaster = getSupportFragmentManager().findFragmentByTag("NewsMaster");
        Fragment customerMaster = getSupportFragmentManager().findFragmentByTag("CustomerMaster");
        Fragment billTableMaster = getSupportFragmentManager().findFragmentByTag("BillTableMaster");


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (home instanceof AdminHomeFragment && home.isVisible()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this, R.style.AlertDialogTheme);
            builder.setTitle("Confirm Action");
            builder.setMessage("Do You Really Want To Exit?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
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
        } else if (homeFragment instanceof CategoryMasterFragment && homeFragment.isVisible() ||
                homeFragment instanceof WaiterFragment && homeFragment.isVisible() ||
                homeFragment instanceof UserMasterFragment && homeFragment.isVisible() ||
                homeFragment instanceof ItemMasterFragment && homeFragment.isVisible() ||
                homeFragment instanceof TableMasterFragment && homeFragment.isVisible() ||
                homeFragment instanceof NewsMasterFragment && homeFragment.isVisible() ||
                homeFragment instanceof UpdateStockFragment && homeFragment.isVisible() ||
                homeFragment instanceof UpdateRateFragment && homeFragment.isVisible() ||
                homeFragment instanceof SettingsFragment && homeFragment.isVisible() ||
                homeFragment instanceof TermsConditionFragment && homeFragment.isVisible() ||
                homeFragment instanceof AboutDeveloperFragment && homeFragment.isVisible() ||
                homeFragment instanceof AboutUsFragment && homeFragment.isVisible() ||
                homeFragment instanceof CustomerMasterFragment && homeFragment.isVisible() ||
                homeFragment instanceof ViewOrdersFragment && homeFragment.isVisible() ||
                homeFragment instanceof TablesForBillFragment && homeFragment.isVisible() ||
                homeFragment instanceof BillDisplayFragment && homeFragment.isVisible() ||
                homeFragment instanceof PrinterIPFragment && homeFragment.isVisible() ||
                homeFragment instanceof ReportsFragment && homeFragment.isVisible() ||
                homeFragment instanceof RejectedOrderReportFragment && homeFragment.isVisible()) {

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new AdminHomeFragment(), "HomeFragment");
            ft.commit();
        } else if (categoryMaster instanceof AddCategoryFragment && categoryMaster.isVisible() ||
                categoryMaster instanceof EditCategoryFragment && categoryMaster.isVisible()) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new CategoryMasterFragment(), "HomeFragment");
            ft.commit();
        } else if (userMaster instanceof AddUserFragment && userMaster.isVisible() ||
                userMaster instanceof EditUserFragment && userMaster.isVisible()) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new UserMasterFragment(), "HomeFragment");
            ft.commit();
        } else if (itemMaster instanceof AddItemFragment && itemMaster.isVisible() ||
                itemMaster instanceof EditItemFragment && itemMaster.isVisible()) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new ItemMasterFragment(), "HomeFragment");
            ft.commit();
        } else if (tableMaster instanceof AddTableFragment && tableMaster.isVisible() ||
                tableMaster instanceof EditTableFragment && tableMaster.isVisible()) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new TableMasterFragment(), "HomeFragment");
            ft.commit();
        } else if (newsMaster instanceof AddNewsFragment && newsMaster.isVisible() ||
                newsMaster instanceof EditNewsFragment && newsMaster.isVisible()) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new NewsMasterFragment(), "HomeFragment");
            ft.commit();
        } else if (customerMaster instanceof AddCustomerFragment && customerMaster.isVisible()) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new CustomerMasterFragment(), "HomeFragment");
            ft.commit();
        } else if (billTableMaster instanceof GenerateBillFragment && billTableMaster.isVisible()) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new TablesForBillFragment(), "HomeFragment");
            ft.commit();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tvAdminMenuHome) {
            Fragment fragment = new AdminHomeFragment();
            if (fragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment, "Home");
                ft.commit();
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        if (view.getId() == R.id.tvAdminMenuCallWaiter) {
            Fragment fragment = new WaiterFragment();
            if (fragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment, "HomeFragment");
                ft.commit();
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (view.getId() == R.id.tvAdminMenuAddUser) {
            Fragment fragment = new UserMasterFragment();
            if (fragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment, "HomeFragment");
                ft.commit();
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (view.getId() == R.id.tvAdminMenuAddCustomer) {
            Fragment fragment = new CustomerMasterFragment();
            if (fragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment, "HomeFragment");
                ft.commit();
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (view.getId() == R.id.tvAdminMenuAddCategory) {
            Fragment fragment = new CategoryMasterFragment();
            if (fragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment, "HomeFragment");
                ft.commit();
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (view.getId() == R.id.tvAdminMenuAddItem) {
            Fragment fragment = new ItemMasterFragment();
            if (fragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment, "HomeFragment");
                ft.commit();
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (view.getId() == R.id.tvAdminMenuAddTable) {
            Fragment fragment = new TableMasterFragment();
            if (fragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment, "HomeFragment");
                ft.commit();
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (view.getId() == R.id.tvAdminMenuSettings) {
            Fragment fragment = new SettingsFragment();
            if (fragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment, "HomeFragment");
                ft.commit();
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (view.getId() == R.id.tvAdminMenuUpdateStock) {
            Fragment fragment = new UpdateStockFragment();
            if (fragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment, "HomeFragment");
                ft.commit();
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (view.getId() == R.id.tvAdminMenuUpdateRate) {
            Fragment fragment = new UpdateRateFragment();
            if (fragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment, "HomeFragment");
                ft.commit();
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (view.getId() == R.id.tvAdminMenuNews) {
            Fragment fragment = new NewsMasterFragment();
            if (fragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment, "HomeFragment");
                ft.commit();
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (view.getId() == R.id.tvAdminMenuTerms) {
            Fragment fragment = new TermsConditionFragment();
            if (fragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment, "HomeFragment");
                ft.commit();
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (view.getId() == R.id.tvAdminMenuAboutDev) {
            Fragment fragment = new AboutDeveloperFragment();
            if (fragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment, "HomeFragment");
                ft.commit();
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (view.getId() == R.id.tvAdminMenuAboutUs) {
            Fragment fragment = new AboutUsFragment();
            if (fragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment, "HomeFragment");
                ft.commit();
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (view.getId() == R.id.tvAdminMenuReports) {
            Fragment fragment = new ReportsFragment();
            if (fragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment, "HomeFragment");
                ft.commit();
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (view.getId() == R.id.tvAdminMenuRejectedOrderReport) {
            Fragment fragment = new RejectedOrderReportFragment();
            if (fragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment, "HomeFragment");
                ft.commit();
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (view.getId() == R.id.tvAdminMenuViewOrders) {
            Fragment fragment = new ViewOrdersFragment();
            if (fragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment, "HomeFragment");
                ft.commit();
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (view.getId() == R.id.tvAdminMenuBillDayEnd) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this, R.style.AlertDialogTheme);
            builder.setTitle("Bill Day End");
            builder.setMessage("Are You Sure You Want To End Day?");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    resetBillNo();
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        } else if (view.getId() == R.id.tvAdminMenuBillGenerate) {
            Fragment fragment = new TablesForBillFragment();
            if (fragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment, "HomeFragment");
                ft.commit();
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (view.getId() == R.id.tvAdminMenuViewBill) {
            Fragment fragment = new BillDisplayFragment();
            if (fragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment, "HomeFragment");
                ft.commit();
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (view.getId() == R.id.tvAdminMenuRateApp) {

            Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
            }

        } else if (view.getId() == R.id.tvAdminMenuPrinterSettings) {
            Fragment fragment = new PrinterIPFragment();
            if (fragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment, "HomeFragment");
                ft.commit();
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (view.getId() == R.id.tvAdminMenuSignOut) {
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this, R.style.AlertDialogTheme);
            builder.setTitle("Sign Out");
            builder.setMessage("Are You Sure You Want To Sign Out?");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences(InterfaceApi.MY_PREF, MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.remove("UserId");
                    editor.remove("UserType");
                    editor.remove("UserName");
                    editor.commit();
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }


/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/


    public void resetBillNo() {
        if (CheckNetwork.isInternetAvailable(this)) {

            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder().baseUrl(InterfaceApi.URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();

            InterfaceApi api = retrofit.create(InterfaceApi.class);
            Call<ErrorMessage> errorMessageCall = api.resetBillNumber();


            final Dialog progressDialog = new Dialog(this);
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
                            Log.e("Reset Bill No", "----------------------------------" + response.body());
                            if (data.getError()) {
                                progressDialog.dismiss();
                                Toast.makeText(HomeActivity.this, "Unable To Process", Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(HomeActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(HomeActivity.this, "Unable To Process", Toast.LENGTH_SHORT).show();
                            Log.e("RESPONSE : ", " NO DATA");
                        }
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Log.e("Exception : ", "" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<ErrorMessage> call, Throwable t) {
                    Toast.makeText(HomeActivity.this, "Unable To Process", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    Log.e("ON FAILURE : ", " ERROR : " + t.getMessage());
                }
            });


        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
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
