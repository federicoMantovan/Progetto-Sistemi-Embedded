package embeddedproject.takethepillkv

import android.util.Log

import java.sql.Time
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.List
import java.util.concurrent.TimeUnit

 class AssumptionEntity {

    // VARIABILI
     var data : Date? = null
     lateinit var ora : Time
     var nomeFarmaco: String="farmaco non definito " // non deve mai restare così
     var stato: Boolean = false
     var dosaggio: Int=0
     var tipoFarmaco: String="tipo non definito"  // non deve mai restare così
     var terapia: Int? = null

     // costruttore per assunzioni DI SOLA LETTURA
     constructor(dat:Date?, ora: Time, nomeFarmaco:String,stato:Boolean, dosaggio:Int,tipoFarmaco:String,terapia:Int){
        this.data=dat
        this.ora=ora
        this.nomeFarmaco=nomeFarmaco
        this.stato=stato
        this.dosaggio=dosaggio
        this.tipoFarmaco=tipoFarmaco
        this.terapia=terapia
    }
    //costruttore per database
    constructor( data: Date?, ora: Time, terapia:Int, stato : Boolean)
    {
        this.data=data
        this.ora=ora
        this.terapia=terapia
        this.stato=stato
    }

     // Costruttore generico
     constructor() {}

    public fun generateAssumption(th :TherapyEntityDB?, hour:Time, dataInizio: Calendar?): List<AssumptionEntity>?{

        if(th?.mDays==0){
            Log.d("errore terapia","numero giorni non trovato");
            return null
        }

         val list : List<AssumptionEntity> = ArrayList<AssumptionEntity>() as List<AssumptionEntity>//cast sennò mi dava errore
        var calendar:Calendar= Calendar.getInstance()
        if(dataInizio!=null) calendar = dataInizio;  // necessaria sta riga?
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.HOUR, 0)
        // torno indietro di un giorno per garantire la correttezza del ciclo
        calendar.add(Calendar.DAY_OF_MONTH,-1)


        var count:Int=0
        // Se si ha la data di fine, ricavo i giorni di differenza con oggi
        if(th?.mDays==-2){
            var diff = (th?.mDateEnd as Date).getTime()-calendar.getTime().getTime()  // cast per assicurare che non è null
            var giorni = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
            Log.d("Data Fine",th.mDateEnd.toString())
            Log.d("Data Inizio",calendar.getTime().toString())
            Log.d("n Giorni",giorni.toString())
            count= giorni.toInt()
        } else if(th?.mDays==-1)count=10;// Se senza limiti
        else count= th?.mDays!!

        while(count>0){
            //scorro il calendario un giorno alla volta
            calendar.add(Calendar.DAY_OF_MONTH,1)
            val data: String=calendar.getTime().toString() // occhio al formato..da controllare

            Log.d("data processata",data)

            //1=sunday,..7=saturday
            val day: Int= calendar.get(Calendar.DAY_OF_WEEK)

            Log.d("day found",day.toString())

            //compare day found and check if it's a day therapy,then add assumption
            if(day==1 && th.mSun)
            {
                val current  = AssumptionEntity(calendar.getTime(),hour,(th.mID as Int),false) // cast necessraio from Int? to Int
                list.add(current)
                count--
                continue
            }
            if(day==2 && th.mMon)
            {
                val current  = AssumptionEntity(calendar.getTime(),hour,(th.mID as Int),false) // cast necessraio from Int? to Int
                list.add(current)
                count--
                continue
            }
            if(day==3 && th.mTue)
            {
                val current  = AssumptionEntity(calendar.getTime(),hour,(th.mID as Int),false)
                list.add(current)
                count--
                continue
            }
            if(day==4 && th.mWed)
            {
                val current  = AssumptionEntity(calendar.getTime(),hour,(th.mID as Int),false)
                list.add(current)
                count--
                continue
            }
            if(day==5 && th.mThu)
            {
                val current  = AssumptionEntity(calendar.getTime(),hour,(th.mID as Int),false)
                list.add(current)
                count--
                continue
            }
            if(day==6 && th.mFri)
            {
                val current  = AssumptionEntity(calendar.getTime(),hour,(th.mID as Int),false)
                list.add(current)
                count--
                continue
            }
            if(day==7 && th.mSat)
            {
                val current  = AssumptionEntity(calendar.getTime(),hour,(th.mID as Int),false)
                list.add(current)
                count--
                continue
            }

        }

        return list;
    }


}
