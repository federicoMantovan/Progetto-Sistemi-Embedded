package embeddedproject.takethepillkv


import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView

import java.util.ArrayList

class CustomAdapterHour(private val dataSet: ArrayList<IntArray>, internal var mContext: Context) : ArrayAdapter<IntArray>(mContext, R.layout.row_item_hour, dataSet) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        convertView = inflater.inflate(R.layout.row_item_hour, null)

        //Definizione costanti
        val hour = convertView!!.findViewById(R.id.tvSingleHour) as TextView
        val ibDelete = convertView.findViewById(R.id.ibDeleteHour) as ImageButton

        val actual = getItem(position)

        var orario = ""

        //Gestione orario
        if (actual!![0] < 10)
            orario = "0" + actual[0]
        else
            orario = actual[0].toString() + ""
        if (actual[1] < 10)
            orario += ":0" + actual[1]
        else
            orario += ":" + actual[1]
        hour.text = orario

        ibDelete.setOnClickListener {
            // Messaggio "SICURO? SI/NO"
            val builder = android.app.AlertDialog.Builder(context)
            builder.setTitle("Sei sicuro di voler eliminare l'orario?")
            builder.setPositiveButton("Si") { dialog, which -> (context as AddHourActivity).eliminaOrario(position) }
            builder.setNegativeButton("No") { dialog, which ->
                // Niente
            }
            builder.setCancelable(false)
            builder.show()
        }

        return convertView
    }
}
