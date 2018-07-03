package embeddedproject.takethepill;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CustomAdapterMain extends ArrayAdapter<AssumptionEntity> {

    private ArrayList<AssumptionEntity> dataSet;
    Context mContext;

    public CustomAdapterMain(ArrayList<AssumptionEntity> data, Context context) {
        super(context, R.layout.row_item_assumption, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.row_item_assumption, null);

        TextView drug = convertView.findViewById(R.id.drug);
        TextView hour = convertView.findViewById(R.id.hour);
        ImageView ivStato =convertView.findViewById(R.id.ivStato);
        TextView tvDosaggio = convertView.findViewById(R.id.tvAssumpQuantity);

        AssumptionEntity actual = getItem(position);
        drug.setText(actual.getNomeFarmaco());

        if(actual.getStato()) ivStato.setVisibility(View.INVISIBLE);
        else ivStato.setVisibility(View.VISIBLE);

        final DateFormat formatter = new SimpleDateFormat("HH:mm");
        hour.setText(formatter.format(actual.getOra()));

        tvDosaggio.setText(actual.getDosaggio().toString()+" "+actual.getTipoFarmaco());

        return convertView;
    }

}
