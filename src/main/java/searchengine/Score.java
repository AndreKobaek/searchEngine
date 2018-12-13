package searchengine;

import java.util.List;

/**
 * An Interface which defines a Score type. This is a type that knows how to calculate a rank (or
 * score) for a website, given a Corpus (database of sites) and a structured search query.
 * 
 * @author André Mortensen Kobæk
 * @author Domenico Villani
 * @author Flemming Westberg
 * @author Mikkel Buch Smedemand
 */
public interface Score {

  /**
   * Calculate the rank of the website, given a corpus/databse of websites, and a search query.
   * 
   * @param site       Website that needs to be ranked.
   * @param corpus     Corpus is the collection of websites that the searchEngine knows about.
   * @param structured query is a list of list of words. All words in a list are "AND'ed" and the
   *                   list of list are "OR'ed".
   *
   * @return reference type Double. The reference type is Double instead of the primitive type
   *         double because the compareTo method of Double is used to sort the websites according to
   *         rank.
   */
  Double rank(Website site, Corpus corpus, List<List<String>> structuredQuery);


  /**
   * Calculate the rank of the website, given a corpus/databse of websites, and a single words.
   * Mainly for use in calculations in Kmeans class.
   */
  Double rankSingle(Website site, Corpus corpus, String word);
}
