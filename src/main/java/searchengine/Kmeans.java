package searchengine;

import java.util.*;

import static java.lang.Double.NaN;
import static java.util.Collections.max;

/**
 * The k-means algorithm implements a cluster analysis of the database. It takes an arbitrary number as K, which represents
 * the number of clusters to create.
 * It uses Tf-Idf score to calculate the relation of each website with all the words in dataset.
 * It randomly assign K number of websites as initial centroids and then calculate the distance of each website from the centroids,
 * when it finds the minumum distance (which is the higher value of cosine similarity) of a website from that centroid it assigns
 * the website to the centroid.
 */
public class Kmeans {
    private List<Website> dataset;
    private SortedSet<String> totalWords;
    private List<Centroid> centroids;
    private List<Centroid> oldCentroids;
    private List<Vector> vectors;
    private CosineSimilarity cosineSimilarity;
    private Corpus corpus;
    private Score score;

    public Kmeans(List<Website> dataset, Corpus corpus, Score score){
        this.dataset = dataset;
        totalWords = new TreeSet<>();
        centroids = new ArrayList<>();
        vectors = new ArrayList<>();
        cosineSimilarity = new CosineSimilarity();
        oldCentroids = new ArrayList<>();
        this.corpus = corpus;
        this.score = score;
    }

    /**
     * It starts the K-means algorithm performing the initial settings and then getting into the loop which recalculates centroids and every time
     * reassigns the websites to the new calculated centroids until the n and n-1 iterations have the same centroids as result.
     * @param k the number of cluster to create
     */
    public void startKmeans(int k){

        calculateTotalWords();
        createVectors();
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

            System.out.println("Total word: "+totalWords.size());
            oldCentroids = new ArrayList<>();

            //Copy the centroids into a new List of centroids to be compared later
            for(Centroid c: centroids){
                oldCentroids.add(new Centroid(c));
            }


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


            if(compareCentroids(centroids, oldCentroids)){
                for(Centroid c: centroids){
                    c.clearListOfWebsites();
                }
            }else {
                condition = false;
            }
            iteration++;
        }



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

//        // This is a test for securing that the words are ordered the same way in totalWords and in the vectors.
//        ArrayList<String> totalWordList = new ArrayList();
//        ArrayList<String> vectorList = new ArrayList();
//
//        int counter = 0;
//        for (String word : totalWords) {
//          counter ++;
//          totalWordList.add(word);
//        }
//        Map<String, Double> tempMap = vectors.get(5).getWordValuesMap();
//        
//        for(String w: tempMap.keySet()) {
//          vectorList.add(w);
//        }
//        
//        for(int i=0; i<totalWordList.size(); i++) {
//          System.out.println(i+" "+totalWordList.get(i)+" is equal? "+totalWordList.get(i).equals(vectorList.get(i)));
//        }
         
        
    }

    public boolean compareCentroids(List<Centroid> centroids1, List<Centroid> centroids2){

        int compared = 0;

        for(int i=0; i<centroids1.size(); i++){
            for(int x=0; x<centroids1.get(i).getCentroidValues().size(); x++){
/
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
                Double tolerance = 0.001;
                if(Math.abs(a-b)<tolerance){
                    compared += 0;
                }else{
                    compared += 1;
                }
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

        //Takes the list and retrieves the maximum value in the list
        Double maxValue = Collections.max(distance);
        //Retrieves the index position of the maximum value in the list and assign it to an int to be returned
        index = distance.indexOf(maxValue);

        return index;
    }

    public void calculateTotalWords(){
        for(Website site: dataset){
            for(String word: site.getWords()){
                totalWords.add(word);
            }
        }
    }

    public void createVectors(){
        // for every website calculate its vector-representation according to database/corpus.
         for(Website website : dataset){
           //System.out.println(website.toString());
            Vector vector = new Vector(website);
            for(String word: totalWords){
              
              if (website.containsWord(word)) {
                // calculate a score and put it in the vector.
                vector.addVectorValue(word, score.rankSingle(website, corpus, word));
              } else {
                vector.addVectorValue(word, 0.0);
              }
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
