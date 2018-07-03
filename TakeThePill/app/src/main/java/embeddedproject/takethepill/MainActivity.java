package embeddedproject.takethepill;

import android.app.AlarmManager;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.MenuItem;


import java.util.Date;
import java.sql.Time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import database.DatabaseHelper;


import android.os.SystemClock;


@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Select Home by default
        navigationView.setCheckedItem(R.id.nav_today);
        Fragment fragment = new TodayFragment();
        displaySelectedFragment(fragment);


        // GESTIONE DELLE NOTIFICHE:
        // Ogni minuto manda un broadcast che attiva la classe AlarmNotificationReiceiver
        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent myIntent;
        PendingIntent pendingIntent;
        myIntent = new Intent(MainActivity.this,AlarmNotificationReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this,0,myIntent,0);
        manager.setRepeating(AlarmManager.RTC_WAKEUP,SystemClock.elapsedRealtime()+3000,60000,pendingIntent);


        // GESTIONE DELLE TERAPIE DI DURATA SENZA LIMITI:
        // Lista delle terapie con durata senza limite
        DatabaseHelper db= new DatabaseHelper(this);
        ArrayList<TherapyEntityDB> listTerapie = (ArrayList<TherapyEntityDB>)db.getAllTherapies();
        for(int i=0; i<listTerapie.size();i++){
            // Lista assunzioni da domani in avanti
            ArrayList<AssumptionEntity> listAssunzioni = (ArrayList<AssumptionEntity>) db.getAssumptionsByTherapy(listTerapie.get(i));

            // Se ci sono meno di 10 assunzioni ne aggiungo 10 dalla data finale
            if(listAssunzioni.size()<10){
                // Lista delle ore della terapia
                ArrayList<int[]> listaOre=new ArrayList<int[]>();
                List<Time> list =db.getTherapyHour(listTerapie.get(i));    // Operazione database Lista delle ore
                for (int j=0; j<list.size();j++){
                    listaOre.add(new int[]{list.get(j).getHours(),list.get(j).getMinutes()});
                }

                Date ultimoGiorno=listAssunzioni.get(listAssunzioni.size()-1).getData();
                Calendar giornoDopo=Calendar.getInstance();
                giornoDopo.set(Calendar.DATE,ultimoGiorno.getDate());

                // Per ogni ora si genera una lista di assunzioni
                for(int j=0; j<listaOre.size();j++){
                    AssumptionEntity assumptionEntity=new AssumptionEntity();
                    Time ora=new Time(listaOre.get(j)[0],listaOre.get(j)[1],0);

                    List<AssumptionEntity> listAss = assumptionEntity.generateAssumption(listTerapie.get(i),ora,giornoDopo);

                    // Inserimento di ogni assunzione nuova
                    for(int k=0;k<listAss.size();k++) db.insertAssumption(listAss.get(k));
                }

            }
        }




    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        @SuppressWarnings("UnusedAssignment") Fragment fragment = null;

        if (id == R.id.nav_today) {
            getSupportActionBar().setTitle("OGGI");

            fragment=new TodayFragment();
            displaySelectedFragment(fragment);

        } else if (id == R.id.nav_therapy) {
            getSupportActionBar().setTitle("TERAPIA");
            fragment=new TherapyFragment();
            displaySelectedFragment(fragment);

        } else if (id == R.id.nav_drugs) {
            getSupportActionBar().setTitle("FARMACI");
            fragment=new DrugsFragment();
            displaySelectedFragment(fragment);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    // Metodo per visualizzare i fragment
    private void displaySelectedFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }


}
