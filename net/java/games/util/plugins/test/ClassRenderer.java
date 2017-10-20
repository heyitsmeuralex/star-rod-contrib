package net.java.games.util.plugins.test;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;










































class ClassRenderer
  implements ListCellRenderer
{
  JLabel label = new JLabel();
  
  ClassRenderer() {}
  
  public Component getListCellRendererComponent(JList jList, Object obj, int param, boolean param3, boolean param4) { label.setText(((Class)obj).getName());
    label.setForeground(Color.BLACK);
    label.setBackground(Color.WHITE);
    


    return label;
  }
}
