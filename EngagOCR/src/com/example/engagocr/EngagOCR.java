package com.example.engagocr;

import java.io.File;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.app.ActionBar;

import com.googlecode.tesseract.android.TessBaseAPI;

public class EngagOCR extends ActionBarActivity {
	public static final String PACKAGE_NAME = "com.example.engagocr";
	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/engagOCR/";
	
	// You should have the trained data file in assets folder
	// You can get them at:
	// http://code.google.com/p/tesseract-ocr/downloads/list
	public static final String lang = "eng";

	private static final String TAG = "engagOCR.java";
	protected Button _button,_button1,_button2,_button3;
	protected ImageView _image;
	protected EditText _field;
	protected String _path,_path1;
	protected boolean _taken;
	protected EditText editText1;
	protected Dialog dialog;
	protected String _name,extension;
	protected Spinner _extension;
	protected ProgressDialog progress;
    private static final int SELECT_PICTURE = 1;

	protected static final String PHOTO_TAKEN = "photo_taken";
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.info:
	        {
	        	Intent info = new Intent(this, Info.class);
	        	startActivity(info);
	        }
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

		for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
					return;
				} else {
					Log.v(TAG, "Created directory " + path + " on sdcard");
				}
			}

		}
		
		// lang.traineddata file with the app (in assets folder)
		// You can get them at:
		// http://code.google.com/p/tesseract-ocr/downloads/list
		// This area needs work and optimization
		if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
			try {

				AssetManager assetManager = getAssets();
				InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
				//GZIPInputStream gin = new GZIPInputStream(in);
				OutputStream out = new FileOutputStream(DATA_PATH
						+ "tessdata/" + lang + ".traineddata");

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				//while ((lenf = gin.read(buff)) > 0) {
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				//gin.close();
				out.close();
				
				Log.v(TAG, "Copied " + lang + " traineddata");
			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
			}
		}

		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		_field = (EditText) findViewById(R.id.field);
		_button = (Button) findViewById(R.id.button);
		_button.setOnClickListener(new ButtonClickHandler());
		_button1 = (Button) findViewById(R.id.button2);
		_button1.setOnClickListener(new Button1ClickHandler());

		_button2 = (Button) findViewById(R.id.button1);
		_button2.setOnClickListener(new Button2ClickHandler());

		_path = DATA_PATH + "/ocr.jpg";
	}

	public class ButtonClickHandler implements View.OnClickListener {
		public void onClick(View view) {
			Log.v(TAG, "Starting Camera app");
			startCameraActivity();
		}
	}
	public class Button2ClickHandler implements View.OnClickListener {
		public void onClick(View view) {
			Log.v(TAG, "Saving text File");
			startSaveActivity();
		}
	}
	public class Button1ClickHandler implements View.OnClickListener {
		public void onClick(View view) {
			Log.v(TAG, "Starting Gallery");
			startFileActivity();
		}
	}
	protected void startFileActivity(){
			Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,
                    "Select Picture"), SELECT_PICTURE);
	}
	// Simple android photo capture:
	// http://labs.makemachine.net/2010/03/simple-android-photo-capture/

	protected void startCameraActivity() {
		File file = new File(_path);
		Uri outputFileUri = Uri.fromFile(file);

		final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

		startActivityForResult(intent, 0);
	}
	public class Button5ClickHandler implements View.OnClickListener {
		public void onClick(View view) {
			 _name =editText1.getText().toString()+extension;
			 writeToFile(_name);
			 dialog.dismiss();
		}
	}
	public class Button6ClickHandler implements View.OnClickListener {
		public void onClick(View view) {
			 dialog.dismiss();
		}
	}
	
	public class sHandler implements OnItemSelectedListener{
	 public void onItemSelected(AdapterView<?> parent, View view, 
	            int pos, long id) {
		 if (((String)_extension.getItemAtPosition(pos)).equalsIgnoreCase("Text File"))
	        extension=".txt";
		 else if (((String)_extension.getItemAtPosition(pos)).equalsIgnoreCase("Word Document"))
		        extension=".docx";
		 else if (((String)_extension.getItemAtPosition(pos)).equalsIgnoreCase("Rich Text Format"))
		        extension=".rtf";
		 else 
			 extension=".txt";
	    }

	    public void onNothingSelected(AdapterView<?> parent) {
	        // Another interface callback
	    	extension=".txt";
	    }
	}
	protected void startSaveActivity(){
		 //set up dialog
		
        dialog = new Dialog(EngagOCR.this);

		
        dialog.setContentView(R.layout.filenamedialog);
        dialog.setTitle("Enter File Name:");
        dialog.setCancelable(true);
        editText1 = (EditText) dialog.findViewById(R.id.editText1);
		_extension = (Spinner) dialog.findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.spinnerelement, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		_extension.setAdapter(adapter);
		_extension.setOnItemSelectedListener(new sHandler());
        //there are a lot of settings, for dialog, check them all out!

        //set up button
        Button button = (Button) dialog.findViewById(R.id.button1);
        button.setOnClickListener(new Button5ClickHandler());
        Button button3 = (Button) dialog.findViewById(R.id.button2);
        button3.setOnClickListener(new Button6ClickHandler());
        dialog.show();
	}
	protected void writeToFile(String myname){
		
		try {
			String root = Environment.getExternalStorageDirectory().toString();
		    File myDir = new File(root + "/OCR");    
		    myDir.mkdirs();
		    String fname = myname;
		    File myFile = new File (myDir, fname);
		    if (myFile.exists ()) myFile.delete (); 
			myFile.createNewFile();
			FileOutputStream fOut = new FileOutputStream(myFile);
			OutputStreamWriter myOutWriter = 
									new OutputStreamWriter(fOut);
			myOutWriter.append(_field.getText());
			myOutWriter.close();
			fOut.close();
			Toast.makeText(getBaseContext(),
					"Done writing SD "+_name,
					Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.i(TAG, "resultCode: " + resultCode);
		if (requestCode == SELECT_PICTURE) {
			if (resultCode==RESULT_CANCELED){
				Log.v(TAG, "User cancelled");
			}
			else
			{
    		_field.setText("");
            Uri selectedImageUri = data.getData();
            _path1 = getPath(selectedImageUri);
 
			//reset progress bar status
            ocr(_path1);
			}
        }
		else if(requestCode == 0){ 
			if (resultCode == -1) {
			_field.setText("");
			onPhotoTaken();
		} 
		else {
			
			Log.v(TAG, "User cancelled");
		}
		}
		
	}
	
	public String getPath(Uri uri) {
        // just some safety built in 
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(EngagOCR.PHOTO_TAKEN, _taken);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.i(TAG, "onRestoreInstanceState()");
		if (savedInstanceState.getBoolean(EngagOCR.PHOTO_TAKEN)) {
			onPhotoTaken();
		}
	}

	protected void onPhotoTaken() {
		_taken = true;
		ocr(_path);
		
	}
	protected void ocr(String mypath){
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;

		Bitmap bitmap = BitmapFactory.decodeFile(mypath, options);

		try {
			ExifInterface exif = new ExifInterface(_path);
			int exifOrientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			Log.v(TAG, "Orient: " + exifOrientation);

			int rotate = 0;

			switch (exifOrientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			}

			Log.v(TAG, "Rotation: " + rotate);

			if (rotate != 0) {

				// Getting width & height of the given image.
				int w = bitmap.getWidth();
				int h = bitmap.getHeight();

				// Setting pre rotate
				Matrix mtx = new Matrix();
				mtx.preRotate(rotate);

				// Rotating Bitmap
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
			}

			// Convert to ARGB_8888, required by tess
			bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

		} catch (IOException e) {
			Log.e(TAG, "Couldn't correct orientation: " + e.toString());
		}

		// _image.setImageBitmap( bitmap );
		
		Log.v(TAG, "Before baseApi");

		TessBaseAPI baseApi = new TessBaseAPI();
		baseApi.setDebug(true);
		baseApi.init(DATA_PATH, lang);
		baseApi.setImage(bitmap);
		
		String recognizedText = baseApi.getUTF8Text();
		
		baseApi.end();

		// You now have the text in recognizedText var, you can do anything with it.
		// We will display a stripped out trimmed alpha-numeric version of it (if lang is eng)
		// so that garbage doesn't make it to the display.

		Log.v(TAG, "OCRED TEXT: " + recognizedText);

		if ( lang.equalsIgnoreCase("eng") ) {
			recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
		}
		
		recognizedText = recognizedText.trim();

		if ( recognizedText.length() != 0 ) {
			_field.setText(_field.getText().toString().length() == 0 ? recognizedText : _field.getText() + " " + recognizedText);
			_field.setSelection(_field.getText().toString().length());
		}
		
		// Cycle done.
	}
}
