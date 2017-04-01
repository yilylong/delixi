package com.delixi.price;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.delixi.price.util.PDFbuilder;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private LinearLayout layoutSearchBar;
    private Button button;
    private EditText materialNum, desc;
    private WebView webView;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        layoutSearchBar = (LinearLayout) findViewById(R.id.layout_search_bar);
        layoutSearchBar.setVisibility(View.INVISIBLE);
        hideSearchBar(layoutSearchBar,1);
        initWebView();
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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                showTipDialog(resultsList.get(position));
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                Snackbar.make(view,"复制内容？",Snackbar.LENGTH_LONG).setAction("ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ElectricParts material = resultsList.get(position);
                        StringBuilder builder = new StringBuilder();
                        builder.append("物料号：");
                        builder.append(material.getMaterial_num()+"\n");
                        builder.append("物料描述：");
                        builder.append(material.getDescription()+"\n");
                        builder.append("归属类：");
                        builder.append(material.getCategory()+"\n");
                        builder.append("库存属性：");
                        builder.append(material.getStock_properties()+"\n");
                        builder.append("含税价格：");
                        builder.append(material.getTax_price()+"\n");
                        builder.append("装箱数：");
                        builder.append(material.getPcs()+"\n");
                        builder.append("调整前价格：");
                        builder.append(material.getPrimary_price()+"\n");
                        builder.append("调整后价格：");
                        builder.append(material.getAdjust_price());
                        ClipData clipData = ClipData.newPlainText("text",builder.toString());
                        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        clipboardManager.setPrimaryClip(clipData);
                        Toast.makeText(MainActivity.this,material.getMaterial_num()+"已成功复制到剪贴板",Toast.LENGTH_SHORT).show();
                    }
                }).show();
                return true;
            }
        });
    }

    private void showTipDialog(final ElectricParts electricParts) {

        new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle("提示")
                .setMessage("需要生成PDF吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        generatePDF(electricParts);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create().show();

    }

    // 生成PDF
    private void generatePDF(ElectricParts electricParts) {
        PDFbuilder.generatePDF(this,electricParts);

    }


    private void initWebView() {
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDisplayZoomControls(false);//不显示放大缩小按钮
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setJavaScriptEnabled(true);
        // 启用localStorage 和 essionStorage
        webView.getSettings().setDomStorageEnabled(true);
        // 开启应用程序缓存
        webView.getSettings().setAppCacheEnabled(true);
        String appCacheDir = this.getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
        webView.getSettings().setAppCachePath(appCacheDir);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setAppCacheMaxSize(1024 * 1024 * 10);// 设置缓冲大小，我设的是10M
        webView.getSettings().setAllowFileAccess(true);
        //webview中滚动拖动到顶部或者底部时的阴影
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY); //取消滚动条白边效果
        // 启用Webdatabase数据库
        webView.getSettings().setDatabaseEnabled(true);
        String databaseDir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        webView.getSettings().setDatabasePath(databaseDir);// 设置数据库路径
        // 启用地理定位
        webView.getSettings().setGeolocationEnabled(true);
        // 设置定位的数据库路径
        webView.getSettings().setGeolocationDatabasePath(databaseDir);
        // 开启插件（对flash的支持）
        webView.getSettings().setRenderPriority(android.webkit.WebSettings.RenderPriority.HIGH);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.loadUrl("http://www.delixi.com/");

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
                hideSearchBar(layoutSearchBar,100);
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
            case R.id.action_search:
                if(webView.getVisibility()==View.VISIBLE){
                    webView.setVisibility(View.GONE);
                }
                if(layoutSearchBar.getVisibility()==View.VISIBLE){
                    hideSearchBar(layoutSearchBar,150);
                }else{
                    showSearchBar(layoutSearchBar,150);
                };
                break;
            case R.id.action_home:
                hideSearchBar(layoutSearchBar,150);
                webView.setVisibility(View.VISIBLE);
                break;
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

    private void showSearchBar(final View view,long duration){
        ObjectAnimator animTranslate = ObjectAnimator.ofFloat(view, "translationY", -view.getHeight(),0);
        animTranslate.setDuration(duration);
        animTranslate.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                layoutSearchBar.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
            @Override
            public void onAnimationEnd(Animator animation) {

            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
        animTranslate.start();
    }

    private void hideSearchBar(final View view,long duration){
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "translationY", 0,-view.getHeight());
        anim.setDuration(duration);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
        anim.start();
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
            String materialNokeyword = "<font color=\"#E34242\">"+materialNum.getText().toString().toUpperCase() + "</font>";
            String materialDesckeyword = "<font color=\"#E34242\">"+desc.getText().toString().toUpperCase()+ "</font>";
            viewHolder.materialNum.setText(Html.fromHtml("物     料     号：" + parts.getMaterial_num().replace(materialNum.getText().toString().toUpperCase(),materialNokeyword)));
            viewHolder.materialDesc.setText(Html.fromHtml("物料描述：" + parts.getDescription().replace(desc.getText().toString().toUpperCase(),materialDesckeyword)));
            viewHolder.stockProperties.setText("库存属性：" + parts.getStock_properties());
            viewHolder.taxPrice.setText(Html.fromHtml("含税价格：<font color=\"#4ea6ed\">" + parts.getTax_price() + "</font>"));
            viewHolder.pcs.setText(Html.fromHtml("装箱数：<font color=\"#4ea6ed\">" + parts.getPcs()+ "</font>"));
            viewHolder.primaryPrice.setText(Html.fromHtml("调整前价格：<font color=\"#4ea6ed\">" + parts.getPrimary_price() + "</font>"));
            viewHolder.adjustPrice.setText(Html.fromHtml("调整后价格：<font color=\"#4ea6ed\">" + parts.getAdjust_price() + "</font>"));
            return convertView;
        }
    }

    private class ViewHolder {
        TextView materialNum, materialDesc, stockProperties, taxPrice, pcs, primaryPrice, adjustPrice;
    }

    @Override
    public void onBackPressed() {
        if(layoutSearchBar.getVisibility()==View.VISIBLE){
            hideSearchBar(layoutSearchBar,150);
            return;
        }
        if(webView.getVisibility()==View.VISIBLE&&webView.canGoBack()){
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }
}
