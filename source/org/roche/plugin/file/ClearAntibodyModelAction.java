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
package org.roche.plugin.file;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.roche.antibody.ui.actions.menu.AbstractEditorAction;
import org.roche.antibody.ui.components.AntibodyEditorAccess;
import org.roche.antibody.ui.components.AntibodyEditorPane;

/**
 * Starts Antibody Loading Dialog.
 * 
 * @author <b>Stefan Klostermann:</b> Stefan DOT Klostermann AT roche DOT com, Roche Pharma Research and Early
 *         Development - Informatics, Roche Innovation Center Munich
 * @author <b>Marco Lanig:</b> lanig AT quattro-research DOT com, quattro research GmbH
 * 
 */
public class ClearAntibodyModelAction extends AbstractEditorAction {

  /** Generated UID */
  private static final long serialVersionUID = 8349196639441490984L;

  public static final String NAME = "Clear Antibody Panel...";

  public static final String NAME_SHORT = "Clear Antibody...";

  public static final String SHORT_DESCRIPTION = "Removes the antibody from the editor panel.";

  public static final String IMAGE_PATH = "dna-trash.png";

  public ClearAntibodyModelAction(JFrame parentFrame) {
    super(parentFrame, NAME);

    ImageIcon icon = getImageIcon(IMAGE_PATH);
    if (icon != null) {
      this.putValue(Action.SMALL_ICON, icon);
    }
    this.putValue(Action.SHORT_DESCRIPTION, SHORT_DESCRIPTION);

    setMenuName("File");
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    AntibodyEditorPane pane = AntibodyEditorAccess.getInstance().getAntibodyEditorPane();
    if (pane != null) {
      AntibodyEditorAccess.getInstance().getAntibodyEditorPane().setModel(null);
    }
  }

  @Override
  public int getMenuEntryIndex() {
    return 3;
  }

}
