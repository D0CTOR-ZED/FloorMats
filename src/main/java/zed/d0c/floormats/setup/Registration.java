package zed.d0c.floormats.setup;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import zed.d0c.floormats.FloorMats;
import zed.d0c.floormats.blocks.connected.*;
import zed.d0c.floormats.blocks.floormats.*;
import zed.d0c.floormats.items.ConnectedBlockItem;
import zed.d0c.floormats.items.FloorMatItem;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
public class Registration {

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, FloorMats.MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, FloorMats.MODID);
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, FloorMats.MODID);

    public static void init() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<SoundEvent> FLOORMATS_MARKED = SOUNDS.register("floormats.marked", () -> new SoundEvent(new ResourceLocation(FloorMats.MODID,"floormats.marked")));
    public static final RegistryObject<SoundEvent> FLOORMATS_LINKED = SOUNDS.register("floormats.linked", () -> new SoundEvent(new ResourceLocation(FloorMats.MODID,"floormats.linked")));
    public static final RegistryObject<SoundEvent> FLOORMATS_UNLINKED = SOUNDS.register("floormats.unlinked", () -> new SoundEvent(new ResourceLocation(FloorMats.MODID,"floormats.unlinked")));
    public static final RegistryObject<SoundEvent> FLOORMATS_DENIED = SOUNDS.register("floormats.denied", () -> new SoundEvent(new ResourceLocation(FloorMats.MODID,"floormats.denied")));
    public static final RegistryObject<SoundEvent> FLOORMATS_WRENCHED = SOUNDS.register("floormats.wrenched", () -> new SoundEvent(new ResourceLocation(FloorMats.MODID,"floormats.wrenched")));

    // Wooden Floor Mats

    public static final RegistryObject<Wooden_FloorMat_Block> OAK_FLOORMAT_BLOCK = BLOCKS.register("oak_floormat_block", Wooden_FloorMat_Block::new);
    public static final RegistryObject<Item> OAK_FLOORMAT_ITEM = ITEMS.register("oak_floormat_item", () -> new FloorMatItem(OAK_FLOORMAT_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Wooden_FloorMat_Block> SPRUCE_FLOORMAT_BLOCK = BLOCKS.register("spruce_floormat_block", Wooden_FloorMat_Block::new);
    public static final RegistryObject<Item> SPRUCE_FLOORMAT_ITEM = ITEMS.register("spruce_floormat_item", () -> new FloorMatItem(SPRUCE_FLOORMAT_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Wooden_FloorMat_Block> BIRCH_FLOORMAT_BLOCK = BLOCKS.register("birch_floormat_block", Wooden_FloorMat_Block::new);
    public static final RegistryObject<Item> BIRCH_FLOORMAT_ITEM = ITEMS.register("birch_floormat_item", () -> new FloorMatItem(BIRCH_FLOORMAT_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Wooden_FloorMat_Block> JUNGLE_FLOORMAT_BLOCK = BLOCKS.register("jungle_floormat_block", Wooden_FloorMat_Block::new);
    public static final RegistryObject<Item> JUNGLE_FLOORMAT_ITEM = ITEMS.register("jungle_floormat_item", () -> new FloorMatItem(JUNGLE_FLOORMAT_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Wooden_FloorMat_Block> DARKOAK_FLOORMAT_BLOCK = BLOCKS.register("darkoak_floormat_block", Wooden_FloorMat_Block::new);
    public static final RegistryObject<Item> DARKOAK_FLOORMAT_ITEM = ITEMS.register("darkoak_floormat_item", () -> new FloorMatItem(DARKOAK_FLOORMAT_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Wooden_FloorMat_Block> ACACIA_FLOORMAT_BLOCK = BLOCKS.register("acacia_floormat_block", Wooden_FloorMat_Block::new);
    public static final RegistryObject<Item> ACACIA_FLOORMAT_ITEM = ITEMS.register("acacia_floormat_item", () -> new FloorMatItem(ACACIA_FLOORMAT_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    // Gilded Floor Mats

    public static final RegistryObject<Gilded_Wood_FloorMat_Block> GILDED_OAK_FLOORMAT_BLOCK = BLOCKS.register("gilded/gilded_oak_floormat_block", Gilded_Wood_FloorMat_Block::new);
    public static final RegistryObject<Item> GILDED_OAK_FLOORMAT_ITEM = ITEMS.register("gilded/gilded_oak_floormat_item", () -> new FloorMatItem(GILDED_OAK_FLOORMAT_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Gilded_Wood_FloorMat_Block> GILDED_SPRUCE_FLOORMAT_BLOCK = BLOCKS.register("gilded/gilded_spruce_floormat_block", Gilded_Wood_FloorMat_Block::new);
    public static final RegistryObject<Item> GILDED_SPRUCE_FLOORMAT_ITEM = ITEMS.register("gilded/gilded_spruce_floormat_item", () -> new FloorMatItem(GILDED_SPRUCE_FLOORMAT_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Gilded_Wood_FloorMat_Block> GILDED_BIRCH_FLOORMAT_BLOCK = BLOCKS.register("gilded/gilded_birch_floormat_block", Gilded_Wood_FloorMat_Block::new);
    public static final RegistryObject<Item> GILDED_BIRCH_FLOORMAT_ITEM = ITEMS.register("gilded/gilded_birch_floormat_item", () -> new FloorMatItem(GILDED_BIRCH_FLOORMAT_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Gilded_Wood_FloorMat_Block> GILDED_JUNGLE_FLOORMAT_BLOCK = BLOCKS.register("gilded/gilded_jungle_floormat_block", Gilded_Wood_FloorMat_Block::new);
    public static final RegistryObject<Item> GILDED_JUNGLE_FLOORMAT_ITEM = ITEMS.register("gilded/gilded_jungle_floormat_item", () -> new FloorMatItem(GILDED_JUNGLE_FLOORMAT_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Gilded_Wood_FloorMat_Block> GILDED_DARKOAK_FLOORMAT_BLOCK = BLOCKS.register("gilded/gilded_darkoak_floormat_block", Gilded_Wood_FloorMat_Block::new);
    public static final RegistryObject<Item> GILDED_DARKOAK_FLOORMAT_ITEM = ITEMS.register("gilded/gilded_darkoak_floormat_item", () -> new FloorMatItem(GILDED_DARKOAK_FLOORMAT_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Gilded_Wood_FloorMat_Block> GILDED_ACACIA_FLOORMAT_BLOCK = BLOCKS.register("gilded/gilded_acacia_floormat_block", Gilded_Wood_FloorMat_Block::new);
    public static final RegistryObject<Item> GILDED_ACACIA_FLOORMAT_ITEM = ITEMS.register("gilded/gilded_acacia_floormat_item", () -> new FloorMatItem(GILDED_ACACIA_FLOORMAT_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    // Other Floor Mats

    public static final RegistryObject<Stone_FloorMat_Block> STONE_FLOORMAT_BLOCK = BLOCKS.register("stone_floormat_block", Stone_FloorMat_Block::new);
    public static final RegistryObject<Item> STONE_FLOORMAT_ITEM = ITEMS.register("stone_floormat_item", () -> new FloorMatItem(STONE_FLOORMAT_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Iron_FloorMat_Block> IRON_FLOORMAT_BLOCK = BLOCKS.register("iron_floormat_block", Iron_FloorMat_Block::new);
    public static final RegistryObject<Item> IRON_FLOORMAT_ITEM = ITEMS.register("iron_floormat_item", () -> new FloorMatItem(IRON_FLOORMAT_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Gold_FloorMat_Block> GOLD_FLOORMAT_BLOCK = BLOCKS.register("gold_floormat_block", Gold_FloorMat_Block::new);
    public static final RegistryObject<Item> GOLD_FLOORMAT_ITEM = ITEMS.register("gold_floormat_item", () -> new FloorMatItem(GOLD_FLOORMAT_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

/*
    public static final RegistryObject<Camouflage_FloorMat_Block> CAMOUFLAGE_FLOORMAT_BLOCK = BLOCKS.register("camouflage_floormat_block", Camouflage_FloorMat_Block::new);
    public static final RegistryObject<Item> CAMOUFLAGE_FLOORMAT_ITEM = ITEMS.register("camouflage_floormat_item", () -> new FloorMatItem(CAMOUFLAGE_FLOORMAT_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));
*/

    // Immersive Floor Mats

    public static final RegistryObject<Wooden_FloorMat_Block> TREATED_WOOD_FLOORMAT_BLOCK = BLOCKS.register("immersive/treated_wood_floormat_block", Wooden_FloorMat_Block::new);
    public static final RegistryObject<Item> TREATED_WOOD_FLOORMAT_ITEM = ITEMS.register("immersive/treated_wood_floormat_item", () -> new FloorMatItem(TREATED_WOOD_FLOORMAT_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Gilded_Wood_FloorMat_Block> GILDED_TREATED_WOOD_FLOORMAT_BLOCK = BLOCKS.register("immersive/gilded_treated_wood_floormat_block", Gilded_Wood_FloorMat_Block::new);
    public static final RegistryObject<Item> GILDED_TREATED_WOOD_FLOORMAT_ITEM = ITEMS.register("immersive/gilded_treated_wood_floormat_item", () -> new FloorMatItem(GILDED_TREATED_WOOD_FLOORMAT_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    // Nether Floor Mats

    public static final RegistryObject<Wooden_FloorMat_Block> CRIMSON_STEM_FLOORMAT_BLOCK = BLOCKS.register("nether/crimson_stem/crimson_stem_floormat_block", Wooden_FloorMat_Block::new);
    public static final RegistryObject<Item> CRIMSON_STEM_FLOORMAT_ITEM = ITEMS.register("nether/crimson_stem/crimson_stem_floormat_item", () -> new FloorMatItem(CRIMSON_STEM_FLOORMAT_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Gilded_Wood_FloorMat_Block> GILDED_CRIMSON_STEM_FLOORMAT_BLOCK = BLOCKS.register("nether/crimson_stem/gilded_crimson_stem_floormat_block", Gilded_Wood_FloorMat_Block::new);
    public static final RegistryObject<Item> GILDED_CRIMSON_STEM_FLOORMAT_ITEM = ITEMS.register("nether/crimson_stem/gilded_crimson_stem_floormat_item", () -> new FloorMatItem(GILDED_CRIMSON_STEM_FLOORMAT_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Wooden_FloorMat_Block> WARPED_STEM_FLOORMAT_BLOCK = BLOCKS.register("nether/warped_stem/warped_stem_floormat_block", Wooden_FloorMat_Block::new);
    public static final RegistryObject<Item> WARPED_STEM_FLOORMAT_ITEM = ITEMS.register("nether/warped_stem/warped_stem_floormat_item", () -> new FloorMatItem(WARPED_STEM_FLOORMAT_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Gilded_Wood_FloorMat_Block> GILDED_WARPED_STEM_FLOORMAT_BLOCK = BLOCKS.register("nether/warped_stem/gilded_warped_stem_floormat_block", Gilded_Wood_FloorMat_Block::new);
    public static final RegistryObject<Item> GILDED_WARPED_STEM_FLOORMAT_ITEM = ITEMS.register("nether/warped_stem/gilded_warped_stem_floormat_item", () -> new FloorMatItem(GILDED_WARPED_STEM_FLOORMAT_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    // Connected Wooden Log

    public static final RegistryObject<Wooden_Connected_Block> OAK_CONNECTED_BLOCK = BLOCKS.register("connected/wooden/connected_oak_log", Wooden_Connected_Block::new);
    public static final RegistryObject<Item> OAK_CONNECTED_ITEM = ITEMS.register("connected/wooden/connected_oak_log", () -> new ConnectedBlockItem(OAK_CONNECTED_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Wooden_Connected_Block> SPRUCE_CONNECTED_BLOCK = BLOCKS.register("connected/wooden/connected_spruce_log", Wooden_Connected_Block::new);
    public static final RegistryObject<Item> SPRUCE_CONNECTED_ITEM = ITEMS.register("connected/wooden/connected_spruce_log", () -> new ConnectedBlockItem(SPRUCE_CONNECTED_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Wooden_Connected_Block> BIRCH_CONNECTED_BLOCK = BLOCKS.register("connected/wooden/connected_birch_log", Wooden_Connected_Block::new);
    public static final RegistryObject<Item> BIRCH_CONNECTED_ITEM = ITEMS.register("connected/wooden/connected_birch_log", () -> new ConnectedBlockItem(BIRCH_CONNECTED_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Wooden_Connected_Block> JUNGLE_CONNECTED_BLOCK = BLOCKS.register("connected/wooden/connected_jungle_log", Wooden_Connected_Block::new);
    public static final RegistryObject<Item> JUNGLE_CONNECTED_ITEM = ITEMS.register("connected/wooden/connected_jungle_log", () -> new ConnectedBlockItem(JUNGLE_CONNECTED_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Wooden_Connected_Block> DARKOAK_CONNECTED_BLOCK = BLOCKS.register("connected/wooden/connected_darkoak_log", Wooden_Connected_Block::new);
    public static final RegistryObject<Item> DARKOAK_CONNECTED_ITEM = ITEMS.register("connected/wooden/connected_darkoak_log", () -> new ConnectedBlockItem(DARKOAK_CONNECTED_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Wooden_Connected_Block> ACACIA_CONNECTED_BLOCK = BLOCKS.register("connected/wooden/connected_acacia_log", Wooden_Connected_Block::new);
    public static final RegistryObject<Item> ACACIA_CONNECTED_ITEM = ITEMS.register("connected/wooden/connected_acacia_log", () -> new ConnectedBlockItem(ACACIA_CONNECTED_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    // Connected Gilded Logs

    public static final RegistryObject<Wooden_Connected_Block> GILDED_OAK_CONNECTED_BLOCK = BLOCKS.register("connected/gilded/connected_gilded_oak_log", Wooden_Connected_Block::new);
    public static final RegistryObject<Item> GILDED_OAK_CONNECTED_ITEM = ITEMS.register("connected/gilded/connected_gilded_oak_log", () -> new ConnectedBlockItem(GILDED_OAK_CONNECTED_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Wooden_Connected_Block> GILDED_SPRUCE_CONNECTED_BLOCK = BLOCKS.register("connected/gilded/connected_gilded_spruce_log", Wooden_Connected_Block::new);
    public static final RegistryObject<Item> GILDED_SPRUCE_CONNECTED_ITEM = ITEMS.register("connected/gilded/connected_gilded_spruce_log", () -> new ConnectedBlockItem(GILDED_SPRUCE_CONNECTED_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Wooden_Connected_Block> GILDED_BIRCH_CONNECTED_BLOCK = BLOCKS.register("connected/gilded/connected_gilded_birch_log", Wooden_Connected_Block::new);
    public static final RegistryObject<Item> GILDED_BIRCH_CONNECTED_ITEM = ITEMS.register("connected/gilded/connected_gilded_birch_log", () -> new ConnectedBlockItem(GILDED_BIRCH_CONNECTED_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Wooden_Connected_Block> GILDED_JUNGLE_CONNECTED_BLOCK = BLOCKS.register("connected/gilded/connected_gilded_jungle_log", Wooden_Connected_Block::new);
    public static final RegistryObject<Item> GILDED_JUNGLE_CONNECTED_ITEM = ITEMS.register("connected/gilded/connected_gilded_jungle_log", () -> new ConnectedBlockItem(GILDED_JUNGLE_CONNECTED_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Wooden_Connected_Block> GILDED_DARKOAK_CONNECTED_BLOCK = BLOCKS.register("connected/gilded/connected_gilded_darkoak_log", Wooden_Connected_Block::new);
    public static final RegistryObject<Item> GILDED_DARKOAK_CONNECTED_ITEM = ITEMS.register("connected/gilded/connected_gilded_darkoak_log", () -> new ConnectedBlockItem(GILDED_DARKOAK_CONNECTED_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Wooden_Connected_Block> GILDED_ACACIA_CONNECTED_BLOCK = BLOCKS.register("connected/gilded/connected_gilded_acacia_log", Wooden_Connected_Block::new);
    public static final RegistryObject<Item> GILDED_ACACIA_CONNECTED_ITEM = ITEMS.register("connected/gilded/connected_gilded_acacia_log", () -> new ConnectedBlockItem(GILDED_ACACIA_CONNECTED_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    // Connected Immersive Wood

    public static final RegistryObject<Wooden_Connected_Block> TREATED_WOOD_CONNECTED_BLOCK = BLOCKS.register("connected/immersive/connected_treated_wood", Wooden_Connected_Block::new);
    public static final RegistryObject<Item> TREATED_WOOD_CONNECTED_ITEM = ITEMS.register("connected/immersive/connected_treated_wood", () -> new ConnectedBlockItem(TREATED_WOOD_CONNECTED_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Wooden_Connected_Block> GILDED_TREATED_WOOD_CONNECTED_BLOCK = BLOCKS.register("connected/immersive/connected_gilded_treated_wood", Wooden_Connected_Block::new);
    public static final RegistryObject<Item> GILDED_TREATED_WOOD_CONNECTED_ITEM = ITEMS.register("connected/immersive/connected_gilded_treated_wood", () -> new ConnectedBlockItem(GILDED_TREATED_WOOD_CONNECTED_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    // Connected Nether Stem

    public static final RegistryObject<Wooden_Connected_Block> CRIMSON_STEM_CONNECTED_BLOCK = BLOCKS.register("connected/nether/connected_crimson_stem", Wooden_Connected_Block::new);
    public static final RegistryObject<Item> CRIMSON_STEM_CONNECTED_ITEM = ITEMS.register("connected/nether/connected_crimson_stem", () -> new ConnectedBlockItem(CRIMSON_STEM_CONNECTED_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Wooden_Connected_Block> GILDED_CRIMSON_STEM_CONNECTED_BLOCK = BLOCKS.register("connected/nether/connected_gilded_crimson_stem", Wooden_Connected_Block::new);
    public static final RegistryObject<Item> GILDED_CRIMSON_STEM_CONNECTED_ITEM = ITEMS.register("connected/nether/connected_gilded_crimson_stem", () -> new ConnectedBlockItem(GILDED_CRIMSON_STEM_CONNECTED_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Wooden_Connected_Block> WARPED_STEM_CONNECTED_BLOCK = BLOCKS.register("connected/nether/connected_warped_stem", Wooden_Connected_Block::new);
    public static final RegistryObject<Item> WARPED_STEM_CONNECTED_ITEM = ITEMS.register("connected/nether/connected_warped_stem", () -> new ConnectedBlockItem(WARPED_STEM_CONNECTED_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Wooden_Connected_Block> GILDED_WARPED_STEM_CONNECTED_BLOCK = BLOCKS.register("connected/nether/connected_gilded_warped_stem", Wooden_Connected_Block::new);
    public static final RegistryObject<Item> GILDED_WARPED_STEM_CONNECTED_ITEM = ITEMS.register("connected/nether/connected_gilded_warped_stem", () -> new ConnectedBlockItem(GILDED_WARPED_STEM_CONNECTED_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    // Connected Other Blocks

    public static final RegistryObject<Wooden_Connected_Block> GOLD_CONNECTED_BLOCK = BLOCKS.register("connected/connected_gold_block", Wooden_Connected_Block::new);
    public static final RegistryObject<Item> GOLD_CONNECTED_ITEM = ITEMS.register("connected/connected_gold_block", () -> new ConnectedBlockItem(GOLD_CONNECTED_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Wooden_Connected_Block> IRON_CONNECTED_BLOCK = BLOCKS.register("connected/connected_iron_block", Wooden_Connected_Block::new);
    public static final RegistryObject<Item> IRON_CONNECTED_ITEM = ITEMS.register("connected/connected_iron_block", () -> new ConnectedBlockItem(IRON_CONNECTED_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Wooden_Connected_Block> STONE_CONNECTED_BLOCK = BLOCKS.register("connected/connected_stone", Wooden_Connected_Block::new);
    public static final RegistryObject<Item> STONE_CONNECTED_ITEM = ITEMS.register("connected/connected_stone", () -> new ConnectedBlockItem(STONE_CONNECTED_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

}
