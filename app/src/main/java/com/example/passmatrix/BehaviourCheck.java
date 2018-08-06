package com.example.passmatrix;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class BehaviourCheck extends Service{
	SQLiteDatabase sqd;
	Handler hd,hd1;
	int call=0,loc=0,app=0;
	String number="",calltype="",place="",appPack="";
	String[] dt,typ;
	static String namespace="http://shoulder/";
	static String method = "setlog";
	static String soapaction = namespace + "setlog";
	SharedPreferences sh;
	String URL="";
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		sh= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		URL=sh.getString("url", "");
		sqd=openOrCreateDatabase("passmatrix", SQLiteDatabase.CREATE_IF_NECESSARY, null);
		sqd.execSQL("CREATE TABLE IF NOT EXISTS call_logs(id integer PRIMARY KEY AUTOINCREMENT,number text,time text,date text,ccount text, type text)");
		String qry="CREATE TABLE IF NOT EXISTS appUsage(id integer primary key AUTOINCREMENT,appname text,dt text,tm text,appcount text)";
		sqd.execSQL(qry);
		qry="create table if not exists location(id integer PRIMARY KEY AUTOINCREMENT,place text,acount integer,adate text,atm text)";
		sqd.execSQL(qry);
		hd=new Handler();
		hd.post(r);
		hd1=new Handler();

	}
	
	public Runnable r=new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub

			SimpleDateFormat sm=new SimpleDateFormat("dd/MM/yyyy");
//
			Toast.makeText(getApplicationContext(),getdate(),Toast.LENGTH_LONG).show();
			if(!getdate().equalsIgnoreCase("no")) {


				String[] data=getdate().split("\\#");
				String dt = data[0];
				String tm = data[1];
				//String dtt = sm.format(new Date());
				////////////////////////////////////
				String sel_qry = "SELECT MAX(ccount) AS c,number,TYPE FROM call_logs   WHERE DATE='" + dt + "'  GROUP BY number ORDER BY c DESC";
				Cursor cr1 = sqd.rawQuery(sel_qry, null);
				if (cr1.getCount() > 0) {
					cr1.moveToFirst();
					call = cr1.getInt(0);
					number = cr1.getString(1);
					calltype = cr1.getString(2);
					//screantime=cr6.getString(1);
					Toast.makeText(getApplicationContext(), call + " --Call  " + number + "   " + calltype, Toast.LENGTH_LONG).show();

				}
				cr1.close();
				///////////////////////////////////
				////////////////////////////////////
				sel_qry = "SELECT MAX(acount) AS c ,place FROM location   WHERE adate='" + dt + "'  GROUP BY place ORDER BY c DESC";
				Cursor cr2 = sqd.rawQuery(sel_qry, null);
				if (cr2.getCount() > 0) {
					cr2.moveToFirst();
					loc = cr2.getInt(0);
					place = cr2.getString(1);
					//screantime=cr6.getString(1);
					Toast.makeText(getApplicationContext(), loc + " --Loc   " + place, Toast.LENGTH_LONG).show();

				}
				cr2.close();
				///////////////////////////////////
				////////////////////////////////////
				sel_qry = "SELECT MAX(appcount) AS c ,appname FROM appUsage   where dt='" + dt + "'  GROUP BY appname ORDER BY c DESC";
				Cursor cr3 = sqd.rawQuery(sel_qry, null);
				if (cr3.getCount() > 0) {
					cr3.moveToFirst();
					app = cr3.getInt(0);
					appPack = cr3.getString(1);
					//screantime=cr6.getString(1);
					Toast.makeText(getApplicationContext(), app + " --App" + appPack, Toast.LENGTH_LONG).show();

				}
				cr3.close();
				String titel = "", answr = "";
				///////////////////////////////////
				if (call > loc && call > app) {

					Toast.makeText(getApplicationContext(), call + "--", Toast.LENGTH_LONG).show();
					System.out.println("First number is largest.");
					titel = "Most called Number(" + calltype + ") is______________";
					answr = number;
					showalert(titel+"(date"+dt+")", answr);
				} else if (loc > call && loc > app) {
					Toast.makeText(getApplicationContext(), place + "--", Toast.LENGTH_LONG).show();
					System.out.println("Second number is largest.");

					titel = "Most visited Place  is";
					answr = place;
					showalert(titel, answr);
				} else if (app > call && app > loc) {
					Toast.makeText(getApplicationContext(), app + "--  " + appPack, Toast.LENGTH_LONG).show();
					System.out.println("Third number is largest.");

					titel = "Most Used App  is (Package name)";
					answr = appPack;
					showalert(titel, answr);
				}
			}

			hd.postDelayed(r, 50000);
		}
	};

	public Runnable r1=new Runnable() {
		@Override
		public void run() {
			Intent i=new Intent(getApplicationContext(), Login.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
		}
	};
	private String getdate() {

		String	sel_qry="SELECT * FROM call_logs";
		Cursor cr1=sqd.rawQuery(sel_qry, null);
		if(cr1.getCount()>0) {
			int cn=cr1.getCount();
			cr1.moveToFirst();
			dt=new String[cn];
			typ=new String[cn];
			int k=0;
			do {
				dt[k]=cr1.getString(3);
				typ[k]=cr1.getString(2);
				k++;
				cr1.moveToNext();
			}while (k<cn);


			Random ran = new Random();
			int x = ran.nextInt(k);
			return dt[x]+"#"+typ[x];
		}
			return "no";
	}

	private void showalert(String titel, final String answr) {
		hd1.postDelayed(r1,30000);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("PassMatrix");
		alert.setMessage(titel);

		// Set an EditText view to get user input
		final EditText input = new EditText(getApplicationContext());
		input.setTextColor(Color.BLACK);
		input.setHint("Your Answer.....");
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();

				if(value.equalsIgnoreCase(answr)){
					Toast.makeText(getApplicationContext(), "Right Answer", Toast.LENGTH_LONG).show();
					setlog();
					hd1.removeCallbacks(r1);
				}else{
					Toast.makeText(getApplicationContext(), "Wrong Answer", Toast.LENGTH_LONG).show();
					Intent i=new Intent(getApplicationContext(), Login.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(i);

				}

				// Do something with value!
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
				Intent i=new Intent(getApplicationContext(), Login.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
			}
		});
		//alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

		AlertDialog alertDialog = alert.create();
		alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		alertDialog.show();

	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		hd.removeCallbacks(r);
	}

	private void setlog() {
		TelephonyManager tm=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		String imei=tm.getDeviceId().toString();
		try
		{
			SoapObject soapObject = new SoapObject(namespace, method);
			soapObject.addProperty("imei",imei);
			soapObject.addProperty("status","1");

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(soapObject);



			HttpTransportSE se = new HttpTransportSE(URL);
			se.call(soapaction, envelope);

			String res = envelope.getResponse().toString();
			Toast.makeText(getApplicationContext(), res+"===", Toast.LENGTH_SHORT).show();

			Intent i=new Intent(getApplicationContext(), Login.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);

		}
		catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.getMessage()+"--", Toast.LENGTH_SHORT).show();
		}
	}
}
