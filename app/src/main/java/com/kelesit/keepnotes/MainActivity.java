package com.kelesit.keepnotes;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //static ArrayList<Bitmap> noteImg;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_notes_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.add_note){
            Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
            intent.putExtra("info", "new");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.listView);

        final ArrayList<String> noteName = new ArrayList<>();
        //noteImg = new ArrayList<>();

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, noteName);
        listView.setAdapter(arrayAdapter);

        try{

            Main2Activity.database = this.openOrCreateDatabase("Notes",MODE_PRIVATE,null);
            Main2Activity.database.execSQL("CREATE TABLE IF NOT EXISTS notes (name VARCHAR, image BLOB)");  //Resim için blob

            Cursor cursor = Main2Activity.database.rawQuery("SELECT * FROM notes", null);
            int nameIx = cursor.getColumnIndex("name"); //Cursor column index i döndürür
            int imageIx = cursor.getColumnIndex("image");

            cursor.moveToFirst(); //İlkinden başla

            while (cursor!=null){ //Sona gelene kadar devam eder
                noteName.add(cursor.getString(nameIx)); //Name listesine gelen değeri ekler

                byte[] byteArrayImg = cursor.getBlob(imageIx);
                Bitmap image = BitmapFactory.decodeByteArray(byteArrayImg,0,byteArrayImg.length); //Alınan byte ı bitmape çevir
                //noteImg.add(image);

                cursor.moveToNext();
                arrayAdapter.notifyDataSetChanged();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                intent.putExtra("info", "old");
                intent.putExtra("name", noteName.get(position));
                intent.putExtra("position", position);

                startActivity(intent);
            }
        });
    }
}
