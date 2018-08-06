package com.example.passmatrix;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class LoginImageAdapter extends BaseAdapter{

	private Context Context;
	private String[] a;	
	private String[] fldr;
	
	public LoginImageAdapter(Context applicationContext,String[] a,String[] fdr) {

		this.Context=applicationContext;
		this.a=a;
		this.fldr=fdr;
	}

	@Override
	public int getCount() {
		return a.length;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}
	
	@Override
	public long getItemId(int arg0) 
	{
		return 0;
	}
	
	@Override
	public View getView(int position, View convertview, ViewGroup parent) {
	LayoutInflater inflator=(LayoutInflater)Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View gridView;
		if(convertview==null)
		{
			gridView=new View(Context);
			gridView=inflator.inflate(R.layout.loginadimg, null);			
		}
		else
		{
			gridView=(View)convertview;			
		}
		
		ImageView im=(ImageView)gridView.findViewById(R.id.imageView1);
		SharedPreferences sh=PreferenceManager.getDefaultSharedPreferences(Context);
				
		String pt="http://"+sh.getString("ip", "")+":8084/PassMatrixs/"+fldr[position]+"/";
		Log.d("pathh..", position+"--"+pt+a[position]);
        Picasso.with(Context)
                .load(pt+a[position])
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .placeholder(R.drawable.icon)
                .error(R.drawable.icon).into(im);
		return gridView;
	}
}
