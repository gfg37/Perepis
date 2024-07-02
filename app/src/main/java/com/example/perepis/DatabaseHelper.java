package com.example.perepis;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private int selectedId;
    private String selectedTitle;
    private String selectedDescription;
    private String selectedImage;

    private static final String DATABASE_NAME = "People.db";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_TABLE_QUERY = "CREATE TABLE People (id INTEGER PRIMARY KEY AUTOINCREMENT, ФИО TEXT, Пол TEXT, возраст TEXT,image TEXT)";
    private static final String TABLE_NAME = "People";
    private static final String USERS_TABLE = "Users";
    private static final String CREATE_USERS_TABLE_QUERY = "CREATE TABLE Users (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT, password TEXT)";
   // private static final String SESSIONS_TABLE = "Sessions";
  //  private static final String CREATE_SESSIONS_TABLE_QUERY = "CREATE TABLE Sessions (id INTEGER PRIMARY KEY AUTOINCREMENT,movie_id INTEGER, data TEXT,time Text)";
  //  private static final String CREATE_TRIGGER_QUERY = "CREATE TRIGGER IF NOT EXISTS insert_movie_id " +
  //          "AFTER INSERT ON Movies " +
   //         "BEGIN " +
   //         "INSERT INTO Sessions (movie_id, data, time) VALUES (NEW.id, '', ''); " +
   //         "END";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_QUERY);
        db.execSQL(CREATE_USERS_TABLE_QUERY);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Movies");
        db.execSQL("DROP TABLE IF EXISTS Users");
        onCreate(db);
    }


    // Метод проверки юзера в БД
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM Users WHERE email = ? AND password = ?", new String[]{email, password});

        boolean result = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return result;
    }



    //Методы для добавления данных в базу данных
    public long addUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Проверяем, существует ли пользователь с таким email в базе
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE email = ?", new String[]{email});
        if (cursor.getCount() > 0) {
            // Пользователь с таким email уже существует
            cursor.close();
            db.close();
            return -1; // Возвращаем -1 как признак ошибки
        }

        cursor.close();

        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("password", password);
        long result = db.insert("users", null, values);

        db.close();
        return result; // Возвращаем идентификатор нового пользователя
    }

    public long addPeople(String fio, String gender, String age, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("ФИО", fio);
        values.put("Пол", gender);
        values.put("возраст", age);
        values.put("image", image);

        // Вставляем данные в таблицу "People"
        long result = db.insert("People", null, values);

        db.close();

        return result;
    }

    public void addAdmin() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT id FROM users WHERE email = 'admin'";
        Cursor cursor = db.rawQuery(query, null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();

        if (count == 0) {
            ContentValues values = new ContentValues();
            values.put("email", "admin");
            values.put("password", "admin");
            db.insert("users", null, values);
        }

        db.close();
    }




    //Метод который выводит список фильмов с отработчиком удаления из бд
    public void showDataPopup(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Список перепещиков");

        List<String> dataList = getAllData(); // Получаем данные из БД

        final String[] dataArr = dataList.toArray(new String[dataList.size()]);

        boolean[] selectedItems = new boolean[dataArr.length];

        builder.setMultiChoiceItems(dataArr, selectedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
                // Обработка клика по элементу списка
                selectedItems[which] = isChecked;
            }
        });

        builder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                SQLiteDatabase db = getWritableDatabase(); // Получаем доступ к базе данных

                for (int i = 0; i < dataArr.length; i++) {
                    if (selectedItems[i]) {
                        String deleteId = getIdByTitle(dataArr[i]); // Получаем id по названию
                        db.execSQL("DELETE FROM People WHERE id = " + deleteId);

                    }
                }


                db.close();

                // Обновление списка после удаления
                dataList.removeAll(Arrays.asList(dataArr));
                showDataPopup(context); // Показать обновленный список
            }
        });

        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    // Метод для получения id по названию фильма
    private String getIdByTitle(String title) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM People WHERE ФИО = ?", new String[]{title});
        String id = "";
        if (cursor.moveToFirst()) {
            id = cursor.getString(cursor.getColumnIndex("id"));
        }
        cursor.close();
        //db.close();
        return id;
    }


    //Методы получения данных из БД
    public List<String> getAllData() {
        List<String> dataList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ФИО FROM People", null);

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex("ФИО"));
                dataList.add(title);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return dataList;
    }
    public List<String> geFIO() {
        List<String> dataList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ФИО FROM People", null);

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex("ФИО"));
                dataList.add(title);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return dataList;
    }
    public List<String> getPOL() {
        List<String> dataList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT Пол FROM People", null);

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex("Пол"));
                dataList.add(title);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return dataList;
    }
    public List<String> getAGE() {
        List<String> dataList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT возраст FROM People", null);

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex("возраст"));
                dataList.add(title);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return dataList;
    }
    public List<byte[]> getImagesAsBytes() {
        List<byte[]> imagesList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT image FROM People"; // Предположим, что изображения хранятся в столбце image_data

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                byte[] imageData = cursor.getBlob(cursor.getColumnIndex("image"));
                imagesList.add(imageData);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return imagesList;
    }



}




