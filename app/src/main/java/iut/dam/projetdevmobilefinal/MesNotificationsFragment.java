package iut.dam.projetdevmobilefinal;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;

import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MesNotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MesNotificationsFragment extends Fragment {

    private final int MAX_CONSO_TiME_SLOT = 2000;

    private List<Time_slot> mesTime_Slot;

    private ListView listView;

    private ArrayList <Integer> mesId;


    private User user;
    boolean getNotif = false;

    public MesNotificationsFragment() {
        // Required empty public constructor
    }

    public static MesNotificationsFragment newInstance(ArrayList<String> notificationMessages, User user,ArrayList <Integer> mesId ) {
        MesNotificationsFragment fragment = new MesNotificationsFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("notificationMessages", notificationMessages);
        args.putSerializable("user", user);
        args.putSerializable("mesId",mesId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ArrayList<String> notificationMessages = getArguments().getStringArrayList("notificationMessages");
        user = (User) getArguments().getSerializable("user");
        mesId = (ArrayList <Integer>) getArguments().getSerializable("mesId");
        View rootView = inflater.inflate(R.layout.fragment_mes_notifications, container, false);

        // Initialize listView
        listView = rootView.findViewById(R.id.notifications);

        NotificationsAdapter adapter = new NotificationsAdapter(requireContext(), notificationMessages, user, mesId);
        listView.setAdapter(adapter);

        return rootView;
    }


    private List<Time_slot> getNotifications(){
        List<Time_slot> time_slot_sature = new ArrayList<>();
        for(int i = 0 ; i < mesTime_Slot.size() ; ++i){
            if(mesTime_Slot.get(i).getWattage() >= MAX_CONSO_TiME_SLOT -200){
                time_slot_sature.add(mesTime_Slot.get(i));
            }
        }
        return time_slot_sature;
    }


}