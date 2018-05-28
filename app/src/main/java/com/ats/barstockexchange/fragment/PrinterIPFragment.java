package com.ats.barstockexchange.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ats.barstockexchange.R;
import com.ats.barstockexchange.activity.PrinterSettingActivity;
import com.ats.barstockexchange.util.InterfaceApi;
import com.ats.barstockexchange.util.PrintHelper;
import com.ats.barstockexchange.util.PrintReceiptType;

import static android.content.Context.MODE_PRIVATE;

public class PrinterIPFragment extends Fragment {

    private Button btnSave, btnSample;
    private EditText edIp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_printer_i, container, false);

        edIp = view.findViewById(R.id.edIP);
        btnSave = view.findViewById(R.id.btnPrinterSave);
        btnSample = view.findViewById(R.id.btnPrinterTest);

        try {
            SharedPreferences pref = getActivity().getSharedPreferences(InterfaceApi.MY_PREF, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            String tempIp = pref.getString("IP", "");

            String ip = tempIp.substring(4);

            edIp.setText("" + ip);


        } catch (Exception e) {
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edIp.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Please Insert Ip", Toast.LENGTH_SHORT).show();
                    edIp.requestFocus();
                } else {
                    String ip = "TCP:" + edIp.getText().toString();

                    SharedPreferences pref = getActivity().getSharedPreferences(InterfaceApi.MY_PREF, MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("IP", ip);
                    editor.apply();

                    Toast.makeText(getActivity(), "SUCCESS", Toast.LENGTH_SHORT).show();

                }
            }
        });

        btnSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = getActivity().getSharedPreferences(InterfaceApi.MY_PREF, MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                String ip = pref.getString("IP", "");

                try {
                    PrintHelper printHelper = new PrintHelper(getActivity(), ip, 9, PrintReceiptType.TEST);
                    printHelper.runPrintReceiptSequence();
                } catch (Exception e) {
                }

            }
        });

        return view;
    }

}
