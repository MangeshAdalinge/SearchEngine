
package set.gui;

import java.io.IOException;

import javax.swing.JOptionPane;

import set.docprocess.BiWordIndexing;
import set.docprocess.PositionalInvertedIndex;
import set.gui.IndexPanel;
import set.gui.StemmingPanel;
import set.gui.VocabPanel;

/**
 * @author Durvijay Sharma
 * @author Mangesh Adalinge
 * @author Surabhi Dixit
 */
public class MainJFrame extends javax.swing.JFrame {
	private PositionalInvertedIndex pInvertedIndex = null;
	private BiWordIndexing bIndexing = null;

	/**
	 * Creates new form MainJFrame
	 */
	public MainJFrame() {
		initComponents();

	}

	/**
	 * Main Frame U.I design components ex: buttons,left pane, right pane etc.
	 * Button action event listeners
	 */
	private void initComponents() {

		SplitPane = new javax.swing.JSplitPane();
		leftPanel = new javax.swing.JPanel();
		btnExit = new javax.swing.JButton();
		btnStem = new javax.swing.JButton();
		btnIndex = new javax.swing.JButton();
		btnVocab = new javax.swing.JButton();
		btnQuery = new javax.swing.JButton();
		lblTotalTime = new javax.swing.JLabel();
		txtTotalTime = new javax.swing.JTextField();
		lblTotalFiles = new javax.swing.JLabel();
		txtTotalFiles = new javax.swing.JTextField();
		btnRank = new javax.swing.JButton();
		rightPanel = new javax.swing.JPanel();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		leftPanel.setPreferredSize(new java.awt.Dimension(185, 266));

		btnExit.setText("Exit");
		btnExit.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnExitActionPerformed(evt);
			}
		});

		btnStem.setText("Stem");
		btnStem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnStemActionPerformed(evt);
			}
		});

		btnIndex.setText("Index");
		btnIndex.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnIndexActionPerformed(evt);
			}
		});

		btnVocab.setText("Vocab");
		btnVocab.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					btnVocabActionPerformed(evt);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		btnQuery.setText("Boolean Query");
		btnQuery.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnQueryActionPerformed(evt);
			}
		});

		lblTotalTime.setText("Total Time:");

		txtTotalTime.setEditable(false);

		lblTotalFiles.setText("Total Files:");

		txtTotalFiles.setEditable(false);

		btnRank.setText("Rank Retrieval");
		btnRank.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnRankActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout leftPanelLayout = new javax.swing.GroupLayout(leftPanel);
		leftPanel.setLayout(leftPanelLayout);
		leftPanelLayout
				.setHorizontalGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(leftPanelLayout.createSequentialGroup().addGap(18, 18, 18)
								.addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(btnRank, javax.swing.GroupLayout.PREFERRED_SIZE, 138,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGroup(leftPanelLayout.createSequentialGroup().addComponent(lblTotalFiles)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(txtTotalFiles, javax.swing.GroupLayout.PREFERRED_SIZE, 82,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addComponent(btnQuery, javax.swing.GroupLayout.PREFERRED_SIZE, 138,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(btnIndex, javax.swing.GroupLayout.PREFERRED_SIZE, 138,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(btnStem, javax.swing.GroupLayout.PREFERRED_SIZE, 138,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(btnVocab, javax.swing.GroupLayout.PREFERRED_SIZE, 138,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 138,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGroup(leftPanelLayout.createSequentialGroup().addComponent(lblTotalTime)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(txtTotalTime, javax.swing.GroupLayout.PREFERRED_SIZE, 81,
														javax.swing.GroupLayout.PREFERRED_SIZE)))
								.addContainerGap(29, Short.MAX_VALUE)));
		leftPanelLayout.setVerticalGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(leftPanelLayout.createSequentialGroup().addGap(19, 19, 19).addComponent(btnIndex)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(btnQuery)
						.addGap(13, 13, 13).addComponent(btnRank)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(btnStem)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(btnVocab)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(btnExit)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(lblTotalTime).addComponent(txtTotalTime,
										javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(lblTotalFiles).addComponent(txtTotalFiles,
										javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap(188, Short.MAX_VALUE)));

		SplitPane.setLeftComponent(leftPanel);

		rightPanel.setRequestFocusEnabled(false);
		rightPanel.setVerifyInputWhenFocusTarget(false);

		javax.swing.GroupLayout rightPanelLayout = new javax.swing.GroupLayout(rightPanel);
		rightPanel.setLayout(rightPanelLayout);
		rightPanelLayout.setHorizontalGroup(rightPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 690, Short.MAX_VALUE));
		rightPanelLayout.setVerticalGroup(rightPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 464, Short.MAX_VALUE));

		SplitPane.setRightComponent(rightPanel);

		getContentPane().add(SplitPane, java.awt.BorderLayout.CENTER);

		pack();
	}

	/**
	 * Frame exit action
	 * 
	 * @param evt
	 */
	private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnExitActionPerformed
		System.exit(0);
	}

	/**
	 * Stemming Window is created
	 * 
	 * @param evt
	 */
	private void btnStemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnStemActionPerformed
		StemmingPanel stemming = new StemmingPanel();
		SplitPane.setRightComponent(stemming);
	}// GEN-LAST:event_btnStemActionPerformed

	/**
	 * Indexing window is created
	 * 
	 * @param evt
	 */
	private void btnIndexActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnIndexActionPerformed
		if (indexpanel == null) {
			indexpanel = new IndexPanel("Index", txtTotalFiles, txtTotalTime, pInvertedIndex, bIndexing);
		}

		SplitPane.setRightComponent(indexpanel);

	}// GEN-LAST:event_btnIndexActionPerformed

	/**
	 * Query window is created
	 * 
	 * @param evt
	 */
	private void btnQueryActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnQueryActionPerformed

		/*
		 * if (IndexPanel.diskExsits == false) {
		 * messageDisplay("Please select directory to search"); } else {
		 * QueryPanel querywindow = new
		 * QueryPanel(indexpanel.getTxtFolderSelect(),indexpanel.
		 * getFileNameLists()); SplitPane.setRightComponent(querywindow);
		 * 
		 * }
		 */

		if (IndexPanel.diskExsits == false) {
			messageDisplay("Please select directory to search");
		} else {
			QueryPanel rankwindow = new QueryPanel(indexpanel.getTxtFolderSelect(), true);
			SplitPane.setRightComponent(rankwindow);

		}

	}// GEN-LAST:event_btnQueryActionPerformed

	/**
	 * Vocabulary list window is created
	 * 
	 * @param evt
	 * @throws IOException
	 */
	private void btnVocabActionPerformed(java.awt.event.ActionEvent evt) throws IOException {// GEN-FIRST:event_btnVocabActionPerformed
		if (IndexPanel.diskExsits == false) {
			messageDisplay("Please select directory to search");
		} else {
			VocabPanel vocabPanel = new VocabPanel(indexpanel.getpInvertedIndex());
			SplitPane.setRightComponent(vocabPanel);

		}
	}// GEN-LAST:event_btnVocabActionPerformed

	private void btnRankActionPerformed(java.awt.event.ActionEvent evt) {
		if (IndexPanel.diskExsits == false) {
			messageDisplay("Please select directory to search");
		} else {
			QueryPanel rankwindow = new QueryPanel(indexpanel.getTxtFolderSelect(), false);
			SplitPane.setRightComponent(rankwindow);

		}
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		// <editor-fold defaultstate="collapsed" desc=" Look and feel setting
		// code (optional) ">
		/*
		 * If Nimbus (introduced in Java SE 6) is not available, stay with the
		 * default look and feel. For details see
		 * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.
		 * html
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			System.out.println("main");
			java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		} catch (InstantiationException ex) {
			System.out.println("main");
			java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		} catch (IllegalAccessException ex) {
			System.out.println("main");
			java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			System.out.println("main");
			java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		}
		// </editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new MainJFrame().setVisible(true);
			}
		});
	}

	public void messageDisplay(String message) {
		JOptionPane.showMessageDialog(null, message);
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JSplitPane SplitPane;
	private javax.swing.JButton btnExit;
	private javax.swing.JButton btnIndex;
	private javax.swing.JButton btnQuery;
	private javax.swing.JButton btnStem;
	private javax.swing.JButton btnVocab;
	private javax.swing.JLabel lblTotalFiles;
	private javax.swing.JLabel lblTotalTime;
	private javax.swing.JPanel leftPanel;
	private javax.swing.JPanel rightPanel;
	private javax.swing.JTextField txtTotalFiles;
	private javax.swing.JTextField txtTotalTime;
	private javax.swing.JButton btnRank;
	// End of variables declaration//GEN-END:variables

	private IndexPanel indexpanel = null;
}
