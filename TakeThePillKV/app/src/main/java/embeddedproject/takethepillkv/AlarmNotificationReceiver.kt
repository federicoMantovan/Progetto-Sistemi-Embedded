package embeddedproject.takethepillkv


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import java.util.ArrayList
import java.util.Calendar

import database.DatabaseHelper

class AlarmNotificationReceiver : BroadcastReceiver() {

    //Definizione variabili
    lateinit var listAssunzioni: ArrayList<AssumptionEntity>
    lateinit var db: DatabaseHelper

    // Metodo viene richiamato ogni minuto
    override fun onReceive(context: Context, intent: Intent) {
        // Assunzioni di OGGI
        db = DatabaseHelper(context)
        listAssunzioni = ArrayList()
        listAssunzioni = db.getAssumptionByDate(Calendar.getInstance().time) as ArrayList<AssumptionEntity>

        // Controllo se c'è un assunzione che ha lo stesso orario dell'orario attuale
        for (i in listAssunzioni.indices) {
            val minNotifica = db.getTherapy(listAssunzioni[i].terapia)!!.getNotify()!! //-1=nessuno, 0=stesso momento, >0=min prima

            // Se l'assunzione non ha notifiche oppure è già stata assunta, passo al prossimo elemento
            if (minNotifica == -1 || listAssunzioni[i].stato) continue

            // Ora attuale + il ritardo della notifica
            val oraCorrente = Calendar.getInstance()
            oraCorrente.add(Calendar.MINUTE, +minNotifica)
            oraCorrente.set(Calendar.SECOND, 0)

            // Ora della assunzione
            val oraAssunzione = Calendar.getInstance()
            oraAssunzione.set(Calendar.HOUR_OF_DAY, listAssunzioni[i].ora.getHours())
            oraAssunzione.set(Calendar.MINUTE, listAssunzioni[i].ora.getMinutes())
            oraAssunzione.set(Calendar.SECOND, 0)

            val oraC = oraCorrente.get(Calendar.HOUR_OF_DAY)
            val minC = oraCorrente.get(Calendar.MINUTE)
            val oraA = oraAssunzione.get(Calendar.HOUR_OF_DAY)
            val minA = oraAssunzione.get(Calendar.MINUTE)

            // Se sono la stessa ora: NOTIFICO
            if (oraC == oraA && minC == minA) {
                val terapia = db.getTherapy(listAssunzioni[i].terapia)
                val farmaco = db.getDrugByName(terapia!!.getDrug())

                createNotificationChannel(context)

                // Quando si clicca sulla notifica viene richiamato il MAIN
                val inte = Intent(context, MainActivity::class.java)
                inte.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                val pendingIntent = PendingIntent.getActivity(context, 0, inte, 0)

                // Costruisco la notifica
                val mBuilder = NotificationCompat.Builder(context, "idCanale")
                        .setSmallIcon(R.drawable.ic_notification_pill)
                        .setContentTitle("Devi prendere " + farmaco!!.getNome())
                        .setContentText(terapia.getDosaggio().toString() + " " + farmaco.getTipo())
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        // Set the intent that will fire when the user taps the notification
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)

                val notificationManager = NotificationManagerCompat.from(context)

                // Lancio la notifica
                notificationManager.notify(1, mBuilder.build())
            }

        }
    }


    // Funzione utilizzata per creare il canale delle notifiche
    private fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "nomeCanale"
            val description = "Descriione canale"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("idCanale", name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
        }
    }
}