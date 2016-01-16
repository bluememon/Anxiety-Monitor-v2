package com.bluecoreservices.anxietymonitor;

        import android.app.AlertDialog;
        import android.app.ProgressDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.net.Uri;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.support.design.widget.FloatingActionButton;
        import android.support.design.widget.Snackbar;
        import android.support.v4.widget.SwipeRefreshLayout;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ListView;
        import android.widget.SimpleAdapter;
        import android.widget.TextView;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.HashMap;

public class ListadoPacientes extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.bluecoreservices.anxietymonitor.ID_PACIENTE";
    public final static String EXTRA_MESSAGE_NAME = "com.bluecoreservices.anxietymonitor.ID_PACIENTE";
    public final static String EXTRA_MESSAGE_TERAPEUTA = "com.bluecoreservices.anxietymonitor.ID_TERAPEUTA";
    public final static String PAGINA_DEBUG = "Listado Pacientes";

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    SharedPreferences sharedPref;
    private String urlText;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private String idTerapeuta;
    SimpleAdapter adapter;

    JSONParser json1 = new JSONParser();
    JSONArray pacientes = null;
    ArrayList<HashMap<String, String>> patientsList;
    ListView lista;
    TextView terapeuta_elemento;
    View headerView;
    Boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = getSharedPreferences("userPref", 0);

        if (sharedPref.getString("logged", null) != null) {
            Log.i("logged", sharedPref.getString("logged", ""));
            Log.i("id", sharedPref.getString("userId", ""));
            Log.i("firstName", sharedPref.getString("firstName", ""));
            Log.i("lastName", sharedPref.getString("lastName", ""));
            Log.i("type", sharedPref.getString("type", ""));

            Intent intent = getIntent();

            switch (sharedPref.getString("type", "")) {
                case "1":
                    // Obtener el id de la lista de terapeutas
                    if (intent.getStringExtra(listadoTerapeutas.EXTRA_MESSAGE) != null) {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("idTerapeuta", intent.getStringExtra(listadoTerapeutas.EXTRA_MESSAGE));
                        editor.commit();

                        idTerapeuta = intent.getStringExtra(listadoTerapeutas.EXTRA_MESSAGE);
                    }
                    else {
                        idTerapeuta = sharedPref.getString("idTerapeuta", "");
                    }
                    isAdmin = true;
                    break;
                case "2":
                    //Obtener el id del terapeuta de las preferencias
                    idTerapeuta = sharedPref.getString("userId", "");
                    break;
            }
        }

        setContentView(R.layout.activity_listado_pacientes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.patients_title));
        getSupportActionBar().setSubtitle(getString(R.string.app_name));

        //Aquí agregamos la flecha de regreso solamente si es usuario tipo administrador
        if (isAdmin) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("agregarPacienteid", idTerapeuta);
                agregarPaciente(idTerapeuta);
            }
        });

        patientsList = new ArrayList<HashMap<String, String>>();

        urlText = "http://app.bluecoreservices.com/webservices/listPatient.php?idTerapeuta=" + idTerapeuta;
        Log.d("urlText", urlText);

        listView = (ListView) findViewById(R.id.pacientes_lista);
        lista = (ListView)findViewById(R.id.pacientes_lista);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // what you want to happen onRefresh goes here
                cargarDatos();
            }
        });

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        cargarDatos();
                                    }
                                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings_logout) {
            //inicializacion del mensaje
            AlertDialog.Builder builder = new AlertDialog.Builder(ListadoPacientes.this);

            //Titulo y Mensaje
            builder.setMessage(R.string.logout_dialog_message)
                    .setTitle(R.string.logout_dialog_title);
            //.setView(inputWrapper);

            //Botones
            builder.setPositiveButton(R.string.logout_dialog_okbutton, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    Log.i(PAGINA_DEBUG, "OK Presionado");
                    sharedPref = getSharedPreferences("userPref", 0);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.clear();
                    editor.commit();

                    finishAffinity();
                }
            });
            builder.setNegativeButton(R.string.logout_dialog_cancelbutton, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                    Log.i(PAGINA_DEBUG, "Cancelar Presionado");
                }
            });

            final AlertDialog dialog = builder.create();
            dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void cargarDatos() {
        ConnectivityManager connex = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo infoNetwork = connex.getActiveNetworkInfo();

        if (infoNetwork != null && infoNetwork.isConnected()) {
            new obtenerDatos().execute(urlText);
            Log.i("JSON", "Corriendo el url");
        } else {
            Log.e("JSON", "No Conectó!!");
        }

    }

    private class obtenerDatos extends AsyncTask<String, Void, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            // params comes from the execute() call: params[0] is the url.
            JSONObject json = null;
            try {
                json = json1.getJsonInfo(urlText);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return json;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);

            if(result != null) {
                procesaJSON(result);
            }

            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.contenidoPacientes), "Lista Actualizada", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    public void procesaJSON(JSONObject result) {
        JSONObject jObj = result;
        Integer listSize = lista.getCount() -1;
        if (listSize > 0){
            patientsList.clear();
            adapter.notifyDataSetChanged();
        }



        //Aqui manejamos el objecto json
        Log.i("JSON", jObj.toString());

        try {
            pacientes = result.getJSONArray("patientsList");
            Log.i("JSON", pacientes.toString());

            for (int i = 0; i < pacientes.length(); i++) {
                JSONObject t = pacientes.getJSONObject(i);

                String fullName = t.getString("firstName") + " " + t.getString("lastName");

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id", t.getString("id"));
                map.put("fullName", fullName);

                patientsList.add(map);
                //terapeuta_nombre
            }

            adapter = new SimpleAdapter(ListadoPacientes.this, patientsList,
                    R.layout.therapeuta_elemento_listado,
                    new String[] { "fullName" }, new int[] {
                    R.id.terapeuta_nombre});

            lista.setAdapter(adapter);
            lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    abrirPrincipal(patientsList.get((position)).get("id"), patientsList.get((position)).get("fullName"));

                }
            });
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void abrirPrincipal(String idPaciente, String NombrePaciente) {
        Intent intent = new Intent(this, MainActivity.class);
        Bundle extras = new Bundle();
            extras.putString("EXTRA_MESSAGE", idPaciente);
            extras.putString("EXTRA_MESSAGE_NAME", NombrePaciente);
        intent.putExtras(extras);
        startActivity(intent);
    }

    public void agregarPaciente(String idTerapeuta) {
        Intent intent = new Intent(this, add_patient.class);
        intent.putExtra(EXTRA_MESSAGE_TERAPEUTA, idTerapeuta);
        startActivity(intent);
    }
}

