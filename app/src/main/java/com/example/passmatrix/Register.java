package com.example.passmatrix;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends Activity 
{
	EditText e1,e2,e3,e4,ed1,ed2,ed3;
	Button b1,b2;
	String URL="";
	static String namespace="http://shoulder/";
	static String method = "userreg";
	static String soapaction = namespace + "userreg";
	public static int lmt=0;
	SharedPreferences sh;
	Handler hd=new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		ed1=(EditText)findViewById(R.id.editText1);
		ed2=(EditText)findViewById(R.id.editText2);
		ed3=(EditText)findViewById(R.id.editText3);
		e1=(EditText)findViewById(R.id.ed1);
		e2=(EditText)findViewById(R.id.ed2);
		e3=(EditText)findViewById(R.id.ed3);
		e4=(EditText)findViewById(R.id.ed4);
		
		b1=(Button)findViewById(R.id.button1);
		b2=(Button)findViewById(R.id.button2);
		b1.setVisibility(View.VISIBLE);
		b2.setVisibility(View.INVISIBLE);
		
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
		
		b1.setOnClickListener(new View.OnClickListener() 
		{		
			@Override
			public void onClick(View arg0) 
			{
				String nm=ed1.getText().toString().trim();
				String adr=ed2.getText().toString().trim();
				String mb=ed3.getText().toString().trim();
				String s=e1.getText().toString().trim();
				String t=e2.getText().toString().trim();
				String pw=e3.getText().toString().trim();
				String cpw=e4.getText().toString().trim();

				TelephonyManager tm=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
		        String imei=tm.getDeviceId().toString();
		        
		        if(nm.equalsIgnoreCase(""))
				{
					ed1.setError("Name");
				}
		        else if(adr.equalsIgnoreCase(""))
				{
					ed2.setError("Address");
				}
		        else if(mb.equalsIgnoreCase(""))
				{
					ed3.setError("Phone");
				}
		        else if(mb.length()<10)
				{
					ed3.setError("Incorrect Phone");
				}
		        else if(s.equalsIgnoreCase(""))
				{
					e1.setError("Username");
				}
		        else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()){
		        	e1.setError("Incorrect Email");
		        }
				else if(pw.equalsIgnoreCase(""))
				{
					e3.setError("Password");
				}
				else if(cpw.equalsIgnoreCase(""))
				{
					e4.setError("Confirm Password");
				}
				else if(!pw.equals(cpw))
				{
					e4.setError("Password Missmatch");
				}
				else if(t.equalsIgnoreCase(""))
				{
					e2.setError("Number of Images");
				}
				else if(t.equalsIgnoreCase("0"))
				{
					e2.setError("Number of Images");
				}
				else{
			try
			{	
				SoapObject soapObject = new SoapObject(namespace, method);
				soapObject.addProperty("imei",imei);
				soapObject.addProperty("unm",s);
				soapObject.addProperty("no",t);
				soapObject.addProperty("pw",pw);
				soapObject.addProperty("nm",nm);
				soapObject.addProperty("ad",adr);
				soapObject.addProperty("mb",mb);				
				
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
				envelope.setOutputSoapObject(soapObject);
				
				HttpTransportSE se = new HttpTransportSE(URL);
				se.call(soapaction, envelope);
				
				String res = envelope.getResponse().toString();
				if(res.equalsIgnoreCase("ok"))
				{
					Toast.makeText(getApplicationContext(), "Succss.\nClick Next to Continue", Toast.LENGTH_SHORT).show();
					Editor e=sh.edit();     
					e.putInt("nofile",Integer.parseInt(t));
					e.commit();
					lmt=Integer.parseInt(t)*2;
					b1.setVisibility(View.INVISIBLE);
					b2.setVisibility(View.VISIBLE);
				}
				else if(res.equalsIgnoreCase("na")){
					Toast.makeText(getApplicationContext(), "User Already Exists", Toast.LENGTH_SHORT).show();
				}
				else if(res.equalsIgnoreCase("no")){
					Toast.makeText(getApplicationContext(), "Insertion error", Toast.LENGTH_SHORT).show();
				}							
			} 
			catch (Exception e) {
				Toast.makeText(getApplicationContext(), e.getMessage()+"--", Toast.LENGTH_SHORT).show();
			}
			}
			}
		});
		
		b2.setOnClickListener(new View.OnClickListener() 
		{		
			@Override
			public void onClick(View arg0) 
			{
				Login.xx=0;
//				String a[]={"Gallery","Stored Images","Cancel"};
//				AlertDialog.Builder ad=new AlertDialog.Builder(Register.this);
//				ad.setTitle("Select Your Option");
//				ad.setItems(a, new DialogInterface.OnClickListener() {
//					
//					@Override
//					public void onClick(DialogInterface arg0, int arg1) {
//						if(arg1==0)
//						{
//							Intent i=new Intent(getApplicationContext(), Images.class);
//							startActivity(i);
//						}
//						else if(arg1==1){
							Intent i=new Intent(getApplicationContext(), SelectImage.class);
							startActivity(i);								
//						}						
//					}
//				});
//				AlertDialog al=ad.create();
//				al.show();				
			}
		});
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
