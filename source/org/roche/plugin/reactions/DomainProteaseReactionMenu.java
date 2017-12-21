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

import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.roche.antibody.model.antibody.Domain;
import org.roche.antibody.services.ConfigFileService;
import org.roche.antibody.services.ProteaseDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quattroresearch.antibody.plugin.EditorPopupMenu;

/**
 * 
 * {@code DomainProteaseReactionMenu} popup menu which shows {@code ProteaseDescription}s.
 * 
 * @author <b>Stefan Klostermann:</b> Stefan DOT Klostermann AT roche DOT com, Roche Pharma Research and Early
 *         Development - Informatics, Roche Innovation Center Munich
 * @author <b>Marco Erdmann:</b> erdmann AT quattro-research DOT com, quattro research GmbH
 * @author <b>Marco Lanig:</b> lanig AT quattro-research DOT com, quattro research GmbH
 * 
 * @version $Id$
 */
public class DomainProteaseReactionMenu extends EditorPopupMenu {

  private static final long serialVersionUID = 1L;

  /** The Logger for this class */
  private static final Logger LOG = LoggerFactory.getLogger(DomainProteaseReactionMenu.class.toString());

  private JFrame parentFrame;

  private boolean isLoaded = false;

  private List<ProteaseDescription> cachedDescriptions;

  public DomainProteaseReactionMenu(JFrame parentFrame) {
    super(parentFrame, "Protease Reaction...");

    this.parentFrame = parentFrame;
  }

  /**
   * Retrieves a list of protease reactions from the backend and generates context menu actions where applicable.
   * 
   * @param popup the {@link JPopupMenu} instance.
   * @return the number of protease reactions that were added
   */
  private int fillInProteaseReactionActions(Domain domain) {
    LOG.debug("Searching protease descriptions for domain " + domain.getName());

    int reactionCount = 0;

    // first remove all that were inside before
    removeAll();
    // readd them
    for (ProteaseDescription desc : cachedDescriptions) {
      if (desc.getIsActive()) {
        add(new DomainProteaseReactionAction(domain, desc));
        reactionCount++;
      }
    }

    return reactionCount;
  }

  @Override
  public void onInit() {
    super.onInit();
  }

  @Override
  public JMenu load(Domain domain) {
    try {
      if (!isLoaded) {
        loadProteaseDescriptions();
        isLoaded = true;
      }

    } finally {
      LOG.info("Domain protease reaction menu loaded with domain " + domain.getName());
      setVisible(fillInProteaseReactionActions(domain) > 0);
    }

    return this;
  }

  public void loadProteaseDescriptions() {
    cachedDescriptions = new LinkedList<ProteaseDescription>();
    try {
      cachedDescriptions = ConfigFileService.getInstance().getProteaseDescriptions();

    } catch (Exception e) {
      JOptionPane.showMessageDialog(parentFrame, "Could not load protease descriptions. Maybe database access failed.", "Protease Descriptions unavailable.", JOptionPane.ERROR_MESSAGE);
      LOG.error(e.getMessage());
    }
    LOG.debug("Protease Reaction plugin preloaded " + cachedDescriptions.size() + " protease descriptions.");
  }
}
