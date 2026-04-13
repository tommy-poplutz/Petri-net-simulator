public abstract class Node{
    //Variablendeklaration
    protected String name; 
    
    //Konstruktor
    public Node(String name){
        this.name = name;
    }

    //gibt Namen zurück
    public String getName(){
        return name; 
    }

    /*abstrakte Methode, die später Transition / Position cloned
    @Override
    public abstract Node clone(); */
}
