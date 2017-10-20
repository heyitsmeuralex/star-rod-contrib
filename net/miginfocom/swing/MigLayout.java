package net.miginfocom.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.BoundSize;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.ComponentWrapper;
import net.miginfocom.layout.ConstraintParser;
import net.miginfocom.layout.ContainerWrapper;
import net.miginfocom.layout.Grid;
import net.miginfocom.layout.LC;
import net.miginfocom.layout.LayoutCallback;
import net.miginfocom.layout.LayoutUtil;
import net.miginfocom.layout.PlatformDefaults;
import net.miginfocom.layout.UnitValue;

public final class MigLayout
  implements LayoutManager2, Externalizable
{
  private final Map<Component, Object> scrConstrMap = new IdentityHashMap(8);
  private Object layoutConstraints = "";
  private Object colConstraints = "";
  private Object rowConstraints = "";
  private transient ContainerWrapper cacheParentW = null;
  private final transient Map<ComponentWrapper, CC> ccMap = new HashMap(8);
  private transient Timer debugTimer = null;
  private transient LC lc = null;
  private transient AC colSpecs = null;
  private transient AC rowSpecs = null;
  private transient Grid grid = null;
  private transient int lastModCount = PlatformDefaults.getModCount();
  private transient int lastHash = -1;
  private transient Dimension lastInvalidSize = null;
  private transient boolean lastWasInvalid = false;
  private transient Dimension lastParentSize = null;
  private transient ArrayList<LayoutCallback> callbackList = null;
  private transient boolean dirty = true;
  private long lastSize = 0L;
  
  public MigLayout()
  {
    this("", "", "");
  }
  
  public MigLayout(String paramString)
  {
    this(paramString, "", "");
  }
  
  public MigLayout(String paramString1, String paramString2)
  {
    this(paramString1, paramString2, "");
  }
  
  public MigLayout(String paramString1, String paramString2, String paramString3)
  {
    setLayoutConstraints(paramString1);
    setColumnConstraints(paramString2);
    setRowConstraints(paramString3);
  }
  
  public MigLayout(LC paramLC)
  {
    this(paramLC, null, null);
  }
  
  public MigLayout(LC paramLC, AC paramAC)
  {
    this(paramLC, paramAC, null);
  }
  
  public MigLayout(LC paramLC, AC paramAC1, AC paramAC2)
  {
    setLayoutConstraints(paramLC);
    setColumnConstraints(paramAC1);
    setRowConstraints(paramAC2);
  }
  
  public Object getLayoutConstraints()
  {
    return layoutConstraints;
  }
  
  public void setLayoutConstraints(Object paramObject)
  {
    if ((paramObject == null) || ((paramObject instanceof String)))
    {
      paramObject = ConstraintParser.prepare((String)paramObject);
      lc = ConstraintParser.parseLayoutConstraint((String)paramObject);
    }
    else if ((paramObject instanceof LC))
    {
      lc = ((LC)paramObject);
    }
    else
    {
      throw new IllegalArgumentException("Illegal constraint type: " + paramObject.getClass().toString());
    }
    layoutConstraints = paramObject;
    dirty = true;
  }
  
  public Object getColumnConstraints()
  {
    return colConstraints;
  }
  
  public void setColumnConstraints(Object paramObject)
  {
    if ((paramObject == null) || ((paramObject instanceof String)))
    {
      paramObject = ConstraintParser.prepare((String)paramObject);
      colSpecs = ConstraintParser.parseColumnConstraints((String)paramObject);
    }
    else if ((paramObject instanceof AC))
    {
      colSpecs = ((AC)paramObject);
    }
    else
    {
      throw new IllegalArgumentException("Illegal constraint type: " + paramObject.getClass().toString());
    }
    colConstraints = paramObject;
    dirty = true;
  }
  
  public Object getRowConstraints()
  {
    return rowConstraints;
  }
  
  public void setRowConstraints(Object paramObject)
  {
    if ((paramObject == null) || ((paramObject instanceof String)))
    {
      paramObject = ConstraintParser.prepare((String)paramObject);
      rowSpecs = ConstraintParser.parseRowConstraints((String)paramObject);
    }
    else if ((paramObject instanceof AC))
    {
      rowSpecs = ((AC)paramObject);
    }
    else
    {
      throw new IllegalArgumentException("Illegal constraint type: " + paramObject.getClass().toString());
    }
    rowConstraints = paramObject;
    dirty = true;
  }
  
  public Map<Component, Object> getConstraintMap()
  {
    return new IdentityHashMap(scrConstrMap);
  }
  
  public void setConstraintMap(Map<Component, Object> paramMap)
  {
    scrConstrMap.clear();
    ccMap.clear();
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      setComponentConstraintsImpl((Component)localEntry.getKey(), localEntry.getValue(), true);
    }
  }
  
  public Object getComponentConstraints(Component paramComponent)
  {
    synchronized (paramComponent.getParent().getTreeLock())
    {
      return scrConstrMap.get(paramComponent);
    }
  }
  
  public void setComponentConstraints(Component paramComponent, Object paramObject)
  {
    setComponentConstraintsImpl(paramComponent, paramObject, false);
  }
  
  private void setComponentConstraintsImpl(Component paramComponent, Object paramObject, boolean paramBoolean)
  {
    Container localContainer = paramComponent.getParent();
    synchronized (localContainer != null ? localContainer.getTreeLock() : new Object())
    {
      if ((!paramBoolean) && (!scrConstrMap.containsKey(paramComponent))) {
        throw new IllegalArgumentException("Component must already be added to parent!");
      }
      SwingComponentWrapper localSwingComponentWrapper = new SwingComponentWrapper(paramComponent);
      if ((paramObject == null) || ((paramObject instanceof String)))
      {
        String str = ConstraintParser.prepare((String)paramObject);
        scrConstrMap.put(paramComponent, paramObject);
        ccMap.put(localSwingComponentWrapper, ConstraintParser.parseComponentConstraint(str));
      }
      else if ((paramObject instanceof CC))
      {
        scrConstrMap.put(paramComponent, paramObject);
        ccMap.put(localSwingComponentWrapper, (CC)paramObject);
      }
      else
      {
        throw new IllegalArgumentException("Constraint must be String or ComponentConstraint: " + paramObject.getClass().toString());
      }
      dirty = true;
    }
  }
  
  public boolean isManagingComponent(Component paramComponent)
  {
    return scrConstrMap.containsKey(paramComponent);
  }
  
  public void addLayoutCallback(LayoutCallback paramLayoutCallback)
  {
    if (paramLayoutCallback == null) {
      throw new NullPointerException();
    }
    if (callbackList == null) {
      callbackList = new ArrayList(1);
    }
    callbackList.add(paramLayoutCallback);
  }
  
  public void removeLayoutCallback(LayoutCallback paramLayoutCallback)
  {
    if (callbackList != null) {
      callbackList.remove(paramLayoutCallback);
    }
  }
  
  private void setDebug(ComponentWrapper paramComponentWrapper, boolean paramBoolean)
  {
    if ((paramBoolean) && ((debugTimer == null) || (debugTimer.getDelay() != getDebugMillis())))
    {
      if (debugTimer != null) {
        debugTimer.stop();
      }
      ContainerWrapper localContainerWrapper = paramComponentWrapper.getParent();
      final Component localComponent = localContainerWrapper != null ? (Component)localContainerWrapper.getComponent() : null;
      debugTimer = new Timer(getDebugMillis(), new MyDebugRepaintListener(null));
      if (localComponent != null) {
        SwingUtilities.invokeLater(new Runnable()
        {
          public void run()
          {
            Container localContainer = localComponent.getParent();
            if (localContainer != null) {
              if ((localContainer instanceof JComponent))
              {
                ((JComponent)localContainer).revalidate();
              }
              else
              {
                localComponent.invalidate();
                localContainer.validate();
              }
            }
          }
        });
      }
      debugTimer.setInitialDelay(100);
      debugTimer.start();
    }
    else if ((!paramBoolean) && (debugTimer != null))
    {
      debugTimer.stop();
      debugTimer = null;
    }
  }
  
  private boolean getDebug()
  {
    return debugTimer != null;
  }
  
  private int getDebugMillis()
  {
    int i = LayoutUtil.getGlobalDebugMillis();
    return i > 0 ? i : lc.getDebugMillis();
  }
  
  private void checkCache(Container paramContainer)
  {
    if (paramContainer == null) {
      return;
    }
    if (dirty) {
      grid = null;
    }
    int i = PlatformDefaults.getModCount();
    if (lastModCount != i)
    {
      grid = null;
      lastModCount = i;
    }
    if (!paramContainer.isValid())
    {
      if (!lastWasInvalid)
      {
        lastWasInvalid = true;
        int j = 0;
        int k = 0;
        Object localObject1 = ccMap.keySet().iterator();
        while (((Iterator)localObject1).hasNext())
        {
          ComponentWrapper localComponentWrapper = (ComponentWrapper)((Iterator)localObject1).next();
          Object localObject2 = localComponentWrapper.getComponent();
          if (((localObject2 instanceof JTextArea)) || ((localObject2 instanceof JEditorPane))) {
            k = 1;
          }
          j ^= localComponentWrapper.getLayoutHashCode();
          j += 285134905;
        }
        if (k != 0) {
          resetLastInvalidOnParent(paramContainer);
        }
        if (j != lastHash)
        {
          grid = null;
          lastHash = j;
        }
        localObject1 = paramContainer.getSize();
        if ((lastInvalidSize == null) || (!lastInvalidSize.equals(localObject1)))
        {
          if (grid != null) {
            grid.invalidateContainerSize();
          }
          lastInvalidSize = ((Dimension)localObject1);
        }
      }
    }
    else {
      lastWasInvalid = false;
    }
    ContainerWrapper localContainerWrapper = checkParent(paramContainer);
    setDebug(localContainerWrapper, getDebugMillis() > 0);
    if (grid == null) {
      grid = new Grid(localContainerWrapper, lc, rowSpecs, colSpecs, ccMap, callbackList);
    }
    dirty = false;
  }
  
  private void resetLastInvalidOnParent(Container paramContainer)
  {
    while (paramContainer != null)
    {
      LayoutManager localLayoutManager = paramContainer.getLayout();
      if ((localLayoutManager instanceof MigLayout)) {
        lastWasInvalid = false;
      }
      paramContainer = paramContainer.getParent();
    }
  }
  
  private ContainerWrapper checkParent(Container paramContainer)
  {
    if (paramContainer == null) {
      return null;
    }
    if ((cacheParentW == null) || (cacheParentW.getComponent() != paramContainer)) {
      cacheParentW = new SwingContainerWrapper(paramContainer);
    }
    return cacheParentW;
  }
  
  public void layoutContainer(Container paramContainer)
  {
    synchronized (paramContainer.getTreeLock())
    {
      checkCache(paramContainer);
      Insets localInsets = paramContainer.getInsets();
      int[] arrayOfInt = { left, top, paramContainer.getWidth() - left - right, paramContainer.getHeight() - top - bottom };
      if (grid.layout(arrayOfInt, lc.getAlignX(), lc.getAlignY(), getDebug(), true))
      {
        grid = null;
        checkCache(paramContainer);
        grid.layout(arrayOfInt, lc.getAlignX(), lc.getAlignY(), getDebug(), false);
      }
      long l = grid.getHeight()[1] + (grid.getWidth()[1] << 32);
      if (lastSize != l)
      {
        lastSize = l;
        final ContainerWrapper localContainerWrapper = checkParent(paramContainer);
        Window localWindow = (Window)SwingUtilities.getAncestorOfClass(Window.class, (Component)localContainerWrapper.getComponent());
        if (localWindow != null) {
          if (localWindow.isVisible()) {
            SwingUtilities.invokeLater(new Runnable()
            {
              public void run()
              {
                MigLayout.this.adjustWindowSize(localContainerWrapper);
              }
            });
          } else {
            adjustWindowSize(localContainerWrapper);
          }
        }
      }
      lastInvalidSize = null;
    }
  }
  
  private void adjustWindowSize(ContainerWrapper paramContainerWrapper)
  {
    BoundSize localBoundSize1 = lc.getPackWidth();
    BoundSize localBoundSize2 = lc.getPackHeight();
    if ((localBoundSize1 == null) && (localBoundSize2 == null)) {
      return;
    }
    Window localWindow = (Window)SwingUtilities.getAncestorOfClass(Window.class, (Component)paramContainerWrapper.getComponent());
    if (localWindow == null) {
      return;
    }
    Dimension localDimension = localWindow.getPreferredSize();
    int i = constrain(checkParent(localWindow), localWindow.getWidth(), width, localBoundSize1);
    int j = constrain(checkParent(localWindow), localWindow.getHeight(), height, localBoundSize2);
    int k = Math.round(localWindow.getX() - (i - localWindow.getWidth()) * (1.0F - lc.getPackWidthAlign()));
    int m = Math.round(localWindow.getY() - (j - localWindow.getHeight()) * (1.0F - lc.getPackHeightAlign()));
    localWindow.setBounds(k, m, i, j);
  }
  
  private int constrain(ContainerWrapper paramContainerWrapper, int paramInt1, int paramInt2, BoundSize paramBoundSize)
  {
    if (paramBoundSize == null) {
      return paramInt1;
    }
    int i = paramInt1;
    UnitValue localUnitValue = paramBoundSize.getPreferred();
    if (localUnitValue != null) {
      i = localUnitValue.getPixels(paramInt2, paramContainerWrapper, paramContainerWrapper);
    }
    i = paramBoundSize.constrain(i, paramInt2, paramContainerWrapper);
    return paramBoundSize.getGapPush() ? Math.max(paramInt1, i) : i;
  }
  
  public Dimension minimumLayoutSize(Container paramContainer)
  {
    synchronized (paramContainer.getTreeLock())
    {
      return getSizeImpl(paramContainer, 0);
    }
  }
  
  public Dimension preferredLayoutSize(Container paramContainer)
  {
    synchronized (paramContainer.getTreeLock())
    {
      if ((lastParentSize == null) || (!paramContainer.getSize().equals(lastParentSize)))
      {
        Iterator localIterator = ccMap.keySet().iterator();
        while (localIterator.hasNext())
        {
          ComponentWrapper localComponentWrapper = (ComponentWrapper)localIterator.next();
          Component localComponent = (Component)localComponentWrapper.getComponent();
          if (((localComponent instanceof JTextArea)) || ((localComponent instanceof JEditorPane)) || (((localComponent instanceof JComponent)) && (Boolean.TRUE.equals(((JComponent)localComponent).getClientProperty("migLayout.dynamicAspectRatio")))))
          {
            layoutContainer(paramContainer);
            break;
          }
        }
      }
      lastParentSize = paramContainer.getSize();
      return getSizeImpl(paramContainer, 1);
    }
  }
  
  public Dimension maximumLayoutSize(Container paramContainer)
  {
    return new Dimension(32767, 32767);
  }
  
  private Dimension getSizeImpl(Container paramContainer, int paramInt)
  {
    checkCache(paramContainer);
    Insets localInsets = paramContainer.getInsets();
    int i = LayoutUtil.getSizeSafe(grid != null ? grid.getWidth() : null, paramInt) + left + right;
    int j = LayoutUtil.getSizeSafe(grid != null ? grid.getHeight() : null, paramInt) + top + bottom;
    return new Dimension(i, j);
  }
  
  public float getLayoutAlignmentX(Container paramContainer)
  {
    return (lc != null) && (lc.getAlignX() != null) ? lc.getAlignX().getPixels(1.0F, checkParent(paramContainer), null) : 0.0F;
  }
  
  public float getLayoutAlignmentY(Container paramContainer)
  {
    return (lc != null) && (lc.getAlignY() != null) ? lc.getAlignY().getPixels(1.0F, checkParent(paramContainer), null) : 0.0F;
  }
  
  public void addLayoutComponent(String paramString, Component paramComponent)
  {
    addLayoutComponent(paramComponent, paramString);
  }
  
  public void addLayoutComponent(Component paramComponent, Object paramObject)
  {
    synchronized (paramComponent.getParent().getTreeLock())
    {
      setComponentConstraintsImpl(paramComponent, paramObject, true);
    }
  }
  
  public void removeLayoutComponent(Component paramComponent)
  {
    synchronized (paramComponent.getParent().getTreeLock())
    {
      scrConstrMap.remove(paramComponent);
      ccMap.remove(new SwingComponentWrapper(paramComponent));
    }
  }
  
  public void invalidateLayout(Container paramContainer)
  {
    dirty = true;
  }
  
  private Object readResolve()
    throws ObjectStreamException
  {
    return LayoutUtil.getSerializedObject(this);
  }
  
  public void readExternal(ObjectInput paramObjectInput)
    throws IOException, ClassNotFoundException
  {
    LayoutUtil.setSerializedObject(this, LayoutUtil.readAsXML(paramObjectInput));
  }
  
  public void writeExternal(ObjectOutput paramObjectOutput)
    throws IOException
  {
    if (getClass() == MigLayout.class) {
      LayoutUtil.writeAsXML(paramObjectOutput, this);
    }
  }
  
  private class MyDebugRepaintListener
    implements ActionListener
  {
    private MyDebugRepaintListener() {}
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (grid != null)
      {
        Component localComponent = (Component)grid.getContainer().getComponent();
        if (localComponent.isShowing())
        {
          grid.paintDebug();
          return;
        }
      }
      debugTimer.stop();
      debugTimer = null;
    }
  }
}
