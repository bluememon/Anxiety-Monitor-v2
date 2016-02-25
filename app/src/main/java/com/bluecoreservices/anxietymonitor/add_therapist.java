package com.bluecoreservices.anxietymonitor;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class add_therapist extends AppCompatActivity {
    public final static String PAGINA_DEBUG = "add_therapist";

    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextUserName;
    private EditText editTextPassword;
    private EditText editTextRepeatPassword;

    String firstname;
    String lastname;
    String username;
    String password;
    String repassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_therapist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    firstname = editTextFirstName.getText().toString();
                    lastname = editTextLastName.getText().toString();
                    username = editTextUserName.getText().toString();
                    password = editTextPassword.getText().toString();
                    repassword = editTextRepeatPassword.getText().toString();

                if (validateForm(firstname, lastname, username, password, repassword)){
                    login(firstname, lastname, username, password);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Faltan datos del Terapeuta", Toast.LENGTH_LONG).show();
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextFirstName = (EditText) findViewById(R.id.add_therapist_first_name_editText);
        editTextLastName = (EditText) findViewById(R.id.add_therapist_last_name_editText);
        editTextUserName = (EditText) findViewById(R.id.add_therapist_user_name_editText);
        editTextPassword = (EditText) findViewById(R.id.add_therapist_password_editText);
        editTextRepeatPassword = (EditText) findViewById(R.id.add_therapist_repeat_password_editText);
    }

    private Boolean validateForm(String firstname, String lastname, String username, String password, String repassword){
        //primero revisamos si los campos no estan vacios
        if (firstname != "" && lastname != "" && username != "" && password != "" && repassword != ""){
            //ahora revisamos que los passwords coincidan
            if (password.equals(repassword)) {
                return true;
            }
            else {
                Log.e(PAGINA_DEBUG, "password's no coinciden");
            }
        }
        else {
            Log.e(PAGINA_DEBUG, "hay un dato faltante o en blanco");
        }
        return false;
    }

    private void login(String firstname, String lastname, String username, String password) {

        class LoginAsync  extends AsyncTask<String, Void, JSONObject> {
            private Dialog loadingDialog;
            private final String url = "http://app.bluecoreservices.com/webservices/agregarTerapeuta.php";

            String charset = "UTF-8";
            HttpURLConnection conn;
            DataOutputStream wr;
            StringBuilder result = new StringBuilder();
            URL urlObj;
            JSONObject jObj = null;
            StringBuilder sbParams;
            String paramsString;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(add_therapist.this, "Please wait", "Loading...");
            }

            @Override
            protected JSONObject doInBackground(String... params) {

                String fName = params[0];
                String lName = params[1];
                String uName = params[2];
                String pass = params[3];

                sbParams = new StringBuilder();

                try {
                    sbParams.append("firstName").append("=").append(URLEncoder.encode(fName, charset));
                    sbParams.append("&");
                    sbParams.append("lastName").append("=").append(URLEncoder.encode(lName, charset));
                    sbParams.append("&");
                    sbParams.append("username").append("=").append(URLEncoder.encode(uName, charset));
                    sbParams.append("&");
                    sbParams.append("password").append("=").append(URLEncoder.encode(pass, charset));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                try {
                    urlObj = new URL(url);

                    conn = (HttpURLConnection) urlObj.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Accept-Charset", charset);
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);

                    conn.connect();

                    paramsString = sbParams.toString();

                    wr = new DataOutputStream(conn.getOutputStream());
                    wr.writeBytes(paramsString);
                    wr.flush();
                    wr.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    //response from the server
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

                conn.disconnect();

                String stringResult = result.toString().trim();

                try {
                    jObj = new JSONObject(stringResult);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return jObj;
            }


            @Override
            protected void onPostExecute(JSONObject result) {

                Boolean respuesta = false;
                Log.i("variable s", result.toString());
                loadingDialog.dismiss();

                try {
                    respuesta = result.getBoolean("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (respuesta) {
                    Log.i("login", "si se agregó el terapeuta");
                    Toast.makeText(getApplicationContext(), "Terapeuta Agregado", Toast.LENGTH_LONG).show();
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Hubo un error en el servidor", Toast.LENGTH_LONG).show();
                }
            }
        }
        LoginAsync la = new LoginAsync();
        la.execute(firstname, lastname, username, password);
    }
}
