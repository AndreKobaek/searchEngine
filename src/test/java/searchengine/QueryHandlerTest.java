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
    sites.add(new Website("4.com", "example4", Arrays.asList("word3", "word4")));
    Index idx = new InvertedIndexTreeMap();
    idx.build(sites);
    qh = new QueryHandler(idx);
  }

  @Test
  void testSingleWord() {
    assertEquals(1, qh.getMatchingWebsites("word1").size());
    assertEquals(2, qh.getMatchingWebsites("word2").size());
    assertEquals(0, qh.getMatchingWebsites("word-doesn't-exist").size());
  }

  @Test
  void testMultipleWords() {
    assertEquals(1, qh.getMatchingWebsites("word1 word2").size());
    assertEquals(2, qh.getMatchingWebsites("word3 word4").size());
    assertEquals(1, qh.getMatchingWebsites("word4 word3 word5").size());
    // order of input shouldn't matter
    assertTrue(qh.getMatchingWebsites("word3 word4").equals(qh.getMatchingWebsites("word4 word3")));
    // duplication of a word shouldn't change anything.
    assertTrue(qh.getMatchingWebsites("word3 word2").equals(qh.getMatchingWebsites("word3 word3 word2 word3")));
  }

  @Test
  void testORQueries() {
    assertEquals(4, qh.getMatchingWebsites("word2 OR word3").size());
    assertEquals(3, qh.getMatchingWebsites("word1 OR word4").size());
    assertEquals(1, qh.getMatchingWebsites("word55 OR word1").size());
    // order of input shouldn't matter
    assertTrue(qh.getMatchingWebsites("word2 OR word3").equals(qh.getMatchingWebsites("word3 OR word2")));
    // Corner case: Does code remove duplicates?
    assertEquals(1, qh.getMatchingWebsites("word1 OR word1").size());
    
    // order shouldn't make a difference.
    assertTrue(qh.getMatchingWebsites("word2 word1 OR word3 OR word1").equals(qh.getMatchingWebsites("word1 OR word3 OR word1 word2")));
  }
  
  // Test for problematic input
  @Test
  void testCornerCases() {
    
    // white space shouldn't matter 
    
    // case shouldn't matter

    
  }

}
