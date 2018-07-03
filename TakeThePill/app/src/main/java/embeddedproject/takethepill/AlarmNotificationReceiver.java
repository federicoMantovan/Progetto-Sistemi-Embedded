package embeddedproject.takethepill;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import java.util.ArrayList;
import java.util.Calendar;

import database.DatabaseHelper;

public class AlarmNotificationReceiver extends BroadcastReceiver {

    ArrayList<AssumptionEntity> listAssunzioni;
    DatabaseHelper db;

    // Metodo viene richiamato ogni minuto
    @Override
    public void onReceive(Context context, Intent intent) {
        // Assunzioni di OGGI
        db=new DatabaseHelper(context);
        listAssunzioni = new ArrayList<>();
        listAssunzioni= (ArrayList<AssumptionEntity>) db.getAssumptionByDate(Calendar.getInstance().getTime());

        // Controllo se c'è un assunzione che ha lo stesso orario dell'orario attuale
        for(int i=0; i<listAssunzioni.size();i++){
            int minNotifica=db.getTherapy(listAssunzioni.get(i).getTerapia()).getNotify(); //-1=nessuno, 0=stesso momento, >0=min prima

            // Se l'assunzione non ha notifiche oppure è già stata assunta, passo al prossimo elemento
            if(minNotifica==-1 || listAssunzioni.get(i).getStato())continue;

            // Ora attuale + il ritardo della notifica
            Calendar oraCorrente= Calendar.getInstance();
            oraCorrente.add(Calendar.MINUTE,+minNotifica);
            oraCorrente.set(Calendar.SECOND,0);

            // Ora della assunzione
            Calendar oraAssunzione=Calendar.getInstance();
            oraAssunzione.set(Calendar.HOUR_OF_DAY,listAssunzioni.get(i).getOra().getHours());
            oraAssunzione.set(Calendar.MINUTE,listAssunzioni.get(i).getOra().getMinutes());
            oraAssunzione.set(Calendar.SECOND,0);

            int oraC=oraCorrente.get(Calendar.HOUR_OF_DAY);
            int minC=oraCorrente.get(Calendar.MINUTE);
            int oraA=oraAssunzione.get(Calendar.HOUR_OF_DAY);
            int minA=oraAssunzione.get(Calendar.MINUTE);

            // Se sono la stessa ora: NOTIFICO
            if((oraC==oraA && minC==minA)){
                TherapyEntityDB terapia= db.getTherapy(listAssunzioni.get(i).getTerapia());
                DrugEntity farmaco=db.getDrugByName(terapia.getDrug());

                createNotificationChannel(context);

                // Quando si clicca sulla notifica viene richiamato il MAIN
                Intent inte = new Intent(context, MainActivity.class);
                inte.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, inte, 0);

                // Costruisco la notifica
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "idCanale")
                        .setSmallIcon(R.drawable.ic_notification_pill)
                        .setContentTitle("Devi prendere "+farmaco.getNome())
                        .setContentText(terapia.getDosaggio()+" "+farmaco.getTipo())
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        // Set the intent that will fire when the user taps the notification
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                // Lancio la notifica
                notificationManager.notify(1, mBuilder.build());
            }

        }


    }


    // Funzione utilizzata per creare il canale delle notifiche
    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "nomeCanale";
            String description = "Descriione canale";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("idCanale", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager =  context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}
