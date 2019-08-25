package com.example.matchpet;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class AddPetchsActivity extends AppCompatActivity  {

    String ServerURL = "http://192.168.56.1/match/webservice/webservice.php" ;
    String key = "123/*saveRegister";
    EditText name, race, color, description ;
    Button button,pdf;
    private int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_petchs);
        getSupportActionBar().setTitle("Agregar Mascota");

        name = (EditText) findViewById(R.id.txtName);
        race = (EditText)findViewById(R.id.txtRace);
        color = (EditText)findViewById(R.id.txtColor);
        description = (EditText)findViewById(R.id.txtDescription);
        button = (Button)findViewById(R.id.btn_save);

        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                //Toast.makeText(AddPetchsActivity.this, "Hola", Toast.LENGTH_SHORT).show();
                String[] datax = getFromSharedPreferences("username");
                saveRegister(Integer.parseInt(datax[2]));
            }
        });

        pdf = (Button) findViewById(R.id.btnImage);

        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();

                intent.setType("image/png");

                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PICK_IMAGE_REQUEST);
                //Toast.makeText(AddPetchsActivity.this, "Prueba", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String[] getFromSharedPreferences(String key){
        SharedPreferences sharedPreferences = getSharedPreferences("name",MODE_PRIVATE);
        String[] data = new String[3];
        data[0] = sharedPreferences.getString("username","");
        data[1] = sharedPreferences.getString("password","");
        data[2] = sharedPreferences.getString("id_c_usuario","");
        return data;
    }

    private void saveRegister(final Integer idUser){
        System.out.println(idUser);
        StringRequest request = new StringRequest(Request.Method.POST, ServerURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.contains("success")){
                    Toast.makeText(AddPetchsActivity.this, "Mascota agregada correctamente", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(AddPetchsActivity.this,MainActivity.class);
                    startActivity(i);
                }else if(response.contains("campos")){
                    Toast.makeText(AddPetchsActivity.this, "Todos los campos son requeridos", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(AddPetchsActivity.this, "Error al insertar mascota", Toast.LENGTH_SHORT).show();
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
                params.put("api_key","123/*saveRegister");
                params.put("name",name.getText().toString().trim());
                params.put("race",race.getText().toString().trim());
                params.put("color",color.getText().toString().trim());
                params.put("description",description.getText().toString().trim());
                params.put("id_c_usuario",String.valueOf(Integer.valueOf(idUser)));
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }


}
