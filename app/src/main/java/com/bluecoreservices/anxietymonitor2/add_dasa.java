package com.bluecoreservices.anxietymonitor2;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
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

public class add_dasa extends AppCompatActivity {
    public final static String PAGINA_DEBUG = "add_dasa";

    private Integer idPaciente;

    private SeekBar add_dasa_1;
    private SeekBar add_dasa_2;
    private SeekBar add_dasa_3;
    private SeekBar add_dasa_4;
    private SeekBar add_dasa_5;
    private SeekBar add_dasa_6;
    private SeekBar add_dasa_7;
    private SeekBar add_dasa_8;


    private Integer dasa_result_1;
    private Integer dasa_result_2;
    private Integer dasa_result_3;
    private Integer dasa_result_4;
    private Integer dasa_result_5;
    private Integer dasa_result_6;
    private Integer dasa_result_7;
    private Integer dasa_result_8;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dasa);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dasa_result_1 = add_dasa_1.getProgress();
                dasa_result_2 = add_dasa_2.getProgress();
                dasa_result_3 = add_dasa_3.getProgress();
                dasa_result_4 = add_dasa_4.getProgress();
                dasa_result_5 = add_dasa_5.getProgress();
                dasa_result_6 = add_dasa_6.getProgress();
                dasa_result_7 = add_dasa_7.getProgress();
                dasa_result_8 = add_dasa_8.getProgress();


                registerDasa(idPaciente, dasa_result_1, dasa_result_2, dasa_result_3, dasa_result_4, dasa_result_5, dasa_result_6, dasa_result_7, dasa_result_8);

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        idPaciente = Integer.parseInt(intent.getStringExtra(MainActivity.EXTRA_MESSAGE));

        add_dasa_1 = (SeekBar) findViewById(R.id.add_dasa_1);
        add_dasa_2 = (SeekBar) findViewById(R.id.add_dasa_2);
        add_dasa_3 = (SeekBar) findViewById(R.id.add_dasa_3);
        add_dasa_4 = (SeekBar) findViewById(R.id.add_dasa_4);
        add_dasa_5 = (SeekBar) findViewById(R.id.add_dasa_5);
        add_dasa_6 = (SeekBar) findViewById(R.id.add_dasa_6);
        add_dasa_7 = (SeekBar) findViewById(R.id.add_dasa_7);
        add_dasa_8 = (SeekBar) findViewById(R.id.add_dasa_8);
    }

    private void registerDasa(Integer idPaciente, Integer result_1, Integer result_2, Integer result_3, Integer result_4, Integer result_5, Integer result_6, Integer result_7, Integer result_8) {

        class LoginAsync  extends AsyncTask<Integer, Void, JSONObject> {
            private Dialog loadingDialog;
            private final String url = "http://app.bluecoreservices.com/webservices/addDAS-A.php";

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
                loadingDialog = ProgressDialog.show(add_dasa.this, "Please wait", "Loading...");
            }

            @Override
            protected JSONObject doInBackground(Integer... params) {

                Integer idPac = params[0];
                Integer question1 = Math.round(params[1]/10);
                Integer question2 = Math.round(params[2]/10);
                Integer question3 = Math.round(params[3]/10);
                Integer question4 = Math.round(params[4]/10);
                Integer question5 = Math.round(params[5]/10);
                Integer question6 = Math.round(params[6]/10);
                Integer question7 = Math.round(params[7]/10);
                Integer question8 = Math.round(params[8]/10);

                sbParams = new StringBuilder();

                try {
                    sbParams.append("id").append("=").append(URLEncoder.encode(idPac.toString(), charset));
                    sbParams.append("&");
                    sbParams.append("question1").append("=").append(URLEncoder.encode(question1.toString(), charset));
                    sbParams.append("&");
                    sbParams.append("question2").append("=").append(URLEncoder.encode(question2.toString(), charset));
                    sbParams.append("&");
                    sbParams.append("question3").append("=").append(URLEncoder.encode(question3.toString(), charset));
                    sbParams.append("&");
                    sbParams.append("question4").append("=").append(URLEncoder.encode(question4.toString(), charset));
                    sbParams.append("&");
                    sbParams.append("question5").append("=").append(URLEncoder.encode(question5.toString(), charset));
                    sbParams.append("&");
                    sbParams.append("question6").append("=").append(URLEncoder.encode(question6.toString(), charset));
                    sbParams.append("&");
                    sbParams.append("question7").append("=").append(URLEncoder.encode(question7.toString(), charset));
                    sbParams.append("&");
                    sbParams.append("question8").append("=").append(URLEncoder.encode(question8.toString(), charset));

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
                    Toast.makeText(getApplicationContext(), "Diario Agregado", Toast.LENGTH_LONG).show();
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Hubo un error en el servidor", Toast.LENGTH_LONG).show();
                }
            }
        }
        LoginAsync la = new LoginAsync();
        la.execute(idPaciente, result_1, result_2, result_3, result_4, result_5, result_6, result_7, result_8);
    }
}
