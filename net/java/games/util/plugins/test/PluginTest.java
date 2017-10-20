package net.java.games.util.plugins.test;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import net.java.games.util.plugins.Plugins;


































































public class PluginTest
{
  static final boolean DEBUG = false;
  Plugins plugins;
  JList plist;
  Class[] piList;
  
  public PluginTest()
  {
    try
    {
      plugins = new Plugins(new File("test_plugins"));
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
    JFrame f = new JFrame("PluginTest");
    plist = new JList(new DefaultListModel());
    plist.setCellRenderer(new ClassRenderer());
    Container c = f.getContentPane();
    c.setLayout(new BorderLayout());
    c.add(new JScrollPane(plist), "Center");
    JPanel p = new JPanel();
    c.add(p, "South");
    p.setLayout(new FlowLayout());
    f.pack();
    f.setDefaultCloseOperation(3);
    f.setVisible(true);
    doListAll();
  }
  
  private void doListAll() {
    SwingUtilities.invokeLater(new ListUpdater(plist, plugins.get()));
  }
  

  public static void main(String[] args)
  {
    new PluginTest();
  }
}
