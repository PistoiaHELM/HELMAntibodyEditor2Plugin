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

package org.roche.plugin.reactions.models;

import org.helm.notation.model.Monomer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * {@code ADCDesignerUserSelection} is used for {@code ADCDesignerDialog}, to encapsulate selected users items.
 * 
 * @author <b>Stefan Klostermann:</b> Stefan DOT Klostermann AT roche DOT com, Roche Pharma Research and Early
 *         Development - Informatics, Roche Innovation Center Munich
 * @author <b>Marco Erdmann:</b> erdmann AT quattro-research DOT com, quattro research GmbH
 * @version $Id$
 */
public class ADCDesignerUserSelection {

  /** The Logger for this class */
  @SuppressWarnings("unused")
  private static final Logger LOG = LoggerFactory.getLogger(ADCDesignerUserSelection.class);

  public static enum ChemistrySelection {
    COUPLE_WITH_MONOMER, PROCESS_REACTANTS_WITH_PIPELINE_PILOT
  }

  private final ChemistrySelection chemistrySelection;

  private final ReactionData reactionData;

  private final ComparableMonomer selectedMonomer;

  private final BindingSiteData bindingSiteData;

  /**
   * Constructor.
   * 
   * @param chemistrySeletion the selected chemical reaction or monomer.
   * @param reactionData the user entered {@link ReactionData}.
   * @param selectedMonomer the selected monomer.
   * @param bindingSiteData information about the selected binding site.
   */
  public ADCDesignerUserSelection(ChemistrySelection chemistrySeletion, ReactionData reactionData,
      ComparableMonomer selectedMonomer, BindingSiteData bindingSiteData) {
    this.chemistrySelection = chemistrySeletion;
    this.reactionData = reactionData;
    this.selectedMonomer = selectedMonomer;
    this.bindingSiteData = bindingSiteData;
  }

  /**
   * 
   * @return the selected chemical reaction or monomer.
   */
  public ChemistrySelection getChemistrySelection() {
    return chemistrySelection;
  }

  /**
   * 
   * @return the reaction name and reactants the user entered.
   */
  public ReactionData getReactionData() {
    return reactionData;
  }

  /**
   * 
   * @return the {@link Monomer} to bind to the domain.
   */
  public ComparableMonomer getSelectedMonomer() {
    return selectedMonomer;
  }

  /**
   * 
   * @return information about the selected binding site.
   */
  public BindingSiteData getBindingSiteData() {
    return bindingSiteData;
  }

}
