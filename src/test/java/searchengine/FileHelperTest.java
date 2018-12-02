package searchengine;

import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FileHelperTest {
  @Test
  void parseGoodFile() {
    Set<Website> sites = FileHelper.parseFile("data/test-file.txt");
    testSites(sites);
  }
  
  @Test
  void parseBadFile() {
    Set<Website> sites = FileHelper.parseFile("data/test-file-errors.txt");
    testSites(sites);
  }
  
  private void testSites(Set<Website> sites) {
    assertEquals(2, sites.size());
    for (Website site : sites ) {
      if (site.getTitle().equals("title1")) {
        assertTrue(site.containsWord("word1"));
        assertFalse(site.containsWord("word3"));
      } else if (site.getTitle().equals("title2")) {
        assertTrue(site.containsWord("word1"));
      } else {
        fail("The title should either be title1 or title2");
      }
    }
  }

}
