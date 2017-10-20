package net.miginfocom.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class ConstraintParser
{
  private ConstraintParser() {}
  
  public static LC parseLayoutConstraint(String paramString)
  {
    LC localLC = new LC();
    if (paramString.length() == 0) {
      return localLC;
    }
    String[] arrayOfString1 = toTrimmedTokens(paramString, ',');
    int k;
    for (int i = 0; i < arrayOfString1.length; i++)
    {
      String str1 = arrayOfString1[i];
      if (str1 != null)
      {
        k = str1.length();
        if ((k == 3) || (k == 11))
        {
          if ((str1.equals("ltr")) || (str1.equals("rtl")) || (str1.equals("lefttoright")) || (str1.equals("righttoleft")))
          {
            localLC.setLeftToRight(str1.charAt(0) == 'l' ? Boolean.TRUE : Boolean.FALSE);
            arrayOfString1[i] = null;
          }
          if ((str1.equals("ttb")) || (str1.equals("btt")) || (str1.equals("toptobottom")) || (str1.equals("bottomtotop")))
          {
            localLC.setTopToBottom(str1.charAt(0) == 't');
            arrayOfString1[i] = null;
          }
        }
      }
    }
    for (String str2 : arrayOfString1) {
      if ((str2 != null) && (str2.length() != 0)) {
        try
        {
          int m = -1;
          int n = str2.charAt(0);
          Object localObject2;
          if ((n == 119) || (n == 104))
          {
            m = startsWithLenient(str2, "wrap", -1, true);
            if (m > -1)
            {
              String str3 = str2.substring(m).trim();
              localLC.setWrapAfter(str3.length() != 0 ? Integer.parseInt(str3) : 0);
              continue;
            }
            int i1 = n == 119 ? 1 : 0;
            if ((i1 != 0) && ((str2.startsWith("w ")) || (str2.startsWith("width "))))
            {
              localObject2 = str2.substring(str2.charAt(1) == ' ' ? 2 : 6).trim();
              localLC.setWidth(parseBoundSize((String)localObject2, false, true));
              continue;
            }
            if ((i1 == 0) && ((str2.startsWith("h ")) || (str2.startsWith("height "))))
            {
              localObject2 = str2.substring(str2.charAt(1) == ' ' ? 2 : 7).trim();
              localLC.setHeight(parseBoundSize((String)localObject2, false, false));
              continue;
            }
            if (str2.length() > 5)
            {
              localObject2 = str2.substring(5).trim();
              if (str2.startsWith("wmin "))
              {
                localLC.minWidth((String)localObject2);
                continue;
              }
              if (str2.startsWith("wmax "))
              {
                localLC.maxWidth((String)localObject2);
                continue;
              }
              if (str2.startsWith("hmin "))
              {
                localLC.minHeight((String)localObject2);
                continue;
              }
              if (str2.startsWith("hmax "))
              {
                localLC.maxHeight((String)localObject2);
                continue;
              }
            }
            if (str2.startsWith("hidemode "))
            {
              localLC.setHideMode(Integer.parseInt(str2.substring(9)));
              continue;
            }
          }
          if (n == 103)
          {
            if (str2.startsWith("gapx "))
            {
              localLC.setGridGapX(parseBoundSize(str2.substring(5).trim(), true, true));
              continue;
            }
            if (str2.startsWith("gapy "))
            {
              localLC.setGridGapY(parseBoundSize(str2.substring(5).trim(), true, false));
              continue;
            }
            if (str2.startsWith("gap "))
            {
              localObject1 = toTrimmedTokens(str2.substring(4).trim(), ' ');
              localLC.setGridGapX(parseBoundSize(localObject1[0], true, true));
              localLC.setGridGapY(localObject1.length > 1 ? parseBoundSize(localObject1[1], true, false) : localLC.getGridGapX());
              continue;
            }
          }
          if (n == 100)
          {
            m = startsWithLenient(str2, "debug", 5, true);
            if (m > -1)
            {
              localObject1 = str2.substring(m).trim();
              localLC.setDebugMillis(((String)localObject1).length() > 0 ? Integer.parseInt((String)localObject1) : 1000);
              continue;
            }
          }
          if (n == 110)
          {
            if (str2.equals("nogrid"))
            {
              localLC.setNoGrid(true);
              continue;
            }
            if (str2.equals("nocache"))
            {
              localLC.setNoCache(true);
              continue;
            }
            if (str2.equals("novisualpadding"))
            {
              localLC.setVisualPadding(false);
              continue;
            }
          }
          if (n == 102)
          {
            if ((str2.equals("fill")) || (str2.equals("fillx")) || (str2.equals("filly")))
            {
              localLC.setFillX((str2.length() == 4) || (str2.charAt(4) == 'x'));
              localLC.setFillY((str2.length() == 4) || (str2.charAt(4) == 'y'));
              continue;
            }
            if (str2.equals("flowy"))
            {
              localLC.setFlowX(false);
              continue;
            }
            if (str2.equals("flowx"))
            {
              localLC.setFlowX(true);
              continue;
            }
          }
          if (n == 105)
          {
            m = startsWithLenient(str2, "insets", 3, true);
            if (m > -1)
            {
              localObject1 = str2.substring(m).trim();
              localObject2 = parseInsets((String)localObject1, true);
              LayoutUtil.putCCString(localObject2, (String)localObject1);
              localLC.setInsets((UnitValue[])localObject2);
              continue;
            }
          }
          if (n == 97)
          {
            m = startsWithLenient(str2, new String[] { "aligny", "ay" }, new int[] { 6, 2 }, true);
            if (m > -1)
            {
              localObject1 = parseUnitValueOrAlign(str2.substring(m).trim(), false, null);
              if (localObject1 == UnitValue.BASELINE_IDENTITY) {
                throw new IllegalArgumentException("'baseline' can not be used to align the whole component group.");
              }
              localLC.setAlignY((UnitValue)localObject1);
              continue;
            }
            m = startsWithLenient(str2, new String[] { "alignx", "ax" }, new int[] { 6, 2 }, true);
            if (m > -1)
            {
              localLC.setAlignX(parseUnitValueOrAlign(str2.substring(m).trim(), true, null));
              continue;
            }
            m = startsWithLenient(str2, "align", 2, true);
            if (m > -1)
            {
              localObject1 = toTrimmedTokens(str2.substring(m).trim(), ' ');
              localLC.setAlignX(parseUnitValueOrAlign(localObject1[0], true, null));
              if (localObject1.length > 1) {
                localLC.setAlignY(parseUnitValueOrAlign(localObject1[1], false, null));
              }
              continue;
            }
          }
          if (n == 112)
          {
            if (str2.startsWith("packalign "))
            {
              localObject1 = toTrimmedTokens(str2.substring(10).trim(), ' ');
              localLC.setPackWidthAlign(localObject1[0].length() > 0 ? Float.parseFloat(localObject1[0]) : 0.5F);
              if (localObject1.length > 1) {
                localLC.setPackHeightAlign(Float.parseFloat(localObject1[1]));
              }
              continue;
            }
            if ((str2.startsWith("pack ")) || (str2.equals("pack")))
            {
              localObject1 = str2.substring(4).trim();
              localObject2 = toTrimmedTokens(((String)localObject1).length() > 0 ? localObject1 : "pref pref", ' ');
              localLC.setPackWidth(parseBoundSize(localObject2[0], false, true));
              if (localObject2.length > 1) {
                localLC.setPackHeight(parseBoundSize(localObject2[1], false, false));
              }
              continue;
            }
          }
          if (localLC.getAlignX() == null)
          {
            localObject1 = parseAlignKeywords(str2, true);
            if (localObject1 != null)
            {
              localLC.setAlignX((UnitValue)localObject1);
              continue;
            }
          }
          Object localObject1 = parseAlignKeywords(str2, false);
          if (localObject1 != null) {
            localLC.setAlignY((UnitValue)localObject1);
          } else {
            throw new IllegalArgumentException("Unknown Constraint: '" + str2 + "'\n");
          }
        }
        catch (Exception localException)
        {
          throw new IllegalArgumentException("Illegal Constraint: '" + str2 + "'\n" + localException.getMessage());
        }
      }
    }
    return localLC;
  }
  
  public static AC parseRowConstraints(String paramString)
  {
    return parseAxisConstraint(paramString, false);
  }
  
  public static AC parseColumnConstraints(String paramString)
  {
    return parseAxisConstraint(paramString, true);
  }
  
  private static AC parseAxisConstraint(String paramString, boolean paramBoolean)
  {
    paramString = paramString.trim();
    if (paramString.length() == 0) {
      return new AC();
    }
    paramString = paramString.toLowerCase();
    ArrayList localArrayList = getRowColAndGapsTrimmed(paramString);
    BoundSize[] arrayOfBoundSize = new BoundSize[(localArrayList.size() >> 1) + 1];
    int i = 0;
    int j = localArrayList.size();
    for (int k = 0; i < j; k++)
    {
      arrayOfBoundSize[k] = parseBoundSize((String)localArrayList.get(i), true, paramBoolean);
      i += 2;
    }
    DimConstraint[] arrayOfDimConstraint = new DimConstraint[localArrayList.size() >> 1];
    j = 0;
    for (k = 0; j < arrayOfDimConstraint.length; k++)
    {
      if (k >= arrayOfBoundSize.length - 1) {
        k = arrayOfBoundSize.length - 2;
      }
      arrayOfDimConstraint[j] = parseDimConstraint((String)localArrayList.get((j << 1) + 1), arrayOfBoundSize[k], arrayOfBoundSize[(k + 1)], paramBoolean);
      j++;
    }
    AC localAC = new AC();
    localAC.setConstaints(arrayOfDimConstraint);
    return localAC;
  }
  
  private static DimConstraint parseDimConstraint(String paramString, BoundSize paramBoundSize1, BoundSize paramBoundSize2, boolean paramBoolean)
  {
    DimConstraint localDimConstraint = new DimConstraint();
    localDimConstraint.setGapBefore(paramBoundSize1);
    localDimConstraint.setGapAfter(paramBoundSize2);
    String[] arrayOfString = toTrimmedTokens(paramString, ',');
    for (int i = 0; i < arrayOfString.length; i++)
    {
      String str = arrayOfString[i];
      try
      {
        if (str.length() != 0) {
          if (str.equals("fill"))
          {
            localDimConstraint.setFill(true);
          }
          else if (str.equals("nogrid"))
          {
            localDimConstraint.setNoGrid(true);
          }
          else
          {
            int j = -1;
            int k = str.charAt(0);
            if (k == 115)
            {
              j = startsWithLenient(str, new String[] { "sizegroup", "sg" }, new int[] { 5, 2 }, true);
              if (j > -1)
              {
                localDimConstraint.setSizeGroup(str.substring(j).trim());
                continue;
              }
              j = startsWithLenient(str, new String[] { "shrinkprio", "shp" }, new int[] { 10, 3 }, true);
              if (j > -1)
              {
                localDimConstraint.setShrinkPriority(Integer.parseInt(str.substring(j).trim()));
                continue;
              }
              j = startsWithLenient(str, "shrink", 6, true);
              if (j > -1)
              {
                localDimConstraint.setShrink(parseFloat(str.substring(j).trim(), ResizeConstraint.WEIGHT_100));
                continue;
              }
            }
            if (k == 103)
            {
              j = startsWithLenient(str, new String[] { "growpriority", "gp" }, new int[] { 5, 2 }, true);
              if (j > -1)
              {
                localDimConstraint.setGrowPriority(Integer.parseInt(str.substring(j).trim()));
                continue;
              }
              j = startsWithLenient(str, "grow", 4, true);
              if (j > -1)
              {
                localDimConstraint.setGrow(parseFloat(str.substring(j).trim(), ResizeConstraint.WEIGHT_100));
                continue;
              }
            }
            if (k == 97)
            {
              j = startsWithLenient(str, "align", 2, true);
              if (j > -1)
              {
                localDimConstraint.setAlign(parseUnitValueOrAlign(str.substring(j).trim(), paramBoolean, null));
                continue;
              }
            }
            UnitValue localUnitValue = parseAlignKeywords(str, paramBoolean);
            if (localUnitValue != null) {
              localDimConstraint.setAlign(localUnitValue);
            } else {
              localDimConstraint.setSize(parseBoundSize(str, false, paramBoolean));
            }
          }
        }
      }
      catch (Exception localException)
      {
        throw new IllegalArgumentException("Illegal contraint: '" + str + "'\n" + localException.getMessage());
      }
    }
    return localDimConstraint;
  }
  
  public static Map<ComponentWrapper, CC> parseComponentConstraints(Map<ComponentWrapper, String> paramMap)
  {
    HashMap localHashMap = new HashMap();
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      localHashMap.put(localEntry.getKey(), parseComponentConstraint((String)localEntry.getValue()));
    }
    return localHashMap;
  }
  
  public static CC parseComponentConstraint(String paramString)
  {
    CC localCC = new CC();
    if (paramString.length() == 0) {
      return localCC;
    }
    String[] arrayOfString1 = toTrimmedTokens(paramString, ',');
    for (String str : arrayOfString1) {
      try
      {
        if (str.length() != 0)
        {
          int k = -1;
          int m = str.charAt(0);
          Object localObject1;
          if (m == 110)
          {
            if (str.equals("north"))
            {
              localCC.setDockSide(0);
              continue;
            }
            if (str.equals("newline"))
            {
              localCC.setNewline(true);
              continue;
            }
            if (str.startsWith("newline "))
            {
              localObject1 = str.substring(7).trim();
              localCC.setNewlineGapSize(parseBoundSize((String)localObject1, true, true));
              continue;
            }
          }
          if ((m == 102) && ((str.equals("flowy")) || (str.equals("flowx"))))
          {
            localCC.setFlowX(str.charAt(4) == 'x' ? Boolean.TRUE : Boolean.FALSE);
          }
          else
          {
            int i2;
            if (m == 115)
            {
              k = startsWithLenient(str, "skip", 4, true);
              if (k > -1)
              {
                localObject1 = str.substring(k).trim();
                localCC.setSkip(((String)localObject1).length() != 0 ? Integer.parseInt((String)localObject1) : 1);
                continue;
              }
              k = startsWithLenient(str, "split", 5, true);
              if (k > -1)
              {
                localObject1 = str.substring(k).trim();
                localCC.setSplit(((String)localObject1).length() > 0 ? Integer.parseInt((String)localObject1) : 2097051);
                continue;
              }
              if (str.equals("south"))
              {
                localCC.setDockSide(2);
                continue;
              }
              k = startsWithLenient(str, new String[] { "spany", "sy" }, new int[] { 5, 2 }, true);
              if (k > -1)
              {
                localCC.setSpanY(parseSpan(str.substring(k).trim()));
                continue;
              }
              k = startsWithLenient(str, new String[] { "spanx", "sx" }, new int[] { 5, 2 }, true);
              if (k > -1)
              {
                localCC.setSpanX(parseSpan(str.substring(k).trim()));
                continue;
              }
              k = startsWithLenient(str, "span", 4, true);
              if (k > -1)
              {
                localObject1 = toTrimmedTokens(str.substring(k).trim(), ' ');
                localCC.setSpanX(localObject1[0].length() > 0 ? Integer.parseInt(localObject1[0]) : 2097051);
                localCC.setSpanY(localObject1.length > 1 ? Integer.parseInt(localObject1[1]) : 1);
                continue;
              }
              k = startsWithLenient(str, "shrinkx", 7, true);
              if (k > -1)
              {
                localCC.getHorizontal().setShrink(parseFloat(str.substring(k).trim(), ResizeConstraint.WEIGHT_100));
                continue;
              }
              k = startsWithLenient(str, "shrinky", 7, true);
              if (k > -1)
              {
                localCC.getVertical().setShrink(parseFloat(str.substring(k).trim(), ResizeConstraint.WEIGHT_100));
                continue;
              }
              k = startsWithLenient(str, "shrink", 6, false);
              if (k > -1)
              {
                localObject1 = toTrimmedTokens(str.substring(k).trim(), ' ');
                localCC.getHorizontal().setShrink(parseFloat(str.substring(k).trim(), ResizeConstraint.WEIGHT_100));
                if (localObject1.length > 1) {
                  localCC.getVertical().setShrink(parseFloat(str.substring(k).trim(), ResizeConstraint.WEIGHT_100));
                }
                continue;
              }
              k = startsWithLenient(str, new String[] { "shrinkprio", "shp" }, new int[] { 10, 3 }, true);
              if (k > -1)
              {
                localObject1 = str.substring(k).trim();
                if ((((String)localObject1).startsWith("x")) || (((String)localObject1).startsWith("y")))
                {
                  (((String)localObject1).startsWith("x") ? localCC.getHorizontal() : localCC.getVertical()).setShrinkPriority(Integer.parseInt(((String)localObject1).substring(2)));
                }
                else
                {
                  String[] arrayOfString3 = toTrimmedTokens((String)localObject1, ' ');
                  localCC.getHorizontal().setShrinkPriority(Integer.parseInt(arrayOfString3[0]));
                  if (arrayOfString3.length > 1) {
                    localCC.getVertical().setShrinkPriority(Integer.parseInt(arrayOfString3[1]));
                  }
                }
                continue;
              }
              k = startsWithLenient(str, new String[] { "sizegroupx", "sizegroupy", "sgx", "sgy" }, new int[] { 9, 9, 2, 2 }, true);
              if (k > -1)
              {
                localObject1 = str.substring(k).trim();
                i2 = str.charAt(k - 1);
                if (i2 != 121) {
                  localCC.getHorizontal().setSizeGroup((String)localObject1);
                }
                if (i2 != 120) {
                  localCC.getVertical().setSizeGroup((String)localObject1);
                }
                continue;
              }
            }
            Object localObject5;
            if (m == 103)
            {
              k = startsWithLenient(str, "growx", 5, true);
              if (k > -1)
              {
                localCC.getHorizontal().setGrow(parseFloat(str.substring(k).trim(), ResizeConstraint.WEIGHT_100));
                continue;
              }
              k = startsWithLenient(str, "growy", 5, true);
              if (k > -1)
              {
                localCC.getVertical().setGrow(parseFloat(str.substring(k).trim(), ResizeConstraint.WEIGHT_100));
                continue;
              }
              k = startsWithLenient(str, "grow", 4, false);
              if (k > -1)
              {
                localObject1 = toTrimmedTokens(str.substring(k).trim(), ' ');
                localCC.getHorizontal().setGrow(parseFloat(localObject1[0], ResizeConstraint.WEIGHT_100));
                localCC.getVertical().setGrow(parseFloat(localObject1.length > 1 ? localObject1[1] : "", ResizeConstraint.WEIGHT_100));
                continue;
              }
              k = startsWithLenient(str, new String[] { "growprio", "gp" }, new int[] { 8, 2 }, true);
              if (k > -1)
              {
                localObject1 = str.substring(k).trim();
                i2 = ((String)localObject1).length() > 0 ? ((String)localObject1).charAt(0) : 32;
                if ((i2 == 120) || (i2 == 121))
                {
                  (i2 == 120 ? localCC.getHorizontal() : localCC.getVertical()).setGrowPriority(Integer.parseInt(((String)localObject1).substring(2)));
                }
                else
                {
                  localObject5 = toTrimmedTokens((String)localObject1, ' ');
                  localCC.getHorizontal().setGrowPriority(Integer.parseInt(localObject5[0]));
                  if (localObject5.length > 1) {
                    localCC.getVertical().setGrowPriority(Integer.parseInt(localObject5[1]));
                  }
                }
                continue;
              }
              if (str.startsWith("gap"))
              {
                localObject1 = parseGaps(str);
                if (localObject1[0] != null) {
                  localCC.getVertical().setGapBefore(localObject1[0]);
                }
                if (localObject1[1] != null) {
                  localCC.getHorizontal().setGapBefore(localObject1[1]);
                }
                if (localObject1[2] != null) {
                  localCC.getVertical().setGapAfter(localObject1[2]);
                }
                if (localObject1[3] != null) {
                  localCC.getHorizontal().setGapAfter(localObject1[3]);
                }
                continue;
              }
            }
            if (m == 97)
            {
              k = startsWithLenient(str, new String[] { "aligny", "ay" }, new int[] { 6, 2 }, true);
              if (k > -1)
              {
                localCC.getVertical().setAlign(parseUnitValueOrAlign(str.substring(k).trim(), false, null));
                continue;
              }
              k = startsWithLenient(str, new String[] { "alignx", "ax" }, new int[] { 6, 2 }, true);
              if (k > -1)
              {
                localCC.getHorizontal().setAlign(parseUnitValueOrAlign(str.substring(k).trim(), true, null));
                continue;
              }
              k = startsWithLenient(str, "align", 2, true);
              if (k > -1)
              {
                localObject1 = toTrimmedTokens(str.substring(k).trim(), ' ');
                localCC.getHorizontal().setAlign(parseUnitValueOrAlign(localObject1[0], true, null));
                if (localObject1.length > 1) {
                  localCC.getVertical().setAlign(parseUnitValueOrAlign(localObject1[1], false, null));
                }
                continue;
              }
            }
            if (((m == 120) || (m == 121)) && (str.length() > 2))
            {
              int n = str.charAt(1);
              if ((n == 32) || ((n == 50) && (str.charAt(2) == ' ')))
              {
                if (localCC.getPos() == null) {
                  localCC.setPos(new UnitValue[4]);
                } else if (!localCC.isBoundsInGrid()) {
                  throw new IllegalArgumentException("Cannot combine 'position' with 'x/y/x2/y2' keywords.");
                }
                i2 = (m == 120 ? 0 : 1) + (n == 50 ? 2 : 0);
                localObject5 = localCC.getPos();
                localObject5[i2] = parseUnitValue(str.substring(2).trim(), null, m == 120 ? 1 : false);
                localCC.setPos((UnitValue[])localObject5);
                localCC.setBoundsInGrid(true);
                continue;
              }
            }
            Object localObject2;
            if (m == 99)
            {
              k = startsWithLenient(str, "cell", 4, true);
              if (k > -1)
              {
                localObject2 = toTrimmedTokens(str.substring(k).trim(), ' ');
                if (localObject2.length < 2) {
                  throw new IllegalArgumentException("At least two integers must follow " + str);
                }
                localCC.setCellX(Integer.parseInt(localObject2[0]));
                localCC.setCellY(Integer.parseInt(localObject2[1]));
                if (localObject2.length > 2) {
                  localCC.setSpanX(Integer.parseInt(localObject2[2]));
                }
                if (localObject2.length > 3) {
                  localCC.setSpanY(Integer.parseInt(localObject2[3]));
                }
                continue;
              }
            }
            Object localObject4;
            if (m == 112)
            {
              k = startsWithLenient(str, "pos", 3, true);
              if (k > -1)
              {
                if ((localCC.getPos() != null) && (localCC.isBoundsInGrid())) {
                  throw new IllegalArgumentException("Can not combine 'pos' with 'x/y/x2/y2' keywords.");
                }
                localObject2 = toTrimmedTokens(str.substring(k).trim(), ' ');
                localObject4 = new UnitValue[4];
                for (int i4 = 0; i4 < localObject2.length; i4++) {
                  localObject4[i4] = parseUnitValue(localObject2[i4], null, i4 % 2 == 0 ? 1 : false);
                }
                if (((localObject4[0] == null) && (localObject4[2] == null)) || ((localObject4[1] == null) && (localObject4[3] == null))) {
                  throw new IllegalArgumentException("Both x and x2 or y and y2 can not be null!");
                }
                localCC.setPos((UnitValue[])localObject4);
                localCC.setBoundsInGrid(false);
                continue;
              }
              k = startsWithLenient(str, "pad", 3, true);
              if (k > -1)
              {
                localObject2 = parseInsets(str.substring(k).trim(), false);
                localCC.setPadding(new UnitValue[] { localObject2[0], localObject2.length > 1 ? localObject2[1] : null, localObject2.length > 2 ? localObject2[2] : null, localObject2.length > 3 ? localObject2[3] : null });
                continue;
              }
              k = startsWithLenient(str, "pushx", 5, true);
              if (k > -1)
              {
                localCC.setPushX(parseFloat(str.substring(k).trim(), ResizeConstraint.WEIGHT_100));
                continue;
              }
              k = startsWithLenient(str, "pushy", 5, true);
              if (k > -1)
              {
                localCC.setPushY(parseFloat(str.substring(k).trim(), ResizeConstraint.WEIGHT_100));
                continue;
              }
              k = startsWithLenient(str, "push", 4, false);
              if (k > -1)
              {
                localObject2 = toTrimmedTokens(str.substring(k).trim(), ' ');
                localCC.setPushX(parseFloat(localObject2[0], ResizeConstraint.WEIGHT_100));
                localCC.setPushY(parseFloat(localObject2.length > 1 ? localObject2[1] : "", ResizeConstraint.WEIGHT_100));
                continue;
              }
            }
            if (m == 116)
            {
              k = startsWithLenient(str, "tag", 3, true);
              if (k > -1)
              {
                localCC.setTag(str.substring(k).trim());
                continue;
              }
            }
            Object localObject6;
            if ((m == 119) || (m == 104))
            {
              if (str.equals("wrap"))
              {
                localCC.setWrap(true);
                continue;
              }
              if (str.startsWith("wrap "))
              {
                localObject2 = str.substring(5).trim();
                localCC.setWrapGapSize(parseBoundSize((String)localObject2, true, true));
                continue;
              }
              boolean bool = m == 119;
              if ((bool) && ((str.startsWith("w ")) || (str.startsWith("width "))))
              {
                localObject4 = str.substring(str.charAt(1) == ' ' ? 2 : 6).trim();
                localCC.getHorizontal().setSize(parseBoundSize((String)localObject4, false, true));
                continue;
              }
              if ((!bool) && ((str.startsWith("h ")) || (str.startsWith("height "))))
              {
                localObject4 = str.substring(str.charAt(1) == ' ' ? 2 : 7).trim();
                localCC.getVertical().setSize(parseBoundSize((String)localObject4, false, false));
                continue;
              }
              if ((str.startsWith("wmin ")) || (str.startsWith("wmax ")) || (str.startsWith("hmin ")) || (str.startsWith("hmax ")))
              {
                localObject4 = str.substring(5).trim();
                if (((String)localObject4).length() > 0)
                {
                  localObject6 = parseUnitValue((String)localObject4, null, bool);
                  int i5 = str.charAt(3) == 'n' ? 1 : 0;
                  DimConstraint localDimConstraint = bool ? localCC.getHorizontal() : localCC.getVertical();
                  localDimConstraint.setSize(new BoundSize(i5 != 0 ? localObject6 : localDimConstraint.getSize().getMin(), localDimConstraint.getSize().getPreferred(), i5 != 0 ? localDimConstraint.getSize().getMax() : (UnitValue)localObject6, (String)localObject4));
                  continue;
                }
              }
              if (str.equals("west"))
              {
                localCC.setDockSide(1);
                continue;
              }
              if (str.startsWith("hidemode "))
              {
                localCC.setHideMode(Integer.parseInt(str.substring(9)));
                continue;
              }
            }
            if ((m == 105) && (str.startsWith("id ")))
            {
              localCC.setId(str.substring(3).trim());
              int i1 = localCC.getId().indexOf('.');
              if ((i1 == 0) || (i1 == localCC.getId().length() - 1)) {
                throw new IllegalArgumentException("Dot must not be first or last!");
              }
            }
            else
            {
              if (m == 101)
              {
                if (str.equals("east"))
                {
                  localCC.setDockSide(3);
                  continue;
                }
                if (str.equals("external"))
                {
                  localCC.setExternal(true);
                  continue;
                }
                k = startsWithLenient(str, new String[] { "endgroupx", "endgroupy", "egx", "egy" }, new int[] { -1, -1, -1, -1 }, true);
                if (k > -1)
                {
                  localObject3 = str.substring(k).trim();
                  int i3 = str.charAt(k - 1);
                  localObject6 = i3 == 120 ? localCC.getHorizontal() : localCC.getVertical();
                  ((DimConstraint)localObject6).setEndGroup((String)localObject3);
                  continue;
                }
              }
              if (m == 100)
              {
                if (str.equals("dock north"))
                {
                  localCC.setDockSide(0);
                  continue;
                }
                if (str.equals("dock west"))
                {
                  localCC.setDockSide(1);
                  continue;
                }
                if (str.equals("dock south"))
                {
                  localCC.setDockSide(2);
                  continue;
                }
                if (str.equals("dock east"))
                {
                  localCC.setDockSide(3);
                  continue;
                }
                if (str.equals("dock center"))
                {
                  localCC.getHorizontal().setGrow(Float.valueOf(100.0F));
                  localCC.getVertical().setGrow(Float.valueOf(100.0F));
                  localCC.setPushX(Float.valueOf(100.0F));
                  localCC.setPushY(Float.valueOf(100.0F));
                  continue;
                }
              }
              Object localObject3 = parseAlignKeywords(str, true);
              if (localObject3 != null)
              {
                localCC.getHorizontal().setAlign((UnitValue)localObject3);
              }
              else
              {
                UnitValue localUnitValue = parseAlignKeywords(str, false);
                if (localUnitValue != null) {
                  localCC.getVertical().setAlign(localUnitValue);
                } else {
                  throw new IllegalArgumentException("Unknown keyword.");
                }
              }
            }
          }
        }
      }
      catch (Exception localException)
      {
        throw new IllegalArgumentException("Illegal Constraint: '" + str + "'\n" + localException.getMessage());
      }
    }
    return localCC;
  }
  
  public static UnitValue[] parseInsets(String paramString, boolean paramBoolean)
  {
    if ((paramString.length() == 0) || (paramString.equals("dialog")) || (paramString.equals("panel")))
    {
      if (!paramBoolean) {
        throw new IllegalAccessError("Insets now allowed: " + paramString + "\n");
      }
      boolean bool = paramString.startsWith("p");
      arrayOfUnitValue = new UnitValue[4];
      for (i = 0; i < 4; i++) {
        arrayOfUnitValue[i] = (bool ? PlatformDefaults.getPanelInsets(i) : PlatformDefaults.getDialogInsets(i));
      }
      return arrayOfUnitValue;
    }
    String[] arrayOfString = toTrimmedTokens(paramString, ' ');
    UnitValue[] arrayOfUnitValue = new UnitValue[4];
    for (int i = 0; i < 4; i++)
    {
      UnitValue localUnitValue = parseUnitValue(arrayOfString[(arrayOfString.length - 1)], UnitValue.ZERO, i % 2 == 1);
      arrayOfUnitValue[i] = (localUnitValue != null ? localUnitValue : PlatformDefaults.getPanelInsets(i));
    }
    return arrayOfUnitValue;
  }
  
  private static BoundSize[] parseGaps(String paramString)
  {
    BoundSize[] arrayOfBoundSize = new BoundSize[4];
    int i = startsWithLenient(paramString, "gaptop", -1, true);
    if (i > -1)
    {
      paramString = paramString.substring(i).trim();
      arrayOfBoundSize[0] = parseBoundSize(paramString, true, false);
      return arrayOfBoundSize;
    }
    i = startsWithLenient(paramString, "gapleft", -1, true);
    if (i > -1)
    {
      paramString = paramString.substring(i).trim();
      arrayOfBoundSize[1] = parseBoundSize(paramString, true, true);
      return arrayOfBoundSize;
    }
    i = startsWithLenient(paramString, "gapbottom", -1, true);
    if (i > -1)
    {
      paramString = paramString.substring(i).trim();
      arrayOfBoundSize[2] = parseBoundSize(paramString, true, false);
      return arrayOfBoundSize;
    }
    i = startsWithLenient(paramString, "gapright", -1, true);
    if (i > -1)
    {
      paramString = paramString.substring(i).trim();
      arrayOfBoundSize[3] = parseBoundSize(paramString, true, true);
      return arrayOfBoundSize;
    }
    i = startsWithLenient(paramString, "gapbefore", -1, true);
    if (i > -1)
    {
      paramString = paramString.substring(i).trim();
      arrayOfBoundSize[1] = parseBoundSize(paramString, true, true);
      return arrayOfBoundSize;
    }
    i = startsWithLenient(paramString, "gapafter", -1, true);
    if (i > -1)
    {
      paramString = paramString.substring(i).trim();
      arrayOfBoundSize[3] = parseBoundSize(paramString, true, true);
      return arrayOfBoundSize;
    }
    i = startsWithLenient(paramString, new String[] { "gapx", "gapy" }, null, true);
    if (i > -1)
    {
      boolean bool = paramString.charAt(3) == 'x';
      String[] arrayOfString2 = toTrimmedTokens(paramString.substring(i).trim(), ' ');
      arrayOfBoundSize[(bool ? 1 : 0)] = parseBoundSize(arrayOfString2[0], true, bool);
      if (arrayOfString2.length > 1) {
        arrayOfBoundSize[(bool ? 3 : 2)] = parseBoundSize(arrayOfString2[1], true, !bool ? 1 : false);
      }
      return arrayOfBoundSize;
    }
    i = startsWithLenient(paramString, "gap ", 1, true);
    if (i > -1)
    {
      String[] arrayOfString1 = toTrimmedTokens(paramString.substring(i).trim(), ' ');
      arrayOfBoundSize[1] = parseBoundSize(arrayOfString1[0], true, true);
      if (arrayOfString1.length > 1)
      {
        arrayOfBoundSize[3] = parseBoundSize(arrayOfString1[1], true, false);
        if (arrayOfString1.length > 2)
        {
          arrayOfBoundSize[0] = parseBoundSize(arrayOfString1[2], true, true);
          if (arrayOfString1.length > 3) {
            arrayOfBoundSize[2] = parseBoundSize(arrayOfString1[3], true, false);
          }
        }
      }
      return arrayOfBoundSize;
    }
    throw new IllegalArgumentException("Unknown Gap part: '" + paramString + "'");
  }
  
  private static int parseSpan(String paramString)
  {
    return paramString.length() > 0 ? Integer.parseInt(paramString) : 2097051;
  }
  
  private static Float parseFloat(String paramString, Float paramFloat)
  {
    return paramString.length() > 0 ? new Float(Float.parseFloat(paramString)) : paramFloat;
  }
  
  public static BoundSize parseBoundSize(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramString.length() == 0) || (paramString.equals("null")) || (paramString.equals("n"))) {
      return null;
    }
    String str1 = paramString;
    boolean bool1 = false;
    if (paramString.endsWith("push"))
    {
      bool1 = true;
      int i = paramString.length();
      paramString = paramString.substring(0, i - (paramString.endsWith(":push") ? 5 : 4));
      if (paramString.length() == 0) {
        return new BoundSize(null, null, null, true, str1);
      }
    }
    String[] arrayOfString = toTrimmedTokens(paramString, ':');
    String str2 = arrayOfString[0];
    if (arrayOfString.length == 1)
    {
      boolean bool2 = str2.endsWith("!");
      if (bool2) {
        str2 = str2.substring(0, str2.length() - 1);
      }
      UnitValue localUnitValue = parseUnitValue(str2, null, paramBoolean2);
      return new BoundSize((paramBoolean1) || (bool2) ? localUnitValue : null, localUnitValue, bool2 ? localUnitValue : null, bool1, str1);
    }
    if (arrayOfString.length == 2) {
      return new BoundSize(parseUnitValue(str2, null, paramBoolean2), parseUnitValue(arrayOfString[1], null, paramBoolean2), null, bool1, str1);
    }
    if (arrayOfString.length == 3) {
      return new BoundSize(parseUnitValue(str2, null, paramBoolean2), parseUnitValue(arrayOfString[1], null, paramBoolean2), parseUnitValue(arrayOfString[2], null, paramBoolean2), bool1, str1);
    }
    throw new IllegalArgumentException("Min:Preferred:Max size section must contain 0, 1 or 2 colons. '" + str1 + "'");
  }
  
  public static UnitValue parseUnitValueOrAlign(String paramString, boolean paramBoolean, UnitValue paramUnitValue)
  {
    if (paramString.length() == 0) {
      return paramUnitValue;
    }
    UnitValue localUnitValue = parseAlignKeywords(paramString, paramBoolean);
    if (localUnitValue != null) {
      return localUnitValue;
    }
    return parseUnitValue(paramString, paramUnitValue, paramBoolean);
  }
  
  public static UnitValue parseUnitValue(String paramString, boolean paramBoolean)
  {
    return parseUnitValue(paramString, null, paramBoolean);
  }
  
  private static UnitValue parseUnitValue(String paramString, UnitValue paramUnitValue, boolean paramBoolean)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return paramUnitValue;
    }
    String str1 = paramString;
    int i = paramString.charAt(0);
    if ((i == 40) && (paramString.charAt(paramString.length() - 1) == ')')) {
      paramString = paramString.substring(1, paramString.length() - 1);
    }
    if ((i == 110) && ((paramString.equals("null")) || (paramString.equals("n")))) {
      return null;
    }
    if ((i == 105) && (paramString.equals("inf"))) {
      return UnitValue.INF;
    }
    int j = getOper(paramString);
    int k = (j == 101) || (j == 102) || (j == 103) || (j == 104) ? 1 : 0;
    String[] arrayOfString;
    if (j != 100)
    {
      if (k == 0)
      {
        String str2 = paramString.substring(4, paramString.length() - 1).trim();
        arrayOfString = toTrimmedTokens(str2, ',');
        if (arrayOfString.length == 1) {
          return parseUnitValue(str2, null, paramBoolean);
        }
      }
      else
      {
        char c;
        if (j == 101) {
          c = '+';
        } else if (j == 102) {
          c = '-';
        } else if (j == 103) {
          c = '*';
        } else {
          c = '/';
        }
        arrayOfString = toTrimmedTokens(paramString, c);
        if (arrayOfString.length > 2)
        {
          localObject = arrayOfString[(arrayOfString.length - 1)];
          String str3 = paramString.substring(0, paramString.length() - ((String)localObject).length() - 1);
          arrayOfString = new String[] { str3, localObject };
        }
      }
      if (arrayOfString.length != 2) {
        throw new IllegalArgumentException("Malformed UnitValue: '" + paramString + "'");
      }
      UnitValue localUnitValue = parseUnitValue(arrayOfString[0], null, paramBoolean);
      Object localObject = parseUnitValue(arrayOfString[1], null, paramBoolean);
      if ((localUnitValue == null) || (localObject == null)) {
        throw new IllegalArgumentException("Malformed UnitValue. Must be two sub-values: '" + paramString + "'");
      }
      return new UnitValue(paramBoolean, j, localUnitValue, (UnitValue)localObject, str1);
    }
    try
    {
      arrayOfString = getNumTextParts(paramString);
      float f = arrayOfString[0].length() > 0 ? Float.parseFloat(arrayOfString[0]) : 1.0F;
      return new UnitValue(f, arrayOfString[1], paramBoolean, j, str1);
    }
    catch (Exception localException)
    {
      throw new IllegalArgumentException("Malformed UnitValue: '" + paramString + "'");
    }
  }
  
  static UnitValue parseAlignKeywords(String paramString, boolean paramBoolean)
  {
    if (startsWithLenient(paramString, "center", 1, false) != -1) {
      return UnitValue.CENTER;
    }
    if (paramBoolean)
    {
      if (startsWithLenient(paramString, "left", 1, false) != -1) {
        return UnitValue.LEFT;
      }
      if (startsWithLenient(paramString, "right", 1, false) != -1) {
        return UnitValue.RIGHT;
      }
      if (startsWithLenient(paramString, "leading", 4, false) != -1) {
        return UnitValue.LEADING;
      }
      if (startsWithLenient(paramString, "trailing", 5, false) != -1) {
        return UnitValue.TRAILING;
      }
      if (startsWithLenient(paramString, "label", 5, false) != -1) {
        return UnitValue.LABEL;
      }
    }
    else
    {
      if (startsWithLenient(paramString, "baseline", 4, false) != -1) {
        return UnitValue.BASELINE_IDENTITY;
      }
      if (startsWithLenient(paramString, "top", 1, false) != -1) {
        return UnitValue.TOP;
      }
      if (startsWithLenient(paramString, "bottom", 1, false) != -1) {
        return UnitValue.BOTTOM;
      }
    }
    return null;
  }
  
  private static String[] getNumTextParts(String paramString)
  {
    int i = 0;
    int j = paramString.length();
    while (i < j)
    {
      int k = paramString.charAt(i);
      if (k == 32) {
        throw new IllegalArgumentException("Space in UnitValue: '" + paramString + "'");
      }
      if (((k < 48) || (k > 57)) && (k != 46) && (k != 45)) {
        return new String[] { paramString.substring(0, i).trim(), paramString.substring(i).trim() };
      }
      i++;
    }
    return new String[] { paramString, "" };
  }
  
  private static int getOper(String paramString)
  {
    int i = paramString.length();
    if (i < 3) {
      return 100;
    }
    if ((i > 5) && (paramString.charAt(3) == '(') && (paramString.charAt(i - 1) == ')'))
    {
      if (paramString.startsWith("min(")) {
        return 105;
      }
      if (paramString.startsWith("max(")) {
        return 106;
      }
      if (paramString.startsWith("mid(")) {
        return 107;
      }
    }
    for (int j = 0; j < 2; j++)
    {
      int k = i - 1;
      int m = 0;
      while (k > 0)
      {
        int n = paramString.charAt(k);
        if (n == 41) {
          m++;
        } else if (n == 40) {
          m--;
        } else if (m == 0) {
          if (j == 0)
          {
            if (n == 43) {
              return 101;
            }
            if (n == 45) {
              return 102;
            }
          }
          else
          {
            if (n == 42) {
              return 103;
            }
            if (n == 47) {
              return 104;
            }
          }
        }
        k--;
      }
    }
    return 100;
  }
  
  private static int startsWithLenient(String paramString, String[] paramArrayOfString, int[] paramArrayOfInt, boolean paramBoolean)
  {
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      int j = paramArrayOfInt != null ? paramArrayOfInt[i] : -1;
      int k = startsWithLenient(paramString, paramArrayOfString[i], j, paramBoolean);
      if (k > -1) {
        return k;
      }
    }
    return -1;
  }
  
  private static int startsWithLenient(String paramString1, String paramString2, int paramInt, boolean paramBoolean)
  {
    if (paramString1.charAt(0) != paramString2.charAt(0)) {
      return -1;
    }
    if (paramInt == -1) {
      paramInt = paramString2.length();
    }
    int i = paramString1.length();
    if (i < paramInt) {
      return -1;
    }
    int j = paramString2.length();
    int k = 0;
    for (int m = 0; m < j; m++)
    {
      while ((k < i) && ((paramString1.charAt(k) == ' ') || (paramString1.charAt(k) == '_'))) {
        k++;
      }
      if ((k >= i) || (paramString1.charAt(k) != paramString2.charAt(m))) {
        return (m >= paramInt) && ((paramBoolean) || (k >= i)) && ((k >= i) || (paramString1.charAt(k - 1) == ' ')) ? k : -1;
      }
      k++;
    }
    return (k >= i) || (paramBoolean) || (paramString1.charAt(k) == ' ') ? k : -1;
  }
  
  private static String[] toTrimmedTokens(String paramString, char paramChar)
  {
    int i = 0;
    int j = paramString.length();
    int k = paramChar == ' ' ? 1 : 0;
    int m = 0;
    for (int n = 0; n < j; n++)
    {
      c1 = paramString.charAt(n);
      if (c1 == '(')
      {
        m++;
      }
      else if (c1 == ')')
      {
        m--;
      }
      else if ((m == 0) && (c1 == paramChar))
      {
        i++;
        while ((k != 0) && (n < j - 1) && (paramString.charAt(n + 1) == ' ')) {
          n++;
        }
      }
      if (m < 0) {
        throw new IllegalArgumentException("Unbalanced parentheses: '" + paramString + "'");
      }
    }
    if (m != 0) {
      throw new IllegalArgumentException("Unbalanced parentheses: '" + paramString + "'");
    }
    if (i == 0) {
      return new String[] { paramString.trim() };
    }
    String[] arrayOfString = new String[i + 1];
    char c1 = '\000';
    int i1 = 0;
    m = 0;
    for (int i2 = 0; i2 < j; i2++)
    {
      char c2 = paramString.charAt(i2);
      if (c2 == '(')
      {
        m++;
      }
      else if (c2 == ')')
      {
        m--;
      }
      else if ((m == 0) && (c2 == paramChar))
      {
        arrayOfString[(i1++)] = paramString.substring(c1, i2).trim();
        c1 = i2 + 1;
        while ((k != 0) && (i2 < j - 1) && (paramString.charAt(i2 + 1) == ' ')) {
          i2++;
        }
      }
    }
    arrayOfString[(i1++)] = paramString.substring(c1, j).trim();
    return arrayOfString;
  }
  
  private static ArrayList<String> getRowColAndGapsTrimmed(String paramString)
  {
    if (paramString.indexOf('|') != -1) {
      paramString = paramString.replaceAll("\\|", "][");
    }
    ArrayList localArrayList = new ArrayList(Math.max(paramString.length() >> 3, 3));
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    int n = paramString.length();
    while (m < n)
    {
      int i1 = paramString.charAt(m);
      if (i1 == 91)
      {
        i++;
      }
      else
      {
        if (i1 != 93) {
          break label130;
        }
        j++;
      }
      if ((i != j) && (i - 1 != j)) {
        break;
      }
      localArrayList.add(paramString.substring(k, m).trim());
      k = m + 1;
      label130:
      m++;
    }
    if (i != j) {
      throw new IllegalArgumentException("'[' and ']' mismatch in row/column format string: " + paramString);
    }
    if (i == 0)
    {
      localArrayList.add("");
      localArrayList.add(paramString);
      localArrayList.add("");
    }
    else if (localArrayList.size() % 2 == 0)
    {
      localArrayList.add(paramString.substring(k, paramString.length()));
    }
    return localArrayList;
  }
  
  public static String prepare(String paramString)
  {
    return paramString != null ? paramString.trim().toLowerCase() : "";
  }
}
