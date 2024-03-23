package iut.dam.projetdevmobilefinal;

public class Appliance {

    public enum EquipmentType {
        Aspirateur,
        Climatiseur,
        FerARepasser,
        MachineALaver,
        Radiateur,
        Television
    }
    private String id;
    private String name;
    private String reference;
    private String wattage;
    private String habitat_id;

    public Appliance(String nom, String reference, String conso){
        this.name=nom;
        this.reference =reference;
        this.wattage = conso;
    }

    public Appliance(String id, String nom, String reference, String conso){
        this.name=nom;
        this.reference =reference;
        this.wattage = conso;
        this.id = id;
    }
    public String getName(){
        return name;
    }

    public String getRef(){
        return reference;
    }
    public String getWattage(){
        return wattage;
    }

    public int getId(){
        return Integer.parseInt(id);
    }

}
