package searchengine;

import java.util.List;

public class TFIDFScore implements Score{
  
 // Rank the site according to the whole query.
 public Double rank(Website site, Corpus corpus, List<List<String>> structuredQuery) {
     return rankQueryTFIDF(site, corpus, structuredQuery);
 }
 
  private Double rankSingleTFIDF(Website site, Corpus corpus, String word) {
    
    // score single word/term according to the document frequency and inverse corpus frequency.
    int wordSize = site.getWordSize();
    double wordCount = (double) site.wordMap.get(word); // number of times word appear on website.
    double siteCount = (double) corpus.appearInSitesMap.get(word); // number of times the word
                                                                   // appears in a corpus website.
    return (wordCount / wordSize) * Math.log(corpus.totalNumberOfSites / siteCount);
  }

  private Double rankQueryTFIDF(Website site, Corpus corpus, List<List<String>> structuredQuery) {

    double maxScoreSubQuery = 0;

    for (List<String> subquery : structuredQuery) {
      // sum the scores for the individual words in the subquery.
      double sum = 0;
      for (String word : subquery) {
        if (site.getWords().contains(word)) {
          sum += rankSingleTFIDF(site, corpus, word);
        }
      }

      if (sum > maxScoreSubQuery) {
        maxScoreSubQuery = sum;
      }
    }
    return maxScoreSubQuery;
  }
}
