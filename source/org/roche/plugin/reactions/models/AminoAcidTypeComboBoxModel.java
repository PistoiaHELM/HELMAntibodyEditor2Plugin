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

import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

/**
 * 
 * {@code AminoAcidTypeComboBoxModel} combo box for {@code ADCDesignerDialog}.
 * 
 * @author <b>Stefan Klostermann:</b> Stefan DOT Klostermann AT roche DOT com, Roche Pharma Research and Early
 *         Development - Informatics, Roche Innovation Center Munich
 * @author <b>Marco Erdmann:</b> erdmann AT quattro-research DOT com, quattro research GmbH
 * @version $Id$
 */
public class AminoAcidTypeComboBoxModel implements ComboBoxModel<AminoAcidType> {

  private final List<AminoAcidType> data;

  private int selectedIndex;

  public AminoAcidTypeComboBoxModel(List<AminoAcidType> data) {
    this.data = data;
    this.selectedIndex = 0;
  }

  @Override
  public int getSize() {
    return data.size();
  }

  @Override
  public AminoAcidType getElementAt(int index) {
    return data.get(index);
  }

  @Override
  public void addListDataListener(ListDataListener l) {
  }

  @Override
  public void removeListDataListener(ListDataListener l) {
  }

  @Override
  public void setSelectedItem(Object anItem) {
    selectedIndex = data.indexOf(anItem);
  }

  @Override
  public Object getSelectedItem() {
    if (selectedIndex < getSize()) {
      return data.get(selectedIndex);
    }
    return null;
  }
}
