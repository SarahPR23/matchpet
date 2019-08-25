package com.example.matchpet;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class LoginActivity extends AppCompatActivity {

    ProgressDialog dialog;
    EditText user,pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        user = (EditText) findViewById(R.id.et_email);
        pass = (EditText) findViewById(R.id.et_password);
        final Button button = (Button) findViewById(R.id.btn_login);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new ProgressDialog(LoginActivity.this);
                dialog.setMessage("Cargando..."); // Setting Message
                dialog.setTitle("Enviando Datos"); // Setting Title
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                dialog.show(); // Display Progress Dialog
                dialog.setCancelable(false);
                Thread tr = new Thread(){
                    @Override
                    public void run() {
                        final String res = sendRequest(user.getText().toString(),pass.getText().toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int r = objJSON(res);
                                if(r>0){
                                    try {
                                        JSONArray arr = new JSONArray(res);
                                        JSONObject jObj = arr.getJSONObject(0);
                                        saveLoginSharedPreferences(jObj.getString("nombre"),jObj.getString("apellidos"),jObj.getString("id_c_usuario"),jObj.getString("usuario"),jObj.getString("password"));
                                        dialog.setProgress(500);
                                        dialog.dismiss();
                                        Intent i = new Intent(LoginActivity.this,MainActivity.class);

                                        startActivity(i);
                                    }catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    //Toast.makeText(LoginActivity.this, "Ok", Toast.LENGTH_SHORT).show();
                                }else{
                                    dialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                };
                tr.start();
            }
        });

        final TextView textView = (TextView) findViewById(R.id.txtRegister);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this,UserRegisterActivity.class);

                startActivity(i);
            }
        });


    }

    private void saveLoginSharedPreferences(String nombre,String apellidos,String idUser,String username,String password){
        SharedPreferences sharedPreferences = getSharedPreferences("name",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("nombre",nombre);
        editor.putString("apellidos",apellidos);
        editor.putString("id_c_usuario",idUser);
        editor.putString("username",username);
        editor.putString("password",password);
        editor.apply();
    }


    public String sendRequest(String username,String password){
        String parametros = "usuario="+username+"&password="+password+"&api_key=123/*setLogin";
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

    public  int objJSON(String resp){
        int res = 0;
        try {
            JSONArray json = new JSONArray(resp);
            if(json.length()>0)
                res = 1;
        }catch (Exception e){}
        return res;
    }

}
