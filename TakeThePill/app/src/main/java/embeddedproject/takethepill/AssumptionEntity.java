package embeddedproject.takethepill;


import android.util.Log;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AssumptionEntity {

    // VARIABILI
    private Date data;
    private Time ora;
    private String nomeFarmaco;
    private Boolean stato;
    private Integer dosaggio;
    private String tipoFarmaco;
    private Integer terapia;

    // costruttore per assunzioni DI SOLA LETTURA
    public AssumptionEntity(Date data, Time ora, String nomeFarmaco, Boolean stato, Integer dosaggio,String tipoFarmaco, int terapia){
        this.data=data;
        this.ora=ora;
        this.nomeFarmaco=nomeFarmaco;
        this.stato=stato;
        this.dosaggio=dosaggio;
        this.tipoFarmaco=tipoFarmaco;
        this.terapia=terapia;
    }
    //costruttore per database
    public AssumptionEntity(Date data, Time ora, int terapia, boolean stato)
    {
        this.data=data;
        this.ora=ora;
        this.terapia=terapia;
        this.stato=stato;
    }
    // Costruttore generico
     AssumptionEntity(){}


    //METODI GET e SET

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }
    public int getTerapia(){return terapia;}
    public void setTerapia(Integer terapia){this.terapia=terapia;}
    public Time getOra() {
        return ora;
    }

    public void setOra(Time ora) {
        this.ora = ora;
    }

    public String getNomeFarmaco() {
        return nomeFarmaco;
    }

    public void setNomeFarmaco(String nomeFarmaco) {
        this.nomeFarmaco = nomeFarmaco;
    }

    public Boolean getStato() {
        return stato;
    }

    public void setStato(Boolean stato) {
        this.stato = stato;
    }

    public Integer getDosaggio() {
        return dosaggio;
    }

    public void setDosaggio(Integer dosaggio) {
        this.dosaggio = dosaggio;
    }

    public String getTipoFarmaco() {
        return tipoFarmaco;
    }

    public void setTipoFarmaco(String tipoFarmaco) {
        this.tipoFarmaco = tipoFarmaco;
    }



     List<AssumptionEntity> generateAssumption(TherapyEntityDB th, Time hour,Calendar dataInizio){

        if(th.getDays()==null){
            return null;
        }

        List<AssumptionEntity> list= new ArrayList<>();
        Calendar calendar= Calendar.getInstance();;
        if(dataInizio!=null) calendar = dataInizio;
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        // torno indietro di un giorno per garantire la correttezza del ciclo
        calendar.add(Calendar.DAY_OF_MONTH,-1);


        int count;
        // Se si ha la data di fine, ricavo i giorni di differenza con oggi
        if(th.getDays()==-2){
            long diff = th.getDateEnd().getTime()-calendar.getTime().getTime();
            long giorni = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            Log.d("generateAssumption(): Data Fine",th.getDateEnd().toString());
            Log.d("generateAssumption(): Data Inizio",calendar.getTime().toString());
            Log.d("generateAssumption(): n Giorni",giorni+"");
            count= (int)giorni;
        } else if(th.getDays()==-1)count=10;
        else count=th.getDays();

        while(count>0){
            //scorro il calendario un giorno alla volta
            calendar.add(Calendar.DAY_OF_MONTH,1);
            String data=calendar.getTime().toString();
            Log.d("data processata",data);

            //1=sunday,..7=saturday
            int day= calendar.get(Calendar.DAY_OF_WEEK);

            Log.d("day found",day+"");

            //compare day found and check if it's a day therapy,then add assumption
            if(day==1 && th.isSun())
            {
                AssumptionEntity current= new AssumptionEntity(calendar.getTime(),hour,th.getID(),false);
                list.add(current);
                count--;
                continue;
            }
            if(day==2 && th.isMon())
            {
                AssumptionEntity current= new AssumptionEntity(calendar.getTime(),hour,th.getID(),false);
                list.add(current);
                count--;
                continue;
            }
            if(day==3 && th.isTue())
            {
                AssumptionEntity current= new AssumptionEntity(calendar.getTime(),hour,th.getID(),false);
                list.add(current);
                count--;
                continue;
            }
            if(day==4 && th.isWed())
            {
                AssumptionEntity current= new AssumptionEntity(calendar.getTime(),hour,th.getID(),false);
                list.add(current);
                count--;
                continue;
            }
            if(day==5 && th.isThu())
            {
                AssumptionEntity current= new AssumptionEntity(calendar.getTime(),hour,th.getID(),false);
                list.add(current);
                count--;
                continue;
            }
            if(day==6 && th.isFri())
            {
                AssumptionEntity current= new AssumptionEntity(calendar.getTime(),hour,th.getID(),false);
                list.add(current);
                count--;
                continue;
            }
            if(day==7 && th.isSat())
            {
                AssumptionEntity current= new AssumptionEntity(calendar.getTime(),hour,th.getID(),false);
                list.add(current);
                count--;

            }



        }



        return list;
    }


}
