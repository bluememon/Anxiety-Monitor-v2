package com.bluecoreservices.anxietymonitor2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
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

import layout.fragment_main_catego;
import layout.fragment_main_dasa;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.bluecoreservices.anxietymonitor2.ID_PACIENTE";
    public static String idPaciente;
    SharedPreferences sharedPref;
    Boolean isPatient = false;

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
            switch (sharedPref.getString("type", "")) {
                case "1":
                    // Obtener el id de la lista de terapeutas
                    if (intent.getStringExtra(ListadoPacientes.EXTRA_MESSAGE) != null){
                        SharedPreferences.Editor editor= sharedPref.edit();
                        editor.putString("idPaciente", intent.getStringExtra(ListadoPacientes.EXTRA_MESSAGE));
                        editor.commit();

                        idPaciente = intent.getStringExtra(ListadoPacientes.EXTRA_MESSAGE);
                    }
                    else {
                        idPaciente = sharedPref.getString("idPaciente", "");
                    }
                    break;
                case "2":
                    //Obtener el id del terapeuta de las preferencias
                    if (intent.getStringExtra(ListadoPacientes.EXTRA_MESSAGE) != null){
                        SharedPreferences.Editor editor= sharedPref.edit();
                        editor.putString("idPaciente", intent.getStringExtra(ListadoPacientes.EXTRA_MESSAGE));
                        editor.commit();

                        idPaciente = intent.getStringExtra(ListadoPacientes.EXTRA_MESSAGE);
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
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarDASA(idPaciente);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
            }

            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Categorias";
                case 1:
                    return "Diario DASA";
            }
            return null;
        }
    }

    public void agregarDASA (String idPaciente) {
        Intent intent = new Intent(this, add_dasa.class);
        intent.putExtra(EXTRA_MESSAGE, idPaciente);
        startActivity(intent);
    }

    public void onStop() {
        super.onStop();
    }
}
