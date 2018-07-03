package embeddedproject.takethepill;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseHelper;

public class CustomAdapterTherapy extends ArrayAdapter<TherapyEntityDB> {

    private ArrayList<TherapyEntityDB> dataSet;
    Context mContext;
    private boolean[] giorniSelezionati;    // Usata nella selezione dei giorni
    private final String giorni[] = {"Lun", "Mar", "Mer", "Gio", "Ven", "Sab", "Dom"};


    public CustomAdapterTherapy(ArrayList<TherapyEntityDB> data, Context context) {
        super(context, R.layout.row_item_therapy, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.row_item_therapy, null);

        TextView drug = convertView.findViewById(R.id.drugName);
        TextView tvDays = convertView.findViewById(R.id.weekDays);
        TextView hours = convertView.findViewById(R.id.hoursTV);
        ImageView ivNotifica=convertView.findViewById(R.id.ivNotification);

        TherapyEntityDB terapia = getItem(position);

        drug.setText(terapia.getDrug());

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
            tvDays.setText("");
            s.setLength(0);
        } else {
            tvDays.setText(s);
        }

        // TextView Ore
        DatabaseHelper db=new DatabaseHelper(convertView.getContext());
        List<Time> list =db.getTherapyHour(terapia);    // Operazione database Lista delle ore
        String h="";
        for (int i=0; i<list.size();i++){
            if(list.get(i).getHours()<10)h+="0";
            h+=list.get(i).getHours()+":";
            if(list.get(i).getMinutes()<10)h+="0";
            h+=list.get(i).getMinutes();
            if(i!=list.size()-1)h+=", ";
        }
        hours.setText(h);

        if(terapia.getNotify()==-1)ivNotifica.setImageDrawable(convertView.getResources().getDrawable(R.drawable.ic_notifications_none_black_24dp));
        else ivNotifica.setImageDrawable(convertView.getResources().getDrawable(R.drawable.ic_notifications_black_24dp));




        return convertView;
    }

}
