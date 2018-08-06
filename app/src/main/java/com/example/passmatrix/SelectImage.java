package com.example.passmatrix;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SelectImage extends Activity implements OnClickListener,OnItemLongClickListener
{
	TextView textTargetUri;
    ImageView targetImage,wtimg;
    ArrayList<Bitmap> smallimages,tmpimages;
    SmallImageAdapter smad=null;
    GridView gd;    
    Bitmap icon;
    Button buttonLoadImage,nxt,clr ;
    TelephonyManager tm;
    SharedPreferences sh;
    
    private Matrix matrix = new Matrix();
    private float scale = 1f;
    //private ScaleGestureDetector SGD;
    DBHelper db;
    
    static int pos=0;
	static String namespace="http://shoulder/";
    static String method = "files";
	static String soapaction = namespace + "files";
	String imei="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_image);
		
		pos=0;
		Login.xx++;
		
		if(Login.xx%2==0){
			Toast.makeText(getApplicationContext(), "Select Watermark Image", Toast.LENGTH_SHORT).show();			
		}
		else{
			Toast.makeText(getApplicationContext(), "Select Password Image", Toast.LENGTH_SHORT).show();			
		}
				
		
		gd=(GridView)findViewById(R.id.gridView1);
		gd.setOnItemLongClickListener(this);
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
		
		sh=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		db=new DBHelper(getApplicationContext(), "passmatrix", null, 2);
//		Cursor c=db.getData();
//		Toast.makeText(getApplicationContext(), c.getCount()+"===========--", Toast.LENGTH_SHORT).show();
				
		
		icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.icon);
		//bitmap= ((BitmapDrawable)targetImage.getDrawable()).getBitmap();
	    
		buttonLoadImage = (Button)findViewById(R.id.loadimage);
		nxt = (Button)findViewById(R.id.button1);
		clr = (Button)findViewById(R.id.button2);
        textTargetUri = (TextView)findViewById(R.id.targeturi);
        targetImage = (ImageView)findViewById(R.id.targetimage);
        wtimg = (ImageView)findViewById(R.id.imageView1);

        nxt.setOnClickListener(this);
        clr.setOnClickListener(this);
        
        tm=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
        imei=tm.getDeviceId().toString();
        
        buttonLoadImage.setOnClickListener(new Button.OnClickListener(){
        	@Override
        	public void onClick(View arg0) 
        	{
        		Intent intent = new Intent(Intent.ACTION_PICK,
        		android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        		startActivityForResult(intent, 0);
        		//startActivity(new Intent(getApplicationContext(), LoginIndicator.class));
        	}
        });
	}
		
	@Override
	public void onBackPressed() {
		
	}

	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	 super.onActivityResult(requestCode, resultCode, data);
	  	if (resultCode == RESULT_OK&&requestCode==0)
	    {
	    	Uri targetUri = data.getData();
	    	Log.d("targetUri============", targetUri+"");
    		try 
	    	{
	    		String path=FileUtils.getPath(getApplicationContext(), targetUri);
	    		Log.d("path============", path+"");
	    		
	    		textTargetUri.setText(path);
	     
	    		Bitmap bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
	    		targetImage.setImageBitmap(bitmap);
	    		if(Login.xx%2==0){
	    			gd.setVisibility(AdapterView.INVISIBLE);
	    			wtimg.setVisibility(View.VISIBLE);
	    			wtimg.setImageBitmap(bitmap);
	    		}
	    		else{
	    			gd.setVisibility(AdapterView.VISIBLE);
	    			wtimg.setVisibility(View.INVISIBLE);
	    			splitImage(targetImage,getApplicationContext());		
	    		}	    		
	     	}
	    	catch (Exception e) 
	    	{
	    		Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
	    		e.printStackTrace();
	    		Log.d("errrrr============", e.getMessage()+"");
	    		
	    	}
	    }
	  	else if(resultCode==RESULT_OK & requestCode==1){
	  		
	  	}
	 }
	 
	 private void splitImage(ImageView image, Context context) 
	 {  
		 int rows=6,cols=5;
		 int smallimage_Numbers=rows*cols;
		 //For height and width of the small image smallimage_s
         int smallimage_Height,smallimage_Width;

         //To store all the small image smallimage_s in bitmap format in this list
         smallimages = new ArrayList<Bitmap>(smallimage_Numbers);
         tmpimages = new ArrayList<Bitmap>(smallimage_Numbers);

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
                    	 tmpimages.add(Bitmap.createBitmap(scaledBitmap, xCo, yCo, smallimage_Width, smallimage_Height));
                         xCo += smallimage_Width;
                     }
                     yCo+= smallimage_Height;
         }
         smad=new SmallImageAdapter(context, smallimages);
		  gd.setAdapter(smad);
		  //gd.setNumColumns((int) Math.sqrt(smallimages.size()));
		  gd.setNumColumns(cols); 
	}

	
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
	{
		Toast.makeText(getApplicationContext(), "selected", Toast.LENGTH_SHORT).show();
		//smallimages.clear();
		//smallimages=tmpimages;
		pos=arg2;
		Collections.copy(smallimages,tmpimages);
		smallimages.set(arg2, icon);
		smad.notifyDataSetChanged();
//		gd.setItemChecked(arg2, true);
		
		return false;
	}

	@Override
	public void onClick(View v) {
		if(v==nxt){
		String pth=textTargetUri.getText().toString();
		if(!pth.equalsIgnoreCase(""))
		{
			int lmt=sh.getInt("nofile", 0);
		//	db.insertdata("imei",pth,SplashStart.xx,2);
			String res=sendtoserver(pth,Login.xx,2);
			Log.d("=================", res+"--000000");
			if(Login.xx<(lmt*2))
			{
				if(res.equalsIgnoreCase("no")){
					Login.xx--;
				}				
				Intent i=new Intent(getApplicationContext(), SelectImage.class);
				startActivity(i);
			}
			else{
				Intent i=new Intent(getApplicationContext(), Login.class);
				startActivity(i);
			}
		}
		}
		else{
		
		}
	}

	private String sendtoserver(String pth, int xx, int i) 
	{	
		try 
		{
			InputStream inputStream = null;//You can get an inputStream using any IO API
			inputStream = new FileInputStream(pth);
			byte[] buffer = new byte[8192];
			int bytesRead;
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);
			try {
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					output64.write(buffer, 0, bytesRead);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			output64.close();
			
			String attachedFile = output.toString();
		
			SoapObject soapObject = new SoapObject(namespace, method);
			soapObject.addProperty("imei",imei);
			soapObject.addProperty("file",attachedFile);
			soapObject.addProperty("fileindex",xx+"");
			soapObject.addProperty("grid",pos+"");
			soapObject.addProperty("ftp",i+"");
			Log.d("--------------", "-------------");
			
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(soapObject);
			
			HttpTransportSE se = new HttpTransportSE(sh.getString("url",""));
			Log.d("--------------", "-------------");
			se.call(soapaction, envelope);
			
			Log.d("--------------", "-------------");
			String res = envelope.getResponse().toString();
			Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
			Log.d("--------------", "==-------------");
			
			return res;						
		} 
		catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.getMessage()+"--", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			return "no";
		}
	}
}
