package com.example.perepis;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class User extends AppCompatActivity {
    private EditText editTextTitle;
    private int mYear, mMonth, mDay;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageButton imagebutton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Button savebutton = findViewById(R.id.save);
        CheckBox womanCheckBox = findViewById(R.id.Woman);
        CheckBox menCheckBox = findViewById(R.id.Men);
        editTextTitle = findViewById(R.id.FIO);
        imagebutton = findViewById(R.id.image);

        Button date = findViewById(R.id.date);
        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        final String[] gender = {""};

        final String[] age = {""};


        imagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Выберите Постер"), PICK_IMAGE_REQUEST);

            }
        });


        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(User.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                mYear = year;
                                mMonth = monthOfYear;
                                mDay = dayOfMonth;

                                // сохраняем выбранную дату в переменную age
                                age[0] = mYear + "-" + (mMonth + 1) + "-" + mDay;
                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.show();
            }
        });

        womanCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gender[0] = "Женский";

                    menCheckBox.setChecked(false); // Снимаем выбор с CheckBox "Муж" при выборе CheckBox "Жен"
                }
            }
        });

        menCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gender[0] = "Мужской";
                    womanCheckBox.setChecked(false); // Снимаем выбор с CheckBox "Жен" при выборе CheckBox "Муж"
                }
            }
        });

        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] image = getImageAsByteArray(imageUri);
                String fio = editTextTitle.getText().toString();
                long result = db.addPeople(fio, gender[0], age[0], image);

                if (result != -1) {
                    Toast.makeText(User.this, "Спасибо, за участиве в переписи населения", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(User.this, "Ошибка", Toast.LENGTH_SHORT).show();
                }


            }
        });




    }

    private byte[] getImageAsByteArray(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] imageBytes = stream.toByteArray();
            stream.close();
            return imageBytes;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imagebutton.setImageURI(imageUri);
            imagebutton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        }
    }

}