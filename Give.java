public class Give extends Edge {

    private final Place placeTo;

    public Give(int weight,Place to){
        super(weight);
        this.placeTo=to;
    }

    public void give(){
        int token=placeTo.getToken();
        placeTo.setToken(token+weight);
    }

    public Place getPlaceTo(){
        return placeTo;
    }
    @Override
    public String toString(){
        return(this.placeTo.getName()+"\t"+this.weight);
    }
}
