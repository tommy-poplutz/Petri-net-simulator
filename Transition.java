
import java.util.*;
public class Transition extends Node{

    //Definition der Variablen für eingehende und ausgehende Kanten

    /*beim Schießen der Transition -> was sind die Stellen, die zu den eingehenden Kanten gehören, 
    welche Gewichtung haben die Kanten */
    private final ArrayList<Take> takes; 
    /*beim Schießen dieser Transition -> was sind die Stellen der ausgehenden Kanten, 
    welche Gewichtung hat diese Kante */
    private final ArrayList<Give> gives;


    //Konstruktor
    public Transition (String name){
        super(name);
        takes = new ArrayList<>();
        gives = new ArrayList<>();
    }

    public boolean addTake (Take t){
        if(t == null || takes.contains(t)) return false;
        takes.add(t);
        return true;
    }

    public boolean addGive (Give g){
        if(g == null || gives.contains(g)) return false;
        gives.add(g);
        return true;
    }

    public ArrayList<Take> getTakes(){
        return takes;
    }

    public ArrayList<Give> getGives(){
        return gives;
    }

    //Abfrage, ob Transition geschossen werden kann
    public boolean loaded(){
        for (Take take : takes) {
            if (!take.canTake()) {
                return false;
            }
        }
        return true;
    }

    //schießt Transition und setzt somit neue Marken in Takes und Gives
    public  boolean shoot(){
        if(!(loaded())){
            return false;
        }
        for (Take take : takes) {
            take.take();
        }
        
        for (Give give : gives) {
            give.give();
        }
        return true;
    }


    /*Aufteilung der toString-Methoden um Eingabeformat 
    bei Ausgabe zu gewährleisten und die Ausgabe damit als Eingabe für Parser nutzen zu können*/
    //toString gibt Name zurück
    @Override
    public String toString() {
        return name;
    }  
    
    /*givesToString gibt alle Gives einer Transition zurück
    durch Dateieingabeformat angabe der jeweilig zugehörigen Transition zur Identifizierbarkeit der Transition der Kante*/
    public String givesToString (){
        StringBuilder sb = new StringBuilder();
        for (Give give : gives) {
            sb.append(name);
            sb.append("\t");
            sb.append("give");
            sb.append("\t");
            sb.append(give.toString());
            sb.append("\n");
        }
         return sb.toString();
    }

    /*takesToString gibt alle Takes einer Transition zurück
    durch Dateieingabeformat angabe der jeweilig zugehörigen Transition zur Identifizierbarkeit der Transition der Kante*/
    public String takesToString(){
        StringBuilder sb = new StringBuilder();
        for (Take take : takes) {
            sb.append(name);
            sb.append("\t");
            sb.append("take");
            sb.append("\t");
            sb.append(take.toString());
            sb.append("\n");
        }
        return sb.toString();
    }


    
    /*gibt Kopie der Transition zurück
   /@Override
    public Transition clone() {
        Transition clone = new Transition(name, takes, gives);
        return clone;
    }*/
}