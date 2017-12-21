/*******************************************************************************
 * Copyright C 2016, Roche pREDi (Roche Innovation Center Munich)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/

package org.roche.plugin.reactions;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.TableRowSorter;

import org.helm.notation.MonomerFactory;
import org.helm.notation.MonomerStore;
import org.helm.notation.model.Monomer;
import org.roche.plugin.reactions.models.ADCDesignerModel;
import org.roche.plugin.reactions.models.ADCDesignerUserSelection;
import org.roche.plugin.reactions.models.ADCDesignerUserSelection.ChemistrySelection;
import org.roche.plugin.reactions.models.AminoAcidType;
import org.roche.plugin.reactions.models.BindingSiteData;
import org.roche.plugin.reactions.models.BindingSiteData.BindingSiteSelection;
import org.roche.plugin.reactions.models.ComparableMonomer;
import org.roche.plugin.reactions.models.MonomerTableModel;
import org.roche.plugin.reactions.models.ReactionData;
import org.roche.plugin.reactions.models.TargetAminoAcid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quattroresearch.antibody.MonomerUtils;

/**
 * 
 * {@code ADCDesignerDialog} allows to design and register chemical monomers, or to attach those to selected binding
 * site.
 * 
 * @author <b>Stefan Klostermann:</b> Stefan DOT Klostermann AT roche DOT com, Roche Pharma Research and Early
 *         Development - Informatics, Roche Innovation Center Munich
 * @author <b>Marco Lanig:</b> lanig AT quattro-research DOT com, quattro research GmbH
 * @author <b>Sabrina Hecht:</b> hecht AT quattro-research DOT com, quattro research GmbH
 * @version $Id$
 */
public class ADCDesignerDialog extends javax.swing.JDialog {

  /**
   * Generated serialVersionUID;
   */
  private static final long serialVersionUID = -5174872012738061075L;

  private static final Logger LOG = LoggerFactory.getLogger(ADCDesignerDialog.class);

  private static final int COMBO_BOX_MAXWIDTH = 500;

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.ButtonGroup bgBindingSite;

  private javax.swing.ButtonGroup bgChemistry;

  private javax.swing.JButton btnCancel;

  private javax.swing.JButton btnCreate;

  private javax.swing.JButton btnDeleteMonomer;

  private javax.swing.JButton btnNewMonomer;

  private javax.swing.JLabel filterLabel;

  private javax.swing.JTextField filterText;

  private javax.swing.JPanel jPanel1;

  private javax.swing.JPanel jPanel2;

  private javax.swing.JScrollPane jScrollPane2;

  private javax.swing.JSpinner jSpinner1;

  private javax.swing.JTable jTable1;

  private javax.swing.JLabel lblBindTo;

  private javax.swing.JLabel lblReactants;

  private javax.swing.JLabel lblReactionType;

  private javax.swing.JComboBox luAminoAcidType;

  private javax.swing.JTable luMonomers;

  private javax.swing.JComboBox luReactionType;

  private javax.swing.JComboBox luTargetAminoAcids;

  private javax.swing.JPanel panBindingOptions;

  private javax.swing.JPanel panMonomerDetails;

  private javax.swing.JPanel panReactionDetails;

  private javax.swing.JRadioButton rbAminoAcidType;

  private javax.swing.JRadioButton rbCTerminal;

  private javax.swing.JRadioButton rbDistinctAminoAcid;

  private javax.swing.JRadioButton rbNTerminal;

  private javax.swing.JScrollPane scrollMonomers;

  private javax.swing.JTabbedPane tabPaneReactionDetails;

  private javax.swing.JTabbedPane tpMonomerDetails;

  private javax.swing.JTabbedPane tpReactants;

  // End of variables declaration//GEN-END:variables

  private TableRowSorter<MonomerTableModel> sorter;

  private Frame parentFrame;

  private List<MonomerViewer> monomerViewers;

  private MonomerViewer monomerDetailsViewer;

  private ADCDesignerModel model;

  private ADCDesignerUserSelection modalResult;

  /**
   * Creates new form ADCDesignerDialog
   * 
   * @param parent the parent {@link java.awt.Frame}.
   * @param model the data model.
   */
  public ADCDesignerDialog(java.awt.Frame parent, String title, ADCDesignerModel model) {
    super(parent, true);
    this.parentFrame = parent;

    setTitle(title);
    initComponents();

    initMonomerViewers();

    getRootPane().setDefaultButton(btnCreate);

    // initialize the data model
    setModel(model);
    initFilter();
    addFilter();
  }

  private void update() {
    this.luMonomers.clearSelection();
    this.monomerDetailsViewer.setMol(null);
    this.luMonomers.setModel(model.getMonomerTableModel());
    initFilter();
    addFilter();
    this.luMonomers.repaint();
    this.scrollMonomers.repaint();
    this.repaint();

  }

  private void initFilter() {
    sorter = new TableRowSorter<MonomerTableModel>(model.getMonomerTableModel());
  }

  private void addFilter() {
    luMonomers.setRowSorter(sorter);
    filterText.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void changedUpdate(DocumentEvent e) {
        newFilter();
      }

      private void newFilter() {
        RowFilter<MonomerTableModel, Object> rf = null;
        try {
          rf = RowFilter.regexFilter("(?i)" + filterText.getText());
        } catch (PatternSyntaxException e) {
          return;
        }

        sorter.setRowFilter(rf);
      }

      @Override
      public void insertUpdate(DocumentEvent e) {
        newFilter();

      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        newFilter();

      }
    });
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
   * content of this method is always regenerated by the Form Editor.
   */
  @SuppressWarnings({"unchecked"})
  // <editor-fold defaultstate="collapsed"
  // <editor-fold defaultstate="collapsed"
  // <editor-fold defaultstate="collapsed" desc="Generated
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    bgChemistry = new javax.swing.ButtonGroup();
    bgBindingSite = new javax.swing.ButtonGroup();
    jScrollPane2 = new javax.swing.JScrollPane();
    jTable1 = new javax.swing.JTable();
    jPanel1 = new javax.swing.JPanel();
    btnCreate = new javax.swing.JButton();
    btnCancel = new javax.swing.JButton();
    jPanel2 = new javax.swing.JPanel();
    btnNewMonomer = new javax.swing.JButton();
    tabPaneReactionDetails = new javax.swing.JTabbedPane();
    panMonomerDetails = new javax.swing.JPanel();
    tpMonomerDetails = new javax.swing.JTabbedPane();
    panReactionDetails = new javax.swing.JPanel();
    lblReactionType = new javax.swing.JLabel();
    lblReactants = new javax.swing.JLabel();
    luReactionType = new javax.swing.JComboBox();
    tpReactants = new javax.swing.JTabbedPane();
    btnDeleteMonomer = new javax.swing.JButton();
    scrollMonomers = new javax.swing.JScrollPane();
    luMonomers = new javax.swing.JTable() {
      @Override
      public String getToolTipText(MouseEvent e) {
        int row = rowAtPoint(e.getPoint());
        int column = columnAtPoint(e.getPoint());
        return getValueAt(row, column).toString();
      }
    };
    filterLabel = new javax.swing.JLabel();
    filterText = new javax.swing.JTextField();
    panBindingOptions = new javax.swing.JPanel();
    rbCTerminal = new javax.swing.JRadioButton();
    rbNTerminal = new javax.swing.JRadioButton();
    rbDistinctAminoAcid = new javax.swing.JRadioButton();
    rbAminoAcidType = new javax.swing.JRadioButton();
    lblBindTo = new javax.swing.JLabel();
    luTargetAminoAcids = new javax.swing.JComboBox();
    luAminoAcidType = new javax.swing.JComboBox();
    jSpinner1 = new javax.swing.JSpinner();

    jTable1.setModel(new javax.swing.table.DefaultTableModel(
        new Object[][] {
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null}
        },
        new String[] {
            "Title 1", "Title 2", "Title 3", "Title 4"
        }
        ));
    jScrollPane2.setViewportView(jTable1);

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

    btnCreate.setText("Create");
    btnCreate.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnCreateActionPerformed(evt);
      }
    });

    btnCancel.setText("Cancel");
    btnCancel.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnCancelActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCreate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancel)
                .addContainerGap())
        );
    jPanel1Layout.setVerticalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCreate)
                    .addComponent(btnCancel))
                .addContainerGap())
        );

    jPanel2.setMaximumSize(new java.awt.Dimension(32767, 100));
    jPanel2.setPreferredSize(new java.awt.Dimension(411, 530));

    btnNewMonomer.setText("New Monomer");
    btnNewMonomer.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnNewMonomerActionPerformed(evt);
      }
    });

    tabPaneReactionDetails.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    tabPaneReactionDetails.setMaximumSize(new java.awt.Dimension(32767, 100));
    tabPaneReactionDetails.setPreferredSize(new java.awt.Dimension(391, 400));

    panMonomerDetails.setMaximumSize(new java.awt.Dimension(32767, 100));

    javax.swing.GroupLayout panMonomerDetailsLayout = new javax.swing.GroupLayout(panMonomerDetails);
    panMonomerDetails.setLayout(panMonomerDetailsLayout);
    panMonomerDetailsLayout.setHorizontalGroup(
        panMonomerDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panMonomerDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tpMonomerDetails)
                .addContainerGap())
        );
    panMonomerDetailsLayout.setVerticalGroup(
        panMonomerDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panMonomerDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tpMonomerDetails)
                .addContainerGap())
        );

    tabPaneReactionDetails.addTab("Monomer Details", panMonomerDetails);

    lblReactionType.setText("Reaction type");

    lblReactants.setText("Reactant(s)");

    luReactionType.setModel(new javax.swing.DefaultComboBoxModel(new String[] {"Click chemistry",
        "Amanitine lysine coupling"}));

    javax.swing.GroupLayout panReactionDetailsLayout = new javax.swing.GroupLayout(panReactionDetails);
    panReactionDetails.setLayout(panReactionDetailsLayout);
    panReactionDetailsLayout.setHorizontalGroup(
        panReactionDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panReactionDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panReactionDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tpReactants)
                    .addGroup(panReactionDetailsLayout.createSequentialGroup()
                        .addGroup(panReactionDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblReactants)
                            .addGroup(panReactionDetailsLayout.createSequentialGroup()
                                .addComponent(lblReactionType)
                                .addGap(18, 18, 18)
                                .addComponent(luReactionType, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
    panReactionDetailsLayout.setVerticalGroup(
        panReactionDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panReactionDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panReactionDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblReactionType)
                    .addComponent(luReactionType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblReactants)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tpReactants)
                .addContainerGap())
        );

    tabPaneReactionDetails.addTab("Reaction Details", panReactionDetails);

    btnDeleteMonomer.setText("Delete Monomer");
    btnDeleteMonomer.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnDeleteMonomerActionPerformed(evt);
      }
    });

    luMonomers.setModel(new javax.swing.table.DefaultTableModel(
        new Object[][] {
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null}
        },
        new String[] {
            "Title 1", "Title 2", "Title 3", "Title 4"
        }
        ));
    luMonomers.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    luMonomers.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    luMonomers.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        luMonomersMouseClicked(evt);
      }
    });
    scrollMonomers.setViewportView(luMonomers);
    luMonomers.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

    filterLabel.setText("Filter Text:");

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(tabPaneReactionDetails, javax.swing.GroupLayout.DEFAULT_SIZE, 705, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scrollMonomers, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnNewMonomer, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnDeleteMonomer)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(filterLabel)
                                .addGap(18, 18, 18)
                                .addComponent(filterText)))))
                .addContainerGap())
        );
    jPanel2Layout.setVerticalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNewMonomer)
                    .addComponent(btnDeleteMonomer))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(filterLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filterText))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollMonomers, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tabPaneReactionDetails, javax.swing.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE)
                .addContainerGap())
        );

    tabPaneReactionDetails.getAccessibleContext().setAccessibleName("tabPaneReactions");

    panBindingOptions.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    bgBindingSite.add(rbCTerminal);
    rbCTerminal.setText("C-Terminal");
    rbCTerminal.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        rbCTerminalActionPerformed(evt);
      }
    });

    bgBindingSite.add(rbNTerminal);
    rbNTerminal.setText("N-Terminal");

    bgBindingSite.add(rbDistinctAminoAcid);
    rbDistinctAminoAcid.setText("Distinct amino acid");

    bgBindingSite.add(rbAminoAcidType);
    rbAminoAcidType.setText("Amino acid type");

    lblBindTo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
    lblBindTo.setText("Bind to");

    luTargetAminoAcids.setModel(new javax.swing.DefaultComboBoxModel(new String[] {"Lysine 293"}));

    luAminoAcidType.setModel(new javax.swing.DefaultComboBoxModel(new String[] {"Cysteine - C", "Aspartic acid - D",
        "Glutamic acid - E", "Lysine - K"}));

    jSpinner1.setModel(new javax.swing.SpinnerNumberModel(1.0f, 0.01f, null, 0.1f));

    javax.swing.GroupLayout panBindingOptionsLayout = new javax.swing.GroupLayout(panBindingOptions);
    panBindingOptions.setLayout(panBindingOptionsLayout);
    panBindingOptionsLayout.setHorizontalGroup(
        panBindingOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panBindingOptionsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panBindingOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panBindingOptionsLayout.createSequentialGroup()
                        .addComponent(rbNTerminal)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panBindingOptionsLayout.createSequentialGroup()
                        .addComponent(rbCTerminal)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panBindingOptionsLayout.createSequentialGroup()
                        .addGroup(panBindingOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbAminoAcidType)
                            .addComponent(rbDistinctAminoAcid))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(panBindingOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(luTargetAminoAcids, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panBindingOptionsLayout.createSequentialGroup()
                                .addComponent(luAminoAcidType, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panBindingOptionsLayout.createSequentialGroup()
                        .addComponent(lblBindTo, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
    panBindingOptionsLayout.setVerticalGroup(
        panBindingOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panBindingOptionsLayout.createSequentialGroup()
                .addComponent(lblBindTo)
                .addGap(7, 7, 7)
                .addComponent(rbNTerminal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rbCTerminal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panBindingOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbDistinctAminoAcid)
                    .addComponent(luTargetAminoAcids, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(panBindingOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbAminoAcidType)
                    .addComponent(luAminoAcidType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(54, 54, 54))
        );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panBindingOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 567, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(187, 187, 187)
                        .addComponent(panBindingOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void rbCTerminalActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_rbCTerminalActionPerformed
    // TODO add your handling code here:
  }// GEN-LAST:event_rbCTerminalActionPerformed

  private void luMonomersMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_luMonomersMouseClicked
    luMonomersActionPerformed(evt);
  }// GEN-LAST:event_luMonomersMouseClicked

  private void btnDeleteMonomerActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnDeleteMonomerActionPerformed
    int rowIndex = luMonomers.getSelectedRow();
    luMonomers.removeRowSelectionInterval(rowIndex, rowIndex);
    int row = luMonomers.convertRowIndexToModel(rowIndex);
    ComparableMonomer monomer = ((MonomerTableModel) luMonomers.getModel()).getMonomerList().get(row);

    int retVal = JOptionPane.showConfirmDialog(
        this, "Do you really want to delete '"
            + ((MonomerTableModel) luMonomers.getModel()).getMonomerList().get(row) + "'?",
        "Please confirm deletion", JOptionPane.YES_NO_OPTION);

    switch (retVal) {
    case JOptionPane.YES_OPTION:
      LOG.debug("Delete " + monomer.getName());

      MonomerFactory factory;
      try {
        factory = MonomerFactory.getInstance();
        MonomerStore store = factory.getMonomerStore();
        Map<String, Map<String, Monomer>> monomerDb = store.getMonomerDB();
        monomerDb.get(Monomer.CHEMICAL_POLYMER_TYPE).remove(monomer.getTrivialName());

        store.getSmilesMonomerDB().remove(monomer.getWrappedMonomer());
        factory.setDBChanged(true);
        factory.saveMonomerCache();

        JOptionPane.showMessageDialog(this,
            "Monomer deleted successfully.", "Deletion successful",
            JOptionPane.INFORMATION_MESSAGE);
        ADCDesignerMonomerStore.getInstance()
            .refreshReactiveMonomersInStore();
        update();

      } catch (Exception e) {
        JOptionPane.showMessageDialog(this, e,
            "Deletion failed", JOptionPane.ERROR_MESSAGE);
      }

      break;
    case JOptionPane.NO_OPTION:
      LOG.debug("Do NOT delete " + monomer.getAlternateId() + " " + monomer.getName());
      return;
    default:
      throw new IllegalArgumentException("Unknown return value. Deletion failed.");
    }

  }// GEN-LAST:event_btnDeleteMonomerActionPerformed

  private void luMonomersActionPerformed(MouseEvent evt) {// GEN-FIRST:event_luMonomersActionPerformed

    int rowIndex = luMonomers.getSelectedRow();
    int row = luMonomers.convertRowIndexToModel(rowIndex);
    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    if (rowIndex >= 0 && rowIndex < luMonomers.getRowCount()) {
      ComparableMonomer selectedMonomer = ((MonomerTableModel) luMonomers.getModel()).getMonomerList().get(row);

      try {
        monomerDetailsViewer
            .setMol(MonomerUtils.createMoleculeFromChemicalNotation(selectedMonomer.getCanSMILES()));
      } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Could not load molecule: " + e.getClass().getSimpleName(),
            "Loading molecule failed", JOptionPane.ERROR_MESSAGE);
      } finally {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      }

    }

  }// GEN-LAST:event_luMonomersActionPerformed

  private void initMonomerViewers() {
    Insets oldInsets = UIManager.getInsets("TabbedPane.contentBorderInsets");
    UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));

    monomerViewers = Arrays.asList(new MonomerViewer[] {new MonomerViewer(), new MonomerViewer()});

    for (int i = 0; i < monomerViewers.size(); i++) {
      tpReactants.addTab("Reactant " + (i + 1), monomerViewers.get(i));
    }

    // Tabs bar is invisible, because it is changed by radio buttons
    tabPaneReactionDetails.setUI(new BasicTabbedPaneUI() {
      @Override
      protected int calculateTabAreaHeight(int tabl_placement, int run_count, int max_tab_height) {
        return 0;
      }
    });

    monomerDetailsViewer = new MonomerViewer(false);
    tpMonomerDetails.add(monomerDetailsViewer);
    // Tabs bar is invisible, because unneeded
    tpMonomerDetails.setUI(new BasicTabbedPaneUI() {
      @Override
      protected int calculateTabAreaHeight(int tabl_placement, int run_count, int max_tab_height) {
        return 0;
      }
    });

    UIManager.put("TabbedPane.contentBorderInsets", oldInsets);
  }

  private void btnNewMonomerActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnNewMonomerActionPerformed
    MonomerDesigner designer = new MonomerDesigner(parentFrame, "Monomer Designer");
    designer.setLocationRelativeTo(parentFrame);
    designer.setVisible(true);
    ADCDesignerMonomerStore.getInstance()
        .refreshReactiveMonomersInStore();
    update();

  }// GEN-LAST:event_btnNewMonomerActionPerformed

  private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnCreateActionPerformed
    setModalResultFromDialog();
    close();
  }// GEN-LAST:event_btnCreateActionPerformed

  private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnCancelActionPerformed
    this.modalResult = null;
    close();
  }// GEN-LAST:event_btnCancelActionPerformed

  /**
   * Fills in the {@link #modalResult} from the user selections.
   */
  private void setModalResultFromDialog() {
    // get binding site selection
    BindingSiteSelection bindingSiteSelection = null;
    for (Enumeration<AbstractButton> buttons = bgBindingSite.getElements(); buttons.hasMoreElements();) {
      AbstractButton button = buttons.nextElement();

      if (button.isSelected()) {
        if (button == rbCTerminal) {
          bindingSiteSelection = BindingSiteSelection.C_TERMINAL;
        } else if (button == rbNTerminal) {
          bindingSiteSelection = BindingSiteSelection.N_TERMINAL;
        } else if (button == rbDistinctAminoAcid) {
          bindingSiteSelection = BindingSiteSelection.DISTINCT_AMINO_ACID;
        } else {
          bindingSiteSelection = BindingSiteSelection.STATISTICAL;
        }
        break;
      }
    }

    ComparableMonomer selectedMonomer = null;
    ReactionData reactionData = null;

    int rowIndex = luMonomers.getSelectedRow();
    int row = luMonomers.convertRowIndexToModel(rowIndex);
    if (row >= 0) {
      selectedMonomer = ((MonomerTableModel) luMonomers.getModel()).getMonomerList().get(row);
    } else {
      selectedMonomer = null;
    }

    TargetAminoAcid distinctAminoAcid = null;
    AminoAcidType aminoAcidType = null;
    double amount = 1.0;

    switch (bindingSiteSelection) {
    case DISTINCT_AMINO_ACID:
      distinctAminoAcid = (TargetAminoAcid) luTargetAminoAcids.getSelectedItem();
      break;
    case STATISTICAL:
      aminoAcidType = (AminoAcidType) luAminoAcidType.getSelectedItem();
      amount = ((Float) jSpinner1.getValue()).doubleValue();
      break;
    case C_TERMINAL:
    case N_TERMINAL:
    default:
      break;
    }

    BindingSiteData bindingSiteData = new BindingSiteData(bindingSiteSelection, distinctAminoAcid, aminoAcidType,
        amount);
    this.modalResult =
        new ADCDesignerUserSelection(ChemistrySelection.COUPLE_WITH_MONOMER, reactionData, selectedMonomer,
            bindingSiteData);
  }

  /**
   * Sets the data model behind the dialog.
   * 
   * @param model the {@link ADCDesignerModel} instance passed into the constructor.
   */
  @SuppressWarnings("unchecked")
  private void setModel(ADCDesignerModel model) {
    this.model = model;
    this.luTargetAminoAcids.setModel(model.getTargetAminoAcidComboBoxModel());

    checkBtnCreateActive(this);

    final ADCDesignerDialog thisDlg = this;
    this.luTargetAminoAcids.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        checkBtnCreateActive(thisDlg);
      }
    });
    this.rbDistinctAminoAcid.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        checkBtnCreateActive(thisDlg);
      }
    });
    this.luMonomers.setModel(model.getMonomerTableModel());
    this.luAminoAcidType.setModel(model.getAminoAcidTypeComboBoxModel());

    // set default selections

    this.bgBindingSite.setSelected(rbCTerminal.getModel(), true);

    // if (luMonomers.getModel().getSize() > 0) {
    // luMonomersActionPerformed(new ActionEvent(this,
    // ActionEvent.ACTION_PERFORMED, null));
    // }
    // luMonomers.adjustDropDownMenuWidth();
  }

  private void checkBtnCreateActive(ADCDesignerDialog thisDlg) {
    if (!thisDlg.rbDistinctAminoAcid.isSelected()) {
      btnCreate.setEnabled(true);
    } else {
      TargetAminoAcid target = (TargetAminoAcid) thisDlg.luTargetAminoAcids.getSelectedItem();
      if (ADCDesignerModel.isPartOfDisulfidBridge(getModel().getDomain(), target.getPosition())) {
        btnCreate.setEnabled(false);
      } else {
        btnCreate.setEnabled(true);
      }
    }
  }

  /**
   * Displays the dialog and returns the form data.
   * 
   * @return
   */
  public ADCDesignerUserSelection showDialog() {
    setVisible(true);
    return modalResult;
  }

  /**
   * Closes the dialog and frees any resources.
   */
  public void close() {
    setVisible(false);
    dispose();
  }

  /**
   * 
   * @return the underlying data model.
   */
  public ADCDesignerModel getModel() {
    return this.model;
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    /* Set the Nimbus look and feel */
    // <editor-fold defaultstate="collapsed"
    // desc=" Look and feel setting code (optional) ">
    /*
     * If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel. For details see
     * http://download.oracle.com/javase /tutorial/uiswing/lookandfeel/plaf.html
     */
    try {
      for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          javax.swing.UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (ClassNotFoundException ex) {
      java.util.logging.Logger.getLogger(ADCDesignerDialog.class.getName()).log(java.util.logging.Level.SEVERE,
          null, ex);
    } catch (InstantiationException ex) {
      java.util.logging.Logger.getLogger(ADCDesignerDialog.class.getName()).log(java.util.logging.Level.SEVERE,
          null, ex);
    } catch (IllegalAccessException ex) {
      java.util.logging.Logger.getLogger(ADCDesignerDialog.class.getName()).log(java.util.logging.Level.SEVERE,
          null, ex);
    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
      java.util.logging.Logger.getLogger(ADCDesignerDialog.class.getName()).log(java.util.logging.Level.SEVERE,
          null, ex);
    }
    // </editor-fold>

    /* Create and display the dialog */
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        ADCDesignerDialog dialog = new ADCDesignerDialog(new javax.swing.JFrame(), "", null);
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
          @Override
          public void windowClosing(java.awt.event.WindowEvent e) {
            System.exit(0);
          }

          @Override
          public void windowClosed(WindowEvent e) {
            System.exit(0);
          }
        });
        dialog.setVisible(true);
      }
    });
  }

  public class WideDropDownComboBox extends JComboBox {

    private static final long serialVersionUID = -2694382778237570550L;

    private boolean layingOut = false;

    private int dropDownMenuWidth = 0;

    // Setting the JComboBox width
    public void adjustDropDownMenuWidth() {
      dropDownMenuWidth = computeMaxItemWidth();
    }

    @Override
    public Dimension getSize() {
      Dimension dim = super.getSize();
      if (!layingOut) {
        dim.width = Math.max(dropDownMenuWidth, dim.width);
      }

      return dim;
    }

    public int computeMaxItemWidth() {

      int numOfItems = this.getItemCount();
      Font font = this.getFont();
      FontMetrics metrics = this.getFontMetrics(font);
      int widest = getSize().width; // The drop down menu must not be less
      // wide than the combo box
      for (int i = 0; i < numOfItems; i++) {
        Object item = this.getItemAt(i);
        int lineWidth = metrics.stringWidth(item.toString());
        widest = Math.max(widest, lineWidth);
      }

      int scrollbarWidth = ((Integer) UIManager.get("ScrollBar.width")).intValue();
      return Math.min(COMBO_BOX_MAXWIDTH, widest + scrollbarWidth);
    }

    @Override
    public void doLayout() {
      try {
        layingOut = true;
        super.doLayout();
      } finally {
        layingOut = false;
      }
    }
  }

}
