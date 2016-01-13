package com.bluecoreservices.anxietymonitor2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class add_catego extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.bluecoreservices.anxietymonitor2.ID_PACIENTE";
    public final static String PAGINA_DEBUG = "add_catego";
    public static String idPaciente;
    public SharedPreferences sharedPref;
    public static String selectedCatego = null;

    private ArrayList<HashMap<String, String>> categostList = new ArrayList<HashMap<String, String>>();
    public SimpleAdapter adapter;
    public SeekBar elmSeveridad;
    public EditText infoTexto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_catego);
        sharedPref = getSharedPreferences("userPref", 0);
        idPaciente = sharedPref.getString("userId", "");
        elmSeveridad = (SeekBar)findViewById(R.id.add_catego_severidad);
        infoTexto = (EditText)findViewById(R.id.catego_detalle);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);

                if (selectedCatego != null) {
                    openRespirationDialog();
                }
            }
        });

        Button addCategoria = (Button) findViewById(R.id.add_catego_nombre);
        addCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCategoDialog();
            }
        });

        //Inicializacion de las categorías
        getCategos();
    }

    private void openCategoDialog() {
        //inicializacion del mensaje
        AlertDialog.Builder builder = new AlertDialog.Builder(add_catego.this);

        LinearLayout inputWrapper = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(30, 0, 30, 0);

        final EditText input = new EditText(add_catego.this);
        inputWrapper.addView(input, lp);

        //Titulo y Mensaje
        builder.setMessage(R.string.new_catego_dialog_message)
                .setTitle(R.string.new_catego_dialog_title)
                .setView(inputWrapper);

        //Botones
        builder.setPositiveButton(R.string.new_catego_dialog_okbutton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Log.i(PAGINA_DEBUG, "OK Presionado");
                Log.i(PAGINA_DEBUG, "Valor del Input: " + input.getText().toString());
                addCatego(input.getText().toString());
            }
        });
        builder.setNegativeButton(R.string.new_catego_dialog_cancelbutton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                Log.i(PAGINA_DEBUG, "Cancelar Presionado");
            }
        });



        final AlertDialog dialog = builder.create();
        dialog.show();

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer temp = input.length();
                if (temp > 0) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                } else {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

    }

    private void getCategos() {

        class LoginAsync  extends AsyncTask<String, Void, JSONObject> {
            private Dialog loadingDialog;
            private final String url = "http://app.bluecoreservices.com/webservices/getCategorias.php";

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
                loadingDialog = ProgressDialog.show(add_catego.this, "Please wait", "Loading...");
            }

            @Override
            protected JSONObject doInBackground(String... params) {

                String uname = idPaciente;

                sbParams = new StringBuilder();

                try {
                    sbParams.append("idPaciente").append("=").append(URLEncoder.encode(uname, charset));
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

                Spinner listaCatego = (Spinner)findViewById(R.id.spinner_catego);
                Integer listSize = listaCatego.getCount() -1;

                JSONArray categoriaLista = null;
                ArrayList<String> nombres = new ArrayList<String>();
                final ArrayList<String> ids = new ArrayList<String>();

                ArrayAdapter adapter = new ArrayAdapter(add_catego.this, android.R.layout.simple_spinner_item, nombres);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                if (listSize > 0){
                    nombres.clear();
                    adapter.notifyDataSetChanged();
                }

                try {
                    categoriaLista = result.getJSONArray("categorias");


                    for (int i = 0; i < categoriaLista.length(); i++){

                        JSONObject categoriaElemento = categoriaLista.getJSONObject(i);

                        nombres.add(categoriaElemento.getString("nombre"));
                        ids.add(categoriaElemento.getString("id"));
                    }

                    listaCatego.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    listaCatego.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedCatego = ids.get(position);
                            Log.i(PAGINA_DEBUG, selectedCatego);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        LoginAsync la = new LoginAsync();
        la.execute();
    }

    private void addCatego(String categoName) {

        class LoginAsync  extends AsyncTask<String, Void, JSONObject> {
            private Dialog loadingDialog;
            private final String url = "http://app.bluecoreservices.com/webservices/addCatego.php";

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
                loadingDialog = ProgressDialog.show(add_catego.this, "Please wait", "Loading...");
            }

            @Override
            protected JSONObject doInBackground(String... params) {

                String uname = idPaciente;
                String cname = params[0];

                sbParams = new StringBuilder();

                try {
                    sbParams.append("idPaciente").append("=").append(URLEncoder.encode(uname, charset));
                    sbParams.append("&");
                    sbParams.append("nuevaCatego").append("=").append(URLEncoder.encode(cname, charset));
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

                getCategos();
            }
        }
        LoginAsync la = new LoginAsync();
        la.execute(categoName);
    }

    private void addReporte(final String selectedCategoNum, final String severidadValue, final String informacionText) {

        class LoginAsync  extends AsyncTask<String, Void, JSONObject> {
            private Dialog loadingDialog;
            private final String url = "http://app.bluecoreservices.com/webservices/addMood.php";

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
                loadingDialog = ProgressDialog.show(add_catego.this, "Please wait", "Loading...");
            }

            @Override
            protected JSONObject doInBackground(String... params) {

                String uname = idPaciente;
                String categoria = params[0];
                String severidad = params[1];
                String informacion = params[2];

                sbParams = new StringBuilder();

                try {
                    sbParams.append("idPaciente").append("=").append(URLEncoder.encode(uname, charset));
                    sbParams.append("&");
                    sbParams.append("categoria").append("=").append(URLEncoder.encode(categoria, charset));
                    sbParams.append("&");
                    sbParams.append("severidad").append("=").append(URLEncoder.encode(severidad, charset));
                    sbParams.append("&");
                    sbParams.append("informacion").append("=").append(URLEncoder.encode(informacion, charset));
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
                        Toast.makeText(getApplicationContext(), R.string.catego_result_insert_positive, Toast.LENGTH_LONG).show();
                        Log.i(PAGINA_DEBUG, result.getString("moodId"));
                        finish(); //for now
                    }
                    else {
                        Toast.makeText(getApplicationContext(), R.string.catego_result_insert_negative, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        LoginAsync la = new LoginAsync();
        la.execute(selectedCategoNum, severidadValue, informacionText);
    }

    private void openRespirationDialog() {
        //inicializacion del mensaje
        AlertDialog.Builder builder = new AlertDialog.Builder(add_catego.this);

        //Titulo y Mensaje
        builder.setMessage(R.string.respiration_dialog_message)
                .setTitle(R.string.respiration_dialog_title);
        //.setView(inputWrapper);

        //Botones
        builder.setPositiveButton(R.string.respiration_dialog_okbutton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Log.i(PAGINA_DEBUG, "OK Presionado");
                Integer resSeveridad = Math.round(elmSeveridad.getProgress() / 10);
                addReporte(selectedCatego, resSeveridad.toString(), infoTexto.getText().toString());

                Intent intent = new Intent(add_catego.this, breathingGame.class);
                intent.putExtra(EXTRA_MESSAGE, idPaciente);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.respiration_dialog_cancelbutton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                Log.i(PAGINA_DEBUG, "Cancelar Presionado");
                Integer resSeveridad = Math.round(elmSeveridad.getProgress() / 10);
                addReporte(selectedCatego, resSeveridad.toString(), infoTexto.getText().toString());
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }
}
