package searchengine;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class ScoreTest {

  // declare variables to be used in the tests
  TFScore tfScore;
  TFIDFScore tfidfScore;
  TFICFScore tficfScore;
 
  Set<Website> sites; 
  Corpus corpus;
  QueryFormat queryFormat;

  

  
  
  // instantiate variables
  @BeforeEach
  void setUp() {
    tfScore = new TFScore();
    tfidfScore = new TFIDFScore();
    tficfScore = new TFICFScore();
    
    // create the test database needed for many(all?) of the following tests. 
    sites = new HashSet<>();
    sites.add(new Website("example1.com", "example1", Arrays.asList("word1", "word2", "word1", "word1")));
    sites.add(new Website("example2.com", "example2", Arrays.asList("word2", "word2", "word3", "word3")));
    sites.add(new Website("example3.com", "example3", Arrays.asList("word2", "word2", "word3", "word3", "word4")));
    sites.add(new Website("example4.com", "example4", Arrays.asList("word2", "word2", "word2", "word3", "word4")));
    sites.add(new Website("example5.com", "example5", Arrays.asList("word2", "word2", "word3", "word3", "word4")));
    sites.add(new Website("example6.com", "example6", Arrays.asList("word2", "word2", "word3", "word3", "word4")));
    sites.add(new Website("example7.com", "example7", Arrays.asList("word2", "word3", "word4", "word5", "word6")));
        
    // build corpus for test database.
    corpus = new Corpus(sites);
    corpus.build();
    corpus.build2GramIndex();
    
    System.out.println(sites.toString());
  }

  // make old objects available to be garbage collected, by removing the reference to them.
  @AfterEach
  void tearDown() {
    tfScore = null;
    tfidfScore = null;
    tficfScore = null;
  }
  
  
  /**
   * Test all our score(sub)classes which implements the Score interface. 
   */
  private void testRankMethod(Score score) {
    
    double tolerance = 0.000001;
    System.out.println("Tolerance is set to: " + tolerance);
    
    List<List<String>> structuredQuery1 = queryFormat.structure("word1 word2");
    List<List<String>> structuredQuery2 = queryFormat.structure("word2 word1");
    List<List<String>> structuredQuery3 = queryFormat.structure("   word1    word2   ");
    List<List<String>> structuredQuery4 = queryFormat.structure(" word2  word1   ");
    
    // create specific website and check if expectations hold.
    for (Website site : sites) {
      
      
      
      // Order of search words shouldn't matter.
      assertTrue( Math.abs(score.rank(site, corpus, structuredQuery1) - score.rank(site, corpus, structuredQuery2)) < tolerance );    
      
      // whitespaces between, before, or after search words shouldn't matter. FIX ME: this is actually a test of queryFormat. 
      assertTrue( Math.abs(score.rank(site, corpus, structuredQuery3) - score.rank(site, corpus, structuredQuery4)) < tolerance );  
    }
    
  }
  
//  @Test
//  void testTFScore() {
//    testRankMethod(tfScore);
//  }

  @Test
  void testTFIDFScore() {
    testRankMethod(tfidfScore);
  }
  
//  @Test
//  void testTFICFcore() {
//    testRankMethod(tficfScore);
//  }
  
}
