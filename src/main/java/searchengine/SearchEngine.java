package searchengine;

import java.util.ArrayList;
import java.util.Comparator;
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

  private Index idx;
  private Corpus corpus;
  private Score score;
  private QueryHandler queryHandler;

  /**
   * Creates a {@code SearchEngine} object from a list of websites.
   *
   * @param sites the list of websites
   */
  public SearchEngine(Set<Website> sites) {
    idx = new InvertedIndexTreeMap();
    System.out.println("Building index...");
    idx.build(sites);
    corpus = new Corpus(sites);
    System.out.println("Building corpus...");
    corpus.build(); // corpus is kept in SearchEngine since this is where ranking is done.
    System.out.println("Building 2-gram index, this may take a while...");
    corpus.build2GramIndex(); // build 2gram inverse index, for fuzzy matching.
    score = new TFIDFScore(); // choose the scoring algorithm to use.
    queryHandler = new QueryHandler(idx, corpus, new Fuzzy(corpus));

    // Kmeans kmeans = new Kmeans(new ArrayList<Website>(sites), corpus, score);
    // kmeans.startKmeans(15);
    KmeansMap kmeans = new KmeansMap(new ArrayList<Website>(sites), corpus, score);
    kmeans.startKmeans(200);
  }

  /**
   * Returns the list of websites matching the query.
   *
   * @param query the query
   * @return the list of websites matching the query
   */
  // public List<Website> search(String query) {
  //   if (query == null || query.isEmpty()) {
  //     return new ArrayList<>();
  //   }

  //   List<Website> results = queryHandler.getMatchingWebsites(query);
  //   List<List<String>> structuredQuery = queryHandler.getStructuredQuery(query);
    
  //   // the websites are ordered according to rank.
  //   return orderWebsites(results, structuredQuery);
  // }
  
  public SearchResult search(String query) {
    if (query == null || query.isEmpty()) {
      return new SearchResult();
    }

    List<Website> results = queryHandler.getMatchingWebsites(query);
    List<List<String>> structuredQuery = queryHandler.getStructuredQuery(query);
    
    // The websites are ordered according to rank and returned as a {@code SearchResult}.
    return new SearchResult(orderWebsites(results, structuredQuery));
  }

  /**
   * Rank a list of websites, according to the query, 
   * also using information about the whole database from corpus object. 
   *
   * @param list List of websites to be ordered according to rank.
   * @param query The search query.
   * @return return the list of websites reordered according to rank.
   * I.e the method modifies the input list.  
  */
  private List<Website> orderWebsites(List<Website> list, List<List<String>> structuredQuery) {

    // create a nested Comparator class
    class RankComparator implements Comparator<Website> {
      public int compare(Website site, Website otherSite) {
        return score.rank(site, corpus, structuredQuery)
            .compareTo(score.rank(otherSite, corpus, structuredQuery));
      }
    }

    // sort the websites according to their rank.
    list.sort(new RankComparator().reversed()); // why do we need to reverse?
    return list;
  }
}
