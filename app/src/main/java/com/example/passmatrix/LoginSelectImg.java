package com.example.passmatrix;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Random;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginSelectImg extends Activity implements OnItemClickListener,OnClickListener{
	ProgressDialog mProgressDialog;
	private PowerManager.WakeLock mWakeLock;
	static final int DIALOG_DOWNLOAD_PROGRESS = 2;
	
	GridView gd;
	TextView tv;
	
	static String namespace="http://shoulder/";
	static String method = "getImages";
	static String method2 = "checkfiles";
	static String method3 = "forgotImg";
	static String soapaction = namespace + method;
	static String soapaction2 =namespace + method2;
	static String soapaction3 =namespace + method3;
	String[] nm,grd,fldr; 
	String[] files={"","","","","","","","",""};
	String[] filefldr={"","","","","","","","",""};
	String[] filegrd={"0","0","0","0","0","0","0","0","0"};
	SharedPreferences sh;
	String URL="",imei="",fpth="",fname="",fgrid="",ip="";
	int nofile=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login1);
		
		QrScan.x++;
		
		gd=(GridView)findViewById(R.id.gridView1);
		tv=(TextView)findViewById(R.id.textView1);
		TelephonyManager tm=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
        imei=tm.getDeviceId().toString();
        sh=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        URL=sh.getString("url", "");
        ip=sh.getString("ip", "");
        nofile=sh.getInt("nofile", 0);
        
        tv.setOnClickListener(this);
		try
		{
			if(android.os.Build.VERSION.SDK_INT>9)
			{
				  StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
				  StrictMode.setThreadPolicy(policy);
			}
		}
		catch(Exception e){}
        if(nofile==0){

			//Toast.makeText(getApplicationContext(),"cjj",Toast.LENGTH_LONG).show();
//        	Intent i=new Intent(getApplicationContext(), Login.class);
//			startActivity(i);
        }
        if(QrScan.x>(nofile*2))
        {	
        	Intent i=new Intent(getApplicationContext(), Userhome.class);
			startActivity(i);
        }
        else
        {
		try
		{
			SoapObject soapObject = new SoapObject(namespace, method);
			soapObject.addProperty("imei",imei);
			soapObject.addProperty("pos",QrScan.x);
			
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(soapObject);
//			envelope.dotNet=true;
			
			HttpTransportSE se = new HttpTransportSE(URL);
			se.call(soapaction, envelope);
			
			String res = envelope.getResponse().toString();
			Log.d("=============", res+"---");
//			Toast.makeText(getApplicationContext(), res+"=-=-=-", Toast.LENGTH_SHORT).show();
			if(res.equalsIgnoreCase("no")||res.equalsIgnoreCase("anytype{}")){
				Toast.makeText(getApplicationContext(), "no data..!!", Toast.LENGTH_SHORT).show();
			}
			else{
				String a[]=res.split("~");
				fldr=new String[a.length];
				nm=new String[a.length];
				grd=new String[a.length];
				if(a.length>=10){
				for (int i = 0; i < 10; i++) {
					String b[]=a[i].split("#");
					nm[i]=b[0];
					fldr[i]=b[1];
					grd[i]=b[2];
				}
				
				Random r=new Random();
				for(int i=0;i<9;i++)
			    {
			        int k=r.nextInt(9);
			        if(!Arrays.asList(files).contains(nm[k]))
			        {
			          	files[i]=nm[k];
			          	filefldr[i]=fldr[k];
			           	filegrd[i]=grd[k];
			            System.out.println(""+files[i]);
			        }
			        else{
			             i--;
			        }
			    }
				gd.setAdapter(new LoginImageAdapter(getApplicationContext(),files,filefldr));
				
				}
			}
		}
		catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.getMessage()+"--", Toast.LENGTH_SHORT).show();
		}
        }
        
		gd.setOnItemClickListener(this);
		
//		  DisplayMetrics displaymetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//        int height = displaymetrics.heightPixels;
//        int width = displaymetrics.widthPixels;
//
//        float density  = getResources().getDisplayMetrics().density;
//        float dpHeight = height / density;
//        float dpWidth  = width / density;
//
//        float scale = getApplicationContext().getResources().getDisplayMetrics().density;
//        int dpw=(width-(int)5f)/(int)scale;
//        int dph=(height-(int)5f)/(int)scale;
//		
//        Toast.makeText(getApplicationContext(),dpHeight+"--"+dpWidth,Toast.LENGTH_SHORT).show();
//        Toast.makeText(getApplicationContext(),dph+"--"+dpw,Toast.LENGTH_SHORT).show();
//
//        Log.d("---", dpHeight+"--"+dpWidth);
//        Log.d("---", dph+"--"+dpw);
        
        //================================================
//        ViewGroup.LayoutParams layoutParams = gd.getLayoutParams();
//        layoutParams.width =(int) dpw ; //this is in pixels
//        gd.setLayoutParams(layoutParams);
        
//        int wd=dpw/7;
//        int hd=dpw/8;
//        
//			Bitmap bmp=BitmapFactory.decodeResource(getResources(), R.drawable.orange);
//			gd.setAdapter(new LoginImageAdapter(getApplicationContext(),bmp,wd,hd));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login1, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		try
		{
//			SoapObject soapObject = new SoapObject(namespace, method2);
//			soapObject.addProperty("imei",imei);
//			soapObject.addProperty("file",files[arg2]);
//			soapObject.addProperty("indx",QrScan.x+"");
//			
//			Log.d("-------------", files[arg2]+"---"+QrScan.x);
//			
//			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//			envelope.setOutputSoapObject(soapObject);
//			Log.d("-------------", "-----1---------------");
//			Log.d("-------------", URL+"--------");
//			
//			HttpTransportSE se = new HttpTransportSE(URL);
//			se.call(soapaction2, envelope);
//			Log.d("-------------", "-----2---------------");
//			String res = envelope.getResponse().toString();
//			if(res.equalsIgnoreCase("ok"))
			if(!filegrd[arg2].equalsIgnoreCase("555"))
			{
//				if(QrScan.x==nofile){
//					Intent i=new Intent(getApplicationContext(), Userhome.class);
//					startActivity(i);
//				}
//				else
//				{
					//========================================================================
					fpth="http://"+ip+":8084/PassMatrixs/waterupfiles/"+files[arg2];				
					fname=files[arg2];
					fgrid=filegrd[arg2];
					new DownloadFileAsync().execute();
					//========================================================================					
//				}
			}
			else{
				Toast.makeText(getApplicationContext(), "Unknown User..!!", Toast.LENGTH_SHORT).show();
				Intent i=new Intent(getApplicationContext(), Login.class);
				startActivity(i);
			}
		}
		catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.getMessage()+"--", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void onBackPressed() {
		Intent i=new Intent(getApplicationContext(), Login.class);
		startActivity(i);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
	switch (id) {
		case DIALOG_DOWNLOAD_PROGRESS:
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage("Loading File...");
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
			return mProgressDialog;
	}
	return null;
	}

	class DownloadFileAsync extends AsyncTask<String, String, String> {

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	   		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
	    		mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,getClass().getName());
	    		mWakeLock.acquire();
		showDialog(DIALOG_DOWNLOAD_PROGRESS);
	}

	public void execute(String url) {
	}
	
	@Override
	protected String doInBackground(String... aurl) {
	int count;
	String s="no";
	try 
	{
		URL url = new URL(fpth);
		Log.d("=========", fpth+"------------");
		Log.d("=========", fpth+"------------");
		URLConnection conexion = url.openConnection();
		conexion.connect();

		int lenghtOfFile = conexion.getContentLength();
		Log.d("ANDRO_ASYNC", "Length of file: " + lenghtOfFile);

		File fil =new File(android.os.Environment.getExternalStorageDirectory(),"Passmatrix");
		if(!fil.exists())
		{
		  	fil.mkdirs();
		}		
	
		InputStream input = new BufferedInputStream(url.openStream());
		OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/Passmatrix/" + fname );
		Log.d("zzzzzzzzzz", "-----------------");

		byte data[] = new byte[1024];
		long total = 0;

			while ((count = input.read(data)) != -1) {
				total += count;
				publishProgress(""+(int)((total*100)/lenghtOfFile));
				output.write(data, 0, count);
			}
			output.flush();
			output.close();
			input.close();
		
			s="ok";
		} catch (Exception e) {
			Log.d("===xxxxxxxxxxxxx======", e.getMessage()+"------------");
			e.printStackTrace();
		}
		return s;
	}
	
	protected void onProgressUpdate(String... progress) {
		 Log.d("ANDRO_ASYNC",progress[0]);
		 mProgressDialog.setProgress(Integer.parseInt(progress[0]));
	}

	@Override
	protected void onPostExecute(String x) {
		dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
		if(x.equalsIgnoreCase("ok"))
		{
			Toast.makeText(getApplicationContext(), "Select Grid", Toast.LENGTH_SHORT).show();
			Editor ed=sh.edit();
			ed.putString("fnm", fname);
			ed.putString("grid", fgrid);
			ed.commit();
			
			Intent i=new Intent(getApplicationContext(), LoginMain.class);
			//i.putExtra("filename", fname);
			startActivity(i);
		}
		else{
			Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
			Intent i=new Intent(getApplicationContext(), Login.class);
			startActivity(i);
		}
	}
	}
	
	@Override
	public void onClick(View arg0) {
		try
		{
			SoapObject soapObject = new SoapObject(namespace, method3);
			soapObject.addProperty("imei",imei);
			soapObject.addProperty("pos",QrScan.x+"");
			
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(soapObject);
			
			HttpTransportSE se = new HttpTransportSE(URL);
			se.call(soapaction3, envelope);
			
			//String res = envelope.getResponse().toString();
			Log.d("=============", "---");
			Toast.makeText(getApplicationContext(), "Request Sent.\nCheck Your Mail", Toast.LENGTH_SHORT).show();
		}
		catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Try Again", Toast.LENGTH_SHORT).show();
		}
	}
}
