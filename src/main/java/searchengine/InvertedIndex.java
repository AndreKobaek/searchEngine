package searchengine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


/**
 * The InvertedIndex data structure provides a way to build an index from a list of websites. It
 * allows to lookup the websites that contain a query word. The InvertedIndex maps query words to
 * websites allowing for a more effective lookup
 */
public abstract class InvertedIndex implements Index {

  protected Map<String, Set<Website>> map;

  /**
   * Takes a set of websites and creates a map. The keys are the words contained in these websites,
   * the value is a set of all the websites containing that key word.
   * 
   * @param sites The set of websites that should be indexed
   */
  @Override
  public void build(Set<Website> sites) {

    if (sites == null) {
      throw new IllegalArgumentException();
    }

    if (sites.contains(null)) {
      throw new IllegalArgumentException();
    }

    for (Website site : sites) {
      for (String word : site.getWords()) {

        // check if the map contains the key word
        if (map.containsKey(word)) {
          // if yes takes the value (a set of websites) add the site to it.
          // if the website already is in the value-set nothing happens.
          map.get(word).add(site);
        } else {
          // If not, create a new HashSet at the position of the key
          Set<Website> webTemp = new HashSet<>();
          webTemp.add(site);
          map.put(word, webTemp);
        }
      }
    }
  }

  
  
  /**
   * getMachingWebsites answers queries of the type "subquery1 OR subquery2 OR subquery3 ...". A
   * "subquery" has the form "word1 word2 word3 ...". A website matches a subquery if all the
   * words occur on the website. A website matches the whole query, if it matches at least one
   * subquery.
   *
   * @param input the structured query.
   * @return the set of websites that matches the query
   */
  public List<Website> getMatchingWebsites(List<List<String>> structuredQuery) {
    
    if (structuredQuery == null) {
      throw new NullPointerException();
    }
    
    if (structuredQuery.isEmpty()) {
      return Collections.emptyList();
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
    results.addAll(lookup(words.get(0)));
    for (int i=1; i<words.size(); i++) {
        results.retainAll(lookup(words.get(i)));
    }
    return results;
  }   
  
  
  /**
   * Returns the set of websites which matches the query string, returns an empty
   * {@code HashSet} if the query string does not match any sites in the map.
   * 
   * @param query The query
   * @return the Set of websites that contain the query word, or null if the query word is not among
   *         the map keys.
   */
  @Override
  public Set<Website> lookup(String query) {
    if (map.containsKey(query)) {
      return map.get(query);
    } else {
      return Collections.emptySet();
    }
  }
}
