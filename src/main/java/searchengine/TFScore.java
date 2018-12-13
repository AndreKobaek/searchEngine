package searchengine;

import java.util.List;

public class TFScore implements Score {

  // Rank the site according to the whole query, and the corpus.
  // NB: the corpus is actually not used in this simple algorithm.
  // But to comply with the Score interface the corpus parameter still needs to be there (ugly, we know).
  @Override
  public Double rank(Website site, Corpus corpus, List<List<String>> structuredQuery) {
    return rankQueryTF(site, structuredQuery);
  }

  @Override
  public Double rankSingle(Website site, Corpus corpus,  String word) {
    return rankSingleTF(site, word);
  }
  
  
  /**
   * Rank a single website according to a single word. Ranking algorithm is TF
   * 
   * @param site a single website that will be ranked.
   * @param a single word from the search query.
   * @return the rank of the site. Rank will always be non-negative.
   */
  private Double rankSingleTF(Website site, String word) {

    // score single word/term according to the document frequency and inverse corpus frequency.
    int wordSize = site.getWordSize();

    // number of times word appear on website, i.e the term site count.
    double wordCount = (double) site.getWordsToOccurences().get(word);

    // the site term frequency.
    return (wordCount / wordSize);
  }

  /**
   * Rank the site according to the whole query. I.e calculate and set the rank field of a website.
   * The ranking algorithm is TF.
   * 
   * @param site the website that are to be ranked.
   * @param corpus the corpus of all websites.
   * @param query the search query string.
   * 
   * @return Double value
   */
  private Double rankQueryTF(Website site, List<List<String>> structuredQuery) {

    double maxScoreSubQuery = 0;

    for (List<String> subquery : structuredQuery) {
      // sum the scores for the individual words in the subquery.
      double sum = 0;
      for (String word : subquery) {
        if (site.getWords().contains(word)) {
          sum += rankSingleTF(site, word);
        }
      }
      assert sum >= 0 : "The rank of a site should always be non-negative."; // sanity check, that
                                                                             // rank is always
                                                                             // non-negative.

      // find the subquery with the highest score. This is the score to be returned.
      if (sum > maxScoreSubQuery) {
        maxScoreSubQuery = sum;
      }
    }
    return maxScoreSubQuery;
  }
}
