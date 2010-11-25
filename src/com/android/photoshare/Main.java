package com.android.photoshare;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Main extends Activity {
    private static final int SELECT_IMAGE = 0;

    Button uploadButton;
    TextView text;
    ImageView image;

    String uploadImageUrl;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);
	uploadButton = (Button) findViewById(R.id.upload_photo);
	text = (TextView) findViewById(R.id.text_view);
	image = (ImageView) findViewById(R.id.image_view);

	String hostname = getResources().getString(R.string.upload_hostname);
	String getBlobstoreUrl = hostname
		+ getResources().getString(R.string.get_upload_url);

	text.setText(getBlobstoreUrl);
	uploadImageUrl = getUploadUrl(getBlobstoreUrl);
	text.setText(uploadImageUrl);
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
		String filename = getFilenameFromURI(selectedImage);
		
		uploadImage(selectedImage, filename);
	    }
    }

    // Need mime4j
    // http://james.apache.org/download.cgi#Apache_Mime4J
    // http://blog.tacticalnuclearstrike.com/2010/01/using-multipartentity-in-android-applications/
    protected void uploadImage(Uri imageToUploadUri, String filename) {

	text.setText(imageToUploadUri.getPath());
	Log.d("Main", "Upload image: " + imageToUploadUri.toString());

	ContentResolver resolver = getContentResolver();
	String mimeType = resolver.getType(imageToUploadUri);

	try {
	    InputStream is = resolver.openInputStream(imageToUploadUri);
	    DefaultHttpClient httpclient = new DefaultHttpClient();
	    HttpPost httpPost = new HttpPost(uploadImageUrl);
	    
	    MultipartEntity mpEntity = new MultipartEntity();
	    mpEntity.addPart("file", new InputStreamBody(is, mimeType, filename));
	    
	    httpPost.setEntity(mpEntity);
	    HttpResponse response = httpclient.execute(httpPost);

	} catch (FileNotFoundException e1) {
	    e1.printStackTrace();
	} catch (ClientProtocolException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

    protected String getUploadUrl(String getBlobstoreUrl) {
	String uploadUrl = "";
	URL url;
	URLConnection connection;

	try {
	    url = new URL(getBlobstoreUrl);

	    connection = url.openConnection();
	    connection.connect();
	    InputStream is = connection.getInputStream();
	    BufferedReader reader = new BufferedReader(
		    new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();
	    String line = null;
	    while ((line = reader.readLine()) != null) {
		sb.append(line); // We don't care to append a newline since this
				 // will bork HTTPclient
	    }
	    is.close();
	    String hostname = getResources()
		    .getString(R.string.upload_hostname);

	    uploadUrl = hostname + sb.toString();

	} catch (MalformedURLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block

	    e.printStackTrace();
	}
	return uploadUrl;
    }
    
    // Taken from here:
    // 	http://stackoverflow.com/questions/3401579/android-get-filename-and-path-from-uri-from-mediastore
    public String getFilenameFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DISPLAY_NAME };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }    

}
