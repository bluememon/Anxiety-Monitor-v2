package layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bluecoreservices.anxietymonitor.JSONParser;
import com.bluecoreservices.anxietymonitor.MainActivity;
import com.bluecoreservices.anxietymonitor.R;
import com.bluecoreservices.anxietymonitor.anxietyUtils;
import com.github.lzyzsd.circleprogress.ArcProgress;
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
import java.util.Timer;
import java.util.TimerTask;

public class fragment_main_catego extends Fragment {
    public final static String PAGINA_DEBUG = "fragment_main_catego";
    private SwipeRefreshLayout swipeRefreshLayout;
    View view;
    PieChart pieChart;
    ListView lista;
    public static String idPaciente;
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
        idPaciente = MainActivity.idPaciente;

        dasaURL += idPaciente;
        dasaURLLista += idPaciente;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_main_catego, container, false);
        pieChart = (PieChart) view.findViewById(R.id.chartCatego);
        lista = (ListView) view.findViewById(R.id.categos_lista);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_main_catego_swipe);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
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
        cargarDatos();
    }

    public void cargarDatos() {
        ConnectivityManager connex = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo infoNetwork = connex.getActiveNetworkInfo();

        if (infoNetwork != null && infoNetwork.isConnected()) {
            new obtenerDatosGrafica().execute(dasaURL);
            new obtenerDatosLista().execute(dasaURLLista);
        } else {
            Log.e(PAGINA_DEBUG, "No Conectó!!");
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

        @Override
        protected void onPostExecute(JSONObject result) {
            procesaJSONLista(result);
            swipeRefreshLayout.setRefreshing(false);

            Snackbar snackbar = Snackbar
                    .make(getActivity().findViewById(R.id.main_content), "Categorias Actualizadas", Snackbar.LENGTH_LONG);

            snackbar.show();
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
                lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        verDetalleCatego(categosList.get((position)).get("moodId"));

                    }
                });
                adapter.notifyDataSetChanged();


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void verDetalleCatego(String categoId) {

        class LoginAsync  extends AsyncTask<String, Void, JSONObject> {
            private Dialog loadingDialog;
            private final String url = "http://app.bluecoreservices.com/webservices/getSingleCatego.php";

            String charset = "UTF-8";
            HttpURLConnection conn;
            DataOutputStream wr;
            StringBuilder result = new StringBuilder();
            URL urlObj;
            JSONObject jObj = null;
            StringBuilder sbParams;
            String paramsString;

            TextView categoDate;
            TextView categoName;
            TextView categoSeverity;
            TextView categoDetails;

            ArcProgress severityBar;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(getContext(), "Please wait", "Loading...");
            }

            @Override
            protected JSONObject doInBackground(String... params) {

                String moodId = params[0];

                sbParams = new StringBuilder();

                try {
                    sbParams.append("idCatego").append("=").append(URLEncoder.encode(moodId, charset));
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
                final View dialogBody = inflater.inflate(R.layout.dialog_catego_detail_body, null);

                //Titulo y Mensaje
                builder.setTitle(R.string.view_catego_dialog_title)
                        .setView(dialogBody);
                //.setView(inputWrapper);

                //Botones
                builder.setNegativeButton(R.string.view_catego_dialog_cancelbutton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        Log.i(PAGINA_DEBUG, "Cancelar Presionado");
                    }
                });

                Log.i(PAGINA_DEBUG, result.toString());
                loadingDialog.dismiss();

                JSONArray categoriaLista = null;
                ArrayList<String> nombres = new ArrayList<String>();
                final ArrayList<String> ids = new ArrayList<String>();

                try {
                    categoriaLista = result.getJSONArray("categoInfo");
                    JSONObject categoriaElemento = categoriaLista.getJSONObject(0);
                    Log.i(PAGINA_DEBUG, categoriaLista.toString());

                    categoName = (TextView) dialogBody.findViewById(R.id.dialog_catego_detail_catName);
                    categoSeverity = (TextView) dialogBody.findViewById(R.id.dialog_catego_detail_catSeverity);
                    categoDetails = (TextView) dialogBody.findViewById(R.id.dialog_catego_detail_catDetails);
                    categoDate = (TextView) dialogBody.findViewById(R.id.dialog_catego_detail_date);
                    severityBar = (ArcProgress) dialogBody.findViewById(R.id.view_catego_seek);

                    final Integer catSeverity = Integer.parseInt(categoriaElemento.getString("severidad")) * 10;

                    categoName.setText(categoriaElemento.getString("categoria"));
                    categoSeverity.setText(catSeverity.toString());
                    if (!categoriaElemento.getString("informacion").equals("")) {
                        categoDetails.setText(categoriaElemento.getString("informacion"));
                    }
                    else {
                        LinearLayout detalles = (LinearLayout)dialogBody.findViewById(R.id.dialog_catego_detail_catDetails_wrapper);
                        detalles.setVisibility(View.INVISIBLE);
                    }

                    anxietyUtils.friendlyDate fDate = new anxietyUtils.friendlyDate(getContext(), categoriaElemento.getString("fecha"));
                    categoDate.setText(fDate.getFriendlyDate());

                    //severityBar.setProgress(catSeverity);
                    severityBar.setBottomText(categoriaElemento.getString("categoria"));

                    final Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        Integer count =  0;
                        @Override
                        public void run() {
                            ((Activity)getContext()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (count <= catSeverity) {
                                        severityBar.setProgress(count);
                                        count++;
                                    }
                                    else {
                                        timer.cancel();
                                    }
                                }
                            });
                        }
                    }, 1000, 70);


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
