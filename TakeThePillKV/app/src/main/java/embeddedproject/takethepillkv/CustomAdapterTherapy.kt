package embeddedproject.takethepillkv

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

import java.sql.Time
import java.util.ArrayList

import database.DatabaseHelper

class CustomAdapterTherapy(private val dataSet: ArrayList<TherapyEntityDB>, internal var mContext: Context?) : ArrayAdapter<TherapyEntityDB>(mContext, R.layout.row_item_therapy, dataSet) {

    //Definizione variabili e costanti
    private var giorniSelezionati: BooleanArray? = null    // Usata nella selezione dei giorni
    private val giorni = arrayOf("Lun", "Mar", "Mer", "Gio", "Ven", "Sab", "Dom")

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        convertView = inflater.inflate(R.layout.row_item_therapy, null)

        //Definizioni costanti
        val drug = convertView!!.findViewById(R.id.drugName) as TextView
        val tvDays = convertView.findViewById(R.id.weekDays) as TextView
        val hours = convertView.findViewById(R.id.hoursTV) as TextView
        val ivNotifica = convertView.findViewById(R.id.ivNotification) as ImageView

        val terapia = getItem(position)

        drug.text = terapia!!.getDrug()

        giorniSelezionati = booleanArrayOf(terapia.isMon(), terapia.isTue(), terapia.isWed(), terapia.isThu(), terapia.isFri(), terapia.isSat(), terapia.isSun())
        // TextView Giorni
        val s = StringBuilder()

        //Gestione dei giorni selezionati
        for (i in giorniSelezionati!!.indices) {
            if (giorniSelezionati!![i]) {
                if (s.length > 0) s.append(", ")
                s.append(giorni[i])
            }
        }
        if (s.toString().trim { it <= ' ' } == "") {
            tvDays.text = ""
            s.setLength(0)
        } else {
            tvDays.text = s
        }

        // TextView Ore
        val db = DatabaseHelper(convertView.context)
        val list = db.getTherapyHour(terapia)    // Operazione database Lista delle ore
        var h = ""
        for (i in list!!.indices) {
            if (list[i].hours < 10) h += "0"
            h += list[i].hours.toString() + ":"
            if (list[i].minutes < 10) h += "0"
            h += list[i].minutes
            if (i != list.size - 1) h += ", "
        }
        hours.text = h

        if (terapia.getNotify() === -1)
            ivNotifica.setImageDrawable(convertView.resources.getDrawable(R.drawable.ic_notifications_none_black_24dp))
        else
            ivNotifica.setImageDrawable(convertView.resources.getDrawable(R.drawable.ic_notifications_black_24dp))

        return convertView
    }

}