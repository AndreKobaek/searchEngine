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

  /** The Index the QueryHandler uses for answering queries. */
  private Index idx;

  /** The Corpus the QueryHandler used to check whether a fuzzy search should be carried out */
  private Corpus corpus;

  /** The Fuzzy object used for fuzzy search */
  private Fuzzy fuzzy;

  /**
   * The regex used to validate queries - and the corresponding {@code Pattern} and {@code Matcher}
   * objects.
   */
  private final String REGEX = "\\b([-\\w]+)\\b";
  private Pattern pattern;
  private Matcher matcher;

  /**
   * The constructor
   *
   * @param idx The index used by the QueryHandler.
   */
  public QueryHandler(Index idx, Corpus corpus, Fuzzy fuzzy) {
    this.idx = idx;
    this.corpus = corpus;
    this.fuzzy = fuzzy;
    pattern = Pattern.compile(REGEX);
  }

  /**
   * getMachingWebsites answers queries of the type "subquery1 OR subquery2 OR subquery3 ...". A
   * "subquery" has the form "word1 word2 word3 ...". A website matches a subquery if all the words
   * occur on the website. A website matches the whole query, if it matches at least one subquery.
   *
   * @param query the query string
   * @return the set of websites that matches the query
   */
  public List<Website> getMatchingWebsites(String query) {

    // Set for storing the combined results
    Set<Website> results = new HashSet<>();

    // The search query is split into sub queries by the keyword 'OR'
    String[] subQueries = query.split("\\bOR\\b");

    // Go through each of the sub queries and get the results
    for (String subQuery : subQueries) {

      // Set for storing the results for this sub query
      Set<Website> subResults = new HashSet<>();

      // Boolean to define whether the lookups should be added or retained
      boolean firstSubQueryDone = false;

      // If the query consists of only 'OR' the split method returns 'OR',
      // therefore there are no queries and the loop should be terminated
      if (subQuery.equals("OR")) {
        break;
      }

      // The query string is converted to lowercase to match the case of the data
      subQuery = subQuery.toLowerCase();
      matcher = pattern.matcher(subQuery);

      while (matcher.find()) {

        // Set for storing the match or the fuzzed versions of a match
        Set<String> wordSet = new HashSet<>();

        // If the Corpus contains the match, add it (and nothing else) to the querySet
        if (corpus.containsWord(matcher.group())) {
          wordSet.add(matcher.group());
        } else {
          // If the Corpus doesn't contain the match, add the fuzzed versions of the match to the
          // querySet
          wordSet = fuzzy.expand(matcher.group());
        }

        if (!firstSubQueryDone) {
          for (String word : wordSet) {
            subResults.addAll(idx.lookup(word));
          }
          firstSubQueryDone = true;
        } else {
          // Set for storing the websites corresponding to the querySet
          Set<Website> retainSet = new HashSet<>();
          for (String word : wordSet) {
            retainSet.addAll(idx.lookup(word));
          }
          subResults.retainAll(retainSet);
        }

      }

      results.addAll(subResults);
    }

    // Simple conversion to a list (to avoid changing types throughout the application (right
    // now (at least)))
    List<Website> resultsAsList = new ArrayList<>();
    resultsAsList.addAll(results);

    return resultsAsList;
  }

  
  /**
   * Restructure a raw string query. A raw query is translated into a structured format as follows:
   * 
   * word1 AND word2 OR word3 -> [[ ]] word1 And word2 OR word3 AND spellingError -> [[word1,
   * word2], [word3, option1], [word3, option2]]
   * 
   * @param query the raw query string supplied by the user.
   * @return a list of list of strings
   */
  public List<List<String>> getStructuredQuery(String query) {

    // Array for processed/expanded subqueries.
    List<List<String>> structuredQuery = new ArrayList<>();

    // Magic

    return structuredQuery;
  }
}
