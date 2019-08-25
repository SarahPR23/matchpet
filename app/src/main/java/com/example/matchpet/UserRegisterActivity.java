package com.example.matchpet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class UserRegisterActivity extends AppCompatActivity {

    String ServerURL = "http://192.168.56.1/match/webservice/webservice.php";
    private EditText name,surname,email,phone,cellphone,user,password;
    private  Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        getSupportActionBar().setTitle("Registro Usuario");
        name = (EditText) findViewById(R.id.txtName);
        surname = (EditText) findViewById(R.id.txtSurname);
        email = (EditText) findViewById(R.id.txtEmail);
        phone = (EditText) findViewById(R.id.txtPhone);
        cellphone = (EditText) findViewById(R.id.txtCellPhone);
        user = (EditText) findViewById(R.id.txtUser);
        password = (EditText) findViewById(R.id.txtPassword);
        button = (Button) findViewById(R.id.btnSaveUser);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUser();
            }
        });

    }

    protected void saveUser(){
        StringRequest request = new StringRequest(Request.Method.POST, ServerURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.contains("success")){
                    Toast.makeText(UserRegisterActivity.this, "Usuario agregada correctamente", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(UserRegisterActivity.this,LoginActivity.class);
                    startActivity(i);
                }else if(response.contains("campos")){
                    Toast.makeText(UserRegisterActivity.this, "Todos los campos son requeridos", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(UserRegisterActivity.this, "Error al insertar usuario", Toast.LENGTH_SHORT).show();
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
                params.put("api_key","123/*saveUser");
                params.put("name",name.getText().toString().trim());
                params.put("surname",surname.getText().toString().trim());
                params.put("email",email.getText().toString().trim());
                params.put("phone",phone.getText().toString().trim());
                params.put("cellphone",cellphone.getText().toString().trim());
                params.put("user",user.getText().toString().trim());
                params.put("password",password.getText().toString().trim());
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }
}
