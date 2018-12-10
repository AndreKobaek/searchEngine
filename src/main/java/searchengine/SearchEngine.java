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

  private Corpus corpus;
  private QueryHandler queryHandler;
  private Score score;
  private QueryFormat queryFormat;

  /**
   * Creates a {@code SearchEngine} object from a list of websites.
   *
   * @param sites the list of websites
   */
  public SearchEngine(Set<Website> sites) {
    Index idx = new InvertedIndexTreeMap();
    System.out.println("Building index...");
    idx.build(sites);
    corpus = new Corpus(sites);
    System.out.println("Building corpus...");
    corpus.build(); // corpus is kept in SearchEngine since this is where ranking is done.
    System.out.println("Building 2-gram index, this may take a while...");
    corpus.build2GramIndex(); // build 2gram inverse index, for fuzzy matching.
    queryHandler = new QueryHandler(idx); // index is passed to QueryHandler since this is where
                                          // lookup is done.
    score = new TFIDFScore(); // choose the scoring algorithm to use.
    queryFormat = new QueryFormat(corpus);
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

    List<List<String>> structuredQuery = queryFormat.structure(query);
    List<Website> results = queryHandler.getMatchingWebsites(structuredQuery);

    // the websites are ordered according to rank.
    return orderWebsites(results, structuredQuery);
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
