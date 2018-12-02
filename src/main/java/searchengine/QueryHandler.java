package searchengine;

import java.util.HashSet;
import java.util.Set;

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
   * "subquery" has the form "word1 word2 word3 ...". A website matches a subquery if all the words
   * occur on the website. A website matches the whole query, if it matches at least one subquery.
   *
   * @param query the query string
   * @return the set of websites that matches the query
   */
  public Set<Website> getMatchingWebsites(String query) {
    // The set is initialized as a HashSet rather than a TreeMap,
    // since the set of websites that matches a query is expected to be fairly small,
    // most of the time.
    Set<Website> results = new HashSet<>();

    // Split query into subquerys separated by OR.
    String[] subquerys = query.split("\\sOR\\s");

    // find sets that matches each subquery and add them.
    for (int j = 0; j < subquerys.length; j++) {
      String[] words = subquerys[j].split("\\s");
      results.addAll(intersect(words)); // lookup all the words in the subquery and take
                                        // intersection.
                                        // then take union of sets from each subquery.
    }
    return results;
  }



  private Set<Website> intersect(String[] words) {
    // The set is initialized as a HashSet rather than a TreeMap,
    // since the set of websites that matches a single word is expected to be fairly small,
    // most of the time.
    Set<Website> results = new HashSet<>();

    // intersection of sets of websites containing at least one of the words in the query.
    // this is where the lookup in the index happens.
    results.addAll(idx.lookup(words[0])); // the first set added should preferable be the biggest
                                          // set, because...???
    for (int i = 1; i < words.length; i++) {
      results.retainAll(idx.lookup(words[i]));
    }
    return results;
  }

}
