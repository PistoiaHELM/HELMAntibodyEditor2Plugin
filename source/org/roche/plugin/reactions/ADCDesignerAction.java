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
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.helm.notation.MonomerFactory;
import org.helm.notation.MonomerStore;
import org.helm.notation.model.Monomer;
import org.roche.antibody.model.antibody.Antibody;
import org.roche.antibody.model.antibody.ChemElement;
import org.roche.antibody.model.antibody.Connection;
import org.roche.antibody.model.antibody.Domain;
import org.roche.antibody.model.antibody.GeneralConnection;
import org.roche.antibody.model.antibody.StatisticalConnection;
import org.roche.antibody.services.UIService;
import org.roche.antibody.services.helmnotation.HELM;
import org.roche.antibody.ui.components.AntibodyEditorAccess;
import org.roche.plugin.reactions.models.ADCDesignerModel;
import org.roche.plugin.reactions.models.ADCDesignerUserSelection;
import org.roche.plugin.reactions.models.BindingSiteData.BindingSiteSelection;
import org.roche.plugin.reactions.models.ComparableMonomer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * {@code ADCDesignerAction} starts the {@code ADCDesignerDialog}
 * 
 * @author <b>Stefan Klostermann:</b> Stefan DOT Klostermann AT roche DOT com, Roche Pharma Research and Early
 *         Development - Informatics, Roche Innovation Center Munich
 * @author <b>Marco Erdmann:</b> erdmann AT quattro-research DOT com, quattro research GmbH
 * @author <b>Marco Lanig:</b> lanig AT quattro-research DOT com, quattro research GmbH
 * @author <b>Sabrina Hecht:</b> hecht AT quattro-research DOT com, quattro research GmbH
 * @version $Id$
 */
public class ADCDesignerAction extends AbstractAction {

  private static final String COULD_NOT_ATTACH_MONOMER_TO_DOMAIN =
      "Could not attach the monomer to the current domain.";

  private static final String NO_ATTACHMENT_POINT_FOUND =
      "Could not find a suitable attachment point for the given input data. Please check!";

  /**
   * Exception thrown when the chemical monomer cannot be attached to the current domain.
   * 
   */
  public static class CannotAttachException extends Exception {

    /** serialVersionUID */
    private static final long serialVersionUID = 2915024260456381426L;
  }

  private static final String CHEMICAL_MONOMER_TEMP_NAME = "CHEM1";

  /**
   * Generated {@code serialVersionUID}.
   */
  private static final long serialVersionUID = 7382517699983644084L;

  /** The Logger for this class */
  private static final Logger LOG = LoggerFactory.getLogger(ADCDesignerAction.class);

  private Domain domain;

  private JFrame parentFrame;

  /**
   * Checks whether the given domain is the last in its peptide chain and has no more amino acids after its last
   * position.
   * 
   * @return {@code true} if the attaching at the C-terminus is possible.
   */
  private boolean canAttachCTerminal() {
    return domain.getDomainIndex() == (domain.getPeptide().getDomains().size() - 1)
        && domain.getPostSequence().equals("");
  }

  /**
   * Checks whether the given domain is the first in its peptide chain and has no more amino acids in front of its first
   * position.
   * 
   * @return {@code true} if the attaching at the N-terminus is possible.
   */
  private boolean canAttachNTerminal() {
    return domain.getDomainIndex() == 0 && domain.getPreSubsequence().equals("");
  }

  /**
   * Attach the given monomer to the selected position in the current domain.
   * 
   * @param selection the user selection from the {@link ADCDesignerDialog}.
   * @param monomer the chemical {@link Monomer} to attach to the selected domain.
   */
  private void attachMonomerToDomain(ADCDesignerUserSelection selection, ComparableMonomer monomer) {
    int position = -1;
    String bindingSitePeptide = "";
    switch (selection.getBindingSiteData().getBindingSiteSelection()) {
    case N_TERMINAL:
      if (canAttachNTerminal()) {
        position = domain.getStartPosition();
        bindingSitePeptide = HELM.R1;
      }
      break;
    case C_TERMINAL:
      if (canAttachCTerminal()) {
        position = domain.getEndPosition() - domain.getStartPosition() + 1;
        bindingSitePeptide = HELM.R2;
      }
      break;
    case DISTINCT_AMINO_ACID:
      position = selection.getBindingSiteData().getDistinctAminoAcid().getPosition();
      bindingSitePeptide = HELM.R3;
      break;
    case STATISTICAL:
      position = selection.getBindingSiteData().getAminoAcidType().getPosition();
      bindingSitePeptide = HELM.R3;
      break;
    default:
      break;
    }

    if (position > 0) {
      Antibody ab = domain.getPeptide().getAntibody();
      List<ChemElement> chemElements = ab.getChemElements();
      String smiles = monomer.getCanSMILES();

      String[] attachments = new String[3];
      for (int i = 0; i < monomer.getAttachmentList().size(); i++) {
        attachments[i] = monomer.getAttachmentList().get(i).getCapGroupName();
      }

      ChemElement chemElement = new ChemElement(ab, "[" + smiles + "]", smiles, monomer.getMolfile(),
          attachments[0], attachments[1], attachments[2]);

      // ML 2016-03-17: This name should be chemElement.getNameForHELM()
      // chemElement.setName("CHEM" + (chemElements.size() + 1));
      chemElement.setName(monomer.getTrivialName());
      chemElements.add(chemElement);
      Connection connection = null;
      if (selection.getBindingSiteData().getBindingSiteSelection() == BindingSiteSelection.STATISTICAL) {
        connection = new StatisticalConnection(domain.getPeptide(), chemElement,
            domain.getStartPosition() + position - 1, 1, bindingSitePeptide, HELM.R1,
            selection.getBindingSiteData().getEquivalents());
      } else {
        connection = new GeneralConnection(domain.getPeptide(), chemElement,
            domain.getStartPosition() + position - 1, 1, bindingSitePeptide, HELM.R1);
      }
      ab.addConnection(connection);
      AntibodyEditorAccess.getInstance().getAntibodyEditorPane()
          .setModel(AntibodyEditorAccess.getInstance().getAntibodyEditorPane().getAntibody());
    } else {
      JOptionPane.showMessageDialog(this.parentFrame, NO_ATTACHMENT_POINT_FOUND, "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Constructor.
   * 
   * @param parentFrame the parent frame.
   * @param domain the {@link Domain} to operate upon.
   */
  public ADCDesignerAction(JFrame parentFrame, Domain domain) {
    super("Start ADC designer...");
    this.domain = domain;
    this.parentFrame = parentFrame;
  }

  @Override
  public void actionPerformed(ActionEvent e) {

    ADCDesignerDialog dialog = null;

    try {
      this.parentFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      MonomerFactory factory = MonomerFactory.getInstance();
      MonomerStore store = factory.getMonomerStore();
      dialog = new ADCDesignerDialog(parentFrame, "ADC Designer", new ADCDesignerModel(domain, store));
    } catch (Exception e1) {
      LOG.error(e1.getMessage(), e1);
    } finally {
      this.parentFrame.setCursor(Cursor.getDefaultCursor());
    }
    try {
      dialog.setLocationRelativeTo(parentFrame);
      ADCDesignerUserSelection selection = dialog.showDialog();

      // user canceled
      if (selection == null) {
        return;
      }
      UIService.getInstance().getGlassPane().setVisible(true);
      UIService.getInstance().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

      attachMonomerToDomain(selection, selection.getSelectedMonomer());

    } catch (Throwable t) {
      LOG.error(t.getMessage(), t);
      JOptionPane.showMessageDialog(parentFrame, COULD_NOT_ATTACH_MONOMER_TO_DOMAIN + "("
          + t.getClass().getSimpleName() + ": " + t.getMessage() + ")", "Error", JOptionPane.ERROR_MESSAGE);
    } finally {
      this.parentFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

      UIService.getInstance().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      UIService.getInstance().getGlassPane().setVisible(false);
      dialog.close();
    }
  }
}
