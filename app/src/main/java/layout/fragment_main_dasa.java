package layout;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bluecoreservices.anxietymonitor2.JSONParser;
import com.bluecoreservices.anxietymonitor2.ListadoPacientes;
import com.bluecoreservices.anxietymonitor2.MainActivity;
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

public class fragment_main_dasa extends Fragment {

    public final static String PAGINA_DEBUG = "fragment_main_dasa";
    private SwipeRefreshLayout swipeRefreshLayout;
    View view;
    ListView lista;
    LineChart lineChart;
    String idPaciente;
    String dasaURL = "http://app.bluecoreservices.com/webservices/getLineChartGet.php?idPaciente=";
    String dasaURLLista = "http://app.bluecoreservices.com/webservices/ListDAS-AGET.php?idPaciente=";

    JSONParser json1 = new JSONParser();
    JSONParser jsonLista = new JSONParser();

    SimpleAdapter adapter;

    ArrayList<HashMap<String, String>> dasasList;


    public fragment_main_dasa() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dasasList = new ArrayList<HashMap<String, String>>();
        idPaciente = MainActivity.idPaciente;

        dasaURL += idPaciente;
        dasaURLLista += idPaciente;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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
        cargarDatos();
    }

    public void cargarDatos() {
        ConnectivityManager connex = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo infoNetwork = connex.getActiveNetworkInfo();

        if (infoNetwork != null && infoNetwork.isConnected()) {
            new obtenerDatos().execute(dasaURL);
            new obtenerDatosLista().execute(dasaURLLista);
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
                    map.put("idEntrada", t.getString("id"));

                    dasasList.add(map);
                }

                adapter = new SimpleAdapter(getActivity(), dasasList,
                        R.layout.elemento_lista_categorias,
                        new String[] { "fechaEnvio", "total" }, new int[] {
                        R.id.nombre_categoria, R.id.severidad_categoria});

                lista.setAdapter(adapter);
                lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        verDetalleDASA(dasasList.get((position)).get("idEntrada"));
                        Log.i(PAGINA_DEBUG, dasasList.get((position)).get("idEntrada"));

                    }
                });
                adapter.notifyDataSetChanged();



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



    private void verDetalleDASA(String categoId) {

        class LoginAsync  extends AsyncTask<String, Void, JSONObject> {
            private Dialog loadingDialog;
            private final String url = "http://app.bluecoreservices.com/webservices/singleDAS-A.php";

            String charset = "UTF-8";
            HttpURLConnection conn;
            DataOutputStream wr;
            StringBuilder result = new StringBuilder();
            URL urlObj;
            JSONObject jObj = null;
            StringBuilder sbParams;
            String paramsString;

            TextView dialog_dasa_detail_total;
            TextView dialog_dasa_detail_date;

            TextView view_dasa_1;
            TextView view_dasa_2;
            TextView view_dasa_3;
            TextView view_dasa_4;
            TextView view_dasa_5;
            TextView view_dasa_6;
            TextView view_dasa_7;
            TextView view_dasa_8;

            ProgressBar view_dasa_1_seek;
            ProgressBar view_dasa_2_seek;
            ProgressBar view_dasa_3_seek;
            ProgressBar view_dasa_4_seek;
            ProgressBar view_dasa_5_seek;
            ProgressBar view_dasa_6_seek;
            ProgressBar view_dasa_7_seek;
            ProgressBar view_dasa_8_seek;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(getContext(), "Please wait", "Loading...");
            }

            @Override
            protected JSONObject doInBackground(String... params) {

                String dasaId = params[0];

                sbParams = new StringBuilder();

                try {
                    sbParams.append("id").append("=").append(URLEncoder.encode(dasaId, charset));
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
                //inicializacion del mensaje
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                // Get the layout inflater
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogBody = inflater.inflate(R.layout.dialog_dasa_detail_body, null);

                //Titulo y Mensaje
                builder.setTitle(R.string.view_dasa_dialog_title)
                        .setView(dialogBody);
                //.setView(inputWrapper);

                //Botones
                builder.setNegativeButton(R.string.view_dasa_dialog_cancelbutton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        Log.i(PAGINA_DEBUG, "Cancelar Presionado");
                    }
                });

                Log.i(PAGINA_DEBUG, "result: " + result.toString());
                loadingDialog.dismiss();

                JSONArray dasLista = null;


                try {
                    dasLista = result.getJSONArray("DasList");
                    JSONObject dasElemento = dasLista.getJSONObject(0);
                    Log.i(PAGINA_DEBUG, dasLista.toString());

                    dialog_dasa_detail_total = (TextView) dialogBody.findViewById(R.id.dialog_dasa_detail_total);
                    dialog_dasa_detail_date = (TextView) dialogBody.findViewById(R.id.dialog_dasa_detail_date);

                    view_dasa_1 = (TextView) dialogBody.findViewById(R.id.view_dasa_1);
                    view_dasa_2 = (TextView) dialogBody.findViewById(R.id.view_dasa_2);
                    view_dasa_3 = (TextView) dialogBody.findViewById(R.id.view_dasa_3);
                    view_dasa_4 = (TextView) dialogBody.findViewById(R.id.view_dasa_4);
                    view_dasa_5 = (TextView) dialogBody.findViewById(R.id.view_dasa_5);
                    view_dasa_6 = (TextView) dialogBody.findViewById(R.id.view_dasa_6);
                    view_dasa_7 = (TextView) dialogBody.findViewById(R.id.view_dasa_7);
                    view_dasa_8 = (TextView) dialogBody.findViewById(R.id.view_dasa_8);

                    view_dasa_1_seek = (ProgressBar) dialogBody.findViewById(R.id.view_dasa_1_seek);
                    view_dasa_2_seek = (ProgressBar) dialogBody.findViewById(R.id.view_dasa_2_seek);
                    view_dasa_3_seek = (ProgressBar) dialogBody.findViewById(R.id.view_dasa_3_seek);
                    view_dasa_4_seek = (ProgressBar) dialogBody.findViewById(R.id.view_dasa_4_seek);
                    view_dasa_5_seek = (ProgressBar) dialogBody.findViewById(R.id.view_dasa_5_seek);
                    view_dasa_6_seek = (ProgressBar) dialogBody.findViewById(R.id.view_dasa_6_seek);
                    view_dasa_7_seek = (ProgressBar) dialogBody.findViewById(R.id.view_dasa_7_seek);
                    view_dasa_8_seek = (ProgressBar) dialogBody.findViewById(R.id.view_dasa_8_seek);

                    dialog_dasa_detail_total.setText(dasElemento.getString("total"));
                    dialog_dasa_detail_date.setText(dasElemento.getString("fechaEnvio"));

                    view_dasa_1.setText(dasElemento.getString("item1"));
                    view_dasa_2.setText(dasElemento.getString("item2"));
                    view_dasa_3.setText(dasElemento.getString("item3"));
                    view_dasa_4.setText(dasElemento.getString("item4"));
                    view_dasa_5.setText(dasElemento.getString("item5"));
                    view_dasa_6.setText(dasElemento.getString("item6"));
                    view_dasa_7.setText(dasElemento.getString("item7"));
                    view_dasa_8.setText(dasElemento.getString("item8"));

                    view_dasa_1_seek.setProgress((Integer.parseInt(dasElemento.getString("item1"))*10));
                    view_dasa_2_seek.setProgress((Integer.parseInt(dasElemento.getString("item2"))*10));
                    view_dasa_3_seek.setProgress((Integer.parseInt(dasElemento.getString("item3"))*10));
                    view_dasa_4_seek.setProgress((Integer.parseInt(dasElemento.getString("item4"))*10));
                    view_dasa_5_seek.setProgress((Integer.parseInt(dasElemento.getString("item5"))*10));
                    view_dasa_6_seek.setProgress((Integer.parseInt(dasElemento.getString("item6"))*10));
                    view_dasa_7_seek.setProgress((Integer.parseInt(dasElemento.getString("item7"))*10));
                    view_dasa_8_seek.setProgress((Integer.parseInt(dasElemento.getString("item8"))*10));

                    final AlertDialog dialog = builder.create();
                    dialog.show();



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        LoginAsync la = new LoginAsync();
        la.execute(categoId);
    }

}
