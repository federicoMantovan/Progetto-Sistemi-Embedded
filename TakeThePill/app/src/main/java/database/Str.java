package database;


public class Str {
   public static final String drugTable="FARMACO";
   public static final String drugName="nomeFarmaco";
   public static final String drugPrice="prezzo";
   public static final String drugQuantities="scorte";
   public static final String drugType="tipo";
   public static final String drugDescription="descrizione";
   public static final String typeTable="TIPO";
   public static final String typeName="nomeTipo";
   public static final String assumptionTable="ASSUNZIONE";
   public static final String assumptionDate="data";
   public static final String assumptionHour="ora";
   public static final String assumptiontherapy="terapiaAssunzione";
   public static final String assumptionState="stato";
   public static final String therapyTable="TERAPIA";
   public static final String therapyID="ID";
   public static final String therapyDateStart="dataInizio";
   public static final String therapyDateEnd="dataFine";
   public static final String therapyNotify="minNotifica";
   public static final String therapyNumberDays="numeroGiorni";
   public static final String therapyMon="Lunedì";
   public static final String therapyTue="Martedì";
   public static final String therapyWed="Mercoledì";
   public static final String therapyThu="Giovedì";
   public static final String therapyFri="Venerdì";
   public static final String therapySat="Sabato";
   public static final String therapySun="Domenica";
   public static final String therapyDrug="farmaco";
   public static final String therapyDosage="dosaggio";



   //QUERY
 static final String CREATE_DRUG_TABLE =
           "CREATE TABLE " + drugTable + "("
                   + drugName + " VARCHAR(50) PRIMARY KEY,"
                   + drugDescription + " VARCHAR(150),"
                   + drugPrice +" REAL,"
                   + drugQuantities + " INTEGER,"
                   + drugType +" VARCHAR(30),"
                   + "FOREIGN KEY("+ drugType +") REFERENCES "+ typeTable +" ("+ typeName +") ON UPDATE CASCADE ON DELETE CASCADE"
                   +");";

    static final String CREATE_TYPE_TABLE =
           "CREATE TABLE " + typeTable + "("
                   + typeName + " VARCHAR(30) PRIMARY KEY"
                   + ");";

    static final String CREATE_ASSUMPTION_TABLE =
           "CREATE TABLE " + assumptionTable + "("
                   + assumptionDate+ " CHARACTER(10),"
                   + assumptionHour + " CHARACTER(8),"
                   + assumptiontherapy + " INT,"
                   + assumptionState +" INT,"
                   + "PRIMARY KEY("+ assumptionDate + ", "+ assumptionHour +", " +assumptiontherapy +"),"
                   + "FOREIGN KEY("+ assumptiontherapy + ") REFERENCES "+ therapyTable +" ("+ therapyID +") ON UPDATE CASCADE ON DELETE CASCADE"
                   + ");";

   static final String CREATE_THERAPY_TABLE =
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
                   + "FOREIGN KEY("+ therapyDrug +") REFERENCES "+ drugTable +" ("+ drugName+") ON UPDATE CASCADE ON DELETE CASCADE"
                   + ");";


    static final String getAllTherapies = "SELECT * FROM "+ therapyTable + " ;";
    static final String getAllDrugs="SELECT * FROM "+ drugTable + " ;";
}
