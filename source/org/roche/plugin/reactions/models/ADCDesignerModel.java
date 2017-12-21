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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ComboBoxModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.helm.notation.MonomerStore;
import org.roche.antibody.model.antibody.Connection;
import org.roche.antibody.model.antibody.Domain;
import org.roche.plugin.reactions.ADCDesignerMonomerStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quattroresearch.antibody.UnknownMutation;

/**
 * 
 * {@code ADCDesignerModel} is the model class for view {@code ADCDesignerDialog}, which holds lov items.
 * 
 * @author <b>Stefan Klostermann:</b> Stefan DOT Klostermann AT roche DOT com, Roche Pharma Research and Early
 *         Development - Informatics, Roche Innovation Center Munich
 * @author <b>Marco Erdmann:</b> erdmann AT quattro-research DOT com, quattro research GmbH
 * @version $Id$
 */
public class ADCDesignerModel {

  /** The Logger for this class */
  @SuppressWarnings("unused")
  private static final Logger LOG = LoggerFactory
      .getLogger(ADCDesignerModel.class);

  private static final Map<Character, TargetAminoAcid> POTENTIAL_TARGETS = createPotentialTargets();

  private final Domain domain;

  @SuppressWarnings("unused")
  private final MonomerStore monomerStore;

  private final List<TargetAminoAcid> targetAminoAcids;

  private final List<AminoAcidType> aminoAcidTypes;

  public ADCDesignerModel(Domain domain, MonomerStore monomerStore) {
    this.domain = domain;
    this.monomerStore = monomerStore;
    this.targetAminoAcids = createTargetAminoAcidList(domain.getSequence());
    this.aminoAcidTypes = createAminoAcidTypeList(domain.getSequence());
  }

  /**
   * Create an unmodifiable list of kinds of amino acids with reactive side chains.
   * 
   * @param domainSequence the amino acid sequence of the domain.
   * @return an unmodifiable list of amino acids with reactive side chains that are part of the domain.
   */
  private List<AminoAcidType> createAminoAcidTypeList(String domainSequence) {
    List<AminoAcidType> result = new ArrayList<AminoAcidType>();
    for (Character oneLetterCode : POTENTIAL_TARGETS.keySet()) {
      int index = domainSequence.indexOf(oneLetterCode);
      if (index >= 0) {
        TargetAminoAcid aa = POTENTIAL_TARGETS.get(oneLetterCode);
        result.add(new AminoAcidType(aa.getName(), index + 1, aa
            .getOneLetterCode(), aa.getThreeLetterCode(), ""));
      }
    }

    Collections.sort(result, new Comparator<AminoAcidType>() {

      @Override
      public int compare(AminoAcidType left, AminoAcidType right) {
        return left.toString().compareTo(right.toString());
      }

    });

    return Collections.unmodifiableList(result);
  }

  /**
   * Creates an unmodifiable list of potential modification targets in the given domain.
   * 
   * @param domainSequence the amino acid sequence of the domain.
   * @return an unmodifiable list of chemically modifiable amino acids in the domain.
   */
  private List<TargetAminoAcid> createTargetAminoAcidList(
      String domainSequence) {
    List<TargetAminoAcid> result = new ArrayList<TargetAminoAcid>();

    for (int i = 0; i < domainSequence.length(); i++) {
      char c = domainSequence.charAt(i);
      if (POTENTIAL_TARGETS.containsKey(c)) {
        String info = getAdditionalAminoAcidInfo(i + 1);
        TargetAminoAcid template = POTENTIAL_TARGETS.get(c);
        result.add(new TargetAminoAcid(template.getName(), i + 1,
            template.getOneLetterCode(), template
                .getThreeLetterCode(), info));
      }
    }

    Collections.sort(result, new Comparator<TargetAminoAcid>() {

      @Override
      public int compare(TargetAminoAcid left, TargetAminoAcid right) {
        int result = left.getOneLetterCode() - right.getOneLetterCode();

        if (result == 0) {
          result = left.getPosition() - right.getPosition();
        }

        return result;
      }
    });

    return Collections.unmodifiableList(result);
  }

  /**
   * Checks whether amino acid in given domain on given position is part of disulfid bridge.
   * 
   * @param domain
   * @param position
   * @return
   */
  public static boolean isPartOfDisulfidBridge(Domain domain, int position) {
    int absolutePosition = position + domain.getStartPosition() - 1;

    List<Connection> connections = domain.getConnections();

    for (Connection connection : connections) {
      if (absolutePosition == connection.getSourcePosition()
          || absolutePosition == connection.getTargetPosition()) {
        if (domain.getSequence().charAt(position - 1) == 'C') {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Retrieves additional information about the current domain position.
   * 
   * @param position the position within the domain
   * @return a {@code String} holding additional information.
   */
  private String getAdditionalAminoAcidInfo(int position) {
    String result = "";

    if (position < 1 || position > domain.getNormalizedEndPosition()) {
      return result;
    }

    if (isPartOfDisulfidBridge(domain, position)) {
      result = "S-S";
    }

    for (UnknownMutation mutation : domain.getAllMutations()) {
      if (position == mutation.getPosition()) {
        result = result.equals("") ? "mut." : result + "|mut.";
        break;
      }
    }

    return result;
  }

  /**
   * Creates a {@code Map} of amino acids that have reactive side chains (i.e. C, D, E, K).
   * 
   * @return the map of unmodifiable amino acids.
   */
  private static Map<Character, TargetAminoAcid> createPotentialTargets() {
    Map<Character, TargetAminoAcid> result = new HashMap<Character, TargetAminoAcid>();
    result.put('C', new TargetAminoAcid("Cysteine", 0, 'C', "Cys", ""));
    result.put('D', new TargetAminoAcid("Aspartic acid", 0, 'D', "Asp", ""));
    result.put('E', new TargetAminoAcid("Glutamic acid", 0, 'E', "Glu", ""));
    result.put('K', new TargetAminoAcid("Lysine", 0, 'K', "Lys", ""));
    return Collections.unmodifiableMap(result);
  }

  /**
   * Creates the {@link ComboBoxModel} for the amino acid selection.
   * 
   * @return a suitable {@code ComboxBoxModel<TargetAminoAcid>}.
   */
  public TargetAminoAcidComboBoxModel getTargetAminoAcidComboBoxModel() {
    return new TargetAminoAcidComboBoxModel(this.targetAminoAcids);
  }

  /**
   * Creates the {@link ComboBoxModel} for the amino acid type selection.
   * 
   * @return a suitable {@code ComboBoxModel<TargetAminoAcid>}.
   */
  public AminoAcidTypeComboBoxModel getAminoAcidTypeComboBoxModel() {
    return new AminoAcidTypeComboBoxModel(this.aminoAcidTypes);
  }

  /**
   * Creates a {@link ComboBoxModel} for the monomer selection.
   * 
   * @return as suitable {@code ComboboxModel<ComparableMonomer>}.
   */
  public MonomerTableModel getMonomerTableModel() {
    return new MonomerTableModel(ADCDesignerMonomerStore.getInstance()
        .getStoredComparableMonomers());
  }

  TableModel dataModel = new AbstractTableModel() {
    @Override
    public int getColumnCount() {
      return 4;
    }

    @Override
    public int getRowCount() {
      return 4;
    }

    @Override
    public Object getValueAt(int row, int col) {
      return new Integer(row * col);
    }
  };

  public Domain getDomain() {
    return this.domain;
  }

}
