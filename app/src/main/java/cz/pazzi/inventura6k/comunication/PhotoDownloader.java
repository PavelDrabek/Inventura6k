package cz.pazzi.inventura6k.comunication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pavel on 03.05.2016.
 */
public class PhotoDownloader extends AsyncTask<Void,Void,Void> {

    private static Map<String, Bitmap> map = new HashMap<>();

    String sUrl;
    //    PhotoListener listener;
    Bitmap result = null;
    ImageView view = null;
    Context context;

    public PhotoDownloader(Context context, String url, ImageView view) {
        this.context = context;
        this.sUrl = url;
        this.view = view;
    }

    @Override
    protected Void doInBackground(Void... params) {

        Bitmap loaded = LoadBitmap(sUrl);
        if(loaded != null) {
            result = loaded;
            return null;
        }

        try {
            URL url = new URL(sUrl);
            result = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            SaveBitmap(sUrl, result);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if(view != null) {
            view.setImageBitmap(result);
        }
    }

    private void SaveBitmap(String url, Bitmap bitmap) {
        map.put(url, bitmap);
        SaveBitmapToFile(url, bitmap);
    }

    private Bitmap LoadBitmap(String url) {
        Bitmap loaded = null;
        if(map.containsKey(url)) {
            loaded = map.get(url);
            return loaded;
        }

        loaded = LoadBitmapFromFile(url);

        return loaded;
    }

    private Bitmap LoadBitmapFromFile(String url) {
        String filename = url + ".png";
        File dir = context.getFilesDir();
        if(dir.exists()) {
            File imageFile = new File(dir, filename);
            if (imageFile.exists()) {
                return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            }
        }

        return null;
    }

    public boolean SaveBitmapToFile(String url, Bitmap bm) {
        String dirPath = url.substring(0, url.lastIndexOf("/"));
        String name = url.substring(url.lastIndexOf("/") + 1);
        String filename = name + ".png";

        File dir = new File(context.getFilesDir(), dirPath);
        boolean doSave = true;
        if (!dir.exists()) {
            doSave = dir.mkdirs();
        }

        if (doSave) {
            File imageFile = new File(dir, filename);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(imageFile);
                bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();

                return true;
            }
            catch (IOException e) {
                Log.e(getClass().getSimpleName(), e.getMessage());
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        else {
            Log.e("app","Couldn't create target directory.");
        }

        return false;
    }
}
