package com.delixi.price;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
                if (materialNum.getText() == null && desc.getText() == null) {
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
