package user;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.io.FileUtils;
import shared.Globals;
import shared.RomValidator;
import shared.SwingUtils;






public class StarRodUser
  extends JFrame
{
  private static final long serialVersionUID = 1L;
  private final JFileChooser romChooser;
  private final JFileChooser patchChooser;
  private File pristineRom = null;
  private File patchFile = null;
  
  private JDialog pleaseWaitDialog;
  
  private JLabel currentTaskLabel;
  private boolean taskRunning = false;
  
  private List<JButton> buttons = new ArrayList();
  
  public static void main(String[] args)
  {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (Exception localException) {}
    

    new StarRodUser();
  }
  
  private StarRodUser()
  {
    setTitle("Star Rod Mod Patcher");
    setDefaultCloseOperation(3);
    setIconImage(Globals.getDefaultIconImage());
    
    setMinimumSize(new Dimension(480, 32));
    setLocationRelativeTo(null);
    
    romChooser = new JFileChooser();
    romChooser.setCurrentDirectory(new File("."));
    romChooser.setFileSelectionMode(0);
    romChooser.setDialogTitle("Choose Pristine ROM");
    
    patchChooser = new JFileChooser();
    patchChooser.setCurrentDirectory(new File("."));
    patchChooser.setFileSelectionMode(0);
    patchChooser.setDialogTitle("Choose Patch File");
    
    JProgressBar progressBar = new JProgressBar();
    progressBar.setIndeterminate(true);
    
    currentTaskLabel = new JLabel("Applying patch to ROM...");
    
    pleaseWaitDialog = new JDialog(this);
    pleaseWaitDialog.setLocationRelativeTo(null);
    pleaseWaitDialog.setLayout(new MigLayout("fill"));
    pleaseWaitDialog.setTitle("Please Wait");
    pleaseWaitDialog.setIconImage(Globals.getDefaultIconImage());
    pleaseWaitDialog.add(currentTaskLabel, "wrap");
    pleaseWaitDialog.add(progressBar, "growx");
    pleaseWaitDialog.setMinimumSize(new Dimension(240, 80));
    pleaseWaitDialog.setResizable(false);
    pleaseWaitDialog.pack();
    
    final JTextField romFileField = new JTextField();
    romFileField.setEditable(false);
    romFileField.setMinimumSize(new Dimension(64, 24));
    
    JButton chooseROMButton = new JButton("Choose");
    
    chooseROMButton.addActionListener(new ActionListener()
    {










      public void actionPerformed(ActionEvent arg0)
      {









        new Thread()
        {
          public void run()
          {
            for (Iterator localIterator = buttons.iterator(); localIterator.hasNext();) { button = (JButton)localIterator.next();
              button.setEnabled(false); }
            JButton button;
            File choice = promptSelectPristineRom();
            if (choice != null)
            {
              val$romFileField.setText(choice.getAbsolutePath());
              pristineRom = choice;
            }
            
            for (JButton button : buttons)
              button.setEnabled(true);
          }
        }.start();
      }
    });
    buttons.add(chooseROMButton);
    
    final JTextField patchFileField = new JTextField();
    patchFileField.setEditable(false);
    patchFileField.setMinimumSize(new Dimension(64, 24));
    
    JButton choosePatchButton = new JButton("Choose");
    choosePatchButton.addActionListener(new ActionListener()
    {

      public void actionPerformed(ActionEvent arg0)
      {
        File choice = promptSelectPatch();
        if (choice != null)
        {
          patchFileField.setText(choice.getAbsolutePath());
          patchFile = choice;
        }
      }
    });
    buttons.add(choosePatchButton);
    
    JButton patchRomButton = new JButton("Create Modded ROM");
    patchRomButton.addActionListener(new ActionListener()
    {

      public void actionPerformed(ActionEvent arg0)
      {
        if (pristineRom == null)
        {
          SwingUtils.showFramedMessageDialog(StarRodUser.this, "You have not selected a valid ROM.", "Missing ROM", 2);
          


          return;
        }
        
        if (patchFile == null)
        {
          SwingUtils.showFramedMessageDialog(null, "You have not selected a valid patch file.", "Missing Patch File", 2);
          


          return;
        }
        
        StarRodUser.this.startTask(new StarRodUser.TaskWorker(StarRodUser.this, new Runnable()
        {


          public void run() {
            StarRodUser.this.patchRom(); } }, null));
      }
      

    });
    buttons.add(patchRomButton);
    
    setDefaultCloseOperation(0);
    addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent e) {
        int choice = 0;
        
        if (taskRunning) {
          choice = SwingUtils.showFramedConfirmDialog(null, "A task is still running.\r\nAre you sure you want to exit?", "Task Still Running", 0, 2);
        }
        

        if (choice == 0) {
          System.exit(0);
        }
      }
    });
    setLayout(new MigLayout("fillx, ins 16 16 n 16, hidemode 3"));
    add(new JLabel("Paper Mario ROM:"));
    add(romFileField, "pushx, growx");
    add(chooseROMButton, "wrap");
    add(new JLabel("Star Rod Patch File:"));
    add(patchFileField, "pushx, growx");
    add(choosePatchButton, "wrap 16");
    add(patchRomButton, "growy, w 50%, center, span");
    
    pack();
    setResizable(false);
    setVisible(true);
  }
  
  public File promptSelectPristineRom()
  {
    if (SwingUtils.showFramedOpenDialog(romChooser, null) == 0)
    {
      File romChoice = romChooser.getSelectedFile();
      if ((pristineRom == null) || (!romChoice.equals(pristineRom))) {
        try
        {
          return RomValidator.validateROM(romChoice);
        }
        catch (IOException e) {
          SwingUtils.showFramedMessageDialog(null, "IOException during ROM validation.", "ROM Validation Failure", 0);
        }
      }
    }
    


    return null;
  }
  
  public File promptSelectPatch()
  {
    if (SwingUtils.showFramedOpenDialog(patchChooser, null) == 0)
    {
      File patchChoice = patchChooser.getSelectedFile();
      if ((patchFile == null) || (!patchChoice.equals(patchFile)))
      {
        return patchChoice;
      }
    }
    return null;
  }
  
  private void startTask(SwingWorker<?, ?> worker)
  {
    taskRunning = true;
    pleaseWaitDialog.setVisible(true);
    for (JButton button : buttons)
      button.setEnabled(false);
    worker.execute();
  }
  
  private void endTask()
  {
    for (JButton button : buttons)
      button.setEnabled(true);
    pleaseWaitDialog.setVisible(false);
    taskRunning = false;
  }
  
  private class TaskWorker extends SwingWorker<Boolean, String>
  {
    private final Runnable runnable;
    
    private TaskWorker(Runnable runnable)
    {
      this.runnable = runnable;
    }
    
    protected Boolean doInBackground()
    {
      runnable.run();
      return Boolean.valueOf(true);
    }
    
    protected void done()
    {
      StarRodUser.this.endTask();
    }
  }
  
  private void patchRom()
  {
    try
    {
      final File patchedRom = applyPatch();
      if (patchedRom != null)
      {
        SwingUtilities.invokeLater(new Runnable()
        {

          public void run()
          {
            Toolkit.getDefaultToolkit().beep();
            SwingUtils.showFramedMessageDialog(null, "Your modded ROM is ready to play:\n" + patchedRom
            
              .getAbsolutePath(), "Done", 1);
          }
          

        });
      }
    }
    catch (Throwable e)
    {
      SwingUtilities.invokeLater(new Runnable()
      {

        public void run()
        {
          e.printStackTrace();
          StarRodUser.displayStackTrace(e);
        }
      });
    }
  }
  
  private File applyPatch()
    throws IOException
  {
    byte[] patchBytes = FileUtils.readFileToByteArray(patchFile);
    ByteBuffer bb = ByteBuffer.wrap(patchBytes);
    
    int firstWord = bb.getInt();
    bb.rewind();
    
    if ((firstWord == 1633235065) || (firstWord == 1297109587))
    {
      byte[] swapped = new byte[patchBytes.length];
      
      for (int i = 0; i < patchBytes.length; i += 2)
      {
        swapped[i] = patchBytes[(i + 1)];
        swapped[(i + 1)] = patchBytes[i];
      }
      
      patchBytes = swapped;
    }
    
    if (firstWord == 1499560240)
    {
      currentTaskLabel.setText("Decompressing mod package...");
      patchBytes = decodeYay0(patchBytes);
      bb = ByteBuffer.wrap(patchBytes);
    }
    
    if (bb.getInt() != 1347244882)
    {
      SwingUtils.showFramedMessageDialog(null, "The patch file you selected is invalid.", "Invalid Patch File", 2);
      


      return null;
    }
    
    currentTaskLabel.setText("Applying patches to ROM...");
    

    String path = pristineRom.getParentFile().getAbsolutePath();
    String extension = pristineRom.getName().substring(pristineRom.getName().lastIndexOf("."));
    

    String name = patchFile.getName();
    name = name.substring(0, name.lastIndexOf("."));
    
    File patchedRom = new File(path + File.separator + name + extension);
    if (patchedRom.exists())
    {
      int choice = SwingUtils.showFramedConfirmDialog(null, "The file \"" + patchedRom
        .getName() + "\" already exists.\nDo you want to replace it?", "Replace Existing File?", 2);
      


      if (choice != 0) {
        return null;
      }
    }
    FileUtils.copyFile(pristineRom, patchedRom);
    
    RandomAccessFile raf = new RandomAccessFile(patchedRom, "rw");
    
    int patchCount = bb.getInt();
    for (int i = 0; i < patchCount; i++)
    {
      int patchStart = bb.getInt();
      int patchLength = bb.getInt();
      
      byte[] patch = new byte[patchLength];
      bb.get(patch);
      
      raf.seek(patchStart);
      raf.write(patch);
    }
    
    raf.seek(16L);
    int crc1 = raf.readInt();
    int crc2 = raf.readInt();
    
    raf.close();
    
    printProject64RDB(name, crc1, crc2);
    
    return patchedRom;
  }
  
  public static void displayStackTrace(Throwable e)
  {
    StackTraceElement[] stackTrace = e.getStackTrace();
    
    JTextArea textArea = new JTextArea(20, 50);
    textArea.setEditable(false);
    JScrollPane detailScrollPane = new JScrollPane(textArea);
    detailScrollPane.setHorizontalScrollBarPolicy(30);
    
    textArea.append(e.getClass().toString() + "\r\n");
    for (StackTraceElement ele : stackTrace) {
      textArea.append("  at " + ele.toString() + "\r\n");
    }
    String title = e.getClass().getSimpleName();
    if (title.isEmpty()) { title = "Anonymous Exception";
    }
    if ((e instanceof AssertionError)) {
      title = "Assertion Failed";
    }
    StringBuilder msgBuilder = new StringBuilder();
    
    if (e.getMessage() != null) {
      msgBuilder.append(e.getMessage());
    } else if (stackTrace.length > 0) {
      msgBuilder.append("at " + stackTrace[0].toString() + "\r\n");
    }
    String[] options = { "OK", "Details" };
    
    int selection = SwingUtils.showFramedOptionDialog(null, msgBuilder
      .toString(), title, 1, 0, Globals.ICON_ERROR, options, options[0]);
    






    if (selection == 1)
    {
      options = new String[] { "OK", "Copy to Clipboard" };
      selection = SwingUtils.showFramedOptionDialog(null, detailScrollPane, "Exception Details", 1, 0, Globals.ICON_ERROR, options, options[0]);
      







      if (selection == 1)
      {
        StringSelection stringSelection = new StringSelection(textArea.getText());
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        cb.setContents(stringSelection, null);
      }
    }
  }
  
  private static void printProject64RDB(String name, int crc1, int crc2) throws IOException
  {
    PrintWriter pw = new PrintWriter(new File("Project64 Settings.txt"));
    pw.printf(String.format("[%08X-%08X-C:45]%s", new Object[] { Integer.valueOf(crc1), Integer.valueOf(crc2), System.lineSeparator() }), new Object[0]);
    pw.printf("Good Name=%s%s", new Object[] { name, System.lineSeparator() });
    pw.println("Internal Name=PAPER MARIO");
    pw.println("Status=Compatible");
    pw.println("32bit=No");
    pw.println("Clear Frame=1");
    pw.println("Counter Factor=1");
    pw.println("Culling=1");
    pw.println("Emulate Clear=0");
    pw.println("Primary Frame Buffer=0");
    pw.println("RDRAM Size=8");
    pw.println("Self Texture=0");
    pw.println("glide64-depthmode=1");
    pw.println("glide64-fb_hires=1");
    pw.println("glide64-fb_read_alpha=1");
    pw.println("glide64-fb_smart=1");
    pw.println("glide64-filtering=1");
    pw.println("glide64-hires_buf_clear=0");
    pw.println("glide64-optimize_texrect=0");
    pw.println("glide64-swapmode=2");
    pw.println("glide64-useless_is_useless=1");
    pw.close();
  }
  
  public static byte[] decodeYay0(byte[] source)
  {
    int decompressedSize = getInteger(source, 4);
    int linkOffset = getInteger(source, 8);
    int sourceOffset = getInteger(source, 12);
    
    byte currentCommand = 0;
    int commandOffset = 16;
    int remainingBits = 0;
    
    byte[] decoded = new byte[decompressedSize];
    int decodedBytes = 0;
    

    do
    {
      if (remainingBits == 0)
      {
        currentCommand = source[commandOffset];
        commandOffset++;
        remainingBits = 8;
      }
      

      if ((currentCommand & 0x80) != 0)
      {
        decoded[decodedBytes] = source[sourceOffset];
        sourceOffset++;
        decodedBytes++;

      }
      else
      {
        short link = getShort(source, linkOffset);
        linkOffset += 2;
        
        int dist = link & 0xFFF;
        int copySrc = decodedBytes - (dist + 1);
        int length = link >> 12 & 0xF;
        

        if (length == 0)
        {
          length = source[sourceOffset] & 0xFF;
          length += 16;
          sourceOffset++;
        }
        
        length += 2;
        

        for (int i = 0; i < length; i++)
        {
          decoded[decodedBytes] = decoded[(copySrc + i)];
          decodedBytes++;
        }
      }
      
      currentCommand = (byte)(currentCommand << 1);
      remainingBits--;
    } while (decodedBytes < decompressedSize);
    
    return decoded;
  }
  
  private static short getShort(byte[] buffer, int start)
  {
    return (short)(buffer[(start + 1)] & 0xFF | (buffer[start] & 0xFF) << 8);
  }
  
  private static int getInteger(byte[] buffer, int start)
  {
    return buffer[(start + 3)] & 0xFF | (buffer[(start + 2)] & 0xFF) << 8 | (buffer[(start + 1)] & 0xFF) << 16 | (buffer[(start + 0)] & 0xFF) << 24;
  }
}
