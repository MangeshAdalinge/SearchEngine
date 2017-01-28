package set.docprocess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import set.beans.TokenDetails;
import set.gui.MainJFrame;

/**
 * @author Durvijay Sharma
 * @author Mangesh Adalinge
 * @author Surabhi Dixit
 */
public class PositionalInvertedIndex implements Indexing {

	public static HashMap<String, List<TokenDetails>> indexMap = new HashMap<>();

	/**
	 * maps Positon of the token and documentId to the tokenDetail Object and
	 * stores it in hashmap
	 * 
	 * @param term
	 * @param docID
	 * @param wordPosition
	 */
	@Override
	public void addTerm(String term, int docID, int wordPosition) {
		List<TokenDetails> list = new ArrayList<>();
		TokenDetails docList = new TokenDetails(docID, wordPosition);

		try {
			// term=pStem.processToken(term);
			if (indexMap.containsKey(term)) {
				list = indexMap.get(term);

				docList = list.get(list.size() - 1);
				if (docList.getDocId() == docID) {
					docList.setPosition(wordPosition);
				} else {

					docList = new TokenDetails(docID, wordPosition);
					list.add(docList);
					indexMap.put(term, list);
				}
			} else {

				list.add(docList);
				indexMap.put(term, list);
			}

		} catch (Exception e) {
			Logger.getLogger(PositionalInvertedIndex.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
			System.out.print("addTerm  " + e);
		}

	}

	@Override
	public void addTerm(String term1, String term2, Integer documentID) {
		// TODO Auto-generated method stub

	}

	/**
	 * returns list of documents id containing the term for PI index
	 * 
	 * @param term
	 * @return
	 */
	@Override
	public List<TokenDetails> getPostings(String term) {
		return indexMap.get(term);
	}

	/**
	 * returns size of the PI index
	 * 
	 * @return
	 */
	@Override
	public int getTermCount() {
		return indexMap.size();
	}

	/**
	 * returns complete list of the vocabulary term of the PI index
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public String[] getDictionary() {
		// TO-DO: fill an array of Strings with all the keys from the hashtable.
		// Sort the array and return it.
		String[] dictionary = new String[indexMap.size()];
		Iterator it = indexMap.entrySet().iterator();
		int i = 0;

		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			dictionary[i] = pair.getKey().toString();

			i++;

		}
		Arrays.sort(dictionary);
		return dictionary;
	}

}
