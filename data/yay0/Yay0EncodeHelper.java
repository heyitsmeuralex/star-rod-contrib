package data.yay0;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import util.Logger;
import util.Priority;



public class Yay0EncodeHelper
{
  private static final int MIN_LINK_LENGTH = 3;
  private static final int MAX_LINK_LENGTH = 273;
  private static final int MAX_OFFSET = 4096;
  private final byte[] source;
  private int bufferPosition = 0;
  
  private final Yay0Encoder encoder;
  private Collection<Encode> codeList = new LinkedList();
  private Deque<EncodeLink> linkQueue = new LinkedList();
  
  public Yay0EncodeHelper(byte[] src, boolean logUpates)
  {
    source = src;
    encoder = new Yay0Encoder(source.length);
    
    EncodeLink newLink;
    while ((bufferPosition < source.length) || (!linkQueue.isEmpty()))
    {
      if ((logUpates) && (bufferPosition % 1024 == 0))
      {
        String progress = String.format("(%.1f%%)", new Object[] { Float.valueOf(100.0F * bufferPosition / source.length) });
        Logger.log("Compressing bytes... " + progress, Priority.UPDATE);
      }
      
      newLink = findPatternFrom(source, bufferPosition);
      if (newLink == null)
      {

        if (linkQueue.size() == 1)
        {
          codeList.add(linkQueue.pollFirst());



        }
        else if (linkQueue.size() > 1)
        {
          optimizeLinks(false);

        }
        else
        {
          codeList.add(new EncodeCopy(source[bufferPosition]));
          bufferPosition += 1;
        }
      }
      else
      {
        linkQueue.addLast(newLink);
        bufferPosition += length;
      }
    }
    

    for (Encode e : codeList) {
      e.exec(encoder);
    }
    encoder.flush();
  }
  








  private void optimizeLinks(boolean debug)
  {
    int totalLength = 0;
    int totalBudget = 0;
    for (Encode e : linkQueue)
    {
      totalBudget += e.getBudgetCost();
      totalLength += e.getEncodeLength();
    }
    
    if (debug)
    {
      System.out.println(String.format("%4X: Optimizing %d links, total length = 0x%02X", new Object[] {
        Integer.valueOf(bufferPosition - totalLength), Integer.valueOf(linkQueue.size()), Integer.valueOf(totalLength) }));
    }
    
    Object revisedQueue = new LinkedList();
    int revisedEncoded = bufferPosition - totalLength;
    int revisedBudget = 0;
    
    ((Deque)revisedQueue).add(new EncodeCopy(source[revisedEncoded]));
    revisedEncoded++;
    revisedBudget++;
    
    while (revisedBudget < totalBudget)
    {
      EncodeLink revisedLink = findPatternFrom(source, revisedEncoded);
      if (revisedLink == null) {
        break;
      }
      ((Deque)revisedQueue).add(revisedLink);
      revisedEncoded += revisedLink.getEncodeLength();
      revisedBudget += revisedLink.getBudgetCost();
    }
    
    int revisedLength = revisedEncoded - (bufferPosition - totalLength);
    
    if (debug)
    {
      System.out.print("      Old: ");
      for (Encode e : linkQueue)
        System.out.print(e + " ");
      System.out.println(" (" + totalBudget + " bytes, length " + totalLength + ")");
      
      System.out.print("      New: ");
      for (Encode e : (Deque)revisedQueue)
        System.out.print(e + " ");
      System.out.println(" (" + revisedBudget + " bytes, length " + revisedLength + ")");
    }
    
    boolean better = (revisedBudget < totalBudget) && (totalLength == revisedLength);
    boolean longer = (revisedBudget <= totalBudget) && (totalLength < revisedLength);
    
    if (debug)
    {
      System.out.println("      Better? " + better);
      System.out.println("      Longer? " + longer);
    }
    
    boolean useRecursive = true;
    Encode revision;
    if ((better) || (longer))
    {
      if (debug) {
        System.out.println("      Using REVISED encoding.");
      }
      if (useRecursive)
      {

        revision = (Encode)((Deque)revisedQueue).pollFirst();
        codeList.add(revision);
        bufferPosition += revision.getEncodeLength();
        
        revision = (Encode)((Deque)revisedQueue).pollFirst();
        codeList.add(revision);
        bufferPosition += revision.getEncodeLength();
        
        while (!linkQueue.isEmpty())
        {
          Encode e = (Encode)linkQueue.pollFirst();
          bufferPosition -= e.getEncodeLength();
        }
      }
      else
      {
        for (Encode e : (Deque)revisedQueue)
        {
          codeList.add(e);
          bufferPosition += e.getEncodeLength();
        }
      }
    } else { while (!linkQueue.isEmpty())
      {
        Encode e = (Encode)linkQueue.pollFirst();
        bufferPosition -= e.getEncodeLength();
        continue;
        



        if (debug) {
          System.out.println("      Using ORIGINAL encoding.");
        }
        if (useRecursive)
        {
          codeList.add(linkQueue.pollFirst());
          
          while (!linkQueue.isEmpty())
          {
            Encode e = (Encode)linkQueue.pollFirst();
            bufferPosition -= e.getEncodeLength();
          }
        }
        

        codeList.add(linkQueue.pollFirst());
        
        while (!linkQueue.isEmpty())
        {
          Encode e = (Encode)linkQueue.pollFirst();
          bufferPosition -= e.getEncodeLength();
        }
      }
    }
    
    if (debug) {
      System.out.println("");
    }
  }
  







  private static EncodeLink findPatternFrom(byte[] source, int encoderBufferPosition)
  {
    int remainingBytes = source.length - encoderBufferPosition;
    

    if (remainingBytes < 3) {
      return null;
    }
    
    int maxMatchLength = remainingBytes > 273 ? 273 : remainingBytes;
    

    int minWindowStart = encoderBufferPosition <= 4096 ? 0 : encoderBufferPosition - 4096;
    

    int bestMatchLength = 0;
    int bestMatchStart = 0;
    
    for (int windowStart = minWindowStart; windowStart < encoderBufferPosition; windowStart++)
    {
      int matchingLength = 0;
      int windowPos = windowStart;
      



      for (;;)
      {
        if (windowPos >= encoderBufferPosition) {
          windowPos = windowStart;
        }
        byte nextSource = source[(encoderBufferPosition + matchingLength)];
        byte nextMatch = source[windowPos];
        
        if (nextMatch == nextSource)
        {
          matchingLength++;
          windowPos++;
          
          if (matchingLength > bestMatchLength)
          {
            bestMatchLength = matchingLength;
            bestMatchStart = windowStart;
          }
          


          if (matchingLength == maxMatchLength)
            break;
        }
      }
    }
    if (bestMatchLength < 3) {
      return null;
    }
    return new EncodeLink(bestMatchLength, encoderBufferPosition - bestMatchStart);
  }
  
  public byte[] getFile()
  {
    return encoder.getFile();
  }
}
