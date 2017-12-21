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

import javax.swing.JFrame;

import org.roche.antibody.ui.toolbar.buttons.ToolBarToggleButton;

/**
 * {@code ToolbarClearAntibodyModelButton} starts the action which clears the antibody.
 * 
 * @author <b>Stefan Klostermann:</b> Stefan DOT Klostermann AT roche DOT com, Roche Pharma Research and Early
 *         Development - Informatics, Roche Innovation Center Munich
 * @author <b>Stefan Zilch:</b> stefan_dieter DOT zilch AT contractors DOT roche DOT com
 * 
 * @version $Id: ToolbarRegisterAntibodyButton.java 14995 2015-02-25 13:39:44Z schirmb $
 */
public class ToolbarClearAntibodyModelButton extends ToolBarToggleButton {

  private static final long serialVersionUID = 8283915324500280640L;

  public static final String IMAGE_PATH = "dna-trash_big.png";

  public ToolbarClearAntibodyModelButton(JFrame parentFrame) {
    super(new ClearAntibodyModelAction(parentFrame));

    super.setIcon(getEditorAction().getImageIcon(IMAGE_PATH));
    setToolTipText(ClearAntibodyModelAction.SHORT_DESCRIPTION);
    setText(ClearAntibodyModelAction.NAME_SHORT);

  }
}
