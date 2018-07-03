package database

@SuppressWarnings("WeakerAccess")
class Str {

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