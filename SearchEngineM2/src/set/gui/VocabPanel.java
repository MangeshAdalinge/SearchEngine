/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package set.gui;

import java.io.IOException;

import javax.swing.AbstractListModel;
import javax.swing.JList;

import set.docprocess.PositionalInvertedIndex;

/**
 * class VocabPanel
 * 
 * @author Durvijay Sharma
 * @author Mangesh Adalinge
 * @author Surabhi Dixit
 */
public class VocabPanel extends javax.swing.JPanel {

	/**
	 * Creates new form VocabPanel assign index object
	 * 
	 * @throws IOException
	 */
	public VocabPanel(PositionalInvertedIndex pindex) throws IOException {
		initComponents();
	}

	/**
	 * Vocabulary Frame U.I design components ex: buttons,left pane, right pane
	 * etc. Button action event listeners
	 * 
	 * @throws IOException
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents() throws IOException {
		lblCount = new javax.swing.JLabel();
		txtCount = new javax.swing.JTextField();
		jScrollPane1 = new javax.swing.JScrollPane();
		jList1 = new javax.swing.JList<>();
		lblCount.setText("Total Count :");
		txtCount.setEditable(false);
		JList<String> jlist = new JList<String>(IndexPanel.dIndexP.getDictionary());
		AbstractListModel<String> abstractListModel = (AbstractListModel<String>) jlist.getModel();
		jList1.setModel(abstractListModel);
		txtCount.setText(Integer.toString(jlist.getModel().getSize()));
		jScrollPane1.setViewportView(jList1);
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap().addComponent(lblCount)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(txtCount, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 611,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap(60, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(lblCount).addComponent(txtCount, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(18, 18, 18)
						.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 441, Short.MAX_VALUE)
						.addContainerGap()));
	}// </editor-fold>//GEN-END:initComponents

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JList<String> jList1;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JLabel lblCount;
	private javax.swing.JTextField txtCount;
	// End of variables declaration//GEN-END:variables
}
