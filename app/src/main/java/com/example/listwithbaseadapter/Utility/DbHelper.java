package com.example.listwithbaseadapter.Utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.listwithbaseadapter.Model.LanguageInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {

    /*Db Structure*/
    private static final String DB_Name = "Language_Db";
    private static final Integer DB_Ver = 1;

    /*Table Structure*/
    private String tbl_language = "tblLanguage";
    private String colId = "ID";
    private String colName = "Name";
    private String colDesc = "Description";
    private String colReleaseDate = "ReleaseDate";

    SQLiteDatabase db;

    public DbHelper(Context context){
        //code to create database
        super(context,DB_Name,null,DB_Ver);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Code to create table
        String query = "CREATE TABLE "+tbl_language+ "( "+colId+" INTEGER PRIMARY KEY AUTOINCREMENT ,"
                +colName+" TEXT,"
                +colDesc+" TEXT,"
                +colReleaseDate+" DATE)";

        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //Drop table
        String query = "DROP TABLE IF EXISTS "+tbl_language;
        sqLiteDatabase.execSQL(query);

        //Call OnCreate Again
        onCreate(sqLiteDatabase);
    }

    //Create For Language
    public int Insert(LanguageInfo info){
        db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(colName,info.getName());
        values.put(colDesc,info.getDescription());
        values.put(colReleaseDate,info.getReleasedDate().toString());

        db.insert(tbl_language,null,values);

        db = getReadableDatabase();
        Cursor cur = db.rawQuery("select max("+colId+") ID from "+tbl_language,null);
        cur.moveToNext();
        int result = Integer.parseInt(cur.getString(0));
        return result;
    }

    //Update For language
    public int Update(LanguageInfo info){
        db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(colName,info.getName());
        contentValues.put(colDesc,info.getDescription());
        contentValues.put(colReleaseDate,info.getReleasedDate().toString());

        int result = db.update(tbl_language,contentValues,colId +" = ?",new String[]{String.valueOf(info.getId())});
        return result;
    }

    //Get All Data For Language
    public ArrayList<LanguageInfo> GetAll() throws ParseException {
        ArrayList<LanguageInfo> infoList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * from "+tbl_language,null);

        while(cur.moveToNext())
        {
            LanguageInfo info =new LanguageInfo();
            info.setId(Integer.parseInt(cur.getString(cur.getColumnIndex(colId))));
            info.setName(cur.getString(cur.getColumnIndex(colName)));
            info.setReleasedDate(new SimpleDateFormat("dd-MM-yyy").parse(cur.getString(cur.getColumnIndex(colReleaseDate))));
            info.setDescription(cur.getString(cur.getColumnIndex(colDesc)));
            infoList.add(info);
        }

        return infoList;
    }

    //Delete For language
    public void Delete(LanguageInfo info){
        db = getWritableDatabase();
        db.execSQL("delete from "+tbl_language+" where "+colId+" ="+info.getId());
    }
}
