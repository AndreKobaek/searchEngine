package searchengine;

import java.util.List;

public class TFICFScore implements Score {

  @Override
  public Double rank(Website site, Corpus corpus, List<List<String>> structuredQuery) {
    return rankQueryTFICF(site, corpus, structuredQuery);
  }

  private Double rankSingleTFICF(Website site, Corpus corpus, String word) {
    
    // score single word/term according to the document frequency and inverse corpus frequency.
    int wordSize = site.getWordSize(); // number of words on the site.
    double wordCount = (double) site.wordMap.get(word); // number of times word appear on website.
    double corpusCount = (double) corpus.index.get(word); // number of times word appear in corpus.
    int corpusSize = corpus.wordSize; // number of words in the corpus. 
        
    return (wordCount / wordSize) * Math.log(corpusSize/corpusCount);
  }

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

      if (sum > maxScoreSubQuery) {
        maxScoreSubQuery = sum;
      }
    }
    return maxScoreSubQuery;
  }
}
