package com.ats.barstockexchange.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.barstockexchange.R;
import com.ats.barstockexchange.bean.PrintInfo;
import com.ats.barstockexchange.util.PrintHelper;
import com.ats.barstockexchange.util.PrintReceiptType;
import com.ats.barstockexchange.util.ShowMsg;
import com.ats.barstockexchange.util.SpnModelsItem;
import com.epson.epos2.Log;
import com.epson.epos2.printer.Printer;

public class PrintActivity extends AppCompatActivity implements View.OnClickListener{

    PrintInfo printInfo;
    private Context mContext = null;
    private Spinner mSpnSeries = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);

        mContext = this;
        int[] target = {
                R.id.btnDiscovery,
                R.id.btnSampleReceipt,
        };

        for (int i = 0; i < target.length; i++)
        {
            Button button = (Button) findViewById(target[i]);
            button.setOnClickListener(this);
        }

        mSpnSeries = (Spinner) findViewById(R.id.spnModel);
        ArrayAdapter<SpnModelsItem> seriesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        seriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_m10), Printer.TM_M10));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_m30), Printer.TM_M30));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_p20), Printer.TM_P20));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_p60), Printer.TM_P60));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_p60ii), Printer.TM_P60II));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_p80), Printer.TM_P80));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t20), Printer.TM_T20));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t70), Printer.TM_T70));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t81), Printer.TM_T81));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t82), Printer.TM_T82));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t83), Printer.TM_T83));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t88), Printer.TM_T88));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t90), Printer.TM_T90));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t90kp), Printer.TM_T90KP));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_u220), Printer.TM_U220));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_u330), Printer.TM_U330));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_l90), Printer.TM_L90));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_h6000), Printer.TM_H6000));
        mSpnSeries.setAdapter(seriesAdapter);
        mSpnSeries.setSelection(0);

        try
        {
            Log.setLogSettings(mContext, Log.PERIOD_TEMPORARY, Log.OUTPUT_STORAGE, null, 0, 1, Log.LOGLEVEL_LOW);
        }
        catch (Exception e)
        {
            ShowMsg.showException(e, "setLogSettings", mContext,false);
        }
        setSelectedPrinter();

    }

    private void setSelectedPrinter()
    {
        try
        {
            //printInfo = new PrintInfo().getCurrentPrinter(DatabaseHelper.getInstance(PrintActivity.this));
            if (printInfo != null)
            {

                ((TextView) findViewById(R.id.PrinterName)).setText(""+printInfo.getPrintName());
                ((TextView) findViewById(R.id.Target)).setText(""+printInfo.getPrintAddress());
            }
            else
            {
                ((TextView) findViewById(R.id.PrinterName)).setText("");
                ((TextView) findViewById(R.id.Target)).setText("");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data)
    {
        if (data != null && resultCode == RESULT_OK)
        {
            PrintInfo printInfo = new PrintInfo();
            String target = data.getStringExtra("Target");
            if (target != null)
            {
                printInfo.setPrintAddress(target);
            }
            String name = data.getStringExtra("PrinterName");
            if (name != null)
            {
                printInfo.setPrintName(name);
            }
            printInfo.setModel(((SpnModelsItem) mSpnSeries.getSelectedItem()).getModelConstant());
            //insert into table
            //printInfo.insertOrUpdate(DatabaseHelper.getInstance(PrintActivity.this));
            setSelectedPrinter();
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId())
        {
            case R.id.btnDiscovery:
                intent = new Intent(this, DiscoveryActivity.class);
                startActivityForResult(intent, 0);
                break;

            case R.id.btnSampleReceipt:
                if (printInfo != null)
                {
                    PrintHelper printHelper = new PrintHelper(this, printInfo.getPrintAddress(), printInfo.getModel(), PrintReceiptType.TEST);
                    if (printHelper.runPrintReceiptSequence())
                    {
                        //print is successful
                        android.util.Log.e("","Print successfully");
                        Toast.makeText(PrintActivity.this, "Print successfully", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        //failed to print
                        android.util.Log.e("","Failed to print");
                        Toast.makeText(PrintActivity.this, "Print successfully", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    ShowMsg.showMsg("Please configure printer first", this,false);
                }
                break;

            default:
                // Do nothing
                break;
        }
    }
}
