package com.example.passmatrix;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity 
{
	public static int xx=1;
	public static int kk=0;
	
	TextView tv;
	EditText e1,e2;
	Button b1;
	String URL="";
	String namespace="http://shoulder/";
	String method = "login";
	String soapaction = namespace + method;
	String method2 = "checkuser";
	String soapaction2 = namespace + method;
	
	SharedPreferences sh;
	Handler hd=new Handler();
	String imei="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		e1=(EditText)findViewById(R.id.ed1);
		e2=(EditText)findViewById(R.id.ed2);
		b1=(Button)findViewById(R.id.button1);
		tv=(TextView)findViewById(R.id.textView1);
		
		xx=1;
//		e1.setText("kir");
		stopService(new Intent(getApplicationContext(),BehaviourCheck.class));
		TelephonyManager tm=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
        imei=tm.getDeviceId().toString();
        
		sh=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		URL=sh.getString("url", "");
		try
		{
		  if(android.os.Build.VERSION.SDK_INT>9)
		  {
			  StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
			  StrictMode.setThreadPolicy(policy);
		  }
		}
		catch(Exception e)
		{}
		if (UStats.getUsageStatsList(this).isEmpty()){
			Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
			startActivity(intent);
		}
		checkuser();
		
//		e1.setText("riss.kiran@gmail.com");
//		e2.setText("212121");

		tv.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				Intent i=new Intent(getApplicationContext(), Ipsettings.class);
				startActivity(i);
			}
		});
		
		b1.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				String s=e1.getText().toString().trim();				
				String t=e2.getText().toString().trim();
		startService(new Intent(getApplicationContext(), LocationService.class));

						if(s.equalsIgnoreCase(""))
				{
					e1.setError("Username");
				}
				else if(t.equalsIgnoreCase(""))
				{
					e2.setError("Password");
				}
				else if(sh.getInt("trial", 0)>2){
					Toast.makeText(getApplicationContext(), "Expires..!!", Toast.LENGTH_SHORT).show();	
					hd.postDelayed(r, 30000);
				}
				else
				{
			try
			{
				SoapObject soapObject = new SoapObject(namespace, method);
				soapObject.addProperty("imei",imei);
				soapObject.addProperty("unm",s);
				soapObject.addProperty("pw",t);
				
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
				envelope.setOutputSoapObject(soapObject);
//				envelope.dotNet=true;
				
				HttpTransportSE se = new HttpTransportSE(URL);
				se.call(soapaction, envelope);
				
				String res = envelope.getResponse().toString();
				if(res.equalsIgnoreCase("no")||res.equalsIgnoreCase("0")){
					
					Toast.makeText(getApplicationContext(), "Unknown User..!!", Toast.LENGTH_SHORT).show();
					kk=sh.getInt("trial", 0);					
					kk++;
					Log.d("kk-------------",kk+ "");
					Editor e=sh.edit();     
					e.putInt("trial",kk);
					e.commit();
					if(kk>2){
						Toast.makeText(getApplicationContext(), "Try Again After 30 seconds", Toast.LENGTH_SHORT).show();
					}
					hd.postDelayed(r, 30000);
				}
				else
				{
					Editor e=sh.edit();     
					e.putInt("nofile",Integer.parseInt(res));
					e.commit();

					Intent i=new Intent(getApplicationContext(), QrScan.class);
					startActivity(i);
				}
			}
			catch (Exception e) {
				Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
			}
			}
			}
		});		
	}
	
	Runnable r=new Runnable() {		
		@Override
		public void run() {
			Editor e=sh.edit();     
			e.putInt("trial",0);
			e.commit();
		}
	};
	
	private void checkuser() {
		try
		{
			SoapObject soapObject = new SoapObject(namespace, method2);
			soapObject.addProperty("imei",imei);
			
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(soapObject);
			
			HttpTransportSE se = new HttpTransportSE(URL);
			se.call(soapaction2, envelope);
			String res = envelope.getResponse().toString();
			
//			Toast.makeText(getApplicationContext(), res+"", Toast.LENGTH_SHORT).show();
			if(res.equalsIgnoreCase("no"))
			{
				Toast.makeText(getApplicationContext(), "New User..!!", Toast.LENGTH_SHORT).show();
				if(sh.getString("ip", "").equalsIgnoreCase("")){
					Intent i=new Intent(getApplicationContext(), Ipsettings.class);
					startActivity(i);
				} 
				else{
					Intent i=new Intent(getApplicationContext(), Register.class);
					startActivity(i);
				}
			}
			if(res.equalsIgnoreCase("na")){
				Toast.makeText(getApplicationContext(), "Server Error..!!", Toast.LENGTH_SHORT).show();				
			}
			else
			{}
		}
		catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Server Error...!!", Toast.LENGTH_SHORT).show();
//			Intent i=new Intent(getApplicationContext(), Ipsettings.class);
//			startActivity(i);
		}
	}
	
	@Override
	public void onBackPressed(){
//		Intent i=new Intent(getApplicationContext(), Ipsettings.class);
//		startActivity(i);


		stopService(new Intent(getApplicationContext(),BehaviourCheck.class));

		Intent i=new Intent(Intent.ACTION_MAIN);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addCategory(Intent.CATEGORY_HOME);
		startActivity(i);
	}
	
//	Runnable r=new Runnable() {
//		public void run() {
//			if(kk<3)
//			{
//				try {
//					Thread.sleep(5000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				if(kk<3){
//					kk++;
//					hd.postDelayed(r, 10000);
//				}
//			}
//		}
//	};
}
