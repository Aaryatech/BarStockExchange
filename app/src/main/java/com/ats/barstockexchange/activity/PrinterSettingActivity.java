package com.ats.barstockexchange.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ats.barstockexchange.R;
import com.ats.barstockexchange.util.InterfaceApi;

public class PrinterSettingActivity extends AppCompatActivity {

    private Button btnSave;
    private EditText edIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer_setting);

      /*  edIp = findViewById(R.id.edIP);
        btnSave = findViewById(R.id.btnPrinterSave);

        try {
            SharedPreferences pref = PrinterSettingActivity.this.getSharedPreferences(InterfaceApi.MY_PREF, Context.MODE_PRIVATE);
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
                    Toast.makeText(PrinterSettingActivity.this, "Please Insert Ip", Toast.LENGTH_SHORT).show();
                    edIp.requestFocus();
                } else {
                    String ip = "TCP:" + edIp.getText().toString();

                    SharedPreferences pref = getApplicationContext().getSharedPreferences(InterfaceApi.MY_PREF, MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("IP", ip);
                    editor.apply();

                }
            }
        });
*/
    }
}
