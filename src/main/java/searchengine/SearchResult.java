package searchengine;

import java.util.List;
import java.util.ArrayList;

/**
 * A {@code SearchResult} contains a list of matching websites – and potentially meta data
 *
 * @author André Mortensen Kobæk
 * @author Domenico Villani
 * @author Flemming Westberg
 * @author Mikkel Buch Smedemand
 */
public class SearchResult {

    // The list of ordered, matching websites
    private List<Website> matchingWebsites;


    /** Construct an empty {@code SearchResult} */
    public SearchResult() {
        this.matchingWebsites = new ArrayList<>();
    }

    /**
     * Construct a {@code SearchResult} by passing an ordered list of matching websites
     * 
     * @param matchingWebsites an ordered list of matching websites
     */
    public SearchResult(List<Website> matchingWebsites) {
        this.matchingWebsites = matchingWebsites;
    }


    /** Returns a {@code List} of matching websites */
    public List<Website> getMatchingWebsites() {
        return matchingWebsites;
    }

    /** Returns the count of matching websites */
    public int getWebsiteCount() {
        return matchingWebsites.size();
    }
}
