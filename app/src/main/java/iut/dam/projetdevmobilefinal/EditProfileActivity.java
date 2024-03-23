package iut.dam.projetdevmobilefinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonSyntaxException;


public class EditProfileActivity extends AppCompatActivity {
    CountDownLatch latch;
    Habitat monHabitat;
    private final int CONSO_CRITIQUE = 1500;


    User user;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        token = getIntent().getStringExtra("TOKEN");
        String email = getIntent().getStringExtra("email");

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();

                if(id == R.id.MonHabitat)
                    loadMonHabitatFragment();
                else if(id == R.id.ListeDesHabitats)
                    loadListeDesHabitatsFragment();
                else if(id == R.id.MesNotifications)
                    loadMesNotificationsFragment();
                else if(id == R.id.LogOut){
                    logOut();
                }
                else if(id == R.id.BoiteDeDialog)
                    boiteDeDialogue();

                drawerLayout.closeDrawers();
                return true;
            }
        });
        latch = new CountDownLatch(2);
        getMyHabitat();
        loadMonHabitatFragment();

    }
    public void getMyUser(){
        String urlString = "http://192.168.1.13/monappli/getMyUser.php?token="+token;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlString)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                user = User.getFromJson(responseBody);
                latch.countDown();
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                latch.countDown();
                loadFailure();
            }
        });
    }

    public void getMyHabitat(){
        String urlString = "http://192.168.1.13/monappli/getMyHabitat.php?token="+token;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlString)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                monHabitat = Habitat.getFromJson(responseBody);
                latch.countDown();
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                loadFailure();
            }
        });
    }
    private void loadMonHabitatFragment (){
        getMyUser();
        try {
            latch.await();
        } catch (InterruptedException e) {
            Toast.makeText(this, "Il se peut que vos eco coin ne soient pas actualisé. Déconnectez vous et reconnectez vous", Toast.LENGTH_LONG).show();
        }
        Bundle bundle = new Bundle();
        Habitat habitat = new Habitat(/* initialisez votre objet Habitat ici */);
        bundle.putSerializable("User", user);
        bundle.putSerializable("Habitat", monHabitat);
        getSupportFragmentManager().popBackStack();
        MonHabitatFragment monHabitatFragment = new MonHabitatFragment();
        monHabitatFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, monHabitatFragment)
                .commit();
    }
    private void loadListeDesHabitatsFragment (){
        getSupportFragmentManager().popBackStack();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ListeDesHabitatsFragment listeDesHabitatsFragment = ListeDesHabitatsFragment.newInstance(token);
        fragmentTransaction.replace(R.id.fragment_container, listeDesHabitatsFragment);
        fragmentTransaction.commit();
    }

    private void loadMesNotificationsFragment (){
        OkHttpClient client = new OkHttpClient();
        String url = "http://192.168.1.13/monappli/getNotifications.php";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        List <Time_slot>  mesTime_Slot = Time_slot.getListFromJson(responseData);
                        mesTime_Slot = getMesTime_Slot(mesTime_Slot);
                        ArrayList <String > notificationMessages = getNotif(mesTime_Slot);
                        ArrayList <Integer> mesId = getIdTimeSlot(mesTime_Slot);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getMyUser();
                                getSupportFragmentManager().popBackStack();
                                MesNotificationsFragment mesNotificationsFragment = MesNotificationsFragment.newInstance(notificationMessages, user, mesId);
                                FragmentManager fragmentManager = getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.fragment_container, mesNotificationsFragment);
                                fragmentTransaction.commit();
                            }
                        });

                    } catch (JsonSyntaxException | IllegalStateException e) {
                        showErrorToast();
                    }
                } else {
                    showErrorToast();
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                showErrorToast();
            }
        });

    }
    private void logOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirmation_logout_message)
                .setPositiveButton(R.string.logOut, (dialog, id) -> {
                    dialog.dismiss();
                    FragmentManager fragmentManager = getSupportFragmentManager();

                    Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
                    if (fragment != null) {
                        // Fermez le fragment
                        fragmentManager.beginTransaction().remove(fragment).commit();
                    }
                    dialog.dismiss(); // Ferme la boîte de dialogue
                    Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    dialog.dismiss(); // Ferme la boîte de dialogue
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void loadFailure(){
        Toast.makeText(getApplicationContext(), R.string.load_failure, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void showErrorToast() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(EditProfileActivity.this, R.string.notifications_error, Toast.LENGTH_LONG).show();
            }
        });
    }
    private ArrayList<Time_slot> getMesTime_Slot(List<Time_slot>mesTime_Slot ){
        ArrayList<Time_slot> time_slot_sature = new ArrayList<>();
        for(int i = 0 ; i < mesTime_Slot.size() ; ++i){
            int wattage = mesTime_Slot.get(i).gettotal_wattage();
            if(wattage >= CONSO_CRITIQUE ){
                time_slot_sature.add(mesTime_Slot.get(i));
            }
        }
        return time_slot_sature;
    }
    private ArrayList<String> getNotif(List<Time_slot>mesTime_Slot){
        ArrayList<String> notificationMessages = new ArrayList<>();
        for (int i = 0; i < mesTime_Slot.size(); i++) {
            notificationMessages.add("Un pic est définie pour le créneau de \n" + mesTime_Slot.get(i).getBegin() + " à " + mesTime_Slot.get(i).getEnd()
                    + " .\n Voulez vous vous engagez à consommer le strict nécessaire durant de ce créneau ?\n" +
                    "Si vous appuyez sur oui, tout vos planifications pour ce créneau seront supprimés.\n Vous pourrez" +
                    "ensuite les reprogrammer dans votre espace 'Mon habitat'. ");
        }
        return notificationMessages;

    }

    private ArrayList<Integer> getIdTimeSlot(List<Time_slot> time_slot){
        ArrayList<Integer> mesId = new ArrayList<>();
        for(int i = 0 ; i < time_slot.size() ; ++i){
            mesId.add(time_slot.get(i).getId());
        }
        return mesId;
    }

    private void boiteDeDialogue(){

        // Création de la boîte de dialogue
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        builder.setMessage(R.string.boiteDeDialogue);

        // Bouton pour fermer la boîte de dialogue
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        // Affichage de la boîte de dialogue
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                TextView messageTextView = dialog.findViewById(android.R.id.message);
                if (messageTextView != null) {
                    messageTextView.setTextColor(0xFFFFD700); // Couleur jaune en format hexadécimal
                }
            }
        });
        dialog.show();
    }
}