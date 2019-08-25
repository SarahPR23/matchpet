package com.example.matchpet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class DescriptionActivity extends AppCompatActivity {

    private TextView n,d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        final String name = getIntent().getStringExtra("name");
        getSupportActionBar().setTitle("Descripción: "+name);

        FloatingActionButton fab = findViewById(R.id.editPetch);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //      .setAction("Action", null).show();
                Intent i = new Intent(DescriptionActivity.this,UpdatePetchActivity.class);
                i.putExtra("name",name);
                startActivity(i);
            }
        });

        final String resp = getDataResult(name);

        try {
            JSONArray arr = new JSONArray(resp);
            JSONObject jObj = arr.getJSONObject(0);
            String id = jObj.getString("id_c_mascota");
            String namex = jObj.getString("nombre");
            String race = jObj.getString("raza");
            String description = jObj.getString("descripcion");
            n = (TextView) findViewById(R.id.txtnamePetch);
            d = (TextView) findViewById(R.id.txtDescriptionPetchs);
            n.setText("Nombre: "+namex+" Raza: "+race);
            d.setText(description);
            new DownloadImageFromInternet((ImageView) findViewById(R.id.photoProfile))
            .execute("http://192.168.56.1/match/res/images/mascota_"+id+".png");
        } catch (JSONException e) {
            e.printStackTrace();
        }


       // new DownloadImageFromInternet((ImageView) findViewById(R.id.photoProfile))
                //.execute("http://192.158.3.194/match/web/res/images/ga2.jpg");
    }

    public String getDataResult(String name){
        String parametros = "name="+name+"&api_key=123/*onlyRow";
        HttpURLConnection conection=null;
        String respuesta = "";
        try {
            URL url = new URL("http://192.168.56.1/match/webservice/webservice.php");
            conection = (HttpURLConnection)url.openConnection();
            conection.setRequestMethod("POST");
            conection.setRequestProperty("Content-Lenght",""+Integer.toString(parametros.getBytes().length));

            conection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(conection.getOutputStream());
            wr.writeBytes(parametros);
            wr.close();

            Scanner inStream = new Scanner(conection.getInputStream());
            while (inStream.hasNextLine())
                respuesta +=(inStream.nextLine());

        }catch (Exception e){

        }
        //System.out.println(respuesta);
        return respuesta.toString();
    }



    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
            //Toast.makeText(getApplicationContext(), "Please wait, it may take a few minute...", Toast.LENGTH_SHORT).show();
        }

        protected Bitmap doInBackground(String... urls) {

            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}
