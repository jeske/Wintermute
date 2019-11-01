/*
 * SortHeaderRenderer.java
 *
 * Created on January 24, 2003, 9:44 PM
 */

package simpleimap;

import java.awt.Component;
import javax.swing.*;
import javax.swing.table.*;

/**
 *
 * Created by Claude Duguay
 * Copyright (c) 2002
 *
 * http://www.fawcette.com/javapro/2002_08/magazine/columns/visualcomponents/
 */

    public class SortHeaderRenderer  extends DefaultTableCellRenderer {
      public static Icon NONSORTED = new SortArrowIcon(SortArrowIcon.NONE);
      public static Icon ASCENDING = new SortArrowIcon(SortArrowIcon.ASCENDING);
      public static Icon DECENDING = new SortArrowIcon(SortArrowIcon.DECENDING);

      public SortHeaderRenderer()
      {
        setHorizontalTextPosition(LEFT);
        setHorizontalAlignment(CENTER);
      }

      public Component getTableCellRendererComponent(
        JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int col)
      {
        int index = -1;
        boolean ascending = true;
        TableModel tm = table.getModel();
        if (tm instanceof TableSorter) {
            TableSorter tms = (TableSorter)tm;
            index = tms.getSortedColumnIndex();
            ascending = tms.isSortedColumnAscending();
        }
        if (table != null)
        {
          JTableHeader header = table.getTableHeader();
          if (header != null)
          {
            setForeground(header.getForeground());
            setBackground(header.getBackground());
            setFont(header.getFont());
          }
        }
        Icon icon = ascending ? ASCENDING : DECENDING;
        setIcon(col == index ? icon : NONSORTED);
        setText((value == null) ? "" : value.toString());
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        return this;
      }
    }

    