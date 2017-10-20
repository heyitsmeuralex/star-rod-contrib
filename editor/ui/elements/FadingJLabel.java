package editor.ui.elements;

import java.awt.Color;
import javax.swing.JLabel;


public class FadingJLabel
  extends JLabel
{
  private static final long serialVersionUID = 1L;
  private Color initialColor = Color.black;
  private final double holdTime;
  private final double fadeTime;
  private double elapsedTime = 0.0D;
  private boolean fadeComplete = true;
  
  public FadingJLabel(double holdTime, double fadeTime)
  {
    super("");
    this.holdTime = holdTime;
    this.fadeTime = fadeTime;
  }
  
  public FadingJLabel(int horizontalAlignment, float holdTime, float fadeTime)
  {
    super("", horizontalAlignment);
    this.holdTime = holdTime;
    this.fadeTime = fadeTime;
  }
  
  public void setMessage(String msg, Color c)
  {
    setText(msg);
    setForeground(c);
    initialColor = c;
    elapsedTime = 0.0D;
    fadeComplete = false;
  }
  
  public void update(double deltaTime)
  {
    if (fadeComplete) {
      return;
    }
    if (elapsedTime >= holdTime)
    {
      double alpha = (elapsedTime - holdTime) / fadeTime;
      alpha = Math.min(alpha, 1.0D);
      
      Color bg = getBackground();
      Color in = initialColor;
      int r = (int)(alpha * bg.getRed() + (1.0D - alpha) * in.getRed());
      int g = (int)(alpha * bg.getGreen() + (1.0D - alpha) * in.getGreen());
      int b = (int)(alpha * bg.getBlue() + (1.0D - alpha) * in.getBlue());
      setForeground(new Color(r, g, b));
      
      if (alpha == 1.0D) {
        fadeComplete = true;
      }
    }
    elapsedTime += deltaTime;
  }
}
