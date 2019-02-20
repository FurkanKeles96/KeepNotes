package com.kelesit.keepnotes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Main2Activity extends AppCompatActivity {

    ImageView imageView;
    EditText editText;
    static SQLiteDatabase database;
    Bitmap selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        imageView = findViewById(R.id.imageView);
        editText = findViewById(R.id.editText);
        Button btnSaveImg = findViewById(R.id.btnSave);
        Button btnSelectImg = findViewById(R.id.btnSelectImg);

        Intent intent = getIntent();

        String info = intent.getStringExtra("info");

        if(info.equalsIgnoreCase("new")){
            Bitmap background = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_launcher_background);
            btnSaveImg.setVisibility(View.VISIBLE);
            btnSelectImg.setVisibility(View.VISIBLE);
            editText.setText("");
            imageView.setImageBitmap(background);
        }
        else{
            btnSaveImg.setVisibility(View.INVISIBLE);
            btnSelectImg.setVisibility(View.INVISIBLE);
        }
    }


    public void selectImage(View view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){ //İzin yoksa
                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},2); //İzin iste
            }
            else{
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==2){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

            Uri selectedImg = data.getData();

            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImg);
                imageView.setImageBitmap(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void save(View view) {

        String noteName = editText.getText().toString();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();       //Resmi sıkıştırıp byte array yaptık.
        selectedImage.compress(Bitmap.CompressFormat.PNG,50,outputStream); //Database'e o şekilde eklenecek
        byte[] byteArrayImg = outputStream.toByteArray();

        try{
            database=this.openOrCreateDatabase("Notes", MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS notes (name VARCHAR, image BLOB)");  //Resim için blob

            String sqlStr = "INSERT INTO notes (name, image) VALUES (?,?)"; //değişken veri tabanına kaydetmek için önce bir string
            SQLiteStatement statement = database.compileStatement(sqlStr); //statement in içine o string i verdik
            statement.bindString(1,noteName); //Stringteki ilk ? yerine noteName değerini koy
            statement.bindBlob(2,byteArrayImg); //Stringteki ikinci ? yerine byteArray yaptığımız resmi koy
            statement.execute();


        }catch (Exception e){
            e.printStackTrace();
        }

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);


    }

}
