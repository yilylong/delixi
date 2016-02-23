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

public class OuterDBmanager {
	private static final String TAG="OuterDBmanager";
	private static final String PAGENAME="com.delixi.price";
	private static final String DBPATH ="/data"+Environment.getDataDirectory().getAbsolutePath()+"/"+PAGENAME+"/";
	private static final String DBNAME="delixi.db";
	public static final String OUT_DBPATH=Environment.getExternalStorageDirectory()+"delixi/delixi.db";

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
	 * 通过一个汉字查找对应的五笔编码（只支持单字查找不支持词组或句子）
	 * @param context
	 * @param word
	 * @return
	 */
	public static String findWBBMbyHanzi(Context context,String word){
		if(word==null||word.trim().length()==0||word.trim().length()>1){
			return null;
		}
//		if(!isGbk(word)){
//			return null;
//		}
		SQLiteDatabase db = null;
		String bm = null;
		try {
			db = openDB(context);
			String sql = " SELECT bm FROM Wbbm WHERE hanzi=? ;";
			Cursor cursor = db.rawQuery(sql,  new String[]{word});
			while(cursor.moveToNext()){
				bm = cursor.getString(cursor.getColumnIndex("bm"));
			}
			cursor.close();
		} catch (Exception e) {
			
		}finally{
			if(db!=null&&db.isOpen()){
				db.close();	
			}
		}
		return bm;
	}
	
	/**
	 * 将汉字转为五笔编码（只支持单字查找不支持词组或句子）
	 * @param context
	 * @param word
	 * @return
	 */
	public static String hanziToWBBM(Context context,String word){
		if(word==null||word.trim().length()==0){
			return null;
		}
//		if(!isGbk(word)){
//			return null;
//		}
		String keyword = word;
		if(word.length()>1){
			keyword = word.substring(0, 1);
		}
		return findWBBMbyHanzi(context,keyword);
	}
	
	/**
	 * 是否是汉字
	 * @param str
	 * @return
	 */
	public static boolean isGbk(String str){  
		if(str==null||str.isEmpty()){
			return false;
		}
		char[] chars = str.toCharArray();
		boolean isGB2312 = false;
		for (int i = 0; i < chars.length; i++) {
			byte[] bytes = ("" + chars[i]).getBytes();
			if (bytes.length == 2) {
				int[] ints = new int[2];
				ints[0] = bytes[0] & 0xff;
				ints[1] = bytes[1] & 0xff;
				if (ints[0] >= 0x81 && ints[0] <= 0xFE && ints[1] >= 0x40
						&& ints[1] <= 0xFE) {
					isGB2312 = true;
					break;
				}
			}
		}
		return isGB2312;
	}
}
