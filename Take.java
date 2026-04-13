public class Take extends Edge {

    private final Place placeFrom;

    public Take(int weight, Place from){
        super(weight);
        this.placeFrom=from;
    }

    public boolean canTake(){
        if(placeFrom.getToken()<this.weight){
            return false;
        }
        return true;
    }

    public boolean take(){
        if(this.canTake()==true){
            int token=placeFrom.getToken();
            placeFrom.setToken(token-weight);
            return true;
        }
        return false;
    }

    public Place getPlaceFrom(){
        return placeFrom;
    }

    @Override
    public String toString(){
        return(this.placeFrom.getName()+"\t"+this.weight);
    }

}
