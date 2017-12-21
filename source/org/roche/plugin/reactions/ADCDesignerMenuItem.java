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

import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenuItem;

import org.helm.notation.MonomerException;
import org.helm.notation.MonomerFactory;
import org.helm.notation.MonomerStore;
import org.helm.notation.model.Monomer;
import org.jdom.JDOMException;
import org.roche.antibody.model.antibody.Domain;
import org.roche.plugin.reactions.rest.ADCUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quattroresearch.antibody.plugin.EditorPopupMenuItem;

/**
 * 
 * {@code ADCDesignerMenuItem} preloads all chemical monomers on init and loads them into the context menu of a selected
 * domain.
 * 
 * @author <b>Stefan Klostermann:</b> Stefan DOT Klostermann AT roche DOT com, Roche Pharma Research and Early
 *         Development - Informatics, Roche Innovation Center Munich
 * @author <b>Marco Lanig:</b> lanig AT quattro-research DOT com, quattro research GmbH
 * @version $Id$
 */
public class ADCDesignerMenuItem extends EditorPopupMenuItem {

  /** */
  private static final long serialVersionUID = -8198850533647152096L;

  /** The Logger for this class */
  private static final Logger LOG = LoggerFactory.getLogger(ADCDesignerMenuItem.class);

  public ADCDesignerMenuItem(JFrame parentFrame) {
    super(parentFrame);
  }

  /**
   * 
   * {@inheritDoc}
   */
  @Override
  public void onInit() {
    super.onInit();

    LOG.info("Init ADC designer. ");
    try {
      addReactiveMonomers();

      ADCDesignerMonomerStore.getInstance().refreshReactiveMonomersInStore();
    } catch (Exception e) {
      LOG.error("Reactive monomers could not be loaded into local store: " + e.getClass().getSimpleName());
    }
  }

  @Override
  public JMenuItem load(Domain domain) {
    LOG.info("ADC designer loaded with domain " + domain.getName());
    return new JMenuItem(new ADCDesignerAction(getParentFrame(), domain));
  }

  private void addReactiveMonomers() throws IOException, MonomerException, JDOMException {
    MonomerFactory factory = MonomerFactory.getInstance();
    MonomerStore store = factory.getMonomerStore();

    /* Biotin */
    Monomer mon1 = new Monomer(Monomer.CHEMICAL_POLYMER_TYPE, Monomer.UNDEFINED_MOMONER_TYPE, "", "Biotin");
    mon1.setName("Biotin");
    mon1.setMolfile(ADCUtils.BIOTIN_COUPLING_DEFAULT_MOL);
    mon1.setCanSMILES(ADCUtils.BIOTIN_COUPLING_DEFAULT_SMILES);
    mon1.setAlternateId("Biotin");

    /* Maleiimid */
    Monomer mon2 = new Monomer(Monomer.CHEMICAL_POLYMER_TYPE, Monomer.UNDEFINED_MOMONER_TYPE, "", "Maleiimid");
    mon2.setName("Maleiimid");
    mon2.setMolfile(ADCUtils.MALEIIMID_COUPLING_DEFAULT_MOL);
    mon2.setCanSMILES(ADCUtils.MALEIIMID_COUPLING_DEFAULT_SMILES);
    mon2.setAlternateId("Maleiimid");

    /* Ethylene glycol is already in the monomer */
    /*
     * Monomer mon3 = new Monomer(Monomer.CHEMICAL_POLYMER_TYPE, Monomer.UNDEFINED_MOMONER_TYPE, "", "Ethylene glycol");
     * mon3.setName("Ethylene glycol"); mon3.setMolfile(ADCUtils.ETHYLENE_COUPLING_DEFAULT_MOL);
     * mon3.setCanSMILES(ADCUtils.ETHYLENE_COUPLING_DEFAULT_SMILES); mon3.setAlternateId("Etylene glycol");
     */

    store.addMonomer(mon1, true);
    store.addMonomer(mon2, true);
    /* store.addMonomer(mon3, true); */

  }

}
