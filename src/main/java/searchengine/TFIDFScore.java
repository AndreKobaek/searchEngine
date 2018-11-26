package searchengine;

import java.util.Collection;

public class TFIDFScore extends TFScore {

    /**
     * Takes a website collections and for every website it gets the lists of words, then sets the tf-idf score
     * for each word into the website wordTfIdfScore collection
     * @param sites
     */
    @Override
    public void insertScore(Collection<Website> sites){
        for (Website website: sites){
            for(String word: website.getWords()){
                website.setWordTfIdfScore(word, getScore(word, website));
            }
        }
    }
    /**
     * Takes a word and a website and calculate the idf(inverse term frequency) for the website.
     * Then returns the value of tf(term frequency) multiplied by the idf
     * @param word
     * @param website
     * @return double tf-idf
     */
    @Override
    public double getScore(String word, Website website) {
        double tf = super.getScore(word, website);

        double numberOfWords = 0;
        double totalNumber = 0;
        for(String wordMatch: website.getWords()){
            totalNumber++;
            if(wordMatch.equalsIgnoreCase(word)){
                numberOfWords++;
            }
        }

        double idf = Math.log(totalNumber/numberOfWords);

        return tf*idf;
    }
}
