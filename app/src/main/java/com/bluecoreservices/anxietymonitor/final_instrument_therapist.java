package com.bluecoreservices.anxietymonitor;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
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

public class final_instrument_therapist extends AppCompatActivity {
    public final static String PAGINA_DEBUG = "final_instrument_thera";
    public static String idPaciente;
    public SharedPreferences sharedPref;

    private SeekBar pregunta1;
    private SeekBar pregunta2;
    private SeekBar pregunta3;
    private SeekBar pregunta4;
    private SeekBar pregunta5;
    private SeekBar pregunta6;
    private SeekBar pregunta7;

    private EditText pregunta8;
    private EditText pregunta9;

    private Integer result_pregunta_1;
    private Integer result_pregunta_2;
    private Integer result_pregunta_3;
    private Integer result_pregunta_4;
    private Integer result_pregunta_5;
    private Integer result_pregunta_6;
    private Integer result_pregunta_7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_instrument_therapist);
        sharedPref = getSharedPreferences("userPref", 0);
        idPaciente = sharedPref.getString("userId", "");
        pregunta1 = (SeekBar)findViewById(R.id.final_instrument_q1_1);
        pregunta2 = (SeekBar)findViewById(R.id.final_instrument_q1_2);
        pregunta3 = (SeekBar)findViewById(R.id.final_instrument_q2_1);
        pregunta4 = (SeekBar)findViewById(R.id.final_instrument_q2_2);
        pregunta5 = (SeekBar)findViewById(R.id.final_instrument_q3_1);
        pregunta6 = (SeekBar)findViewById(R.id.final_instrument_q3_2);
        pregunta7 = (SeekBar)findViewById(R.id.final_instrument_q3_3);

        pregunta8 = (EditText)findViewById(R.id.final_instrument_q4_1);
        pregunta9 = (EditText)findViewById(R.id.final_instrument_q4_2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result_pregunta_1 = pregunta1.getProgress();
                result_pregunta_2 = pregunta2.getProgress();
                result_pregunta_3 = pregunta3.getProgress();
                result_pregunta_4 = pregunta4.getProgress();
                result_pregunta_5 = pregunta5.getProgress();
                result_pregunta_6 = pregunta6.getProgress();
                result_pregunta_7 = pregunta7.getProgress();

                addReporte(result_pregunta_1.toString(), result_pregunta_2.toString(), result_pregunta_3.toString(), result_pregunta_4.toString(), result_pregunta_5.toString(), result_pregunta_6.toString(), result_pregunta_7.toString(), pregunta8.getText().toString(), pregunta9.getText().toString());
            }
        });
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void addReporte(String pregunta1, String pregunta2, String pregunta3, String pregunta4, String pregunta5, String pregunta6, String pregunta7, String pregunta8, String pregunta9) {

        class LoginAsync  extends AsyncTask<String, Void, JSONObject> {
            private Dialog loadingDialog;
            private final String url = "http://app.bluecoreservices.com/webservices/addEvalTerapeuta.php";

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
                loadingDialog = ProgressDialog.show(final_instrument_therapist.this, "Please wait", "Loading...");
            }

            @Override
            protected JSONObject doInBackground(String... params) {

                String uname = idPaciente;
                String q1 = params[0];
                String q2 = params[1];
                String q3 = params[2];
                String q4 = params[3];
                String q5 = params[4];
                String q6 = params[5];
                String q7 = params[6];
                String q8 = params[7];
                String q9 = params[8];

                sbParams = new StringBuilder();

                try {
                    sbParams.append("idPaciente").append("=").append(URLEncoder.encode(uname, charset));
                    sbParams.append("&");
                    sbParams.append("question1").append("=").append(URLEncoder.encode(q1, charset));
                    sbParams.append("&");
                    sbParams.append("question2").append("=").append(URLEncoder.encode(q2, charset));
                    sbParams.append("&");
                    sbParams.append("question3").append("=").append(URLEncoder.encode(q3, charset));
                    sbParams.append("&");
                    sbParams.append("question4").append("=").append(URLEncoder.encode(q4, charset));
                    sbParams.append("&");
                    sbParams.append("question5").append("=").append(URLEncoder.encode(q5, charset));
                    sbParams.append("&");
                    sbParams.append("question6").append("=").append(URLEncoder.encode(q6, charset));
                    sbParams.append("&");
                    sbParams.append("question7").append("=").append(URLEncoder.encode(q7, charset));
                    sbParams.append("&");
                    sbParams.append("question8").append("=").append(URLEncoder.encode(q8, charset));
                    sbParams.append("&");
                    sbParams.append("question9").append("=").append(URLEncoder.encode(q9, charset));
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
                Log.i(PAGINA_DEBUG, result.toString());
                loadingDialog.dismiss();
                boolean resultado = false;

                try {
                    resultado = result.getBoolean("result");

                    if (resultado) {
                        //Toast.makeText(getApplicationContext(), R.string.catego_result_insert_positive, Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else {
                        //Toast.makeText(getApplicationContext(), R.string.catego_result_insert_negative, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        LoginAsync la = new LoginAsync();
        la.execute(pregunta1, pregunta2, pregunta3, pregunta4, pregunta5, pregunta6, pregunta7, pregunta8, pregunta9);
    }

}
