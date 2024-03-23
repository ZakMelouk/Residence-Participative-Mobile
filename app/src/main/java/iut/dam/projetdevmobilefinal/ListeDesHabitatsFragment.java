package iut.dam.projetdevmobilefinal;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListeDesHabitatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class ListeDesHabitatsFragment extends Fragment {
    List<Habitat> habitats;
    List<User> users;

    private static final String token = "param1";

    private String token_;

    CountDownLatch latch;

    public ListeDesHabitatsFragment() {
        // Required empty public constructor
    }

    public static ListeDesHabitatsFragment newInstance(String param1) {
        ListeDesHabitatsFragment fragment = new ListeDesHabitatsFragment();
        Bundle args = new Bundle();
        args.putString(token, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            token_ = getArguments().getString(token);
        }

        latch = new CountDownLatch(2);
        getHabitats();
        getUsers();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_liste_des_habitats, container, false);

        // Assurez-vous que R.id.habitatLV est l'ID de votre ListView dans fragment_layout.xml

        return inflater.inflate(R.layout.fragment_liste_des_habitats, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ListView lv = view.findViewById(R.id.habitatLV);
        HabitatAdapter adapter = new HabitatAdapter(requireContext(), R.layout.item_habitat, habitats, users);
        lv.setAdapter(adapter);
        super.onViewCreated(view, savedInstanceState);
    }

    private void closeFragment(){
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();

    }

    private void getUsers(){
        String urlString = "http://192.168.1.13/monappli/getUsers.php";
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(urlString)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody2 = response.body().string();
                users = User.getListFromJson(responseBody2);
                latch.countDown();
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(requireContext(), R.string.liste_habitat_loading_failure, Toast.LENGTH_LONG).show();
                        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, new MonHabitatFragment());
                        transaction.addToBackStack(null);  // Ajoutez ce fragment à la pile de retour arrière
                        transaction.commit();
                        closeFragment();
                    }
                });

            }
        });
    }

    private void getHabitats(){
        String urlString = "http://192.168.1.13/monappli/getHabitats.php?token="+token_;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(urlString)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                habitats = Habitat.getListFromJson(responseBody);
                latch.countDown();
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(requireContext(), R.string.liste_habitat_loading_failure, Toast.LENGTH_LONG).show();
                        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, new MonHabitatFragment());
                        transaction.addToBackStack(null);  // Ajoutez ce fragment à la pile de retour arrière
                        transaction.commit();
                        closeFragment();
                    }
                });
            }
        });
    }

}