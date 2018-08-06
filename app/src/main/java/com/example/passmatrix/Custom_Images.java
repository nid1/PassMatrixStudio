package com.example.passmatrix;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Custom_Images extends BaseAdapter {
	private Context Context;
	String[] b;
	public Custom_Images(Context applicationContext, String[] b) {
		// TODO Auto-generated constructor stub
		this.Context=applicationContext;
		this.b=b;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return b.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflator=(LayoutInflater)Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View gridView;
		if(convertView==null)
		{
			gridView=new View(Context);
			gridView=inflator.inflate(R.layout.activity_custom_img, null);
		}
		else
		{
			gridView=(View)convertView;			
		}
		
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
		
		ImageView im=(ImageView)gridView.findViewById(R.id.imageView1);
		
		SharedPreferences sh=PreferenceManager.getDefaultSharedPreferences(Context);
		
		
		String pt="http://"+sh.getString("ip", "")+":8084/PassMatrixs/upfiles/";
		
		Log.d("===================",pt+b[position]);
		
        Picasso.with(Context)
                .load(pt+b[position])
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .placeholder(R.drawable.icon)
                .error(R.drawable.icon).into(im);
		
		return gridView;
		
	}
	
	
	
}