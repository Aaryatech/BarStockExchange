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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ats.barstockexchange.R;
import com.ats.barstockexchange.bean.ErrorMessage;
import com.ats.barstockexchange.bean.News;
import com.ats.barstockexchange.util.CheckNetwork;
import com.ats.barstockexchange.util.InterfaceApi;
import com.ats.barstockexchange.util.PermissionsUtil;
import com.ats.barstockexchange.util.StoreCameraOrGalleryData;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.ats.barstockexchange.activity.HomeActivity.tvTitle;

public class AddNewsFragment extends Fragment implements View.OnClickListener {

    private EditText edTitle, edDesc;
    private ImageView ivClick, ivViewImage;
    private Button btnSave, btnReset;

    private static final int GALLERY_PICTURE = 101;
    private static final int CAMERA_PICTURE = 102;
    AlertDialog.Builder builder;
    String imageEncoded_pic;
    File folder = new File(Environment.getExternalStorageDirectory() + File.separator, "BSEData");
    File f;
    Bitmap bitmap_pic;
    String selectedImagePath_Pic;

    int userId;
    String userType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_news, container, false);
        tvTitle.setText("Add News");

        try {
            SharedPreferences pref = getContext().getSharedPreferences(InterfaceApi.MY_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            userId = pref.getInt("UserId", 0);
            userType = pref.getString("UserType", "");

        } catch (Exception e) {
        }

        if (PermissionsUtil.checkAndRequestPermissions(getActivity())) {

        }

        createFolder();

        edTitle = view.findViewById(R.id.edAddNews_Title);
        edDesc = view.findViewById(R.id.edAddNews_Desc);

        btnSave = view.findViewById(R.id.btnAddNews_Save);
        btnReset = view.findViewById(R.id.btnAddNews_Reset);

        btnReset.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        ivClick = view.findViewById(R.id.ivAddNews_ClickImage);
        ivViewImage = view.findViewById(R.id.ivAddNews_ViewImage);


        return view;
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

                File outputFile = new File(folder + File.separator, "News_" + currentDateFormat() + ".jpeg");
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

                    File outputFile = new File(folder + File.separator, "News_" + currentDateFormat() + ".jpeg");
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


    public void addNews(String title, String image, String desc, int userId) {

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

            News news = new News(title, image, desc, "", 0, userId, "");
            Call<ErrorMessage> errorMessageCall = api.addNews(news);

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
                                ft.replace(R.id.content_frame, new NewsMasterFragment(), "HomeFragment");
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnAddNews_Save) {
            if (edTitle.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Please Enter Title", Toast.LENGTH_SHORT).show();
                edTitle.requestFocus();
            } else if (edDesc.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Please Enter Description", Toast.LENGTH_SHORT).show();
                edDesc.requestFocus();
            } else {
                String title = edTitle.getText().toString();
                String desc = edDesc.getText().toString();
                addNews(title, imageEncoded_pic, desc, userId);
            }

        } else if (v.getId() == R.id.btnAddNews_Reset) {
            edTitle.setText("");
            edDesc.setText("");
            ivViewImage.setImageResource(0);
            edTitle.requestFocus();

        }
    }
}
