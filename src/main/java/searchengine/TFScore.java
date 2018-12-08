package searchengine;

import java.util.List;

public class TFScore implements Score {

  @Override
  public Double rank(Website site, Corpus corpus, List<List<String>> structuredQuery) {
    return rankQueryTF(site, structuredQuery);
  }
  
private Double rankSingleTF(Website site, String word) {
    
    // score single word/term according to the document frequency and inverse corpus frequency.
    int wordSize = site.getWordSize();
    double wordCount = (double) site.wordMap.get(word); // number of times word appear on website.
    return (wordCount / wordSize);
  }

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

      if (sum > maxScoreSubQuery) {
        maxScoreSubQuery = sum;
      }
    }
    return maxScoreSubQuery;
  }
}
