package searchengine;

/**
 * The Score data structure provide a way to get the score of a word matching a website
 */
public interface Score {
    /**
     * Given a word it returns the score based on how many times that word
     * is present into the website using a certain index.
     * @param word
     * @param website
     * @return a float number representing the tf-idf score of the word in the website
     */
    double getScore(String word, Website website, Index idx);
}
