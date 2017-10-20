package main.config;











public enum Options
{
  ModPath(Scope.Main, Type.String, "ModPath", null), 
  RomPath(Scope.Main, Type.String, "RomPath", null), 
  Dumped(Scope.Main, Type.Boolean, "Dumped", "False"), 
  LogDetails(Scope.Main, Type.Boolean, "LogDetails", "False"), 
  

  CleanDump(Scope.Dump, Type.Boolean, "FullDump", "False", "Clear existing dump directory", "Clears the existing dump directory and dumps all assets."), 
  
  DumpReports(Scope.Dump, Type.Boolean, "DumpReports", "True", "Create reports on dumped content", "Enables gathering and writing reports on metadata tracked during the dumping process."), 
  
  DumpProfiling(Scope.Dump, Type.Boolean, "DumpProfiling", "False", "Profile map dumping performance", "Prints performance profiles for map dumping."), 
  
  DumpStrings(Scope.Dump, Type.Boolean, "DumpStrings", "True", "Strings", ""), 
  DumpTables(Scope.Dump, Type.Boolean, "DumpTables", "True", "Tables", ""), 
  DumpMaps(Scope.Dump, Type.Boolean, "DumpMaps", "True", "Map Data", "Includes configuration files, scripts, and maps."), 
  
  DumpBattles(Scope.Dump, Type.Boolean, "DumpBattles", "True", "Battle Scripts", "Includes configuration files and scripts."), 
  
  DumpMoves(Scope.Dump, Type.Boolean, "DumpMoves", "True", "Move Scripts", "Also includes Star Powers, Partner modes, and item scripts."), 
  
  DumpTextures(Scope.Dump, Type.Boolean, "DumpTextures", "True", "Textures", ""), 
  DumpIcons(Scope.Dump, Type.Boolean, "DumpIcons", "True", "Icons", ""), 
  DumpSprites(Scope.Dump, Type.Boolean, "DumpSprites", "True", "Spritesheets", ""), 
  
  PrintLineOffsets(Scope.Dump, Type.Boolean, "PrintLineOffsets", "True", "Print line offsets", "Print memory offsets before each line for scripts and functions."), 
  
  UseTabIndents(Scope.Dump, Type.Boolean, "UseTabIndents", "True", "Use tabs for indentation", "Indent scripts using tab characters rather than spaces."), 
  
  PrintRequiredBy(Scope.Dump, Type.Boolean, "PrintRequiredBy", "False", "Print \"RequiredBy\" Annotations", ""), 
  

  CompressBattleData(Scope.Patch, Type.Boolean, "CompressBattleData", "True", "Compress Battle Data", "(Recommended) This option saves space by compressing battle data and modifying the battle loading code."), 
  
  EnableDebugCode(Scope.Patch, Type.Boolean, "EnableDebugCode", "False", "Enable Debug Information", "This option will print map and battle information to the screen during gameplay."), 
  

  ClearJapaneseStrings(Scope.Patch, Type.Boolean, "ClearJapaneseStrings", "True", "Clear SJIS Strings", "(Recommended) This option will clear the memory used by unused SJIS (japanese) strings, making extra room for new data."), 
  
  BuildTextures(Scope.Patch, Type.Boolean, "BuildTextures", "False", "Build Texture Archives", "Only use this option if you are using modified or custom textures."), 
  
  BuildBackgrounds(Scope.Patch, Type.Boolean, "BuildBackgrounds", "False", "Build Backgrounds", "Only use this option if you are using modified or custom backgrounds."), 
  
  BuildSpriteSheets(Scope.Patch, Type.Boolean, "BuildSpriteSheets", "False", "Build Sprite Sheets", "Only use this option if you are using modified or custom spritesheets."), 
  

  SkipIntroLogos(Scope.Patch, Type.Boolean, "SkipIntroLogos", "True", "Skip Intro Logos", "Developer logos will not appear during boot."), 
  
  DisableDemoReel(Scope.Patch, Type.Boolean, "DisableDemoReel", "True", "Disable Demo Reel", "Demo reel will not play from title screen."), 
  

  TestMapName(Scope.Patch, Type.String, "TestMapName", "machi"), 
  TestBattleID(Scope.Patch, Type.String, "TestBattleID", "0000"), 
  
  CheckScriptSyntax(Scope.Patch, Type.Boolean, "CheckScriptSyntax", "True"), 
  
  ModVersionString(Scope.Patch, Type.String, "ModVersionString", "Paper Mario Mod"), 
  
  CompressModPackage(Scope.Patch, Type.Boolean, "CompressModPackage", "True", "Compress Mod Package", "Uses Yay0 to compress the final diff file for your mod. It may take several minutes."), 
  


  UndoLimit(Scope.Editor, Type.Integer, "UndoLimit", "32"), 
  AngleSnap(Scope.Editor, Type.Float, "AngleSnap", "15.0"), 
  RecentMap0(Scope.Editor, Type.String, "RecentMap0", null), 
  RecentMap1(Scope.Editor, Type.String, "RecentMap1", null), 
  RecentMap2(Scope.Editor, Type.String, "RecentMap2", null), 
  RecentMap3(Scope.Editor, Type.String, "RecentMap3", null), 
  RecentMap4(Scope.Editor, Type.String, "RecentMap4", null), 
  RecentMap5(Scope.Editor, Type.String, "RecentMap5", null);
  

  public final Scope scope;
  
  public final Type type;
  
  public final String key;
  
  public final String defaultValue;
  public final String checkBoxLabel;
  public final String checkBoxDesc;
  
  private Options(Scope scope, Type type, String key, String defaultValue)
  {
    this.key = key;
    this.defaultValue = defaultValue;
    this.scope = scope;
    this.type = type;
    checkBoxLabel = null;
    checkBoxDesc = null;
  }
  


  private Options(Scope scope, Type type, String key, String defaultValue, String checkBoxLabel, String checkBoxDesc)
  {
    this.key = key;
    this.defaultValue = defaultValue;
    this.checkBoxLabel = checkBoxLabel;
    this.checkBoxDesc = checkBoxDesc;
    this.scope = scope;
    this.type = type;
  }
  
  public void setToDefault(Config cfg)
  {
    switch (1.$SwitchMap$main$config$Options$Type[type.ordinal()]) {
    case 1: 
      cfg.setBoolean(this, defaultValue); break;
    case 2:  cfg.setInteger(this, defaultValue); break;
    case 3:  cfg.setFloat(this, defaultValue); break;
    case 4:  cfg.setString(this, defaultValue);
    }
  }
  
  public static enum Scope
  {
    Main, 
    Editor, 
    Dump, 
    Patch;
    
    private Scope() {}
  }
  
  public static enum Type { Boolean, 
    Integer, 
    Float, 
    String;
    
    private Type() {}
  }
}
