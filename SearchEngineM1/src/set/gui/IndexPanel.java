
package set.gui;

import com.google.gson.Gson;

import set.beans.JsonFile;
import set.docprocess.Indexing;
import set.docprocess.SimpleTokenStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * @author Durvijay Sharma
 * @author Mangesh Adalinge
 * @author Surabhi Dixit
 */
public class IndexPanel extends javax.swing.JPanel {

	private Path currentDirectory;
	private HashMap<Integer, File> fileNameLists = new HashMap<>();
	private Indexing indexingObj = new Indexing();
	private JTextField txtTotalFiles;
	private JTextField txtTotalTime;

	/**
	 * constructor to set indexlist and pass the totalfile list and total time
	 * 
	 * @param btnLabel
	 * @param txtTotalFiles
	 * @param txtTotalTime
	 */
	public IndexPanel(String btnLabel, JTextField txtTotalFiles, JTextField txtTotalTime) {
		initComponents();
		btnIndex.setText(btnLabel);
		this.txtTotalFiles = txtTotalFiles;
		this.txtTotalTime = txtTotalTime;
	}

	// getter for index object
	public Indexing getIndexingObj() {
		return indexingObj;
	}

	// setter for index object
	public void setIndexingObj(Indexing indexingObj) {
		this.indexingObj = indexingObj;
	}

	// getter for file name list
	public HashMap<Integer, File> getFileNameLists() {
		return fileNameLists;
	}

	// setter for file name list
	public void setFileNameLists(HashMap<Integer, File> fileNameLists) {
		this.fileNameLists = fileNameLists;
	}

	// Code">//GEN-BEGIN:initComponents
	/**
	 * Index Frame U.I design components ex: buttons,left pane, right pane etc.
	 * Button action event listeners
	 */
	private void initComponents() {

		btnBrowse = new javax.swing.JButton();
		btnIndex = new javax.swing.JButton();
		txtFolderSelect = new javax.swing.JTextField();

		btnBrowse.setText("Browse");
		btnBrowse.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnBrowseActionPerformed(evt);
			}
		});

		btnIndex.setText("Index");
		btnIndex.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnIndexActionPerformed(evt);
			}
		});

		txtFolderSelect.setEditable(false);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap()
						.addComponent(txtFolderSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 359,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(btnBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 90,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(28, 28, 28).addComponent(btnIndex, javax.swing.GroupLayout.PREFERRED_SIZE, 92,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(149, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup().addContainerGap()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
						.addComponent(btnIndex, javax.swing.GroupLayout.Alignment.TRAILING,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)
						.addComponent(txtFolderSelect, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
						.addComponent(btnBrowse, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
				.addContainerGap(22, Short.MAX_VALUE)));
	}// </editor-fold>//GEN-END:initComponents

	private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnBrowseActionPerformed
		JFileChooser fileChooser = new JFileChooser();

		// For Directory
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		// For File
		// fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		fileChooser.setAcceptAllFileFilterUsed(false);
		int rVal = fileChooser.showOpenDialog(null);
		if (rVal == JFileChooser.APPROVE_OPTION) {
			txtFolderSelect.setText(fileChooser.getSelectedFile().toString());
		} else {
			System.out.println(".actionPerformed()");
		}
	}// GEN-LAST:event_btnBrowseActionPerformed

	/**
	 * start indexing button event listener
	 * also used to reset and set total file list and total time required
	 * for indexing
	 * @param evt
	 */
	private void btnIndexActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnIndexActionPerformed
		try {

			if (!txtFolderSelect.getText().trim().equalsIgnoreCase("")) {
				txtTotalFiles.setText("");
				txtTotalTime.setText("");
				Gson gson = new Gson();
				long startTime = System.nanoTime();
				fileNameLists = new HashMap<>();
				currentDirectory = Paths.get(txtFolderSelect.getText());

				Files.walkFileTree(currentDirectory, new SimpleFileVisitor<Path>() {
					int l = 0;

					public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
						// make sure we only process the current working
		
						if (currentDirectory != null) {
							return FileVisitResult.CONTINUE;
						}
						return FileVisitResult.SKIP_SUBTREE;
					}

					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
							throws FileNotFoundException {
						// only process .json files
						if (file.toString().endsWith(".json")) {
							fileNameLists.put(l, file.toFile());
							JsonFile fileclass = gson.fromJson(new FileReader(file.toFile().getAbsolutePath()),
									JsonFile.class);
							retrieveToken(fileclass.getBody(), indexingObj, l);

							l++;
						}
						return FileVisitResult.CONTINUE;
					}

					// don't throw exceptions if files are locked/other errors
					// occur
					public FileVisitResult visitFileFailed(Path file, IOException e) {

						return FileVisitResult.CONTINUE;
					}
				});

				long endTime = System.nanoTime();
				long totalTime = endTime - startTime;
				System.out.println("Total Indexing Time" + TimeUnit.NANOSECONDS.toMinutes(totalTime));
				if (fileNameLists.size() > 0) {
					txtTotalFiles.setText(Integer.toString(fileNameLists.size()));//setting total file size
					txtTotalTime.setText(Long.toString(TimeUnit.NANOSECONDS.toSeconds(totalTime)));//setting total indexing time
					JOptionPane.showMessageDialog(null, "Indexing Complete for " + fileNameLists.size() + " files");
				}

			} else {
				JOptionPane.showMessageDialog(null, "Please Select Folder To Index");
			}
		} catch (IOException ex) {
			Logger.getLogger(IndexPanel.class.getName()).log(Level.SEVERE, null, ex);
		}
	}// GEN-LAST:event_btnIndexActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton btnBrowse;
	private javax.swing.JButton btnIndex;
	private javax.swing.JTextField txtFolderSelect;
	// End of variables declaration//GEN-END:variables

	/**
	 * retrieve individual token from the body and process, also passes the token for Position Inverted indexing
	 * and biword index
	 * @param body
	 * @param index
	 * @param docID
	 */
	private void retrieveToken(String body, Indexing index, int docID) {
		try {
			int i = 1;
			SimpleTokenStream st = new SimpleTokenStream(body);
			String token1 = "";
			String token2 = "";
			if (st.hasNextToken()) {
				token1 = st.nextToken();
				//PI INDEX
				invertedIndexTerm(token1, token2, docID, 0, index);
			}
			while (st.hasNextToken()) {
				token2 = st.nextToken();
				// BIWORD INDEX
				index.AddBiWordTerm(index.processWord(token1), index.processWord(token2), docID);
				// PI INDEX
				invertedIndexTerm(token2, token2, docID, i, index);
				i++;
				token1 = token2;
			}
		} catch (Exception e) {
			Logger.getLogger(IndexPanel.class.getName()).log(java.util.logging.Level.SEVERE, null,e);
			e.printStackTrace();
		}
	}

	//passing token to Index file for PI Index
	private void invertedIndexTerm(String token1, String token2, Integer docid, Integer i, Indexing index) {
		if (token1.contains("-")) {
			for (String splitTok : index.processWordHypen(token1)) {
				index.addTermInvertedIndex(index.processWord(splitTok), docid, i);
			}
			index.addTermInvertedIndex(index.processWord(token1.replaceAll("-", "")), docid, i);

		} else {
			index.addTermInvertedIndex(index.processWord(token1), docid, i);
		}
	}

}
