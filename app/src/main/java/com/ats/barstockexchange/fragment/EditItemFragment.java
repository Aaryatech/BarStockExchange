package com.ats.barstockexchange.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.barstockexchange.R;
import com.ats.barstockexchange.bean.CategoryNameDisplay;
import com.ats.barstockexchange.bean.ErrorMessage;
import com.ats.barstockexchange.bean.Item;
import com.ats.barstockexchange.util.CheckNetwork;
import com.ats.barstockexchange.util.InterfaceApi;
import com.ats.barstockexchange.util.StoreCameraOrGalleryData;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditItemFragment extends Fragment implements View.OnClickListener {

    private ArrayList<String> catNameArray = new ArrayList<>();
    private ArrayList<Integer> catIdArray = new ArrayList<>();
    private Spinner spCategory;

    private Button btnUpdate;
    private EditText edItemName, edDesc, edStock, edOpenRate, edMrpGame, edMrpReg, edMrpSpecial, edMaxRate, edMinRate, edSgst, edCgst, edMinStock;
    private TextView tvCatId;
    private ImageView ivViewImage, ivClick;
    private CheckBox cbMixer;
    int userId;

    private static final int GALLERY_PICTURE = 101;
    private static final int CAMERA_PICTURE = 102;
    AlertDialog.Builder builder;
    String imageEncoded_pic;
    File folder = new File(Environment.getExternalStorageDirectory() + File.separator, "BSEData");
    File f;
    Bitmap bitmap_pic;
    String selectedImagePath_Pic;

    String bName, bDesc, bImage;
    Float bOpenRate, bGame, bReg, bSpecial, bMin, bMax, bSgst, bCgst;
    int bMixer, bCurrStock, bMinStock, bCatId, bItemId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_item, container, false);
        try {
            SharedPreferences pref = getContext().getSharedPreferences(InterfaceApi.MY_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            userId = pref.getInt("UserId", 0);
            String userType = pref.getString("UserType", "");

            bItemId = getArguments().getInt("ItemId");
            bCatId = getArguments().getInt("CatId");
            bName = getArguments().getString("ItemName");
            bDesc = getArguments().getString("ItemDesc");
            bImage = getArguments().getString("ItemImage");
            bMixer = getArguments().getInt("IsMixer");
            bCurrStock = getArguments().getInt("CurrentStock");
            bMinStock = getArguments().getInt("MinStock");
            bOpenRate = getArguments().getFloat("OpenRate");
            bGame = getArguments().getFloat("GameRate");
            bReg = getArguments().getFloat("RegRate");
            bSpecial = getArguments().getFloat("SpecialRate");
            bMin = getArguments().getFloat("MinRate");
            bMax = getArguments().getFloat("MaxRate");
            bSgst = getArguments().getFloat("SGST");
            bCgst = getArguments().getFloat("CGST");

        } catch (Exception e) {
        }

        Log.e("IS MIXER : ","---------------------------------------"+bMixer);

        spCategory = view.findViewById(R.id.spEditItem_Category);
        tvCatId = view.findViewById(R.id.tvEditItem_CatId);
        btnUpdate = view.findViewById(R.id.btnEditItem_Update);

        createFolder();

        btnUpdate.setOnClickListener(this);

        edItemName = view.findViewById(R.id.edEditItem_Name);
        edDesc = view.findViewById(R.id.edEditItem_Desc);
        edStock = view.findViewById(R.id.edEditItem_Stock);
        edOpenRate = view.findViewById(R.id.edEditItem_OpeningRate);
        edMrpGame = view.findViewById(R.id.edEditItem_GameMRP);
        edMrpReg = view.findViewById(R.id.edEditItem_RegularMRP);
        edMrpSpecial = view.findViewById(R.id.edEditItem_SpecialMRP);
        edMaxRate = view.findViewById(R.id.edEditItem_MaxRate);
        edMinRate = view.findViewById(R.id.edEditItem_MinRate);
        edSgst = view.findViewById(R.id.edEditItem_SGST);
        edCgst = view.findViewById(R.id.edEditItem_CGST);
        edMinStock = view.findViewById(R.id.edEditItem_MinStock);

        ivViewImage = view.findViewById(R.id.ivEditItem_ViewImage);
        ivClick = view.findViewById(R.id.ivEditItem_ClickImage);
        ivClick.setOnClickListener(this);

        cbMixer = view.findViewById(R.id.cbEditItem_Mixer);

        getCategoryList();


        edItemName.setText("" + bName);
        edDesc.setText("" + bDesc);
        edStock.setText("" + bCurrStock);
        edOpenRate.setText("" + bOpenRate);
        edMrpGame.setText("" + bGame);
        edMrpReg.setText("" + bReg);
        edMrpSpecial.setText("" + bSpecial);
        edMaxRate.setText("" + bMax);
        edMinRate.setText("" + bMin);
        edSgst.setText("" + bSgst);
        edCgst.setText("" + bCgst);
        edMinStock.setText("" + bMinStock);
        tvCatId.setText("" + bCatId);

        try{
            Picasso.with(getContext())
                    .load(InterfaceApi.IMAGE_PATH+""+bImage)
                    .placeholder(R.drawable.bottle_a)
                    .error(R.drawable.bottle_a)
                    .into(ivViewImage);
        }catch(Exception e){}

        if (bMixer == 1) {
            cbMixer.setChecked(true);
        } else {
            cbMixer.setChecked(false);
        }


        return view;
    }

    public void getCategoryList() {
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
            final Call<CategoryNameDisplay> categoryNameList = api.getCategoryNameList();

            final Dialog progressDialog = new Dialog(getContext());
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setContentView(R.layout.loading_progress_layout);
            progressDialog.show();


            categoryNameList.enqueue(new Callback<CategoryNameDisplay>() {
                @Override
                public void onResponse(Call<CategoryNameDisplay> call, Response<CategoryNameDisplay> response) {
                    try {
                        if (response.body() != null) {
                            CategoryNameDisplay data = response.body();
                            if (data.getErrorMessage().getError()) {
                                progressDialog.dismiss();
                                Log.e("ON RESPONSE : ", " ERROR : " + data.getErrorMessage().getMessage());
                                Toast.makeText(getContext(), "unable to fetch data", Toast.LENGTH_SHORT).show();
                            } else {
                                catNameArray.clear();
                                catIdArray.clear();
                                catNameArray.add("select Category");
                                catIdArray.add(0);
                                for (int i = 0; i < data.getCategoryNameList().size(); i++) {
                                    catNameArray.add(data.getCategoryNameList().get(i).getName());
                                    catIdArray.add(data.getCategoryNameList().get(i).getId());
                                }

                                Log.e("RESPONSE : ", " DATA : " + catNameArray);
//                                ArrayAdapter<String> spAdapter = new ArrayAdapter<String>(getContext(), R.layout.custom_spinner_layout, catNameArray);
//                                spCategory.setAdapter(spAdapter);

                                Log.e("catNameArray : ", "" + catNameArray);
                                Log.e("catIdArray : ", "" + catIdArray);

                                int pos = -1;
                                for (int i = 0; i < catIdArray.size(); i++) {
                                    if (bCatId == catIdArray.get(i)) {
                                        pos = i;
                                    }
                                }

                                ArrayAdapter<String> adapterStr = new ArrayAdapter<String>(getContext(), R.layout.custom_spinner_layout, catNameArray);
                                // adapterStr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spCategory.setAdapter(adapterStr);
                                if (pos >= 0) {
                                    spCategory.setSelection(pos);
                                }

                                spinnerListener();

                                progressDialog.dismiss();

                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "no categories found", Toast.LENGTH_SHORT).show();
                            Log.e("RESPONSE : ", " NO DATA");
                        }
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Log.e("Exception : ", "" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<CategoryNameDisplay> call, Throwable t) {
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

    public void EditItem(int itemId, String itemName, String itemDesc, String image, float mrpGame, float mrpReg, float mrpSpecial, float openRate, float maxRate, float minRate, int currStock, int catId, float sgst, float cgst, int isMixer, int userId, int minStock) {
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

            Item item = new Item(itemId, itemName, itemDesc, image, mrpGame, mrpReg, mrpSpecial, openRate, maxRate, minRate, currStock, catId, sgst, cgst, isMixer, userId, "", 0, minStock);
            Call<ErrorMessage> errorMessageCall = api.editItem(item);

            final Dialog progressDialog = new Dialog(getContext());
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
                            if (data.getError()) {
                                progressDialog.dismiss();
                                Log.e("ON RESPONSE : ", "ERROR : " + data.getMessage());
                                Toast.makeText(getContext(), "Unable To Save", Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.content_frame, new ItemMasterFragment(), "HomeFragment");
                                ft.commit();
                            }

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Unable To Save", Toast.LENGTH_SHORT).show();

                            Log.e("ON RESPONSE : ", "NO DATA");
                        }
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Unable To Save", Toast.LENGTH_SHORT).show();
                        Log.e("Exception : ", "" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<ErrorMessage> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Unable To Save", Toast.LENGTH_SHORT).show();
                    Log.e("ON FAILURE : ", "ERROR : " + t.getMessage());
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
        if (view.getId() == R.id.btnEditItem_Update) {
            if (bItemId <= 0) {
                Toast.makeText(getContext(), "Please Select Item Again", Toast.LENGTH_SHORT).show();
            } else if (spCategory.getSelectedItemPosition() == 0) {
                Toast.makeText(getContext(), "Please Select Category", Toast.LENGTH_SHORT).show();
                spCategory.requestFocus();
            } else if (edItemName.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Please Enter Item Name", Toast.LENGTH_SHORT).show();
                edItemName.requestFocus();
            }
//            else if (edDesc.getText().toString().isEmpty()) {
//                Toast.makeText(getContext(), "Please Enter Description", Toast.LENGTH_SHORT).show();
//                edDesc.requestFocus();
//            }
            else if (edStock.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Please Enter Stock", Toast.LENGTH_SHORT).show();
                edStock.requestFocus();
            } else if (edMinStock.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Please Enter Minimum Stock", Toast.LENGTH_SHORT).show();
                edMinStock.requestFocus();
            }
//            else if (edOpenRate.getText().toString().isEmpty()) {
//                Toast.makeText(getContext(), "Please Enter Opening Rate", Toast.LENGTH_SHORT).show();
//                edOpenRate.requestFocus();
//            }
            else if (edMrpGame.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Please Enter Game Rate", Toast.LENGTH_SHORT).show();
                edMrpGame.requestFocus();
            } else if (edMrpReg.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Please Enter Regular Rate", Toast.LENGTH_SHORT).show();
                edMrpReg.requestFocus();
            } else if (edMrpSpecial.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Please Enter Special Rate", Toast.LENGTH_SHORT).show();
                edMrpSpecial.requestFocus();
            } else if (edMinRate.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Please Enter Minimum Rate", Toast.LENGTH_SHORT).show();
                edMinRate.requestFocus();
            } else if (edMaxRate.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Please Enter Maximum Rate", Toast.LENGTH_SHORT).show();
                edMaxRate.requestFocus();
            } else if (edSgst.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Please Enter SGST Tax", Toast.LENGTH_SHORT).show();
                edSgst.requestFocus();
            } else if (edCgst.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Please Enter CGST Tax", Toast.LENGTH_SHORT).show();
                edCgst.requestFocus();
            }  else {

                int mixer = 1;
                if (cbMixer.isChecked()) {
                    mixer = 1;
                } else {
                    mixer = 0;
                }

                float game = Float.parseFloat(edMrpGame.getText().toString());
                float reg = Float.parseFloat(edMrpReg.getText().toString());
                float special = Float.parseFloat(edMrpSpecial.getText().toString());
                float min = Float.parseFloat(edMinRate.getText().toString());
                float max = Float.parseFloat(edMaxRate.getText().toString());
                float open = Float.parseFloat(edMaxRate.getText().toString());
                float sgstTax = Float.parseFloat(edSgst.getText().toString());
                float cgstTax = Float.parseFloat(edCgst.getText().toString());
                int stock = Integer.parseInt(edStock.getText().toString());
                int minStock = Integer.parseInt(edMinStock.getText().toString());
                int cId = Integer.parseInt(tvCatId.getText().toString());

                EditItem(bItemId, edItemName.getText().toString(), edDesc.getText().toString(), imageEncoded_pic, game, reg, special, open, max, min, stock, cId, sgstTax, cgstTax, mixer, userId, minStock);
            }
        } else if (view.getId() == R.id.ivEditItem_ClickImage) {
            startDialog(CAMERA_PICTURE, GALLERY_PICTURE);
        }
    }

    public void spinnerListener() {
        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tvCatId.setText("" + catIdArray.get(i));
                Log.e("CAT ID : ", "" + tvCatId.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    public void startDialog(final int camera, final int gallery) {
        builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        builder.setTitle("Choose");
        builder.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent pictureActionIntent = null;
                pictureActionIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pictureActionIntent, gallery);
            }
        });
        builder.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    f = new File(folder + File.separator, "Camera.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(intent, camera);
                } catch (Exception e) {
                    Log.e("select camera : ", " Exception : " + e.getMessage());
                }
            }
        });
        builder.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bitmap_pic = null;

        if (resultCode == getActivity().RESULT_OK && requestCode == CAMERA_PICTURE) {
            try {
                //bitmap_Emp = BitmapFactory.decodeFile(f.getAbsolutePath());
                bitmap_pic = StoreCameraOrGalleryData.ShrinkBitmap(f.getAbsolutePath(), 1280, 720);
                ivViewImage.setImageBitmap(bitmap_pic);

                File outputFile = new File(folder + File.separator, "Item_" + currentDateFormat() + ".jpeg");
                //Log.e("PATH : ", outputFile.getAbsolutePath());

                ByteArrayOutputStream stream = new ByteArrayOutputStream(1000);
                bitmap_pic.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                imageEncoded_pic = Base64.encodeToString(byteArray, Base64.DEFAULT);
                //Log.e("Array  : ", "" + imageEncoded_Emp);

                //tvPhoto.setText(outputFile.getName());

                FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                bitmap_pic.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (resultCode == getActivity().RESULT_OK && requestCode == GALLERY_PICTURE) {
            try {
                if (data != null) {
                    //Toast.makeText(getActivity().getApplicationContext(), "Result OK  " + RESULT_OK + "Request Code  " + requestCode, Toast.LENGTH_LONG).show();
                    Uri selectedImage = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = getActivity().getContentResolver().query(selectedImage, filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    selectedImagePath_Pic = c.getString(columnIndex);
                    c.close();

                    //bitmap_Emp = BitmapFactory.decodeFile(selectedImagePath_Emp); // load
                    bitmap_pic = StoreCameraOrGalleryData.ShrinkBitmap(selectedImagePath_Pic, 1280, 720);
                    ivViewImage.setImageBitmap(bitmap_pic);

                    File outputFile = new File(folder + File.separator, "Item_" + currentDateFormat() + ".jpeg");
                    //Log.e("PATH : ", outputFile.getAbsolutePath());
                    // tvPhoto.setText(outputFile.getName());

                    ByteArrayOutputStream stream = new ByteArrayOutputStream(1000);
                    bitmap_pic.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    imageEncoded_pic = Base64.encodeToString(byteArray, Base64.DEFAULT);

                    FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                    bitmap_pic.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String currentDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String currentTimeStamp = dateFormat.format(new Date());
        return currentTimeStamp;
    }

    public void createFolder() {
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

}
