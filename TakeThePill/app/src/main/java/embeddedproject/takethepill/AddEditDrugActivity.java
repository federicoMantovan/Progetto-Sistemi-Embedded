package embeddedproject.takethepill;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import database.DatabaseHelper;


public class AddEditDrugActivity extends AppCompatActivity {

    private int elemSelez,prev; //Gestiscono la selezione del tipo di farmaco

    private String tipiFarmaci[];// Lista dei tipi di farmaci
    private DrugEntity drug;    // Rappresenta il farmaco in considerazione

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_drug);

        android.support.v7.widget.Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DatabaseHelper db=new DatabaseHelper(this);
        tipiFarmaci=db.getTypeList();

        // TextView e EditText del layout:
        final EditText etNome =    findViewById(R.id.etDrugName);
        final TextView tvTipo =    findViewById(R.id.tvDrugType);
        final EditText etDescr =   findViewById(R.id.etDescription);
        final EditText etPrezzo =  findViewById(R.id.etDrugPrice);
        final EditText etScorte =  findViewById(R.id.etDrugQuantity);

        final boolean nuovo=getIntent().getBooleanExtra("nuovo",true);
        if(nuovo){  // Se stiamo creando un nuovo farmaco
            drug=new DrugEntity("","","Applicazione/i",5,20);
            elemSelez=prev=0;   // elemento selezionato nella lista TIPO farmaco
            tvTipo.setText("Tipo: seleziona...");
        }else { // Se stiamo modificando un farmaco
            drug= db.getDrugByName(getIntent().getStringExtra("name"));
            for(int i=0; i<tipiFarmaci.length;i++){
                if(tipiFarmaci[i].equals(drug.getTipo()))elemSelez=prev=i;   // elemento selezionato nella lista TIPO farmaco
            }
            tvTipo.setText("Tipo: "+tipiFarmaci[elemSelez]);
        }

        etNome.setText(drug.getNome());
        etDescr.setText(drug.getDescrizione());
        etPrezzo.setText(String.valueOf(drug.getPrezzo()));
        etScorte.setText(String.valueOf(drug.getScorte()));

        // BOTTONI TOOLBAR SALVA E ANNULLA
        TextView tvSave =  findViewById(R.id.toolbar_save);
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drug.setDescrizione(etDescr.getText().toString());
                drug.setPrezzo(Double.parseDouble(etPrezzo.getText().toString()));
                drug.setScorte(Integer.parseInt(etScorte.getText().toString()));
                drug.setTipo(drug.getTipo());
                if(nuovo){  // Se stiamo creando un nuovo farmaco
                    // OPERAZIONE DATABASE aggiungi farmaco
                    drug.setNome(etNome.getText().toString());
                    if(drug.getNome().equals("")){  //se il nome è vuoto
                        Snackbar snackbar = Snackbar
                                .make(v, "Devi inserire un nome!", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                    else{
                        db.insertDrug(drug);
                        finish();   // Chiude l'activity e riapre la precedente
                    }
                }else{  // Se stiamo modificando un farmaco
                    db.updateDrug(drug);    // OPERAZIONE DATABASE modifica farmaco
                    finish();   // Chiude l'activity e riapre la precedente
                }


            }
        });
        TextView tvAnnulla =  findViewById(R.id.toolbar_annulla);
        tvAnnulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();   // Chiude l'activity e riapre la precedente
            }
        });

        // Bottone seleziona Tipo di farmaco
        ImageButton btnTipo = (ImageButton) findViewById(R.id.ibEditType);
        btnTipo.setOnClickListener(new View.OnClickListener() {

            //private String chooseItem;
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Tipo di Farmaco");


                builder.setSingleChoiceItems(tipiFarmaci, elemSelez, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int itemIndex) {
                        elemSelez=itemIndex;
                        drug.setTipo(tipiFarmaci[elemSelez]);
                    }
                });

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        prev=elemSelez;
                        drug.setTipo(tipiFarmaci[elemSelez]);
                        tvTipo.setText("Tipo: "+drug.getTipo());
                    }
                });

                builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Niente
                        elemSelez=prev;
                        drug.setTipo(tipiFarmaci[elemSelez]);
                        tvTipo.setText("Tipo: "+drug.getTipo());
                    }
                });

                builder.show();

            }
        });

        // BOTTONE ELIMINA
        Button btnElimina = (Button) findViewById(R.id.btnDeleteDrug);
        if(nuovo) btnElimina.setVisibility(View.GONE);  //Se si sta inserendo un nuovo farmaco il bottone non deve essere visibile
        btnElimina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Messaggio "SICURO? SI/NO"
                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Sei sicuro di voler eliminare il farmaco?");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Operazione DATABASE CHE CANCELLA IL FARMACO
                        db.removeDrugBYName(drug.getNome());
                        db.removeTherapyByDrug(drug.getNome());
                        finish();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Nothing
                    }
                });
                builder.setCancelable(false);
                builder.show();

            }
        });




    }

}
