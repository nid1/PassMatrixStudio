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
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Userhome extends Activity {
	Button b1;
	String URL="";
	SharedPreferences sh;
	
	static String namespace="http://shoulder/";
	static String method = "setlog";
	static String soapaction = namespace + "setlog";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userhome);
		
		b1=(Button)findViewById(R.id.button1);

		sh=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		URL=sh.getString("url", "");

		startService(new Intent(getApplicationContext(), BehaviourCheck.class));
		//setlog();
		
		b1.setOnClickListener(new View.OnClickListener() 
		{		
			@Override
			public void onClick(View arg0) 
			{
				Intent i=new Intent(getApplicationContext(), Login.class);
				startActivity(i);


				stopService(new Intent(getApplicationContext(),BehaviourCheck.class));


			}
		});
		
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
			Toast.makeText(getApplicationContext(), "sending....", Toast.LENGTH_SHORT).show();
			

			HttpTransportSE se = new HttpTransportSE(URL);
			se.call(soapaction, envelope);
			
			String res = envelope.getResponse().toString();
			Toast.makeText(getApplicationContext(), res+"===", Toast.LENGTH_SHORT).show();
			
			Intent i=new Intent(getApplicationContext(), Login.class);
			startActivity(i);
									
		} 
		catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.getMessage()+"--", Toast.LENGTH_SHORT).show();
		}
	}
}
