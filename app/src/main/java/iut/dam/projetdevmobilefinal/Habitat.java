package iut.dam.projetdevmobilefinal;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

public class Habitat implements Serializable {
    private int id;
    private String floor;
    private String area;
    private List<Appliance> appliances;


    public static Habitat getFromJson(String json){
        Gson gson = new Gson();
        Habitat obj = gson.fromJson(json, Habitat.class);
        return obj;
    }
    public static List<Habitat> getListFromJson(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<List<Habitat>>(){}.getType();
        List<Habitat> list = gson.fromJson(json, type);
        return list;
    }
    public String getArea(){
        return area;

    }
    public List<Appliance> getAppliances() {
        return appliances;
    }

    public String getFloor(){
        return floor;
    }

    public int getId(){
        return id;
    }

    public void setAppliances(int id, String nom, String reference, String consommation) {
        Appliance a = new Appliance(id+"",nom,reference,consommation);
        appliances.add(a);
    }
    public void deleteAppliance(String nom){
        for(int i = 0 ; i < appliances.size() ; ++i){
            if(appliances.get(i).getName().equals(nom))
                appliances.remove(i);
        }
    }
}
