package com.example.engagocr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class splash extends Activity {
	/** Called when the activity is first created. */
	   @Override
	   public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	      setContentView(R.layout.splash);
	      Thread splashThread = new Thread() {
	         @Override
	         public void run() {
	            try {
	               int waited = 0;
	               while (waited < 3000) {
	                  sleep(500);
	                  waited += 500;
	               }
	            } catch (InterruptedException e) {
	               // do nothing
	            } finally {
	            	Intent i = new Intent();
		               i.setClassName("com.example.engagocr",
		                              "com.example.engagocr.EngagOCR");
		               startActivity(i);
	               finish();
	               overridePendingTransition(R.anim.fadein,
                           R.anim.fadeout);
	               
	            }
	         }
	      };
	      splashThread.start();
	   }
	}