package cz.pazzi.inventura6k.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonElement;

import java.io.File;

import cz.pazzi.inventura6k.ImageUploader;
import cz.pazzi.inventura6k.R;
import cz.pazzi.inventura6k.ServerGateway;
import cz.pazzi.inventura6k.comunication.ServerListener;
import cz.pazzi.inventura6k.data.Settings;

public class InventoryActivity extends AppCompatActivity {

    public static final int REQUEST_TAKE_PHOTO = 1;

    EditText tName, tRegNumber, tPrice, tDate, tPlace, tDesc;
    ImageView imgPhoto;
    Button btnSend;

    File file = null;
    String fileName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        tName = (EditText)findViewById(R.id.tName);
        tRegNumber = (EditText)findViewById(R.id.tRegNumber);
        tPrice = (EditText)findViewById(R.id.tPrice);
        tDate = (EditText)findViewById(R.id.tDate);
        tPlace = (EditText)findViewById(R.id.tPlace);
        tDesc = (EditText)findViewById(R.id.tDesc);
        imgPhoto = (ImageView)findViewById(R.id.imgPhoto);

        btnSend = (Button)findViewById(R.id.btnSend);
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Send();
            }
        });

        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetFile();
            }
        });

        ServerGateway cmd = new ServerGateway(Settings.urlNextRegNumber, new ServerListener() {
            @Override
            public void OnServerResult(String result) {
                tRegNumber.setText(result);
            }

            @Override
            public void OnServerOK(JsonElement json) {

            }

            @Override
            public void OnServerError(String error) {
                Toast.makeText(InventoryActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
        cmd.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void GetFile() {
        if(file == null) {
            file = new File(Environment.getExternalStorageDirectory(), "photo.png");
            fileName = String.valueOf(file);
        }

//        Intent intent = new Intent();
//        intent.putExtra( MediaStore.EXTRA_OUTPUT, Uri.fromFile( file ) );
//        startActivityForResult( intent, REQUEST_TAKE_PHOTO );

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "result ok", Toast.LENGTH_SHORT).show();

                Bitmap b = BitmapFactory.decodeFile( file.getAbsolutePath() );
                imgPhoto.setImageBitmap( Bitmap.createScaledBitmap(b, b.getWidth()/10, b.getHeight()/10, true));


            } else {
                Toast.makeText(this, "result not ok", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "request is not photo", Toast.LENGTH_SHORT).show();
        }

//        Bitmap bmp = BitmapFactory.decodeFile( file.getAbsolutePath() );
//        imgPhoto.setImageBitmap(bmp);
    }

    private void Send() {
        if(file != null) {
            ImageUploader.UploadFileAsync("test.png", file.getAbsolutePath());
        } else {
            Toast.makeText(this, "file is null", Toast.LENGTH_SHORT).show();
        }
    }
}
