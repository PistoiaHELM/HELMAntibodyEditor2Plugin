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
import javax.swing.JFrame;

import org.roche.antibody.ui.actions.menu.AbstractEditorAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * {@code LoadReactionAction} starts sortase coupling dialog ({@code ReactionDialog}).
 * 
 * @author <b>Stefan Klostermann:</b> Stefan DOT Klostermann AT roche DOT com, Roche Pharma Research and Early
 *         Development - Informatics, Roche Innovation Center Munich
 * @author <b>Marco Lanig:</b> lanig AT quattro-research DOT com, quattro research GmbH
 * @version $Id$
 */
public class LoadReactionAction extends AbstractEditorAction {

  /** Generated UID */
  private static final long serialVersionUID = 8349196639441490984L;

  @SuppressWarnings("unused")
  private static Logger LOG = LoggerFactory
      .getLogger(LoadReactionAction.class);

  public static final String NAME = "Sortase Coupling ...";

  public static final String NAME_SHORT = "Sortase Coupling ...";

  public static final String SHORT_DESCRIPTION = "Sortase Coupling";

  public static final String IMAGE_PATH = "";

  private JFrame parentFrame;

  public LoadReactionAction(JFrame parentFrame) {
    super(parentFrame, NAME);

    this.putValue(Action.SHORT_DESCRIPTION, SHORT_DESCRIPTION);

    setMenuName("Reaction");

    this.parentFrame = parentFrame;
  }

  @Override
  public void actionPerformed(ActionEvent e) {

    ReactionDialog dialog = new ReactionDialog(super.getParentFrame(), true);

    dialog.setLocationRelativeTo(super.getParentFrame());
    dialog.setVisible(true);
    dialog = null;

  }

  @Override
  public void onInit() {
    super.onInit();

  }
}
