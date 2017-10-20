package net.java.games.util.plugins.test;

import javax.swing.DefaultListModel;
import javax.swing.JList;


























































class ListUpdater
  implements Runnable
{
  Object[] objList;
  DefaultListModel mdl;
  
  public ListUpdater(JList jlist, Object[] list)
  {
    objList = list;
    mdl = ((DefaultListModel)jlist.getModel());
  }
  
  public void run() {
    mdl.clear();
    for (int i = 0; i < objList.length; i++) {
      mdl.addElement(objList[i]);
    }
  }
}
