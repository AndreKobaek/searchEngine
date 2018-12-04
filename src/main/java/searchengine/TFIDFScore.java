package searchengine;

import java.util.*;

public class TFIDFScore extends TFScore {

    private List<Website> list;
    private Set<Website> set;


    public TFIDFScore(List<Website> list){
        super(list);
        this.list = list;
    }
    /**
     * Takes a website collections and for every website it gets the lists of words, then sets the tf-idf score
     * for each word into the website wordTfIdfScore collection
     * @param sites
     */
//    @Override
//    public List<Website> insertScore(Collection<Website> sites, Index idx){
//
//        set = new HashSet<>();
//        for (Website website: sites){
//            for(String word: website.getWords()){
//                website.setWordTfIdfScore(word, getScore(word, website, idx));
//                set.add(website);
//            }
//        }
//        ArrayList<Website> scoredWebsites = new ArrayList<>(set);
//        return scoredWebsites;
//    }
    /**
     * Takes a word and a website and calculate the idf(inverse term frequency) for the website.
     * Then returns the value of tf(term frequency) multiplied by the idf
     * @param word
     * @param website
     * @return double tf-idf
     */

    @Override
    public double getScore(String word, Website website, Index idx) {

        double tf = super.getScore(word, website, idx);


        double numberOfWords = 0;
        double totalNumber = 0;

        //Probably delete this part commented
//        for(String wordMatch: website.getWords()){
//            totalNumber++;
//
//            if(wordMatch.equalsIgnoreCase(word)){
//                numberOfWords++;
//            }
//        }

        //Takes the total list of websites into the database
        Double totalDouble = new Double(list.size());
        //Takes the list of websites containing the word
        Double numberOfDouble = new Double(idx.lookup(word).size());

        //FIX this the totalNumber needs to be the number of websites in the database and numberOfWords is the number of websites where the word occours
        double idf = Math.log(totalDouble/numberOfDouble);

        return tf*idf;
    }
}
