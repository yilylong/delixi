package com.delixi.price;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
    private EditText materialNum, desc;
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
        if (!OuterDBmanager.checkDatabase()) {
            new AsyncTask<Void, Void, Void>() {
                private ProgressDialog progressDialog;

                @Override
                protected void onPreExecute() {
                    progressDialog = ProgressDialog.show(MainActivity.this, null, "正在导入数据", true, false);
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
        } else {
            Toast.makeText(this, "数据已经初始化", Toast.LENGTH_SHORT).show();
        }

    }

    private void initView() {
        listView = (ListView) findViewById(R.id.search_result_list);
        button = (Button) findViewById(R.id.btn_search);
        materialNum = (EditText) findViewById(R.id.material_num);
        desc = (EditText) findViewById(R.id.material_desc);
        materialNum.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // 开始搜索
                searchPrice();
                return true;
            }
        });
        desc.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // 开始搜索
                searchPrice();
                return true;
            }
        });
        listView.setAdapter(adapter = new ResultsAdapter());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.isEmpty(materialNum.getText().toString()) && StringUtils.isEmpty(desc.getText().toString())) {
                    Toast.makeText(MainActivity.this, "请输入物料号或者描述查询", Toast.LENGTH_LONG).show();
                    return;
                }
                // 开始搜索
                searchPrice();
            }
        });
    }

    private void closeIME(IBinder windowToken) {
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(windowToken, 0);
    }


    private void searchPrice() {
        new AsyncTask<String, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                // 关闭输入法
                closeIME(materialNum.getWindowToken());
                showProgress();
            }

            @Override
            protected Boolean doInBackground(String... params) {
                ArrayList<ElectricParts> partses = OuterDBmanager.findElectricParts(MainActivity.this, params[0], params[1]);
                if (partses != null && partses.size() > 0) {
                    resultsList.clear();
                    resultsList.addAll(partses);
                    return true;
                } else {
                    resultsList.clear();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                dismissProgress();
                adapter.notifyDataSetChanged();
                if (!aBoolean) {
                    Toast.makeText(MainActivity.this, "无此配件，请检查物料号和物料描述", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "共找到" + resultsList.size() + "条相关配件", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(materialNum.getText().toString(), desc.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_import_data:
                checkDatabase();
                break;
            case R.id.action_import_from_sd:
                importDataFromSD();
                break;
            case R.id.action_about:
                statement();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 软件声明
     */
    private void statement() {
        new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle("应用声明")
                .setMessage("本应用只供个人使用，如果商用或对公请联系作者yilylong@163.com,请尊重作者劳动，谢谢！")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    private void importDataFromSD() {

        new AsyncTask<Void, Void, Boolean>() {
            private ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(MainActivity.this, null, "正在拼命地导入数据", true, false);
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                File sFile = new File(OuterDBmanager.OUT_DBPATH);
                if (!sFile.exists()) {
                    return false;
                }
                OuterDBmanager.importDataBaseFromSD(MainActivity.this);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                progressDialog.dismiss();
                if (!aBoolean) {
                    Toast.makeText(MainActivity.this, "请将数据库文件到" + OuterDBmanager.OUT_DIR + "后再重试导入数据", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    private void showProgress() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(this, null, "正在拼命查询...", true);
        } else {
            progressDialog.show();
        }
    }

    private void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     *
     */
    private class ResultsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return resultsList == null ? 0 : resultsList.size();
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
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.item_result, parent, false);
                viewHolder.materialNum = (TextView) convertView.findViewById(R.id.item_material_num);
                viewHolder.materialDesc = (TextView) convertView.findViewById(R.id.item_material_desc);
                viewHolder.stockProperties = (TextView) convertView.findViewById(R.id.item_stock_properties);
                viewHolder.taxPrice = (TextView) convertView.findViewById(R.id.item_tax_price);
                viewHolder.pcs = (TextView) convertView.findViewById(R.id.item_pcs);
                viewHolder.primaryPrice = (TextView) convertView.findViewById(R.id.item_primary_price);
                viewHolder.adjustPrice = (TextView) convertView.findViewById(R.id.item_adjust_price);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ElectricParts parts = resultsList.get(position);
            viewHolder.materialNum.setText("物  料  号：" + parts.getMaterial_num());
            viewHolder.materialDesc.setText("物料描述：" + parts.getDescription());
            viewHolder.stockProperties.setText("库存属性：" + parts.getStock_properties());
            viewHolder.taxPrice.setText(Html.fromHtml("含税价格：<font color=\"#E34242\">" + parts.getTax_price() + "</font>"));
            viewHolder.pcs.setText("装箱数：" + parts.getPcs());
            viewHolder.primaryPrice.setText(Html.fromHtml("调整前价格：<font color=\"#E34242\">" + parts.getPrimary_price() + "</font>"));
            viewHolder.adjustPrice.setText(Html.fromHtml("调整后价格：<font color=\"#E34242\">" + parts.getAdjust_price() + "</font>"));
            return convertView;
        }
    }

    private class ViewHolder {
        TextView materialNum, materialDesc, stockProperties, taxPrice, pcs, primaryPrice, adjustPrice;
    }

}
