package set.gui;

import com.google.gson.Gson;

import set.beans.TokenDetails;
import set.beans.JsonFile;
import set.docprocess.Indexing;
import set.queryprocessing.QueryLiterals;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;

/**
 * @author Durvijay Sharma
 * @author Mangesh Adalinge
 * @author Surabhi Dixit
 */
public class QueryPanel extends javax.swing.JPanel {
    
    private Indexing indexobj;
    private HashMap<Integer, File> fileList;
    private HashMap<String, File> fetchfile=new HashMap<>();
    
    /**
     * constructor to set indexing object and set the complete filelist
     * @param abc
     * @param index
     */
    public QueryPanel(HashMap<Integer, File> filelist, Indexing index) {
        indexobj=index;
        fileList=filelist;
        initComponents();
        
    }
    
	/**
	 * Query Frame U.I design components ex: buttons,left pane, right pane  etc.
	 * Button action event listeners
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        txtQueryAreaField = new javax.swing.JTextArea();
        btnProcess = new javax.swing.JButton();
        lblOutput = new javax.swing.JLabel();
        txtCountField = new javax.swing.JTextField();
        scrollPaneFileList = new javax.swing.JScrollPane();
        listWindow = new javax.swing.JList<>();
        scrollPaneFileDisplay = new javax.swing.JScrollPane();
        txtFileDisplayArea = new javax.swing.JTextArea();

        txtQueryAreaField.setColumns(20);
        txtQueryAreaField.setRows(5);
        jScrollPane1.setViewportView(txtQueryAreaField);
        btnProcess.setText("Process");
        
        btnProcess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcessActionPerformed(evt);
            }
        });

        lblOutput.setText("Total Count :");

        txtCountField.setEditable(false);

        listWindow.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION); //single selection of list
        listWindow.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listWindowMouseClicked(evt);
            }
        });
        scrollPaneFileList.setViewportView(listWindow);

        txtFileDisplayArea.setEditable(false);
        txtFileDisplayArea.setColumns(20);
        txtFileDisplayArea.setLineWrap(true);
        txtFileDisplayArea.setRows(5);
        scrollPaneFileDisplay.setViewportView(txtFileDisplayArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblOutput)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtCountField, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(scrollPaneFileList, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(28, 28, 28)
                        .addComponent(btnProcess)))
                .addGap(34, 34, 34)
                .addComponent(scrollPaneFileDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 581, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addComponent(btnProcess)
                        .addGap(107, 107, 107)
                        .addComponent(scrollPaneFileList, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scrollPaneFileDisplay, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblOutput, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtCountField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(26, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * on click of Process button following action occurs
     * query is passed to QueryLiteral class
     * Result is retrieved and displayed
     * resetting of total count of files received and list
     * @param evt
     */
    @SuppressWarnings("serial")
	private void btnProcessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcessActionPerformed
        txtCountField.setText(Integer.toString(0));
        displayResult=new ArrayList<>();
    	if (txtQueryAreaField.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Enter Query to Process");
        }
    	else{
            
            QueryLiterals qL=new QueryLiterals();
            displayResult=qL.splitQueryString(txtQueryAreaField.getText().trim(),indexobj);
            if (displayResult!=null && displayResult.size()>0) {
                txtCountField.setText(Integer.toString(displayResult.size()));
                listWindow = new javax.swing.JList<>();                
                listWindow.setModel(new javax.swing.AbstractListModel<String>() {// result is added to display list
                    public int getSize() {
                        return displayResult.size(); }
                    public String getElementAt(int i) {
                        TokenDetails result = displayResult.get(i);
                        fetchfile.put(fileList.get(result.getDocId()).getName(),fileList.get(result.getDocId()));// recieved result file list is created
                        return fileList.get(result.getDocId()).getName(); //file id is decoded with file id
                    }
                });
                listWindow.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
                // event listener for select file in the result
                listWindow.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        listWindowMouseClicked(evt);
                    }
                    
                });
                scrollPaneFileList.setViewportView(listWindow);
            }else{
            	// resetting of display result
            	JList<String> jlist = new JList<String>();
                AbstractListModel<String> abstractListModel = (AbstractListModel<String>)jlist.getModel();
                listWindow.setModel(abstractListModel);
            	JOptionPane.showMessageDialog(null, "No records Found");
            }
            
        }
    }//GEN-LAST:event_btnProcessActionPerformed
    /**
     * the selected file is decoded and its complete path is retrieved and passed on to Filereader to read
     * and the body of the selected file is displayed of on the file display text area
     * @param evt
     */
    private void listWindowMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listWindowMouseClicked
        
        String Selectedfile=listWindow.getSelectedValue();
        if (Selectedfile!=null) {
            try {
                Gson gson = new Gson();
                File myFile = fetchfile.get(Selectedfile);
                FileReader fr = new FileReader(myFile.getAbsoluteFile());
                JsonFile fileclass = gson.fromJson(fr, JsonFile.class);
                
                
                txtFileDisplayArea.setText(fileclass.getBody());
            } catch (IOException ex) {
            	Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null,ex);
                ex.printStackTrace();
            }
        }
        
        
    }//GEN-LAST:event_listWindowMouseClicked
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnProcess;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea txtFileDisplayArea;
    private javax.swing.JLabel lblOutput;
    private javax.swing.JList<String> listWindow;
    private javax.swing.JScrollPane scrollPaneFileDisplay;
    private javax.swing.JScrollPane scrollPaneFileList;
    private javax.swing.JTextField txtCountField;
    private javax.swing.JTextArea txtQueryAreaField;
    // End of variables declaration//GEN-END:variables
    private List<TokenDetails> displayResult=new ArrayList<>();
}
