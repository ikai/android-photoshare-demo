package com.android.photoshare;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Main extends Activity {
    private static final int SELECT_IMAGE = 0;

    Button uploadButton;
    TextView text;
    ImageView image;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);
	uploadButton = (Button) findViewById(R.id.upload_photo);
	text = (TextView) findViewById(R.id.text_view);
	image = (ImageView) findViewById(R.id.image_view);
    }

    public void selectPhoto(View view) {
	Date date = new Date();
	text.setText(date.toString());
	// Guess this can also be EXTERNAL_CONTENT_URI
	startActivityForResult(new Intent(Intent.ACTION_PICK,
		android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
		SELECT_IMAGE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	super.onActivityResult(requestCode, resultCode, data);
	if (requestCode == SELECT_IMAGE)
	    if (resultCode == Activity.RESULT_OK) {
		Uri selectedImage = data.getData();
		text.setText(selectedImage.toString());
		image.setImageURI(selectedImage);
	    }
    }

}