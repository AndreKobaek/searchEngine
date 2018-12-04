package searchengine;

import java.util.*;

public class TFScore implements Score {

    private HashMap<String,Integer> words;

    private List<Website> list;

    public TFScore(List<Website> list){
        this.list = list;
    }
    /**
     * Takes a website collections and for every website it gets the lists of words, then sets the tf score
     * for each word into the website wordTfScore collection
     * @param sites
     */
    public List<Website> insertScore(Collection<Website> sites, Index idx){
        Set<Website> set = new HashSet<>();
        for (Website website: sites){
            for(String word: website.getWords()){
                website.setWordTFScore(word, getScore(word, website, idx));
                set.add(website);
            }
        }

        ArrayList<Website> scoredWebsites = new ArrayList<>(set);
        return scoredWebsites;
    }

    /**
     * Takes a word and a website and calculate the tf(time frequency) that word occours in the website
     * @param word
     * @param website
     * @return the score as a double
     */
    @Override
    public double getScore(String word, Website website, Index idx){

        double numberOfWords = 0;
        double totalWords = 0;

        for(String wordMatch: website.getWords()){
            totalWords++;
            if(wordMatch.equalsIgnoreCase(word)){
                numberOfWords++;
            }
        }

        return numberOfWords/totalWords;
    }
}
