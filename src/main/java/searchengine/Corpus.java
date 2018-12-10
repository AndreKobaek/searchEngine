package searchengine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/** This class holds information about the whole database (corpus) of websites.
 * It is fairly similar to the Index classes. 
 */
public class Corpus { 
  Map<String, Integer> index; // package private. Word is mapped to the number of times it appear in the corpus.
  int wordSize = 0; // package private
  Set<Website> allSites;  // all websites in the corpus.
  Map<String, Integer> appearInSitesMap; // package private
  int totalNumberOfSites;
  Map<String, int[]> biGramMap;
  ArrayList<String> wordsInCorpus;
  
  public Corpus(Set<Website> sites) {
    index = new TreeMap<>();
    appearInSitesMap = new TreeMap<>();
    allSites = sites; 
  }

  // build the map of words
  public void build() {
    for (Website site : allSites) {
      
      // get unique words from the site, and add 1 to the number of sites that the word appear in.
      site.getWords().stream()
          .distinct()
          .forEach( w -> {
            if (appearInSitesMap.containsKey(w)) {
              appearInSitesMap.put(w, appearInSitesMap.get(w) + 1);
            } else {
              appearInSitesMap.put(w, 1);              
            }
          });   
      
      for (String word : site.getWords()) {

        // number of times the word occur on website site. 
        int n = site.wordMap.get(word); // Always >= 1.
        assert n >= 1 : "n should always be at least one";

        // update the "index"
        if (this.index.containsKey(word)) {
          this.index.put(word, this.index.get(word) + n);
        } else {
          this.index.put(word, n);
        }
        wordSize += n;
      }
    }
    totalNumberOfSites = allSites.size();
  }
  
  
  public void build2GramIndex() {
    
    // initialize map
    biGramMap = new TreeMap<>();
    
    // get all the words in the corpus, put them in an ArrayList and sort them alphabetically.
    wordsInCorpus = new ArrayList<>(index.keySet()); 
    Collections.sort(wordsInCorpus);
    
    // create a list of all bigrams
    ArrayList<String> allBiGrams = new ArrayList<>();
    String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
                         "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
                         "u", "v", "w", "x", "y", "z", "$"};
    
    for (String letter1 : alphabet) {
      for (String letter2 : alphabet) {
        allBiGrams.add(letter1 + letter2);
      }
    }
    // remove "$$" bigram.
    allBiGrams.remove((alphabet.length * alphabet.length) -1);
    
    // build map from bigram to boolean vector, which tells if word has bigram or not.
    int nrows = allBiGrams.size();
    int ncols = wordsInCorpus.size();
        
    for (int i=0; i<nrows; i++) {
      int[] rowVector = new int[ncols];
      for (int j=0; j<ncols; j++) {
        if (calculate2Gram(wordsInCorpus.get(j)).contains(allBiGrams.get(i))) { // inefficient, calculates grams to many times.
          rowVector[j] = 1;
        } else {
          rowVector[j] = 0;
        }
      }
      biGramMap.put(allBiGrams.get(i), rowVector);
    }
  }
  
  
  /**
   * Calculate 2-grams for a word.
   */
  private Set<String> calculate2Gram(String word) {

    if (word.length() <= 1) {
      return Collections.emptySet();
    }

    Set<String> biGrams = new HashSet<>();
    biGrams.add("$" + word.charAt(0));
    for (int i = 0; i < word.length() - 1; i++) {
      String biGram = word.substring(i, i + 2);
      biGrams.add(biGram);
    }
    biGrams.add(word.charAt(word.length() - 1) + "$");
    return biGrams;
  }
  
  
  
}