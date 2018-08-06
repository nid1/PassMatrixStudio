package com.example.passmatrix;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Ipsettings extends Activity 
{
	EditText e1;
	Button b1;
	public static String URL="";
	public static String ip="";
	SharedPreferences sh;
	String namespace="http://shoulder/";
	public static int kk=1;
	Handler hd=new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ipsettings);


		
		e1=(EditText)findViewById(R.id.editText1);
		b1=(Button)findViewById(R.id.button1);
		sh=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		ip=sh.getString("ip", "");
		e1.setText(ip);


		//startService(new Intent(getApplicationContext(), LocationService.class));
		//startService(new Intent(getApplicationContext(), BehaviourCheck.class));
		b1.setOnClickListener(new View.OnClickListener() 
		{		
			@Override
			public void onClick(View arg0) 
			{
				ip=e1.getText().toString().trim();
				if(ip.equalsIgnoreCase("")){
					e1.setError("Enter Ip Address");
				}
				else{
					URL = "http://"+ip+":8084/PassMatrixs/web?wsdl";
				
					Editor e=sh.edit();
					e.putString("ip", ip);      
					e.putString("url", URL);
					e.commit();

					Intent i= new Intent(getApplicationContext(),Login.class);
					startActivity(i);
				}
			}
		});
	}
	
	@Override
	public void onBackPressed(){
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
//
//				if(kk<3){
//					kk++;
//					hd.postDelayed(r, 10000);
//				}
//			}
//		}
//	};
}
