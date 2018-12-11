package searchengine;

import java.util.ArrayList;
import java.util.List;

public class Centroid {

    private List<Double> values;
    private List<Vector> websiteVectors;
    private String name;

    public Centroid(List<Double> values, String name){
        this.name = name;
        websiteVectors = new ArrayList<>();
        this.values = values;
    }

    public Centroid (Centroid c){
        this.name = c.getClusterName();
        this.websiteVectors = new ArrayList<>();
        this.values = c.getCentroidValues();
    }

    public List<Double> getCentroidValues(){
        return values;
    }

    public void setNewValues(List<Double> newValues){
        values = new ArrayList<>(newValues);
        //values.addAll(0,newValues);
    }

    public void assignWebsiteVectorToCentroid(Vector w){
        websiteVectors.add(w);
    }

    public void clearListOfWebsites(){
        websiteVectors = new ArrayList<>();
    }

    public List<Vector> getWebsiteVectors(){
        return websiteVectors;
    }

    public String getClusterName(){
        return name;
    }
}
