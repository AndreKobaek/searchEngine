package searchengine;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * A website is the basic entity of the search engine. It has a url, a title, and a list of words.
 * It also has a map (wordMap) that relates a word to the number of times it appears on the site,
 * This field are useful when calculating the rank of the site.
 * As mentioned the website also has a rank. The rank is supposed to be set by an external ranking algorithm. 
 * This is necessary because a website rank depends on both all the other websites in the database/corpus, and the specific query.   
 *
 */
public class Website {

  /**
   * the website's title
   */
  private String title;

  /**
   * the website's url
   */
  private String url;


  /**
   * a list of words storing the words on the website
   */
  private List<String> words;

  /**
   * A map from a specific word to the number of times it appear on the site.
   * The map is made package private so that the map can be conveniently accessed 
   * by the ranking methods thats will calculate the site rank according to a specific query. 
   */
  Map<String, Integer> wordMap; // package private

  /**
   * The currently assigned rank of the website. 
   * Rank depends on both the query and the other websites in the database/corpus, as well as on the ranking method.  
   * If the rank has not (yet) been set by an external ranking method it has the value of 0. 
   */
  private double rank = 0;

  /**
   * Creates a {@code Website} object from a url, a title, and a list of words that are contained on
   * the website.
   *
   * @param url the website's url
   * @param title the website's title
   * @param words the website's list of words
   */
  public Website(String url, String title, List<String> words) {
    this.url = url;
    this.title = title;
    this.words = words;

    // build the map which holds the words and the corresponding word counts for the website.
    wordMap = new HashMap<>();
    for (String word : words) {
      if (wordMap.containsKey(word)) {
        wordMap.put(word, wordMap.get(word) + 1);
      } else {
        wordMap.put(word, 1);
      }
    }
    assert words.size() == wordMap.values().stream().reduce(0, (total, count) -> total + count) : "Word count in the map is wrong."; // Sanity check, that all the words have been correctly counted.
  }

  /**
   * Returns the website's title.
   *
   * @return the website's title.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Returns the website's url.
   *
   * @return the website's url.
   */
  public String getUrl() {
    return url;
  }

  /**
   * Returns the website's list of words
   * 
   * @return website's list of words
   */
  public List<String> getWords() {
    return words;
  }

  /**
   * Returns the number of words in list of words. I.e the number of words on the site. 
   * 
   * @return number of words in list of words.
   */
  public int getWordSize() {
    return words.size();
  }

  /**
   * Returns the currently assigned rank of the website. 
   * 
   * @return the current rank of the website. 
   */
  public double getRank() {
    return rank;
  }

  /** 
   * Set the rank of the site. This method is used by an external ranking method
   * to set the rank of the site. 
   * 
   * @param rank the rank that is to be assigned to the website.  
   */
  public void setRank(double rank) {
    this.rank = rank;
  }

  /**
   * Checks whether a word is present on the website or not.
   *
   * @param word the query word
   * @return True, if the word is present on the website
   */
  public Boolean containsWord(String word) {
    return words.contains(word);
  }

  @Override
  public String toString() {
    return "Website{" + "title='" + title + '\'' + ", url='" + url + '\'' + ", words=" + words
        + '}';
  }

}
