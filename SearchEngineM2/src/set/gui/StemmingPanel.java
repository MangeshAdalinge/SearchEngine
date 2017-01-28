/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package set.gui;

import javax.swing.JOptionPane;

import set.docprocess.PorterStemmer;

/**
 * class StemmingPanel
 * 
 * @author Durvijay Sharma
 * @author Mangesh Adalinge
 * @author Surabhi Dixit
 */
public class StemmingPanel extends javax.swing.JPanel {

	/**
	 * Creates new form Stemming
	 */
	public StemmingPanel() {
		initComponents();
	}

	/**
	 * Stemming Frame U.I design components ex: buttons,left pane, right pane
	 * etc. Button action event listeners
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jScrollPane1 = new javax.swing.JScrollPane();
		txtAreaStem = new javax.swing.JTextArea();
		btnStem = new javax.swing.JButton();
		jScrollPane2 = new javax.swing.JScrollPane();
		txtStemOput = new javax.swing.JTextArea();

		txtAreaStem.setColumns(20);
		txtAreaStem.setLineWrap(true);
		txtAreaStem.setRows(5);
		txtAreaStem.setWrapStyleWord(true);
		jScrollPane1.setViewportView(txtAreaStem);

		btnStem.setText("Stem");
		btnStem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnStemActionPerformed(evt);
			}
		});

		txtStemOput.setEditable(false);
		txtStemOput.setColumns(20);
		txtStemOput.setLineWrap(true);
		txtStemOput.setRows(5);
		txtStemOput.setWrapStyleWord(true);
		jScrollPane2.setViewportView(txtStemOput);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup().addGap(43, 43, 43)
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
										.addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 459,
												Short.MAX_VALUE)
										.addComponent(jScrollPane1))
								.addGap(40, 40, 40).addComponent(btnStem, javax.swing.GroupLayout.PREFERRED_SIZE, 120,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addContainerGap(80, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup().addGap(30, 30, 30).addComponent(jScrollPane1,
								javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(layout.createSequentialGroup().addGap(71, 71, 71).addComponent(btnStem)))
				.addGap(42, 42, 42)
				.addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addContainerGap(215, Short.MAX_VALUE)));
	}// </editor-fold>//GEN-END:initComponents

	/**
	 * stem button click action event listener
	 * 
	 * @param evt
	 */
	private void btnStemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnStemActionPerformed
		String[] userInputWords = null;
		StringBuilder stemResult = new StringBuilder("");
		if (txtAreaStem.getText().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Please Enter words before stemming");
		} else {
			userInputWords = txtAreaStem.getText().split(" ");
			for (String userInputWord : userInputWords) {
				stemResult.append(PorterStemmer.processWordAndStem(userInputWord)).append(" ");
			}
			txtStemOput.setText(stemResult.toString());
		}
	}// GEN-LAST:event_btnStemActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton btnStem;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JTextArea txtAreaStem;
	private javax.swing.JTextArea txtStemOput;
	// End of variables declaration//GEN-END:variables
	// private PorterStemmer pStemmer = new PorterStemmer();
}
