package cz.pazzi.inventura6k.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonElement;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.StringTokenizer;

import cz.pazzi.inventura6k.ImageUploader;
import cz.pazzi.inventura6k.R;
import cz.pazzi.inventura6k.ServerGateway;
import cz.pazzi.inventura6k.comunication.ServerListener;
import cz.pazzi.inventura6k.data.DateParser;
import cz.pazzi.inventura6k.data.Item;
import cz.pazzi.inventura6k.data.Settings;

public class InventoryActivity extends AppCompatActivity {

    public static final int REQUEST_TAKE_PHOTO = 1;

    EditText tName, tRegNumber, tPrice, tDate, tPlace, tDesc;
    ImageView imgPhoto;
    Button btnSend;

    String fileAbsolutePath = null;
    boolean hasPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        setTitle("Nový předmět");

        hasPhoto = false;
        fileAbsolutePath = Environment.getExternalStorageDirectory() + "/photo.png";


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
                try {
                    Send();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
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
//        if(fileAbsolutePath == null) {
//            fileAbsolutePath = Environment.getExternalStorageDirectory() + "/photo.png";
//        }

//        Intent intent = new Intent();
//        intent.putExtra( MediaStore.EXTRA_OUTPUT, Uri.fromFile( file ) );
//        startActivityForResult( intent, REQUEST_TAKE_PHOTO );

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(fileAbsolutePath)));
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "result ok", Toast.LENGTH_SHORT).show();

                try {
                    ShrinkImage(fileAbsolutePath, 1024, 1024);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Bitmap b = BitmapFactory.decodeFile(fileAbsolutePath);
                imgPhoto.setImageBitmap(b); //Bitmap.createScaledBitmap(b, b.getWidth()/10, b.getHeight()/10, true));

                hasPhoto = true;

            } else {
                Toast.makeText(this, "result not ok", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "request is not photo", Toast.LENGTH_SHORT).show();
        }

//        Bitmap bmp = BitmapFactory.decodeFile( file.getAbsolutePath() );
//        imgPhoto.setImageBitmap(bmp);
    }

    private void Send() throws UnsupportedEncodingException {
        final Item i = ToItem();

        if(CanSendItem(i)) {
            String url = String.format(Settings.urlAddItem, e(i.name), e(i.regNumber), e(i.price), e(i.place), i.buyDate == null ? "" : e(i.buyDate.getTime().toString()), e(i.description));
            Log.d(getClass().getSimpleName(), url);
            new ServerGateway(url, new ServerListener() {
                @Override
                public void OnServerResult(String result) {
                    Toast.makeText(InventoryActivity.this, "predmet ulozen", Toast.LENGTH_SHORT).show();
                    if(hasPhoto) {
                        ImageUploader.UploadFileAsync("test.png", fileAbsolutePath, i.regNumber);
                    } else {
                        Toast.makeText(InventoryActivity.this, "poslano bez fotky", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void OnServerOK(JsonElement json) { }

                @Override
                public void OnServerError(String error) {
                    Toast.makeText(InventoryActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }).execute();

        } else {
            Toast.makeText(this, "vypln nazev a registracni cislo", Toast.LENGTH_SHORT).show();
        }
    }

    private String e(String parameter) throws UnsupportedEncodingException {
        return URLEncoder.encode(parameter, "UTF-8");
    }

    private Item ToItem() {
        Item item = new Item();
        item.name = tName.getText().toString();
        item.regNumber = tRegNumber.getText().toString();
        item.price = tPrice.getText().toString();
        item.place = tPlace.getText().toString();
        item.description = tDesc.getText().toString();
        return item;
    }

    private boolean CanSendItem(Item i) {
        return !(i.name.isEmpty() || i.regNumber.isEmpty());
    }

    private static void ShrinkImage(String absolutePath, int maxWidth, int maxHeight) throws IOException {
        Bitmap b = BitmapFactory.decodeFile(absolutePath);
        float scale = (float)Math.sqrt((float)(maxWidth * maxHeight) / (float)(b.getWidth() * b.getHeight()));

        Bitmap scaled = Bitmap.createScaledBitmap(b, (int)(b.getWidth() * scale), (int)(b.getHeight() * scale), true);

        File file = new File(absolutePath);
        OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
        scaled.compress(Bitmap.CompressFormat.JPEG, 100, os);
        os.close();
    }
}
