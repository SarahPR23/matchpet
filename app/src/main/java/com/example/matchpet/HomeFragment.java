package com.example.matchpet;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    String ServerURL = "http://192.168.56.1/match/webservice/webservice.php" ;
    ListView listView;
    ProgressDialog dialog;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setActionBarTitle("Inicio");
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Cargando..."); // Setting Message
        dialog.setTitle("Obteniendo Datos"); // Setting Title
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        dialog.show(); // Display Progress Dialog
        dialog.setCancelable(false);
        String[] datax = getFromSharedPreferences("username");
        //Toast.makeText(getContext(), datax[2], Toast.LENGTH_SHORT).show();
        Integer idUSer = Integer.parseInt(datax[2]);
        listView = (ListView) v.findViewById(R.id.listview);
        getJSON(ServerURL+"?api_key=123/*listPetchs&id_c_usuario="+idUSer);

        return v;
    }

    private String[] getFromSharedPreferences(String key){
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("name", Context.MODE_PRIVATE);
        //SharedPreferences sharedPreferences = getFromSharedPreferences("name",Mode);
        String[] data = new String[3];
        data[0] = sharedPreferences.getString("username","");
        data[1] = sharedPreferences.getString("password","");
        data[2] = sharedPreferences.getString("id_c_usuario","");
        return data;
    }

    private void getJSON(final String urlWebService){
        class GetJSON extends AsyncTask<Void,Void,String>{

            @Override
            protected void onPreExecute(){
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s){
                //System.out.println(s);
                super.onPostExecute(s);
                if (!TextUtils.isEmpty(s) && s!=null) {
                    //Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
                    try {
                        loadIntoListView(s);
                    } catch (JSONException e) {
                        dialog.dismiss();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }

    private void loadIntoListView(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        String[] heroes = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            heroes[i] = obj.getString("raza");
        }
        dialog.dismiss();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, heroes);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                String selectedItem = (String) parent.getItemAtPosition(position);

                // Display the selected item text on TextView
                //Toast.makeText(getContext(), selectedItem, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getContext(),DescriptionActivity.class);
                i.putExtra("name",selectedItem);
                startActivity(i);
            }
        });

        listView.setLongClickable(true);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
                String selectedItem = (String) arg0.getItemAtPosition(arg2);
                confirm(selectedItem);
                //Toast.makeText(getContext(), "Pulsacion larga", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    public void confirm(final String name){
        new AlertDialog.Builder(getContext())
                .setMessage("Seguro que desea eliminar esta mascota?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteRegister(name);
                        Toast.makeText(getContext(), "Ok", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


    public void deleteRegister(final String namex){
        String rx = namex;
        StringRequest request = new StringRequest(Request.Method.POST, ServerURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.contains("success")){
                    Toast.makeText(getContext(), "Mascota eliminada correctamente", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getContext(),MainActivity.class);
                    startActivity(i);
                }else if(response.contains("campos")){
                    Toast.makeText(getContext(), "Todos los campos son requeridos", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), "Error al insertar mascota", Toast.LENGTH_SHORT).show();
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
                params.put("api_key","123/*delete");
                params.put("name",namex);
                return params;
            }
        };
        Volley.newRequestQueue(getContext()).add(request);
    }



}
