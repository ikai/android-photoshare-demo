package com.android.photoshare;

import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Main extends Activity {
	
    Button uploadButton;
    TextView text;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        uploadButton = (Button) findViewById(R.id.upload_photo);
        text = (TextView) findViewById(R.id.text_view);
    }
    
    public void selectPhoto(View view) {
	Date date = new Date();
	text.setText(date.toString());
    }
    
    
}