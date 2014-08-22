package com.example.engagocr;


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
	    textView.setTextSize(40);
	    textView.setText("Hello");
	    // TODO Auto-generated method stub
	}

}
