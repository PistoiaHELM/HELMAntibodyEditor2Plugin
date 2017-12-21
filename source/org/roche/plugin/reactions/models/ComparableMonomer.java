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

import java.io.IOException;
import java.util.List;

import org.helm.notation.model.Attachment;
import org.helm.notation.model.MoleculeInfo;
import org.helm.notation.model.Monomer;

import chemaxon.marvin.plugin.PluginException;

/**
 * 
 * {@code ComparableMonomer} is the comparable monomer class used in LOVs and lists of {@code ADCDesignerDialog}.
 * 
 * @author <b>Stefan Klostermann:</b> Stefan DOT Klostermann AT roche DOT com, Roche Pharma Research and Early
 *         Development - Informatics, Roche Innovation Center Munich
 * @author <b>Marco Erdmann:</b> erdmann AT quattro-research DOT com, quattro research GmbH
 * @version $Id$
 */
public class ComparableMonomer extends Monomer implements Comparable<ComparableMonomer> {

  /**
   * Generated {@code serialVersionUID}.
   */
  private static final long serialVersionUID = 7838672265430414533L;

  private final Monomer wrappedMonomer;

  private final String name;

  /**
   * Constructs a sortable instance of {@link Monomer} by wrapping the original object.
   * 
   * @param monomer the monomer to initialize this instance from.
   */
  public ComparableMonomer(Monomer monomer, String trivialName) {
    super();
    this.wrappedMonomer = monomer;
    this.name = trivialName;

  }

  /**
   * 
   * @return the wrapped monomer, i.e. the real implementation.
   */
  public Monomer getWrappedMonomer() {
    return wrappedMonomer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {

    return (getTrivialName().isEmpty()) ? getWrappedMonomer().getName()
        : getTrivialName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(ComparableMonomer o) {
    if (this == o) {
      return 0;
    }
    if (o == null) {
      return 1;
    }
    String nameForCompareThis = getTrivialName().isEmpty() ? getName()
        : getTrivialName();
    String nameForCompareOther = o.getTrivialName().isEmpty() ? o.getName()
        : o.getTrivialName();
    return nameForCompareThis.compareToIgnoreCase(nameForCompareOther);
  }

  public String getTrivialName() {
    return name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getId() {
    return getWrappedMonomer().getId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(int id) {
    getWrappedMonomer().setId(id);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getAlternateId() {
    return getWrappedMonomer().getAlternateId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setAlternateId(String alternateId) {
    getWrappedMonomer().setAlternateId(alternateId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getNaturalAnalog() {
    return getWrappedMonomer().getNaturalAnalog();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setNaturalAnalog(String naturalAnalog) {
    getWrappedMonomer().setNaturalAnalog(naturalAnalog);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setAdHocMonomer(boolean adHocMonomer) {
    getWrappedMonomer().setAdHocMonomer(adHocMonomer);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isAdHocMonomer() {
    return getWrappedMonomer().isAdHocMonomer();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCanSMILES() {
    return getWrappedMonomer().getCanSMILES();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCanSMILES(String canSMILES) {
    getWrappedMonomer().setCanSMILES(canSMILES);
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public String getMonomerType() {
    return getWrappedMonomer().getMonomerType();
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public void setMonomerType(String monomerType) {
    getWrappedMonomer().setMonomerType(monomerType);
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public String getPolymerType() {
    return getWrappedMonomer().getPolymerType();
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public void setPolymerType(String polymerType) {
    getWrappedMonomer().setPolymerType(polymerType);
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public List<Attachment> getAttachmentList() {
    return getWrappedMonomer().getAttachmentList();
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public void setAttachmentList(List<Attachment> attachmentList) {
    getWrappedMonomer().setAttachmentList(attachmentList);
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public String getMolfile() {
    return getWrappedMonomer().getMolfile();
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public void setMolfile(String molfile) {
    getWrappedMonomer().setMolfile(molfile);
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public Attachment getAttachment(String label) {
    return getWrappedMonomer().getAttachment(label);
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public MoleculeInfo getCapMoleculeInfo(String label) throws IOException,
      PluginException {
    return getWrappedMonomer().getCapMoleculeInfo(label);
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public boolean addAttachment(Attachment attachment) {
    return getWrappedMonomer().addAttachment(attachment);
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public String getName() {
    return getWrappedMonomer().getName();
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public void setName(String name) {
    getWrappedMonomer().setName(name);
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public boolean isModified() {
    return getWrappedMonomer().isModified();
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public boolean isSameType(Monomer m) {
    return getWrappedMonomer().isSameType(m);
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public String getAttachmentListString() {
    return getWrappedMonomer().getAttachmentListString();
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public boolean isNewMonomer() {
    return getWrappedMonomer().isNewMonomer();
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public void setNewMonomer(boolean newMonomer) {
    getWrappedMonomer().setNewMonomer(newMonomer);
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public boolean attachmentEquals(Monomer monomer) {
    return getWrappedMonomer().attachmentEquals(monomer);
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public boolean attachmentContains(Monomer monomer) {
    return getWrappedMonomer().attachmentContains(monomer);
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public boolean containAnyAtom() throws IOException {
    return getWrappedMonomer().containAnyAtom();
  }

}
