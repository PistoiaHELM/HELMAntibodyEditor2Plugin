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

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * {@code ReactiveMolecule} contains all relevant data for performing ADC coupling reactions
 * 
 * @author <b>Stefan Klostermann:</b> Stefan DOT Klostermann AT roche DOT com, Roche Pharma Research and Early
 *         Development - Informatics, Roche Innovation Center Munich
 * @author <b>Marco Erdmann:</b> erdmann AT quattro-research DOT com, quattro research GmbH
 * @author <b>Marco Lanig:</b> lanig AT quattro-research DOT com, quattro research GmbH
 * @version $Id$
 */
public class ReactiveMolecule {

  /** The Logger for this class */
  @SuppressWarnings("unused")
  private static final Logger LOG = LoggerFactory.getLogger(ReactiveMolecule.class);

  private final String molfile;

  private final String name;

  private final String extendedSmiles;

  private final String r1;

  private final String r2;

  private final String r3;

  private final String category_1;

  private final String category_2;

  private final String alternate_id;

  /**
   * Constructor used for deserialization from JSON notation.
   * 
   * @param conceptPk the conceptPk.
   * @param conceptUID the concept unique ID.
   * @param srn SRN from IRCI.
   * @param molfile the molfile.
   * @param iupacName the IUPAC name.
   * @param trivialName the trivial name.
   * @param casNo the CAS number.
   * @param extendedSmiles extended smiles notation by ChemAxon.
   * @param r1 leaving group.
   * @param r2 leaving group.
   * @param r3 leaving group.
   */
  @JsonCreator
  public ReactiveMolecule(@JsonProperty("molfile") String molfile, @JsonProperty("trivialName") String trivialName,
      @JsonProperty("extendedSmiles") String extendedSmiles, @JsonProperty("r1") String r1,
      @JsonProperty("r2") String r2, @JsonProperty("r3") String r3, @JsonProperty("category1") String category_1,
      @JsonProperty("category2") String category_2, @JsonProperty("alternate_id") String alternate_id) {
    super();
    this.molfile = molfile;
    this.name = trivialName;
    this.extendedSmiles = extendedSmiles;
    this.r1 = r1;
    this.r2 = r2;
    this.r3 = r3;
    this.category_1 = category_1;
    this.category_2 = category_2;
    this.alternate_id = alternate_id;
  }

  /**
   * 
   * @return the molfile.
   */
  public String getMolfile() {
    return this.molfile;
  }

  /**
   * 
   * @return extended smiles notation by ChemAxon.
   */
  public String getExtendedSmiles() {
    return this.extendedSmiles;
  }

  /**
   * 
   * @return leaving group.
   */
  public String getR1() {
    return this.r1;
  }

  /**
   * 
   * @return leaving group.
   */
  public String getR2() {
    return this.r2;
  }

  /**
   * 
   * @return leaving group.
   */
  public String getR3() {
    return this.r3;
  }

  /**
   * 
   * @return filtering category 1
   */
  public String getCategory1() {
    return this.category_1;
  }

  /**
   * 
   * @return filtering category 2
   */
  public String getCategory2() {
    return this.category_2;
  }

  public String getAlternate_id() {
    return this.alternate_id;
  }

  public String getName() {
    return this.name;
  }

}
