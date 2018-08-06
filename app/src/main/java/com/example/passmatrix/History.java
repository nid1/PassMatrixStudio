package com.example.passmatrix;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat") 
public class History extends ContentObserver {

 Context c;
 String name=null;
 String sim="";
 
 String sim1imei="",sim2imei="",callimei="",callphone="";
 
 public History(Handler handler, Context cc) {
  // TODO Auto-generated constructor stub
  super(handler);
  c=cc;
 }
 
 @Override
    public boolean deliverSelfNotifications() {
        return true;
    }

 @Override
 public void onChange(boolean selfChange) {
  // TODO Auto-generated method stub
  super.onChange(selfChange);
  SharedPreferences sp=c.getSharedPreferences("ZnSoftech", Activity.MODE_PRIVATE);
  String number=sp.getString("number", null);

  sim=sp.getString("sim", null);
  if(number!=null)
  {
   getCalldetailsNow();
   sp.edit().putString("number", null).commit();
  }
 }
 
 private void getCalldetailsNow() {
  // TODO Auto-generated method stub
  String simserailno="";
  int roaming=0;
  try {
		    TelephonyManager tmgr  = (TelephonyManager)c.getSystemService(Context.TELEPHONY_SERVICE);
			Cursor managedCursor=c.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, android.provider.CallLog.Calls.DATE + " DESC");
	        int number = managedCursor.getColumnIndex( CallLog.Calls.NUMBER ); 
	        int duration1 = managedCursor.getColumnIndex( CallLog.Calls.DURATION);
	        int type1=managedCursor.getColumnIndex(CallLog.Calls.TYPE);
	        int date1=managedCursor.getColumnIndex(CallLog.Calls.DATE);
	        String callid = "0";
	        if( managedCursor.moveToFirst() == true ) {
	            String phNumber = managedCursor.getString(number);
	            String callDuration = managedCursor.getString(duration1);
	            
	            String type=managedCursor.getString(type1);
	            String date=managedCursor.getString(date1);
	            
	            String dir = null;
	      int dircode = Integer.parseInt(type);
	      switch (dircode)
	      { 
	      case CallLog.Calls.OUTGOING_TYPE:
	       dir = "OUTGOING";
	       break;
	      case CallLog.Calls.INCOMING_TYPE:
	       dir = "INCOMING";
	       break;
	      case CallLog.Calls.MISSED_TYPE:
	       dir = "MISSED";
	       break;
	      default: 
	       dir = "MISSED";
	       break;
	      }

				Toast.makeText(c, phNumber ,Toast.LENGTH_LONG).show();
				SimpleDateFormat sdf_date = new SimpleDateFormat("dd/MM/yyyy");
	            SimpleDateFormat sdf_time = new SimpleDateFormat("h:mm a");
			   String dateString = sdf_date.format(new Date(Long.parseLong(date)));
			   String timeString = sdf_time.format(new Date(Long.parseLong(date)));
	      	   SharedPreferences sh=PreferenceManager.getDefaultSharedPreferences(c);
	      	   TelephonyManager tm=(TelephonyManager)c.getSystemService(Context.TELEPHONY_SERVICE); 
	      	   String id=tm.getDeviceId().toString();
	           DBHelper db=new DBHelper(c, "passmatrix", null, 2);
			   String result=   db.insertCalldata(id,phNumber, dateString, timeString, callDuration, dir,name);
	           Toast.makeText(c, result ,Toast.LENGTH_LONG).show();
	      	   callimei=simserailno;
	      	   callphone=tmgr.getLine1Number();
	      
	        }
	        
	        if(sim1imei.equalsIgnoreCase(callimei)){
	        	
	        	
	        }
	        managedCursor.close();
} catch (Exception e) {
	// TODO: handle exception
	Log.d("err his", e.toString()+"");

	  Toast.makeText(c, e.toString() ,Toast.LENGTH_LONG).show();

  }
 }

}
