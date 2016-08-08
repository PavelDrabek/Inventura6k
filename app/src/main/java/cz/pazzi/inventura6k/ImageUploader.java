package cz.pazzi.inventura6k;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by pavel on 08.08.16.
 * http://stackoverflow.com/questions/23648028/upload-image-to-server-with-multiple-parameters/23648537#23648537
 */
public class ImageUploader {

    static String serverUrl = "http://6k.pazzi.cz/api/photoUploader.php";

    String boundary = "*****";

    public static void Upload(final String fileName, final String absolutePath) {
        new Thread(new Runnable() {
            public void run() {

                ImageUploader u = new ImageUploader();
                u.UploadFile(serverUrl, fileName, absolutePath);

            }
        }).start();
    }


    public boolean UploadFile(String serverUrl, String fileName, String absolutePath) {

        HttpURLConnection conn;
        DataOutputStream dos = null;
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;


        try {



            // open a URL connection to the Servlet
            InputStream fileInputStream = ShrinkImage(absolutePath); // new FileInputStream(new File(absolutePath));
            URL url = new URL(serverUrl);

            // Open a HTTP  connection to  the URL
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            //  conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type","multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", fileName);

            conn.setRequestProperty("fileName", fileName);

            dos = new DataOutputStream(conn.getOutputStream());

            //    dos.writeBytes(twoHyphens + boundary + lineEnd);
            //    dos.writeBytes("Content-Disposition: form-data;
            //    number=\"userNumber\";
            //    usernumber=\""+ usernumber + "\"" + lineEnd);
            //    Log.d("number",usernumber);
            //   dos.writeBytes(lineEnd);

            // create a buffer of  maximum size
            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {

                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            }

            // Responses from the server (code and message)
            int serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();

            Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);

            if(serverResponseCode == 200){
                String jsonReply = GetResponse(conn.getInputStream());
                Log.d("reply", jsonReply);
            }

            //close the streams //
            fileInputStream.close();
            dos.flush();
            dos.close();

            return true;

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Upload file to server", "Exception : "  + e.getMessage(), e);
        }
        return false;
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

    private static InputStream ShrinkImage(String absolutePath) {
        Bitmap b = BitmapFactory.decodeFile(absolutePath);
        Bitmap out = Bitmap.createScaledBitmap(b, 320, 480, false);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        out.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] bitmapdata = outputStream.toByteArray();
        InputStream is = new ByteArrayInputStream(bitmapdata);
        return is;
    }

    public static long copyLarge(InputStream input, OutputStream output) throws IOException
    {
        byte[] buffer = new byte[4096];
        long count = 0L;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}
