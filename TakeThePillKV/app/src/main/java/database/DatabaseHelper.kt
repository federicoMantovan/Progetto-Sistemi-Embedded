package database


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.sql.Time
import java.util.Date
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList

import embeddedproject.takethepillkv.AssumptionEntity
import embeddedproject.takethepillkv.DrugEntity
import embeddedproject.takethepillkv.TherapyEntityDB

import database.Str


class DatabaseHelper:SQLiteOpenHelper{

    constructor(context: Context?):super(context, "PillDb", null, 1){
        Log.d("costruttore db", "ok")
        val db = writableDatabase
    }


    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TYPE_TABLE)
        db?.execSQL(CREATE_DRUG_TABLE)
        db?.execSQL(CREATE_THERAPY_TABLE)
        db?.execSQL(CREATE_ASSUMPTION_TABLE)
        setTypeList(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //delete table
        db?.execSQL("DROP TABLE IF EXISTS " + therapyTable)
        db?.execSQL("DROP TABLE IF EXISTS " + assumptionTable)
        db?.execSQL("DROP TABLE IF EXISTS " + drugTable)
        db?.execSQL("DROP TABLE IF EXISTS " + typeTable)

        // Create tables again
        onCreate(db)

    }

/////////////////////////////////////////////////////////////////
////////////////////////////// TERAPIE /////////////////////////
/////////////////////////////////////////////////////////////////
    fun insertTherapy(terapia: TherapyEntityDB?): Long {
        //  write a new raw on database
        val db = writableDatabase
        val myFormat = SimpleDateFormat("dd/MM/yyyy")

        if (getTherapy(terapia?.getID()) != null) {
            Log.d("insertTherapy()", "tentato ma fallito")
            return -1
        }

        val values = ContentValues()
        values.put(therapyDrug, terapia?.getDrug())
        values.put(therapyDateStart, myFormat.format(terapia?.getDateStart()))

        if (terapia?.getDateEnd() == null)
            values.put(therapyDateEnd, null as String?)
        else
            values.put(therapyDateEnd, myFormat.format(terapia.getDateEnd()))

        values.put(therapyNumberDays, terapia?.getDays())
        values.put(therapyNotify, terapia?.getNotify())
        values.put(therapyMon, checkBool(terapia!!.isMon()))
        values.put(therapyTue, checkBool(terapia.isTue()))
        values.put(therapyWed, checkBool(terapia.isWed()))
        values.put(therapyThu, checkBool(terapia.isThu()))
        values.put(therapyFri, checkBool(terapia.isFri()))
        values.put(therapySat, checkBool(terapia.isSat()))
        values.put(therapySun, checkBool(terapia.isSun()))
        values.put(therapyDosage, terapia.getDosaggio())

        val id = db.insert(therapyTable, null, values)
        Log.d("insertTherapy()", "inserimento terapia")
        // close db connection
        db.close()

        return id
    }

    fun getAllTherapies(): List<TherapyEntityDB> {
        val list = ArrayList<TherapyEntityDB>()
        val db = readableDatabase
        val cursor = db.rawQuery(getAllTherapies, null)
        if (!cursor.moveToFirst())
            Log.d("getAllTherapy", "No therapy found")
        else {
            do {
                val current = TherapyEntityDB()
                if (cursor.getString(cursor.getColumnIndex(therapyDateEnd)) == null)
                    current.setDateEnd(null)
                else
                    current.setDateEnd(stringToDate(cursor.getString(cursor.getColumnIndex(therapyDateEnd))))
                current.setDateStart(stringToDate(cursor.getString(cursor.getColumnIndex(therapyDateStart))))
                current.setDays(cursor.getInt(cursor.getColumnIndex(therapyNumberDays)))
                current.setID(cursor.getInt(cursor.getColumnIndex(therapyID)))
                current.setNotify(cursor.getInt(cursor.getColumnIndex(therapyNotify)))
                current.setMon(checkInt(cursor.getInt(cursor.getColumnIndex(therapyMon))))
                current.setTue(checkInt(cursor.getInt(cursor.getColumnIndex(therapyTue))))
                current.setThu(checkInt(cursor.getInt(cursor.getColumnIndex(therapyThu))))
                current.setWed(checkInt(cursor.getInt(cursor.getColumnIndex(therapyWed))))
                current.setFri(checkInt(cursor.getInt(cursor.getColumnIndex(therapyFri))))
                current.setSat(checkInt(cursor.getInt(cursor.getColumnIndex(therapySat))))
                current.setSun(checkInt(cursor.getInt(cursor.getColumnIndex(therapySun))))
                current.setDrug(cursor.getString(cursor.getColumnIndex(therapyDrug)))
                current.setDosage(cursor.getInt(cursor.getColumnIndex(therapyDosage)))
                list.add(current)

            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return list
    }

    fun getTherapy(ID: Int?): TherapyEntityDB? {
        val db = readableDatabase
        val query = "SELECT * FROM $therapyTable WHERE $therapyID=?"

        Log.d("getTherapy ", "id="+ID.toString())
        if(ID==null)return null
        val cursor:Cursor? = db.rawQuery(query, arrayOf(ID!!.toString() + ""))
        if (cursor?.count == 0 || cursor?.count == -1) {
            Log.d("therapy ", ID.toString() + " not found")
            return null
        }
        val count = cursor?.count
        val current = TherapyEntityDB()
        cursor?.moveToFirst()
        if (cursor?.getString(cursor.getColumnIndex(therapyDateEnd)) == null)
            current.setDateEnd(null)
        else
            current.setDateEnd(stringToDate(cursor.getString(cursor.getColumnIndex(therapyDateEnd))))
        current.setDateStart(stringToDate(cursor!!.getString(cursor.getColumnIndex(therapyDateStart))))
        current.setDays(cursor.getInt(cursor.getColumnIndex(therapyNumberDays)))
        current.setID(cursor.getInt(cursor.getColumnIndex(therapyID)))
        current.setNotify(cursor.getInt(cursor.getColumnIndex(therapyNotify)))
        current.setMon(checkInt(cursor.getInt(cursor.getColumnIndex(therapyMon))))
        current.setTue(checkInt(cursor.getInt(cursor.getColumnIndex(therapyTue))))
        current.setThu(checkInt(cursor.getInt(cursor.getColumnIndex(therapyThu))))
        current.setWed(checkInt(cursor.getInt(cursor.getColumnIndex(therapyWed))))
        current.setFri(checkInt(cursor.getInt(cursor.getColumnIndex(therapyFri))))
        current.setSat(checkInt(cursor.getInt(cursor.getColumnIndex(therapySat))))
        current.setSun(checkInt(cursor.getInt(cursor.getColumnIndex(therapySun))))
        current.setDrug(cursor.getString(cursor.getColumnIndex(therapyDrug)))
        current.setDosage(cursor.getInt(cursor.getColumnIndex(therapyDosage)))
        db.close()
        cursor.close()
        return current
    }

    fun updateTherapy(toUpdate: TherapyEntityDB?): Long {
        // get writable database as we want to write data
        val db = this.writableDatabase
        val myFormat = SimpleDateFormat("dd/MM/yyyy")
        val values = ContentValues()
        values.put(therapyID, toUpdate?.getID())
        values.put(therapyDrug, toUpdate?.getDrug())
        values.put(therapyDateStart, myFormat.format(toUpdate?.getDateStart()))

        if (toUpdate?.getDateEnd() == null)
            values.put(therapyDateEnd, null as String?)
        else
            values.put(therapyDateEnd, myFormat.format(toUpdate.getDateEnd()))

        values.put(therapyNumberDays, toUpdate?.getDays())
        values.put(therapyNotify, toUpdate?.getNotify())
        values.put(therapyMon, checkBool(toUpdate!!.isMon()))
        values.put(therapyTue, checkBool(toUpdate.isTue()))
        values.put(therapyWed, checkBool(toUpdate.isWed()))
        values.put(therapyThu, checkBool(toUpdate.isThu()))
        values.put(therapyFri, checkBool(toUpdate.isFri()))
        values.put(therapySat, checkBool(toUpdate.isSat()))
        values.put(therapySun, checkBool(toUpdate.isSun()))
        values.put(therapyDosage, toUpdate.getDosaggio())
        // update row
        val id = db.update(therapyTable, values, "$therapyID=?", arrayOf(toUpdate.getID().toString() + "")).toLong()
        Log.d("updateTherapy()", "terapia aggiornata")
        // close db connection
        db.close()
        return id
    }

    fun removeTherapyBYId(ID: Int?): Int {
        val db = writableDatabase
        val deleteStatus = db.delete(therapyTable, "$therapyID=?", arrayOf(ID.toString() + ""))
        db.close()
        return deleteStatus
    }

    fun removeTherapyByDrug(nome: String?): Int {
        val db = writableDatabase
        val deleteStatus = db.delete(therapyTable, "$therapyDrug=?", arrayOf(nome))
        db.close()
        return deleteStatus
    }


    // Ottenere la lista delle ore di una terapia
    fun getTherapyHour(th: TherapyEntityDB?): List<Time>? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT DISTINCT " + assumptionHour + " FROM " + assumptionTable + " WHERE " + assumptiontherapy
                + " = " + th?.getID(), null)

        if (cursor.count == 0) {
            Log.d("assumption error", "nessun orario trovato per la terapia inserita")
            return null
        }

        val list = ArrayList<Time>()
        cursor.moveToFirst()
        do {
            val time = cursor.getString(cursor.getColumnIndex(assumptionHour))
            val hour = Time.valueOf(time)

            list.add(hour)
        } while (cursor.moveToNext())

        return list
    }


    /////////////////////////////////////////////////////////////////
    ////////////////////////////// FARMACI //////////////////////////
    /////////////////////////////////////////////////////////////////
    fun insertDrug(drug: DrugEntity?): Long {
        val db = writableDatabase

        if (getDrugByName(drug?.getNome()) != null) {
            Log.d("inserimento fallito", "farmaco già presente")
            return -1
        }
        val toInsert = drug?.getAllValues()
        val id = db.insert(drugTable, null, toInsert)
        Log.d("avvenuto:", "inserimento farmaco")
        // close db connection
        db.close()

        return id
    }

    fun getDrugByName(name: String?): DrugEntity? {

        val db = readableDatabase
        val query = "SELECT * FROM $drugTable WHERE $drugName=?"
        val cursor = db.rawQuery(query, arrayOf(name + ""))
        if (cursor.count == 0 || cursor.count == -1) {
            Log.d("getDrugByName", "$name non trovata")
            return null
        }
        val current = DrugEntity()
        cursor.moveToFirst()
        current.setNome(cursor.getString(cursor.getColumnIndex(drugName)))
        current.setDescrizione(cursor.getString(cursor.getColumnIndex(drugDescription)))
        current.setPrezzo(cursor.getDouble(cursor.getColumnIndex(drugPrice)))
        current.setScorte(cursor.getInt(cursor.getColumnIndex(drugQuantities)))
        current.setTipo(cursor.getString(cursor.getColumnIndex(drugType)))
        cursor.close()
        db.close()
        return current
    }


    fun removeDrugBYName(nome: String?): Int {
        val db = writableDatabase
        val deleteStatus = db.delete(drugTable, "$drugName=?", arrayOf(nome + ""))
        db.close()
        return deleteStatus
    }


    fun getAllDrugs(): List<DrugEntity> {
        val list = ArrayList<DrugEntity>()
        val db = readableDatabase
        val cursor = db.rawQuery(getAllDrugs, null)
        if (!cursor.moveToFirst())
            Log.d("getAllDrugs", "Nessuna terapia trovata")
        else {
            do {
                val current = DrugEntity()

                current.setNome(cursor.getString(cursor.getColumnIndex(drugName)))
                current.setDescrizione(cursor.getString(cursor.getColumnIndex(drugDescription)))
                current.setPrezzo(cursor.getDouble(cursor.getColumnIndex(drugPrice)))
                current.setScorte(cursor.getInt(cursor.getColumnIndex(drugQuantities)))
                current.setTipo(cursor.getString(cursor.getColumnIndex(drugType)))
                list.add(current)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return list
    }

    fun updateDrug(toUpdate: DrugEntity?): Long {
        // get writable database as we want to write data
        val db = this.writableDatabase

        val values = ContentValues()

        values.put(drugName, toUpdate?.getNome())
        values.put(drugPrice, toUpdate?.getPrezzo())
        values.put(drugDescription, toUpdate?.getDescrizione())
        values.put(drugQuantities, toUpdate?.getScorte())
        values.put(drugType, toUpdate?.getTipo())
        // update row
        val id = db.update(drugTable, values, "$drugName=?", arrayOf(toUpdate?.getNome() + "")).toLong()
        Log.d("updateDrug", "aggiornata")
        // close db connection
        db.close()

        return id
    }


    /////////////////////////////////////////////////////////////////
    ////////////////////////////// TIPI /////////////////////////////
    /////////////////////////////////////////////////////////////////
    // Richiamata all'avvio dell'applicazione
    private fun setTypeList(db: SQLiteDatabase?) {
        val values = arrayOfNulls<ContentValues>(13)   // Abbiamo 13 tipi predefiniti
        for (i in 0..12) values[i] = ContentValues()
        values[0]?.put(typeName, "Applicazione/i")
        values[1]?.put(typeName, "Capsula/e")
        values[2]?.put(typeName, "Fiala/e")
        values[3]?.put(typeName, "Goccia/e")
        values[4]?.put(typeName, "Grammo/i")
        values[5]?.put(typeName, "Inalazione/i")
        values[6]?.put(typeName, "Iniezione/i")
        values[7]?.put(typeName, "Milligrammo/i")
        values[8]?.put(typeName, "Millilitro/i")
        values[9]?.put(typeName, "Pezzo/i")
        values[10]?.put(typeName, "Pillola/e")
        values[11]?.put(typeName, "Supposta/e")
        values[12]?.put(typeName, "Unità")

        for (i in 0..12) db?.insert(typeTable, null, values[i])
        //db non viene chiuso perchè lo fa già il metodo onCreate, dopo aver chiamato questo metodo

    }

    fun getTypeList(): Array<String?> {
        val db = readableDatabase
        val query = "SELECT * FROM $typeTable ORDER BY $typeName"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()
        //create an array using size of query's result
        val list = arrayOfNulls<String>(cursor.count)
        var i = 0
        do {
            list[i] = cursor.getString(cursor.getColumnIndex(typeName))
            i++
            Log.d("getTypeList()", "Inserito: " + cursor.getString(cursor.getColumnIndex(typeName)))
        } while (cursor.moveToNext())
        cursor.close()
        db.close()
        return list
    }


    ////////////////////////////////////////////////////////////////////
    ////////////////////////////// ASSUNZIONI //////////////////////////
    ////////////////////////////////////////////////////////////////////
    fun insertAssumption(assumption: AssumptionEntity): Long {
        val db = writableDatabase
        val toInsert = ContentValues()
        val myFormat = SimpleDateFormat("yyyy-MM-dd")

        val data = myFormat.format(assumption.data)
        toInsert.put(assumptionDate, data)
        toInsert.put(assumptionHour, assumption.ora.toString()) //formato di default hh:mm:ss. Per riconvertire:valueOf(String)
        toInsert.put(assumptiontherapy, assumption.terapia)
        if (assumption.stato) toInsert.put(assumptionState, 1) //medicina presa
        else toInsert.put(assumptionState, 0) //medicina non presa

        val id = db.insert(assumptionTable, null, toInsert)
        Log.d("insertAssuption()", "inserimento assunzione di TerapiaID: " + assumption.terapia + ", Data:" + data)
        // close db connection
        db.close()

        return id
    }

    fun removeAssumption(assumption: AssumptionEntity): Int {
        val db = writableDatabase
        val myFormat = SimpleDateFormat("yyyy-MM-dd")
        val data = myFormat.format(assumption.data)

        val ora = assumption.ora.toString()
        val deleteStatus = db.delete(drugTable, arrayOf(assumptionDate, assumptionHour, assumptiontherapy).toString() + "=?", arrayOf(data.toString(), ora.toString(), assumption.terapia.toString()))
        db.close()
        return deleteStatus
    }
    fun removeAssumptionByTherapy(therapy: TherapyEntityDB?): Int {
        val db = writableDatabase

        val deleteStatus = db.delete(assumptionTable, assumptiontherapy + "=" + therapy?.getID().toString(), null)
        db.close()
        Log.d("removeAssumptionByTherapy", "assunzioni eliminate, deleteStatus:$deleteStatus")
        return deleteStatus
    }

    fun setAssumption(assumption: AssumptionEntity, state: Boolean) {
        val myFormat = SimpleDateFormat("yyyy-MM-dd")

        val data = myFormat.format(assumption.data)
        val db = writableDatabase
        val values = ContentValues()
        values.put(assumptionState, state)

        var stato = 0
        if (state) stato = 1

        db.update(assumptionTable, values,
                "$assumptionDate=? and $assumptionHour=? and $assumptiontherapy=?",
                arrayOf(data, assumption.ora.toString(), assumption.terapia.toString()))
    }

    // Lista delle Assunzioni di oggi
    fun getAssumptionByDate(data: Date): List<AssumptionEntity> {
        val db = readableDatabase
        val myFormat = SimpleDateFormat("yyyy-MM-dd")

        val dataToRead = myFormat.format(data)
        //Query di ricerca
        val current = db.rawQuery("SELECT " + assumptionDate + "," + assumptionHour + "," + assumptionState + "," + therapyDrug + "," + therapyDosage
                + "," + assumptiontherapy + " FROM " + assumptionTable + " INNER JOIN " + therapyTable + " ON " + assumptiontherapy + "=" + therapyID
                + " WHERE " + assumptionDate + "='" + dataToRead + "'", null)

        val list = ArrayList<AssumptionEntity>()

        if (current.count == 0)
            Log.d("nessuna assunzione in data", dataToRead)
        else {
            current.moveToFirst()
            do {
                //inserisco nella lista tutte le assunzioni trovate

                val farmaco = current.getString(current.getColumnIndex(therapyDrug))
                //query per leggere il tipo(uso un cursore temporaneo)
                val temp = db.rawQuery("SELECT " + typeName
                        + " FROM " + drugTable + " INNER JOIN " + typeTable + " ON " + drugType + "=" + typeName
                        + " WHERE " + drugName + "='" + farmaco + "'", null)
                temp.moveToFirst()
                val tipo = temp.getString(temp.getColumnIndex(typeName))
                temp.close()
                val ora = Time.valueOf(current.getString(current.getColumnIndex(assumptionHour)))
                val stato = checkInt(current.getInt(current.getColumnIndex(assumptionState)))
                Log.d("Stato Assunzione:", current.getInt(current.getColumnIndex(assumptionState)).toString() + stato.toString())
                val dosaggio = current.getInt(current.getColumnIndex(therapyDosage))
                val terapiaId = current.getInt(current.getColumnIndex(assumptiontherapy))
                val assunzione = AssumptionEntity(data, ora, farmaco, stato, dosaggio, tipo, terapiaId)
                list.add(assunzione)
            } while (current.moveToNext())
        }


        db.close()
        current.close()

        Log.d("lista assunzioni", "creata")
        return list

    }

    // Usata per le notifiche, restituisce le assunzioni con data da oggi in poi
    fun getAssumptionsFromNow(): List<AssumptionEntity> {
        val myFormat = SimpleDateFormat("yyyy-MM-dd")
        val list = ArrayList<AssumptionEntity>()

        // Database
        val db = readableDatabase
        val query = "SELECT * FROM $assumptionTable WHERE $assumptionDate>= date('now')"
        val cursor = db.rawQuery(query, null)

        if (!cursor.moveToFirst())
            Log.d("getAssumptionsFromNow", "Nessuna assunzione trovata")
        else {
            do {
                var data: Date? = null
                try {
                    data = myFormat.parse(cursor.getString(cursor.getColumnIndex(assumptionDate)))
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

                val ora = Time.valueOf(cursor.getString(cursor.getColumnIndex(assumptionHour)))
                val terapia = cursor.getInt(cursor.getColumnIndex(assumptiontherapy))
                val stato = checkInt(cursor.getInt(cursor.getColumnIndex(assumptionState)))
                val assunzione = AssumptionEntity(data, ora, terapia, stato)
                list.add(assunzione)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        Log.d("lista assunzioni", "creata")
        return list
    }


    // Lista delle assunzioni di una Terapia da domani in poi(usata per le terapie con durata senza limiti)
    fun getAssumptionsByTherapy(therapy: TherapyEntityDB): List<AssumptionEntity> {
        val myFormat = SimpleDateFormat("yyyy-MM-dd")

        val list = ArrayList<AssumptionEntity>()

        // Database
        val db = readableDatabase
        val query = ("SELECT * FROM " + assumptionTable
                + " WHERE " + assumptionDate + "> date('now')" + " AND " + assumptiontherapy + "=" + therapy.getID())
        val cursor = db.rawQuery(query, null)

        if (!cursor.moveToFirst())
            Log.d("getAssumptionsByTherapy", "Nessuna assunzione trovata")
        else {
            do {
                var data: Date? = null
                try {
                    data = myFormat.parse(cursor.getString(cursor.getColumnIndex(assumptionDate)))
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

                val ora = Time.valueOf(cursor.getString(cursor.getColumnIndex(assumptionHour)))
                val terapia = cursor.getInt(cursor.getColumnIndex(assumptiontherapy))
                val stato = checkInt(cursor.getInt(cursor.getColumnIndex(assumptionState)))
                val assunzione = AssumptionEntity(data, ora, terapia, stato)
                list.add(assunzione)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        Log.d("getAssumptionsByTherapy()", "Lista creata")
        return list

    }


////////////////////////////////////////////////////////////////////////////
////////////////////////////// METODI DI SUPPORTO //////////////////////////
///////////////////////////////////////////////////////////////////////////
    // Converte INT in BOOLEAN
    private fun checkInt(i: Int): Boolean {
        return if (i == 1) true
        else false
    }

    // Converte BOOLEAN in INT
    private fun checkBool(value: Boolean): Int {
        return if (value)
            1
        else
            0
    }

    private fun stringToDate(toDate: String): Date? {
        var data: Date? = null
        val dt = SimpleDateFormat("dd/MM/yyyy")
        try {
            data = dt.parse(toDate)
        } catch (e: ParseException) {
            Log.d("parsing Data", "fallito")
        }

        return data
    }

    //Definizione costanti
    val drugTable = "FARMACO"
    val drugName = "nomeFarmaco"
    val drugPrice = "prezzo"
    val drugQuantities = "scorte"
    val drugType = "tipo"
    val drugDescription = "descrizione"
    val typeTable = "TIPO"
    val typeName = "nomeTipo"
    val assumptionTable = "ASSUNZIONE"
    val assumptionDate = "data"
    val assumptionHour = "ora"
    val assumptiontherapy = "terapiaAssunzione"
    val assumptionState = "stato"
    val therapyTable = "TERAPIA"
    val therapyID = "ID"
    val therapyDateStart = "dataInizio"
    val therapyDateEnd = "dataFine"
    val therapyNotify = "minNotifica"
    val therapyNumberDays = "numeroGiorni"
    val therapyMon = "Lunedì"
    val therapyTue = "Martedì"
    val therapyWed = "Mercoledì"
    val therapyThu = "Giovedì"
    val therapyFri = "Venerdì"
    val therapySat = "Sabato"
    val therapySun = "Domenica"
    val therapyDrug = "farmaco"
    val therapyDosage = "dosaggio"


    //QUERY
    val CREATE_DRUG_TABLE = (
            "CREATE TABLE " + drugTable + "("
                    + drugName + " VARCHAR(50) PRIMARY KEY,"
                    + drugDescription + " VARCHAR(150),"
                    + drugPrice + " REAL,"
                    + drugQuantities + " INTEGER,"
                    + drugType + " VARCHAR(30),"
                    + "FOREIGN KEY(" + drugType + ") REFERENCES " + typeTable + " (" + typeName + ") ON UPDATE CASCADE ON DELETE CASCADE"
                    + ");")

    val CREATE_TYPE_TABLE = (
            "CREATE TABLE " + typeTable + "("
                    + typeName + " VARCHAR(30) PRIMARY KEY"
                    + ");")

    val CREATE_ASSUMPTION_TABLE = (
            "CREATE TABLE " + assumptionTable + "("
                    + assumptionDate + " CHARACTER(10),"
                    + assumptionHour + " CHARACTER(8),"
                    + assumptiontherapy + " INT,"
                    + assumptionState + " INT,"
                    + "PRIMARY KEY(" + assumptionDate + ", " + assumptionHour + ", " + assumptiontherapy + "),"
                    + "FOREIGN KEY(" + assumptiontherapy + ") REFERENCES " + therapyTable + " (" + therapyID + ") ON UPDATE CASCADE ON DELETE CASCADE"
                    + ");")

    val CREATE_THERAPY_TABLE = (
            "CREATE TABLE " + therapyTable + "("
                    + therapyID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + therapyDateStart + " CHARACTER(10),"
                    + therapyDateEnd + " CHARACTER(10),"
                    + therapyNotify + " INTEGER,"
                    + therapyNumberDays + " INTEGER,"
                    + therapyDosage + " INT,"
                    + therapyMon + " INT,"
                    + therapyTue + " INT,"
                    + therapyWed + " INT,"
                    + therapyThu + " INT,"
                    + therapyFri + " INT,"
                    + therapySat + " INT,"
                    + therapySun + " INT,"
                    + therapyDrug + " VARCHAR(50),"
                    + "FOREIGN KEY(" + therapyDrug + ") REFERENCES " + drugTable + " (" + drugName + ") ON UPDATE CASCADE ON DELETE CASCADE"
                    + ");")


    val getAllTherapies = "SELECT * FROM $therapyTable ;"
    val getAllDrugs = "SELECT * FROM $drugTable ;"
}