package iut.dam.projetdevmobilefinal;

import static java.lang.Integer.parseInt;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.gson.JsonSyntaxException;
import com.stacktips.view.CalendarListener;
import com.stacktips.view.CustomCalendarView;
import com.stacktips.view.DayDecorator;
import com.stacktips.view.DayView;
import com.stacktips.view.utils.CalendarUtils;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MonHabitatFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class MonHabitatFragment extends Fragment {
    private Habitat monHabitat;
    private User monUser;
    private CustomCalendarView customCalendarView;
    private List<Time_slot> mesTime_Slot;
    private static final int MAX_VALUE = 1500;
    private ArrayList<String> equipmentTypeNames;

    private final int MAX_CONSO_RESIDENCE_PAR_JOUR = 5000;

    private final int MAX_CONSO_TiME_SLOT = 2000;
    private int valeurCalculee = 0;
    private ProgressBar progressBar;
    private Handler handler;
    private static final int PROGRESS_UPDATE_INTERVAL = 100; // en millisecondes
    private ArrayAdapter<String> adapter;
    private EditText reference;
    CountDownLatch latch;

    private ImageView aspirateurIV ;
    private ImageView ferARepasserIV;
    private ImageView climatiseurIV ;
    private ImageView machinelaverIV ;
    private ImageView televisionIV;
    private EditText wattage;
    private ImageView radiateurIV;
    private TextView textViewProgress;

    public static MonHabitatFragment newInstance(String param1, String param2) {
        MonHabitatFragment fragment = new MonHabitatFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MonHabitatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        latch = new CountDownLatch(1);
        handler = new Handler();

        Bundle bundle = getArguments();
        if (bundle != null) {
            User user = (User) bundle.getSerializable("User");
            Habitat habitat = (Habitat)bundle.getSerializable("Habitat");
            if (user != null && habitat != null) {
                this.monHabitat=habitat;
                this.monUser=user;
            }
            else{
                loadFailure();
            }
        }
        else{
            loadFailure();
        }



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mon_habitat, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        textViewProgress = view.findViewById(R.id.textViewProgress);
        aspirateurIV = view.findViewById(R.id.imageViewAspirateur);
        ferARepasserIV = view.findViewById(R.id.imageFer);
        climatiseurIV = view.findViewById(R.id.imageViewClimatiseur);
        machinelaverIV = view.findViewById(R.id.imageViewMachineaLaver);
        televisionIV = view.findViewById(R.id.imageViewTelevision);
        radiateurIV = view.findViewById(R.id.imageViewRadiateur);
        reference = view.findViewById(R.id.editTextReference);
        wattage = view.findViewById(R.id.editTextConsumption);
        Button buttonAjouterEquipement = view.findViewById(R.id.ButtonAjouterEquipement);
        aspirateurIV.setVisibility(View.GONE);
        ferARepasserIV.setVisibility(View.GONE);
        machinelaverIV.setVisibility(View.GONE);
        climatiseurIV.setVisibility(View.GONE);
        televisionIV.setVisibility(View.GONE);
        radiateurIV.setVisibility(View.GONE);
        buttonAjouterEquipement.setBackgroundColor(Color.parseColor("#FFD700"));

        TextView ecoCoin = view.findViewById(R.id.ecoCoin);
        customCalendarView = (CustomCalendarView) view.findViewById(R.id.calendar_view);

        //Initialize calendar with date
        customCalendarView = (CustomCalendarView) view.findViewById(R.id.calendar_view);

        //Initialize calendar with date
        Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());

        //Show monday as first date of week
        customCalendarView.setFirstDayOfWeek(Calendar.MONDAY);

        //Show/hide overflow days of a month
        customCalendarView.setShowOverflowDate(false);

        //call refreshCalendar to update calendar the view
        customCalendarView.refreshCalendar(currentCalendar);

        //Handling custom calendar events
        customCalendarView.setCalendarListener(new CalendarListener() {
            @Override
            public void onDateSelected(Date date) {
                if (!CalendarUtils.isPastDay(date)) {
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                } else {
                }
            }
            @Override
            public void onMonthChanged(Date date) {
                SimpleDateFormat df = new SimpleDateFormat("MM-yyyy");
            }
        });

        requestCalendar(currentCalendar);

        for(Appliance appliance : monHabitat.getAppliances()){
            if (appliance.getName().contentEquals(getText(R.string.aspirateur)))
                aspirateurIV.setVisibility(View.VISIBLE);

            else if (appliance.getName().contentEquals(getText(R.string.ferARepasser)))
                ferARepasserIV.setVisibility(View.VISIBLE);

            else if (appliance.getName().contentEquals(getText(R.string.machineALaver)))
                machinelaverIV.setVisibility(View.VISIBLE);

            else if (appliance.getName().contentEquals(getText(R.string.climatiseur)))
                climatiseurIV.setVisibility(View.VISIBLE);

            else if (appliance.getName().contentEquals(getText(R.string.television)))
                televisionIV.setVisibility(View.VISIBLE);

            else if (appliance.getName().contentEquals(getText(R.string.radiateur)))
                radiateurIV.setVisibility(View.VISIBLE);
        }
        updateProgressBar();
        textViewProgress.setText("0" + " / " + MAX_VALUE);

        Spinner spinnerEquipment = view.findViewById(R.id.spinnerEquipment);
        Appliance.EquipmentType[] equipmentTypes = Appliance.EquipmentType.values();
        equipmentTypeNames = new ArrayList<>();
        equipmentTypeNames.add("");
        if(monHabitat.getAppliances().size()==0){
            for(int i = 0 ; i < equipmentTypes.length;++i){
                equipmentTypeNames.add(equipmentTypes[i].name());
            }
        }
        else {
            for (int i = 0; i < equipmentTypes.length; ++i) {
                int valide = 0;
                for (int j = 0; j < monHabitat.getAppliances().size(); ++j) {
                    if (!(monHabitat.getAppliances().get(j).getName().equals(equipmentTypes[i].name())))
                        valide++;
                    if (valide == monHabitat.getAppliances().size())
                        equipmentTypeNames.add(equipmentTypes[i].name());
                }
            }
        }

        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, equipmentTypeNames);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerEquipment.setAdapter(adapter);

        spinnerEquipment.setSelection(0, false);
        buttonAjouterEquipement.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(reference.getText().toString().trim().isEmpty() || wattage.getText().toString().isEmpty()){
                    Toast.makeText(requireContext(), R.string.empty_field, Toast.LENGTH_LONG).show();
                    return;
                }
                String equipement = spinnerEquipment.getSelectedItem().toString();
                if(equipement.equals("")){
                    Toast.makeText(requireContext(),"Veuillez selectionner un équipement", Toast.LENGTH_LONG).show();
                    return;
                }
                buttonAjouterEquipement.setEnabled(false);
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url("http://192.168.1.13/monappli/insertAppliance.php?name="+equipement+"&reference="+reference.getText().toString()+"&wattage="+Integer.parseInt(wattage.getText().toString())+"&habitat_id="+monHabitat.getId()).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String message="";
                        int appliance_id = 10;
                        try{
                            String responseBody = response.body().string();
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            message = jsonResponse.getString("message");
                            appliance_id = jsonResponse.getInt("appliance_id");
                        }
                        catch(JSONException e){
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(requireContext(), R.string.failure_add_equipment, Toast.LENGTH_LONG).show();
                                }
                            });
                            return;
                        }
                        if (!message.equals(getString(R.string.appliance_inserted_successfully))) {
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(requireContext(), R.string.failure_add_equipment, Toast.LENGTH_LONG).show();
                                }
                            });
                            return;
                        }

                        int finalAppliance_id = appliance_id;
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < equipmentTypeNames.size(); ++i) {
                                    if (equipmentTypeNames.get(i).equals(equipement)) {
                                        equipmentTypeNames.remove(i);
                                        break;
                                    }
                                }
                                if (equipement.contentEquals(getText(R.string.aspirateur))) {
                                    aspirateurIV.setVisibility(View.VISIBLE);
                                    monHabitat.setAppliances(finalAppliance_id,equipement,reference.getText().toString(),wattage.getText().toString());
                                } else if (equipement.contentEquals(getText(R.string.ferARepasser))) {
                                    ferARepasserIV.setVisibility(View.VISIBLE);
                                    monHabitat.setAppliances(finalAppliance_id,equipement,reference.getText().toString(),wattage.getText().toString());
                                } else if (equipement.contentEquals(getText(R.string.machineALaver))) {
                                    monHabitat.setAppliances(finalAppliance_id,equipement,reference.getText().toString(),wattage.getText().toString());
                                    machinelaverIV.setVisibility(View.VISIBLE);
                                } else if (equipement.contentEquals(getText(R.string.climatiseur))) {
                                    climatiseurIV.setVisibility(View.VISIBLE);
                                    monHabitat.setAppliances(finalAppliance_id,equipement,reference.getText().toString(),wattage.getText().toString());
                                } else if (equipement.contentEquals(getText(R.string.television))) {
                                    televisionIV.setVisibility(View.VISIBLE);
                                    monHabitat.setAppliances(finalAppliance_id,equipement,reference.getText().toString(),wattage.getText().toString());
                                } else if (equipement.contentEquals(getText(R.string.radiateur))) {
                                    monHabitat.setAppliances(finalAppliance_id,equipement,reference.getText().toString(),wattage.getText().toString());
                                    radiateurIV.setVisibility(View.VISIBLE);
                                }
                                adapter.notifyDataSetChanged();
                                updateProgressBar();
                                Toast.makeText(requireContext(), R.string.appliance_inserted_successfully, Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(requireContext(), R.string.failure_add_equipment, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
                buttonAjouterEquipement.setEnabled(true);
            }
        });
        aspirateurIV.setOnClickListener(v -> showEquipmentDialog(getString(R.string.aspirateur)));
        climatiseurIV.setOnClickListener(v -> showEquipmentDialog(getString(R.string.climatiseur)));
        radiateurIV.setOnClickListener(v -> showEquipmentDialog(getString(R.string.radiateur)));
        ferARepasserIV.setOnClickListener(v -> showEquipmentDialog(getString(R.string.ferARepasser)));
        machinelaverIV.setOnClickListener(v -> showEquipmentDialog(getString(R.string.machineALaver)));
        televisionIV.setOnClickListener(v -> showEquipmentDialog(getString(R.string.television)));
        ecoCoin.setText(monUser.getEco_coin()+"");

        return view;
    }
    private void updateProgressBar() {
        valeurCalculee = 0;
        for (int i = 0; i < monHabitat.getAppliances().size(); ++i) {
            valeurCalculee += parseInt(monHabitat.getAppliances().get(i).getWattage());
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                int currentProgress = progressBar.getProgress();

                while (valeurCalculee < currentProgress) {
                    valeurCalculee += 10;
                    currentProgress = valeurCalculee;

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            updateUI();
                        }
                    });

                    try {
                        Thread.sleep(PROGRESS_UPDATE_INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                while (valeurCalculee > currentProgress) {
                    valeurCalculee -= 10;
                    currentProgress = valeurCalculee;

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            updateUI();
                        }
                    });

                    try {
                        Thread.sleep(PROGRESS_UPDATE_INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }            }
        }).start();
    }

    private void updateUI() {
        textViewProgress.setText(valeurCalculee + " / " + MAX_VALUE);
        progressBar.setProgress(valeurCalculee);
    }
    private class CustomDayDecorator implements DayDecorator {
        private Map<LocalDate, Integer> dateColorMap;  // Liste des dates à colorier

        // Constructeur pour initialiser les dates à colorier
        public CustomDayDecorator(Map<LocalDate, Integer> dateColorMap) {
            this.dateColorMap = dateColorMap;
        }

        @Override
        public void decorate(DayView dayView) {
            Date currentDate = dayView.getDate();
            LocalDate date = new LocalDate(currentDate);
            boolean dateTrouvee = false;
            if(dateColorMap.containsKey(date)){
                int nb = dateColorMap.get(date);
                int pourcentage = (nb * 100) / MAX_CONSO_RESIDENCE_PAR_JOUR;
                if(pourcentage <= 30)
                    dayView.setBackgroundColor(Color.parseColor("#009933"));
                else if(pourcentage <= 70)
                    dayView.setBackgroundColor(Color.parseColor("#ffad33"));
                else
                    dayView.setBackgroundColor(Color.parseColor("#e62e00"));
            }
            else
                dayView.setBackgroundColor(Color.parseColor("#009933"));

        }
    }



    public Map<LocalDate, Integer> setCouleurDate(List<Time_slot> times_slots){
        Time_slot.setDate(times_slots);
        Map<LocalDate, Integer> groupByStartDateResult = Time_slot.groupAndSumWattage(times_slots, true);
        return groupByStartDateResult;
    }
    private void loadFailure() {
        Toast.makeText(requireContext(), R.string.load_failure, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void requestCalendar(Calendar currentCalendar){
        String getTimeSlot = "http://192.168.1.13/monappli/getTimeSlot.php";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getTimeSlot)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                try{
                    mesTime_Slot = Time_slot.getListFromJson(responseBody);
                }catch (JsonSyntaxException | IllegalStateException e) {
                    mesTime_Slot = new ArrayList<>();
                }
                Map<LocalDate, Integer> groupByStartDateResult = setCouleurDate(mesTime_Slot);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<DayDecorator> decorators = new ArrayList<>();
                        decorators.add(new CustomDayDecorator(groupByStartDateResult));  // Remplacez datesToColor par votre liste de dates à colorier
                        customCalendarView.setDecorators(decorators);
                        customCalendarView.refreshCalendar(currentCalendar);
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(requireContext(), R.string.calendar_loading_failure, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
    private void showEquipmentDialog(String equipmentName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.popup_layout, null);
        builder.setView(dialogView);

        // Récupérez les éléments de la boîte de dialogue personnalisée
        TextView textView1 = dialogView.findViewById(R.id.textView1);
        TextView textView2 = dialogView.findViewById(R.id.textView2);
        TextView textView3 = dialogView.findViewById(R.id.textView3);
        Button button1 = dialogView.findViewById(R.id.button1);
        Button button2 = dialogView.findViewById(R.id.button2);
        Button button3 = dialogView.findViewById(R.id.button3);
        textView3.setText(equipmentName);
        button1.setBackgroundColor(Color.parseColor("#FFD700"));
        button2.setBackgroundColor(Color.parseColor("#FFD700"));
        button3.setBackgroundColor(Color.parseColor("#FFD700"));
        // Recherchez l'appareil dans monHabitat
        for (int i = 0; i < monHabitat.getAppliances().size(); ++i) {
            if (monHabitat.getAppliances().get(i).getName().equals(equipmentName)) {
                textView1.setText("Référence: " + monHabitat.getAppliances().get(i).getRef());
                textView2.setText("Consommation: " + monHabitat.getAppliances().get(i).getWattage());
                break;
            }
        }

        button2.setOnClickListener(v -> {
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View dialogView1 = inflater.inflate(R.layout.dialog_planification_layout, null);

            // Créez une boîte de dialogue
            AlertDialog.Builder builder1 = new AlertDialog.Builder(requireContext());
            builder1.setView(dialogView1);
            DatePicker firstDatePicker = dialogView1.findViewById(R.id.datePickerStart);
            DatePicker secondDatePicker = dialogView1.findViewById(R.id.datePickerEnd);

            NumberPicker hourPickerStart = dialogView1.findViewById(R.id.hourPickerStart);
            NumberPicker hourPickerEnd = dialogView1.findViewById(R.id.hourPickerEnd);
            NumberPicker minutePickerStart = dialogView1.findViewById(R.id.minutePickerStart);
            NumberPicker minutePickerEnd = dialogView1.findViewById(R.id.minutePickerEnd);
            Button buttonPlanifier = dialogView1.findViewById(R.id.buttonPlanifier);
            buttonPlanifier.setBackgroundColor(Color.parseColor("#FFD700"));

            hourPickerStart.setMinValue(0);
            hourPickerStart.setMaxValue(23);
            minutePickerStart.setMinValue(0);
            minutePickerStart.setMaxValue(59);

            hourPickerEnd.setMinValue(0);
            hourPickerEnd.setMaxValue(23);
            minutePickerEnd.setMinValue(0);
            minutePickerEnd.setMaxValue(59);
            hourPickerEnd.setValue(1);
            String[] displayedHours = new String[24];
            for (int i = 0; i < 24; i++) {
                displayedHours[i] = String.format("%02d", i); // Ajoute un zéro précédent si nécessaire
            }
            hourPickerStart.setDisplayedValues(displayedHours);
            hourPickerEnd.setDisplayedValues(displayedHours);

// Définir le format pour afficher les minutes avec des zéros précédents
            String[] displayedMinutes = new String[60];
            for (int i = 0; i < 60; i++) {
                displayedMinutes[i] = String.format("%02d", i); // Ajoute un zéro précédent si nécessaire
            }
            minutePickerStart.setDisplayedValues(displayedMinutes);
            minutePickerEnd.setDisplayedValues(displayedMinutes);

            minutePickerEnd.setEnabled(false);
            minutePickerStart.setOnValueChangedListener((picker, oldVal, newVal) -> {
                minutePickerEnd.setValue((newVal) % 60);
            });

            hourPickerEnd.setEnabled(false);
            hourPickerStart.setOnValueChangedListener((picker, oldVal, newVal) -> {
                hourPickerEnd.setValue((newVal + 1) % 24);
            });
            secondDatePicker.setEnabled(false);
            hourPickerEnd.setEnabled(false);

            firstDatePicker.init(
                    firstDatePicker.getYear(),
                    firstDatePicker.getMonth(),
                    firstDatePicker.getDayOfMonth(),
                    new DatePicker.OnDateChangedListener() {
                        @Override
                        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            secondDatePicker.updateDate(year, monthOfYear, dayOfMonth);
                        }
                    }
            );

            // Ajoutez des actions (le cas échéant)
            // builder.setPositiveButton("OK", (dialog, which) -> { /* Logique de bouton OK */ });
            // builder.setNegativeButton("Annuler", (dialog, which) -> { /* Logique de bouton Annuler */ });

            // Affichez la boîte de dialogue
            AlertDialog dialog = builder1.create();
            dialog.show();
            Button buttonAnnuler = dialogView1.findViewById(R.id.buttonCancel);
            buttonAnnuler.setBackgroundColor(Color.parseColor("#FFD700"));
            buttonAnnuler.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss(); // Ferme la boîte de dialogue
                }
            });

            buttonPlanifier.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar startCalendar = Calendar.getInstance();
                    startCalendar.set(Calendar.YEAR, firstDatePicker.getYear());
                    startCalendar.set(Calendar.MONTH, firstDatePicker.getMonth());
                    startCalendar.set(Calendar.DAY_OF_MONTH, firstDatePicker.getDayOfMonth());
                    startCalendar.set(Calendar.HOUR_OF_DAY, hourPickerStart.getValue());
                    startCalendar.set(Calendar.MINUTE, minutePickerStart.getValue());
                    startCalendar.set(Calendar.SECOND, 0); // Seconde automatique 00

                    Calendar endCalendar = Calendar.getInstance();
                    endCalendar.set(Calendar.YEAR, secondDatePicker.getYear());
                    endCalendar.set(Calendar.MONTH, secondDatePicker.getMonth());
                    endCalendar.set(Calendar.DAY_OF_MONTH, secondDatePicker.getDayOfMonth());
                    endCalendar.set(Calendar.HOUR_OF_DAY, hourPickerEnd.getValue());
                    endCalendar.set(Calendar.MINUTE, minutePickerEnd.getValue());
                    endCalendar.set(Calendar.SECOND, 0); // Seconde automatique 00

                    Date startDate = startCalendar.getTime();
                    Date endDate = endCalendar.getTime();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formattedStartDate = dateFormat.format(startDate);
                    String formattedEndDate = dateFormat.format(endDate);
                    //String formattedStartDate = string.replace("-", "/");
                    //String formattedEndDate = string.replace("-", "/");

                    Appliance monAppliance = null;
                    for(int i = 0 ; i < monHabitat.getAppliances().size() ; ++i){
                        if(equipmentName.equals(monHabitat.getAppliances().get(i).getName()))
                            monAppliance = monHabitat.getAppliances().get(i);
                    }
                    String urlString = "http://192.168.1.13/monappli/insertAppliance_Time_Slot.php?appliance_id=" + monAppliance.getId() + "&start_date_str=" + formattedStartDate + "&end_date_str=" + formattedEndDate + "&max_wattage=" + MAX_CONSO_TiME_SLOT;
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(urlString)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            String responseBody = response.body().string();
                            try {
                                JSONObject jsonObject = new JSONObject(responseBody);
                                String message = jsonObject.getString("message");

                                if (message.contains("successfully")) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getActivity().getApplicationContext(), R.string.appliance_time_slot_sucess, Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    });

                                } else {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getActivity().getApplicationContext(), R.string.appliance_time_slot_error, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            } catch (JSONException e) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity().getApplicationContext(), R.string.appliance_time_slot_error, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }

                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getActivity().getApplicationContext(), R.string.appliance_time_slot_error_serveur, Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                }
            });
        });

        button3.setOnClickListener(v -> {
            requeteSupprimerEquipement(equipmentName);
        });



        AlertDialog dialog = builder.create();
        dialog.show();
        button1.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    private void requeteSupprimerEquipement(String equipmentName){
        String deleteEquipment = "http://192.168.1.13/monappli/deleteAppliance.php?habitat_id="+monHabitat.getId()+"&name="+equipmentName;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(deleteEquipment)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String message="";
                try{
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    message = jsonResponse.getString("message");
                }
                catch(JSONException e){
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), R.string.failure_delete_equipment, Toast.LENGTH_LONG).show());
                    return;
                }
                if (!message.equals(getString(R.string.appliance_deleted_successfully))) {
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), R.string.failure_add_equipment, Toast.LENGTH_LONG).show());
                    return;
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        equipmentTypeNames.add(equipmentName);

                        if (equipmentName.contentEquals(getText(R.string.aspirateur))) {
                            aspirateurIV.setVisibility(View.GONE);
                            monHabitat.deleteAppliance(equipmentName);
                        } else if (equipmentName.toString().contentEquals(getText(R.string.ferARepasser))) {
                            ferARepasserIV.setVisibility(View.GONE);
                            monHabitat.deleteAppliance(equipmentName);
                        } else if (equipmentName.contentEquals(getText(R.string.machineALaver))) {
                            monHabitat.deleteAppliance(equipmentName);
                            machinelaverIV.setVisibility(View.GONE);
                        } else if (equipmentName.contentEquals(getText(R.string.climatiseur))) {
                            climatiseurIV.setVisibility(View.GONE);
                            monHabitat.deleteAppliance(equipmentName);
                        } else if (equipmentName.contentEquals(getText(R.string.television))) {
                            televisionIV.setVisibility(View.GONE);
                            monHabitat.deleteAppliance(equipmentName);
                        } else if (equipmentName.contentEquals(getText(R.string.radiateur))) {
                            monHabitat.deleteAppliance(equipmentName);
                            radiateurIV.setVisibility(View.GONE);
                        }
                        adapter.notifyDataSetChanged();
                        updateProgressBar();
                        Toast.makeText(requireContext(), R.string.appliance_deleted_successfully, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(requireContext(), R.string.failure_delete_equipment, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

}
