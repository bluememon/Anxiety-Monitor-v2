package layout;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.bluecoreservices.anxietymonitor2.JSONParser;
import com.bluecoreservices.anxietymonitor2.ListadoPacientes;
import com.bluecoreservices.anxietymonitor2.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class fragment_main_dasa extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "idPaciente";
    private SwipeRefreshLayout swipeRefreshLayout;
    View view;
    ListView lista;
    LineChart lineChart;
    String idPaciente;
    String dasaURL = "http://app.bluecoreservices.com/webservices/getLineChartGet.php?idPaciente=";
    String dasaURLLista = "http://app.bluecoreservices.com/webservices/ListDAS-AGET.php?idPaciente=";
    /*ArrayList<String> fechas = new ArrayList<String>();
    ArrayList<Entry> valores = new ArrayList<Entry>();*/
    JSONParser json1 = new JSONParser();
    JSONParser jsonLista = new JSONParser();

    SimpleAdapter adapter;

    ArrayList<HashMap<String, String>> dasasList;

    // TODO: Rename and change types of parameters
    private String mParam1;
    //public String idPaciente;


    public fragment_main_dasa() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dasasList = new ArrayList<HashMap<String, String>>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        idPaciente = getActivity().getIntent().getStringExtra(ListadoPacientes.EXTRA_MESSAGE);
        dasaURL += idPaciente;
        dasaURLLista += idPaciente;
        view = inflater.inflate(R.layout.fragment_main_dasa, container, false);
        View headerView = View.inflate(getActivity(), R.layout.dasa_chart, null);
        lineChart = (LineChart) headerView.findViewById(R.id.dasa_chart);
        lista = (ListView) view.findViewById(R.id.dasa_lista);
        lista.addHeaderView(headerView);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_main_dasa_swipe);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // what you want to happen onRefresh goes here
                generateDasaChart();
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
                                        generateDasaChart();
                                    }
                                }
        );

        //View headerView = View.inflate(getActivity(), R.layout.)

        return view;
    }

    public void generateDasaChart() {
        Log.i("fragmento", "generando grafica");
        cargarDatos();
    }

    public void cargarDatos() {
        ConnectivityManager connex = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo infoNetwork = connex.getActiveNetworkInfo();

        if (infoNetwork != null && infoNetwork.isConnected()) {
            Log.i("JSON_DASA", dasaURL);
            new obtenerDatos().execute(dasaURL);
            new obtenerDatosLista().execute(dasaURLLista);
            Log.i("JSON_DASA", "Corriendo el url");
        } else {
            Log.e("JSON_DASA", "No Conectó!!");
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
                json = json1.getJsonInfo(dasaURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return json;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(JSONObject result) {
            procesaJSON(result);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void procesaJSON(JSONObject result) {
        JSONObject jObj = result;

        //Aqui manejamos el objecto json
        Log.i("JSON", jObj.toString());

        ArrayList<String> fechas = new ArrayList<String>();
        ArrayList<Entry> valores = new ArrayList<Entry>();

        try {
            JSONArray listaDatos = result.getJSONArray("lineData");
            Log.i("JSON listaDatos", listaDatos.toString());

            Float f;

            for (int i = 0; i < listaDatos.length(); i++) {
                JSONObject t = listaDatos.getJSONObject(i);
                String tempStr = t.getString("total");

                f = Float.parseFloat(tempStr);
                valores.add(new Entry(f, i));
                fechas.add(t.getString("fechaEnvio"));

            }

                LineDataSet set1 = new LineDataSet(valores, "Severidad");
                set1.setDrawValues(false);

                set1.setColors(ColorTemplate.PASTEL_COLORS);
                set1.disableDashedLine();
                set1.setDrawFilled(true);


                ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
            dataSets.add(set1);


                XAxis xAxis = lineChart.getXAxis();
                xAxis.setDrawLabels(false);


                YAxis leftAxis = lineChart.getAxisLeft();
                leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
                leftAxis.setAxisMaxValue(100f);
                leftAxis.setAxisMinValue(0f);
                leftAxis.setStartAtZero(false);
                lineChart.setHardwareAccelerationEnabled(true);

                YAxis rightAxis = lineChart.getAxisRight();
                rightAxis.setDrawLabels(false);

                LineData data = new LineData(fechas, dataSets);
                lineChart.setData(data);
                lineChart.invalidate();
                lineChart.setPinchZoom(true);
                lineChart.getLegend().setEnabled(false);
                lineChart.setHardwareAccelerationEnabled(true);
                lineChart.animateX(2500, Easing.EasingOption.Linear);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class obtenerDatosLista extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPreExecute() { super.onPreExecute(); }

        @Override
        protected JSONObject doInBackground(String... args) {
            // params comes from the execute() call: params[0] is the url.
            JSONObject json2 = null;
            try {
                json2 = jsonLista.getJsonInfo(dasaURLLista);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return json2;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(JSONObject result) {
            procesaJSONLista(result);
            swipeRefreshLayout.setRefreshing(false);

            Snackbar snackbar = Snackbar
                    .make(getActivity().findViewById(R.id.main_content), "Diario Actualizado", Snackbar.LENGTH_LONG);

            snackbar.show();
        }
    }

    public void procesaJSONLista(JSONObject result) {
        JSONObject jObj = result;

        Integer listSize = lista.getCount() -1;
        if (listSize > 0){
            dasasList.clear();
            adapter.notifyDataSetChanged();
        }

        Log.i("JSON CategoLista", jObj.toString());

        try {
            JSONArray listaCategos = result.getJSONArray("DasList");
            Log.i("Dasalist", listaCategos.toString());

            for (int i = 0; i < listaCategos.length(); i++) {
                JSONObject t = listaCategos.getJSONObject(i);

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("total", t.getString("total"));
                map.put("fechaEnvio", t.getString("fechaEnvio"));

                dasasList.add(map);
            }

            adapter = new SimpleAdapter(getActivity(), dasasList,
                    R.layout.elemento_lista_categorias,
                    new String[] { "fechaEnvio", "total" }, new int[] {
                    R.id.nombre_categoria, R.id.severidad_categoria});

            lista.setAdapter(adapter);
            adapter.notifyDataSetChanged();



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
