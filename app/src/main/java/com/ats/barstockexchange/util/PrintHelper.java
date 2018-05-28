package com.ats.barstockexchange.util;

import android.app.Activity;
import android.content.ContentValues;
import android.util.Log;
import android.widget.Toast;

import com.ats.barstockexchange.R;
import com.ats.barstockexchange.bean.Bill;
import com.ats.barstockexchange.bean.BillDetailsList;
import com.ats.barstockexchange.bean.CustomBillHeader;
import com.ats.barstockexchange.bean.CustomBillItems;
import com.ats.barstockexchange.bean.GenerateBillOutput;
import com.ats.barstockexchange.bean.Item;
import com.ats.barstockexchange.bean.OrderDetailsList;
import com.ats.barstockexchange.bean.OrderDisplay;
import com.ats.barstockexchange.bean.OrderEntry;
import com.ats.barstockexchange.bean.OrderItem;
import com.ats.barstockexchange.bean.ReportDisplayBean;
import com.ats.barstockexchange.bean.Table;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;

import java.util.ArrayList;

/**
 * Created by MAXADMIN on 27/1/2018.
 */


public class PrintHelper implements ReceiveListener {
    Activity activity;
    String printerAddress;
    int modelConstant;
    int printReceiptType;
    private Printer mPrinter = null;
    public static String tableType = null;
    private OrderDisplay orderDisplays;
    private CustomBillHeader customBillHeader;
    String captainName;
    private ArrayList<ReportDisplayBean> reportDisplayArray;
    private String date;
    private GenerateBillOutput billOutput;
    private OrderEntry orderEntry;
    private ArrayList<OrderItem> orderItemArrayList;
    private ArrayList<Table> tableNameArray;

    static {
        try {
            System.loadLibrary("libepos2.so");
        } catch (UnsatisfiedLinkError e) {
            Log.e("UnsatisfiedLinkError", "-----------------------" + e.getMessage());
        } catch (Exception e) {
            Log.e("Exception", "------------------------" + e.getMessage());
        }
    }

    public PrintHelper(Activity activity, String printerAddress, int ModelConstant, int printReceiptType) {
        this.activity = activity;
        this.printerAddress = printerAddress;
        this.modelConstant = Printer.TM_T82; //ModelConstant;
        this.printReceiptType = printReceiptType;
    }

    public PrintHelper(Activity activity, String printerAddress, int modelConstant) {
        this.activity = activity;
        this.printerAddress = printerAddress;
        this.modelConstant = Printer.TM_T82;
    }


    public PrintHelper(Activity activity, String printerAddress, int modelConstant, OrderDisplay orderDisplay, int printReceiptType) {
        this.activity = activity;
        this.printerAddress = printerAddress;
        this.modelConstant = Printer.TM_T82;
        this.orderDisplays = orderDisplay;
        this.printReceiptType = printReceiptType;
    }

    public PrintHelper(Activity activity, String printerAddress, int modelConstant, OrderDisplay orderDisplay, int printReceiptType, String captainName,ArrayList<Table> tableNameArray) {
        this.activity = activity;
        this.printerAddress = printerAddress;
        this.modelConstant = Printer.TM_T82;
        this.orderDisplays = orderDisplay;
        this.printReceiptType = printReceiptType;
        this.captainName = captainName;
        this.tableNameArray = tableNameArray;
    }

    public PrintHelper(Activity activity, String printerAddress, int modelConstant, OrderEntry orderEntry, ArrayList<OrderItem> orderItemArrayList, int printReceiptType, String captainName,ArrayList<Table> tableNameArray) {
        this.activity = activity;
        this.printerAddress = printerAddress;
        this.modelConstant = Printer.TM_T82;
        this.orderEntry = orderEntry;
        this.orderItemArrayList = orderItemArrayList;
        this.printReceiptType = printReceiptType;
        this.captainName = captainName;
        this.tableNameArray = tableNameArray;

    }

    public PrintHelper(Activity activity, String printerAddress, int modelConstant, CustomBillHeader billHeader, int printReceiptType,ArrayList<Table> tableNameArray) {
        this.activity = activity;
        this.printerAddress = printerAddress;
        this.modelConstant = Printer.TM_T82;
        this.customBillHeader = billHeader;
        this.printReceiptType = printReceiptType;
        this.tableNameArray = tableNameArray;
    }

    public PrintHelper(Activity activity, String printerAddress, int modelConstant, GenerateBillOutput billOutput, int printReceiptType, ArrayList<Table> tableNameArray) {
        this.activity = activity;
        this.printerAddress = printerAddress;
        this.modelConstant = Printer.TM_T82;
        this.billOutput = billOutput;
        this.printReceiptType = printReceiptType;
        this.tableNameArray = tableNameArray;
    }

    public PrintHelper(Activity activity, String printerAddress, int modelConstant, ArrayList<ReportDisplayBean> reportDisplayArray, String date, int printReceiptType) {
        this.activity = activity;
        this.printerAddress = printerAddress;
        this.modelConstant = Printer.TM_T82;
        this.reportDisplayArray = reportDisplayArray;
        this.date = date;
        this.printReceiptType = printReceiptType;
    }

    public boolean createReceiptData() {
        if (mPrinter == null) {
            return false;
        }

        if (printReceiptType == PrintReceiptType.BILL) {
            //create bill invoice
            return createBillReceiptPrint(customBillHeader);
        } else if (printReceiptType == PrintReceiptType.KOT) {
            //create KOT invoice
            return createKOTReceipt(orderDisplays,tableNameArray);
        } else if (printReceiptType == PrintReceiptType.REPORT) {
            //create REPORT print
            return createReportPrint(reportDisplayArray, date);
        } else if (printReceiptType == PrintReceiptType.GENERATE_BILL) {
            //create bill invoice
            return createBillReceiptPrintAfterGenerate(billOutput, tableNameArray);
        } else if (printReceiptType == PrintReceiptType.KOT_CAPTAIN) {
            //create bill invoice
            return createKOTReceiptByCaptain(orderEntry, orderItemArrayList,tableNameArray);
        } else {
            return createTestReceipt();
        }
        // return createTestReceipt();
    }

    private boolean createTestReceipt() {
        String method = "";
        StringBuilder textData = new StringBuilder();

        try {
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            method = "addFeedLine";
            mPrinter.addFeedLine(1);
            textData.append("This is a TEST receipt\n");
            // textData.append("\tThis is a TEST receipt\n");
            mPrinter.addText(textData.toString());
            mPrinter.addCut(Printer.CUT_FEED);
        } catch (Exception e) {
            ShowMsg.showException(e, method, activity, false);
            return false;
        }
        return true;
    }

    private boolean createKOTReceipt(OrderDisplay orderDisplays, ArrayList<Table> tableArray) {
        String method = "";
        StringBuilder textData = new StringBuilder();

        try {
            ArrayList<OrderItem> orderItems = (ArrayList<OrderItem>) orderDisplays.getOrderItems();

            String tableNo = "";
            for (int i = 0; i < tableArray.size(); i++) {
                if (tableArray.get(i).getTableNo() == orderDisplays.getOrder().getTableNo()) {
                    tableNo = tableArray.get(i).getTableName();
                }
            }

            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            method = "addFeedLine";
            mPrinter.addFeedLine(1);
            String date = orderDisplays.getOrder().getOrderDate();
            textData.append("\t\t\tSHAIL\n");

            // textData.append("Captain Name :- "+captainName + "\n");
            textData.append(date + "\n");

            textData.append("Order No :- " + orderDisplays.getOrder().getOrderId());
            textData.append("\t\t\t Table No :- " + tableNo + "\n\n");

            Log.e("OrderItems : ", "-----------" + orderItems.toString());

            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.COLOR_1);

            int maxCharacterCount = 35;
            int maxItemNameCount = 28;
            int maxQuantityCount = 3;
            int spaceCount = 4;

            textData.append("Item");
            for (int s = 0; s < 24; s++) {
                textData.append(" ");
            }
            textData.append("    Qty\n");
            textData.append("-------------------------------------\n");

            for (int i = 0; i < orderItems.size(); i++) {
                try {

                    String strName = orderItems.get(i).getItemName();
                    if (strName.length() > 28) {
                        String itemName = orderItems.get(i).getItemName().substring(0, 28);
                        textData.append("" + itemName);
                    } else if (strName.length() < 28) {
                        textData.append("" + strName);
                        int difference = 28 - strName.length();

                        for (int d = 0; d < difference; d++) {
                            textData.append(" ");
                        }
                    }

                    String qty = String.valueOf(orderItems.get(i).getQuantity());

                    textData.append("    " + qty + "\n");


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            textData.append("\n");


            mPrinter.addText(textData.toString());
            Log.e("Print ", "\n\n" + textData.toString());

            mPrinter.addCut(Printer.CUT_FEED);


        } catch (Exception e) {
            e.printStackTrace();
            ShowMsg.showException(e, method, activity, false);
            return false;
        }
        return true;
    }

    private boolean createKOTReceiptByCaptain(OrderEntry order, ArrayList<OrderItem> orderItems, ArrayList<Table> tableArray) {
        String method = "";
        StringBuilder textData = new StringBuilder();

        try {


            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            method = "addFeedLine";
            mPrinter.addFeedLine(1);
            String date = order.getOrderDate();
            textData.append("\t\t\tSHAIL\n");

            // textData.append("Captain Name :- "+captainName + "\n");
            textData.append(date + "\n");

            String tableNo = "";
            for (int i = 0; i < tableArray.size(); i++) {
                if (tableArray.get(i).getTableNo() == order.getTableNo()) {
                    tableNo = tableArray.get(i).getTableName();
                }
            }

            // textData.append("Order No :- " + orderDisplays.getOrder().getOrderId());
            textData.append("\t\t\t Table No :- " + tableNo + "\n\n");

            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.COLOR_1);

            textData.append("Item");
            for (int s = 0; s < 24; s++) {
                textData.append(" ");
            }
            textData.append("    Qty\n");
            textData.append("-------------------------------------\n");

            for (int i = 0; i < orderItems.size(); i++) {
                try {

                    String strName = orderItems.get(i).getItemName();
/*
                    Log.e("ITEM NAME : ","-------********************----------"+orderItems.get(i).getItemId());
                    for (int j = 0; j < itemList.size(); j++) {
                        if (orderItems.get(i).getItemId() == itemList.get(j).getItemId()) {
                            strName = itemList.get(j).getItemName();
                            Log.e("ITEM NAME : ","-----------------"+strName);
                            break;
                        }
                    }
                    Log.e("ITEM NAME : ","---------@@@@@@@@@@@@@@@@@@@--------"+strName);
*/

                    if (strName.length() > 28) {
                        String itemName = strName.substring(0, 28);
                        textData.append("" + itemName);
                    } else if (strName.length() < 28) {
                        textData.append("" + strName);
                        int difference = 28 - strName.length();

                        for (int d = 0; d < difference; d++) {
                            textData.append(" ");
                        }
                    }

                    String qty = String.valueOf(orderItems.get(i).getQuantity());

                    textData.append("    " + qty + "\n");


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            textData.append("\n");


            mPrinter.addText(textData.toString());
            Log.e("Print ", "\n\n" + textData.toString());

            mPrinter.addCut(Printer.CUT_FEED);


        } catch (Exception e) {
            e.printStackTrace();
            ShowMsg.showException(e, method, activity, false);
            return false;
        }
        return true;
    }


    private boolean createBillReceiptPrint(CustomBillHeader billHeader) {
        String method = "";
        StringBuilder textData = new StringBuilder();

        try {
            ArrayList<CustomBillItems> orderItems = (ArrayList<CustomBillItems>) billHeader.getCustomBillItems();

            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            method = "addFeedLine";
            mPrinter.addFeedLine(1);
            String date = billHeader.getBillDate();
            textData.append("\t\t\bHotel Shail\n");
            textData.append("\tKokanwadi Railway St. Road\n");
            textData.append("\t\tAurangabad\n\n");

            // textData.append(date + "\n");

            textData.append("Bill No :- " + billHeader.getBillNo() + "\t" + date + "\n\n");
            // textData.append("\t\t\t Table No :- " + tableNo + "\n\n");

            Log.e("OrderItems : ", "-----------" + orderItems.toString());

            textData.append("Item");
            for (int i = 0; i < 16; i++) {
                textData.append(" ");
            }
            textData.append("   Qty");
            textData.append("     Rate");
            textData.append("    Amount\n");

            for (int i = 0; i < 45; i++) {
                textData.append("-");
            }
            textData.append("\n");

            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.COLOR_1);

            double billTotal = 0;

            for (int i = 0; i < orderItems.size(); i++) {

                String strName = orderItems.get(i).getItemName();
                if (strName.length() >= 20) {
                    String itemName = orderItems.get(i).getItemName().substring(0, 20);
                    textData.append(itemName);

                } else if (strName.length() < 20) {
                    textData.append(strName);
                    int difference = 20 - strName.length();

                    for (int d = 0; d < difference; d++) {
                        textData.append(" ");
                    }
                }

                String qty = String.valueOf(orderItems.get(i).getQuantity());
                double totalDouble = orderItems.get(i).getRate() * orderItems.get(i).getQuantity();
                //String rate = String.valueOf(rateDouble);
                String total = String.format("%.1f", totalDouble);
                String rate = String.valueOf(orderItems.get(i).getRate());

                billTotal = billTotal + totalDouble;


                try {

                    textData.append("   " + qty);
                    int difference = 3 - qty.length();
                    for (int d = 0; d < difference; d++) {
                        textData.append(" ");
                    }

                    textData.append("   ");

                    difference = 6 - rate.length();
                    for (int d = 0; d < difference; d++) {
                        textData.append(" ");
                    }
                    textData.append("" + rate);

                    textData.append("   ");

                    difference = 7 - total.length();
                    for (int d = 0; d < difference; d++) {
                        textData.append(" ");
                    }
                    textData.append("" + total + "\n");


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < 45; i++) {
                textData.append("-");
            }
            textData.append("\n");

            String bTot = "Bar Total : " + String.format("%.2f", billTotal);
            int difference = 45 - bTot.length();
            for (int d = 0; d < difference; d++) {
                textData.append(" ");
            }
            textData.append(bTot + "\n");

            int billDisc = (int) billHeader.getDiscount();
            String disc = "Discount : " + billDisc + " %";
            difference = 45 - disc.length();
            for (int d = 0; d < difference; d++) {
                textData.append(" ");
            }
            textData.append(disc + "\n");


            String gst = "VAT : 5 %";
            difference = 45 - gst.length();
            for (int d = 0; d < difference; d++) {
                textData.append(" ");
            }
            textData.append(gst + "\n");

            //textData.append("\nDiscount :- " + billHeader.getDiscount());
            //textData.append("\t Total :- " + billHeader.getPayableAmount() + "\n");

            for (int i = 0; i < 45; i++) {
                textData.append("-");
            }
            textData.append("\n");

            String grandTotal = "GRAND TOTAL : " + String.format("%.2f", billHeader.getPayableAmount());
            difference = 45 - grandTotal.length();
            for (int d = 0; d < difference; d++) {
                textData.append(" ");
            }
            textData.append(grandTotal + "\n");

            //textData.append("GRAND TOTAL : " + billHeader.getPayableAmount() + "\n");

            for (int i = 0; i < 45; i++) {
                textData.append("-");
            }
            textData.append("\n");

            //textData.append("Soft drink & TBC 5 % GST\n");
            textData.append("GSTNo : 27ABGPJ9389N1ZP\n\n");


            mPrinter.addText(textData.toString());
            Log.e("Print ", "\n\n" + textData.toString());

            mPrinter.addCut(Printer.CUT_FEED);


        } catch (Exception e) {
            e.printStackTrace();
            ShowMsg.showException(e, method, activity, false);
            return false;
        }
        return true;
    }

    private boolean createBillReceiptPrintAfterGenerate(GenerateBillOutput billOutput, ArrayList<Table> tableArray) {
        String method = "";
        StringBuilder textData = new StringBuilder();

        try {
            //   ArrayList<CustomBillItems> orderItems = (ArrayList<CustomBillItems>) billHeader.getCustomBillItems();
            ArrayList<BillDetailsList> orderItems = (ArrayList<BillDetailsList>) billOutput.getBillDetailsList();


            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            method = "addFeedLine";
            mPrinter.addFeedLine(1);
            String date = billOutput.getBillRes().getBillDate();
            textData.append("\t\t\bHotel Shail\n");
            textData.append("\tKokanwadi Railway St. Road\n");
            textData.append("\t\tAurangabad\n\n");

            // textData.append(date + "\n");

            textData.append("Bill No :- " + billOutput.getBillRes().getBillNo() + "\t" + date + "\n");

            String tableNo = "";
            for (int i = 0; i < tableArray.size(); i++) {
                if (tableArray.get(i).getTableNo() == billOutput.getBillRes().getTableNo()) {
                    tableNo = tableArray.get(i).getTableName();
                }
            }
            textData.append("Table No :- " + tableNo + "\n\n");

            Log.e("OrderItems : ", "-----------" + orderItems.toString());

            textData.append("Item");
            for (int i = 0; i < 16; i++) {
                textData.append(" ");
            }
            textData.append("   Qty");
            textData.append("     Rate");
            textData.append("    Amount\n");

            for (int i = 0; i < 45; i++) {
                textData.append("-");
            }
            textData.append("\n");

            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.COLOR_1);

            double billTotal = 0;

            for (int i = 0; i < orderItems.size(); i++) {

                String strName = orderItems.get(i).getItemName();
                if (strName.length() >= 20) {
                    String itemName = orderItems.get(i).getItemName().substring(0, 20);
                    textData.append(itemName);

                } else if (strName.length() < 20) {
                    textData.append(strName);
                    int difference = 20 - strName.length();

                    for (int d = 0; d < difference; d++) {
                        textData.append(" ");
                    }
                }

                String qty = String.valueOf(orderItems.get(i).getQuantity());
                double totalDouble = orderItems.get(i).getRate() * orderItems.get(i).getQuantity();
                //String rate = String.valueOf(rateDouble);
                String total = String.format("%.1f", totalDouble);
                String rate = String.valueOf(orderItems.get(i).getRate());

                billTotal = billTotal + totalDouble;


                try {

                    textData.append("   " + qty);
                    int difference = 3 - qty.length();
                    for (int d = 0; d < difference; d++) {
                        textData.append(" ");
                    }

                    textData.append("   ");

                    difference = 6 - rate.length();
                    for (int d = 0; d < difference; d++) {
                        textData.append(" ");
                    }
                    textData.append("" + rate);

                    textData.append("   ");

                    difference = 7 - total.length();
                    for (int d = 0; d < difference; d++) {
                        textData.append(" ");
                    }
                    textData.append("" + total + "\n");


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < 45; i++) {
                textData.append("-");
            }
            textData.append("\n");

            String bTot = "Bar Total : " + String.format("%.2f", billTotal);
            int difference = 45 - bTot.length();
            for (int d = 0; d < difference; d++) {
                textData.append(" ");
            }
            textData.append(bTot + "\n");

            int billDisc = (int) billOutput.getBillRes().getDiscount();
            String disc = "Discount : " + billDisc + " %";
            difference = 45 - disc.length();
            for (int d = 0; d < difference; d++) {
                textData.append(" ");
            }
            textData.append(disc + "\n");


            String gst = "VAT : 5 %";
            difference = 45 - gst.length();
            for (int d = 0; d < difference; d++) {
                textData.append(" ");
            }
            textData.append(gst + "\n");

            //textData.append("\nDiscount :- " + billHeader.getDiscount());
            //textData.append("\t Total :- " + billHeader.getPayableAmount() + "\n");

            for (int i = 0; i < 45; i++) {
                textData.append("-");
            }
            textData.append("\n");

            String grandTotal = "GRAND TOTAL : " + String.format("%.2f", billOutput.getBillRes().getPayableAmt());
            difference = 45 - grandTotal.length();
            for (int d = 0; d < difference; d++) {
                textData.append(" ");
            }
            textData.append(grandTotal + "\n");

            //textData.append("GRAND TOTAL : " + billHeader.getPayableAmount() + "\n");

            for (int i = 0; i < 45; i++) {
                textData.append("-");
            }
            textData.append("\n");

            //textData.append("Soft drink & TBC 5 % GST\n");
            textData.append("GSTNo : 27ABGPJ9389N1ZP\n\n\n");


            mPrinter.addText(textData.toString());
            Log.e("Print ", "\n\n" + textData.toString());

            mPrinter.addCut(Printer.CUT_FEED);

            mPrinter.addText(textData.toString());
            Log.e("Print ", "\n\n" + textData.toString());

            mPrinter.addCut(Printer.CUT_FEED);


        } catch (Exception e) {
            e.printStackTrace();
            ShowMsg.showException(e, method, activity, false);
            return false;
        }
        return true;
    }

    private boolean createReportPrint(ArrayList<ReportDisplayBean> reportArray, String date) {
        String method = "";
        StringBuilder textData = new StringBuilder();

        try {

            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            method = "addFeedLine";
            mPrinter.addFeedLine(1);
            // textData.append("\t\t\bHotel Shail\n");
            // textData.append("\tKokanwadi Railway St. Road\n");
            // textData.append("\t\tAurangabad\n\n");

            textData.append("Item Wise Sales Summary\n");
            textData.append("Date : " + date + "\n");

            for (int i = 0; i < 45; i++) {
                textData.append("-");
            }

            textData.append("\n");

            textData.append("Qty   ");

            textData.append("Item Name");
            for (int i = 0; i < 18; i++) {
                textData.append(" ");
            }

            textData.append("      Amount\n");

            for (int i = 0; i < 45; i++) {
                textData.append("-");
            }
            textData.append("\n");

            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.COLOR_1);

            double grandTotal = 0;
            for (int i = 0; i < reportArray.size(); i++) {

                String catName = "\bGroup Name : " + reportArray.get(i).getCatName();
                textData.append(catName);
                int difference = 45 - catName.length();
                for (int d = 0; d < difference; d++) {
                    textData.append(" ");
                }
                textData.append("\n");

                double subTotal = 0;
                for (int j = 0; j < reportArray.get(i).getBillList().size(); j++) {

                    Bill bill = reportArray.get(i).getBillList().get(j);

                    subTotal = subTotal + bill.getTotal();
                    grandTotal = grandTotal + bill.getTotal();

                    String qty = String.valueOf(bill.getQuantity());

                    difference = 3 - qty.length();
                    for (int d = 0; d < difference; d++) {
                        textData.append(" ");
                    }
                    textData.append(qty + "   ");

                    String strName = bill.getItemName();
                    if (strName.length() >= 27) {
                        String itemName = bill.getItemName().substring(0, 27);
                        textData.append(itemName);

                    } else if (strName.length() < 27) {
                        textData.append(strName);
                        difference = 27 - strName.length();

                        for (int d = 0; d < difference; d++) {
                            textData.append(" ");
                        }
                    }
                    textData.append("   ");

                    String amt = String.valueOf(bill.getTotal());
                    if (amt.length() >= 9) {
                        String amtStr = amt.substring(0, 9);
                        textData.append(amtStr);
                    } else if (amt.length() < 9) {
                        difference = 9 - amt.length();

                        for (int d = 0; d < difference; d++) {
                            textData.append(" ");
                        }

                        textData.append(amt + "\n");
                    }

                }

                for (int d = 0; d < 30; d++) {
                    textData.append(" ");
                }
                for (int d = 0; d < 15; d++) {
                    textData.append("-");
                }

                textData.append("\n");

                String labelTotal = "Total : ";
                difference = 30 - labelTotal.length();
                for (int d = 0; d < difference; d++) {
                    textData.append(" ");
                }
                textData.append(labelTotal);

                String subTotalStr = String.format("%.1f", subTotal);
                difference = 15 - subTotalStr.length();
                for (int d = 0; d < difference; d++) {
                    textData.append(" ");
                }
                textData.append(subTotalStr + "\n");


                textData.append("\n");


            }

            for (int i = 0; i < 45; i++) {
                textData.append("-");
            }
            textData.append("\n");


            String labelTotal = "Grand Total : ";
            int difference = 30 - labelTotal.length();
            for (int d = 0; d < difference; d++) {
                textData.append(" ");
            }
            textData.append(labelTotal);

            String subTotalStr = String.format("%.1f", grandTotal);
            difference = 15 - subTotalStr.length();
            for (int d = 0; d < difference; d++) {
                textData.append(" ");
            }
            textData.append(subTotalStr + "\n");

            for (int i = 0; i < 45; i++) {
                textData.append("-");
            }
            textData.append("\n");

            mPrinter.addText(textData.toString());
            Log.e("Print ", "\n\n" + textData.toString());

            mPrinter.addCut(Printer.CUT_FEED);


        } catch (Exception e) {
            e.printStackTrace();
            ShowMsg.showException(e, method, activity, false);
            return false;
        }
        return true;
    }

    public boolean runPrintReceiptSequence() {
        try {
            if (!initializeObject()) {
                return false;
            }

            if (!createReceiptData()) {
                finalizeObject();
                return false;
            }

            if (!printData()) {
                finalizeObject();
                return false;
            }

        } catch (Exception e) {
        }

        return true;
    }

    private boolean printData() {
        if (mPrinter == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }

        PrinterStatusInfo status = mPrinter.getStatus();
        if (!isPrintable(status)) {
            ShowMsg.showMsg(makeErrorMessage(status), activity, false);
            try {
                mPrinter.disconnect();
            } catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        try {
            mPrinter.sendData(Printer.PARAM_DEFAULT);
        } catch (Exception e) {
            ShowMsg.showException(e, "sendData", activity, false);
            try {
                mPrinter.disconnect();
            } catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        return true;
    }

    private boolean initializeObject() {
        try {
            mPrinter = new Printer(modelConstant,
                    Printer.MODEL_ANK,
                    activity);
        } catch (UnsatisfiedLinkError e) {
            Log.e("UnsatisfiedLinkError", "-----initializeObject" + e.getMessage());
            Toast.makeText(activity, "Please Check Printer IP, Printer Must Be In Same Network", Toast.LENGTH_SHORT).show();
        } catch (NoClassDefFoundError e) {
            Log.e("NoClassDefFoundError", "-----initializeObject" + e.getMessage());
            Toast.makeText(activity, "Please Check Printer IP, Printer Must Be In Same Network", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("initializeObject", "-----------------------------------------------------------------");
            ShowMsg.showException(e, "Printer", activity, false);
            return false;
        }

        mPrinter.setReceiveEventListener(this);
        return true;
    }

    /**
     * Release all printer resources
     */
    private void finalizeObject() {
        if (mPrinter == null) {
            return;
        }
        mPrinter.clearCommandBuffer();
        mPrinter.setReceiveEventListener(null);
        mPrinter = null;
    }

    private boolean connectPrinter() {
        Log.e("connectPrinter", "----------------------------");
        boolean isBeginTransaction = false;

        if (mPrinter == null) {
            return false;
        }

        try {
            Log.e("connectPrinter", "----------------------------printerAddress" + printerAddress);
            mPrinter.connect(printerAddress, Printer.PARAM_DEFAULT);
        } catch (Exception e) {
            Log.e("connectPrinter", "----------------------------showException" + e.getMessage());
            e.printStackTrace();
            Log.e("connectPrinter", "---------------------------------------------------------------------------");
            ShowMsg.showException(e, "connect", activity, false);
            return false;
        }

        try {
            mPrinter.beginTransaction();
            isBeginTransaction = true;
        } catch (Exception e) {
            Log.e("connectPrinter", "----------------------------Exception");
            Log.e("connectPrinter", "beginTransaction---------------------------------------------------------------------------");
            ShowMsg.showException(e, "beginTransaction", activity, false);
        }

        if (isBeginTransaction == false) {
            try {
                mPrinter.disconnect();
            } catch (Epos2Exception e) {
                // Do nothing
                return false;
            }
        }

        return true;
    }

    private void disconnectPrinter() {
        if (mPrinter == null) {
            return;
        }

        try {
            mPrinter.endTransaction();
        } catch (final Exception e) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    Log.e("disconnectPrinter", "endTransaction---------------------------------------------------------------------------");
                    ShowMsg.showException(e, "endTransaction", activity, false);
                }
            });
        }

        try {
            mPrinter.disconnect();
        } catch (final Exception e) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    Log.e("disconnectPrinter", "---------------------------------------------------------------------------");
                    ShowMsg.showException(e, "disconnect", activity, false);
                }
            });
        }

        finalizeObject();
    }

    private boolean isPrintable(PrinterStatusInfo status) {
        if (status == null) {
            return false;
        }

        if (status.getConnection() == Printer.FALSE) {
            return false;
        } else if (status.getOnline() == Printer.FALSE) {
            return false;
        } else {
            ;//print available
        }

        return true;
    }

    private String makeErrorMessage(PrinterStatusInfo status) {
        String msg = "";

        if (status.getOnline() == Printer.FALSE) {
            msg += activity.getResources().getString(R.string.handlingmsg_err_offline);
        }
        if (status.getConnection() == Printer.FALSE) {
            msg += activity.getResources().getString(R.string.handlingmsg_err_no_response);
        }
        if (status.getCoverOpen() == Printer.TRUE) {
            msg += activity.getResources().getString(R.string.handlingmsg_err_cover_open);
        }
        if (status.getPaper() == Printer.PAPER_EMPTY) {
            msg += activity.getResources().getString(R.string.handlingmsg_err_receipt_end);
        }
        if (status.getPaperFeed() == Printer.TRUE || status.getPanelSwitch() == Printer.SWITCH_ON) {
            msg += activity.getResources().getString(R.string.handlingmsg_err_paper_feed);
        }
        if (status.getErrorStatus() == Printer.MECHANICAL_ERR || status.getErrorStatus() == Printer.AUTOCUTTER_ERR) {
            msg += activity.getResources().getString(R.string.handlingmsg_err_autocutter);
            msg += activity.getResources().getString(R.string.handlingmsg_err_need_recover);
        }
        if (status.getErrorStatus() == Printer.UNRECOVER_ERR) {
            msg += activity.getResources().getString(R.string.handlingmsg_err_unrecover);
        }
        if (status.getErrorStatus() == Printer.AUTORECOVER_ERR) {
            if (status.getAutoRecoverError() == Printer.HEAD_OVERHEAT) {
                msg += activity.getResources().getString(R.string.handlingmsg_err_overheat);
                msg += activity.getResources().getString(R.string.handlingmsg_err_head);
            }
            if (status.getAutoRecoverError() == Printer.MOTOR_OVERHEAT) {
                msg += activity.getResources().getString(R.string.handlingmsg_err_overheat);
                msg += activity.getResources().getString(R.string.handlingmsg_err_motor);
            }
            if (status.getAutoRecoverError() == Printer.BATTERY_OVERHEAT) {
                msg += activity.getResources().getString(R.string.handlingmsg_err_overheat);
                msg += activity.getResources().getString(R.string.handlingmsg_err_battery);
            }
            if (status.getAutoRecoverError() == Printer.WRONG_PAPER) {
                msg += activity.getResources().getString(R.string.handlingmsg_err_wrong_paper);
            }
        }
        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_0) {
            msg += activity.getResources().getString(R.string.handlingmsg_err_battery_real_end);
        }

        return msg;
    }

    @Override
    public void onPtrReceive(final Printer printerObj, final int code, final PrinterStatusInfo status, final String printJobId) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                Log.e("onPtrReceive", "---------------------------------------------------------------------------");
                ShowMsg.showResult(code, makeErrorMessage(status), activity, false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        disconnectPrinter();
                    }
                }).start();
            }
        });
    }
}
