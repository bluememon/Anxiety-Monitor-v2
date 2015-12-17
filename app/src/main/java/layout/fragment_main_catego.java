package layout;


import android.content.Context;
import android.graphics.Color;
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
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class fragment_main_catego extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "idPaciente";
    private SwipeRefreshLayout swipeRefreshLayout;
    View view;
    PieChart pieChart;
    ListView lista;
    String idPaciente;
    String dasaURL = "http://app.bluecoreservices.com/webservices/getMoodInfoGet.php?idPaciente=";
    String dasaURLLista = "http://app.bluecoreservices.com/webservices/ListCategoGet.php?idPaciente=";
    JSONParser json1 = new JSONParser();
    JSONParser jsonLista = new JSONParser();

    SimpleAdapter adapter;

    ArrayList<HashMap<String, String>> categosList;



    public fragment_main_catego() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categosList = new ArrayList<HashMap<String, String>>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        idPaciente = getActivity().getIntent().getStringExtra(ListadoPacientes.EXTRA_MESSAGE);
        dasaURL += idPaciente;
        dasaURLLista += idPaciente;
        Log.i("urlcompleto_catego", dasaURL);

        view = inflater.inflate(R.layout.fragment_main_catego, container, false);
        //View headerView = View.inflate(getActivity(), R.layout.catego_chart, null);
        pieChart = (PieChart) view.findViewById(R.id.chartCatego);
        lista = (ListView) view.findViewById(R.id.categos_lista);
        //lista.addHeaderView(headerView, null, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_main_catego_swipe);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // what you want to happen onRefresh goes here
                generateCategoChart();
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
                generateCategoChart();
            }
        }
        );

        return view;
    }

    public void generateCategoChart() {
        Log.i("fragmento", "generando grafica");
        cargarDatos();
    }

    public void cargarDatos() {
        ConnectivityManager connex = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo infoNetwork = connex.getActiveNetworkInfo();

        if (infoNetwork != null && infoNetwork.isConnected()) {
            Log.i("JSON_Catego", dasaURL);
            new obtenerDatosGrafica().execute(dasaURL);
            new obtenerDatosLista().execute(dasaURLLista);
            Log.i("JSON_Catego", "Corriendo el url");
        } else {
            Log.e("JSON_Catego", "No Conectó!!");
        }

    }

    private class obtenerDatosGrafica extends AsyncTask<String, Void, JSONObject> {
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

            Snackbar snackbar = Snackbar
                    .make(getActivity().findViewById(R.id.main_content), "Categorias Actualizadas", Snackbar.LENGTH_LONG);

            snackbar.show();
        }
    }

    public void procesaJSON(JSONObject result) {
        JSONObject jObj = result;
        Log.i("JSON Catego", jObj.toString());

        ArrayList<String> categoriasNombre = new ArrayList<String>();
        ArrayList<Entry> valores = new ArrayList<Entry>();

        try {
            JSONArray listaCategos = result.getJSONArray("lineData");
            Log.i("JSON listaCategos", listaCategos.toString());

            Float f;

            for (int i = 0; i < listaCategos.length(); i++) {

                JSONObject t = listaCategos.getJSONObject(i);
                String tempStr = t.getString("y");

                f = Float.parseFloat(tempStr);
                valores.add(new Entry(f, i));
                categoriasNombre.add(t.getString("name"));
            }

            PieDataSet dataSet = new PieDataSet(valores, "");
            dataSet.setSliceSpace(2f);
            dataSet.setSelectionShift(5f);

            ArrayList<Integer> colors = new ArrayList<Integer>();

            for (int c : ColorTemplate.VORDIPLOM_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.JOYFUL_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.COLORFUL_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.LIBERTY_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.PASTEL_COLORS)
                colors.add(c);

            //colors.add(ColorTemplate.getHoloBlue());

            dataSet.setColors(colors);

            pieChart.setUsePercentValues(true);
            pieChart.setDescription("");
            pieChart.setExtraOffsets(5, 10, 5, 5);
            pieChart.setDragDecelerationFrictionCoef(0.95f);

            pieChart.setDrawHoleEnabled(true);
            pieChart.setHoleColorTransparent(true);

            pieChart.setTransparentCircleColor(Color.WHITE);
            pieChart.setTransparentCircleAlpha(110);

            pieChart.setHoleRadius(48f);
            pieChart.setTransparentCircleRadius(51f);

            pieChart.setDrawCenterText(true);

            pieChart.setRotationAngle(0);
            // enable rotation of the chart by touch
            pieChart.setRotationEnabled(true);
            pieChart.setHighlightPerTapEnabled(true);
            pieChart.setDrawSliceText(false);

            pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

            Legend l = pieChart.getLegend();
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
            l.setXEntrySpace(7f);
            l.setYEntrySpace(0f);
            l.setYOffset(0f);


            PieData data = new PieData(categoriasNombre, dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(11f);

            pieChart.setData(data);

            // undo all highlights
            pieChart.highlightValues(null);

            pieChart.invalidate();

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
                    .make(getActivity().findViewById(R.id.main_content), "Categorias Actualizadas", Snackbar.LENGTH_LONG);

            snackbar.show();
        }
    }

    public void procesaJSONLista(JSONObject result) {
        JSONObject jObj = result;

        Integer listSize = lista.getCount() -1;
        if (listSize > 0){
            categosList.clear();
            adapter.notifyDataSetChanged();
        }
        Log.i("CategoLista", jObj.toString());



        try {
            JSONArray listaCategos = result.getJSONArray("DasList");
            Log.e("Daslist", listaCategos.toString());

            for (int i = 0; i < listaCategos.length(); i++) {
                JSONObject t = listaCategos.getJSONObject(i);

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("severidad", t.getString("severidad"));
                map.put("nombre", t.getString("nombre"));
                map.put("moodId", t.getString("idMood"));

                categosList.add(map);
            }

                adapter = new SimpleAdapter(getActivity(), categosList,
                        R.layout.elemento_lista_categorias,
                        new String[] { "nombre", "severidad" }, new int[] {
                        R.id.nombre_categoria, R.id.severidad_categoria});

                lista.setAdapter(adapter);
                adapter.notifyDataSetChanged();


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
