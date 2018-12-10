package searchengine;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is responsible for answering queries to our search engine.
 */
public class QueryHandler {

  /** The index the QueryHandler uses for answering queries. */
  private Index idx = null;

  /**
   * The regex used to validate queries - and the corresponding {@code Pattern} and
   * {@code Matcher} objects.
   */
  private final String REGEX = "\\b([-\\w]+)\\b";
  private final String URLREGEX = "^\\s*site:(\\S+)";
  private Pattern pattern;
  private Matcher matcher;
  private Matcher urlMatcher;
  private Pattern urlPattern;

  /**
   * The constructor
   *
   * @param idx The index used by the QueryHandler.
   */
  public QueryHandler(Index idx) {
    this.idx = idx;
    pattern = Pattern.compile(REGEX);
    urlPattern = Pattern.compile(URLREGEX);
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
  public List<Website> getMatchingWebsites(String input) {
    // Set for storing the combined results
    Set<Website> results = new HashSet<>();

    // check if input string starts with "site:", if so the following string until the next white space
    // is saved for later, and the matched part is removed from the input string.
    String siteUrl = null;
    urlMatcher = urlPattern.matcher(input);
    if (urlMatcher.find()){
      siteUrl = urlMatcher.group(1).toLowerCase();
      input = input.replace(urlMatcher.group(),"");
    }
    // The search query is split into sub queries by the keyword 'OR'
    String[] subQueries = input.split("\\bOR\\b");

    // Go through each of the sub queries and get the results
    for (String query : subQueries) {

      // Set for storing the results for this sub query
      Set<Website> subResults = new HashSet<>();

      // Boolean to define whether the lookups should be added or retained
      boolean firstQueryDone = false;

      // If the query consists of only 'OR' the split method returns 'OR',
      // therefore there are no queries and the loop should be terminated
      if (query.equals("OR")) {
        break;
      }

      // The query string is converted to lowercase to match the case of the data
      query = query.toLowerCase();
      matcher = pattern.matcher(query);

      while (matcher.find()) {
        if (!firstQueryDone) {
          subResults.addAll(idx.lookup(matcher.group()));
          firstQueryDone = true;
        } else {
          subResults.retainAll(idx.lookup(matcher.group()));
        }
      }

      results.addAll(subResults);
    }

    // Simple conversion to a list (to avoid changing types throughout the application (right
    // now (at least)))
    List<Website> resultsAsList = new ArrayList<>();
    resultsAsList.addAll(results);

    if (siteUrl!=null){
      for (int i = 0; i < resultsAsList.size(); i++) {
        if (siteUrl.length()>= resultsAsList.get(i).getUrl().length() ||!resultsAsList.get(i).getUrl().substring(0,siteUrl.length()).equals(siteUrl)){
          resultsAsList.remove(i);
        }
      }
    }
    return resultsAsList;
  }
}