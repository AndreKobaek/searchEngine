package searchengine;

import java.util.List;

public interface Score {
  
  Double rank(Website site, Corpus corpus, List<List<String>> structuredQuery);

}
