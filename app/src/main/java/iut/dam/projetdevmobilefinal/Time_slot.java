package iut.dam.projetdevmobilefinal;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.joda.time.LocalDate;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Time_slot {

    @SerializedName("id")
    private int id;
    @SerializedName("wattage")
    private int wattage;

    @SerializedName("max_wattage")
    private int maxWattage;

    @SerializedName("begin")
    private String begin;

    private int total_wattage;

    @SerializedName("end")
    private String end;

    private Date dateDebut;
    private Date dateFin;

    public int getWattage() {
        return wattage;
    }

    public int getMaxWattage() {
        return maxWattage;
    }

    public int gettotal_wattage(){return total_wattage;}

    public String getBegin() {
        return begin;
    }

    public String getEnd() {
        return end;
    }

    public static Date parseDateString(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Time_slot getFromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Time_slot.class);
    }

    public static List<Time_slot> getListFromJson(String json) {
        Type type = new TypeToken<List<Time_slot>>(){}.getType();
        Gson gson = new Gson();
        return gson.fromJson(json, type);
    }

    public void setDateDebut(Date d){
        dateDebut = d;
    }
    public void setDateFin(Date d){
        dateFin = d;
    }
    public LocalDate getDateDebut() {
        return new LocalDate(dateDebut);
    }

    public LocalDate getDateFin() {
        return new LocalDate(dateFin);
    }
    public static Map<LocalDate, Integer> groupAndSumWattage(List<Time_slot> timeSlots, boolean groupByStartDate) {
        return timeSlots.stream()
                .collect(Collectors.groupingBy(
                        groupByStartDate ? Time_slot::getDateDebut : Time_slot::getDateFin,
                        Collectors.summingInt(Time_slot::calculateWattage)
                ));
    }

    private static int calculateWattage(Time_slot timeSlot) {
        long hours = calculateHoursBetweenDates(timeSlot);
        return timeSlot.getWattage() * (int) hours;
    }

    private static long calculateHoursBetweenDates(Time_slot timeSlot) {
        long differenceInMilliseconds = timeSlot.dateFin.getTime()-timeSlot.dateDebut.getTime();
        long differenceInHours = TimeUnit.MILLISECONDS.toHours(differenceInMilliseconds);
        return differenceInHours;
    }
    public static List<Time_slot> setDate(List<Time_slot> times_slots) {
        for(int i = 0 ; i < times_slots.size() ; ++i){
            times_slots.get(i).setDateDebut(Time_slot.parseDateString(times_slots.get(i).getBegin()));
            times_slots.get(i).setDateFin(Time_slot.parseDateString(times_slots.get(i).getEnd()));
        }
        return times_slots;
    }

    public int getId(){
        return id;
    }
}
