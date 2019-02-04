package com.ats.barstockexchange.util;

import com.ats.barstockexchange.bean.Admin;
import com.ats.barstockexchange.bean.AllCategoryAndItemsData;
import com.ats.barstockexchange.bean.BillData;
import com.ats.barstockexchange.bean.BillEntry;
import com.ats.barstockexchange.bean.CallWaiter;
import com.ats.barstockexchange.bean.Category;
import com.ats.barstockexchange.bean.CategoryData;
import com.ats.barstockexchange.bean.CategoryNameDisplay;
import com.ats.barstockexchange.bean.ErrorMessage;
import com.ats.barstockexchange.bean.GenerateBillOutput;
import com.ats.barstockexchange.bean.Item;
import com.ats.barstockexchange.bean.ItemData;
import com.ats.barstockexchange.bean.ItemListData;
import com.ats.barstockexchange.bean.LoginData;
import com.ats.barstockexchange.bean.News;
import com.ats.barstockexchange.bean.NewsData;
import com.ats.barstockexchange.bean.OrderEntry;
import com.ats.barstockexchange.bean.OrdersSortByTable;
import com.ats.barstockexchange.bean.RejectedOrder;
import com.ats.barstockexchange.bean.ReportBean;
import com.ats.barstockexchange.bean.Settings;
import com.ats.barstockexchange.bean.SettingsData;
import com.ats.barstockexchange.bean.Table;
import com.ats.barstockexchange.bean.TableData;
import com.ats.barstockexchange.bean.UpdateRate;
import com.ats.barstockexchange.bean.UpdateStock;
import com.ats.barstockexchange.bean.User;
import com.ats.barstockexchange.bean.UserData;
import com.ats.barstockexchange.bean.UserMasterData;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by maxadmin on 3/11/17.
 */

public interface InterfaceApi {

   // public static final String URL = "http://132.148.143.124:8080/BSEWebApis/";
   // public static final String URL = "http://192.168.2.18:8062/";

    public static final String URL = "http://148.72.209.226:8080/BSEWebApis/";

    public static final String IMAGE_PATH = "http://148.72.209.226:8080/BSEWebApis/images/";

    public static final String MY_PREF = "BSE_Admin";

    @POST("adminLogin")
    Call<LoginData> doLogin(@Query("username") String username, @Query("password") String password);

    @GET("getAllAdminUser")
    Call<UserData> getAllUser();

    @POST("insertAdminUser")
    Call<ErrorMessage> addAdminUser(@Body Admin admin);

    @POST("editAdminUser")
    Call<ErrorMessage> editAdminUser(@Body Admin admin);

    @POST("deleteAdminUser")
    Call<ErrorMessage> deleteAdminUser(@Query("adminId") int adminId);

    @GET("getAllCategory")
    Call<CategoryData> getAllCategory();

    @POST("insertCategory")
    Call<ErrorMessage> addCategory(@Body Category category);

    @POST("editCategory")
    Call<ErrorMessage> editCategory(@Body Category category);

    @POST("deleteCategory")
    Call<ErrorMessage> deleteCategory(@Query("categoryId") int categoryId);

    @GET("getAllItem")
    Call<ItemData> getAllItem();

    @GET("getCategoryNameList")
    Call<CategoryNameDisplay> getCategoryNameList();

    @POST("insertItem")
    Call<ErrorMessage> addItem(@Body Item item);

    @POST("editItem")
    Call<ErrorMessage> editItem(@Body Item item);

    @POST("deleteItem")
    Call<ErrorMessage> deleteItem(@Query("itemId") int itemId);

    @GET("getAllTable")
    Call<TableData> getAllTable();

    @POST("insertTable")
    Call<ErrorMessage> addTable(@Body Table table);

    @POST("editTable")
    Call<ErrorMessage> editTable(@Body Table table);

    @POST("deleteTable")
    Call<ErrorMessage> deleteTable(@Query("tableId") int tableId);

    @POST("editSetting")
    Call<ErrorMessage> editSetting(@Body Settings settings);

    @GET("getSettings")
    Call<SettingsData> getSettings();

    @GET("getAllNews")
    Call<NewsData> getAllNews();

    @POST("insertNews")
    Call<ErrorMessage> addNews(@Body News news);

    @POST("editNews")
    Call<ErrorMessage> editNews(@Body News news);

    @POST("deleteNews")
    Call<ErrorMessage> deleteNews(@Query("newsId") int newsId);

    @GET("getAllCategoryAndItems")
    Call<AllCategoryAndItemsData> getAllCatAndItems();

    //user/getAllCategoryAndItems
    //getAllCategoryAndItemsByQuery

    @GET("user/getAllCategoryAndItems")
    Call<AllCategoryAndItemsData> getAllCatAndItemsByQuery();

    @POST("updateItemStock")
    Call<ErrorMessage> updateItemStock(@Body ArrayList<UpdateStock> updateStocks);

    @POST("updateItemMinMaxRate")
    Call<ErrorMessage> updateItemMinMaxRate(@Body ArrayList<UpdateRate> updateRates);

    @GET("getAllUsersByEnterBy")
    Call<UserMasterData> getAllUserByEnterBy(@Query("enterBy") int enterBy);

    @POST("insertUserByCaptain")
    Call<ErrorMessage> addCustomer(@Body User user);

    @GET("getOrdersByTable")
    Call<OrdersSortByTable> getAllOrders(@Query("type") int type);

    @GET("getOrdersByTable1")
    Call<OrdersSortByTable> getAllOrders1(@Query("type") int type);

    @GET("getCaptainOrders")
    Call<OrdersSortByTable> getAllOrdersFrom1to5();

    @GET("getCaptainOrdersRejected")
    Call<OrdersSortByTable> getRejectedOrders();

    @POST("updateBillStatus")
    Call<ErrorMessage> updateBillStatus(@Query("orderId") int orderId, @Query("status") int status);

    @POST("deleteOrder")
    Call<ErrorMessage> deleteOrder(@Query("orderId") int orderId);

    @POST("deleteUserByCaptain")
    Call<ErrorMessage> deleteUserByCaptain(@Query("userId") int userId);

    @POST("getItemBycatId")
    Call<ItemListData> getItemsByCategory(@Query("catId") int catId);

    @POST("user/placeOrder")
    Call<ErrorMessage> placeUserOrder(@Body OrderEntry orderEntry);

    @GET("getAllUsers")
    Call<UserMasterData> getAllCustomers();

    @POST("saveBill")
    Call<ErrorMessage> generateBill(@Body BillEntry billEntry);

    @POST("saveBill")
    Call<GenerateBillOutput> generateBillOutput(@Body BillEntry billEntry);

    @POST("getBillByDate")
    Call<BillData> getBillData(@Query("fmdate") String fDate, @Query("todate") String tDate);

    @GET("user/getAllRequests")
    Call<ArrayList<CallWaiter>> getCustomerPendingRequest();

    @POST("user/updateIsVisited")
    Call<ErrorMessage> updateVisitStatus(@Query("id") int id);

    @GET("updateAdminToken")
    Call<ErrorMessage> updateToken(@Query("userId") int userId, @Query("token") String token);

    @GET("billDayEnd")
    Call<ErrorMessage> resetBillNumber();

    @POST("billReport")
    Call<ReportBean> getReport(@Query("date") String date);

    @GET("user/getAllCategoryAndItemsForUpdate")
    Call<AllCategoryAndItemsData> getAllCatAndItemsForUpdate();

    @GET("getRejectedOrder")
    Call<ArrayList<RejectedOrder>> getRejectedOrderList(@Query("fromDate") String fromDate, @Query("toDate") String toDate);

}
