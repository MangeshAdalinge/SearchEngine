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
public class BiWordIndexing implements Indexing {

	public static HashMap<String, List<TokenDetails>> indexMap = new HashMap<>();



	@Override
	public List<TokenDetails> getPostings(String term) {
		return indexMap.get(term);
	}

	@Override
	public int getTermCount() {
		return indexMap.size();
	}

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

	@Override
	public void addTerm(String term, int docID, int wordPosition) {
		// TODO Auto-generated method stub

	}

	/**
	 * maps Positon of the token by combining first and second token and
	 * documentId to the tokenDetail Object and stores it in hashmap
	 * 
	 * @param term1
	 * @param term2
	 * @param documentID
	 */
	@Override
	public void addTerm(String term1, String term2, Integer documentID) {
		String term = term1 + " " + term2;
		List<TokenDetails> mArr = new ArrayList<>();
		TokenDetails biDocList = new TokenDetails();
		
		if (indexMap.containsKey(term)) {
			mArr = indexMap.get(term);
			int docId = mArr.get(mArr.size() - 1).getDocId();
			if (documentID != docId) {
				biDocList.setDocId(documentID);
				indexMap.get(term).add(biDocList);
			}

		} else {
			biDocList.setDocId(documentID);

			mArr.add(biDocList);
			indexMap.put(term, mArr);
		}

	}

}
