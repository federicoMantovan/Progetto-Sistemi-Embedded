package embeddedproject.takethepill;

import android.content.ContentValues;

import database.Str;

public class DrugEntity {

// VARIABILI

    private String nome;
    private String descrizione;
    private String tipo;
    private double prezzo;
    private int scorte;

//COSTRUTTORE
    public DrugEntity(String nome, String descrizione, String tipo, double prezzo, int scorte){

        this.nome=nome;
        this.descrizione=descrizione;
        this.tipo=tipo;
        this.prezzo=prezzo;
        this.scorte=scorte;
    }
    //costruttore di default

    public DrugEntity()
    {

        this.nome=null;
        this.descrizione=null;
        this.tipo=null;
        this.prezzo=0.0;
        this.scorte=0;
    }

//METODI GET e SET

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    public int getScorte() {
        return scorte;
    }

    public void setScorte(int scorte) {
        this.scorte = scorte;
    }

    public ContentValues getAllValues()
    {
        ContentValues values =new ContentValues();

        values.put(Str.drugName,nome);
        values.put(Str.drugPrice,prezzo);
        values.put(Str.drugDescription,descrizione);
        values.put(Str.drugQuantities,scorte);
        values.put(Str.drugType,tipo);
        return values;
    }
}
