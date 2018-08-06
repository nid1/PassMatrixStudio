 package com.example.passmatrix;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;


import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import static java.lang.System.currentTimeMillis;

 public class LocationService extends Service {

	 private LocationManager locationManager;
	    private Boolean locationChanged;

	    public static int tmp=0,flg=0;
	    Date Date2=null;
	    private Handler handler = new Handler();
	    public static Location curLocation;
	    public static boolean isService = true;
	    public String temp="";
	    String ip="";
	    String[] zone;
	    String pc="",status1="";
	    public static long tmpdt=0;

	    public static String phoneid="",place="Kozhikode",address="",lati="12.45444",logi="75.576544";
//	    static String namespace="http://dbcon/";
		String url="";
		SharedPreferences sh;

//		static String METHOD_LOCATION = "location";
//		static String SOAP_LOCATION = namespace+"location";	

		LocationListener locationListener = new LocationListener() {

	        public void onLocationChanged(Location location) {
	            if (curLocation == null) {
	                curLocation = location;
	                locationChanged = true;
	            }
	            else if (curLocation.getLatitude() == location.getLatitude() && curLocation.getLongitude() == location.getLongitude()){
	                locationChanged = false;
	                return;
	            }
	            else
	                locationChanged = true;
	                curLocation = location;
	            if (locationChanged)
	                locationManager.removeUpdates(locationListener);
	        }
	        public void onProviderDisabled(String provider) {}

	        public void onProviderEnabled(String provider) {}

	        @Override
			public void onStatusChanged(String provider, int status,Bundle extras) {
				 if (status == 0)// UnAvailable
		            {
		            } else if (status == 1)// Trying to Connect
		            {
		            } else if (status == 2) {// Available
		            }
			}
	    };

	@Override
	public void onCreate() {
		 super.onCreate();
	        curLocation = getBestLocation();

	        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss aa");
      		String dt=format.format(new Date());
            try {
				 Date2 = format.parse(dt);
			} catch (ParseException e) {
				e.printStackTrace();
			}
	        if (curLocation == null){
	        	System.out.println("starting problem.........3...");
	        	Toast.makeText(this, "GPS problem..........", Toast.LENGTH_SHORT).show();
	       }
	        else{
	         	// Log.d("ssssssssssss", String.valueOf("latitude2.........."+curLocation.getLatitude()));
	        }
	        isService =  true;
	    }
	    final String TAG="LocationService";
	    @Override
	    public int onStartCommand(Intent intent, int flags, int startId) {
	    	return super.onStartCommand(intent, flags, startId);
	   }
	   @Override

	   public void onLowMemory() {
	       super.onLowMemory();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Toast.makeText(this, "Start services", Toast.LENGTH_SHORT).show();

		sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		  String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

		  if(!provider.contains("gps"))
	        { //if gps is disabled
	        	final Intent poke = new Intent();
	        	poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
	        	poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
	        	poke.setData(Uri.parse("3"));
	        	sendBroadcast(poke);
	        }
		flg=0;
	      handler.postDelayed(GpsFinder,100);
	}

	@Override
	public void onDestroy() {

		   String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

		   if(provider.contains("gps"))
		   { //if gps is enabled
		   final Intent poke = new Intent();
		   poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
		   poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
		   poke.setData(Uri.parse("3"));
		   sendBroadcast(poke);
		   }

		   handler.removeCallbacks(GpsFinder);
	       handler = null;
	       Toast.makeText(this, "Service Stopped..!!", Toast.LENGTH_SHORT).show();
	       isService = false;
	   }


	  public Runnable GpsFinder = new Runnable()
	  {
	    public void run(){
	    	String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

	  	  if(!provider.contains("gps"))
          { //if gps is disabled
          	final Intent poke = new Intent();
          	poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
          	poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
          	poke.setData(Uri.parse("3"));
          	sendBroadcast(poke);
          }

	  	TelephonyManager telephonyManager  = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
	    phoneid=telephonyManager.getDeviceId().toString();

	    Location tempLoc = getBestLocation();
	    	if(tempLoc!=null)
	        {
          		String logid = sh.getString("logid", "0");
          		String url = sh.getString("url", "");
				curLocation = tempLoc;
	            lati=String.valueOf(curLocation.getLatitude());
	            logi=String.valueOf(curLocation.getLongitude());
	            saveLocation();
//	            Toast.makeText(getApplicationContext(),phoneid+"\nlat.. and longi.."+ lati+"..."+logi, Toast.LENGTH_SHORT).show();
				saveActivity(status1);
				appUsageEntry();
	            Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
	            try
	            {
	            List<Address> addresses = geoCoder.getFromLocation(curLocation.getLatitude(), curLocation.getLongitude(), 1);
	            if (addresses.size() > 0)
	            {
	            	place=addresses.get(0).getFeatureName().toString();
	            	temp=place;

	            	SimpleDateFormat t=new SimpleDateFormat("hh:mm:ss a");
	                String tm=t.format(new Date());

	           }
	          }
	          catch (IOException e)
	          {
	            e.printStackTrace();
	          }
	            Toast.makeText(getBaseContext(), "locality-"+place, Toast.LENGTH_SHORT).show();

	        }

	    	handler.postDelayed(GpsFinder,70000);// register again to start after 120 seconds...
	    }
	  };

	  	private Location getBestLocation() {
	        Location gpslocation = null;
	        Location networkLocation = null;
	        if(locationManager==null){
	          locationManager = (LocationManager) getApplicationContext() .getSystemService(Context.LOCATION_SERVICE);
	        }
	        try
	        {
	            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
	            {
	            	 locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000, 0, locationListener);// here you can set the 2nd argument time interval also that after how much time it will get the gps location
	                gpslocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	            }
	            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
	                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000, 0, locationListener);
	                networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	            }
	        } catch (IllegalArgumentException e) {
	            Log.e("error", e.toString());
	        }
	        if(gpslocation==null && networkLocation==null)
	            return null;

	        if(gpslocation!=null && networkLocation!=null){
	            if(gpslocation.getTime() < networkLocation.getTime()){
	                gpslocation = null;
	                return networkLocation;
	            }else{
	                networkLocation = null;
	                return gpslocation;
	            }
	        }
	        if (gpslocation == null) {
	            return networkLocation;
	        }
	        if (networkLocation == null) {
	            return gpslocation;
	        }
	        return null;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	 public void saveActivity(String act) {
	    	SQLiteDatabase obj=openOrCreateDatabase("passmatrix", SQLiteDatabase.CREATE_IF_NECESSARY,null);
		 	int loccoun=0;
			String qry="create table if not exists location(id integer PRIMARY KEY AUTOINCREMENT,place text,acount integer,adate text,atm text)";
			obj.execSQL(qry);
			SimpleDateFormat sm=new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat st=new SimpleDateFormat("hh:mm:ss a");
			String dt=sm.format(new Date());
			String tm=DBHelper.getTime();
			String query="select * from location where place='"+place+"' and adate='"+dt+"' and atm='"+tm+"'";
			Cursor cr=obj.rawQuery(query, null);
			Log.d("",cr.getCount()+"");

			if(cr!=null&&cr.getCount()>0)
			{
			while(cr.moveToNext()){
				Log.d("",cr.getString(2)+"");
				loccoun=cr.getInt(2)+2;
				ContentValues cv=new ContentValues();
				cv.put("place", place);
				cv.put("acount", cr.getInt(2)+2);
				cv.put("adate", dt);
				cv.put("atm",  tm);
				obj.update("location",cv, "id=?", new String[]{cr.getString(0)});
				}
			}
			else{
				loccoun=1;
			ContentValues cv=new ContentValues();
			cv.put("place", place);
			cv.put("acount", 1);
			cv.put("adate", dt);
			cv.put("atm", tm);
			obj.insert("location", null, cv);
			}
			cr.close();
			obj.close();


//		 Toast.makeText(getApplicationContext(),loccoun+"place='"+place+"' and adate='"+dt+"' and atm='"+tm+"'",Toast.LENGTH_SHORT).show();
		}



	 public void saveLocation() {

//		    SQLiteDatabase sdb=openOrCreateDatabase("SmartDiary", SQLiteDatabase.CREATE_IF_NECESSARY, null);
//			String qry="CREATE TABLE IF NOT EXISTS location(id integer primary key autoincrement,uid integer,place text,latitude text, longitude text,imei text,dt text,tm text)";
//			sdb.execSQL(qry);
//
//			SimpleDateFormat sm=new SimpleDateFormat("dd/MM/yyyy");
//			SimpleDateFormat st=new SimpleDateFormat("hh:mm:ss a");
//			String dt=sm.format(new Date());
//			Calendar c = Calendar.getInstance();
//	        int seconds = c.get(Calendar.SECOND);
//	        String sc=DBHelper.getTime();
//
//
//			//String tm=sm.format(new Date());
//	        String tm=sc;
//
//
//				ContentValues cv=new ContentValues();
//
//				cv.put("place",place);
//				cv.put("latitude",lati);
//				cv.put("longitude",logi);
//				cv.put("dt", dt);
//				cv.put("tm", tm);
//				sdb.insert("location", null, cv);
//				sdb.close();

	 }


	public  void appUsageEntry()
	{
		UsageStatsManager usm = (UsageStatsManager) getApplicationContext().getSystemService("usagestats");
		long time = currentTimeMillis();

		Toast.makeText(getApplicationContext(),time+"----",Toast.LENGTH_SHORT).show();
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0);

		List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, System.currentTimeMillis()-10000, System.currentTimeMillis());
		Toast.makeText(getApplicationContext(),appList.size()+"----aPP List",Toast.LENGTH_SHORT).show();

		if (appList != null && appList.size() > 0) {
			SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
			for (UsageStats usageStats : appList) {
				mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
				Log.d("++++++++++", usageStats.getPackageName());
			}
			if (mySortedMap != null && !mySortedMap.isEmpty()) {
				int ts= mySortedMap.keySet().size();
				List<Long>l=new ArrayList<Long>();
				for(int k=0;k<ts;k++)
				{
					l.add((long)mySortedMap.keySet().toArray()[k]);

				}
				Toast.makeText(getApplicationContext(), mySortedMap.get(l.get(ts-3)).getPackageName(),Toast.LENGTH_SHORT).show();
				Collections.sort(l);
				Log.d("---------------------", mySortedMap.get(l.get(ts-3)).getPackageName());
				insertAppUsage(mySortedMap.get(l.get(ts-3)).getPackageName());
			}
		}


	}

	 private void insertAppUsage(String packageName) {
		 SQLiteDatabase obj=openOrCreateDatabase("passmatrix", SQLiteDatabase.CREATE_IF_NECESSARY,null);
		 String qry="CREATE TABLE IF NOT EXISTS appUsage(id integer primary key autoincrement,appname text,dt text,tm text,appcount text)";
		 obj.execSQL(qry);

		 SimpleDateFormat sm=new SimpleDateFormat("dd/MM/yyyy");
		 SimpleDateFormat st=new SimpleDateFormat("hh:mm:ss a");
		 String dt=sm.format(new Date());
		 String tm=DBHelper.getTime();
		 String query="select * from appUsage where appname='"+packageName+"' and dt='"+dt+"' and tm='"+tm+"'";
		 Cursor cr=obj.rawQuery(query, null);
		 Log.d("",cr.getCount()+"");
		int app=0;
		 if(cr!=null&&cr.getCount()>0)
		 {
			 while(cr.moveToNext()){
				 Log.d("",cr.getString(2)+"");
				 app=cr.getInt(4)+2;
				 ContentValues cv=new ContentValues();
				 cv.put("appname", packageName);
				 cv.put("appcount", cr.getInt(4)+2);
				 cv.put("dt", dt);
				 cv.put("tm",  tm);
				 obj.update("appUsage",cv, "id=?", new String[]{cr.getString(0)});
			 }
		 }
		 else{
			 ContentValues cv=new ContentValues();
			 cv.put("appname", packageName);
			 cv.put("appcount", 1);
			 cv.put("dt", dt);
			 cv.put("tm",  tm);
			 obj.insert("appUsage", null, cv);
		 }
		// Toast.makeText(getApplicationContext(),app+" app='"+packageName+"' and adate='"+dt+"' and atm='"+tm+"'",Toast.LENGTH_SHORT).show();
		 cr.close();
		 obj.close();
	 }


 }

	

