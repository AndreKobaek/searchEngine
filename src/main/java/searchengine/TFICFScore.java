package searchengine;

import java.util.List;

public class TFICFScore implements Score {

  // Rank the site according to the whole query, and the corpus.
  @Override
  public Double rank(Website site, Corpus corpus, List<List<String>> structuredQuery) {
    return rankQueryTFICF(site, corpus, structuredQuery);
  }

  @Override
  public Double rankSingle(Website site, Corpus corpus,  String word) {
    return rankSingleTFICF(site, corpus, word);
  }
  
  
  /**
   * Rank a single website according to a single word. Ranking algorithm is TFICF (i.e Inverse
   * Corpus term Frequency instead of Inverse Document Frequency).
   * 
   * @param site a single website that will be ranked.
   * @param corpus of all websites that the search engine knows about.
   * @param a single word from the search query.
   * @return the rank of the site. Rank will always be non-negative.
   */
  private Double rankSingleTFICF(Website site, Corpus corpus, String word) {

    // number of words on the site.
    int wordSize = site.getWordSize();

    // number of times word appear on website, i.e the term site count.
    double wordCount = (double) site.getWordsToOccurences().get(word);

    // number of times word appear in the corpus, i.e the corpus count.
    double corpusCount = (double) corpus.getWordsToOccurences().get(word);

    // total number of words in the corpus.
    int corpusSize = corpus.getWordCountTotal();

    return (wordCount / wordSize) * Math.log(corpusSize / corpusCount);
  }


  /**
   * Rank the site according to the whole query. I.e calculate and set the rank field of a website.
   * The ranking algorithm is TFIDF.
   * 
   * @param site the website that are to be ranked.
   * @param corpus the corpus of all websites.
   * @param query the search query string.
   * 
   * @return Double value
   */
  private Double rankQueryTFICF(Website site, Corpus corpus, List<List<String>> structuredQuery) {

    double maxScoreSubQuery = 0;

    for (List<String> subquery : structuredQuery) {
      // sum the scores for the individual words in the subquery.
      double sum = 0;
      for (String word : subquery) {
        if (site.getWords().contains(word)) {
          sum += rankSingleTFICF(site, corpus, word);
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
