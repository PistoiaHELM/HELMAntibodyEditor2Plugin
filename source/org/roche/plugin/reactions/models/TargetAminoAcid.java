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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * {@code TargetAminoAcid} contains amino acid data used e.g. in {@code TargetAminoAcidComboBoxModel} for ADC coupling
 * reactions.
 * 
 * @author <b>Stefan Klostermann:</b> Stefan DOT Klostermann AT roche DOT com, Roche Pharma Research and Early
 *         Development - Informatics, Roche Innovation Center Munich
 * @author <b>Marco Erdmann:</b> erdmann AT quattro-research DOT com, quattro research GmbH
 * @version $Id$
 */
public class TargetAminoAcid {

  /** The Logger for this class */
  @SuppressWarnings("unused")
  private static final Logger LOG = LoggerFactory.getLogger(TargetAminoAcid.class);

  private final String name;

  private final int position;

  private final char oneLetterCode;

  private final String threeLetterCode;

  private final String comment;

  /**
   * Constructs a new instance and initializes the necessary fields.
   * 
   * @param name the amino acid name (e.g. Lysine).
   * @param position the position within the domain.
   * @param oneLetterCode the one-letter-code (e.g. K).
   * @param threeLetterCode the three-letter-code (e.g. Lys).
   * @param comment additional information about the amino acid.
   */
  public TargetAminoAcid(String name, int position, char oneLetterCode, String threeLetterCode, String comment) {
    this.name = name;
    this.position = position;
    this.oneLetterCode = oneLetterCode;
    this.threeLetterCode = threeLetterCode;
    this.comment = comment;
  }

  /**
   * 
   * @return the name of the amino acid.
   */
  public String getName() {
    return name;
  }

  /**
   * 
   * @return the position within the domain.
   */
  public int getPosition() {
    return position;
  }

  /**
   * 
   * @return the one-letter-code.
   */
  public char getOneLetterCode() {
    return oneLetterCode;
  }

  /**
   * 
   * @return the three letter code.
   */
  public String getThreeLetterCode() {
    return threeLetterCode;
  }

  /**
   * 
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    if (comment.equals("")) {
      return String.format("%s - %d", threeLetterCode, position);
    } else {
      return String.format("%s - %d (%s)", threeLetterCode, position, comment);
    }
  }

}
