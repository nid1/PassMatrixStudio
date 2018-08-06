package com.example.passmatrix;

import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{
	Context cc;
 public DBHelper(Context context, String name, CursorFactory factory,int version) {
	 super(context, name, factory, version);  
	 cc=context;
 }

 @Override
 public void onCreate(SQLiteDatabase db) {
//	 db.execSQL("CREATE TABLE IF NOT EXISTS login(logindex INTEGER PRIMARY KEY AUTOINCREMENT,mem_id INTEGER,question_id TEXT,answer INTEGER)");
	 db.execSQL("CREATE TABLE IF NOT EXISTS login(logindex INTEGER PRIMARY KEY AUTOINCREMENT,imei text,logimg TEXT,blockindex INTEGER,ftype INTEGER)");
	 
 }

 @Override
 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	 db.execSQL("DROP TABLE IF EXISTS login");
     onCreate(db);
 }
 
 public boolean insertdata(String imei, String path, int indx,int x)
 {
	 SQLiteDatabase sdb=this.getWritableDatabase();
	 Cursor c=sdb.rawQuery("select * from login where imei='"+imei+"' and blockindex='"+indx+"'", null);
	 if(c.getCount()>0)
	 {
	     ContentValues values = new ContentValues();
	     values.put("logimg", path);
	     values.put("ftype", x+"");
		 sdb.update("login", values, "imei=? and blockindex=?",new String[] { imei,indx+"" });
	 }
	 else
	 {
		 sdb.execSQL("insert into login values(null,'"+imei+"','"+path+"','"+indx+"','"+indx+"')");
	 }
	 return true;
 }
 
 public Cursor getPaths(int i)
 {
	 SQLiteDatabase sdb=this.getReadableDatabase();
	 Cursor c=sdb.rawQuery("select * from login where blockindex='"+i+"'", null);
	 return c;
 } 
 public Cursor getData()
 {
	 SQLiteDatabase sdb=this.getReadableDatabase();
	 Cursor c=sdb.rawQuery("select * from login", null);
	 return c;
 }
 public void deleteTable()
 {
	 SQLiteDatabase db=this.getWritableDatabase();
	 db.execSQL("DROP TABLE IF EXISTS login");
	 onCreate(db);
 }

 // Updating single call
 public int updateCall(String number,String duration) {
     SQLiteDatabase db = this.getWritableDatabase();
 
     ContentValues values = new ContentValues();
     values.put("duration", duration);
     return db.update("login", values, "number = ?",new String[] { number });
 }
 
    // Deleting single call
 public void deleteContact(String number) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("login", "number= ?",
                new String[] { number});
        db.close();
 }
 
 
 public String insertCalldata(String id,String number, String date, String time,String duration, String type,String names)
 {
    Log.d("inside", "inside:"+type);
	 SQLiteDatabase sdb=cc.openOrCreateDatabase("passmatrix", SQLiteDatabase.CREATE_IF_NECESSARY,null);

		sdb.execSQL("CREATE TABLE IF NOT EXISTS call_logs(id integer PRIMARY KEY AUTOINCREMENT,number text,time text,date text,ccount text, type text)");
		String query="select * from call_logs where number='"+number+"' and date='"+date+"' and time='"+DBHelper.getTime()+"'";
	 Log.d("----------",query);
		Cursor cr=sdb.rawQuery(query, null);
		Log.d("----------",cr.getCount()+"");
String cnt="1";
		if(cr.getCount()>0)
		{
			Log.d("----------",cr.getCount()+"");
			cr.moveToFirst();
			ContentValues cv=new ContentValues();
			//cv.put("uid", id);
			cv.put("number", number);
			cv.put("time", DBHelper.getTime());
			cv.put("date", date);
			cnt= cr.getInt(4)+2+"";
			cv.put("ccount", cr.getInt(4)+1);
			cv.put("type",type);
			sdb.update("call_logs", cv,"id=?", new String[]{cr.getString(0)});
		}else{

			ContentValues cv=new ContentValues();
			//cv.put("uid", id);
			cv.put("number", number);
			cv.put("time", DBHelper.getTime());
			cv.put("date", date);
			cv.put("ccount", "1");
			cv.put("type",type);
			sdb.insert("call_logs", null, cv);
		}
		sdb.close();
		return "inseted   "+cnt;

	 //return "Error";
}
 
 public Boolean insertloc(String id, String place, String latitude,String longitude,String dt,String tm) 
 {
 	 // Log.d("inside", "inside:"+this_msg);
 	  try
 	  {    
 		  SQLiteDatabase sdb=this.getWritableDatabase();
 	sdb.execSQL("CREATE TABLE IF NOT EXISTS location(id integer primary key autoincrement,uid integer,place text,latitude text, longitude text,imei text,dt text,tm text)");
 	ContentValues cv=new ContentValues();
 	cv.put("uid", id);
 	cv.put("place", place);
 	cv.put("latitude", latitude);
 	cv.put("longitude", longitude);
 	cv.put("dt", dt);
 	 Log.d(" 6 ", "-----");
     cv.put("tm", tm);
     Log.d(" 7 ", "-----");

 	sdb.insert("location", null, cv);
 	
 	
 	sdb.close();
 	
 	 Log.d("insrt     ", "-----");
 	  }
 	  catch(Exception e){
 		  Log.d("errrrrrrr", e.getMessage()+"-----");
 		  return false;
 	  }
 	
   return true;
 }
 public static String getTime(){
	 Calendar c = Calendar.getInstance();
	 int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

	 if(timeOfDay >= 0 && timeOfDay < 12){
		 return "Morning";
	 }else if(timeOfDay >= 12 && timeOfDay < 16){
		 return "Afternoon";
	 }else if(timeOfDay >= 16 && timeOfDay < 21){
		 return "Evening";
	 }else if(timeOfDay >= 21 && timeOfDay < 24){
		 return "Night";
	 }
	 return "";
 }
}