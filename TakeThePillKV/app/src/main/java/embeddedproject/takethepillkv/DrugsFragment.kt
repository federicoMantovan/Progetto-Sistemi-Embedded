package embeddedproject.takethepillkv

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
import database.DatabaseHelper
import java.util.ArrayList

//Definizione costanti
private const val VAL1 = "val1"
private const val VAL2 = "val2"


class DrugsFragment : Fragment() {

    //Definizioni variabili
    private var val1: String? = null
    private var val2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val1 = it.getString(VAL1)
            val2 = it.getString(VAL2)
        }
    }

    //Definizioni variabili
    lateinit var listaFarmaci: ArrayList<DrugEntity>
    lateinit var customAdapter: CustomAdapterDrug
    lateinit var listView: ListView
    lateinit var db: DatabaseHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_drugs, container, false)

        // LISTA DEI FARMACI
        listView = view.findViewById(R.id.listDrugs) as ListView

        // Operazione DATABASE: lista farmaci (nome,
        listaFarmaci = ArrayList()
        db = DatabaseHelper(context)
        listaFarmaci = db.getAllDrugs() as ArrayList<DrugEntity>

        customAdapter = CustomAdapterDrug(listaFarmaci, context)
        listView.adapter = customAdapter

        // BOTTONE AGGIUNGI-FARMACO
        val fab = view.findViewById(R.id.fab_drug) as FloatingActionButton
        fab.setOnClickListener { view ->
            // Richiama AddEditDrugActivity
            val intent = Intent(view.context, AddEditDrugActivity::class.java)
            intent.putExtra("nuovo", true)
            startActivity(intent)
            Log.d("DrugsFragment","pulsante cliccato")
        }

        // QUANDO SI CLICCA SU UN ELEMENTO
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val farmaco = listaFarmaci[position]

            val nomeFarmaco = farmaco.getNome()

            // Richiama AddEditDrugActivity
            val intent = Intent(view.context, AddEditDrugActivity::class.java)
            intent.putExtra("nuovo", false)
            intent.putExtra("name", nomeFarmaco)
            startActivity(intent)
            Log.d("DrugsFragment","elemento lista cliccato")
        }
        return view
    }

    // Quando ritorna da AddEtidDrugActivity bisogna aggiornare la lista
    override fun onResume() {
        super.onResume()
        listaFarmaci.clear()
        listaFarmaci.addAll(db.getAllDrugs() as ArrayList<DrugEntity>)
        customAdapter.notifyDataSetChanged()
    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        @JvmStatic
        fun newInst(val1: String, val2: String) =
                DrugsFragment().apply {
                    arguments = Bundle().apply {
                        putString(VAL1, val1)
                        putString(VAL2, val2)
                    }
                }
    }
}
