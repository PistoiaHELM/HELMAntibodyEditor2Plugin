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


package org.roche.plugin.registration;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.roche.antibody.ui.actions.menu.AbstractEditorAction;
import org.roche.plugin.reactions.ADCDesignerMonomerStore;
import org.roche.plugin.reactions.MonomerDesigner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Starts {@code MonomerDesigner}, which is used to create new monomers and save it to monomer store.
 * 
 * @author <b>Stefan Klostermann:</b> Stefan DOT Klostermann AT roche DOT com, Roche Pharma Research and Early
 *         Development - Informatics, Roche Innovation Center Munich
 * @author <b>Marco Lanig:</b> lanig AT quattro-research DOT com, quattro research GmbH
 * 
 */
public class MonomerDesignerAction extends AbstractEditorAction {

  private static final long serialVersionUID = 8349196639441490984L;

  @SuppressWarnings("unused")
  private static Logger LOG = LoggerFactory
      .getLogger(MonomerDesignerAction.class);

  private MonomerDesigner monomerDesignerDialog;

  public static final String NAME = "Monomer Designer...";

  public static final String SHORT_DESCRIPTION = "May be used to design a monomer and save it to local monomer store.";

  public static final String IMAGE_PATH = "";

  JFrame parentFrame;

  public MonomerDesignerAction(JFrame parentFrame) {
    super(parentFrame, NAME);
    ImageIcon icon = getImageIcon(IMAGE_PATH);
    if (icon != null) {
      this.putValue(Action.SMALL_ICON, icon);
    }
    this.putValue(Action.SHORT_DESCRIPTION, SHORT_DESCRIPTION);

    setMenuName("Registration");

    this.parentFrame = parentFrame;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (monomerDesignerDialog == null) {
      monomerDesignerDialog = new MonomerDesigner(getParentFrame(),
          "Monomer Designer");
      monomerDesignerDialog.setLocationRelativeTo(getParentFrame());
    }
    monomerDesignerDialog.setVisible(true);
    ADCDesignerMonomerStore.getInstance().refreshReactiveMonomersInStore();
    monomerDesignerDialog = null;
  }

  @Override
  public void onInit() {
    super.onInit();
  }

}
