package searchengine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class QueryHandlerTest {
  private QueryHandler qh = null;

  @BeforeEach
  void setUp() {
    Set<Website> sites = new HashSet<>();
    sites.add(new Website("1.com", "example1", Arrays.asList("word1", "word2")));
    sites.add(new Website("2.com", "example2", Arrays.asList("word2", "word3")));
    sites.add(new Website("3.com", "example3", Arrays.asList("word3", "word4", "word5")));
    Index idx = new InvertedIndexTreeMap();
    idx.build(sites);
    qh = new QueryHandler(idx);
  }

  @Test
  void testSingleWord() {
    assertEquals(1, qh.getMatchingWebsites(structureQuery("word1")).size());
    assertEquals(2, qh.getMatchingWebsites(structureQuery("word2")).size());
  }

  @Test
  void testInvalidInput() {
    assertEquals(1, qh.getMatchingWebsites(structureQuery("WORD1")).size());
    assertEquals("example1", qh.getMatchingWebsites(structureQuery("WoRd1")).get(0).getTitle());
    assertEquals(2, qh.getMatchingWebsites(structureQuery("woRD2")).size());
    //assertEquals(1, qh.getMatchingWebsites("word-3").size()); // Separated by dash
    //assertEquals(1, qh.getMatchingWebsites("wOrD_4").size()); // Random case and separated by underscore 
    assertEquals(0, qh.getMatchingWebsites(structureQuery("")).size()); // The empty string
  }

  @Test
  void testMultipleWords() {
    assertEquals(1, qh.getMatchingWebsites(structureQuery("word1 word2")).size());
    assertEquals(1, qh.getMatchingWebsites(structureQuery("word3 word4")).size());
    assertEquals(1, qh.getMatchingWebsites(structureQuery("word4 word3 word5")).size());
    //assertEquals(1, qh.getMatchingWebsites(structureQuery("word4 WORD3 word5?")).size()); // Same as above - but with special characters
  }

  @Test
  void testORQueries() {
    assertEquals(3, qh.getMatchingWebsites(structureQuery("word2 OR word3")).size());
    assertEquals(2, qh.getMatchingWebsites(structureQuery("word1 OR word4")).size());
    // Corner case: Does code remove duplicates?
    assertEquals(1, qh.getMatchingWebsites(structureQuery("word1 OR word1")).size());
    // Corner case: Does a standalone 'OR' entail a size of 0?
    assertEquals(0, qh.getMatchingWebsites(structureQuery("OR")).size());
    // Corner case: Does the code support 'OR' at the beginning or end of the query?
    assertEquals(3, qh.getMatchingWebsites(structureQuery("OR word2 OR word3")).size());
    assertEquals(2, qh.getMatchingWebsites(structureQuery("word1 OR word4 OR ")).size());
  }

  // Test for problematic input
  @Test
  void testCornerCases() {

  }
  
  List<List<String>> structureQuery(String rawQuery) {  // package private so tests can use it.

    // Array for processed/validated subqueries.
    List<List<String>> queryArray = new ArrayList<>();

    // split the query into subqueries
    String[] subquerys = rawQuery.split("(\\s)*OR(\\s)+");
    for (int j = 0; j < subquerys.length; j++) {
      String[] words = subquerys[j].split("(\\s)+");

      // construct subquery
      ArrayList<String> subqueryArray = new ArrayList<>();
      for (String word : words) { // always at least 1 word.
        word = word.toLowerCase();
        if (false) { // !corpus.containsWord(word)
          
          // do fuzzyExpansion
        } else {
          subqueryArray.add(word); // just keep the lowercase version of word.
        }
      }
      
      // add subquery to queryArray
      queryArray.add(subqueryArray);
    }

    return queryArray;
  }
  

}
