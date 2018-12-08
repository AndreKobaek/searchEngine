package searchengine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The search engine. Upon receiving a list of websites, it performs the necessary configuration
 * (i.e. building an index and a query handler) to then be ready to receive search queries.
 *
 * @author Willard Rafnsson
 * @author Martin Aumüller
 * @author Leonid Rusnac
 */
public class SearchEngine {

  private Corpus corpus;
  private QueryHandler queryHandler;
  private Score score;

  /**
   * Creates a {@code SearchEngine} object from a list of websites.
   *
   * @param sites the list of websites
   */
  public SearchEngine(Set<Website> sites) {
    Index idx = new InvertedIndexTreeMap();
    idx.build(sites);
    corpus = new Corpus(sites);
    corpus.build(); // corpus is kept in SearchEngine since this is where ranking is done.
    corpus.build2GramIndex(); // build 2gram inverse index, for fuzzy matching.
    queryHandler = new QueryHandler(idx); // index is passed to QueryHandler since this is where
                                          // lookup is done.
    score = new TFScore(); // choose the scoring algorithm to use.
  }

  /**
   * Returns the list of websites matching the query.
   *
   * @param query the query
   * @return the list of websites matching the query
   */
  public List<Website> search(String query) {
    if (query == null || query.isEmpty()) {
      return new ArrayList<>();
    }
     
    
    List<List<String>> structuredQuery = structureQuery(query);
    System.out.println(structuredQuery.size());
    System.out.println(structuredQuery.toString());
    List<Website> results = queryHandler.getMatchingWebsites(structuredQuery);

    // the websites are ordered according to rank.
    return orderWebsites(results, structuredQuery);
  }


  /**
   * 
   */
  private List<List<String>> structureQuery(String rawQuery) {  

    // Array for processed/expanded subqueries.
    List<List<String>> queryArray = new ArrayList<>(new ArrayList<>());
    // split the query into subqueries
    String[] subqueries = rawQuery.split("(\\s)*OR(\\s)+");
    for (int j = 0; j < subqueries.length; j++) {
      String[] subquery = subqueries[j].split("(\\s)+");
      
      // check all words in subquery to see if it must be expanded.
      // also turn all words into lower case.
      Set<List<String>> childQueries = new HashSet<>(Collections.emptyList());
      for (String word : subquery) { // always at least 1 word.
        word = word.toLowerCase();
        if (!corpus.index.containsKey(word)) {
          System.out.println("Cannot find word " + word);
          Set<String> fuzzySet = fuzzyExpand(word);
          System.out.println("Instead I'll try with: " + fuzzySet.toString());
          
          // make a new reference to existing set of sets object.
          Set<List<String>> temporaryStorage = childQueries;
          
          // create new object for storing queries. Overwrite old local variable childQueries.
          childQueries = new HashSet<>(Collections.emptyList());          
          // create a new child subquery for each word in fuzzySet
          for (String fword : fuzzySet) {
            List<String> newList = new ArrayList<>();
            newList.add(fword);
            if (temporaryStorage.isEmpty()) {
              childQueries.add(newList);
            } else {
              for (List<String> oldList : temporaryStorage) {
                newList.addAll(oldList);
                childQueries.add(newList);
              }
            }  
          }
        } else {
          // add the known word to all existing sets (corresponding to "child" subqueries).
          if (childQueries.isEmpty()) {
            List<String> list = new ArrayList<>();
            list.add(word);
            childQueries.add(list);
          } else {
            for (List<String> list : childQueries) {
              list.add(word);
            }
          } 
        }
      }
      // add all relevant subqueries to queryArray.
      queryArray.addAll(childQueries);
    }
    return queryArray;
  }
    
  
  private Set<String> fuzzyExpand(String unknownWord) {
    int ncols = corpus.wordsInCorpus.size();
    
    Set<String> approximateStrings = new HashSet<>();
    
    int[] summedRowVector = new int[ncols];
    for (String bigram : calculate2Gram(unknownWord)) {
      for (int ncol=0; ncol<ncols; ncol++) {
        int[] rowVector = corpus.biGramMap.get(bigram);
        summedRowVector[ncol] += rowVector[ncol];
      }  
    }
    
    // add approximate words  
    for (int i=0; i<summedRowVector.length; i++) {
      if (summedRowVector[i] >= 4) {
        approximateStrings.add(corpus.wordsInCorpus.get(i));
      }
    }
    // pick the best of the approximate strings, the one(s) with the smallest edit distance.
          // TODO 
    
    return approximateStrings; 
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
    for (int i=0; i<word.length()-1; i++) {
      String biGram = word.substring(i, i+2);
      biGrams.add(biGram);
    }
    biGrams.add(word.charAt(word.length()-1) + "$");
    return biGrams; 
  }
  
  
  
  /**
   * Rank a list of websites, according to the query (also using information about the whole
   * database from corpus object.)
   */
  private List<Website> orderWebsites(List<Website> list, List<List<String>> structuredQuery) {

    // create a nested Comparator class
    class RankComparator implements Comparator<Website> {
      public int compare(Website site, Website otherSite) {
        return score.rank(site, corpus, structuredQuery).compareTo(score.rank(otherSite, corpus, structuredQuery));
      }
    }

    // sort the websites according to their rank.
    list.sort(new RankComparator().reversed()); // why do we need to reverse?
    return list;
  }

}
