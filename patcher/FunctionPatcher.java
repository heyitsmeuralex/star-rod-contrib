package patcher;

import asm.MIPS;
import data.strings.StringEncoder;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import main.Mod;
import main.config.Config;
import util.Logger;
import util.Priority;

public class FunctionPatcher
{
  public FunctionPatcher() {}
  
  public static void hookBoot(RandomAccessFile raf, int size) throws IOException
  {
    if (size + 41943040 < 0) {
      Logger.log("ERROR: TRYING TO LOAD TOO MUCH DURING BOOT!", Priority.WARNING);
      System.exit(0);
    }
    


    raf.seek(5084L);
    raf.writeInt(201365264);
    

    int ins1 = 1006961280;
    int ins2 = 614793216;
    
    int end = 41943040 + size;
    int ins3 = 0x3C040000 | end >>> 16;
    int ins4 = 0x24A50000 | end & 0xFFFF;
    
    int ins5 = 1007059008;
    int ins6 = 616955904;
    
    raf.seek(4160L);
    int[] data = { 666763240, 1007006719, 883228692, -1347420144, 1007714314, 638625336, -1346437100, -1912340480, 201425752, 12333, 1007006719, 883228676, -1912340480, 201425752, 12333, 1007006719, 1007044947, -1912340480, 201425752, 885405236, ins1, ins2, ins3, ins4, ins5, ins6, 201369039, 0, -1883308012, -1884291056, 65011720, 666697752 };
    








    for (int i : data) {
      raf.writeInt(i);
    }
    
    Logger.log("Boot process hooked.", Priority.IMPORTANT);
  }
  
  public static void showVersionInfo(RandomAccessFile raf, int offset) throws IOException
  {
    Logger.log(String.format("Version information will be stored at 0x%08X", new Object[] {
      Integer.valueOf(Patcher.toAddress(offset)) }), Priority.IMPORTANT);
    raf.seek(offset);
    
    int starRodStringAddress = Patcher.toAddress((int)raf.getFilePointer());
    ByteBuffer bb = StringEncoder.encodeString("Star Rod v0.1.2");
    while (bb.hasRemaining())
      raf.write(bb.get());
    raf.write(-3);
    while ((raf.getFilePointer() & 0x3) != 0L) {
      raf.write(0);
    }
    int modStringAddress = Patcher.toAddress((int)raf.getFilePointer());
    String modName = currentModconfig.getString(main.config.Options.ModVersionString);
    bb = StringEncoder.encodeString(modName);
    while (bb.hasRemaining())
      raf.write(bb.get());
    raf.write(-3);
    while ((raf.getFilePointer() & 0x3) != 0L) {
      raf.write(0);
    }
    int fpPrintVersionString = Patcher.toAddress((int)raf.getFilePointer());
    assembleAndWrite(raf, new String[] {
      String.format("LUI\tA0, %04X    ", new Object[] {Integer.valueOf(starRodStringAddress >>> 16) }), 
      String.format("ORI\tA0, A0, %04X", new Object[] {Integer.valueOf(starRodStringAddress & 0xFFFF) }), "ORI \tA1, R0, C8", "SUB\tA2, R0, S3", "ADDI \tA2, A2, F5", "ORI \tA3, R0, FF", "SW \tR0, 10 (SP)", "JAL\t8024997C", "SW \tR0, 14 (SP)", 
      







      String.format("LUI\tA0, %04X    ", new Object[] {Integer.valueOf(modStringAddress >>> 16) }), 
      String.format("ORI\tA0, A0, %04X", new Object[] {Integer.valueOf(modStringAddress & 0xFFFF) }), "ORI \tA1, R0, 08", "SUB\tA2, R0, S3", "ADDI \tA2, A2, F5", "ORI \tA3, R0, FF", "SW \tR0, 10 (SP)", "JAL\t8024997C", "SW \tR0, 14 (SP)", "LW\t\tRA, 28 (SP)", "J\t\t80244CD8", "LW\t\tS3, 24 (SP)" });
    













    raf.seek(1463600L);
    writeJump(raf, fpPrintVersionString);
    System.out.printf(">>> JUMP TO %08X\n", new Object[] { Integer.valueOf(fpPrintVersionString) });
    raf.writeInt(0);
  }
  
  public static void enableCompressedScripts(RandomAccessFile raf, int offset) throws IOException
  {
    int address = Patcher.toAddress(offset);
    


    raf.seek(949044L);
    assembleAndWrite(raf, new String[] { "LBU\tV0, 1 (V1)", "J\t\t" + 
    
      String.format("%08X", new Object[] {Integer.valueOf(address) }), "LBU\tA1, 3 (V1)", "NOP" });
    



    raf.seek(offset);
    assembleAndWrite(raf, new String[] { "BEQL\tV0, R0 C", "LW\t\tV0, 4 (V1)", "LBU\tV0, 0 (V1)", "ADDI\tV1, V1, -4", "J\t\t802C3194", "ADDIU\tV1, V1, 8" });
    







    Logger.log(String.format("Compressed scripts enabled at 0x%08X", new Object[] { Integer.valueOf(offset) }), Priority.IMPORTANT);
  }
  

  public static void enableCompressedBattleSections(RandomAccessFile raf, int offset)
    throws IOException
  {
    int address = Patcher.toAddress(offset);
    
    raf.seek(319512L);
    assembleAndWrite(raf, "J " + String.format("%08X", new Object[] { Integer.valueOf(address) }));
    
    raf.seek(offset);
    assembleAndWrite(raf, new String[] { "OR\t\tA2, R0, R0", "JAL\t8002973C", "LUI\tA2, 8060", "LW\t\tA1, C (S1)", "OR\t\tA0, R0, R0", "JAL\t8006EDF0", "LUI\tA0, 8060", "J\t\t80072C20", "ADDIU\tV1, V1, 8" });
  }
  















  /**
   * @deprecated
   */
  public static void addFinalRank(RandomAccessFile raf, int offset)
    throws IOException
  {
    raf.seek(1647200L);
    raf.writeInt(MIPS.getJumpIns(Patcher.toAddress(offset)));
    raf.writeInt(0);
    
    raf.seek(offset);
    raf.writeInt(537395203);
    raf.writeInt(4735010);
    raf.writeInt(83886082);
    raf.writeInt(0);
    raf.writeInt(537001986);
    raf.writeInt(608305154);
    raf.writeInt(-1568538098);
    raf.writeInt(MIPS.getJumpIns(-2144978552L));
    raf.writeInt(0);
    

    int newOffset = (int)raf.getFilePointer();
    int newRamAddress = Patcher.toAddress(newOffset);
    raf.seek(1647236L);
    raf.writeInt(MIPS.getJumpIns(newRamAddress));
    raf.writeInt(0);
    
    raf.seek(newOffset);
    raf.writeInt(537395203);
    raf.writeInt(6832162);
    raf.writeInt(83886082);
    raf.writeInt(0);
    raf.writeInt(537067522);
    raf.writeInt(35921962);
    raf.writeInt(6428705);
    raf.writeInt(MIPS.getJumpIns(-2144978516L));
    raf.writeInt(0);
    

    newOffset = (int)raf.getFilePointer();
    newRamAddress = Patcher.toAddress(newOffset);
    
    raf.seek(7302040L);
    raf.writeInt(newRamAddress);
    
    int[] script = { 67, 2, -2144960064, 2, 67, 1, -2144694272, 67, 3, -2144862904, 256, -30000000, 20, 1, -30000000, 22, 1, 0, 36, 2, -29999986, 1, 36, 2, -29999985, 2, 22, 1, 1, 36, 2, -29999986, 1, 36, 2, -29999985, 3, 22, 1, 2, 36, 2, -29999986, 1, 36, 2, -29999985, 4, 22, 1, 3, 36, 2, -29999986, 3, 36, 2, -29999985, 5, 35, 0, 70, 1, -2145149116, 2, 0, 1, 0 };
    




















    raf.seek(newOffset);
    for (int i : script) {
      raf.writeInt(i);
    }
  }
  














  /**
   * @deprecated
   */
  public static void increaseEnemyMaxHP(RandomAccessFile raf, int offset)
    throws IOException
  {
    raf.seek(1501832L);
    int ins1 = raf.readInt();
    int ins2 = raf.readInt();
    raf.seek(raf.getFilePointer() - 8L);
    raf.writeInt(MIPS.getJumpIns(Patcher.toAddress(offset)));
    raf.writeInt(0);
    
    raf.seek(offset);
    
    int[] over100 = { 42074136, 1316803, 16400, 530563, 6457379, 1151104, 38866977, 135232, 42115107, 1007714346, -1911492676, 604307466, 201658999, 33562669, 1314944, 1007190056, 621283836, 1006993449, 11151397, 201658879, 33562669, 8237, 8398893, -1765670910, 8400941, 608305158, 809697279, -1348337648, -1767768062, 8402989, -1346502632, -1346502628, 608305154, 809697279, 201957007, -1348337644, 33562669, -1885011936, -1884946396, 614858750, 201658977, 616955910, 201658865, 33562669 };
    












































    raf.writeInt(537395400);
    raf.writeInt(42485794);
    raf.writeInt(85983236);
    raf.writeInt(0);
    raf.writeInt(537534456);
    raf.writeInt(MIPS.getJumpIns(48 + Patcher.toAddress(offset)));
    
    raf.writeInt(0);
    

    raf.writeInt(537395300);
    raf.writeInt(42485794);
    raf.writeInt(85983239 + over100.length);
    raf.writeInt(0);
    raf.writeInt(537534416);
    

    raf.writeInt(18915360);
    raf.writeInt(ins1);
    raf.writeInt(ins2);
    for (int i : over100) raf.writeInt(i);
    raf.writeInt(MIPS.getJumpIns(-2145123880L));
    raf.writeInt(0);
    

    raf.writeInt(ins1);
    raf.writeInt(ins2);
    raf.writeInt(MIPS.getJumpIns(-2145123920L));
    raf.writeInt(0);
    
    Logger.log(String.format("Increased maximum enemy HP at 0x%08X", new Object[] { Integer.valueOf(offset) }), Priority.IMPORTANT);
  }
  

  public static void generalHook(RandomAccessFile raf, int targetROM, int targetRAM, int[] instructions, int offset)
    throws IOException
  {
    raf.seek(targetROM);
    int ins1 = raf.readInt();
    int ins2 = raf.readInt();
    raf.seek(raf.getFilePointer() - 8L);
    raf.writeInt(MIPS.getJumpIns(Patcher.toAddress(offset)));
    raf.writeInt(0);
    
    raf.seek(offset);
    for (int i : instructions) {
      raf.writeInt(i);
    }
    raf.writeInt(ins1);
    raf.writeInt(ins2);
    raf.writeInt(MIPS.getJumpIns(targetRAM + 8));
    raf.writeInt(0);
  }
  
  protected static void assembleAndWrite(RandomAccessFile raf, String[] lines) throws IOException
  {
    String[] assembled = MIPS.assemble(lines);
    
    for (String s : assembled) {
      raf.writeInt((int)Long.parseLong(s, 16));
    }
  }
  
  protected static void assembleAndWrite(RandomAccessFile raf, String s) throws IOException {
    raf.writeInt((int)Long.parseLong(MIPS.assemble(s), 16));
  }
  
  protected static void writeJump(RandomAccessFile raf, int dest) throws IOException
  {
    String ins = String.format("J %08X", new Object[] { Integer.valueOf(dest) });
    raf.writeInt((int)Long.parseLong(MIPS.assemble(ins), 16));
  }
}
