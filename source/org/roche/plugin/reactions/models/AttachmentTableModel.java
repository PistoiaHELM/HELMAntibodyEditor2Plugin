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
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import org.helm.notation.MonomerFactory;
import org.helm.notation.model.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * {@code AttachmentTableModel} models the attachment table used in {@code ADCDesignerDialog}.
 * 
 * @author <b>Stefan Klostermann:</b> Stefan DOT Klostermann AT roche DOT com, Roche Pharma Research and Early
 *         Development - Informatics, Roche Innovation Center Munich
 * @author <b>Marco Erdmann:</b> erdmann AT quattro-research DOT com, quattro research GmbH
 * @version $Id$
 */
public class AttachmentTableModel extends AbstractTableModel {

  /** */
  private static final long serialVersionUID = 2312483826116071480L;

  /** The Logger for this class */
  private static final Logger LOG = LoggerFactory.getLogger(AttachmentTableModel.class);

  private static final int COLUMN_COUNT = 2;

  private List<AttachmentUIBean> data;

  public static final int LABEL_COLUMN_INDEX = 0;

  public static final int ID_COLUMN_INDEX = 1;

  public AttachmentTableModel(List<Attachment> attachments) {
    try {
      data = new ArrayList<AttachmentUIBean>();

      Map<String, List<String>> labelMap = MonomerFactory.getInstance().getAttachmentLabelIDs();

      // Sort labels
      List<String> labels = new ArrayList<String>();
      labels.addAll(labelMap.keySet());
      Collections.sort(labels);

      // build data for the model
      for (String label : labels) {
        AttachmentUIBean bean = new AttachmentUIBean();
        Attachment attachmentOfLabel = getAttachmentByLabel(attachments, label);
        bean.setLabel(label);
        // TODO correct???
        bean.setId(attachmentOfLabel == null ? "" : attachmentOfLabel.getCapGroupName() + "");
        List<String> l = labelMap.get(label);
        l.add(0, "");
        bean.setIds(l.toArray(new String[0]));
        data.add(bean);
      }
    } catch (Exception ex) {
      LOG.debug(ex.getMessage());
    }
  }

  @Override
  public String getColumnName(int columnIndex) {
    switch (columnIndex) {
    case 0:
      return "Attachment Point";
    case 1:
      return "Leaving Group";
    default:
      return "UNKNOWN COLUMN";
    }
  }

  private Attachment getAttachmentByLabel(List<Attachment> attachments, String label) {
    for (Attachment att : attachments) {
      if (att.getLabel().equals(label)) {
        return att;
      }
    }
    return null;
  }

  public List<Attachment> getAttachmentList() {
    List<Attachment> aList = new ArrayList<Attachment>();

    List<AttachmentUIBean> beans = this.getData();
    for (AttachmentUIBean bean : beans) {
      if (null != bean.getId() && bean.getId().length() > 0) {
        Attachment att = new Attachment();
        att.setAlternateId(bean.getId());
        att.setCapGroupName(bean.getId());
        att.setLabel(bean.getLabel());
        aList.add(att);
      }
    }

    return aList;
  }

  @Override
  public int getRowCount() {
    return data.size();
  }

  @Override
  public int getColumnCount() {
    return COLUMN_COUNT;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    AttachmentUIBean bean = getData().get(rowIndex);
    switch (columnIndex) {
    case 0:
      return bean.getLabel();
    case 1:
      return bean.getId();
    default:
      return "";

    }
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    if (column == ID_COLUMN_INDEX) {
      return true;
    }
    return false;
  }

  @Override
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    AttachmentUIBean bean = getData().get(rowIndex);
    if (null != aValue && aValue instanceof String) {
      if (columnIndex == ID_COLUMN_INDEX) {
        bean.setId((String) aValue);
      }
    }
  }

  public List<AttachmentUIBean> getData() {
    return data;
  }

  public void updateAttachments(List<Attachment> attachments) {
    if (null == attachments) {
      attachments = new ArrayList<Attachment>();
    }

    for (AttachmentUIBean bean : data) {
      String uiLabel = bean.getLabel();
      String uiId = "";
      for (Attachment att : attachments) {
        String label = att.getLabel();
        String id = att.getAlternateId();
        if (uiLabel.equals(label)) {
          uiId = id;
          break;
        }
      }
      bean.setId(uiId);
    }
    fireTableDataChanged();
  }

  public String[] getIDsByRow(int row) {
    return getData().get(row).getIds();
  }

  public class AttachmentUIBean {

    private String label;

    private String id;

    private String[] ids;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String[] getIds() {
      return ids;
    }

    public void setIds(String[] ids) {
      this.ids = ids;
    }

    public String getLabel() {
      return label;
    }

    public void setLabel(String label) {
      this.label = label;
    }
  }
}
