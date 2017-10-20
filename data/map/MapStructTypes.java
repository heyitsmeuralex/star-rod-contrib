package data.map;

import data.map.struct.EntryList;
import data.map.struct.Header;
import data.map.struct.ItemList;
import data.map.struct.LavaResetList;
import data.map.struct.TriggerCoord;
import data.map.struct.npc.AISettings;
import data.map.struct.npc.AnimationList;
import data.map.struct.npc.Npc;
import data.map.struct.npc.NpcGroup;
import data.map.struct.npc.NpcGroupList;
import data.map.struct.npc.NpcSettings;
import data.map.struct.shop.BadgeShopInventory;
import data.map.struct.shop.ShopInventory;
import data.map.struct.shop.ShopItemPositions;
import data.map.struct.shop.ShopOwner;
import data.map.struct.shop.ShopPriceList;
import data.map.struct.special.NpcList;
import data.map.struct.special.SearchBushEvent;
import data.map.struct.special.ShakeTreeEvent;
import data.map.struct.special.TreeDropList;
import data.map.struct.special.TreeEffectVectors;
import data.map.struct.special.TreeModelList;
import data.map.struct.special.TweesterPath;
import data.map.struct.special.TweesterPathList;
import data.shared.SharedStructTypes;
import data.shared.struct.StructType;
import data.shared.struct.TypeMap;








































public class MapStructTypes
{
  public MapStructTypes() {}
  
  public static final TypeMap mapTypes = new TypeMap();
  static { mapTypes.add(SharedStructTypes.sharedTypes);
    
    HeaderT = new StructType(mapTypes, "Header", 2);
    InitFunctionT = new StructType(mapTypes, "Function_Init", SharedStructTypes.FunctionT, 2);
    GetTattleFunctionT = new StructType(mapTypes, "Function_GetTattle", SharedStructTypes.FunctionT, 2);
    MainScriptT = new StructType(mapTypes, "Script_Main", SharedStructTypes.ScriptT, 2);
    EntryListT = new StructType(mapTypes, "EntryList", 2);
    
    NpcT = new StructType(mapTypes, "Npc");
    NpcGroupListT = new StructType(mapTypes, "NpcGroupList");
    NpcGroupT = new StructType(mapTypes, "NpcGroup");
    NpcSettingsT = new StructType(mapTypes, "NpcSettings");
    AISettingsT = new StructType(mapTypes, "AISettings", 1);
    ExtraAnimationListT = new StructType(mapTypes, "ExtraAnimationList");
    
    BadgeShopInventoryT = new StructType(mapTypes, "BadgeShopInventory");
    ShopOwnerT = new StructType(mapTypes, "ShopOwnerNPC");
    ShopItemPositionsT = new StructType(mapTypes, "ShopItemPositions", 1);
    ShopInventoryT = new StructType(mapTypes, "ShopInventory");
    ShopPriceListT = new StructType(mapTypes, "ShopPriceList");
    
    ItemListT = new StructType(mapTypes, "ItemList");
    TriggerCoordT = new StructType(mapTypes, "TriggerCoord");
    
    TweesterPathListT = new StructType(mapTypes, "TweesterPathList");
    TweesterPathT = new StructType(mapTypes, "TweesterPath");
    
    SearchBushEventT = new StructType(mapTypes, "SearchBushEvent");
    ShakeTreeEventT = new StructType(mapTypes, "ShakeTreeEvent");
    TreeModelListT = new StructType(mapTypes, "TreeModelList");
    TreeDropListT = new StructType(mapTypes, "TreeDropList");
    TreeEffectVectorsT = new StructType(mapTypes, "TreeEffectVectors");
    NpcListT = new StructType(mapTypes, "NpcList");
    
    LavaResetListT = new StructType(mapTypes, "LavaResetList");
    
    HeaderT.bind(Header.instance);
    EntryListT.bind(EntryList.instance);
    
    NpcT.bind(Npc.instance);
    
    NpcGroupListT.bind(NpcGroupList.instance);
    NpcGroupT.bind(NpcGroup.instance);
    NpcSettingsT.bind(NpcSettings.instance);
    AISettingsT.bind(AISettings.instance);
    ExtraAnimationListT.bind(AnimationList.instance);
    
    BadgeShopInventoryT.bind(BadgeShopInventory.instance);
    ShopOwnerT.bind(ShopOwner.instance);
    ShopItemPositionsT.bind(ShopItemPositions.instance);
    ShopInventoryT.bind(ShopInventory.instance);
    ShopPriceListT.bind(ShopPriceList.instance);
    
    ItemListT.bind(ItemList.instance);
    TriggerCoordT.bind(TriggerCoord.instance);
    
    TweesterPathListT.bind(TweesterPathList.instance);
    TweesterPathT.bind(TweesterPath.instance);
    
    SearchBushEventT.bind(SearchBushEvent.instance);
    ShakeTreeEventT.bind(ShakeTreeEvent.instance);
    TreeModelListT.bind(TreeModelList.instance);
    TreeDropListT.bind(TreeDropList.instance);
    TreeEffectVectorsT.bind(TreeEffectVectors.instance);
    NpcListT.bind(NpcList.instance);
    LavaResetListT.bind(LavaResetList.instance);
  }
  
  public static final StructType HeaderT;
  public static final StructType InitFunctionT;
  public static final StructType GetTattleFunctionT;
  public static final StructType MainScriptT;
  public static final StructType EntryListT;
  public static final StructType NpcGroupListT;
  public static final StructType NpcGroupT;
  public static final StructType NpcSettingsT;
  public static final StructType AISettingsT;
  public static final StructType ExtraAnimationListT;
  public static final StructType NpcT;
  public static final StructType BadgeShopInventoryT;
  public static final StructType ShopOwnerT;
  public static final StructType ShopItemPositionsT;
  public static final StructType ShopInventoryT;
  public static final StructType ShopPriceListT;
  public static final StructType ItemListT;
  public static final StructType TriggerCoordT;
  public static final StructType TweesterPathListT;
  public static final StructType TweesterPathT;
  public static final StructType SearchBushEventT;
  public static final StructType ShakeTreeEventT;
  public static final StructType TreeModelListT;
  public static final StructType TreeDropListT;
  public static final StructType TreeEffectVectorsT;
  public static final StructType NpcListT;
  public static final StructType LavaResetListT;
}
