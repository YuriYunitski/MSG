package com.yunitski.msg.data;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadingFromUrl extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... strings) {
        int count;
        try {
            URL url = new URL(strings[0]);
            URLConnection connection = url.openConnection();
            connection.connect();
            int lenghtOfFile = connection.getContentLength();
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(Environment
                    .getExternalStorageDirectory().toString() + "/mcgMusic");
            byte[] data = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
