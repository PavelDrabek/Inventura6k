package cz.pazzi.inventura6k;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

public class InventoryActivity extends AppCompatActivity {

    public static final int REQUEST_TAKE_PHOTO = 1;

    EditText tName, tRegNumber;
    ImageView imgPhoto;

    File file = null;
    String fileName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        tName = (EditText)findViewById(R.id.tName);
        tRegNumber = (EditText)findViewById(R.id.tRegNumber);
        imgPhoto = (ImageView)findViewById(R.id.imgPhoto);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabSend);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Send();
            }
        });

        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetFile();
            }
        });
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
//                Bitmap bmp = BitmapFactory.decodeFile( file.getAbsolutePath() );
//                imgPhoto.setImageBitmap(bmp);


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
            ImageUploader.Upload("test.png", file.getAbsolutePath());
        } else {
            Toast.makeText(this, "file is null", Toast.LENGTH_SHORT).show();
        }
    }
}
