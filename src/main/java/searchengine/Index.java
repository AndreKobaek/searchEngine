package searchengine;

import java.util.Set;

/**
 * The index data structure provides a way to build an index from a set of websites. It allows to
 * lookup the websites that contain a query word.
 *
 * @author Martin Aumüller
 */
public interface Index {

  /**
   * The build method processes a set of websites into the index data structure.
   *
   * @param sites The set of websites that should be indexed
   */
  void build(Set<Website> sites);

  /**
   * Given a query string, returns a set of all websites that contain the query.
   * 
   * @param query The query
   * @return the set of websites that contains the query word.
   */
  Set<Website> lookup(String query);
}
