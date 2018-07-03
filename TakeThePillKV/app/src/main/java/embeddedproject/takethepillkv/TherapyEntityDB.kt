package embeddedproject.takethepillkv

import android.content.ContentValues
import android.util.Log

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

import database.Str

public class TherapyEntityDB {

    //Definizione variabili
     var mID : Int?
     var mDrug: String?
     var mDateStart : Date?
     var mDateEnd :Date?
     var mNotify : Int   //-1=nessuna notifica, 0=notifica allo stesso momento, >0=min prima
     var mDays : Int    //-2=DataFine, -1=Senza limiti
     var mMon : Boolean
     var mTue : Boolean
     var mWed : Boolean
     var mThu : Boolean
     var mFri : Boolean
     var mSat : Boolean
     var mSun : Boolean
     var mDosaggio: Int
    //istanza di un'oggetto Str, necessario per leggere le stringhe
     val str: Str=Str()


    constructor()
    {
        mID=null
        mDrug=null
        mDateStart=null
        mDateEnd =null
        mNotify=20
        mDays=1
        mMon=false
        mTue=false
        mWed=false
        mThu=false
        mFri=false
        mSat=false
        mSun=false
        mDosaggio=0
    }


    constructor( dataFine: Date?,  nGiorni: Int, minNotifica: Int,
            lun:Boolean, mar:Boolean, mer:Boolean, gio:Boolean, ven:Boolean, sab:Boolean, dom:Boolean,
     dosaggio: Int, nomeFarmaco: String?){

        mID = null

         val c: Calendar = Calendar.getInstance()    // Data di oggi
        mDateStart=  Date(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))


        // Uno tra dataFine e nGiorni deve essere NULL
        this.mDateEnd=dataFine
        this.mDays=nGiorni
        this.mNotify=minNotifica
        this.mMon=lun
        this.mTue=mar
        this.mWed=mer
        this.mThu=gio
        this.mFri=ven
        this.mSat=sab
        this.mSun=dom
        this.mDosaggio=dosaggio
        this.mDrug=nomeFarmaco
    }


    // Metodo per ottere la lista dei giorni della settimana in stringa
     fun  getGiorni() : String{
        var s =""
        var count=0
        if(mMon){
            s="Lun"
            count++
        }
        if(mTue){
            if(count>0) s+=", "
            s+="Mar"
            count++
        }
        if(mWed){
            if(count>0) s+=", "
            s+="Mer"
            count++
        }
        if(mThu){
            if(count>0) s+=", "
            s+="Gio"
            count++
        }
        if(mFri){
            if(count>0) s+=", "
            s+="Ven"
            count++
        }
        if(mSat){
            if(count>0) s+=", "
            s+="Sab"
            count++
        }
        if(mSun){
            if(count>0) s+=", "
            s+="Dom"
            count++
        }
        return s
    }

    //Funzioni get e set
    fun getID(): Int? {
        return mID
    }

    fun getDrug(): String? {
        return mDrug
    }

    fun setDrug(drug: String?) {
        mDrug = drug
    }

    fun setID(ID: Int?) {
        mID = ID
    }

    fun getDateStart(): Date? {
        return mDateStart
    }

    fun setDateStart(dateStart: Date?) {
        mDateStart = dateStart
    }

    fun getDateEnd(): Date? {
        return mDateEnd
    }

    fun setDateEnd(dateEnd: Date?) {
        mDateEnd = dateEnd
    }

    fun getNotify(): Int? {
        return mNotify
    }

    fun setNotify(notify: Int) {
        mNotify = notify
    }

    fun getDays(): Int? {
        return mDays
    }

    fun setDays(days: Int) {
        mDays = days
    }

    fun isMon(): Boolean {
        return mMon
    }

    fun setMon(lun: Boolean) {
        mMon = lun
    }

    fun isTue(): Boolean {
        return mTue
    }

    fun setTue(tue: Boolean) {
        mTue = tue
    }

    fun isWed(): Boolean {
        return mWed
    }

    fun setWed(wed: Boolean) {
        mWed = wed
    }

    fun isThu(): Boolean {
        return mThu
    }

    fun setThu(thu: Boolean) {
        mThu = thu
    }

    fun isFri(): Boolean {
        return mFri
    }

    fun setFri(fri: Boolean) {
        mFri = fri
    }

    fun isSat(): Boolean {
        return mSat
    }

    fun setSat(sat: Boolean) {
        mSat = sat
    }

    fun isSun(): Boolean {
        return mSun
    }

    fun setSun(sun: Boolean) {
        mSun = sun
    }

    fun setDosage(dosaggio: Int) {
        mDosaggio = dosaggio
    }

    fun getDosaggio(): Int {
        return mDosaggio
    }

//Funzione che ritorna tutti i valori
 fun getAllValues() : ContentValues
    {   val current=  ContentValues()
        current.put(str.therapyDrug,mDrug)
        val myFormat=SimpleDateFormat("dd/mm/yyyy")

        if(mDateEnd==null)
            current.put(str.therapyDateEnd,null as? String)
        else
            current.put(str.therapyDateEnd,myFormat.format(mDateEnd))

        current.put(str.therapyDateStart,myFormat.format(mDateStart))
        current.put(str.therapyNotify,mNotify)
        current.put(str.therapyNumberDays,mDays)
        current.put(str.therapyID,mID)
        current.put(str.therapyDosage,mDosaggio)
        current.put(str.therapyMon,checkBool(mMon))
        current.put(str.therapyTue,checkBool(mTue))
        current.put(str.therapyWed,checkBool(mWed))
        current.put(str.therapyThu,checkBool(mThu))
        current.put(str.therapyFri,checkBool(mFri))
        current.put(str.therapySat,checkBool(mSat))
        current.put(str.therapySun,checkBool(mSun))

        return current;
    }


    private fun checkBool( value: Boolean) : Int
    {
        if(value) return 1
        else return 0
    }
}

