package com.yunitski.msg.data;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadingFromUrl extends AsyncTask<String, Void, String> {

    private Context context;

    public DownloadingFromUrl(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        int count;
        try {
            URL url = new URL(strings[0]);
            URLConnection conexion = url.openConnection();
            conexion.connect();
            // this will be useful so that you can show a tipical 0-100% progress bar
            int lenghtOfFile = conexion.getContentLength();

            // downlod the file
            InputStream input = new BufferedInputStream(url.openStream());
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/msgMusic");
            OutputStream output = new FileOutputStream(file);

            byte data[] = strings[0].getBytes();

            long total = 0;
            Log.d("download msg", ""+file.getAbsolutePath());

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                publishProgress((int)(total*100/lenghtOfFile));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {}
        return null;
    }

    private void publishProgress(int i) {
    }
}
