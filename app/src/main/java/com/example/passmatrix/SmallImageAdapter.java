package com.example.passmatrix;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

//The adapter class for SmallImageActivity class
public class SmallImageAdapter extends BaseAdapter {

       private Context mContext;
       private ArrayList<Bitmap> smallimages;
       private int imageWidth, imageHeight;

       public SmallImageAdapter(Context c, ArrayList<Bitmap> images){
              mContext = c;
              smallimages = images;
//              imageWidth = images.get(0).getWidth();
//              imageHeight =images.get(0).getHeight();
              imageWidth = 90;//images.get(0).getWidth();
              imageHeight = 90;//images.get(0).getHeight();
       }
       // return how many view is crated
       public int getCount() {
              return smallimages.size();
       }
       public Object getItem(int position) {
              return smallimages.get(position);
       }
       public long getItemId(int position) {
              return position;
       }

       public View getView(int position, View convertview, ViewGroup parent) {
//              ImageView image;
//              if(convertView == null){
//                     image = new ImageView(mContext);
//                     image.setLayoutParams(new GridView.LayoutParams(imageWidth - 12 , imageHeight));
//                     image.setPadding(0, 0, 0, 0);
//              }else{//
//                     image = (ImageView) convertView;
//              }
//              image.setImageBitmap(smallimages.get(position));

    	   LayoutInflater inflator=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
   		
   			View gridView;
   			if(convertview==null)
   			{
   				gridView=new View(mContext);
   				gridView=inflator.inflate(R.layout.smimg, null);
   			}
   			else
   			{
   				gridView=(View)convertview;
   			}
   			
   			
   			ImageView im=(ImageView)gridView.findViewById(R.id.imageView1);
   			im.setImageBitmap(smallimages.get(position));
   			
   		    return gridView;

       }
       
       
}
