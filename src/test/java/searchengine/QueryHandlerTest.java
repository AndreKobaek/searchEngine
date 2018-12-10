package searchengine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.HashSet;
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
    assertEquals(1, qh.getMatchingWebsites("word1").size());
    assertEquals(2, qh.getMatchingWebsites("word2").size());
  }

  @Test
  void testInvalidInput() {
    assertEquals(1, qh.getMatchingWebsites("WORD1").size());
    assertEquals("example1", qh.getMatchingWebsites("WoRd1").get(0).getTitle());
    assertEquals(2, qh.getMatchingWebsites("woRD2").size());
    assertEquals(1, qh.getMatchingWebsites("word-3").size()); // Separated by dash
    assertEquals(1, qh.getMatchingWebsites("wOrD_4").size()); // Random case and separated by underscore 
    assertEquals(0, qh.getMatchingWebsites("").size()); // The empty string
  }

  @Test
  void testMultipleWords() {
    assertEquals(1, qh.getMatchingWebsites("word1 word2").size());
    assertEquals(1, qh.getMatchingWebsites("word3 word4").size());
    assertEquals(1, qh.getMatchingWebsites("word4 word3 word5").size());
    assertEquals(1, qh.getMatchingWebsites("word4 WORD3 word5?").size()); // Same as above - but with special characters
  }

  @Test
  void testORQueries() {
    assertEquals(3, qh.getMatchingWebsites("word2 OR word3").size());
    assertEquals(2, qh.getMatchingWebsites("word1 OR word4").size());
    // Corner case: Does code remove duplicates?
    assertEquals(1, qh.getMatchingWebsites("word1 OR word1").size());
    // Corner case: Does a standalone 'OR' entail a size of 0?
    assertEquals(0, qh.getMatchingWebsites("OR").size());
    // Corner case: Does the code support 'OR' at the beginning og end of the query?
    assertEquals(3, qh.getMatchingWebsites("OR word2 OR word3").size());
    assertEquals(2, qh.getMatchingWebsites("word1 OR word4 OR ").size());
  }

  // Test for problematic input
  @Test
  void testCornerCases() {

  }
  @Test
  void testUrlSearch(){
    assertEquals(1, qh.getMatchingWebsites("site:3.com word3").size());
    assertEquals(1, qh.getMatchingWebsites("site:2.com word3").size());
  }

}
