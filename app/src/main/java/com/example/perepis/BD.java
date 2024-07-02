package com.example.perepis;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class BD extends AppCompatActivity {


    private DatabaseHelper db, dbHelper;
    private LinearLayout moviesLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bd);

        displayPeopleData();


    }

    public void displayPeopleData() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<String> fioList = dbHelper.geFIO();
        List<String> genderList = dbHelper.getPOL();
        List<String> ageList = dbHelper.getAGE();
        List<byte[]> imageList = dbHelper.getImagesAsBytes();

        LinearLayout linearLayout = findViewById(R.id.dd); // Получаем ссылку на LinearLayout из макета

        for (int i = 0; i < fioList.size(); i++) {
            TextView textView = new TextView(this);
            textView.setText("ФИО: " + fioList.get(i) + "\nПол: " + genderList.get(i) + "\nДата Рождения: " + ageList.get(i));
            linearLayout.addView(textView);

            if (imageList.size() > i) {
                ImageView imageView = new ImageView(this);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageList.get(i), 0, imageList.get(i).length);
                imageView.setImageBitmap(bitmap);
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                linearLayout.addView(imageView);
            }
        }
    }
}