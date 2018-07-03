package embeddedproject.takethepill;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapterHour extends ArrayAdapter<int[]> {


    private ArrayList<int[]> dataSet;
    Context mContext;

    public CustomAdapterHour(ArrayList<int[]> data, Context context) {
        super(context, R.layout.row_item_hour, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.row_item_hour, null);

        final TextView hour = convertView.findViewById(R.id.tvSingleHour);
        ImageButton ibDelete=convertView.findViewById(R.id.ibDeleteHour);

        int[] actual = getItem(position);

        String orario="";
        if(actual[0]<10) orario="0"+actual[0];
        else orario = actual[0]+"";
        if(actual[1]<10) orario+=(":0"+actual[1]);
        else orario+=(":"+actual[1]);
        hour.setText(orario);

        ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Messaggio "SICURO? SI/NO"
                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                builder.setTitle("Sei sicuro di voler eliminare l'orario?");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((AddHourActivity) getContext()).eliminaOrario(position);
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

        return convertView;
    }
}
