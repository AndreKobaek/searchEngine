package searchengine;

import java.util.*;

import static java.lang.Double.NaN;
import static java.util.Collections.max;

public class Kmeans {
    private List<Website> dataset;
    private Set<String> totalWords;
    private List<Centroid> centroids;
    private List<Centroid> oldCentroids;
    private List<Vector> vectors;
    private CosineSimilarity cosineSimilarity;
    private Index idx;

    public Kmeans(List<Website> dataset, Index idx){
        this.dataset = dataset;
        totalWords = new TreeSet<>();
        centroids = new ArrayList<>();
        vectors = new ArrayList<>();
        cosineSimilarity = new CosineSimilarity();
        oldCentroids = new ArrayList<>();
        this.idx = idx;
    }

    public void startKmeans(int k){

        calculateTotalWords();
        createVectors(idx);
        //randomize centroids
        centroids = calculateInitialCentroids(k);

        boolean condition = true;
        int iteration = 0;
        while (condition){

            System.out.println("Into kmeans condition interation number: "+iteration);
            iteration++;
            //This needs to be performed in a bigger loop
            for(int v=0; v<vectors.size(); v++){
                ArrayList<Double> distance = new ArrayList<>();
                for(int c=0; c<centroids.size(); c++){
                    Double similarity = cosineSimilarity.calculateCS(centroids.get(c).getCentroidValues(), vectors.get(v).getVectorValues());
                    distance.add(similarity);
//                    System.out.println("Centroid name: "+centroids.get(c).getClusterName()+" Website: "+vectors.get(v).getWebsite().getTitle()+" Distance: "+similarity);
                }
                int index = calculateClosestCentroid(distance);
                centroids.get(index).assignWebsiteVectorToCentroid(vectors.get(v));
            }

//            for(Centroid c: centroids){
//                System.out.println("Centroid "+c.getClusterName()+" size: "+c.getCentroidValues().size());
//                for(int i=0; i<c.getWebsiteVectors().size(); i++){
//                    System.out.println("Vector "+c.getWebsiteVectors().get(i).getWebsite().getTitle()+" size: "+c.getWebsiteVectors().get(i).getVectorValues().size());
//                }
//            }
            System.out.println("Total word: "+totalWords.size());
            oldCentroids = new ArrayList<>();

            //Copy the centroids into a new List of centroids to be compared later
            for(Centroid c: centroids){
                oldCentroids.add(new Centroid(c));
            }

            //Recalculate centroids
//            oldCentroids = new ArrayList<Centroid>(centroids); //it creates a new object not pointing to that reference
            //oldCentroids = centroids; bad implementation it copies the reference to the object
            //For every centroid

            //If the list of website assigned to the centroid is not empty
//                if(!centroids.get(c).getWebsiteVectors().isEmpty()){

            for(int z=0; z<centroids.size();z++){
                System.out.println("---------Centroid: "+centroids.get(z).getClusterName()+"--------------");
                for(int i=0; i<centroids.get(z).getWebsiteVectors().size();i++){
                    System.out.println("Centroid "+centroids.get(z).getClusterName()+" Vector web: "+centroids.get(z).getWebsiteVectors().get(i).getWebsite().getTitle()+" distance: "+cosineSimilarity.calculateCS(centroids.get(z).getCentroidValues(),centroids.get(z).getWebsiteVectors().get(i).getVectorValues()));
                }
            }

            //Recalculate the centroids
            for(int c=0; c<centroids.size(); c++){

                ArrayList<Double> temporaryCentroid = new ArrayList<>();

                //For every word in the dataset
                for(int w=0; w<totalWords.size(); w++){
                    double tempVector = 0;
                    double tempCentroidValue = 0;
                    //For every vector
                    for(int v=0; v<centroids.get(c).getWebsiteVectors().size(); v++){
//                        System.out.println("Recalculating CENTROI size: "+centroids.get(c).getWebsiteVectors().size()+" Iteration: "+v);

                        //Add the value of a single word from every vector into a temporary var
                        if(centroids.get(c).getWebsiteVectors().get(v).getVectorValues().get(w).isNaN()||
                                centroids.get(c).getWebsiteVectors().get(v).getVectorValues().get(w)==null){
                            //Do nothing
                        }else {
                            tempVector += Math.abs(centroids.get(c).getWebsiteVectors().get(v).getVectorValues().get(w));
                        }
                    }

                    //Calculate temporary centroid median and add it to the temporary vector
                    //Add the value of the temporary divided by the number of vectors into the centroid
                    //to a single temporary var
                    tempCentroidValue = Math.abs(tempVector/centroids.get(c).getWebsiteVectors().size());
//                    System.out.println("TempVector: "+ tempVector+ " Divided by: "+centroids.get(c).getWebsiteVectors().size()+ " Result: "+tempCentroidValue);

                    temporaryCentroid.add(tempCentroidValue);
                }

                //Add the vector resetting the centroid
                centroids.get(c).setNewValues(temporaryCentroid);
//                tempCentroidValue = tempCentroidValue/totalWords.size();
            }

//            for(int c=0; c<centroids.size(); c++) {
//                for(int v=0; v<centroids.get(c).getCentroidValues().size(); v++){
//                    System.out.println("Old centroid: " + oldCentroids.get(c).getCentroidValues().get(v)+" New centroid: "+centroids.get(c).getCentroidValues().get(v));
//                }
//            }

            if(compareCentroids(centroids, oldCentroids)){
                for(Centroid c: centroids){
                    c.clearListOfWebsites();
                }
            }else {
                condition = false;
            }
            iteration++;
        }


//        for(int v=0; v<vectors.size(); v++){
//            ArrayList<Double> distance = new ArrayList<>();
//            for(int c=0; c<centroids.size(); c++){
//                System.out.println("---------Centroid: "+centroids.get(c).getClusterName()+"--------------");
//                System.out.println("Website: "+vectors.get(v).getWebsite().getTitle()+" distance: "+cosineSimilarity.calculateCS(centroids.get(c).getCentroidValues(), vectors.get(v).getVectorValues()));
//            }
//        }


        for(Centroid c: centroids){
            int b=0;
            System.out.println("---------Centroid: "+c.getClusterName()+"--------------");
            for(Vector v: c.getWebsiteVectors()){
                System.out.println("Website: "+v.getWebsite().getTitle()+" distance: "+cosineSimilarity.calculateCS(c.getCentroidValues(), v.getVectorValues()));
//                int x = 0;
//                for(Double d: v.vectorValues){
//                    System.out.println("Vector"+x+ " value: "+d);
//                }
                b++;
            }
        }

    }

    public boolean compareCentroids(List<Centroid> centroids1, List<Centroid> centroids2){

        int compared = 0;

        for(int i=0; i<centroids1.size(); i++){
            for(int x=0; x<centroids1.get(i).getCentroidValues().size(); x++){
//                if(centroids1.get(i).getCentroidValues().get(x).equals(centroids2.get(i).getCentroidValues().get(x))){
//                    compared += 0;
//                }else {
//                    compared += 1;
//                }
                //Better implementation to round the doubles
                Double a = centroids1.get(i).getCentroidValues().get(x);
                Double b = centroids2.get(i).getCentroidValues().get(x);
                if(a.isNaN()){
                    a = 0.0;
                }
                if(b.isNaN()){
                    b = 0.0;
                }
//                System.out.println("Comparin Centroids:");
//                System.out.println("Cent a"+i+": "+a+" Cent b: "+b);

                //Try to implement like
                Double tolerance = 0.01;
                if(Math.abs(a-b)<tolerance){
                    compared += 0;
                }else{
                    compared += 1;
                }

//                if(a.equals(b)){
//                    compared += 0;
//                }else {
//                    compared += 1;
//                }
            }
        }
        System.out.println("Compared = "+compared);

        if(compared>0){
            return true;
        }else {
            return false;
        }
    }



    public int calculateClosestCentroid(List<Double> distance){
        int index = 0;


        //Try to use this implementation
        Double maxValue = Collections.max(distance);
        index = distance.indexOf(maxValue);
//        for(int i=1; i<distance.size(); i++){
//            Double a = distance.get(index);
//            Double b = distance.get(i);
//            if(a.isNaN()){
//                a = 0.0;
//            }
//            if(b.isNaN()){
//                b = 0.0;
//            }
//            if(a<b){
//                index = i;
//            }
//        }

        return index;
    }

    public void calculateTotalWords(){
        for(Website site: dataset){
            for(String word: site.getWords()){
                totalWords.add(word);
            }
        }
    }

    public void createVectors(Index idx){
        //This behaviour needs to be changed
        TFIDFScore score = new TFIDFScore(dataset);
        score.insertScore(dataset, idx);

        //This should not be conncted directly with website trough the TFIDF calculation
        //Mikkel implementation should be better
        for(Website website: dataset){
            Vector vector = new Vector(website);
            for(String word: totalWords){
                vector.addVectorValue(word, website.getWordTfIdfScore(word));
            }
            vectors.add(vector);
        }
    }

    public List<Centroid> calculateInitialCentroids(int cardinality){
        List<Centroid> initialCentroids = new ArrayList<>();
        List<Double> distance = new ArrayList<>(); //Not used?
        int x = 0;

        while (x < cardinality){
            String clusterName = "cluster "+x;
            Centroid c = randomPoint(clusterName);
            initialCentroids.add(c);
            x++;
        }

        for(int z=0; z<initialCentroids.size();z++){
            for(int i=0; i<vectors.size();i++){
                System.out.println("Initial Centroid "+initialCentroids.get(z).getClusterName()+" Vector web: "+vectors.get(i).getWebsite().getTitle()+" distance: "+cosineSimilarity.calculateCS(initialCentroids.get(z).getCentroidValues(),vectors.get(i).getVectorValues()));
            }
        }

        return initialCentroids;
    }

    public Centroid randomPoint(String clusterName){
        Random randomGenerator = new Random();
        int index = randomGenerator.nextInt(dataset.size());
        Vector v = vectors.get(index);
        System.out.println("Initial Cluster random: "+ v.getWebsite().getTitle());
        return new Centroid(v.getVectorValues(), clusterName);
    }
}
