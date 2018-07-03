package embeddedproject.takethepill;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import database.DatabaseHelper;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TherapyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TherapyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TherapyFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    ArrayList<TherapyEntityDB> listaTerapie;
    CustomAdapterTherapy customAdapter;
    ListView listView;
    DatabaseHelper db;

    public TherapyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TherapyFragment.
     */

    public static TherapyFragment newInstance(String param1, String param2) {
        TherapyFragment fragment = new TherapyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_therapy, container, false);

        // BOTTONE AGGIUNGI TERAPIA
        FloatingActionButton fab = view.findViewById(R.id.fab_therapy);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddEditTherapyActivity.class);
                intent.putExtra("nuova",true);
                intent.putExtra("id","");
                startActivity(intent);
            }
        });

        // LISTA DELLE  TERAPIE
        listView =  view.findViewById(R.id.listTherapies);

        listaTerapie = new ArrayList<>();
        db=new DatabaseHelper(getContext());
        listaTerapie=(ArrayList<TherapyEntityDB>) db.getAllTherapies();

        customAdapter = new CustomAdapterTherapy(listaTerapie, getContext());
        listView.setAdapter(customAdapter);

        // Quando si clicca su un elemento
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TherapyEntityDB terapia= listaTerapie.get(position);
                Integer idTerapia=terapia.getID();

                Intent intent = new Intent(view.getContext(), AddEditTherapyActivity.class);
                intent.putExtra("nuova",false);
                intent.putExtra("id",idTerapia);
                startActivity(intent);
            }
        });

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        listaTerapie.clear();
        listaTerapie.addAll( db.getAllTherapies());
        customAdapter.notifyDataSetChanged();
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }

}
