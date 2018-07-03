package embeddedproject.takethepill;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;

public class AddHourActivity extends AppCompatActivity {

    private ArrayList<int[]> listaOre;
    private int mHour, mMinute;
    CustomAdapterHour customAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hour);

        // BOTTONI TOOLBAR SALVA E ANNULLA
        TextView tvSave =  findViewById(R.id.toolbar_save3);
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // PASSARE ad AddEtitTherapy la lista delle ore
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",listaOre); // Passo la lista delle ore ad AddEdictTherapy
                setResult(Activity.RESULT_OK,returnIntent);
                finish();   // Chiude l'activity e riapre la precedente
            }
        });
        TextView tvAnnulla =  findViewById(R.id.toolbar_annulla3);
        tvAnnulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();   // Chiude l'activity e riapre la precedente
            }
        });

        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // LISTA DELLE ORE
        final ListView listView =  findViewById(R.id.listHours);

        //Ricevo la lista delle ore da AddEditTherapyActivity
        listaOre=(ArrayList<int[]>)getIntent().getSerializableExtra("listaore");
        if(listaOre==null) listaOre = new ArrayList<>();

        customAdapter = new CustomAdapterHour(listaOre, this);
        listView.setAdapter(customAdapter);

        //TextView orario
        final TextView tvTime=findViewById(R.id.tvNewHour);
        String orario;
        if(mHour<10) orario="0"+mHour;
        else orario = mHour+"";
        if(mMinute<10) orario+=(":0"+mMinute);
        else orario+=(":"+mMinute);
        tvTime.setText(orario);

        // Bottone Seleziona Orario
        Button btnTimePicker=findViewById(R.id.btnSelectHour);
        btnTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                mHour=hourOfDay;
                                mMinute=minute;
                                String orario="";
                                if(mHour<10) orario="0"+mHour;
                                else orario = mHour+"";
                                if(mMinute<10) orario+=(":0"+mMinute);
                                else orario+=(":"+mMinute);
                                tvTime.setText(orario);
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.show();
            }
        });


        // Bottone Aggiungi Orario
        Button btnAddHour = (Button)findViewById(R.id.btnAddHour);
        btnAddHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean contiene=false;
                for(int i=0; i<listaOre.size();i++){
                    if(listaOre.get(i)[0]==mHour && listaOre.get(i)[1]==mMinute) contiene=true;
                }
                if(!contiene) listaOre.add(new int[]{mHour,mMinute});
                customAdapter.notifyDataSetChanged();
            }
        });


    }

    // Funzione richiamata quando si clicca sul bottone X di un elemento della lista
    public void eliminaOrario(int i){
        listaOre.remove(i);
        customAdapter.notifyDataSetChanged();
    }


}
