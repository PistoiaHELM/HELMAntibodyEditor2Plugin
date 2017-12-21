/**
 * ***************************************************************************** Copyright C 2016, The Pistoia Alliance
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
 *****************************************************************************
 */
package org.roche.plugin.reactions;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.helm.notation.model.Monomer;
import org.roche.plugin.file.InvalidInputException;
import org.roche.plugin.reactions.models.AttachmentTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chemaxon.formats.MolImporter;
import chemaxon.marvin.beans.MSketchPane;
import chemaxon.marvin.beans.MViewPane;
import chemaxon.struc.Molecule;

/**
 * 
 * {@code MonomerViewer} is used to show monomers inside a sketch pane or convert it to chemaxon {@code Molecule}.
 * 
 * @author <b>Stefan Klostermann:</b> Stefan DOT Klostermann AT roche DOT com, Roche Pharma Research and Early
 *         Development - Informatics, Roche Innovation Center Munich
 * @author <b>Marco Lanig:</b> lanig AT quattro-research DOT com, quattro research GmbH
 * @author <b>Sabrina Hecht:<b> hecht AT quattro-research DOT com, quattro research GmbH
 * @version $Id$
 */
public class MonomerViewer extends JPanel {

  /** */
  private static final long serialVersionUID = 1L;

  /** The Logger for this class */
  private static final Logger LOG = LoggerFactory.getLogger(MonomerViewer.class);

  private MSketchPane msketchPane;

  private JButton editButton;

  private MViewPane mviewPane;

  private AttachmentTableModel model;

  private boolean modifiable = true;

  public MonomerViewer() {
    this(true);
  }

  public MonomerViewer(boolean modifiable) {
    this.modifiable = modifiable;

    JPanel viewerPanel = createViewerPanel();
    viewerPanel.setBorder(BorderFactory.createEtchedBorder());

    setLayout(new BorderLayout());
    add(viewerPanel, BorderLayout.CENTER);

    setModifiableStatus(modifiable);
  }

  /**
   * Converts a molfile to an instance of {@link chemaxon.struc.Molecule}.
   * 
   * @param molfile a molfile {@code String}.
   * @return an instance of {@link Molecule} or null.
   * @throws InvalidInputException
   */
  public static Molecule convertToMolecule(String molfile) throws InvalidInputException {
    if (molfile == null) {
      throw new InvalidInputException("Molfile is null and cannot be converted to molecule.");
    }
    Molecule result = null;
    ByteArrayInputStream is = null;
    try {
      is = new ByteArrayInputStream(molfile.getBytes());
      MolImporter importer = new MolImporter(is);
      result = importer.read();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
          LOG.error(e.getMessage(), e);
        }
      }
    }
    return result;
  }

  /**
   * mviewPane will always be enabled, otherwise, the menu bar for the application will be disabled (Marvin did this)
   * 
   * @param modifiable
   */
  public void setModifiableStatus(boolean modifiable) {

    this.modifiable = modifiable;
    editButton.setVisible(modifiable);
  }

  private JPanel createViewerPanel() {
    msketchPane = new MSketchPane();
    msketchPane.setBorder(BorderFactory.createEtchedBorder());
    mviewPane = new MViewPane();
    mviewPane.setEnabled(true);
    mviewPane.setDetachable(false);
    mviewPane.setM(0, "");

    editButton = new JButton("Edit");
    editButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        if (isModifiable()) {
          msketchPane.setMol(mviewPane.getM(0));
          int result = JOptionPane.showOptionDialog(mviewPane, msketchPane, "Monomer Structure Editor",
              JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);

          if (result == JOptionPane.OK_OPTION) {
            mviewPane.setM(0, msketchPane.getMol());
          }
        }
      }
    });

    Box buttonBox = Box.createHorizontalBox();
    buttonBox.add(editButton);

    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.add(mviewPane, BorderLayout.CENTER);
    panel.add(buttonBox, BorderLayout.SOUTH);

    return panel;
  }

  public boolean isModifiable() {
    return modifiable;
  }

  public Molecule getMol() {
    return this.mviewPane.getM(0);
  }

  public void setMol(Molecule mol) {
    this.mviewPane.setM(0, mol);
  }

  public void clear() {
    mviewPane.setM(0, "");
    if (model != null)
      model.updateAttachments(null);

  }

  class AttachmentIDTableCellEditor extends DefaultCellEditor {

    /** */
    private static final long serialVersionUID = 4167611074656033265L;

    private JComboBox comboBox = new JComboBox();

    public AttachmentIDTableCellEditor(JComboBox comboBox) {
      super(comboBox);
      this.comboBox = comboBox;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
        int column) {
      String[] ids = ((AttachmentTableModel) table.getModel()).getIDsByRow(row);
      DefaultComboBoxModel model = new DefaultComboBoxModel(ids);
      comboBox.setModel(model);
      return comboBox;
    }
  }

  public JTable createAttachmentTableForMonomer(Monomer monomer) {
    AttachmentTableModel atModel = new AttachmentTableModel(monomer.getAttachmentList());
    JTable attachmentTable = new JTable(atModel);
    AttachmentIDTableCellEditor editor = new AttachmentIDTableCellEditor(new JComboBox());
    attachmentTable.getColumnModel().getColumn(AttachmentTableModel.ID_COLUMN_INDEX).setCellEditor(editor);
    attachmentTable.setPreferredScrollableViewportSize(new Dimension(200, 160));
    return attachmentTable;
  }
}
