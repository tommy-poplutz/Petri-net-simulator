public class Place extends Node{
    //Anzahl Marken, die auf jeweiliger Posiition liegen
    private int token; 

    //Konstruktor
    public Place (String name, int token){
        super(name);
        this.token = token;
    }


    //ändert Markenanzahl
    public void setToken(int in){
        if(in >= 0){
            token = in; 
        }
        else
            throw new IllegalArgumentException("Please use a correct number of token.");
    }

    //gibt Markenanzahl zurück
    public int getToken(){
        return token; 
    }
    
    // gibt Kopie der Place zurück
    @Override
    @SuppressWarnings("CloneDeclaresCloneNotSupported")
    public Place clone(){
        try{
            return (Place) super.clone();
        }
        catch(CloneNotSupportedException e){
            throw new AssertionError(e);
        }
    };

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append("\t");
        sb.append(token);
        sb.append("\n");
        return sb.toString();
    }

}