package iut.dam.projetdevmobilefinal;

import android.app.Activity;
import android.content.Context;
import android.provider.CallLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class HabitatAdapter extends ArrayAdapter<Habitat> {

    Context context;
    int itemResourceId;
    List<Habitat> items;

    List<User> users;

    public HabitatAdapter(Context context,
                          int itemResourceId,
                          List<Habitat> items, List <User> users) {
        super(context, itemResourceId, items);

        this.context = context;
        this.itemResourceId = itemResourceId;
        this.items = items;
        this.users=users;
    }

    @Override
    public View getView(int position,
                        View convertView,
                        ViewGroup parent) {
        Log.d("HabitatAdapter", "getView called for position: " + position);

        View layout = convertView;
        if (convertView ==  null) {
            //LayoutInflater inflater = activity.getLayoutInflater();
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(itemResourceId, parent, false);
        }
        Habitat H = items.get(position);
        int numbermACHINES = H.getAppliances().size();

        TextView applianceTV = layout.findViewById(R.id.applianceTV);
        TextView floor = layout.findViewById(R.id.numberFloorTV);
        TextView textFloor = layout.findViewById(R.id.textFloorTV);
        TextView residentTV = layout.findViewById(R.id.residentTV);
        ImageView aspirateurIV = layout.findViewById(R.id.aspirateurIV);
        ImageView ferARepasserIV = layout.findViewById(R.id.ferARepasserIV);
        ImageView climatiseurIV = layout.findViewById(R.id.climatiseurIV);
        ImageView machinelaverIV = layout.findViewById(R.id.machinelaverIV);
        ImageView radiateurIV = layout.findViewById(R.id.radiateurIV);
        ImageView televisionIV = layout.findViewById(R.id.televisionIV);
        aspirateurIV.setVisibility(View.GONE);
        ferARepasserIV.setVisibility(View.GONE);
        machinelaverIV.setVisibility(View.GONE);
        climatiseurIV.setVisibility(View.GONE);
        radiateurIV.setVisibility(View.GONE);
        televisionIV.setVisibility(View.GONE);

        for(Appliance A : H.getAppliances()){
            if (A.getName().equals("Aspirateur")){
                aspirateurIV.setVisibility(View.VISIBLE);
            }
            else if (A.getName().equals("FerARepasser")){
                ferARepasserIV.setVisibility(View.VISIBLE);
            }
            else if(A.getName().equals("MachineALaver")){
                machinelaverIV.setVisibility(View.VISIBLE);
            }else if(A.getName().equals("Climatiseur")){
                climatiseurIV.setVisibility(View.VISIBLE);
            }else if(A.getName().equals("Radiateur")){
                radiateurIV.setVisibility(View.VISIBLE);
            }else if(A.getName().equals("Television")){
                televisionIV.setVisibility(View.VISIBLE);
            }
        }

        for(User u : users){
            if(u.getHabitat_id()==position+1){
                String nom = u.getFirstname()+" "+u.getLastname();
                residentTV.setText(nom);
            }
        }
        floor.setText(H.getFloor());
        applianceTV.setText(numbermACHINES + " equimements");
        return layout;
    }

}
