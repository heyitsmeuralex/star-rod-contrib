package shared;

import java.awt.Component;
import java.awt.Container;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class SwingUtils
{
  public SwingUtils() {}
  
  public static final void enableComponents(Container root, boolean enabled)
  {
    root.setEnabled(enabled);
    enableChildComponents(root, enabled);
  }
  
  private static final void enableChildComponents(Container container, boolean enabled)
  {
    Component[] components = container.getComponents();
    for (Component component : components)
    {
      component.setEnabled(enabled);
      if ((component instanceof Container)) {
        enableComponents((Container)component, enabled);
      }
    }
  }
  
  private static final JFrame createDiaglogFrame(Component parentComponent, String title) {
    JFrame dialogFrame = new JFrame(title);
    dialogFrame.setUndecorated(true);
    dialogFrame.setVisible(true);
    dialogFrame.setLocationRelativeTo(parentComponent);
    dialogFrame.setIconImage(Globals.getDefaultIconImage());
    return dialogFrame;
  }
  
  public static final int showFramedOpenDialog(JFileChooser chooser, Component parentComponent)
  {
    JFrame dialogFrame = createDiaglogFrame(parentComponent, chooser.getDialogTitle());
    int choice = chooser.showOpenDialog(dialogFrame);
    dialogFrame.dispose();
    return choice;
  }
  
  public static final int showFramedSaveDialog(JFileChooser chooser, Component parentComponent)
  {
    JFrame dialogFrame = createDiaglogFrame(parentComponent, chooser.getDialogTitle());
    int choice = chooser.showSaveDialog(dialogFrame);
    dialogFrame.dispose();
    return choice;
  }
  





  public static final void showFramedMessageDialog(Component parentComponent, Object message, String title, int messageType, javax.swing.Icon icon)
  {
    JFrame dialogFrame = createDiaglogFrame(parentComponent, title);
    JOptionPane.showMessageDialog(dialogFrame, message, title, messageType, icon);
    dialogFrame.dispose();
  }
  




  public static final void showFramedMessageDialog(Component parentComponent, Object message, String title, int messageType)
  {
    JFrame dialogFrame = createDiaglogFrame(parentComponent, title);
    JOptionPane.showMessageDialog(dialogFrame, message, title, messageType);
    dialogFrame.dispose();
  }
  




















  public static final int showFramedOptionDialog(Component parentComponent, Object message, String title, int optionType, int messageType, javax.swing.Icon icon, Object[] options, Object initialValue)
  {
    JFrame dialogFrame = createDiaglogFrame(parentComponent, title);
    int choice = JOptionPane.showOptionDialog(dialogFrame, message, title, optionType, messageType, icon, options, initialValue);
    


    dialogFrame.dispose();
    return choice;
  }
  





  public static final int showFramedConfirmDialog(Component parentComponent, Object message, String title, int optionType, int messageType)
  {
    JFrame dialogFrame = createDiaglogFrame(parentComponent, title);
    int choice = JOptionPane.showConfirmDialog(dialogFrame, message, title, optionType, messageType);
    

    dialogFrame.dispose();
    return choice;
  }
  




  public static final int showFramedConfirmDialog(Component parentComponent, Object message, String title, int optionType)
  {
    JFrame dialogFrame = createDiaglogFrame(parentComponent, title);
    int choice = JOptionPane.showConfirmDialog(dialogFrame, message, title, optionType);
    
    dialogFrame.dispose();
    return choice;
  }
  




  public static final String showFramedInputDialog(Component parentComponent, Object message, String title, int messageType)
  {
    JFrame dialogFrame = createDiaglogFrame(parentComponent, title);
    String reply = JOptionPane.showInputDialog(dialogFrame, message, title, messageType);
    dialogFrame.dispose();
    return reply;
  }
  
  public static final JLabel getLabel(String text, float point)
  {
    JLabel lbl = new JLabel(text);
    setFontSize(lbl, point);
    return lbl;
  }
  
  public static JLabel getLabel(String text, int horizontalAlignment, int point)
  {
    JLabel lbl = new JLabel(text, horizontalAlignment);
    setFontSize(lbl, point);
    return lbl;
  }
  
  public static final void setFontSize(JComponent comp, float point)
  {
    comp.setFont(comp.getFont().deriveFont(point));
  }
}
