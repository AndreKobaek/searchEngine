package searchengine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Creates a Vector of a Website. Initialize a Website a vectorValues List of Double and a wordValues Map of String and Double.
 * Takes a String and a Double and assign them to the Map and to the List to represent the Vector's values
 */
public class Vector {
    Website website;
    List <Double> vectorValues;
//    Map<String, Double> wordValues;

    public Vector(Website website){
        this.website = website;
        vectorValues = new ArrayList<>();
//        wordValues = new TreeMap<>();
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

        vectorValues.add(value);
    }

    /**
     * Returns the list of Double containing the value of the Vector
     * @return List of Double
     */
    public List<Double> getVectorValues(){
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
