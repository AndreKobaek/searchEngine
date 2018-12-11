package searchengine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Vector {
    Website website;
    List <Double> vectorValues;
    Map<String, Double> wordValues;

    public Vector(Website website){
        this.website = website;
        vectorValues = new ArrayList<>();
        wordValues = new TreeMap<>();
    }

    public void addVectorValue(String word, Double value){

        if(value == null){
            wordValues.put(word, 0.0);
        }else{
            wordValues.put(word, value);
        }

        vectorValues.add(value);
    }

    public List<Double> getVectorValues(){
        return vectorValues;
    }
    
    public Map<String, Double> getWordValuesMap(){
      return wordValues;
    }

    public Website getWebsite(){
        return website;
    }
}
