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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.configuration.Configuration;
import org.roche.antibody.services.PreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * {@code MonomerTableModel} is the table model used to show monomers in {@code ADCDesignerDialog}.
 * 
 * @author <b>Stefan Klostermann:</b> Stefan DOT Klostermann AT roche DOT com, Roche Pharma Research and Early
 *         Development - Informatics, Roche Innovation Center Munich
 * @author <b>Marco Erdmann:</b> erdmann AT quattro-research DOT com, quattro research GmbH
 * @author <b>Sabrina Hecht:</b> hecht AT quattro-research DOT com, quattro research GmbH
 * @version $Id$
 */
public class MonomerTableModel extends AbstractTableModel {

  private static final long serialVersionUID = -1714374340081060389L;

  /** The Logger for this class */
  private static final Logger LOG = LoggerFactory.getLogger(MonomerTableModel.class);

  private final static List<String> BLACK_LIST = createBlackList();

  private final List<ComparableMonomer> chemMonomers;

  private String[] columnNames = new String[] {"Name", "Smiles"};

  public MonomerTableModel(final List<ComparableMonomer> monomers) {
    this.chemMonomers = createChemMonomersList(monomers);
    LOG.debug("" + getRowCount());

  }

  private List<ComparableMonomer> createChemMonomersList(final List<ComparableMonomer> monomers) {
    List<ComparableMonomer> result = new ArrayList<ComparableMonomer>();

    for (ComparableMonomer monomer : monomers) {
      if (!BLACK_LIST.contains(monomer.getName())) {
        if (monomer.getMolfile() == null) {
          LOG.error("Monomer " + monomer.getName() + " has no molfile and won't be added to list.");
        } else {
          result.add(monomer);
        }
      }
    }
    Collections.sort(result);
    return Collections.unmodifiableList(result);
  }

  private static List<String> createBlackList() {
    Configuration appPrefs = PreferencesService.getInstance().getApplicationPrefs();
    return Arrays.asList(new String[] {appPrefs.getString(PreferencesService.C_BLOCKER),
        appPrefs.getString(PreferencesService.N_BLOCKER), appPrefs.getString(PreferencesService.CYS_BLOCKER)});
  }

  @Override
  public int getColumnCount() {
    return columnNames.length;
  }

  @Override
  public String getColumnName(int columnIndex) {
    return columnNames[columnIndex];
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    ComparableMonomer mo = this.chemMonomers.get(rowIndex);
    switch (columnIndex) {
    case 0:
      return mo.getTrivialName();
    case 1:
      return mo.getCanSMILES();
    default:
      return "N/A";
    }
  }

  public List<ComparableMonomer> getMonomerList() {
    return this.chemMonomers;
  }

  @Override
  public int getRowCount() {
    if (this.chemMonomers.isEmpty()) {
      return 0;
    } else {
      return this.chemMonomers.size();
    }
  }

}
