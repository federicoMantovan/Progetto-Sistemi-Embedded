package embeddedproject.takethepillkv

import android.content.ContentValues;

import database.Str;

public class DrugEntity {

// VARIABILI
    private var nome:String?
    private var descrizione:String?
    private var tipo:String?
    private var prezzo:Double
    private var scorte:Int

//COSTRUTTORE
    constructor( nome :String, descrizione:String ,tipo:String ,prezzo: Double ,scorte: Int){

        this.nome=nome
        this.descrizione=descrizione
        this.tipo=tipo
        this.prezzo=prezzo
        this.scorte=scorte
    }


    constructor()
    {
        this.nome=null
        this.descrizione=null
        this.tipo=null
        this.prezzo=0.0
        this.scorte=0
    }

//METODI GET e SET
    fun getNome(): String? {
        return nome
    }

    fun setNome(nome: String) {
        this.nome = nome
    }

    fun getDescrizione(): String? {
        return descrizione
    }

    fun setDescrizione(descrizione: String) {
        this.descrizione = descrizione
    }

    fun getTipo(): String? {
        return tipo
    }

    fun setTipo(tipo: String?) {
        this.tipo = tipo
    }

    fun getPrezzo(): Double {
        return prezzo
    }

    fun setPrezzo(prezzo: Double) {
        this.prezzo = prezzo
    }

    fun getScorte(): Int {
        return scorte
    }

    fun setScorte(scorte: Int) {
        this.scorte = scorte
    }

   fun  getAllValues() : ContentValues
    {
        val values =ContentValues()
        val str=Str()
        values.put(str.drugName,nome)
        values.put(str.drugPrice,prezzo)
        values.put(str.drugDescription,descrizione)
        values.put(str.drugQuantities,scorte)
        values.put(str.drugType,tipo)
        return values
    }
}
