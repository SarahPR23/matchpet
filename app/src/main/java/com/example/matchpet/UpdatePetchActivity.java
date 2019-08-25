package com.example.matchpet;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.service.autofill.OnClickAction;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UpdatePetchActivity extends AppCompatActivity {

    String ServerURL = "http://192.168.56.1/match/webservice/webservice.php" ;
    private EditText n,d,dsc,col,hiden;
    private Button button,imageBtn;
    private static final int IMAGE_REQUEST_CODE = 3;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_petch);
        getSupportActionBar().setTitle("Actualizar mascota");
        final String name = getIntent().getStringExtra("name");

        button = (Button) findViewById(R.id.btn_edit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSaveRegister();
                //Toast.makeText(UpdatePetchActivity.this, "Editando", Toast.LENGTH_SHORT).show();
            }
        });

        imageBtn = (Button) findViewById(R.id.btnImage);
        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), IMAGE_REQUEST_CODE);
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
            String color = jObj.getString("color");
            n = (EditText) findViewById(R.id.txtName);
            d = (EditText) findViewById(R.id.txtRace);
            dsc = (EditText) findViewById(R.id.txtDescription);
            col = (EditText) findViewById(R.id.txtColor);
            hiden = (EditText) findViewById(R.id.txtHiddenRace);
            n.setText(namex);
            d.setText(race);
            dsc.setText(description);
            col.setText(color);
            hiden.setText(race);
            //new DescriptionActivity.DownloadImageFromInternet((ImageView) findViewById(R.id.photoProfile))
                    //.execute("http://187.162.76.66:88/match/res/images/mascota_"+id+".png");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }









    public String getDataResult(String name){
        String parametros = "name="+name+"&api_key=123/*onlyRow";
        HttpURLConnection conection=null;
        String respuesta = "";
        try {
            URL url = new URL(ServerURL);
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

    private void setSaveRegister(){
        StringRequest request = new StringRequest(Request.Method.POST, ServerURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.contains("success")){
                    Toast.makeText(UpdatePetchActivity.this, "Mascota actualizada correctamente", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(UpdatePetchActivity.this,MainActivity.class);
                    startActivity(i);
                }else if(response.contains("campos")){
                    Toast.makeText(UpdatePetchActivity.this, "Todos los campos son requeridos", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(UpdatePetchActivity.this, "Error al actualizar mascota", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("api_key","123/*setSaveRegister");
                params.put("name",n.getText().toString().trim());
                params.put("race",d.getText().toString().trim());
                params.put("color",col.getText().toString().trim());
                params.put("description",dsc.getText().toString().trim());
                params.put("parametro",hiden.getText().toString().trim());
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

}
