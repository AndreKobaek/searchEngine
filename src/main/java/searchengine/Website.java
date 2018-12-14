package searchengine;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
   * A {@code Map} from words to number of occurences in the {@code Website}
   */
  private Map<String, Integer> wordsToOccurences;

  /**
   * The number of words on the website.
   */
  private int wordCount;

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
    this.wordCount = words.size();

    // build the map which holds words and corresponding word counts for the website.
    wordsToOccurences = new HashMap<>();
    for (String word : words) {
      if (wordsToOccurences.containsKey(word)) {
        wordsToOccurences.put(word, wordsToOccurences.get(word) + 1);
      } else {
        wordsToOccurences.put(word, 1);
      }
    }
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
   * Returns the {@Map} of words to its occurences
   *
   * @return Returns the {@Map} of words to its occurences
   */
  public Map<String, Integer> getWordsToOccurences() {
    return wordsToOccurences;
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
   * Returns the number of words in list of words.
   * 
   * @return number of words in list of words.
   */
  public int getWordCount() {
    return wordCount;
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
