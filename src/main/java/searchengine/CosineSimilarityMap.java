package searchengine;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;

public class CosineSimilarityMap {

    /**
     * Takes two vectors as lists and returns the distance by a number between 0.0 and 1.0
     * @param vector1 List of Double
     * @param vector2 List of Double
     * @return double value representing cosine similarity
     */
    public double calculateCS(Map<String,Double> vector1, Map<String, Double> vector2, SortedSet<String> totalWords){


        double magnitudeVect1 = 0;
        double magnitudeVect2 = 0;
        double dotProduct = 0;

        //Take the value of the vectors of the two lists, add the value of each position in the vector in power of 2
        //to a magnitude variable and multiply each value with the value at the same index in the other Vector.
        //If the value is null or NaN, add 0

        //OLD IMPLEMENTATION NOT WITH MAPS
//        for(int i=0; i<vector1.size(); i++){
//            if(Double.isNaN(vector1.get(i))&&Double.isNaN(vector2.get(i))){
//                //Do nothing
//            }else if(!Double.isNaN(vector1.get(i))&&Double.isNaN(vector2.get(i))){
//                //Add only magnitude 1
//                magnitudeVect1 += Math.pow(Math.abs(vector1.get(i)), 2);
//
//            }else if(Double.isNaN(vector1.get(i))&&!Double.isNaN(vector2.get(i))){
//                //Add only magnitude 2
//                magnitudeVect2 += Math.pow(Math.abs(vector2.get(i)), 2);
//            }else{
//                dotProduct += vector1.get(i)*vector2.get(i);
//                magnitudeVect1 += Math.pow(Math.abs(vector1.get(i)), 2);
//                magnitudeVect2 += Math.pow(Math.abs(vector2.get(i)), 2);
//            }
////            System.out.println("Vect1: "+vector1.get(i)+" Vect2: "+vector2.get(i));
////            System.out.println("Magnitude Vect1: "+magnitudeVect1+" Magnitude Vect2: "+magnitudeVect2+ " DotProduct: "+dotProduct);
//        }


        for(String word: totalWords){

            //The absolute value is used for normalization purposes
            if(vector1.containsKey(word) && vector2.containsKey(word)){
                dotProduct += vector1.get(word)*vector2.get(word);
                magnitudeVect1 += Math.pow(Math.abs(vector1.get(word)),2);
                magnitudeVect2 += Math.pow(Math.abs(vector2.get(word)),2);
            }else if(vector1.containsKey(word) && !vector2.containsKey(word)){
                //Add only magnitude 1
                magnitudeVect1 += Math.pow(Math.abs(vector1.get(word)), 2);
            }else if(!vector1.containsKey(word) && vector2.containsKey(word)){
                magnitudeVect2 += Math.pow(Math.abs(vector2.get(word)), 2);
            }
        }

        //Divide the dotProduct by the square root of each vector and multiplied together
        if(Double.isNaN(dotProduct/(Math.sqrt(magnitudeVect1)*Math.sqrt(magnitudeVect2)))){
            return 0.0;
        }
        return dotProduct/(Math.sqrt(magnitudeVect1)*Math.sqrt(magnitudeVect2));
    }
}
