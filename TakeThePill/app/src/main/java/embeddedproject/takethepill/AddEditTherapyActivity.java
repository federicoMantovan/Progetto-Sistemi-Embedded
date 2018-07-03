package embeddedproject.takethepill;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;


import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseHelper;

public class AddEditTherapyActivity extends AppCompatActivity {

    private TherapyEntityDB terapia;    // Rappresenta la terapia in considerazione
    private String drugList[];  // Rappresenta la lista dei farmaci
    private ArrayList<int[]> listaOre;  // Rappresenta la lista delle ore
    private boolean[] giorniSelezionati;    // Usata nella selezione dei giorni
    private final String giorni[] = {"Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica"};
    DatabaseHelper db;
    TextView tvHours;
    int day, month, year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_therapy);

        Toolbar toolbar =  findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        // Database
        db=new DatabaseHelper(this);

        // Lista dei farmaci
        List<DrugEntity> l=db.getAllDrugs();
        drugList=new String[l.size()];
        for(int i=0;i<l.size();i++) drugList[i]=l.get(i).getNome();

        // TextView ed EditText del layout
        final TextView tvDrugName=findViewById(R.id.tvDrugName);
        final EditText etQuantity=findViewById(R.id.etDrugQuantity2);
        final TextView tvDuration=findViewById(R.id.tvDuration);
        final TextView tvDays=findViewById(R.id.tvDays);
        final TextView tvNotify=findViewById(R.id.tvNotify);
        tvHours=findViewById(R.id.tvHour);

        // Stiamo creando una nuova terapia?
        final boolean nuova=getIntent().getBooleanExtra("nuova",true);
        final int id=getIntent().getIntExtra("id",-1);

        // Impostare le variabili:
        if(nuova) { // Se è una NUOVA terapia
            terapia=new TherapyEntityDB(null,-1,-1,true,false,false,false,false,false,false,1,null);
            tvDrugName.setText("Seleziona farmaco ...");
            listaOre=new ArrayList<>();
        }
        else{   // Se è MODIFICA terapia
            terapia=db.getTherapy(id);
            tvDrugName.setText(terapia.getDrug());

            listaOre=new ArrayList<>();
            List<Time> list =db.getTherapyHour(terapia);    // Operazione database Lista delle ore
            for (int i=0; i<list.size();i++){
                listaOre.add(new int[]{list.get(i).getHours(),list.get(i).getMinutes()});
            }

            if(terapia.getDays()==-2) Log.d("onCreate: AddEditTher: Data Fine",terapia.getDateEnd().toString());

        }

        // TextView Quantità
        etQuantity.setText(String.valueOf(terapia.getDosaggio()));

        // TextView Durata
        final DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        if(terapia.getDays()==-1)tvDuration.setText("Durata: Senza Limiti");
        else if(terapia.getDays()==-2)tvDuration.setText("Durata: fino al "+formatter.format(terapia.getDateEnd()));
        else tvDuration.setText("Durata: per "+terapia.getDays().toString()+ " giorni");

        giorniSelezionati = new boolean[]{terapia.isMon(), terapia.isTue(), terapia.isWed(), terapia.isThu(), terapia.isFri(), terapia.isSat(), terapia.isSun()};

        // TextView Giorni
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < giorniSelezionati.length; i++) {
            if (giorniSelezionati[i]) {
                if (s.length() > 0) s.append(", ");
                s.append(giorni[i]);
            }
        }
        if (s.toString().trim().equals("")) {
            tvDays.setText("Giorni: nessuno");
            s.setLength(0);
        } else {
            tvDays.setText("Giorni: " + s);
        }

        // TextView Notifica
        if(terapia.getNotify()!=-1) tvNotify.setText("Notifiche: "+terapia.getNotify().toString()+" min prima");
        else tvNotify.setText("Notifiche: nessuna");


        // TextView Ore
        tvHours.setText(listaOre.toString());
        String o="Ora: ";
        for (int i=0; i<listaOre.size();i++){
            if(listaOre.get(i)[0]<10)o+=("0"+listaOre.get(i)[0]);
            else o+=listaOre.get(i)[0]+"";
            if(listaOre.get(i)[1]<10)o+=":0"+listaOre.get(i)[1];
            else o+=(":"+listaOre.get(i)[1]);
            if(i!=listaOre.size()-1)o+=", ";
        }
        tvHours.setText(o);


        // BOTTONI TOOLBAR SALVA E ANNULLA
        TextView tvSave = (TextView) findViewById(R.id.toolbar_save2);
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                terapia.setDosage(Integer.valueOf(etQuantity.getText().toString()));
                if(nuova){  // Se si sta creando una nuova Terapia
                    if(terapia==null || listaOre==null || listaOre.size()==0){    //Se l'utente non ha inserito il farmaco o un orario
                        Snackbar snackbar = Snackbar
                                .make(v, "Devi inserire tutti i dati", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }else{
                        saveAll();
                        finish();   // Chiude l'activity e riapre la precedente
                    }
                }else{  // Se si sta modificando una terapia
                    db.updateTherapy(terapia);  // Operazione DATABASE modifica terapia di id=...
                    db.removeAssumptionByTherapy(terapia);

                    salvaOre();
                    finish();   // Chiude l'activity e riapre la precedente
                }
            }
        });
        TextView tvAnnulla = (TextView) findViewById(R.id.toolbar_annulla2);
        tvAnnulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();   // Chiude l'aactivity e riapre la precedente
            }
        });

        // BOTTONE "Nome farmaco"
        ImageButton drugName = findViewById(R.id.ibEditDrugName);
        drugName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AddEditTherapyActivity.this);
                builder.setTitle("Seleziona Farmaco");

                builder.setItems(drugList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tvDrugName.setText(drugList[i]);
                        terapia.setDrug(drugList[i]);
                    }
                });
                builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Niente
                    }
                });

                builder.show();

            }
        });


        // BOTTONE DURATA
        ImageButton btnDuration =  findViewById(R.id.ibEditDuration);
        btnDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AddEditTherapyActivity.this);

                final View durationView = getLayoutInflater().inflate(R.layout.alert_duration, null);
                builder.setView(durationView);

                final RadioButton rdbtNoLimits =  durationView.findViewById(R.id.noLimits);
                final RadioButton rdbtUntil=  durationView.findViewById(R.id.untilRdBtn);
                final RadioButton rdbtNumbDays=  durationView.findViewById(R.id.number_days_RdBtn);
                final EditText etDaysNumb = durationView.findViewById(R.id.etDaysNumber);
                final Button btnUntil =  durationView.findViewById(R.id.btnUntil);

                if(terapia.getDays()==-2){    // DataFine
                    rdbtNoLimits.setChecked(false);
                    rdbtUntil.setChecked(true);
                    rdbtNumbDays.setChecked(false);
                    btnUntil.setText(formatter.format(terapia.getDateEnd()));
                    etDaysNumb.setText("");
                }else if(terapia.getDays()==-1){ //Senza Limiti
                    rdbtNoLimits.setChecked(true);
                    rdbtUntil.setChecked(false);
                    rdbtNumbDays.setChecked(false);
                    btnUntil.setText("Seleziona");
                    etDaysNumb.setText("");
                }
                else{   // n giorni
                    rdbtNoLimits.setChecked(false);
                    rdbtUntil.setChecked(false);
                    rdbtNumbDays.setChecked(true);
                    btnUntil.setText("Seleziona");
                    etDaysNumb.setText(terapia.getDays().toString());
                }

                //Pulsante durata
                btnUntil.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar c = Calendar.getInstance();
                        day = c.get(Calendar.DAY_OF_MONTH);
                        month = c.get(Calendar.MONTH);
                        year = c.get(Calendar.YEAR);

                        DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int yearSelected, int monthSelected, int daySelected) {
                                year = yearSelected;
                                month = monthSelected+1;
                                day = daySelected;

                                btnUntil.setText(day + "/" + month + "/" + year);
                            }
                        };
                        new DatePickerDialog(AddEditTherapyActivity.this, datePicker, year, month, day).show();
                    }
                });

                rdbtNoLimits.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnUntil.setEnabled(false);
                        etDaysNumb.setEnabled(false);
                    }
                });
                rdbtUntil.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnUntil.setEnabled(true);
                        etDaysNumb.setEnabled(false);
                    }
                });
                rdbtNumbDays.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnUntil.setEnabled(false);
                        etDaysNumb.setEnabled(true);
                    }
                });

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(rdbtNoLimits.isChecked()){   // Cliccato su senza limiti
                            terapia.setDays(-1);
                            terapia.setDateEnd(null);
                            tvDuration.setText("Durata: Senza Limiti");
                        }
                        else if(rdbtNumbDays.isChecked()) { //Cliccasto su nGiorni
                            terapia.setDays(Integer.valueOf(etDaysNumb.getText().toString()));
                            terapia.setDateEnd(null);
                            tvDuration.setText("Durata: per "+terapia.getDays().toString()+ " giorni");
                        }else { //Cliccato su datafine
                            terapia.setDays(-2);
                            Date date = null;
                            try {
                                date = formatter.parse(btnUntil.getText().toString());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            terapia.setDateEnd(date);
                            tvDuration.setText("Durata: fino al "+formatter.format(terapia.getDateEnd()));
                        }

                    }
                });

                builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Bottone annulla, nessuna azione
                    }
                });

                builder.show();
            }
        });



        // BOTTONE GIORNI
        ImageButton btnDays =  findViewById(R.id.ibEditDays);
        btnDays.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AddEditTherapyActivity.this);
                builder.setTitle("Seleziona i giorni");

                builder.setMultiChoiceItems(giorni, giorniSelezionati, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int itemIndex, boolean checked) {

                    }
                });

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        // giorniSelezionati viene modificato in automatico

                        ListView list = ((AlertDialog) dialogInterface).getListView();


                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < list.getCount(); i++) {
                            boolean checked = list.isItemChecked(i);

                            if (checked) {
                                if (stringBuilder.length() > 0) stringBuilder.append(", ");
                                stringBuilder.append(list.getItemAtPosition(i));

                            }
                        }

                        if (stringBuilder.toString().trim().equals("")) {
                            tvDays.setText("Giorni: nessuno");
                            stringBuilder.setLength(0);
                        } else {
                            tvDays.setText("Giorni: " + stringBuilder);
                        }

                        terapia.setMon(giorniSelezionati[0]);
                        terapia.setTue(giorniSelezionati[1]);
                        terapia.setWed(giorniSelezionati[2]);
                        terapia.setThu(giorniSelezionati[3]);
                        terapia.setFri(giorniSelezionati[4]);
                        terapia.setSat(giorniSelezionati[5]);
                        terapia.setSun(giorniSelezionati[6]);
                    }

                });

                builder.show();

            }
        });


        // BOTTONE ORA
        ImageButton btnHour =  findViewById(R.id.ibEditHour);
        btnHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddHourActivity.class);
                //intent.putExtra("id",id);
                intent.putExtra("listaore",listaOre);
                startActivityForResult(intent,1);
            }
        });



        // BOTTONE NOTIFICHE
        ImageButton btnNotify =  findViewById(R.id.ibEditNotify);
        btnNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddEditTherapyActivity.this);

                final View notifyView = getLayoutInflater().inflate(R.layout.alert_notify, null);
                builder.setView(notifyView);

                final RadioButton rbNotNotify=notifyView.findViewById(R.id.notNotify);
                final RadioButton rbNotify=notifyView.findViewById(R.id.minBefore);
                final EditText etNotify=notifyView.findViewById(R.id.etNotify);

                if(terapia.getNotify()==-1){
                    rbNotNotify.setChecked(true);
                    rbNotify.setChecked(false);
                    etNotify.setText("");
                }
                else {
                    rbNotNotify.setChecked(false);
                    rbNotify.setChecked(true);
                    etNotify.setText(terapia.getNotify().toString());
                }

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Fai qualcosa quando premi il pulsante "ok"
                        if(rbNotify.isChecked()){
                            if(etNotify.getText().toString().equals(""))terapia.setNotify(0);
                            else terapia.setNotify(Integer.valueOf(etNotify.getText().toString()));
                            tvNotify.setText("Notifiche: "+terapia.getNotify().toString() + " min prima");
                        } else {
                            terapia.setNotify(-1);
                            tvNotify.setText("Notifiche: nessuna");
                        }

                    }
                });

                builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Nessuna azione quando si preme il pulsante "annulla"
                    }
                });

                builder.show();
            }
        });



        // BOTTONE ELIMINA
        Button btnElimina =  findViewById(R.id.btnDeleteTherapy);
        if(nuova) btnElimina.setVisibility(View.GONE);  //Se si sta inserendo una nuova terapia il bottone non deve essere visibile
        btnElimina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(v.getContext());
                builder.setTitle("Sei sicuro di voler eliminare la Terapia?");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // AGGIUNGI CODICE DATABASE CHE CANCELLA LA TERAPIA
                        db.removeTherapyBYId(terapia.getID());
                        // Le assunzioni si eliminano da sole in cascata dalle terapie
                        finish();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Niente
                    }
                });
                builder.setCancelable(false);
                builder.show();

            }
        });
    }


    // QUANDO SI RITORNA DA AddHourActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){

                if(terapia.getDays()==-2) Log.d("onActivityResult(): Data Fine",terapia.getDateEnd().toString());

                listaOre=(ArrayList<int[]>)data.getSerializableExtra("result");

                // Visualizzo le ore sul tvHour
                tvHours.setText(listaOre.toString());
                String o="Ora: ";
                for (int i=0; i<listaOre.size();i++){
                    if(listaOre.get(i)[0]<10)o+=("0"+listaOre.get(i)[0]);
                    else o+=listaOre.get(i)[0]+"";
                    if(listaOre.get(i)[1]<10)o+=":0"+listaOre.get(i)[1];
                    else o+=(":"+listaOre.get(i)[1]);
                    if(i!=listaOre.size()-1)o+=", ";
                }
                tvHours.setText(o);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    // QUANDO SI SALVA UNA NUOVA TERAPIA
    private void saveAll(){
        // Salva la terapia
        db.insertTherapy(terapia);

        // Recupero L'ID dell'ultima terapia inserita
        ArrayList<TherapyEntityDB> listaTerapie ;
        listaTerapie=(ArrayList<TherapyEntityDB>) db.getAllTherapies();
        int ID=listaTerapie.get(listaTerapie.size()-1).getID();

        terapia.setID(ID);
        // Salva le ore (Assunzioni)
        salvaOre();

    }

    // SALVARE LE ASSUNZIONI
    private void salvaOre(){
        // Per ogni ora si genera una lista di assunzioni
        Log.d("salvaOre()","salvataggio assunzioni");
        if(terapia.getDays()==-2) Log.d("salvaOre(): Data Fine",terapia.getDateEnd().toString());

        for(int i=0;i<listaOre.size();i++){
            AssumptionEntity assumptionEntity=new AssumptionEntity();
            Time ora=new Time(listaOre.get(i)[0],listaOre.get(i)[1],0);

            List<AssumptionEntity> listAssunzioni = assumptionEntity.generateAssumption(terapia,ora,null);


            for(int j=0;j<listAssunzioni.size();j++) db.insertAssumption(listAssunzioni.get(j));

        }
    }

}
