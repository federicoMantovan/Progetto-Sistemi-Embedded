package embeddedproject.takethepillkv

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import database.DatabaseHelper
import java.util.*

//Definizione costanti
private const val VAL1 = "val1"
private const val VAL2 = "val2"

class TodayFragment : Fragment() {
    //Definizione costanti
    private var val1: String? = null
    private var val2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val1 = it.getString(VAL1)
            val2 = it.getString(VAL2)
        }
    }

    //Definizione variabili
    lateinit var listAssunzioni: ArrayList<AssumptionEntity>
    lateinit var customAdapter: CustomAdapterMain
    lateinit var db: DatabaseHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_today, container, false)

        // BOTTONE AGGIUNGI-TERAPIA
        val fab = view.findViewById(R.id.fab_main) as FloatingActionButton
        fab.setOnClickListener { view ->
            // Richiama AddEditTherapyActivity
            val intent = Intent(view.context, AddEditTherapyActivity::class.java)
            startActivity(intent)
            Log.d("TodayFragment","bottone premuto")
        }

        // LISTA ASSUNZIONI
        val listView = view.findViewById(R.id.listAssumptions) as ListView

        listAssunzioni = ArrayList<AssumptionEntity>()

        db = DatabaseHelper(context)
        listAssunzioni = db.getAssumptionByDate(Calendar.getInstance().time) as ArrayList<AssumptionEntity>


        customAdapter = CustomAdapterMain(listAssunzioni, context)
        listView.adapter = customAdapter

        // QUANDO SI CLICCA SU UN ELEMENTO
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val assunzione = listAssunzioni[position]

            // Messaggio Preso/non preso
            val builder = AlertDialog.Builder(view.context)
            builder.setMessage("Hai assunto " + assunzione.nomeFarmaco + "?")
            builder.setPositiveButton("Si") { dialog, which ->
                // Funzione database Aggiorna stato assunzione
                db.setAssumption(assunzione, true)

                // Aggiornare le scorte del farmaco
                if (!assunzione.stato) {
                    val farmaco = db.getDrugByName(assunzione.nomeFarmaco)
                    farmaco!!.setScorte(farmaco.getScorte() - assunzione.dosaggio)
                    db.updateDrug(farmaco)
                }

                // Ricarico la lista degli elementi
                listAssunzioni.clear()
                listAssunzioni.addAll(db.getAssumptionByDate(Calendar.getInstance().time) as ArrayList<AssumptionEntity>)

                customAdapter.notifyDataSetChanged()
            }
            builder.setNegativeButton("No") { dialog, which ->
                // Funzione database Aggiorna stato assunzione
                db.setAssumption(assunzione, false)

                // Aggiornare le scorte del farmaco
                if (assunzione.stato) {
                    val farmaco = db.getDrugByName(assunzione.nomeFarmaco)
                    farmaco!!.setScorte(farmaco.getScorte() + assunzione.dosaggio)
                    db.updateDrug(farmaco)
                }

                // Ricarico la lista degli elementi
                listAssunzioni.clear()
                listAssunzioni.addAll(db.getAssumptionByDate(Calendar.getInstance().time) as ArrayList<AssumptionEntity>)

                customAdapter.notifyDataSetChanged()
            }
            builder.setCancelable(false)
            builder.show()
        }
        return view
    }


    override fun onResume() {
        super.onResume()
        listAssunzioni.clear()
        listAssunzioni.addAll(db.getAssumptionByDate(Calendar.getInstance().time) as ArrayList<AssumptionEntity>)

        customAdapter.notifyDataSetChanged()
    }


    companion object {
        @JvmStatic
        fun newInst(val1: String, val2: String) =
                TodayFragment().apply {
                    arguments = Bundle().apply {
                        putString(VAL1, val1)
                        putString(VAL2, val2)
                    }
                }
    }
}
