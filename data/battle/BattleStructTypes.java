package data.battle;

import data.battle.struct.Actor;
import data.battle.struct.DefenseTable;
import data.battle.struct.DmaArgTable;
import data.battle.struct.ForegroundList;
import data.battle.struct.Formation;
import data.battle.struct.FormationTable;
import data.battle.struct.IdleAnimations;
import data.battle.struct.IntVector;
import data.battle.struct.SpecialFormation;
import data.battle.struct.SpriteTable;
import data.battle.struct.Stage;
import data.battle.struct.StageTable;
import data.battle.struct.StatusTable;
import data.battle.struct.StringSJIS;
import data.shared.SharedStructTypes;
import data.shared.struct.StructType;
import data.shared.struct.TypeMap;























public class BattleStructTypes
{
  public BattleStructTypes() {}
  
  public static final TypeMap battleTypes = new TypeMap();
  static { battleTypes.add(SharedStructTypes.sharedTypes);
    
    UseScriptT = new StructType(battleTypes, "Script_Use", SharedStructTypes.ScriptT);
    
    DmaArgTableT = new StructType(battleTypes, "DmaArgTable", 2);
    StageTableT = new StructType(battleTypes, "StageTable", 2);
    StageT = new StructType(battleTypes, "Stage");
    ForegroundListT = new StructType(battleTypes, "ForegroundModelList");
    SjisT = new StructType(battleTypes, "SJIS");
    
    FormationTableT = new StructType(battleTypes, "FormationTable", 2);
    IndexedFormationT = new StructType(battleTypes, "Formation");
    SpecialFormationT = new StructType(battleTypes, "SpecialFormation");
    Vector3dT = new StructType(battleTypes, "Vector3D");
    
    ActorT = new StructType(battleTypes, "Actor");
    StatusTableT = new StructType(battleTypes, "StatusTable");
    DefenseTableT = new StructType(battleTypes, "DefenseTable");
    SpriteTableT = new StructType(battleTypes, "SpriteTable");
    IdleAnimationsT = new StructType(battleTypes, "IdleAnimations");
    
    DmaArgTableT.bind(DmaArgTable.instance);
    StageTableT.bind(StageTable.instance);
    StageT.bind(Stage.instance);
    ForegroundListT.bind(ForegroundList.instance);
    SjisT.bind(StringSJIS.instance);
    
    FormationTableT.bind(FormationTable.instance);
    IndexedFormationT.bind(Formation.instance);
    SpecialFormationT.bind(SpecialFormation.instance);
    Vector3dT.bind(IntVector.instance);
    
    ActorT.bind(Actor.instance);
    StatusTableT.bind(StatusTable.instance);
    DefenseTableT.bind(DefenseTable.instance);
    SpriteTableT.bind(SpriteTable.instance);
    IdleAnimationsT.bind(IdleAnimations.instance);
  }
  
  public static final StructType UseScriptT;
  public static final StructType DmaArgTableT;
  public static final StructType StageTableT;
  public static final StructType StageT;
  public static final StructType ForegroundListT;
  public static final StructType SjisT;
  public static final StructType FormationTableT;
  public static final StructType IndexedFormationT;
  public static final StructType SpecialFormationT;
  public static final StructType Vector3dT;
  public static final StructType ActorT;
  public static final StructType StatusTableT;
  public static final StructType DefenseTableT;
  public static final StructType SpriteTableT;
  public static final StructType IdleAnimationsT;
}
