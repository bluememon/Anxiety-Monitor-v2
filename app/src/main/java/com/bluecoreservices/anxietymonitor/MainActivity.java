package com.bluecoreservices.anxietymonitor;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import layout.dasa_calendar;
import layout.fragment_main_catego;
import layout.fragment_main_dasa;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.bluecoreservices.anxietymonitor.ID_PACIENTE";
    public final static String PAGINA_DEBUG = "main_activity";
    public static String idPaciente;
    public static String nombrePaciente;
    SharedPreferences sharedPref;
    Boolean isPatient = false;

    //pointer to fab button
    android.support.design.widget.FloatingActionButton fab;
    com.github.clans.fab.FloatingActionButton fabDasa;
    com.github.clans.fab.FloatingActionButton fabCatego;
    RelativeLayout backgroundMenu;


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = getSharedPreferences("userPref", 0);

        if (sharedPref.getString("logged", null) != null) {
            Log.i("Main Activity: logged", sharedPref.getString("logged", ""));
            Log.i("Main Activity: id", sharedPref.getString("userId", ""));
            Log.i("Main Activity:firstName", sharedPref.getString("firstName", ""));
            Log.i("Main Activity: lastName", sharedPref.getString("lastName", ""));
            Log.i("Main Activity: type", sharedPref.getString("type", ""));

            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
            switch (sharedPref.getString("type", "")) {
                case "1":
                        // Obtener el id de la lista de terapeutas
                        if (extras != null){
                            idPaciente = extras.getString("EXTRA_MESSAGE");
                            nombrePaciente = extras.getString("EXTRA_MESSAGE_NAME");

                            Log.i(PAGINA_DEBUG, "idPaciente: " + idPaciente);
                            Log.i(PAGINA_DEBUG, "nombrePaciente: " + nombrePaciente);

                            SharedPreferences.Editor editor= sharedPref.edit();
                            editor.putString("idPaciente", idPaciente);
                            editor.commit();

                        }
                        else {
                            idPaciente = sharedPref.getString("idPaciente", "");
                        }
                    break;
                case "2":
                        //Obtener el id del terapeuta de las preferencias
                        if (extras != null){
                            idPaciente = extras.getString("EXTRA_MESSAGE");
                            nombrePaciente = extras.getString("EXTRA_MESSAGE_NAME");

                            SharedPreferences.Editor editor= sharedPref.edit();
                            editor.putString("idPaciente", idPaciente);
                            editor.commit();
                        }
                        else {
                            idPaciente = sharedPref.getString("idPaciente", "");
                        }
                    break;
                case "3":
                        idPaciente = sharedPref.getString("userId", "");
                        isPatient = true;
                    break;
            }
        }

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (!isPatient) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(nombrePaciente);
            getSupportActionBar().setSubtitle("Anxiety Monitor");
        }
        else {
            //agregar iconno de home
            getSupportActionBar().setIcon(R.mipmap.am_launcher);
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //Aquí configuramos el menu principal
        fab = (android.support.design.widget.FloatingActionButton) findViewById(R.id.fab);
        fabDasa =  (com.github.clans.fab.FloatingActionButton)findViewById(R.id.fabDasa);
        fabCatego =  (com.github.clans.fab.FloatingActionButton)findViewById(R.id.fabCatego);
        backgroundMenu =  (RelativeLayout)findViewById(R.id.backgroundViewMenu);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateMenu();
            }
        });
        backgroundMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateMenu();
            }
        });

        fabDasa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateMenu();
                agregarDASA(idPaciente);
            }
        });

        fabCatego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateMenu();
                agregarCatego(idPaciente);
            }
        });

        if (!sharedPref.getString("type", "").equals("3")) {
            fab.hide();
        }

        if (sharedPref.getString("firstTime", "").equals("true") && sharedPref.getString("type", "").equals("3")){
            new MaterialShowcaseView.Builder(this)
                    .setTarget(fab)
                    .setDismissText(R.string.main_activity_first_time_button)
                    .setContentText(R.string.main_activity_first_time_text)
                    .setDelay(500) // optional but starting animations immediately in onCreate can make them choppy
                            //.singleUse(EXTRA_MESSAGE) // provide a unique ID used to ensure it is only shown once
                    .show();
        }
    }

    public void animateMenu() {
        final RelativeLayout itemDasa = (RelativeLayout)findViewById(R.id.menu_item_dasa);
        final RelativeLayout itemCatego = (RelativeLayout)findViewById(R.id.menu_item_catego);


        final Animation rotateButton = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotatebutton);
        final Animation showElements = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.showmenu);

        final Animation showElementsDasa = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.showmenudasa);
        final Animation hideElementsDasa = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hidemenudasa);


        final Animation rotateButtonBack = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotatebuttonback);
        final Animation hideElements = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hidemenu);




        if (backgroundMenu.getVisibility() == View.INVISIBLE){
            backgroundMenu.startAnimation(showElements);
            itemDasa.startAnimation(showElementsDasa);
            itemCatego.startAnimation(showElementsDasa);
            fab.startAnimation(rotateButton);
        }
        else {
            backgroundMenu.startAnimation(hideElements);
            itemDasa.startAnimation(hideElementsDasa);
            itemCatego.startAnimation(hideElementsDasa);
            fab.startAnimation(rotateButtonBack);
        }

        AnimationListener entrada = new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                backgroundMenu.setVisibility(View.VISIBLE);
                itemDasa.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) { }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        };

        AnimationListener entrada2 = new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                itemCatego.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (sharedPref.getString("firstTime", "").equals("true") && sharedPref.getString("type", "").equals("3")) {
                    //Add the tutorials
                    presentShowcaseView();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        };

        AnimationListener salida = new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                backgroundMenu.setVisibility(View.INVISIBLE);
                itemDasa.setVisibility(View.INVISIBLE);
                itemCatego.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        };

        rotateButton.setAnimationListener(entrada);
        showElements.setAnimationListener(entrada2);
        showElementsDasa.setAnimationListener(entrada);

        rotateButtonBack.setAnimationListener(salida);
        hideElements.setAnimationListener(salida);
        hideElementsDasa.setAnimationListener(salida);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Integer temp = item.getItemId();
        switch (item.getItemId()) {
            case android.R.id.home:
                // This is called when the Home (Up) button is pressed in the action bar.
                // Create a simple intent that starts the hierarchical parent activity and
                // use NavUtils in the Support Package to ensure proper handling of Up.
                Intent upIntent = new Intent(this, MainActivity.class);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is not part of the application's task, so create a new task
                    // with a synthesized back stack.
                    TaskStackBuilder.from(this)
                            // If there are ancestor activities, they should be added here.
                            .addNextIntent(upIntent)
                            .startActivities();
                    finish();
                } else {
                    // This activity is part of the application's task, so simply
                    // navigate up to the hierarchical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
            case R.id.action_settings_logout:

                //inicializacion del mensaje
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

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
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return new fragment_main_catego();
                case 1:
                    return new fragment_main_dasa();
                case 2:
                    return new dasa_calendar();
            }

            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Categorias";
                case 1:
                    return "Diario DASA";
                case 2:
                    return "Calendario DASA";
            }
            return null;
        }
    }

    public void agregarDASA (String idPaciente) {
        Intent intent = new Intent(this, add_dasa.class);
        intent.putExtra(EXTRA_MESSAGE, idPaciente);
        startActivity(intent);
    }

    public void agregarCatego (String idPaciente) {
        Intent intent = new Intent(this, add_catego.class);
        intent.putExtra(EXTRA_MESSAGE, idPaciente);
        startActivity(intent);
    }

    public void onStop() {
        super.onStop();
    }

    private void presentShowcaseView() {

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500);

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this);

        sequence.setConfig(config);


        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(fabCatego)
                        .setDismissOnTouch(true)
                        .setDismissText(R.string.main_activity_first_time_button)
                        .setContentText(R.string.categories_button_first_time_text)
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(fabDasa)
                        .setDismissOnTouch(true)
                        .setContentText(R.string.dasa_button_first_time_text)
                        .build()
        );

        sequence.start();
    }
}
