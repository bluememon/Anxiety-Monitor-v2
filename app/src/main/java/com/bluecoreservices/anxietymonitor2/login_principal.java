package com.bluecoreservices.anxietymonitor2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import service.dasa_notifications;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class login_principal extends AppCompatActivity {

    SharedPreferences sharedPref;


    private EditText editTextUserName;
    private EditText editTextPassword;

    public static final String USER_NAME = "USERNAME";

    String username;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_principal);

        editTextUserName = (EditText) findViewById(R.id.username);
        editTextPassword = (EditText) findViewById(R.id.password);

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                username = editTextUserName.getText().toString();
                password = editTextPassword.getText().toString();

                login(username, password);
            }
        });

        startService(new Intent(this, dasa_notifications.class));

    }
    @Override
    public void onResume() {
        super.onResume();
        sharedPref = getSharedPreferences("userPref", 0);

        if (sharedPref.getString("logged", null) != null) {
            Log.i("logged", sharedPref.getString("logged", ""));
            Log.i("id", sharedPref.getString("id", ""));
            Log.i("firstName", sharedPref.getString("firstName", ""));
            Log.i("lastName", sharedPref.getString("lastName", ""));
            Log.i("type", sharedPref.getString("type", ""));

            sendToLandingPage(sharedPref.getString("type", ""));
        }
    }

    public void sendToLandingPage(String position) {
        Intent intent = null;
        Log.e("tipo actual", position);
        switch (position) {
            case "1":
                // Go to therapist list
                intent = new Intent(login_principal.this, listadoTerapeutas.class);
                break;
            case "2":
                //Go to patient list
                intent = new Intent(login_principal.this, ListadoPacientes.class);
                break;
            case "3":
                //Go to main activity
                intent = new Intent(login_principal.this, MainActivity.class);
                break;
        }
        startActivity(intent);
    }

    public static String getMD5EncryptedString(String encTarget){
        MessageDigest mdEnc = null;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Exception while encrypting to md5");
            e.printStackTrace();
        } // Encryption algorithm
        mdEnc.update(encTarget.getBytes(), 0, encTarget.length());
        String md5 = new BigInteger(1, mdEnc.digest()).toString(16);
        while ( md5.length() < 32 ) {
            md5 = "0"+md5;
        }
        return md5;
    }

    private void login(final String username, String password) {

        class LoginAsync  extends AsyncTask<String, Void, JSONObject> {
            private Dialog loadingDialog;
            private final String url = "http://app.bluecoreservices.com/webservices/loginCheck.php";

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
                loadingDialog = ProgressDialog.show(login_principal.this, "Please wait", "Loading...");
            }

            @Override
            protected JSONObject doInBackground(String... params) {

                String uname = params[0];
                String pass = params[1];
                String pass2 = getMD5EncryptedString(pass);

                sbParams = new StringBuilder();

                try {
                    sbParams.append("username").append("=").append(URLEncoder.encode(uname, charset));
                    sbParams.append("&");
                    sbParams.append("password").append("=").append(URLEncoder.encode(pass2, charset));
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
                //String s = result.trim();
                String tipo = null;
                Log.i("variable s", result.toString());
                loadingDialog.dismiss();

                try {
                    tipo = result.getString("logged");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (tipo == "true") {
                    Log.i("login", "si lo encontro como trv");
                    SharedPreferences.Editor editor= sharedPref.edit();

                    try {
                        editor.putString("logged", result.getString("logged").toString());
                        editor.putString("userId", result.getString("id").toString());
                        editor.putString("firstName", result.getString("firstName").toString());
                        editor.putString("lastName", result.getString("lastName").toString());
                        editor.putString("type", result.getString("type").toString());

                        editor.commit();

                        Log.i("login", "se agregaron las variables globales");

                        sendToLandingPage(result.getString("type").toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Nombre de usuario o password incorrecto", Toast.LENGTH_LONG).show();
                }
            }
        }
        LoginAsync la = new LoginAsync();
        la.execute(username, password);
    }
}

