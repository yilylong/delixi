package com.delixi.price;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private Button button;
    private EditText materialNum,desc;
    private ArrayList<ElectricParts> resultsList = new ArrayList<ElectricParts>();
    private ProgressDialog progressDialog;
    private ResultsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        checkDatabase();
    }

    private void checkDatabase() {
        if(!OuterDBmanager.checkDatabase()){
            new AsyncTask<Void,Void,Void>(){
                private ProgressDialog progressDialog;
                @Override
                protected void onPreExecute() {
                    progressDialog = ProgressDialog.show(MainActivity.this,null,"正在导入数据",true,false);
                }
                @Override
                protected Void doInBackground(Void... params) {
                    OuterDBmanager.importDataBase(MainActivity.this);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    progressDialog.dismiss();
                }
            }.execute();
        }else{
            Toast.makeText(this,"数据已经初始化",Toast.LENGTH_SHORT).show();
        }

    }

    private void initView() {
        listView  = (ListView) findViewById(R.id.search_result_list);
        button = (Button) findViewById(R.id.btn_search);
        materialNum = (EditText) findViewById(R.id.material_num);
        desc = (EditText) findViewById(R.id.material_desc);
        listView.setAdapter(adapter = new ResultsAdapter());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.isEmpty(materialNum.getText().toString())&& StringUtils.isEmpty(desc.getText().toString())) {
                    Toast.makeText(MainActivity.this, "请输入物料号或者描述查询", Toast.LENGTH_LONG).show();
                    return;
                }
                searchPrice();

            }
        });
    }

    private void searchPrice() {
        new AsyncTask<String,Void,Boolean>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(String... params) {
                return null;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {

            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_import_data:
                checkDatabase();
                break;
            case R.id.action_import_from_sd:
                importDataFromSD();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void importDataFromSD() {

        new AsyncTask<Void,Void,Boolean>(){
            private ProgressDialog progressDialog;
            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(MainActivity.this,null,"正在拼命地导入数据",true,false);
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                File sFile = new File(OuterDBmanager.OUT_DBPATH);
                if(!sFile.exists()){
                    return false;
                }
                OuterDBmanager.importDataBaseFromSD(MainActivity.this);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                progressDialog.dismiss();
                if(!aBoolean){
                    Toast.makeText(MainActivity.this, "请将数据库文件到"+OuterDBmanager.OUT_DBPATH+"后再重试导入数据", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void showProgress(){
        if(progressDialog==null){
            progressDialog = ProgressDialog.show(this,null,"正在拼命查询...",true);
        }else{
            progressDialog.show();
        }
    }
    private void dismissProgress(){
        if(progressDialog!=null&&progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    /**
     *
     */
    private class ResultsAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return resultsList==null?0:resultsList.size();
        }

        @Override
        public Object getItem(int position) {
            return resultsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }
    private class ViewHolder{
        TextView materialNum,materialDesc,stockProperties,taxPrice,pcs,primaryPrice,adjustPrice;
    }

}
