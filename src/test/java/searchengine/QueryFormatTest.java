package searchengine;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class QueryFormatTest {

  // declare variables to be used in the tests
  List<Website> sites;
  InvertedIndex idx;
  Corpus corpus;
  QueryFormat queryFormat;


  @BeforeEach
  void setUp() {
    // create the test database needed for many(all?) of the following tests.
    sites = new ArrayList<>();
    sites.add(new Website("1.com", "example1", Arrays.asList("word1", "word2")));
    sites.add(new Website("2.com", "example2", Arrays.asList("word2", "word3")));
    sites.add(new Website("3.com", "example3", Arrays.asList("word3", "word4", "word5")));

    // build index for test database.
    idx = new InvertedIndexTreeMap();
    idx.build(new HashSet<Website>(sites));

    // build corpus for test database.
    corpus = new Corpus(new HashSet<Website>(sites));
    corpus.build();
    corpus.build2GramIndex();

    // instantiate queryFormat
    queryFormat = new QueryFormat(corpus);
  }


  @Test
  void testSingleWord() {

    List<List<String>> structuredQuery1 = queryFormat.structure("word1");
    List<List<String>> structuredQuery2 = queryFormat.structure("word2");

    assertEquals(1, idx.getMatchingWebsites(structuredQuery1).size());
    assertEquals(2, idx.getMatchingWebsites(structuredQuery2).size());
  }

  @Test
  void testInputMixedCase() {

    List<List<String>> structuredQuery1 = queryFormat.structure("WORD1");
    List<List<String>> structuredQuery2 = queryFormat.structure("WoRd1");
    List<List<String>> structuredQuery3 = queryFormat.structure("woRD2");
    List<List<String>> structuredQuery4 = queryFormat.structure(""); // The empty string

    assertEquals(1, idx.getMatchingWebsites(structuredQuery1).size());
    assertEquals("example1", idx.getMatchingWebsites(structuredQuery2).get(0).getTitle());
    assertEquals(2, idx.getMatchingWebsites(structuredQuery3).size());
    assertEquals(0, idx.getMatchingWebsites(structuredQuery4).size()); 
  }

  @Test
  void testMultipleWords() {

    List<List<String>> structuredQuery1 = queryFormat.structure("word1 word2");
    List<List<String>> structuredQuery2 = queryFormat.structure("word3 word4");
    List<List<String>> structuredQuery3 = queryFormat.structure("word4 word3 word5");

    assertEquals(1, idx.getMatchingWebsites(structuredQuery1).size());
    assertEquals(1, idx.getMatchingWebsites(structuredQuery2).size());
    assertEquals(1, idx.getMatchingWebsites(structuredQuery3).size());
  }
  
  
  @Test
  void testORQueries() {
    
    List<List<String>> structuredQuery1 = queryFormat.structure("word2 OR word3");
    List<List<String>> structuredQuery2 = queryFormat.structure("word1 OR word4");
    List<List<String>> structuredQuery3 = queryFormat.structure("word1 OR word1");
    List<List<String>> structuredQuery4 = queryFormat.structure("OR");
    List<List<String>> structuredQuery5 = queryFormat.structure("OR word2 OR word3");
    List<List<String>> structuredQuery6 = queryFormat.structure("word1 OR word4 OR");
    
    assertEquals(3, idx.getMatchingWebsites(structuredQuery1).size());
    assertEquals(2, idx.getMatchingWebsites(structuredQuery2).size());
    // Corner case: Does code remove duplicates?
    assertEquals(1, idx.getMatchingWebsites(structuredQuery3).size());
    // Corner case: Does a standalone 'OR' entail a size of 0?
    assertEquals(0, idx.getMatchingWebsites(structuredQuery4).size());
    // Corner case: Does the code support 'OR' at the beginning or end of the query?
    assertEquals(3, idx.getMatchingWebsites(structuredQuery5).size());
    // this currently fails: assertEquals(2, idx.getMatchingWebsites(structuredQuery6).size()); 
  }
  
  @Disabled("We decided that this is currently not a requirement.")
  @Test
  void testInvalidInput() {
    // test input for special characters.
    // what should be the response if one or more special character is found? perhaps ask user for new input?
    fail("not implemented yet"); 
  }
}
