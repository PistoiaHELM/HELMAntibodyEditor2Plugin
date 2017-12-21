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

import org.roche.plugin.reactions.ADCDesignerDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * {@code BindingSiteData} is part of the {@code ADCDesignerUserSelection}.
 * 
 * @author <b>Stefan Klostermann:</b> Stefan DOT Klostermann AT roche DOT com, Roche Pharma Research and Early
 *         Development - Informatics, Roche Innovation Center Munich
 * @author <b>Marco Erdmann:</b> erdmann AT quattro-research DOT com, quattro research GmbH
 * @version $Id$
 */
public class BindingSiteData {

  /** The Logger for this class */
  private static final Logger LOG = LoggerFactory.getLogger(BindingSiteData.class);

  /**
   * Encapsulates the selected binding site in the {@link ADCDesignerDialog}.
   * 
   */
  public static enum BindingSiteSelection {
    C_TERMINAL, N_TERMINAL, DISTINCT_AMINO_ACID, STATISTICAL, BIOTINYLATION
  }

  private final BindingSiteSelection bindingSiteSelection;

  private final TargetAminoAcid distinctAminoAcid;

  private final AminoAcidType aminoAcidType;

  private final double equivalents;

  /**
   * Default constructor.
   * 
   * @param bindingSiteSelection the targeted binding site.
   * @param distinctAminoAcid optional distinct amino acid to bind to.
   * @param aminoAcidType optional type of amino acid to bind to.
   * @param equivalents the equivalents in the case of a statistical connection.
   */
  public BindingSiteData(BindingSiteSelection bindingSiteSelection, TargetAminoAcid distinctAminoAcid,
      AminoAcidType aminoAcidType, double equivalents) {
    super();
    this.bindingSiteSelection = bindingSiteSelection;
    this.distinctAminoAcid = distinctAminoAcid;
    this.aminoAcidType = aminoAcidType;
    this.equivalents = equivalents;
  }

  /**
   * 
   * @return the selected binding site.
   */
  public BindingSiteSelection getBindingSiteSelection() {
    return this.bindingSiteSelection;
  }

  /**
   * 
   * @return the distinct amino acid the user selected.
   */
  public TargetAminoAcid getDistinctAminoAcid() {
    return this.distinctAminoAcid;
  }

  /**
   * 
   * @return a targeted amino acid type in the case of statistical connections.
   */
  public AminoAcidType getAminoAcidType() {
    return this.aminoAcidType;
  }

  /**
   * 
   * @return the equivalents of the bound monomer in the case of a statistical connection.
   */
  public double getEquivalents() {
    return this.equivalents;
  }
}
