package set.disk;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import set.beans.TokenDetails;
import set.docprocess.BiWordIndexing;
import set.docprocess.PositionalInvertedIndex;
import set.gui.IndexPanel;

/**
 * @author Durvijay Sharma
 * @author Mangesh Adalinge
 * @author Surabhi Dixit
 */
/**
 * Writes an inverted indexing of a directory to disk.
 */
public class IndexWriter {

	private static HashMap<Integer, Double> docWeightLd = new HashMap<>();

	/**
	 * Constructs an IndexWriter object which is prepared to index the given
	 * folder.
	 */
	public IndexWriter(String folderPath) {
	}

	/**
	 * Builds the postings.bin file for the indexed directory, using the given
	 * NaiveInvertedIndex of that directory.
	 * 
	 * @param bIndex
	 * @param fileType
	 */
	public void buildPostingsFile(String folder, PositionalInvertedIndex pIndex, BiWordIndexing bIndex,
			String[] dictionary, long[] vocabPositions, String fileType) {
		FileOutputStream postingsFile = null;
		try {
			postingsFile = new FileOutputStream(new File(folder, fileType + "postings.bin"));

			// simultaneously build the vocabulary table on disk, mapping a term
			// index to a
			// file location in the postings file.
			FileOutputStream vocabTable = new FileOutputStream(new File(folder, fileType + "vocabTable.bin"));

			// the first thing we must write to the vocabTable file is the
			// number of vocab terms.
			byte[] tSize = ByteBuffer.allocate(4).putInt(dictionary.length).array();
			vocabTable.write(tSize, 0, tSize.length);
			int vocabI = 0;
			List<TokenDetails> postings = new ArrayList<>();
			for (String s : dictionary) {
				// for each String in dictionary, retrieve its postings.
				if (fileType.equals("P")) {
					postings = PositionalInvertedIndex.indexMap.get(s);
				} else if (fileType.equals("B")) {
					postings = BiWordIndexing.indexMap.get(s);
				}
				// write the vocab table entry for this term: the byte location
				// of the term in the vocab list file,
				// and the byte location of the postings for the term in the
				// postings file.
				byte[] vPositionBytes = ByteBuffer.allocate(8).putLong(vocabPositions[vocabI]).array();
				vocabTable.write(vPositionBytes, 0, vPositionBytes.length);

				// System.out.println("Position in postings.bin where the doc
				// freq of this term starts : "+s+"
				// "+postingsFile.getChannel().position());
				byte[] pPositionBytes = ByteBuffer.allocate(8).putLong(postingsFile.getChannel().position()).array();
				vocabTable.write(pPositionBytes, 0, pPositionBytes.length);

				// write the postings file for this term. first, the document
				// frequency for the term, then
				// the document IDs, encoded as gaps.
				byte[] docFreqBytes = ByteBuffer.allocate(4).putInt(postings.size()).array();
				postingsFile.write(docFreqBytes, 0, docFreqBytes.length);

				int lastDocId = 0;
				for (TokenDetails docId1 : postings) {
					byte[] docIdBytes = ByteBuffer.allocate(4).putInt(docId1.getDocId() - lastDocId).array(); // encode
																												// a
																												// gap,
																												// not
																												// a
																												// doc
																												// ID
					postingsFile.write(docIdBytes, 0, docIdBytes.length);
					double weightdoc = 1 + Math.log((double) docId1.getPosition().size());
					if (docWeightLd.containsKey(docId1.getDocId())) {
						docWeightLd.put(docId1.getDocId(), docWeightLd.get(docId1.getDocId()) + Math.pow(weightdoc, 2));
					} else {
						docWeightLd.put(docId1.getDocId(), Math.pow(weightdoc, 2));
					}
					byte[] weightDocTerm = ByteBuffer.allocate(8).putDouble(weightdoc).array(); // encode
																								// a
																								// gap,
																								// not
																								// a
																								// doc
																								// ID
					byte[] sizeOfPositionsBytes = ByteBuffer.allocate(4).putInt(docId1.getPosition().size()).array(); // encode
																														// token
																														// pos
					postingsFile.write(weightDocTerm, 0, weightDocTerm.length);
					postingsFile.write(sizeOfPositionsBytes, 0, sizeOfPositionsBytes.length);
					for (Integer pos : docId1.getPosition()) {
						byte[] positionBytes = ByteBuffer.allocate(4).putInt(pos).array(); // encode
																							// token
																							// pos
						postingsFile.write(positionBytes, 0, positionBytes.length);
					}
					lastDocId = docId1.getDocId();
				}
				vocabI++;
			}
			vocabTable.close();
			postingsFile.close();
		} catch (FileNotFoundException ex) {
		} catch (IOException ex) {
		} finally {
			try {
				postingsFile.close();
			} catch (IOException ex) {
			}
		}
	}

	/**
	 * 
	 * @param folder
	 * @param dictionary
	 * @param vocabPositions
	 * @param fileType
	 */
	public void buildVocabFile(String folder, String[] dictionary, long[] vocabPositions, String fileType) {
		OutputStreamWriter vocabList = null;
		try {
			// first build the vocabulary list: a file of each vocab word
			// concatenated together.
			// also build an array associating each term with its byte location
			// in this file.
			int vocabI = 0;
			vocabList = new OutputStreamWriter(new FileOutputStream(new File(folder, fileType + "vocab.bin")), "ASCII");

			int vocabPos = 0;
			for (String vocabWord : dictionary) {
				// for each String in dictionary, save the byte position where
				// that term will start in the vocab file.
				vocabPositions[vocabI] = vocabPos;
				vocabList.write(vocabWord); // then write the String
				vocabI++;
				vocabPos += vocabWord.length();
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				vocabList.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Writes the Ld values to disk
	 * 
	 * @param folder
	 * @param fileType
	 * @throws NullPointerException
	 * @throws Exception
	 */
	public static void builDocWeightFile(String folder, String fileType) throws NullPointerException, Exception {
		try {
			FileOutputStream docWeight = new FileOutputStream(new File(folder, fileType + "docWeights.bin"));
			byte[] lDvalue;
			for (int i = 0; i < IndexPanel.fileNameLists.size(); i++) {
				if (null != docWeightLd.get(i)) {
					lDvalue = ByteBuffer.allocate(8).putDouble(Math.sqrt(docWeightLd.get(i))).array();
				} else {
					lDvalue = ByteBuffer.allocate(8).putDouble(0).array();
				}
				docWeight.write(lDvalue, 0, lDvalue.length);
			}
			docWeight.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Writes the list of file names on disk
	 * 
	 * @param path
	 * @param fileList
	 */
	public static void buildFilename(String path, HashMap<Integer, File> fileList) {
		try {
			File fileOne = new File(path + "/" + "FileList");
			FileOutputStream fos = new FileOutputStream(fileOne);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(fileList);
			oos.flush();
			oos.close();
			fos.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Writes Kgrams on disk
	 * 
	 * @param path
	 * @param kgrams
	 */
	public static void buildKGramFile(String path, HashMap<String, List<String>> kgrams) {
		try {
			File fileOne = new File(path + "/" + "KGram");
			FileOutputStream fos = new FileOutputStream(fileOne);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(kgrams);
			oos.flush();
			oos.close();
			fos.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
