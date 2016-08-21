package cz.pazzi.inventura6k;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Debug;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by pavel on 08.08.16.
 * http://stackoverflow.com/questions/23648028/upload-image-to-server-with-multiple-parameters/23648537#23648537
 */
public class ImageUploader {

    static String serverUrl = "http://6k.pazzi.cz/api/photoUploader.php";

    String boundary = "*****";

    public static void UploadFileAsync(final String fileName, final String absolutePath) {
        new Thread(new Runnable() {
            public void run() {

//                ImageUploader u = new ImageUploader();
//                UploadFile(serverUrl, fileName, absolutePath);
                try {

                    MultipartUtility multipart = new MultipartUtility(serverUrl, "UTF-8");

                    //add your file here.
                    /*This is to add file content*/
    //                for (int i = 0; i < myFileArray.size(); i++) {
                    multipart.addFilePart("photo.png", new File(absolutePath));

    //                }

                    List<String> response = multipart.finish();
                    String responseString = "";
                    Log.d("fileUpload", "SERVER REPLIED:");
                    for (String line : response) {
                        Log.d("fileUpload", "Upload Files Response:::" + line);
                        // get your server response here.
                        responseString = line;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void UploadFile(String serverUrl, String fileName, String filePath) {
        HttpURLConnection connection = null;
        String error = "";
        try {
            URL url = new URL(serverUrl);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            String params = "image_name=" + "photo.png" + "&image_encoded=" + ShrinkAndEncodeImage(filePath);

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(params);
            writer.close();

            int responseCode = connection.getResponseCode();
            Log.d("upload", GetResponse(connection.getInputStream()));
            Log.d("upload", "ResponseCode = " + responseCode);

            if(responseCode == 200) { //If the code contained in this response equals 200, then the upload is successful (and ready to be processed by the php code)
                Log.d("upload", "Upload successful !");
            }
        } catch (Exception e) {
            error = e.toString();
            Log.e("upload", "error in catch: " + error);
        }

        if(connection != null) {
            connection.disconnect();
            Log.d("upload", "Connection disconected");
        }
    }

    protected final static String GetResponse(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuffer response = new StringBuffer();
        String line = "";
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        return response.toString();
    }

    private static Bitmap ShrinkImage(String absolutePath) {
        Bitmap b = BitmapFactory.decodeFile(absolutePath);
        return Bitmap.createScaledBitmap(b, b.getWidth()/10, b.getHeight()/10, true);
    }

    private static String ShrinkAndEncodeImage(String absolutePath) {
        Bitmap b = ShrinkImage(absolutePath);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 80, outputStream);

        byte[] bitmapdata = outputStream.toByteArray();
        Log.d("odkodovano", bitmapdata.toString());
        b.recycle();
        return Base64.encodeToString(bitmapdata, 0);
    }

}
