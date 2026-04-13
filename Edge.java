public abstract class Edge {

    protected final int weight;

    public Edge(int weight){
        if(weight<0){
            throw new IllegalArgumentException("Weight has to be positive");
        }
        this.weight=weight;
    }

    public int getWeight(){
        return this.weight;
    }

}
