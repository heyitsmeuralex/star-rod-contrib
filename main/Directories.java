package main;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.io.FileUtils;

public enum Directories
{
  DATABASE(Root.NONE, "/database/"), 
  DATABASE_TYPES(Root.NONE, "/database/types/"), 
  DATABASE_HINTS(Root.NONE, "/database/hints/"), 
  
  DEFAULTS_GLOBAL(Root.NONE, "/database/defaults/global/"), 
  DEFAULTS_MAP(Root.NONE, "/database/defaults/map/"), 
  DEFAULTS_BATTLE(Root.NONE, "/database/defaults/battle/"), 
  DEFAULTS_MOVE(Root.NONE, "/database/defaults/move/"), 
  DEFAULTS_ALLY(Root.NONE, "/database/defaults/partner/"), 
  DEFAULTS_ITEM(Root.NONE, "/database/defaults/item/"), 
  DEFAULTS_STARS(Root.NONE, "/database/defaults/starpower/"), 
  
  DUMP_REPORTS(Root.DUMP, "/reports/"), 
  DUMP_REQUESTS(Root.DUMP, "/reports/requests/"), 
  
  DUMP_MAP(Root.DUMP, "/map/"), 
  DUMP_MAP_YAY0(Root.DUMP, "/map/yay0/"), 
  DUMP_MAP_RAW(Root.DUMP, "/map/raw/"), 
  DUMP_MAP_SRC(Root.DUMP, "/map/src/"), 
  DUMP_MAP_NPC(Root.DUMP, "/map/npc/"), 
  
  DUMP_BATTLE(Root.DUMP, "/battle/"), 
  DUMP_BATTLE_RAW(Root.DUMP, "/battle/raw/"), 
  DUMP_BATTLE_SRC(Root.DUMP, "/battle/src/"), 
  DUMP_BATTLE_ENEMY(Root.DUMP, "/battle/enemy/"), 
  
  DUMP_MOVE(Root.DUMP, "/battle/misc/move/"), 
  DUMP_MOVE_RAW(Root.DUMP, "/battle/misc/move/raw/"), 
  DUMP_MOVE_SRC(Root.DUMP, "/battle/misc/move/src/"), 
  
  DUMP_ALLY(Root.DUMP, "/battle/misc/partner/"), 
  DUMP_ALLY_RAW(Root.DUMP, "/battle/misc/partner/raw/"), 
  DUMP_ALLY_SRC(Root.DUMP, "/battle/misc/partner/src/"), 
  
  DUMP_ITEM(Root.DUMP, "/battle/misc/item/"), 
  DUMP_ITEM_RAW(Root.DUMP, "/battle/misc/item/raw/"), 
  DUMP_ITEM_SRC(Root.DUMP, "/battle/misc/item/src/"), 
  
  DUMP_STARS(Root.DUMP, "/battle/misc/starpower/"), 
  DUMP_STARS_RAW(Root.DUMP, "/battle/misc/starpower/raw/"), 
  DUMP_STARS_SRC(Root.DUMP, "/battle/misc/starpower/src/"), 
  
  DUMP_IMG_TEX(Root.DUMP, "/image/texture/"), 
  DUMP_IMG_BG(Root.DUMP, "/image/bg/"), 
  DUMP_IMG_MISC(Root.DUMP, "/image/misc/"), 
  
  DUMP_ICON(Root.DUMP, "/image/icon/"), 
  DUMP_ICON_MENU(Root.DUMP, "/image/icon/menu/"), 
  DUMP_ICON_PARTNER(Root.DUMP, "/image/icon/partner/"), 
  DUMP_ICON_WORLD(Root.DUMP, "/image/icon/world/"), 
  
  DUMP_SPRITE(Root.DUMP, "/sprite/"), 
  DUMP_SPRITE_RAW(Root.DUMP, "/sprite/raw/"), 
  DUMP_SPRITE_SRC(Root.DUMP, "/sprite/src/"), 
  
  DUMP_GLOBALS(Root.DUMP, "/globals/"), 
  DUMP_STRINGS(Root.DUMP, "/strings/"), 
  
  DUMP_YAY0_DECODED(Root.DUMP, "/yay0/decoded/", true), 
  DUMP_YAY0_ENCODED(Root.DUMP, "/yay0/encoded/", true), 
  
  MOD_MAP(Root.MOD, "/map/"), 
  MOD_MAP_IMPORT(Root.MOD, "/map/import/"), 
  MOD_MAP_SRC(Root.MOD, "/map/src/"), 
  MOD_MAP_PATCH(Root.MOD, "/map/patch/"), 
  MOD_MAP_SAVE(Root.MOD, "/map/save/"), 
  MOD_MAP_BUILD(Root.MOD, "/map/build/"), 
  MOD_MAP_TEMP(Root.MOD, "/map/temp/", true), 
  
  MOD_BATTLE(Root.MOD, "/battle/"), 
  MOD_BATTLE_IMPORT(Root.MOD, "/battle/import/"), 
  MOD_BATTLE_ENEMY(Root.MOD, "/battle/enemy/"), 
  MOD_BATTLE_SRC(Root.MOD, "/battle/src/"), 
  MOD_BATTLE_PATCH(Root.MOD, "/battle/patch/"), 
  MOD_BATTLE_TEMP(Root.MOD, "/battle/temp/", true), 
  
  MOD_MOVE(Root.MOD, "/battle/misc/move/"), 
  MOD_MOVE_SRC(Root.MOD, "/battle/misc/move/src/"), 
  MOD_MOVE_PATCH(Root.MOD, "/battle/misc/move/patch/"), 
  MOD_MOVE_TEMP(Root.MOD, "/battle/misc/move/temp/", true), 
  
  MOD_ALLY(Root.MOD, "/battle/misc/partner/"), 
  MOD_ALLY_SRC(Root.MOD, "/battle/misc/partner/src/"), 
  MOD_ALLY_PATCH(Root.MOD, "/battle/misc/partner/patch/"), 
  MOD_ALLY_TEMP(Root.MOD, "/battle/misc/partner/temp/", true), 
  
  MOD_ITEM(Root.MOD, "/battle/misc/item/"), 
  MOD_ITEM_SRC(Root.MOD, "/battle/misc/item/src/"), 
  MOD_ITEM_PATCH(Root.MOD, "/battle/misc/item/patch/"), 
  MOD_ITEM_TEMP(Root.MOD, "/battle/misc/item/temp/", true), 
  
  MOD_STARS(Root.MOD, "/battle/misc/starpower/"), 
  MOD_STARS_SRC(Root.MOD, "/battle/misc/starpower/src/"), 
  MOD_STARS_PATCH(Root.MOD, "/battle/misc/starpower/patch/"), 
  MOD_STARS_TEMP(Root.MOD, "/battle/misc/starpower/temp/", true), 
  
  MOD_IMG_TEX(Root.MOD, "/image/texture/"), 
  MOD_IMG_BG(Root.MOD, "/image/bg/"), 
  MOD_IMG_MISC(Root.MOD, "/image/misc/"), 
  
  MOD_ICON_MENU(Root.MOD, "/image/icon/menu/"), 
  


  MOD_SPRITE(Root.MOD, "/sprite/"), 
  MOD_SPRITE_SRC(Root.MOD, "/sprite/src/"), 
  MOD_SPRITE_TEMP(Root.MOD, "/sprite/temp/"), 
  
  MOD_STRINGS(Root.MOD, "/strings/"), 
  
  MOD_GLOBALS(Root.MOD, "/globals/"), 
  MOD_PATCH(Root.MOD, "/globals/patch/"), 
  
  MOD_BACKUP(Root.MOD, "/backup/", true), 
  MOD_OUT(Root.MOD, "/out/");
  

  public static final String FN_BATTLE_SECTIONS = "BattleSections.txt";
  
  public static final String FN_BATTLE_ACTORS = "ActorTypes.xml";
  public static final String FN_BATTLE_MOVES = "Moves.txt";
  public static final String FN_BATTLE_ITEMS = "Items.txt";
  public static final String FN_SPRITE_TABLE = "Sprites.txt";
  public static final String FN_ASSET_TABLE = "AssetTable.txt";
  public static final String FN_MAP_TABLE = "MapTable.xml";
  public static final String FN_MOVE_TABLE = "MoveTable.csv";
  public static final String FN_ITEM_TABLE = "ItemTable.csv";
  public static final String FN_WORLD_MAP = "WorldMap.xml";
  private final Root root;
  private final String path;
  private final boolean optional;
  public static final List<Directories> requiredDumpDirectories;
  public static final List<Directories> requiredModDirectories;
  
  private Directories(Root root, String path)
  {
    this(root, path, false);
  }
  
  private Directories(Root root, String path, boolean optional)
  {
    this.root = root;
    this.path = path;
    this.optional = optional;
  }
  



  static
  {
    requiredDumpDirectories = new LinkedList();
    requiredModDirectories = new LinkedList();
    
    for (Directories dir : values())
    {
      if (!optional)
      {
        switch (1.$SwitchMap$main$Directories$Root[root.ordinal()]) {
        case 1: 
          requiredDumpDirectories.add(dir); break;
        case 2:  requiredModDirectories.add(dir);
        }
        
      }
    }
  }
  

  public String toString()
  {
    return getRootPath(root) + path;
  }
  


  public File toFile() { return new File(toString()); }
  
  private static enum Root {
    NONE,  DUMP,  MOD;
    
    private Root() {} }
  
  private static String getRootPath(Root root) { switch (1.$SwitchMap$main$Directories$Root[root.ordinal()]) {
    case 3: 
      return ".";
    case 1:  return dumpPath;
    case 2:  return modPath;
    }
    return null;
  }
  
  private static String dumpPath = null;
  private static String modPath = null;
  
  public static void setDumpDirectory(String path)
  {
    if (path.contains("\\"))
      path = path.replaceAll("\\\\", "/");
    if (path.endsWith("/"))
      path = path.substring(0, path.length() - 1);
    dumpPath = path;
  }
  
  public static String getDumpPath()
  {
    return dumpPath;
  }
  
  public static void setModDirectory(String path)
  {
    if (path.contains("\\"))
      path = path.replaceAll("\\\\", "/");
    if (path.endsWith("/"))
      path = path.substring(0, path.length() - 1);
    modPath = path;
    
    System.out.println("Using Mod Directory: " + modPath);
  }
  
  public static String getModPath()
  {
    return modPath;
  }
  
  public static void createDumpDirectories() throws IOException
  {
    if (dumpPath == null) {
      throw new IOException("Dump directory is not set.");
    }
    for (Directories dir : values())
    {
      if ((root == Root.DUMP) && (!optional)) {
        FileUtils.forceMkdir(dir.toFile());
      }
    }
  }
  
  public static void createModDirectories() throws IOException {
    if (modPath == null) {
      throw new IOException("Mod directory is not set.");
    }
    for (Directories dir : values())
    {
      if ((root == Root.MOD) && (!optional)) {
        FileUtils.forceMkdir(dir.toFile());
      }
    }
  }
  
  public static void copyIfMissing(Directories src, Directories dest, String filename) throws IOException {
    File srcFile = new File(src + filename);
    if (!srcFile.exists()) {
      throw new RuntimeException("Missing source file: " + srcFile.getCanonicalPath());
    }
    File destFile = new File(dest + filename);
    if (!destFile.exists()) {
      FileUtils.copyFile(srcFile, destFile);
    }
  }
  
  public static void copyIfEmpty(Directories src, Directories dest) throws IOException {
    copyIfEmpty(src, dest, false);
  }
  
  public static void copyIfEmpty(Directories src, Directories dest, boolean ignoreMissingSource) throws IOException
  {
    File srcDir = src.toFile();
    if (!srcDir.exists())
    {
      if (ignoreMissingSource) {
        return;
      }
      throw new RuntimeException("Missing source directory: " + srcDir.getCanonicalPath());
    }
    
    File destDir = dest.toFile();
    if (!destDir.exists()) {
      throw new RuntimeException("Missing target directory: " + destDir.getCanonicalPath());
    }
    if (destDir.list().length == 0) {
      FileUtils.copyDirectory(srcDir, destDir);
    }
  }
  
  public static void copyAllMissing(Directories src, Directories dest) throws IOException {
    copyAllMissing(src, dest, false);
  }
  
  public static void copyAllMissing(Directories src, Directories dest, boolean ignoreMissingSource) throws IOException
  {
    File srcDir = src.toFile();
    if (!srcDir.exists())
    {
      if (ignoreMissingSource) {
        return;
      }
      throw new RuntimeException("Missing source directory: " + srcDir.getCanonicalPath());
    }
    
    File destDir = dest.toFile();
    if (!destDir.exists()) {
      throw new RuntimeException("Missing target directory: " + destDir.getCanonicalPath());
    }
    copyAllMissing(srcDir, destDir);
  }
  
  private static void copyAllMissing(File srcDir, File destDir) throws IOException
  {
    for (File srcFile : srcDir.listFiles())
    {
      File destFile = new File(destDir.getAbsolutePath() + File.separator + srcFile.getName());
      
      if (srcFile.isDirectory())
      {
        if (!destFile.exists())
          FileUtils.forceMkdir(destFile);
        copyAllMissing(srcFile, destFile);


      }
      else if (!destFile.exists()) {
        FileUtils.copyFile(srcFile, destFile);
      }
    }
  }
}
