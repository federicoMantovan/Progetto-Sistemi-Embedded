package embeddedproject.takethepillkv

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import java.util.ArrayList

class CustomAdapterDrug(private val dataSet: ArrayList<DrugEntity>, internal var mContext: Context?) : ArrayAdapter<DrugEntity>(mContext, R.layout.row_item_drug, dataSet) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        convertView = inflater.inflate(R.layout.row_item_drug, null)

        //Definizione costanti
        val name = convertView!!.findViewById<View>(R.id.drugName) as TextView
        val type = convertView.findViewById<View>(R.id.tvType) as TextView
        val scorte = convertView.findViewById<View>(R.id.tvScorte) as TextView
        val prezzo = convertView.findViewById<View>(R.id.tvPrezzo) as TextView
        val descr = convertView.findViewById<View>(R.id.drugDescr) as TextView

        val actual = getItem(position)

        //Memorizzazione nelle costanti dei valori ritornati dai metodi get
        name.text = actual!!.getNome()
        type.text = "Tipo: " + actual.getTipo()
        scorte.text = "Scorte: " + actual.getScorte()
        prezzo.text = "Prezzo: " + actual.getPrezzo() + "€"

        //Gestione campo descrizione
        if (actual.getDescrizione().equals("") || actual.getDescrizione() == null)
            descr.text = "Nessuna descrizione"
        else
            descr.text = "Descrizione: " + actual.getDescrizione()

        return convertView
    }


}
