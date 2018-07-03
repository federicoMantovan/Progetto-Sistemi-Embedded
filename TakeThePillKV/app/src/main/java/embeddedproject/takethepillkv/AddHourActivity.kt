package embeddedproject.takethepillkv


import android.app.Activity
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.TimePicker

import java.util.ArrayList
import java.util.Calendar

class AddHourActivity : AppCompatActivity() {

    //Definizione variabili
    lateinit var listaOre: ArrayList<IntArray>
    private var mHour: Int = 0
    private var mMinute: Int = 0
    lateinit var customAdapter: CustomAdapterHour


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_hour)

        // BOTTONI TOOLBAR SALVA E ANNULLA
        val tvSave = findViewById(R.id.toolbar_save3) as TextView
        tvSave.setOnClickListener {
            // PASSARE ad AddEtitTherapy la lista delle ore
            val returnIntent = Intent()
            returnIntent.putExtra("result", listaOre) // Passo la lista delle ore ad AddEdictTherapy
            setResult(Activity.RESULT_OK, returnIntent)
            finish()   // Chiude l'activity e riapre la precedente
        }
        val tvAnnulla = findViewById(R.id.toolbar_annulla3) as TextView
        tvAnnulla.setOnClickListener {
            val returnIntent = Intent()
            setResult(Activity.RESULT_CANCELED, returnIntent)
            finish()   // Chiude l'activity e riapre la precedente
        }

        val c = Calendar.getInstance()
        mHour = c.get(Calendar.HOUR_OF_DAY)
        mMinute = c.get(Calendar.MINUTE)

        // LISTA DELLE ORE
        val listView = findViewById(R.id.listHours) as ListView

        //Ricevo la lista delle ore da AddEditTherapyActivity
        listaOre = intent.getSerializableExtra("listaore") as ArrayList<IntArray>
        if (listaOre == null) listaOre = ArrayList()

        customAdapter = CustomAdapterHour(listaOre, this)
        listView.adapter = customAdapter

        //TextView orario
        val tvTime = findViewById(R.id.tvNewHour) as TextView
        var orario = ""
        if (mHour < 10)
            orario = "0$mHour"
        else
            orario = mHour.toString() + ""
        if (mMinute < 10)
            orario += ":0$mMinute"
        else
            orario += ":$mMinute"
        tvTime.text = orario

        // Bottone Seleziona Orario
        val btnTimePicker = findViewById(R.id.btnSelectHour) as Button
        btnTimePicker.setOnClickListener { v ->
            val c = Calendar.getInstance()
            mHour = c.get(Calendar.HOUR_OF_DAY)
            mMinute = c.get(Calendar.MINUTE)

            // Launch Time Picker Dialog
            val timePickerDialog = TimePickerDialog(v.context,
                    TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                        mHour = hourOfDay
                        mMinute = minute
                        var orario = ""
                        if (mHour < 10)
                            orario = "0$mHour"
                        else
                            orario = mHour.toString() + ""
                        if (mMinute < 10)
                            orario += ":0$mMinute"
                        else
                            orario += ":$mMinute"
                        tvTime.text = orario
                    }, mHour, mMinute, true)
            timePickerDialog.show()
        }


        // Bottone Aggiungi Orario
        val btnAddHour = findViewById(R.id.btnAddHour) as Button
        btnAddHour.setOnClickListener {
            var contiene = false
            for (i in listaOre!!.indices) {
                if (listaOre!![i][0] == mHour && listaOre!![i][1] == mMinute) contiene = true
            }
            if (!contiene) listaOre!!.add(intArrayOf(mHour, mMinute))
            customAdapter.notifyDataSetChanged()
        }
    }

    // Funzione richiamata quando si clicca sul bottone X di un elemento della lista
    fun eliminaOrario(i: Int) {
        listaOre!!.removeAt(i)
        customAdapter.notifyDataSetChanged()
    }


}
