package com.example.perepis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Admin extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        dbHelper = new DatabaseHelper(this);
        Button registerButton = findViewById(R.id.register_button);
        Button gotopeople = findViewById(R.id.gotopeople);
        Button delete = findViewById(R.id.button);


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.showDataPopup(Admin.this);

            }
        });


        gotopeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin.this, BD.class);
                startActivity(intent);


            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin.this, registoractivity.class);
                startActivity(intent);
            }
        });
    }
}