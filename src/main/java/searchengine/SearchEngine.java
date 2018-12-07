package searchengine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    queryHandler = new QueryHandler(idx); // index is passed to QueryHandler since this is where
                                          // lookup is done.
    score = new TFICFScore(); // choose the scoring algorithm to use.
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

    List<Website> results = queryHandler.getMatchingWebsites(structureQuery(query));

    // the websites are ordered according to rank.
    return orderWebsites(results, query);
  }


  /**
   * 
   */
  List<List<String>> structureQuery(String rawQuery) {  // package private so tests can use it.

    // Array for processed/validated subqueries.
    List<List<String>> queryArray = new ArrayList<>();

    // split the query into subqueries
    String[] subquerys = rawQuery.split("(\\s)*OR(\\s)+");
    for (int j = 0; j < subquerys.length; j++) {
      String[] words = subquerys[j].split("(\\s)+");
      for (String word : words) {
        System.out.println(word);
      }
      

      // construct subquery
      ArrayList<String> subqueryArray = new ArrayList<>();
      for (String word : words) { // always at least 1 word.
        word = word.toLowerCase();
        if (false) { // !corpus.containsWord(word)
          
          // do fuzzyExpansion
        } else {
          subqueryArray.add(word); // just keep the lowercase version of word.
        }
      }
      
      // add subquery to queryArray
      queryArray.add(subqueryArray);
      System.out.println(queryArray);
    }

    return queryArray;
  }


  /**
   * Rank a list of websites, according to the query (also using information about the whole
   * database from corpus object.)
   */
  private List<Website> orderWebsites(List<Website> list, String query) {

    // create a nested Comparator class
    class RankComparator implements Comparator<Website> {
      public int compare(Website site, Website otherSite) {
        return score.rank(site, corpus, query).compareTo(score.rank(otherSite, corpus, query));
      }
    }

    // sort the websites according to their rank.
    list.sort(new RankComparator().reversed()); // why do we need to reverse?
    return list;
  }

}
