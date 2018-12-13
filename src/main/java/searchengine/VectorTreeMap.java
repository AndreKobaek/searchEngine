package searchengine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class VectorTreeMap {
    Website website;
    Map<String, Double> vectorValues;
//    Map<String, Double> wordValues;

    public VectorTreeMap(Website website){
        this.website = website;
        vectorValues = new HashMap<>();
    }

    /**
     * Takes a word and a value and assign them to a list and a map
     * @param word to assign in the wordWalues Map
     * @param value to assign in the wordValues Map and vectorValues list
     */
    public void addVectorValue(String word, Double value){

//        if(value == null){
//            wordValues.put(word, 0.0);
//        }else{
//            wordValues.put(word, value);
//        }

        vectorValues.put(word,value);
    }

    /**
     * Returns the list of Double containing the value of the Vector
     * @return List of Double
     */
    public Map<String, Double> getVectorValuesMap(){
        return vectorValues;
    }

    /**
     * Return the Map containing each word mapped to itś Double value
     * @return Map of String mapped to Double value
     */
//    public Map<String, Double> getWordValuesMap(){
//      return wordValues;
//    }

    /**
     * Return the website assigned to the Vector
     * @return Website
     */
    public Website getWebsite(){
        return website;
    }

}
