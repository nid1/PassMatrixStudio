package com.example.passmatrix;

import java.util.ArrayList;
import java.util.Random;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginMain extends Activity implements OnItemClickListener
{
	TextView textTargetUri;
	ImageView targetImage;
	ArrayList<Bitmap> smallimages;
	ArrayList<Bitmap> fsmallimages;

	ArrayList<Integer> findx;
	SmallImageAdapter smad=null;
	GridView gd;
	Bitmap icon;
	TelephonyManager tm;

	Handler hd=new Handler();

	private static int kk=0;
	private Matrix matrix = new Matrix();
	private float scale = 1f;
	//    private ScaleGestureDetector SGD;
	DBHelper db;
	SharedPreferences sh;

	static String namespace="http://shoulder/";
	static String method = "checkfile";
	static String soapaction = namespace + "checkfile";
	static String method2 = "sendPwdgrid";
	static String soapaction2 = namespace + method2;
	String imei="",fname="",fgrid="",URL="",ip="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_main);

		gd=(GridView)findViewById(R.id.gridView1);
		gd.setOnItemClickListener(this);
		try
		{
			if(android.os.Build.VERSION.SDK_INT>9)
			{
				StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);
			}
		}
		catch(Exception e)
		{
		}
		db=new DBHelper(getApplicationContext(), "passmatrix", null, 2);

		sh=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		URL=sh.getString("url", "");
		ip=sh.getString("ip", "");

		fname=sh.getString("fnm","");
		fgrid=sh.getString("grid","");

		Editor e=sh.edit();
		e.putInt("mainflag",0);
		e.commit();

//		gd.setOnTouchListener(this);
//		SGD = new ScaleGestureDetector(this,new ScaleListener());

		icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.icon);

		textTargetUri = (TextView)findViewById(R.id.targeturi);
		targetImage = (ImageView)findViewById(R.id.targetimage);
		tm=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		imei=tm.getDeviceId().toString();

		String path=Environment.getExternalStorageDirectory().getPath()+"/Passmatrix/"+fname;
		textTargetUri.setText(path);

		Bitmap bitmap= BitmapFactory.decodeFile(path);
		targetImage.setImageBitmap(bitmap);

		gd.setVisibility(AdapterView.VISIBLE);
		splitImage(targetImage,getApplicationContext());
	}

	@Override
	public void onBackPressed() {

	}

	private void splitImage(ImageView image,  Context context)
	{
		int rows=6,cols=5;
		int smallimage_Numbers=rows*cols;

		//For height and width of the small image smallimage_s
		int smallimage_Height,smallimage_Width;

		//To store all the small image smallimage_s in bitmap format in this list
		smallimages = new ArrayList<Bitmap>(smallimage_Numbers);
		findx = new ArrayList<Integer>(smallimage_Numbers);
		fsmallimages = new ArrayList<Bitmap>(smallimage_Numbers);

		//Getting the scaled bitmap of the source image
		BitmapDrawable mydrawable = (BitmapDrawable) image.getDrawable();
		Bitmap bitmap = mydrawable.getBitmap();
		Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

		// rows = cols = (int) Math.sqrt(smallimage_Numbers);
		smallimage_Height = bitmap.getHeight()/rows;
		smallimage_Width = bitmap.getWidth()/cols;

		//xCo and yCo are the pixel positions of the image smallimage_s
		int yCo = 0;
		for(int x=0; x<rows; x++){
			int xCo = 0;
			for(int y=0; y<cols; y++){
				smallimages.add(Bitmap.createBitmap(scaledBitmap, xCo, yCo, smallimage_Width, smallimage_Height));
				xCo += smallimage_Width;
			}
			yCo+= smallimage_Height;
		}

		Random r=new Random();
		for(int i=0;i<smallimages.size();i++)
		{
			int k=r.nextInt(smallimages.size());
			if(!findx.contains(k))
			{
				findx.add(k);
				fsmallimages.add(smallimages.get(k));
				System.out.println("---"+smallimages.get(k));
			}
			else{
				i--;
			}
		}
		smad=new SmallImageAdapter(context, fsmallimages);
		gd.setAdapter(smad);
//		 smad=new SmallImageAdapter(context, smallimages);
//		 gd.setAdapter(smad);
		//gd.setNumColumns((int) Math.sqrt(smallimages.size()));
		gd.setNumColumns(cols);
	}

	private String sendtoserver(int pos, int i)
	{
		try
		{
			SoapObject soapObject = new SoapObject(namespace, method);
			soapObject.addProperty("imei",imei);
			soapObject.addProperty("grid",pos+"");
			soapObject.addProperty("fno",i+"");
			soapObject.addProperty("ftp","2");

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(soapObject);

			HttpTransportSE se = new HttpTransportSE(URL);
			se.call(soapaction, envelope);

			String res = envelope.getResponse().toString();
			Toast.makeText(getApplicationContext(), res+"==="+pos, Toast.LENGTH_SHORT).show();
			return res;
		}
		catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.getMessage()+"--", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			return "no";
		}
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.login_main, menu);
		return true;
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		if(R.id.fgrd==item.getItemId()){
//			startActivity(new Intent(this, About.class));
			forgotGrid();
		}
		return true;
	}

	private void forgotGrid() {
		try
		{
			SoapObject soapObject = new SoapObject(namespace, method2);
			soapObject.addProperty("imei",imei);
			soapObject.addProperty("indx",QrScan.x+"");

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(soapObject);

			HttpTransportSE se = new HttpTransportSE(URL);
			se.call(soapaction2, envelope);

//			String res = envelope.getResponse().toString();
			Toast.makeText(getApplicationContext(), "Password Sent To Your Mail", Toast.LENGTH_SHORT).show();
		}
		catch (Exception e) {
			Toast.makeText(getApplicationContext(),"Try Again", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//		Toast.makeText(getApplicationContext(), findx.get(arg2)+"--"+QrScan.x, Toast.LENGTH_SHORT).show();

		String res=sendtoserver(findx.get(arg2),QrScan.x);
		if(res.equalsIgnoreCase("ok"))
		//	if(!fgrid.equalsIgnoreCase("555"))
		{
			QrScan.x++;
			Intent i=new Intent(getApplicationContext(), LoginSelectImg.class);
			startActivity(i);
		}
		else{
			Toast.makeText(getApplicationContext(), "Unknown User..!!", Toast.LENGTH_SHORT).show();
			kk=sh.getInt("mainflag", 0);
			kk++;
			Log.d("kk-------------",kk+ "");
			Editor e=sh.edit();
			e.putInt("mainflag",kk);
			e.commit();
			if(kk>2)
			{
				Toast.makeText(getApplicationContext(), "Try Again After 30 seconds", Toast.LENGTH_SHORT).show();
				Editor ee=sh.edit();
				ee.putInt("trial",3);
				ee.commit();
				Intent i=new Intent(getApplicationContext(), Login.class);
				startActivity(i);
			}
			hd.postDelayed(r, 30000);
		}
	}
	Runnable r=new Runnable() {
		@Override
		public void run() {
			//Toast.makeText(getApplicationContext(), "session starts...--", Toast.LENGTH_SHORT).show();
			Editor e=sh.edit();
			e.putInt("mainflag",0);
			e.commit();
		}
	};

}
