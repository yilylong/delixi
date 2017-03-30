package com.delixi.price;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class OuterDBmanager {
	private static final String TAG="OuterDBmanager";
	private static final String PAGENAME="com.delixi.price";
	private static final String DBPATH ="/data"+Environment.getDataDirectory().getAbsolutePath()+"/"+PAGENAME+"/";
	private static final String DBNAME="delixi.db";
	public static final String OUT_DBPATH=Environment.getExternalStorageDirectory()+"/delixi/delixi.db";
	public static final String OUT_DIR=Environment.getExternalStorageDirectory()+"/delixi";

	private Context mContext;
	private static SQLiteDatabase mDataBase;
	public OuterDBmanager(Context context){
		this.mContext = context;
	}


	public static boolean checkDatabase(){
		File dbFile = new File(DBPATH+DBNAME);
		return dbFile.exists();
	}
	/**
	 * 导入数据库
	 * @param context
	 */
	public static void importDataBase(Context context){
		File dbFile = new File(DBPATH+DBNAME);
//		if(dbFile.exists()){
//			Log.i(TAG, "五笔编码库已经存在");
//			return;
//		}
		InputStream in = null;
		FileOutputStream out = null;
		try {
			in = context.getResources().openRawResource(R.raw.delixi);
			out = new FileOutputStream(dbFile);
			int len = 0;
			byte[] buff = new byte[1024];
			while((len=in.read(buff))!=-1){
				out.write(buff, 0, len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(in!=null){
					in.close();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if(out!=null){
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 导入数据库
	 * @param context
	 */
	public static void importDataBaseFromSD(Context context){
		File sFile = new File(OUT_DBPATH);
		if(!sFile.exists()){
			return;
		}
		File dbFile = new File(DBPATH+DBNAME);
//		if(dbFile.exists()){
//			Log.i(TAG, "五笔编码库已经存在");
//			return;
//		}
		InputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(sFile);
			out = new FileOutputStream(dbFile);
			int len = 0;
			byte[] buff = new byte[1024];
			while((len=in.read(buff))!=-1){
				out.write(buff, 0, len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(in!=null){
					in.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if(out!=null){
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 打开一个外部数据库
	 * @param context
	 * @return
	 */
	public static SQLiteDatabase openDB(Context context){
		File dbFile = new File(DBPATH+DBNAME);
		//如果没有数据库文件先导入
		if(!dbFile.exists()){
			importDataBase(context);
		}
		return SQLiteDatabase.openOrCreateDatabase(dbFile, null);
	}
	/**
	 * 关闭数据库
	 */
	public static void closeDB(){
		if(mDataBase!=null&&mDataBase.isOpen()){
			mDataBase.close();
		}
	}

	/**
	 * 通过物料号查找电器配件详情
	 * @param context
	 * @param MaterarialNum
	 * @param desc
	 * @return
	 */
	public static ArrayList<ElectricParts> findElectricParts(Context context,String MaterarialNum,String desc){
		if((MaterarialNum==null||MaterarialNum.trim().length()==0)&&(desc==null||desc.trim().length()==0)){
			return null;
		}
		StringBuilder arguments = new StringBuilder();
		if(!StringUtils.isEmpty(MaterarialNum)){
			arguments.append(" material_num LIKE '%"+MaterarialNum+"%' ");
		}else{
			arguments.append(" 1=1 ");
		}
		if(!StringUtils.isEmpty(desc)){
			arguments.append(" and description LIKE '%"+desc+"%' ");
		}

		ArrayList<ElectricParts> result = null;
		SQLiteDatabase db = null;
		String bm = null;
		try {
			result = new ArrayList<ElectricParts>();
			db = openDB(context);
			String sql = " SELECT * FROM t_price WHERE "+arguments.toString()+";";
			Cursor cursor = db.rawQuery(sql,null);
			while(cursor.moveToNext()){
				ElectricParts parts = new ElectricParts();
				parts.setMaterial_num(cursor.getString(cursor.getColumnIndex("material_num")));
				parts.setDescription(cursor.getString(cursor.getColumnIndex("description")));
				parts.setCategory(cursor.getString(cursor.getColumnIndex("category")));
				parts.setTax_price(cursor.getFloat(cursor.getColumnIndex("tax_price")));
				parts.setPcs(cursor.getFloat(cursor.getColumnIndex("pcs")));
				parts.setStock_properties(cursor.getString(cursor.getColumnIndex("stock_properties")));
				parts.setPrimary_price(cursor.getFloat(cursor.getColumnIndex("primary_price")));
				parts.setAdjust_price(cursor.getFloat(cursor.getColumnIndex("adjust_price")));
				result.add(parts);
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(db!=null&&db.isOpen()){
				db.close();	
			}
		}
		return result;
	}
	

	

}
