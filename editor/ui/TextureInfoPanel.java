package editor.ui;

import data.texture.EditorTexture;
import editor.render.TextureManager;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;





public class TextureInfoPanel
  extends JPanel
{
  private static final long serialVersionUID = 1L;
  private EditorTexture texture;
  private final JLabel image;
  private final JLabel aux;
  private final JLabel name;
  private final JLabel size;
  private final JLabel count;
  private final JPanel auxPanel;
  private final JLabel combine;
  private static TextureInfoPanel instance = null;
  
  public static TextureInfoPanel instance()
  {
    return instance;
  }
  
  public TextureInfoPanel(SwingGUI gui)
  {
    if (instance != null)
      throw new IllegalStateException("There can be only one TextureInfoPanel");
    instance = this;
    
    image = new JLabel();
    image.setHorizontalAlignment(0);
    
    aux = new JLabel();
    aux.setHorizontalAlignment(0);
    
    name = new JLabel();
    size = new JLabel();
    count = new JLabel();
    
    JButton selectModelsButton = new JButton("Select All Models");
    gui.addButtonCommand(selectModelsButton, GuiCommand.SELECT_ALL_WITH_TEXTURE);
    
    JButton previewButton = new JButton("Preview Options");
    gui.addButtonCommand(previewButton, GuiCommand.SHOW_TEXTURE_SCROLL);
    
    setLayout(new MigLayout("fillx, hidemode 3"));
    add(image);
    
    JPanel generalInfo = new JPanel(new MigLayout("flowy, fill, ins 0"));
    generalInfo.add(name);
    generalInfo.add(size);
    generalInfo.add(count);
    generalInfo.add(selectModelsButton, "bottom");
    generalInfo.add(previewButton, "bottom");
    add(generalInfo, "growy, gapleft 8, gapright 8");
    
    combine = new JLabel();
    combine.setHorizontalAlignment(0);
    
    auxPanel = new JPanel(new MigLayout("flowy, fill, ins 0"));
    
    auxPanel.add(new JLabel("Aux Image:"), "center");
    auxPanel.add(aux, "center");
    auxPanel.add(combine, "center");
    auxPanel.add(new JLabel(" "), "growy, pushy");
    add(auxPanel, "growy");
    
    auxPanel.setVisible(false);
  }
  
  public void setTexture(final EditorTexture t)
  {
    texture = t;
    
    if (t != null)
    {
      ImageIcon icon = new ImageIcon(TextureManager.background)
      {
        private static final long serialVersionUID = 1L;
        
        public void paintIcon(Component c, Graphics g, int x, int y)
        {
          g.drawImage(TextureManager.background, x, y, null);
          g.drawImage(timgPreview, x, y, null);
        }
      };
      image.setIcon(icon);
      
      if (t.hasAux())
      {
        ImageIcon auxIcon = new ImageIcon(TextureManager.miniBackground)
        {
          private static final long serialVersionUID = 1L;
          
          public void paintIcon(Component c, Graphics g, int x, int y)
          {
            g.drawImage(TextureManager.miniBackground, x, y, null);
            g.drawImage(tauxPreview, x, y, 48, 48, null);
          }
        };
        aux.setIcon(auxIcon);
        combine.setText(String.format("Combine: %02X", new Object[] { Integer.valueOf(t.getAuxCombine()) }));
        auxPanel.setVisible(true);
      }
      else
      {
        auxPanel.setVisible(false);
      }
      
      name.setText(t.getName());
      setCount(modelCount);
      size.setText(t.getWidth() + " x " + t.getHeight());
    } else {
      image.setIcon(new ImageIcon(TextureManager.background));
      name.setText("no texture");
      setCount(TextureManager.untexturedCount);
      size.setText(" ");
    }
    
    repaint();
  }
  
  public void updateCount(EditorTexture t)
  {
    if (t == texture)
    {
      if (t != null) {
        setCount(modelCount);
      } else {
        setCount(TextureManager.untexturedCount);
      }
    }
  }
  
  private void setCount(int num) {
    if (num > 1) {
      count.setText("Used by " + num + " models");
    } else if (num == 1) {
      count.setText("Used by 1 model");
    } else {
      count.setText("Unused");
    }
  }
}
