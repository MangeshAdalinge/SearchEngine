/**
 * 
 */
package set.docprocess;

import java.util.HashMap;
import java.util.List;

import set.beans.TokenDetails;

/**
 * @author Durvijay.sharma
 *
 */
public interface Indexing {

	void addTerm(String term, int docID, int wordPosition);

	void addTerm(String term1, String term2, Integer documentID);

	List<TokenDetails> getPostings(String term);

	int getTermCount();

	String[] getDictionary();

}
