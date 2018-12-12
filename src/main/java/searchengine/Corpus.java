package searchengine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * The class holds information about the whole database (corpus) of websites. This information is
 * used by ranking methods to calculate the rank of individual websites in the corpus. The Corpus
 * class is fairly similar to the Index classes, and the two could perhaps be merged.
 */
public class Corpus {

  /**
   * A map which relates a word in the corpus to the number of times the word appear in the corpus.
   * The map is build when the corpus is instantiated. The map is made package private to allow
   * convenient access for external ranking methods.
   */
  Map<String, Integer> wordsToOccurences;

  /**
   * The total number of words in the Corpus (duplicates allowed)
   */
  private int wordCountTotal = 0; 

  /**
   * A set containing all the websites in the corpus. This set must be given to the Corpus when it
   * is instantiated.
   */
  Set<Website> allSites; // all websites in the corpus.

  /**
   * A map which relates a word to the number of sites/documents in which the word appears. The map
   * is build when the corpus is instantiated. The map is made package private to allow convenient
   * access for external ranking methods.
   */
  Map<String, Integer> appearInSitesMap; // package private


  /**
   * The total number of websites in the corpus.
   */
  int totalNumberOfSites;


  /**
   * A list of all the words in the corpus/database.
   */  
  ArrayList<String> wordsInCorpus;


  /**
  * A map from a bigram (i.e aa, ab ,ac, and so on) to an int[] array of size wordInCorpus.size()
  * int[] consists has only 0's or 1's. A 1 if the corresponding word in WordsInCorpus contains the bigram.
  */
  Map<String, int[]> biGramMap;

      

  /**
   * A constructor that instantiates the corpus.
   * 
   * NOTE: Most of the fields described above is first instantiated when the build method is
   * invoked. This is because the build method is potentially an "expensive" operation, and
   * therefore the programmer should actively think about when to invoke the method. If the build
   * method was "hid" in the constructor, this concern is more likely to be overlooked. This is
   * similar to the way the InvertedIndex is designed.
   * 
   * @param sites the sites that can be searched by the search engine. The sites must be supplied
   *        from an external database.
   */
  public Corpus(Set<Website> sites) {
    wordsToOccurences = new TreeMap<>();
    appearInSitesMap = new TreeMap<>();
    allSites = sites;
    totalNumberOfSites = allSites.size();
  }


  public boolean containsWord(String word){
    return wordsToOccurences.containsKey(word);
  }

  public int getWordCountUnique(){
    return wordsToOccurences.keySet().size();
  }

  public int getWordCountTotal(){
    return wordCountTotal;
  }

  // build the map of words
  public void build() {
    for (Website site : allSites) {

      // get unique words from the site, and add 1 to the number of sites that the word appear in.
      // NB: according to "Effective Java" by Joshua Bloch the forEach terminator shouldn't be used
      // in cases like this (Item ...). Code should probably be refactored.
      site.getWords().stream().distinct().forEach(w -> {
        if (appearInSitesMap.containsKey(w)) {
          appearInSitesMap.put(w, appearInSitesMap.get(w) + 1);
        } else {
          appearInSitesMap.put(w, 1);
        }
      });

      // take the number of times a word appear on the site,
      // and add it to the map that counts the number of times the word appears in the corpus.
      site.getWords().stream().distinct().forEach(w -> {
        // number of times the word occur on site.
        int n = site.wordMap.get(w);
        assert n >= 1 : "n should always be at least one"; // word is on the site, and hence a
                                                           // key/value pair should exist in the
                                                           // map.

        // update the "index" which counts word occurrences in the whole corpus.
        if (this.wordsToOccurences.containsKey(w)) {
          this.wordsToOccurences.put(w, this.wordsToOccurences.get(w) + n);
        } else {
          this.wordsToOccurences.put(w, n);
        }
        wordCountTotal += n;
      });
    }
    assert wordCountTotal == allSites.stream().map(Website::getWordSize).reduce(0,
        (total, count) -> total + count); // sanity check, that wordSize is calculated correctly.
  }

  
  public void build2GramIndex() {
    
    // initialize map
    biGramMap = new TreeMap<>();
    
    // get all the words in the corpus, put them in an ArrayList and sort them alphabetically.
    wordsInCorpus = new ArrayList<>(wordsToOccurences.keySet()); 
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
        if (calculate2Gram(wordsInCorpus.get(j)).contains(allBiGrams.get(i))) { // inefficient, calculates grams to many times. But luckily the map is build only once
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

