package com.example.listwithbaseadapter;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.listwithbaseadapter.Model.LanguageInfo;
import com.example.listwithbaseadapter.Utility.ApiHelper;
import com.example.listwithbaseadapter.Utility.DbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ListView toDoList;
    FloatingActionButton fab;
    public ArrayList<LanguageInfo> arrayList;
    LanguageAdapter adapter;

    EditText txtLangName,txtDescription,txtReleaseDate,txtId;
    String name,desc,releasedate;
    Integer id,tmpPosition = -9;
    AlertDialog alertDialog;
    Button btnSave,btnCancel,btnDelete;
    LanguageInfo tmpLanguageInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toDoList = (ListView)findViewById(R.id.toDoList);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        //Get Data From List
        //GenerateList();

        /*
        String[] nameList = {"Java","Android","C#","C++","PHP","Python","GO"};
        ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.list_item,nameList);
        toDoList.setAdapter(adapter);
        */

        //Get All Data From Sqlite
        /*
        try {
            GetAllData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        */
        //Get Data From Api
        new CRUDSync().execute("http://192.168.99.135:82/api/values?value=","");

        adapter = new LanguageAdapter(this,arrayList);
        toDoList.setAdapter(adapter);

        toDoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tmpPosition = i; //Array's Index
                showPopupDialog(arrayList.get(i));
                Toast.makeText(MainActivity.this,arrayList.get(i).getName(),Toast.LENGTH_LONG).show();
            }
        });

        fab.setOnClickListener(this);
    }

    //Get All Data From Sqlite
    public  void GetAllData() throws ParseException {
        DbHelper dbHelper = new DbHelper(MainActivity.this);
        arrayList = dbHelper.GetAll();
    }

    private void GenerateList(){
        for(int i=0;i<8;i++){
            LanguageInfo info = new LanguageInfo();
            info.setId(i+1);
            info.setName("Language "+(i+1));
            Date d = new Date();
            info.setReleasedDate(d);
            info.setDescription(info.getName() +" "+"Lorem Ipsum is simply dummy text of the printing and typesetting industry");
            arrayList.add(info);
        }
    }

    private void showPopupDialog(LanguageInfo info){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.language_view,null);
        builder.setView(v);

        alertDialog = builder.create();
        alertDialog.show();
        //Showing

        txtId = (EditText) v.findViewById(R.id.txtId);
        txtLangName = (EditText) v.findViewById(R.id.txtLangName);
        txtDescription = (EditText) v.findViewById(R.id.txtDescription);
        txtReleaseDate = (EditText) v.findViewById(R.id.txtReleaseDate);

        if(info != null){
            txtId.setText(String.valueOf(info.getId()));
            txtLangName.setText(info.getName());
            txtDescription.setText(info.getDescription());
            txtReleaseDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(info.releasedDate));
        }

        btnSave = (Button) v.findViewById(R.id.btnSave);
        btnCancel = (Button) v.findViewById(R.id.btnCancel);
        btnDelete = (Button) v.findViewById(R.id.btnDelete);

        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSave:
                //Code To Insert Here
                String name = txtLangName.getText().toString();
                String desc = txtDescription.getText().toString();
                String releaseDate = txtReleaseDate.getText().toString();

                int id = Integer.parseInt(txtId.getText().toString());

                LanguageInfo info = new LanguageInfo();

                if(tmpPosition >= 0){
                    info = arrayList.get(tmpPosition);
                }

                info.setId(id);
                info.setName(name);
                info.setDescription(desc);

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    info.setReleasedDate(df.parse(releaseDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //Insert to Database
                DbHelper db = new DbHelper(this);

                if(tmpPosition < 0) //For New Data
                {
                    id = db.Insert(info);
                    info.setId(id);
                    arrayList.add(info);
                }
                else{ //For Update Data
                    db.Update(info);
                }

                adapter.notifyDataSetChanged();
                alertDialog.hide();
                tmpPosition = -9;
                ;break;
            case R.id.btnCancel:
                tmpPosition = -9;
                alertDialog.cancel();
                ;break;
            case R.id.fab:
//                tmpPosition = -9;
//                showPopupDialog(null);break;
                  new CRUDSync().execute("http://192.168.99.135:82/api/values?value=","");break;
            case R.id.btnDelete:
                int deletedId = Integer.parseInt(txtId.getText().toString());
                if(deletedId < 0){
                    alertDialog.hide();
                    tmpPosition = -9;
                    return;
                }else{
                    if(tmpPosition >= 0){
                        arrayList.remove(arrayList.get(tmpPosition));
                        adapter.notifyDataSetChanged();
                    }
                }
                alertDialog.hide();
                tmpPosition = -9;
                break;
        }
    }

    public void onClickOld(View view) {
        switch (view.getId()){
            case R.id.btnSave:
                //Code To Insert Here
                String name = txtLangName.getText().toString();
                String desc = txtDescription.getText().toString();
                String releaseDate = txtReleaseDate.getText().toString();

                int id = Integer.parseInt(txtId.getText().toString());

                if(arrayList == null || arrayList.size() == 0)
                {
                    id = 1;
                }
                else if(id < 0){
                    id += arrayList.get(arrayList.size()-1).getId();
                }

                LanguageInfo info = new LanguageInfo();

                if(tmpPosition >= 0){
                    info = arrayList.get(tmpPosition);
                }

                info.setId(id);
                info.setName(name);
                info.setDescription(desc);

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    info.setReleasedDate(df.parse(releaseDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //Add to list if currenct position is less than 0
                if(tmpPosition < 0){
                    arrayList.add(info);
                }
                adapter.notifyDataSetChanged();
                alertDialog.hide();
                tmpPosition = -9;
                ;break;
            case R.id.btnCancel:
                tmpPosition = -9;
                alertDialog.cancel();
                ;break;
            case R.id.fab:
                tmpPosition = -9;
                showPopupDialog(null);break;
            case R.id.btnDelete:
                int deletedId = Integer.parseInt(txtId.getText().toString());
                if(deletedId < 0){
                    alertDialog.hide();
                    tmpPosition = -9;
                    return;
                }else{
                    if(tmpPosition >= 0){
                        arrayList.remove(arrayList.get(tmpPosition));
                        adapter.notifyDataSetChanged();
                    }
                }
                alertDialog.hide();
                tmpPosition = -9;
                break;
        }
    }

    private class CRUDSync extends AsyncTask<String,Void,ArrayList<LanguageInfo>> {

        @Override
        protected ArrayList<LanguageInfo> doInBackground(String... strings) {
            String strUrl = strings[0];
            String paramValue = strings[1];
            String result = "";
            result = ApiHelper.Insert(strUrl,paramValue);

            try {
                if(result != null && result != ""){
                    JSONArray jsonArray = null;
                    try {
                        String jsonFormattedString = result.replaceAll("\\\\", "");
                        jsonArray = new JSONArray(jsonFormattedString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        LanguageInfo _info = new LanguageInfo();
                        _info.setId(jsonObject.getInt("id"));
                        _info.setName(jsonObject.getString("name"));
                        _info.setDescription(jsonObject.getString("description"));
                        _info.setReleasedDate(new SimpleDateFormat("dd-MM-yyyy").parse(jsonObject.getString("releasedDate")));
                        arrayList.add(_info);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return arrayList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            arrayList = new ArrayList<>();
        }

        @Override
        protected void onPostExecute(ArrayList<LanguageInfo> list) {
            super.onPostExecute(list);
        }
    }
}