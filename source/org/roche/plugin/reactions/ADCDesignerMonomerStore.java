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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.helm.notation.MonomerException;
import org.helm.notation.MonomerFactory;
import org.helm.notation.MonomerStore;
import org.helm.notation.model.Monomer;
import org.jdom.JDOMException;
import org.roche.plugin.reactions.models.ComparableMonomer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * {@code ADCDesignerMonomerStore} manages monomers in local monomer store.
 * 
 * @author <b>Stefan Klostermann:</b> Stefan DOT Klostermann AT roche DOT com, Roche Pharma Research and Early
 *         Development - Informatics, Roche Innovation Center Munich
 * @author <b>Sabrina Hecht:</b> hecht AT quattro-research DOT com, quattro research GmbH
 * @version $Id$
 */
public class ADCDesignerMonomerStore {

  /** The Logger for this class */
  private static final Logger LOG = LoggerFactory.getLogger(ADCDesignerMonomerStore.class);

  private static ADCDesignerMonomerStore _instance;

  List<ComparableMonomer> monomerStore;

  private ADCDesignerMonomerStore() {
  }

  public static ADCDesignerMonomerStore getInstance() {
    if (_instance == null) {
      _instance = new ADCDesignerMonomerStore();
    }

    return _instance;
  }

  public List<ComparableMonomer> getStoredComparableMonomers() {
    return monomerStore;
  }

  /**
   * Fetches a list of reactive monomers already available from the local monomer store
   */
  public void refreshReactiveMonomersInStore() {
    monomerStore = new LinkedList<ComparableMonomer>();
    MonomerFactory factory;
    try {
      factory = MonomerFactory.getInstance();

      MonomerStore store = factory.getMonomerStore();
      Map<String, Monomer> chemicalMonomers = store.getMonomers(Monomer.CHEMICAL_POLYMER_TYPE);
      for (String key : chemicalMonomers.keySet()) {
        LOG.debug("Key: " + key + " - ");
        monomerStore.add(convertMonomerToComparableMonomer(chemicalMonomers.get(key)));

      }
    } catch (MonomerException | IOException | JDOMException e) {
      LOG.error(e.getMessage(), e);
    }

  }

  /**
   * Converts molecule
   * 
   * @param monomer a reactive species.
   * @throws Exception
   */
  private ComparableMonomer convertMonomerToComparableMonomer(Monomer monomer) {

    ComparableMonomer compMon = new ComparableMonomer(monomer, monomer.getAlternateId());
    return compMon;
  }

}
