package searchengine;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * The search engine. Upon receiving a list of websites, it performs the necessary configuration
 * (i.e. building an index and a query handler) to then be ready to receive search queries.
 *
 * @author Willard Rafnsson
 * @author Martin Aumüller
 * @author Leonid Rusnac
 */
public class SearchEngine {

  private QueryHandler queryHandler;
  private Index idx;
  private List<Website> database;

  /**
   * Creates a {@code SearchEngine} object from a list of websites.
   *
   * @param sites the list of websites
   */
  public SearchEngine(List<Website> sites) {
    this.database = sites;
    idx = new InvertedIndexTreeMap();
    idx.build(sites);
    queryHandler = new QueryHandler(idx);

  }

  /**
   * Returns the list of websites matching the query.
   *
   * @param query the query
   * @return the list of websites matching the query
   */
  public List<Website> search(String query) {
    if (query == null || query.isEmpty()) {
      return new ArrayList<Website>();
    }
    List<Website> resultList = queryHandler.getMatchingWebsites(query);

    System.out.println("Into the search method");
    resultList = orderWebsites(query, resultList);

    return resultList;
  }

  public List<Website> orderWebsites(String query, List<Website> resultList){
    System.out.println("Into the orderWeb method");
    TFIDFScore score = new TFIDFScore(database);
    resultList = score.insertScore(resultList, idx);
    System.out.println("Before ordering:");
    for(Website w: resultList){
      System.out.println(w.getTitle() + " value double: "+ w.getWordTfScore(query));
    }
    resultList.sort((Website w1, Website w2)->w2.getWordTfScore(query).compareTo(w1.getWordTfScore(query)));
    System.out.println("After ordering:");
    for(Website w: resultList){
      System.out.println(w.getTitle() +  " value double: "+ w.getWordTfScore(query));
    }
    return resultList;
  }
}
