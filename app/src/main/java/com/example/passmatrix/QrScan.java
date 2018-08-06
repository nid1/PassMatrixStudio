package com.example.passmatrix;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

public class QrScan extends Activity {
	static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
	String imei="";
	public static int x=0;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qr);
		
		TelephonyManager tm=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
        imei=tm.getDeviceId().toString();
	}
	
	public void scanBar(View v) {
		try {
			Intent intent = new Intent(ACTION_SCAN);
			intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
			startActivityForResult(intent, 0);
		} catch (ActivityNotFoundException anfe) {
			showDialog(QrScan.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
		}
	}
	
	public void scanQR(View v) {
		try {
			Intent intent = new Intent(ACTION_SCAN);
			intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
			startActivityForResult(intent, 0);
		} catch (ActivityNotFoundException anfe) {
			showDialog(QrScan.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
		}
	}
	
	private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
		AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
		downloadDialog.setTitle(title);
		downloadDialog.setMessage(message);
		downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int i) {
				Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				try {
					act.startActivity(intent);
				} catch (ActivityNotFoundException anfe) {}
			}
		});
		downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int i) {
			}
		});
		return downloadDialog.show();
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				
				x=0;				
			Toast.makeText(this, "Content:" + contents + " Format:" + format, Toast.LENGTH_SHORT).show();
				
				byte[] b=Base64.decode(contents, Base64.NO_WRAP);
				String s=new String(b);
				Toast.makeText(this, "imei :" + s, Toast.LENGTH_SHORT).show();
				
				if(s.equals(imei)){
					Intent i=new Intent(getApplicationContext(), LoginSelectImg.class);
					startActivity(i);
				}
				else {
					Toast.makeText(this, "Unknown User", Toast.LENGTH_SHORT).show();
					Intent i=new Intent(getApplicationContext(), Login.class);
					startActivity(i);
				}
			}
		}
	}
}