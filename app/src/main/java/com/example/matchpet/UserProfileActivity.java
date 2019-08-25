package com.example.matchpet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UserProfileActivity extends AppCompatActivity {

    EditText name,surname,mail,phone,cellphone,user,pass;
    Button button;
    String ServerURL = "http://192.168.56.1/match/webservice/webservice.php" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setTitle("Perfil");
        String[] data = getFromSharedPreferences("username");
        //Toast.makeText(UserProfileActivity.this, username[1], Toast.LENGTH_SHORT).show();
        final String resp = getDataResult(Integer.parseInt(data[2]));
        //System.out.println(resp);
        try {
            JSONArray arr = new JSONArray(resp);
            JSONObject jObj = arr.getJSONObject(0);
            String nombre = jObj.getString("nombre");
            String apellidos = jObj.getString("apellidos");
            String correo = jObj.getString("correo");
            String telefono = jObj.getString("telefono");
            String celular = jObj.getString("celular");
            String usuario = jObj.getString("usuario");
            name = (EditText) findViewById(R.id.txtName);
            name.setText(nombre);
            surname = (EditText) findViewById(R.id.txtSurname);
            surname.setText(apellidos);
            mail = (EditText) findViewById(R.id.txtEmail);
            mail.setText(correo);
            phone = (EditText) findViewById(R.id.txtPhone);
            phone.setText(telefono);
            cellphone = (EditText) findViewById(R.id.txtCellPhone);
            cellphone.setText(celular);
            user = (EditText) findViewById(R.id.txtUser);
            user.setText(usuario);
            pass = (EditText) findViewById(R.id.txtPassword);

            button = (Button) findViewById(R.id.btnEditUser);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] datax = getFromSharedPreferences("username");
                    saveRegister(Integer.parseInt(datax[2]));
                    //Toast.makeText(UserProfileActivity.this, "Prueba", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private String[] getFromSharedPreferences(String key){
        SharedPreferences sharedPreferences = getSharedPreferences("name",MODE_PRIVATE);
        String[] data = new String[3];
        data[0] = sharedPreferences.getString("username","");
        data[1] = sharedPreferences.getString("password","");
        data[2] = sharedPreferences.getString("id_c_usuario","");
        return data;
    }

    public String getDataResult(Integer idUser){
        System.out.println(idUser);
        String parametros = "id_c_usuario="+idUser+"&api_key=123/*getProfile";
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


    private void saveRegister(final Integer idUser){
        StringRequest request = new StringRequest(Request.Method.POST, ServerURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.contains("success")){
                    Toast.makeText(UserProfileActivity.this, "Usuario modificado correctamente", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(UserProfileActivity.this,MainActivity.class);
                    startActivity(i);
                }else if(response.contains("campos")){
                    Toast.makeText(UserProfileActivity.this, "Todos los campos son requeridos", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(UserProfileActivity.this, "Error al modificar Usuario", Toast.LENGTH_SHORT).show();
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
                params.put("api_key","123/*setUserProfile");
                params.put("name",name.getText().toString().trim());
                params.put("surname",surname.getText().toString().trim());
                params.put("mail",mail.getText().toString().trim());
                params.put("phone",phone.getText().toString().trim());
                params.put("cellphone",cellphone.getText().toString().trim());
                params.put("user",user.getText().toString().trim());
                params.put("pass",pass.getText().toString().trim());
                params.put("id_c_usuario",String.valueOf(Integer.valueOf(idUser)));
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

}
