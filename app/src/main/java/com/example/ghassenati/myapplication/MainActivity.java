package com.example.ghassenati.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Browser;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.X509Certificate;
import java.util.Random;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {

    String iconName;
    Button btn ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadIcone().execute("Icone.png");
            }
        });


        //createWebAppBookmark("http://google.com", "Google");
    }

    public void createWebAppBookmark(String url, String title){

        Context context = this.getApplicationContext();

        //A ajouter {{


        int size = (int) getResources().getDimension(android.R.dimen.app_icon_size);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile("/storage/emulated/0/ProjetEMMIcones/Icone.png"),size,size,true);
        //}}

        final Intent in = new Intent();
        final Intent shortcutIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        long urlHash = url.hashCode();
        long uniqueId = (urlHash << 32) | shortcutIntent.hashCode();
        shortcutIntent.putExtra(Browser.EXTRA_APPLICATION_ID, Long.toString(uniqueId));
        in.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        in.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
        //A ajouter {{
        in.putExtra(Intent.EXTRA_SHORTCUT_ICON,scaledBitmap);
        in.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        //}}



        context.sendBroadcast(in);
    }

    private class DownloadIcone extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                //SSL Stuff
                TrustManager[] trustAllCerts = new TrustManager[] {
                        new X509TrustManager() {
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return null;
                            }
                            public void checkClientTrusted(X509Certificate[] certs, String authType) {
                            }
                            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                            }
                        }
                };
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HostnameVerifier allHostsValid = new HostnameVerifier() {
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                };
                HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

                URL url = new URL("https://emm.sifast.fr:9443/publisher/upload/sGxawkeep-512.png");



                File root = android.os.Environment.getExternalStorageDirectory();
                File dir = new File(root.getAbsolutePath()+"/ProjetEMMIcones");

                if (dir.exists() == false) {
                    dir.mkdirs();
                }
                File file = new File( dir, params[0]);
                //File file = new File( PATH + params[0]);
                Log.d("THE WHOLE PATH" ,file.toString());
                long startTime = System.currentTimeMillis();
                Log.d("ImageManager", "download begining");
                Log.d("ImageManager", "download url:" + url);
                Log.d("ImageManager", "downloaded file name:" + params[0]);


                URLConnection urlConnection = url.openConnection();

                InputStream is = urlConnection.getInputStream();

                BufferedInputStream bis = new BufferedInputStream(is);

                ByteArrayBuffer baf = new ByteArrayBuffer(50);
                int current = 0;
                while ((current = bis.read()) != -1) {
                    baf.append((byte) current);
                }

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(baf.toByteArray());
                fos.close();
                Log.d("ImageManager", "download ready in"
                        + ((System.currentTimeMillis() - startTime) / 1000)
                        + " sec");

            } catch (Exception e) {
                Log.d("ImageManager", "Error: " + e);
            }

            return "All Done!";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            createWebAppBookmark("http://google.com", "Google");
        }
    }


}
