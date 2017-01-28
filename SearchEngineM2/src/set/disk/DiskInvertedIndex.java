package set.disk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.*;
import set.beans.TokenDetails;
import set.gui.IndexPanel;

/**
 * @author Durvijay Sharma
 * @author Mangesh Adalinge
 * @author Surabhi Dixit
 */
public class DiskInvertedIndex {

	private RandomAccessFile mVocabList;
	private RandomAccessFile mPostings;
	private long[] mVocabTable;
	private List<String> mFileNames;
	private String[] dictionary;

	public String[] getDictionary() {
		return dictionary;
	}

	public void setDictionary(String[] dictionary) {
		this.dictionary = dictionary;
	}

	public DiskInvertedIndex(String fileType) throws IOException {
		try {
			mVocabList = new RandomAccessFile(new File(IndexPanel.currentDirectory.toString(), fileType + "vocab.bin"),
					"r");
			mPostings = new RandomAccessFile(
					new File(IndexPanel.currentDirectory.toString(), fileType + "postings.bin"), "r");
			mVocabTable = readVocabTable(IndexPanel.currentDirectory.toString(), fileType);
			dictionary = readVocabWordValueFromFile();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
	}

	public RandomAccessFile getmPostings() {
		return mPostings;
	}

	public void setmPostings(RandomAccessFile mPostings) {
		this.mPostings = mPostings;
	}

	public List<TokenDetails> readPostingsFromFile(RandomAccessFile postings, long postingsPosition, String term) {
		try {
			// seek to the position in the file where the postings start.
			postings.seek(postingsPosition);

			// read the 4 bytes for the document frequency
			byte[] buffer = new byte[4];
			byte[] docWeightbuffer = new byte[8];
			postings.read(buffer, 0, buffer.length);

			// use ByteBuffer to convert the 4 bytes into an int.
			int documentFrequency = ByteBuffer.wrap(buffer).getInt();

			// write the following code:
			// read 4 bytes at a time from the file, until you have read as many
			// postings as the document frequency promised.
			//
			// after each read, convert the bytes to an int posting. this value
			// is the GAP since the last posting. decode the document ID from
			// the gap and put it in the array.
			//
			// repeat until all postings are read.
			int docId = 0;
			double docWeight = 0;
			List<TokenDetails> docdetails = new ArrayList<>();
			for (int i = 0; i < documentFrequency; i++) {
				TokenDetails tokDet = new TokenDetails();
				postings.read(buffer, 0, buffer.length);
				docId = ByteBuffer.wrap(buffer).getInt() + docId;
				postings.read(docWeightbuffer, 0, docWeightbuffer.length);
				docWeight = ByteBuffer.wrap(docWeightbuffer).getDouble();
				tokDet.setDocWeight(docWeight);
				postings.read(buffer, 0, buffer.length);
				int noOfPostings = ByteBuffer.wrap(buffer).getInt();
				
				tokDet.setDocId(docId);

				for (int j = 0; j < noOfPostings; j++) {
					postings.read(buffer, 0, buffer.length);
					int positions = ByteBuffer.wrap(buffer).getInt();
					tokDet.setPosition(positions);
				}
				docdetails.add(tokDet);
			}
			return docdetails;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public long binarySearchVocabulary(String term) {

		// do a binary search over the vocabulary, using the vocabTable and the
		// file vocabList.
		int i = 0, j = mVocabTable.length / 2 - 1;
		while (i <= j) {
			try {
				int m = (i + j) / 2;
				long vListPosition = mVocabTable[m * 2];
				int termLength;
				if (m == mVocabTable.length / 2 - 1) {
					termLength = (int) (mVocabList.length() - mVocabTable[m * 2]);
				} else {
					termLength = (int) (mVocabTable[(m + 1) * 2] - vListPosition);
				}
				// termLength=termLength+1;
				mVocabList.seek(vListPosition);

				byte[] buffer = new byte[termLength];
				mVocabList.read(buffer, 0, termLength);
				String fileTerm = new String(buffer, "ASCII");

				int compareValue = term.compareTo(fileTerm);

				if (compareValue == 0) {
					// found it!
					return mVocabTable[m * 2 + 1];
				} else if (compareValue < 0) {
					j = m - 1;
				} else {
					i = m + 1;
				}
			} catch (IOException ex) {
				System.out.println(ex.toString());
			}
		}
		return -1;
	}

	private long[] readVocabTable(String indexName, String fileType) {
		try {
			long[] vocabTable;

			RandomAccessFile tableFile = new RandomAccessFile(new File(indexName, fileType + "vocabTable.bin"), "r");

			byte[] byteBuffer = new byte[4];
			tableFile.read(byteBuffer, 0, byteBuffer.length);

			int tableIndex = 0;
			vocabTable = new long[ByteBuffer.wrap(byteBuffer).getInt() * 2];
			byteBuffer = new byte[8];

			while (tableFile.read(byteBuffer, 0, byteBuffer.length) > 0) { // while
																			// we
																			// keep
																			// reading
																			// 4
																			// bytes
				vocabTable[tableIndex] = ByteBuffer.wrap(byteBuffer).getLong();
				tableIndex++;
			}
			tableFile.close();
			return vocabTable;
		} catch (FileNotFoundException ex) {
			System.out.println(ex.toString());
		} catch (IOException ex) {
			System.out.println(ex.toString());
		}
		return null;
	}

	/**
	 * Reads the files list from disk
	 * 
	 * @param path
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static HashMap<Integer, File> fileNameRead(String path) {
		try {
			File toRead = new File(path + "/" + "FileList");
			FileInputStream fis = new FileInputStream(toRead);
			ObjectInputStream ois = new ObjectInputStream(fis);
			HashMap<Integer, File> mapInFile = (HashMap<Integer, File>) ois.readObject();
			ois.close();
			fis.close();
			return mapInFile;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public List<String> getFileNames() {
		return mFileNames;
	}

	public int getTermCount() {
		return mVocabTable.length / 2;
	}

	/**
	 * Reads Ld value from file
	 * 
	 * @param pos
	 * @return
	 */
	public static double readLdValueFromFile(int pos) {
		double ldValue = 0;
		try {
			RandomAccessFile ldFile = new RandomAccessFile(
					new File(IndexPanel.currentDirectory.toString(), "PdocWeights.bin"), "r");
			ldFile.seek(pos);
			byte[] byteBuffer = new byte[8];
			ldFile.read(byteBuffer, 0, byteBuffer.length);
			ldValue = ByteBuffer.wrap(byteBuffer).getDouble();
			ldFile.close();
		} catch (FileNotFoundException ex) {
			System.out.println(ex.toString());
		} catch (IOException ex) {
			System.out.println(ex.toString());
		}
		return ldValue;
	}

	/**
	 * readVocabWordValueFromFile() This function gets the count and complete
	 * vocabulary list of the corpus
	 * 
	 * @return vocabulary list of corpus
	 */
	public String[] readVocabWordValueFromFile() {
		int wordLength = 0;
		int vocabLength = mVocabTable.length / 2;
		String[] vocabList = new String[vocabLength];
		try {
			for (int vocabIndex = 0; vocabIndex < vocabLength; vocabIndex++) {
				int vocabTableIndex = vocabIndex * 2;
				if (vocabTableIndex + 2 < mVocabTable.length) {
					wordLength = (int) (mVocabTable[vocabTableIndex + 2] - mVocabTable[vocabTableIndex]);
				} else {
					wordLength = (int) (mVocabList.length() - mVocabTable[vocabTableIndex]);
				}
				byte[] byteBuffer = new byte[wordLength];
				mVocabList.read(byteBuffer, 0, wordLength);
				vocabList[vocabIndex] = new String(byteBuffer, "ASCII");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return vocabList;
	}
}
