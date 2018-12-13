package searchengine;

import java.util.HashSet;
import java.util.Set;


public class Fuzzy {

  /** The Corpus used for fuzzy search. */
  private Corpus corpus;

  public Fuzzy(Corpus corpus) {
    this.corpus = corpus;
  }

  /**
   * Restructure and possibly "fuzzy-expand" a raw string query.
   * 
   * A raw query is translated into a structured format as follows. 
   * 
   * word1 AND word2 OR word3 -> [[  ]]
   * word1 And word2 OR word3 AND spellingError -> [[word1, word2], [word3, option1], [word3, option2]]  
   * 
   * //@param the raw query string supplied by the user.
   * //@return a list of list of strings
   */
  // public List<List<String>> structure(String rawQuery) {
    
    
  //   // Array for processed/expanded subqueries.
  //   List<List<String>> queryArray = new ArrayList<>(new ArrayList<>());

  //   // split the query into subqueries
  //   String[] subqueries = rawQuery.trim().split("(\\s)*OR(\\s)+");
  //   for (int j = 0; j < subqueries.length; j++) {
  //     String[] subquery = subqueries[j].split("(\\s)+");

  //     // check all words in subquery to see if it must be expanded.
  //     // also turn all words into lower case.
  //     Set<List<String>> childQueries = new HashSet<>(Collections.emptyList());
  //     for (String word : subquery) { // always at least 1 word.
  //       word = word.toLowerCase();
  //       if (!corpus.wordsToOccurences.containsKey(word)) {
  //         System.out.println("Cannot find word: " + word);
  //         Set<String> fuzzySet = expand(word);
  //         System.out.println("Instead I'll make the search with: " + fuzzySet.toString());

  //         // make a new reference to existing set of sets object.
  //         Set<List<String>> temporaryStorage = childQueries;

  //         // create new object for storing queries. Overwrite old local variable childQueries.
  //         childQueries = new HashSet<>(Collections.emptyList());
  //         // create a new child subquery for each word in fuzzySet
  //         for (String fword : fuzzySet) {
  //           List<String> newList;
  //           if (temporaryStorage.isEmpty()) {
  //             newList = new ArrayList<>();
  //             newList.add(fword);
  //             childQueries.add(newList);
  //           } else {
  //             for (List<String> oldList : temporaryStorage) {
  //               newList = new ArrayList<>();
  //               newList.add(fword);
  //               newList.addAll(oldList);
  //               childQueries.add(newList);
  //             }
  //           }
  //         }
  //       } else {
  //         // add the known word to all existing sets (corresponding to "child" subqueries).
  //         if (childQueries.isEmpty()) {
  //           List<String> list = new ArrayList<>();
  //           list.add(word);
  //           childQueries.add(list);
  //         } else {
  //           for (List<String> list : childQueries) {
  //             list.add(word);
  //           }
  //         }
  //       }
  //     }
  //     // add all relevant subqueries to queryArray.
  //     queryArray.addAll(childQueries);
  //   }
  //   System.out
  //       .println("Final restructured search query is:" + System.lineSeparator() + queryArray.toString());
  //   return queryArray;
  // }

  public Set<String> expand(String unknownWord) {
    
    // Set for storing the fuzzy strings
    Set<String> fuzzyStrings = new HashSet<>();

    // maximum allowed edit distance.
    int delta;
    // delta is assigned based on the length of the word
    switch (unknownWord.length()) {
      case 3:   delta = 1;
                break;
      case 2:   delta = 1;
                break;
      case 1:   fuzzyStrings.add(unknownWord);
                return fuzzyStrings;
      default:  delta = 2;
                break;
    }

    // only looking at 2-grams for now
    int gramSize = 2; 

    int ncols = corpus.getTotalNumberOfSites();

    Set<String> approximateStrings = new HashSet<>();

    int[] summedRowVector = new int[ncols];
    for (String bigram : calculate2Gram(unknownWord)) {
      for (int ncol = 0; ncol < ncols; ncol++) {
        //HER
        int[] rowVector = corpus.getBiGramMap().get(bigram);
        summedRowVector[ncol] += rowVector[ncol];
      }
    }

    // add approximate words
    for (int i = 0; i < summedRowVector.length; i++) {
      //HER
      int commonGramsBound = Math.max(unknownWord.length(), corpus.getWordsInCorpus().get(i).length())
          - 1 - (delta - 1) * gramSize;
      if (summedRowVector[i] >= commonGramsBound) {
        //HER
        approximateStrings.add(corpus.getWordsInCorpus().get(i));
      }
    }
    // print message
    System.out.println("Other related words based on 2-gram index: ");
    System.out.println(approximateStrings.toString());

    // pick the best of the approximate strings, the one(s) with the smallest edit distance.
    for (String approxString : approximateStrings) {

      // check if editDistance is smaller than delta
      int editDistance = editDistance(unknownWord, approxString);
      if (editDistance <= delta) {
        fuzzyStrings.add(approxString);
      } else {
        System.out.println(
            "Discard: " + approxString + " (Edit Distance = " + editDistance + " > " + delta + ")");
      }
    }
    System.out.println("I'll try to search for:");
    System.out.println(fuzzyStrings.toString());
    
    return fuzzyStrings;
  }


  /**
   * Calculate edit distance for two strings x and y. algorithm from reference: "The
   * String-to-string correction problem", R. A. Wagner and M. J. Fischer
   */
  private int editDistance(String x, String y) {

    // cost "function" for allowed edits. All edits have the same cost.
    int deleteCost = 1;
    int insertCost = 1;
    int changeCost = 1;

    // instantiate matrix D
    int[][] D = new int[x.length() + 1][y.length() + 1];

    // loop over string x.length. Populate first column.
    for (int i = 1; i < x.length() + 1; i++) {
      D[i][0] = D[i - 1][0] + deleteCost;
    }

    // loop over string y.length. Populate first row.
    for (int j = 1; j < y.length() + 1; j++) {
      D[0][j] = D[0][j - 1] + insertCost;
    }

    // calculate remaining matrix elements.
    for (int i = 1; i < x.length() + 1; i++) {
      for (int j = 1; j < y.length() + 1; j++) {

        // calculate
        int equalIndicator = changeCost;
        if (x.substring(i - 1, i).equals(y.substring(j - 1, j))) {
          equalIndicator = 0;
        }

        // do something
        int m1 = D[i - 1][j - 1] + equalIndicator;
        int m2 = D[i - 1][j] + deleteCost;
        int m3 = D[i][j - 1] + insertCost;
        D[i][j] = Math.min(m1, Math.min(m2, m3));
      }
    }
    return D[x.length()][y.length()];
  }


  /**
   * Calculate 2-grams for a word.
   */
  private Set<String> calculate2Gram(String word) {

    Set<String> biGrams = new HashSet<>();

    // If the word has <= 1 character, return an empty set
    if (word.length() <= 1) {
      return biGrams;
    }

    biGrams.add("$" + word.charAt(0));
    for (int i = 0; i < word.length() - 1; i++) {
      String biGram = word.substring(i, i + 2);
      biGrams.add(biGram);
    }
    biGrams.add(word.charAt(word.length() - 1) + "$");
    return biGrams;
  }

}
