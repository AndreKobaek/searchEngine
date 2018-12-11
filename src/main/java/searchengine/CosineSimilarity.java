package searchengine;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static java.lang.Double.NaN;
import static java.lang.Double.doubleToLongBits;

public class CosineSimilarity {

    public CosineSimilarity(){

    }

    public double calculateCS(List<Double> vector1, List<Double> vector2){


        double magnitudeVect1 = 0;
        double magnitudeVect2 = 0;
        double dotProduct = 0;


        for(int i=0; i<vector1.size(); i++){
            if(Double.isNaN(vector1.get(i))&&Double.isNaN(vector2.get(i))){
                //Do nothing
            }else if(!Double.isNaN(vector1.get(i))&&Double.isNaN(vector2.get(i))){
                //Add only magnitude 1
                magnitudeVect1 += Math.pow(Math.abs(vector1.get(i)), 2);

            }else if(Double.isNaN(vector1.get(i))&&!Double.isNaN(vector2.get(i))){
                //Add only magnitude 2
                magnitudeVect2 += Math.pow(Math.abs(vector2.get(i)), 2);
            }else{
                dotProduct += vector1.get(i)*vector2.get(i);
                magnitudeVect1 += Math.pow(Math.abs(vector1.get(i)), 2);
                magnitudeVect2 += Math.pow(Math.abs(vector2.get(i)), 2);
            }
//            System.out.println("Vect1: "+vector1.get(i)+" Vect2: "+vector2.get(i));
//            System.out.println("Magnitude Vect1: "+magnitudeVect1+" Magnitude Vect2: "+magnitudeVect2+ " DotProduct: "+dotProduct);
        }

        if(Double.isNaN(dotProduct/(Math.sqrt(magnitudeVect1)*Math.sqrt(magnitudeVect2)))){
            return 0;
        }
        return Math.abs(dotProduct/(Math.sqrt(magnitudeVect1)*Math.sqrt(magnitudeVect2)));
    }
}
