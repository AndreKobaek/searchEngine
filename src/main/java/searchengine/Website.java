package searchengine;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A website is the basic entity of the search engine. It has a url, a title, and a list of words.
 *
 * @author Martin Aumüller
 */
public class Website {

  /**
   * The website's title
   */
  private String title;

  /**
   * The website's url
   */
  private String url;

  /**
   * A list of words storing the words on the website
   */
  private List<String> words;

  /**
   * A map of words mapped to the respective term frequecy score
   */
  private Map<String, Double> wordTfScore;

  /**
   * A map of words mapped to the respective term frequency score
   */
  private Map<String, Double> wordTfIdfScore;

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
    wordTfScore = new TreeMap<>();
    wordTfIdfScore = new TreeMap<>();
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

  /**
   * Get the score of a single word into the wordTFScore
   * @return wordTfScore map
   */
  public Double getWordTfScore(String word) {
    return wordTfScore.get(word);
  }

  /**
   * Takes a word and a score and pur it into a TreeMap
   * @param word
   * @param score
   */
  public void setWordTFScore(String word, Double score) {
    wordTfScore.put(word.toLowerCase(),score);
  }

  /**
   * Get the score of a single word into the wordTfIdfScore
   * @return wordTfIdfScore
   */
  public Double getWordTfIdfScore(String word) {

    return wordTfIdfScore.get(word.toLowerCase());
  }


  /**
   * Takes a word and a score and put it into a TreeMap
   * @param word
   * @param score
   */
  public void setWordTfIdfScore(String word, Double score) {

    word = word.toLowerCase();
    wordTfIdfScore.put(word, score);
  }
}
