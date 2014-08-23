package com.example.engagocr;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

public class Info extends ActionBarActivity {

    private TextView textView ;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

		setContentView(R.layout.info);
	    ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    textView = (TextView)(findViewById(R.id.textView1));
	    textView.setTextColor(Color.WHITE);
	    textView.setTextSize(12);
	    textView.setText(readTxt());
    }
    
    private String readTxt(){

     InputStream inputStream = getResources().openRawResource(R.raw.help);
     
     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
     
     int i;
  try {
   i = inputStream.read();
   while (i != -1)
      {
       byteArrayOutputStream.write(i);
       i = inputStream.read();
      }
      inputStream.close();
  } catch (IOException e) {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
  
     return byteArrayOutputStream.toString();
    }
	    // TODO Auto-generated method stub
	}
