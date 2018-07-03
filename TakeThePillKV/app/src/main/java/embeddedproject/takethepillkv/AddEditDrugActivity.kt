package embeddedproject.takethepillkv

import android.app.AlertDialog
import android.content.DialogInterface
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView

import database.DatabaseHelper
import kotlinx.android.synthetic.main.app_bar_main.*

public class AddEditDrugActivity : AppCompatActivity() {

    //Definizione variabili
    private var elemSelez: Int = 0
    private var prev:Int = 0 //Gestiscono la selezione del tipo di farmaco

    private lateinit var tipiFarmaci: Array<String?> // Lista dei tipi di farmaci
    private var drug: DrugEntity? = null    // Rappresenta il farmaco in considerazione

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_drug)
        setSupportActionBar(toolbar)

        val db = DatabaseHelper(this)
        tipiFarmaci = db.getTypeList()

        // TextView e EditText del layout:
        val etNome = findViewById(R.id.etDrugName) as EditText
        val tvTipo = findViewById(R.id.tvDrugType) as TextView
        val etDescr = findViewById(R.id.etDescription) as EditText
        val etPrezzo = findViewById(R.id.etDrugPrice) as EditText
        val etScorte = findViewById(R.id.etDrugQuantity) as EditText

        val nuovo = intent.getBooleanExtra("nuovo", true)
        if (nuovo) {  // Se stiamo creando un nuovo farmaco
            drug = DrugEntity("", "", "Applicazione/i", 5.0, 20)
            prev = 0
            elemSelez = prev   // elemento selezionato nella lista TIPO farmaco
            tvTipo.text = "Tipo: seleziona..."
        } else { // Se stiamo modificando un farmaco
            drug = db.getDrugByName(intent.getStringExtra("name"))
            for (i in tipiFarmaci.indices) {
                if (tipiFarmaci[i] == drug?.getTipo()) {
                    prev = i
                    elemSelez = prev
                }   // elemento selezionato nella lista TIPO farmaco
            }
            tvTipo.text = "Tipo: " + tipiFarmaci[elemSelez]
        }


        etNome.setText(drug?.getNome())
        etDescr.setText(drug?.getDescrizione())
        etPrezzo.setText(drug?.getPrezzo().toString())
        etScorte.setText(drug?.getScorte().toString())


        // BOTTONI TOOLBAR SALVA E ANNULLA
        val tvSave = findViewById(R.id.toolbar_save) as TextView
        tvSave.setOnClickListener { v ->
            drug?.setDescrizione(etDescr.text.toString())
            drug?.setPrezzo(java.lang.Double.parseDouble(etPrezzo.text.toString()))
            drug?.setScorte(Integer.parseInt(etScorte.text.toString()))
            drug?.setTipo(drug?.getTipo().toString())
            if (nuovo) {  // Se stiamo creando un nuovo farmaco
                // OPERAZIONE DATABASE aggiungi farmaco
                drug?.setNome(etNome.text.toString())
                if (drug?.getNome().equals("")) {  //se il nome è vuoto
                    val snackbar = Snackbar
                            .make(v, "Devi inserire un nome!", Snackbar.LENGTH_LONG)
                    snackbar.show()
                } else {
                    db.insertDrug(drug)
                    finish()   // Chiude l'activity e riapre la precedente
                }
            } else {  // Se stiamo modificando un farmaco
                db.updateDrug(drug)    // OPERAZIONE DATABASE modifica farmaco
                finish()   // Chiude l'activity e riapre la precedente
            }
        }

        val tvAnnulla = findViewById(R.id.toolbar_annulla) as TextView
        tvAnnulla.setOnClickListener {
            finish()   // Chiude l'activity e riapre la precedente
        }

        // Bottone seleziona Tipo di farmaco
        val btnTipo = findViewById(R.id.ibEditType) as ImageButton
        btnTipo.setOnClickListener { v ->
            //private String chooseItem;
            val builder = AlertDialog.Builder(v.context)
            builder.setTitle("Tipo di Farmaco")

            builder.setSingleChoiceItems(tipiFarmaci, elemSelez) { dialogInterface, itemIndex ->
                elemSelez = itemIndex
                drug?.setTipo(tipiFarmaci[elemSelez])
            }

            builder.setPositiveButton("Ok") { dialogInterface, i ->
                prev = elemSelez
                drug?.setTipo(tipiFarmaci[elemSelez])
                tvTipo.text = "Tipo: " + drug?.getTipo()
            }

            builder.setNegativeButton("Annulla") { dialog, which ->
                // Niente
                elemSelez = prev
                drug?.setTipo(tipiFarmaci[elemSelez])
                tvTipo.text = "Tipo: " + drug?.getTipo()
            }

            builder.show()
        }


        // BOTTONE ELIMINA
        val btnElimina = findViewById(R.id.btnDeleteDrug) as Button
        if (nuovo) btnElimina.visibility = View.GONE  //Se si sta inserendo un nuovo farmaco il bottone non deve essere visibile
        btnElimina.setOnClickListener { v ->
            // Messaggio "SICURO? SI/NO"
            val builder = AlertDialog.Builder(v.context)
            builder.setMessage("Sei sicuro di voler eliminare il farmaco?")
            builder.setPositiveButton("Si") { dialog, which ->
                // Operazione DATABASE CHE CANCELLA IL FARMACO
                db.removeDrugBYName(drug?.getNome())
                db.removeTherapyByDrug(drug?.getNome())
                finish()
            }
            builder.setNegativeButton("No") { dialog, which ->
                // Niente
            }
            builder.setCancelable(false)
            builder.show()
        }
    }
}
