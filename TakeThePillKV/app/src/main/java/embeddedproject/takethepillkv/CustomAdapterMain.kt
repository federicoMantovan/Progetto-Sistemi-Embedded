package embeddedproject.takethepillkv

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.ArrayList

class CustomAdapterMain(private val dataSet: ArrayList<AssumptionEntity>, internal var mContext: Context?) : ArrayAdapter<AssumptionEntity>(mContext, R.layout.row_item_assumption, dataSet) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        convertView = inflater.inflate(R.layout.row_item_assumption, null)

        //Definizioni costanti
        val drug = convertView!!.findViewById(R.id.drug) as TextView
        val hour = convertView.findViewById(R.id.hour) as TextView
        val ivStato = convertView.findViewById(R.id.ivStato) as ImageView
        val tvDosaggio = convertView.findViewById(R.id.tvAssumpQuantity) as TextView

        val actual = getItem(position)
        drug.setText(actual!!.nomeFarmaco)

        if (actual!!.stato)
            ivStato.visibility = View.INVISIBLE
        else
            ivStato.visibility = View.VISIBLE

        //Definizione della costante per la formattazione dell'orario
        val formatter = SimpleDateFormat("HH:mm")
        hour.text = formatter.format(actual.ora)

        tvDosaggio.setText(actual.dosaggio.toString() + " " + actual.tipoFarmaco)

        return convertView
    }

}