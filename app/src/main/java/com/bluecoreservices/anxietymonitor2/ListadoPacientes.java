package com.bluecoreservices.anxietymonitor2;

        import android.app.ProgressDialog;
        import android.content.Context;
        import android.content.Intent;
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
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ListView;
        import android.widget.SimpleAdapter;
        import android.widget.TextView;

        import com.google.android.gms.appindexing.Action;
        import com.google.android.gms.appindexing.AppIndex;
        import com.google.android.gms.common.api.GoogleApiClient;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.HashMap;

public class ListadoPacientes extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.bluecoreservices.anxietymonitor2.ID_PACIENTE";
    public final static String EXTRA_MESSAGE_TERAPEUTA = "com.bluecoreservices.anxietymonitor2.ID_TERAPEUTA";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_pacientes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Anxiety Monitor");
        toolbar.setSubtitle("Listado Pacientes");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("agregarPacienteid", idTerapeuta);
                agregarPaciente(idTerapeuta);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        idTerapeuta = intent.getStringExtra(listadoTerapeutas.EXTRA_MESSAGE);

        Log.d("intent", idTerapeuta);

        patientsList = new ArrayList<HashMap<String, String>>();

        urlText = "http://app.bluecoreservices.com/webservices/listPatient.php?idTerapeuta=" + idTerapeuta;
        Log.d("urlText", urlText);

        listView = (ListView) findViewById(R.id.pacientes_lista);
        lista = (ListView)findViewById(R.id.pacientes_lista);
        headerView = View.inflate(this, R.layout.header_listado_pacientes, null);
        lista.addHeaderView(headerView, null, false);

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

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
                    position -= lista.getHeaderViewsCount();

                    abrirPrincipal(patientsList.get((position)).get("id"));

                }
            });
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void abrirPrincipal(String idPaciente) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_MESSAGE, idPaciente);
        startActivity(intent);
    }

    public void agregarPaciente(String idTerapeuta) {
        Intent intent = new Intent(this, add_patient.class);
        intent.putExtra(EXTRA_MESSAGE_TERAPEUTA, idTerapeuta);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "listadoTerapeutas Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.bluecoreservices.anxietymonitor2/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "listadoTerapeutas Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.bluecoreservices.anxietymonitor2/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}

