package patcher;

import asm.MIPS;
import data.shared.DataUtils;
import java.io.IOException;
import java.io.RandomAccessFile;
import main.DevContext;
import main.Mod;
import main.config.Config;
import main.config.Options;
import util.Logger;
import util.Priority;

public class DebugPatcher extends FunctionPatcher
{
  private static final int[] battleBytes = { 255, 11, 9, 255, 13, 12, 12, 34, 65, 84, 84, 76, 69, 26, 247, 31, 31, 13, 31, 31, 253 };
  



  private static final int battleIdIndex = 15;
  


  private static final int[] mapBytes = { 255, 11, 9, 255, 13, 12, 12, 56, 26, 247, 31, 31, 31, 31, 31, 31, 31, 31, 247, 247, 57, 26, 247, 31, 31, 31, 31, 31, 31, 31, 31, 247, 247, 58, 26, 247, 31, 31, 31, 31, 31, 31, 31, 31, 247, 247, 45, 65, 80, 26, 247, 31, 31, 31, 31, 31, 31, 31, 253 };
  


  private static final int xIndex = 10;
  


  private static final int yIndex = 23;
  


  private static final int zIndex = 36;
  


  private static final int mapNameIndex = 51;
  


  private static int mapStringAddress = 0;
  private static int battleStringAddress = 0;
  
  private static int fpGetCharAddress = 0;
  private static int fpStoreWordAsHexString = 0;
  
  private static int fpStoreMapName = 0;
  private static int fpStoreBattleID = 0;
  
  private static int fpPrintDebugString = 0;
  
  private static int fpLoadDebugMap = 0;
  private static int fpLoadDebugBattle = 0;
  
  public DebugPatcher() {}
  
  public static void showDebugInformation(RandomAccessFile raf, int offset) throws IOException { Logger.log(String.format("Debug data will be stored at 0x%08X", new Object[] {
      Integer.valueOf(Patcher.toAddress(offset)) }), Priority.IMPORTANT);
    raf.seek(offset);
    

    battleStringAddress = Patcher.toAddress((int)raf.getFilePointer());
    for (int i : battleBytes)
      raf.write(i);
    while ((raf.getFilePointer() & 0x3) != 0L) {
      raf.write(0);
    }
    
    mapStringAddress = Patcher.toAddress((int)raf.getFilePointer());
    for (int i : mapBytes)
      raf.write((byte)i);
    while ((raf.getFilePointer() & 0x3) != 0L) {
      raf.write(0);
    }
    
    writeGetCharAddress(raf);
    writeStoreWordAsHexString(raf);
    writeStoreMapName(raf);
    writePrintDebugString(raf);
    writeStoreBattleID(raf);
    writeDebugLoading(raf);
    

    raf.seek(534360L);
    writeJump(raf, fpPrintDebugString);
    raf.writeInt(0);
    

    raf.seek(220144L);
    writeJump(raf, fpStoreMapName);
    raf.writeInt(0);
    

    raf.seek(319632L);
    writeJump(raf, fpStoreBattleID);
    raf.writeInt(0);
    
    raf.seek(978360L);
    String jalIns = String.format("JAL %08X", new Object[] { Integer.valueOf(fpLoadDebugMap) });
    raf.writeInt((int)Long.parseLong(MIPS.assemble(jalIns), 16));
    raf.writeInt(0);
    
    raf.seek(117736L);
    writeJump(raf, fpLoadDebugBattle);
    raf.writeInt(0);
  }
  
  private static void writeGetCharAddress(RandomAccessFile raf)
    throws IOException
  {
    fpGetCharAddress = Patcher.toAddress((int)raf.getFilePointer());
    assembleAndWrite(raf, new String[] { "ADDIU\tT2, T1, 10", "ADDI\tT3, T2, FFE6", "BGEZL\tT3, 4", "ADDIU\tT2, T2, 7", "JR\t\tRA", "NOP" });
  }
  







  private static void writeStoreWordAsHexString(RandomAccessFile raf)
    throws IOException
  {
    fpStoreWordAsHexString = Patcher.toAddress((int)raf.getFilePointer());
    assembleAndWrite(raf, new String[] { "ADDIU\tSP, SP, -14", "SW\t\tRA, 10 (SP)", "OR\t\tT0, A1, R0", 
    




      String.format("JAL   %08X", new Object[] {Integer.valueOf(fpGetCharAddress) }), "ANDI\tT1, T0, 0F", "SB\t\tT2, 7 (A0)", "SRL\tT0, T0, 4", 
      



      String.format("JAL   %08X", new Object[] {Integer.valueOf(fpGetCharAddress) }), "ANDI\tT1, T0, 0F", "SB\t\tT2, 6 (A0)", "SRL\tT0, T0, 4", 
      



      String.format("JAL   %08X", new Object[] {Integer.valueOf(fpGetCharAddress) }), "ANDI\tT1, T0, 0F", "SB\t\tT2, 5 (A0)", "SRL\tT0, T0, 4", 
      



      String.format("JAL   %08X", new Object[] {Integer.valueOf(fpGetCharAddress) }), "ANDI\tT1, T0, 0F", "SB\t\tT2, 4 (A0)", "SRL\tT0, T0, 4", 
      



      String.format("JAL   %08X", new Object[] {Integer.valueOf(fpGetCharAddress) }), "ANDI\tT1, T0, 0F", "SB\t\tT2, 3 (A0)", "SRL\tT0, T0, 4", 
      



      String.format("JAL   %08X", new Object[] {Integer.valueOf(fpGetCharAddress) }), "ANDI\tT1, T0, 0F", "SB\t\tT2, 2 (A0)", "SRL\tT0, T0, 4", 
      



      String.format("JAL   %08X", new Object[] {Integer.valueOf(fpGetCharAddress) }), "ANDI\tT1, T0, 0F", "SB\t\tT2, 1 (A0)", "SRL\tT0, T0, 4", 
      



      String.format("JAL   %08X", new Object[] {Integer.valueOf(fpGetCharAddress) }), "ANDI\tT1, T0, 0F", "SB\t\tT2, 0 (A0)", "LW\t\tRA, 10 (SP)", "JR\t\tRA", "ADDIU\tSP, SP, 14" });
  }
  







  private static void writeStoreMapName(RandomAccessFile raf)
    throws IOException
  {
    int mapNameAddress = mapStringAddress + 51;
    fpStoreMapName = Patcher.toAddress((int)raf.getFilePointer());
    assembleAndWrite(raf, new String[] { "ADDU\tS2, V0, V1", "LW\t\tA2, 0 (S2)", "ADDIU\tSP, SP, FFF4", "SW\t\tS1, 0 (SP)", "SW\t\tS2, 4 (SP)", "SW\t\tS3, 8 (SP)", "OR\t\tS1, A2, R0", 
    









      String.format("LUI\tS3, %04X    ", new Object[] {Integer.valueOf(mapNameAddress >>> 16) }), 
      String.format("ORI\tS3, S3, %04X", new Object[] {Integer.valueOf(mapNameAddress & 0xFFFF) }), ".nextchar", "LBU\tS2, 0 (S1)", "BEQ\tS2, R0, .terminate", "NOP", "ADDI\tS2, S2, FFE0", "SB\t\tS2, 0 (S3)", "ADDIU\tS1, S1, 1", "BEQ\tR0, R0, .nextchar", "ADDIU\tS3, S3, 1", ".terminate", "ORI\tS2, R0, FD", "SB\t\tS2, 0 (S3)", "LW\t\tS1, 0 (SP)", "LW\t\tS2, 4 (SP)", "LW\t\tS3, 8 (SP)", "J\t\t8005A7F8", "ADDIU\tSP, SP, C" });
  }
  





















  private static void writePrintDebugString(RandomAccessFile raf)
    throws IOException
  {
    fpPrintDebugString = Patcher.toAddress((int)raf.getFilePointer());
    assembleAndWrite(raf, new String[] { "ADDIU\tSP, SP, -24", "SW\t\tRA, 18 (SP)", "SW\t\tS0, 1C (SP)", "SWC1\tF0, 20 (SP)", "LUI\tA1, 800A", "LW\t\tA1, A600 (A1)", "ORI\tA2, R0, 5", "BEQ\tA1, A2, .DrawBattleString", "NOP", ".DrawMapString", 
    















      String.format("LUI\tS0, %04X    ", new Object[] {Integer.valueOf(mapStringAddress >>> 16) }), 
      String.format("ORI\tS0, S0, %04X", new Object[] {Integer.valueOf(mapStringAddress & 0xFFFF) }), "LUI\tA0, 8010", "ORI\tA0, A0, EFF0", "LWC1\tF0, 0 (A0)", "CVT.W.S\tF0, F0", "MFC1\tA1, F0", 
      







      String.format("ADDIU A0, S0, %X", new Object[] {Integer.valueOf(10) }), 
      String.format("JAL   %08X", new Object[] {Integer.valueOf(fpStoreWordAsHexString) }), "NOP", "LUI\tA0, 8010", "ORI\tA0, A0, EFF0", "LWC1\tF0, 4 (A0)", "CVT.W.S\tF0, F0", "MFC1\tA1, F0", 
      








      String.format("ADDIU A0, S0, %X", new Object[] {Integer.valueOf(23) }), 
      String.format("JAL   %08X", new Object[] {Integer.valueOf(fpStoreWordAsHexString) }), "NOP", "LUI\tA0, 8010", "ORI\tA0, A0, EFF0", "LWC1\tF0, 8 (A0)", "CVT.W.S\tF0, F0", "MFC1\tA1, F0", 
      








      String.format("ADDIU A0, S0, %X", new Object[] {Integer.valueOf(36) }), 
      String.format("JAL   %08X", new Object[] {Integer.valueOf(fpStoreWordAsHexString) }), "NOP", "DADDU \tA0, S0, R0", "ORI \tA1, R0, 10", "ORI \tA2, R0, CC", "ORI \tA3, R0, FF", "ORI\tV0, R0, 0", "SW \tV0, 10 (SP)", "ORI\tV0, R0, 0", "SW \tV0, 14 (SP)", "JAL\t801264EC", "NOP", "BEQ\tR0, R0, .Done", "NOP", ".DrawBattleString", 
      
















      String.format("LUI\tA0, %04X    ", new Object[] {Integer.valueOf(battleStringAddress >>> 16) }), 
      String.format("ORI\tA0, A0, %04X", new Object[] {Integer.valueOf(battleStringAddress & 0xFFFF) }), "ORI \tA1, R0, D0", "ORI \tA2, R0, CC", "ORI \tA3, R0, FF", "ORI\tV0, R0, 0", "SW \tV0, 10 (SP)", "ORI\tV0, R0, 0", "SW \tV0, 14 (SP)", "JAL\t801264EC", "NOP", ".Done", "LW\t\tRA, 18 (SP)", "LW\t\tS0, 1C (SP)", "LWC1\tF0, 20 (SP)", "ADDIU\tSP, SP, 24", "LW\t\tRA, 8C (SP)", "LW\t\tS8, 88 (SP)", "J\t\t800E92B0", "NOP" });
  }
  




















  private static void writeStoreBattleID(RandomAccessFile raf)
    throws IOException
  {
    int battleIDAddress = battleStringAddress + 15;
    fpStoreBattleID = Patcher.toAddress((int)raf.getFilePointer());
    assembleAndWrite(raf, new String[] { "ADDIU\tSP, SP, -4", "SW\t\tRA, 0 (SP)", 
    


      String.format("LUI\tAT, %04X    ", new Object[] {Integer.valueOf(battleIDAddress >>> 16) }), 
      String.format("ORI\tAT, AT, %04X", new Object[] {Integer.valueOf(battleIDAddress & 0xFFFF) }), "OR\t\tT0, A0, R0", 
      

      String.format("JAL   %08X", new Object[] {Integer.valueOf(fpGetCharAddress) }), "ANDI\tT1, T0, 0F", "SB\t\tT2, 4 (AT)", "SRL\tT0, T0, 4", 
      



      String.format("JAL   %08X", new Object[] {Integer.valueOf(fpGetCharAddress) }), "ANDI\tT1, T0, 0F", "SB\t\tT2, 3 (AT)", "SRL\tT0, T0, 4", 
      



      String.format("JAL   %08X", new Object[] {Integer.valueOf(fpGetCharAddress) }), "ANDI\tT1, T0, 0F", "SB\t\tT2, 1 (AT)", "SRL\tT0, T0, 4", 
      



      String.format("JAL   %08X", new Object[] {Integer.valueOf(fpGetCharAddress) }), "ANDI\tT1, T0, 0F", "SB\t\tT2, 0 (AT)", "LW\t\tRA, 0 (SP)", "ADDIU\tSP, SP, 4", "ADDIU\tSP, SP, FFE8", "J\t\t80072C98", "LUI\tAT, 800E" });
  }
  










  private static void writeDebugLoading(RandomAccessFile raf)
    throws IOException
  {
    int ptrMapName = Patcher.toAddress((int)raf.getFilePointer());
    String mapName = currentModconfig.getString(Options.TestMapName);
    raf.write(mapName.getBytes());
    raf.write(0);
    raf.seek((int)raf.getFilePointer() + 3 & 0xFFFFFFFC);
    
    int ptrBattleID = Patcher.toAddress((int)raf.getFilePointer());
    String battleID = currentModconfig.getString(Options.TestBattleID);
    if ((battleID.length() > 4) || (!DataUtils.isInteger(battleID)))
      battleID = TestBattleIDdefaultValue;
    raf.writeShort(Integer.parseInt(battleID, 16));
    raf.writeShort(-1);
    
    fpLoadDebugMap = Patcher.toAddress((int)raf.getFilePointer());
    assembleAndWrite(raf, new String[] { "LUI\tV0, 800B", "ORI\tV0, V0, 0ED4", "LW\t\tV0, 0 (V0)", "LUI\tV1, 0800", "AND\tV0, V0, V1", "BNE\tV0, R0, C", "NOP", "J\t\t802CA304", "DADDU\tA1, R0, R0", "ADDIU\tSP, SP, FFD0", "SW\t\tS0, 18 (SP)", "SW\t\tS1, 1C (SP)", "SW\t\tS2, 20 (SP)", "SW\t\tS3, 24 (SP)", "SW\t\tS4, 28 (SP)", "SW\t\tRA, 2C (SP)", "DADDU\tS2, A0, R0", "DADDU\tS4, A1, R0", 
    



















      String.format("LUI\tA0, %04X    ", new Object[] {Integer.valueOf(ptrMapName >>> 16) }), 
      String.format("ORI\tA0, A0, %04X", new Object[] {Integer.valueOf(ptrMapName & 0xFFFF) }), "ADDIU\tA1, SP, 10", "JAL\t8005ABF8", "ADDIU\tA2, SP, 12", "LUI\tV0, 8007", "ADDIU\tV0, V0, 419C", "LW\t\tV1, 0 (V0)", "LHU\tV0, 10 (SP)", "SH\t\tV0, 86 (V1)", "LHU\tV0, 12 (SP)", "SH\t\tV0, 8C (V1)", "SH\t\tV0, 8E (V1)", "J\t\t802CA3CC", "DADDU\tS3, R0, R0" });
    














    fpLoadDebugBattle = Patcher.toAddress((int)raf.getFilePointer());
    assembleAndWrite(raf, new String[] { "LUI\tV0, 800B", "ORI\tV0, V0, 0ED4", "LW\t\tV0, 0 (V0)", "LUI\tV1, 0800", "AND\tV0, V0, V1", "BEQ\tV0, R0, .Done", "NOP", 
    






      String.format("LUI\tV0, %04X", new Object[] {Integer.valueOf(ptrBattleID >>> 16) }), 
      String.format("LW\tV0, %04X (V0)", new Object[] {Integer.valueOf(ptrBattleID & 0xFFFF) }), "SW\t\tV0, 44 (S4)", ".Done", "LH\t\tA0, 46 (S4)", "LUI\tAT, 800E", "J\t\t800417F4", "SW\t\tA0, C4EC (AT)" });
  }
}
