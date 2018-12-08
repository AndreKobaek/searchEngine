package searchengine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * This class is responsible for answering queries to our search engine.
 */
public class QueryHandler {

  /** The index the QueryHandler uses for answering queries. */
  private Index idx = null;

  /**
   * The constructor
   *
   * @param idx The index used by the QueryHandler.
   */
  public QueryHandler(Index idx) {
    this.idx = idx;
  }

  /**
   * getMachingWebsites answers queries of the type "subquery1 OR subquery2 OR subquery3 ...". A
   * "subquery" has the form "word1 word2 word3 ...". A website matches a subquery if all the
   * words occur on the website. A website matches the whole query, if it matches at least one
   * subquery.
   *
   * @param input the query string
   * @return the set of websites that matches the query
   */
  public List<Website> getMatchingWebsites(List<List<String>> structuredQuery) {
    
    if (structuredQuery == null || structuredQuery.isEmpty()) {
      throw new IllegalArgumentException();
    }
    
    // Set for storing the combined results
    Set<Website> results = new HashSet<>();

    // take union of websites returned by each subquery.
    for (List<String> subquery : structuredQuery) {
      results.addAll(intersect(subquery));   
    }

    // Simple conversion to a list (to avoid changing types throughout the application (right
    // now (at least)))
    List<Website> resultsAsList = new ArrayList<>();
    resultsAsList.addAll(results);
    return resultsAsList;
  }
  
  private Set<Website> intersect(List<String> words) {

    if (words.isEmpty()) {
      return Collections.emptySet();
    }
    
    Set<Website> results = new HashSet<>();
    
    // intersection of sets of websites containing all the words
    results.addAll(idx.lookup(words.get(0)));
    for (int i=1; i<words.size(); i++) {
        results.retainAll(idx.lookup(words.get(i)));
    }
    return results;
  }   
}
