package embeddedproject.takethepillkv

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import database.DatabaseHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.sql.Time
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)


        //Seleziona la Home come impostazione predefinita
        nav_view.setCheckedItem(R.id.nav_today)
        val fragment = TodayFragment()
        displaySelectedFragment(fragment)


        // GESTIONE DELLE NOTIFICHE:
        // Ogni minuto manda un broadcast che attiva la classe AlarmNotificationReiceiver
        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val myIntent: Intent
        val pendingIntent: PendingIntent
        myIntent = Intent(this@MainActivity, AlarmNotificationReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0)
        manager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 3000, 60000, pendingIntent)


        // GESTIONE DELLE TERAPIE DI DURATA SENZA LIMITI:
        // Lista delle terapie con durata senza limite
        val db = DatabaseHelper(this)
        val listTerapie = db.getAllTherapies() as ArrayList<TherapyEntityDB>
        for (i in listTerapie.indices) {
            // Lista assunzioni da domani in avanti
            val listAssunzioni = db.getAssumptionsByTherapy(listTerapie[i]) as ArrayList<AssumptionEntity>

            // Se ci sono meno di 10 assunzioni ne aggiungo 10 dalla data finale
            if (listAssunzioni.size < 10) {
                // Lista delle ore della terapia
                val listaOre = ArrayList<IntArray>()
                val list = db.getTherapyHour(listTerapie[i])    // Operazione database Lista delle ore
                for (j in list!!.indices) {
                    listaOre.add(intArrayOf(list[j].hours, list[j].minutes))
                }

                val ultimoGiorno = listAssunzioni[listAssunzioni.size - 1].data
                val giornoDopo = Calendar.getInstance()
                giornoDopo.set(Calendar.DATE, ultimoGiorno!!.getDate())

                // Per ogni ora si genera una lista di assunzioni
                for (j in listaOre.indices) {
                    val assumptionEntity = AssumptionEntity()
                    val ora = Time(listaOre[j][0], listaOre[j][1], 0)

                    val listAss = assumptionEntity.generateAssumption(listTerapie[i], ora, giornoDopo)

                    // Inserimento di ogni assunzione nuova
                    for (k in listAss!!.indices) db.insertAssumption(listAss.get(k))
                }
            }
        }
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.activity_main_drawer, menu)
        return true
    }*/


    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        //Gestione dei clicks sugli elementi di navigazione
        val id = item.itemId

        var fragment: Fragment? = null

        if (id == R.id.nav_today) {
            supportActionBar!!.setTitle("OGGI")

            fragment = TodayFragment()
            displaySelectedFragment(fragment)

        } else if (id == R.id.nav_therapy) {
            supportActionBar!!.setTitle("TERAPIA")
            fragment = TherapyFragment()
            displaySelectedFragment(fragment)

        } else if (id == R.id.nav_drugs) {
            supportActionBar!!.setTitle("FARMACI")
            fragment = DrugsFragment()
            displaySelectedFragment(fragment)
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


    // Metodo per visualizzare i fragment
    private fun displaySelectedFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame, fragment)
        fragmentTransaction.commit()
    }
}
