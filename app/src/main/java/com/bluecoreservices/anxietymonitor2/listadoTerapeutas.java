package com.bluecoreservices.anxietymonitor2;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class listadoTerapeutas extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.bluecoreservices.anxietymonitor2.ID_TERAPEUTA";

    private String urlText;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    SimpleAdapter adapter;

    JSONParser json1 = new JSONParser();
    JSONArray terapeutas = null;
    ArrayList<HashMap<String, String>> therapistList = new ArrayList<HashMap<String, String>>();
    ListView lista;
    TextView terapeuta_elemento;
    View headerView;

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_terapeutas);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.therapist_title));
        getSupportActionBar().setSubtitle(getString(R.string.app_name));

        therapistList = new ArrayList<HashMap<String, String>>();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarTerapeuta();
            }
        });

        urlText = "http://app.bluecoreservices.com/webservices/ListTherapist.php";
        //cargarDatos();

        listView = (ListView) findViewById(R.id.terapeutas_lista);
        lista = (ListView)findViewById(R.id.terapeutas_lista);
        headerView = View.inflate(this, R.layout.header_listado_terapeutas, null);
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
            sharedPref = getSharedPreferences("userPref", 0);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.commit();

            this.finishAffinity();
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
        @Override
        protected void onPreExecute() { super.onPreExecute(); }
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

            procesaJSON(result);

            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.contenidoTerapeutas), "Lista Actualizada", Snackbar.LENGTH_LONG);

            snackbar.show();
        }
    }

    public void procesaJSON(JSONObject result) {
        JSONObject jObj = result;
        Integer listSize = lista.getCount() -1;
        if (listSize > 0){
            therapistList.clear();
            adapter.notifyDataSetChanged();
        }



        //Aqui manejamos el objecto json
        Log.i("JSON", jObj.toString());

        try {
            terapeutas = result.getJSONArray("TherapistList");
            Log.i("JSON", terapeutas.toString());

            for (int i = 0; i < terapeutas.length(); i++) {
                JSONObject t = terapeutas.getJSONObject(i);

                //therapistList
                String fullName = t.getString("firstName") + " " + t.getString("lastName");

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id", t.getString("id"));
                map.put("fullName", fullName);

                therapistList.add(map);
                //terapeuta_nombre
            }

            adapter = new SimpleAdapter(listadoTerapeutas.this, therapistList,
                    R.layout.therapeuta_elemento_listado,
                    new String[] { "fullName" }, new int[] {
                    R.id.terapeuta_nombre});



            lista.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    abrirListaPacientes(therapistList.get(position).get("id"));

                }
            });
            swipeRefreshLayout.setRefreshing(false);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void abrirListaPacientes(String idTerapeuta) {
        Intent intent = new Intent(this, ListadoPacientes.class);
        intent.putExtra(EXTRA_MESSAGE, idTerapeuta);
        startActivity(intent);
    }

    public void agregarTerapeuta() {
        Intent intent = new Intent(this, add_therapist.class);
        startActivity(intent);
    }
}
