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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.roche.antibody.model.antibody.Antibody;
import org.roche.antibody.model.antibody.Domain;
import org.roche.antibody.services.ProteaseDescription;
import org.roche.antibody.services.UIService;
import org.roche.antibody.services.tools.ProteaseTools;
import org.roche.antibody.ui.components.AntibodyEditorAccess;
import org.roche.antibody.ui.components.AntibodyEditorPane;
import org.roche.plugin.reactions.rest.ADCReactions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@code DomainProteaseReactionAction} is used to cleave proteins according to cleavage sites.
 * 
 * @author <b>Stefan Klostermann:</b> Stefan DOT Klostermann AT roche DOT com, Roche Pharma Research and Early
 *         Development - Informatics, Roche Innovation Center Munich
 * @author <b>Marco Lanig:</b> lanig AT quattro-research DOT com, quattro research GmbH
 * 
 * @version $Id$
 */
public class DomainProteaseReactionAction extends AbstractAction {

  private static final long serialVersionUID = 1L;

  /** The Logger for this class */
  private static final Logger LOG = LoggerFactory.getLogger(DomainProteaseReactionAction.class);

  @SuppressWarnings("unused")
  private Domain domain;

  private ProteaseDescription description;

  /**
   * Constructs an {@link AbstractAction} to apply a Sortase reaction on the given domain.
   * 
   * @param editor the current {@link AntibodyEditorPane}.
   * @param domain the current {@link Domain}.
   */
  public DomainProteaseReactionAction(Domain domain, ProteaseDescription description) {
    super(description.getProteaseName() + " reaction...");

    this.domain = domain;
    this.description = description;
    this.setEnabled(ProteaseTools.sequenceHasCleaveSite(domain.getPeptide().getSequence(), description));
  }

  /**
   * Cleaves the protein in the editor with the given protease (i.e. its description).
   * 
   * @param desc the {@link ProteaseDescription} object that describes the protease reaction.
   * @throws Exception
   */
  private void cleaveProtein() {

    Antibody cleavedAntibody = null;
    try {
      cleavedAntibody = ADCReactions
          .cleave(AntibodyEditorAccess.getInstance().getAntibodyEditorPane().getAntibody(), -1, description);
      if (cleavedAntibody != null) {
        AntibodyEditorAccess.getInstance().getAntibodyEditorPane().setModel(cleavedAntibody);
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(UIService.getInstance().getMainFrame(),
          "Cleavage failed with " + e.getClass().getSimpleName(), "Cleavage failed!",
          JOptionPane.ERROR_MESSAGE);
      LOG.error("Cleavage failed with " + e.getClass().getSimpleName(), e);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    cleaveProtein();
  }
}
