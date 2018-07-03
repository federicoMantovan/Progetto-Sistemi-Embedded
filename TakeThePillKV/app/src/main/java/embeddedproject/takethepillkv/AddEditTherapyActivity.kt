package embeddedproject.takethepillkv

import android.app.Activity
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast

import java.sql.Time
import java.util.Calendar
import java.util.Date
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList

import database.DatabaseHelper

class AddEditTherapyActivity : AppCompatActivity() {

    //Definizione variabili e costanti
    private var terapia: TherapyEntityDB? = null    // Rappresenta la terapia in considerazione
    lateinit var drugList: Array<String?> // Rappresenta la lista dei farmaci
    private var listaOre: ArrayList<IntArray>? = null  // Rappresenta la lista delle ore
    private var giorniSelezionati: BooleanArray? = null    // Usata nella selezione dei giorni
    private val giorni = arrayOf("Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica")
    lateinit var db: DatabaseHelper
    lateinit var tvHours: TextView
    private var day = 0
    private var month = 0
    private var year  = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_therapy)

        val toolbar = findViewById(R.id.toolbar2) as Toolbar
        setSupportActionBar(toolbar)

        // Database
        db = DatabaseHelper(this)

        // Lista dei farmaci
        val l = db.getAllDrugs()
        drugList = arrayOfNulls(l.size)
        for (i in l.indices) drugList[i] = l[i].getNome()

        // TextView ed EditText del layout
        val tvDrugName = findViewById(R.id.tvDrugName) as TextView
        val etQuantity = findViewById(R.id.etDrugQuantity2) as EditText
        val tvDuration = findViewById(R.id.tvDuration) as TextView
        val tvDays = findViewById(R.id.tvDays) as TextView
        val tvNotify = findViewById(R.id.tvNotify) as TextView
        tvHours = findViewById(R.id.tvHour) as TextView

        // Stiamo creando una nuova terapia?
        val nuova = intent.getBooleanExtra("nuova", true)
        val id = intent.getIntExtra("id", -1)

        // Impostare le variabili:
        if (nuova) { // Se è una NUOVA terapia
            terapia = TherapyEntityDB(null, -1, -1, true, false, false, false, false, false, false, 1, null)
            tvDrugName.text = "Seleziona farmaco ..."
            listaOre = ArrayList()
        } else {   // Se è MODIFICA terapia
            terapia = db.getTherapy(id)
            tvDrugName.text = terapia?.getDrug()

            listaOre = ArrayList()
            val list = db.getTherapyHour(terapia)    // Operazione database Lista delle ore
            for (i in list!!.indices) {
                listaOre?.add(intArrayOf(list[i].hours, list[i].minutes))
            }

            if (terapia?.getDays() === -2) Log.d("onCreate: AddEditTher: Data Fine", terapia?.getDateEnd().toString())

        }

        // TextView Quantità
        etQuantity.setText(terapia?.getDosaggio().toString())

        // TextView Durata
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        if (terapia?.getDays() === -1)
            tvDuration.text = "Durata: Senza Limiti"
        else if (terapia?.getDays() === -2)
            tvDuration.text = "Durata: fino al " + formatter.format(terapia?.getDateEnd())
        else
            tvDuration.text = "Durata: per " + terapia?.getDays().toString() + " giorni"

        giorniSelezionati = booleanArrayOf(terapia?.isMon()!!, terapia?.isTue()!!, terapia?.isWed()!!, terapia?.isThu()!!, terapia?.isFri()!!, terapia?.isSat()!!, terapia?.isSun()!!)

        // TextView Giorni
        val s = StringBuilder()
        for (i in giorniSelezionati?.indices!!) {
            if (giorniSelezionati!![i]) {
                if (s.length > 0) s.append(", ")
                s.append(giorni[i])
            }
        }
        if (s.toString().trim { it <= ' ' } == "") {
            tvDays.text = "Giorni: nessuno"
            s.setLength(0)
        } else {
            tvDays.text = "Giorni: $s"
        }

        // TextView Notifica
        if (terapia?.getNotify() !== -1)
            tvNotify.text = "Notifiche: " + terapia?.getNotify().toString() + " min prima"
        else
            tvNotify.text = "Notifiche: nessuna"


        // TextView Ore
        tvHours.text = listaOre.toString()
        var o = "Ora: "
        for (i in listaOre?.indices!!) {
            if (listaOre?.get(i)!![0] < 10)
                o += "0" + listaOre!!.get(i)[0]
            else
                o += listaOre!!.get(i)[0].toString() + ""
            if (listaOre!!.get(i)[1] < 10)
                o += ":0" + listaOre!!.get(i)[1]
            else
                o += ":" + listaOre!!.get(i)[1]
            if (i != listaOre!!.size - 1) o += ", "
        }
        tvHours.text = o


        // BOTTONI TOOLBAR SALVA E ANNULLA
        val tvSave = findViewById(R.id.toolbar_save2) as TextView
        tvSave.setOnClickListener { v ->
            terapia?.setDosage(Integer.valueOf(etQuantity.text.toString()))
            if (nuova) {  // Se si sta creando una nuova Terapia
                if (terapia?.getDrug() == null || listaOre == null || listaOre?.size == 0) {    //Se l'utente non ha inserito il farmaco o un orario
                    val snackbar = Snackbar
                            .make(v, "Devi inserire tutti i dati", Snackbar.LENGTH_LONG)
                    snackbar.show()
                } else {
                    saveAll()
                    finish()   // Chiude l'activity e riapre la precedente
                }
            } else {  // Se si sta modificando una terapia
                db.updateTherapy(terapia)  // Operazione DATABASE modifica terapia di id=...
                db.removeAssumptionByTherapy(terapia)

                salvaOre()
                finish()   // Chiude l'activity e riapre la precedente
            }
        }
        val tvAnnulla = findViewById(R.id.toolbar_annulla2) as TextView
        tvAnnulla.setOnClickListener {
            finish()   // Chiude l'aactivity e riapre la precedente
        }

        // BOTTONE "Nome farmaco"
        val drugName = findViewById(R.id.ibEditDrugName) as ImageButton
        drugName.setOnClickListener {
            val builder = AlertDialog.Builder(this@AddEditTherapyActivity)
            builder.setTitle("Seleziona Farmaco")

            builder.setItems(drugList) { dialogInterface, i ->
                tvDrugName.text = drugList[i]
                terapia?.setDrug(drugList[i])
            }
            builder.setNegativeButton("Annulla") { dialog, which ->
                // Niente
            }

            builder.show()
        }


        // BOTTONE DURATA
        val btnDuration = findViewById(R.id.ibEditDuration) as ImageButton
        btnDuration.setOnClickListener {
            val builder = AlertDialog.Builder(this@AddEditTherapyActivity)

            val durationView = layoutInflater.inflate(R.layout.alert_duration, null)
            builder.setView(durationView)

            val rdbtNoLimits = durationView.findViewById(R.id.noLimits) as RadioButton
            val rdbtUntil = durationView.findViewById(R.id.untilRdBtn) as RadioButton
            val rdbtNumbDays = durationView.findViewById(R.id.number_days_RdBtn) as RadioButton
            val etDaysNumb = durationView.findViewById(R.id.etDaysNumber) as EditText
            val btnUntil = durationView.findViewById(R.id.btnUntil) as Button

            if (terapia?.getDays() === -2) {    // DataFine
                rdbtNoLimits.isChecked = false
                rdbtUntil.isChecked = true
                rdbtNumbDays.isChecked = false
                btnUntil.text = formatter.format(terapia?.getDateEnd())
                etDaysNumb.setText("")
            } else if (terapia?.getDays() === -1) { //Senza Limiti
                rdbtNoLimits.isChecked = true
                rdbtUntil.isChecked = false
                rdbtNumbDays.isChecked = false
                btnUntil.text = "Seleziona"
                etDaysNumb.setText("")
            } else {   // n giorni
                rdbtNoLimits.isChecked = false
                rdbtUntil.isChecked = false
                rdbtNumbDays.isChecked = true
                btnUntil.text = "Seleziona"
                etDaysNumb.setText(terapia?.getDays().toString())
            }

            //Pulsante durata
            btnUntil.setOnClickListener {
                val c = Calendar.getInstance()
                day = c.get(Calendar.DAY_OF_MONTH)
                month = c.get(Calendar.MONTH)
                year = c.get(Calendar.YEAR)

                val datePicker = DatePickerDialog.OnDateSetListener { datePicker, yearSelected, monthSelected, daySelected ->
                    year = yearSelected
                    month = monthSelected + 1
                    day = daySelected

                    btnUntil.text = day.toString() + "/" + month + "/" + year
                }
                DatePickerDialog(this@AddEditTherapyActivity, datePicker, year, month, day).show()
            }

            rdbtNoLimits.setOnClickListener {
                btnUntil.isEnabled = false
                etDaysNumb.isEnabled = false
            }
            rdbtUntil.setOnClickListener {
                btnUntil.isEnabled = true
                etDaysNumb.isEnabled = false
            }
            rdbtNumbDays.setOnClickListener {
                btnUntil.isEnabled = false
                etDaysNumb.isEnabled = true
            }

            builder.setPositiveButton("Ok") { dialogInterface, i ->
                if (rdbtNoLimits.isChecked) {   // Cliccato su senza limiti
                    terapia?.setDays(-1)
                    terapia?.setDateEnd(null)
                    tvDuration.text = "Durata: Senza Limiti"
                } else if (rdbtNumbDays.isChecked) { //Cliccasto su nGiorni
                    terapia?.setDays(Integer.valueOf(etDaysNumb.text.toString()))
                    terapia?.setDateEnd(null)
                    tvDuration.text = "Durata: per " + terapia?.getDays().toString() + " giorni"
                } else { //Cliccato su datafine
                    terapia?.setDays(-2)
                    var date: Date? = null
                    try {
                        date = formatter.parse(btnUntil.text.toString())
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }

                    terapia?.setDateEnd(date)
                    tvDuration.text = "Durata: fino al " + formatter.format(terapia?.getDateEnd())
                }
            }

            builder.setNegativeButton("Annulla") { dialogInterface, i ->
                //Bottone annulla, nessuna azione
            }

            builder.show()
        }


        // BOTTONE GIORNI
        val btnDays = findViewById(R.id.ibEditDays) as ImageButton
        btnDays.setOnClickListener {
            val builder = AlertDialog.Builder(this@AddEditTherapyActivity)
            builder.setTitle("Seleziona i giorni")

            builder.setMultiChoiceItems(giorni, giorniSelezionati) { dialogInterface, itemIndex, checked -> }

            builder.setPositiveButton("Ok") { dialogInterface, which ->
                // giorniSelezionati viene modificato in automatico

                val list = (dialogInterface as AlertDialog).listView

                // make selected item in the comma separated string
                val stringBuilder = StringBuilder()
                for (i in 0 until list.count) {
                    val checked = list.isItemChecked(i)

                    if (checked) {
                        if (stringBuilder.length > 0) stringBuilder.append(", ")
                        stringBuilder.append(list.getItemAtPosition(i))

                    }
                }

                if (stringBuilder.toString().trim { it <= ' ' } == "") {
                    tvDays.text = "Giorni: nessuno"
                    stringBuilder.setLength(0)
                } else {
                    tvDays.text = "Giorni: $stringBuilder"
                }

                terapia?.setMon(giorniSelezionati!![0])
                terapia?.setTue(giorniSelezionati!![1])
                terapia?.setWed(giorniSelezionati!![2])
                terapia?.setThu(giorniSelezionati!![3])
                terapia?.setFri(giorniSelezionati!![4])
                terapia?.setSat(giorniSelezionati!![5])
                terapia?.setSun(giorniSelezionati!![6])
            }

            builder.show()
        }


        // BOTTONE ORA
        val btnHour = findViewById(R.id.ibEditHour) as ImageButton
        btnHour.setOnClickListener { v ->
            val intent = Intent(v.context, AddHourActivity::class.java)
            intent.putExtra("listaore", listaOre)
            startActivityForResult(intent, 1)
        }


        // BOTTONE NOTIFICHE
        val btnNotify = findViewById(R.id.ibEditNotify) as ImageButton
        btnNotify.setOnClickListener {
            val builder = AlertDialog.Builder(this@AddEditTherapyActivity)

            val notifyView = layoutInflater.inflate(R.layout.alert_notify, null)
            builder.setView(notifyView)

            val rbNotNotify = notifyView.findViewById(R.id.notNotify) as RadioButton
            val rbNotify = notifyView.findViewById(R.id.minBefore) as RadioButton
            val etNotify = notifyView.findViewById(R.id.etNotify) as EditText

            if (terapia?.getNotify() === -1) {
                rbNotNotify.isChecked = true
                rbNotify.isChecked = false
                etNotify.setText("")
            } else {
                rbNotNotify.isChecked = false
                rbNotify.isChecked = true
                etNotify.setText(terapia?.getNotify().toString())
            }

            builder.setPositiveButton("Ok") { dialogInterface, i ->
                //Fai qualcosa quando premi il pulsante "ok"
                if (rbNotify.isChecked) {
                    if (etNotify.text.toString() == "")
                        terapia?.setNotify(0)
                    else
                        terapia?.setNotify(Integer.valueOf(etNotify.text.toString()))
                    tvNotify.text = "Notifiche: " + terapia?.getNotify().toString() + " min prima"
                } else {
                    terapia?.setNotify(-1)
                    tvNotify.text = "Notifiche: nessuna"
                }
            }

            builder.setNegativeButton("Annulla") { dialogInterface, i ->
                //Nessuna azione quando si preme il pulsante "annulla"
            }

            builder.show()
        }


        // BOTTONE ELIMINA
        val btnElimina = findViewById(R.id.btnDeleteTherapy) as Button
        if (nuova) btnElimina.visibility = View.GONE  //Se si sta inserendo una nuova terapia il bottone non deve essere visibile
        btnElimina.setOnClickListener { v ->
            // Messaggio "SICURO? SI/NO"
            val builder = android.app.AlertDialog.Builder(v.context)
            builder.setTitle("Sei sicuro di voler eliminare la Terapia?")
            builder.setPositiveButton("Si") { dialog, which ->
                db.removeTherapyBYId(terapia?.getID())
                // Le assunzioni si eliminano da sole in cascata dalle terapie
                finish()
            }
            builder.setNegativeButton("No") { dialog, which ->
                // Niente
            }
            builder.setCancelable(false)
            builder.show()
        }
    }


    // QUANDO SI RITORNA DA AddHourActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {

                if (terapia?.getDays() === -2) Log.d("onActivityResult(): Data Fine", terapia?.getDateEnd().toString())

                listaOre = data.getSerializableExtra("result") as ArrayList<IntArray>

                // Visualizzo le ore sul tvHour
                tvHours.text = listaOre.toString()
                var o = "Ora: "
                for (i in listaOre?.indices!!) {
                    if (listaOre!!.get(i)[0] < 10)
                        o += "0" + listaOre!!.get(i)[0]
                    else
                        o += listaOre!!.get(i)[0].toString() + ""
                    if (listaOre!!.get(i)[1] < 10)
                        o += ":0" + listaOre!!.get(i)[1]
                    else
                        o += ":" + listaOre!!.get(i)[1]
                    if (i != listaOre!!.size - 1) o += ", "
                }
                tvHours.text = o
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    // QUANDO SI SALVA UNA NUOVA TERAPIA
    private fun saveAll() {
        // Salva la terapia
        db.insertTherapy(terapia)

        // Recupero L'ID dell'ultima terapia inserita
        var listaTerapie = ArrayList<TherapyEntityDB>()
        listaTerapie = db.getAllTherapies() as ArrayList<TherapyEntityDB>
        val ID = listaTerapie[listaTerapie.size - 1].getID()

        terapia?.setID(ID)
        // Salva le ore (Assunzioni)
        salvaOre()

    }

    // SALVARE LE ASSUNZIONI
    private fun salvaOre() {
        // Per ogni ora si genera una lista di assunzioni
        Log.d("salvaOre()", "salvataggio assunzioni")
        if (terapia?.getDays() === -2) Log.d("salvaOre(): Data Fine", terapia?.getDateEnd().toString())

        for (i in listaOre?.indices!!) {
            val assumptionEntity = AssumptionEntity()
            val ora = Time(listaOre!!.get(i)[0], listaOre!!.get(i)[1], 0)

            val listAssunzioni = assumptionEntity.generateAssumption(terapia, ora, null)


            for (j in listAssunzioni!!.indices) db.insertAssumption(listAssunzioni!!.get(j))

        }
    }

}