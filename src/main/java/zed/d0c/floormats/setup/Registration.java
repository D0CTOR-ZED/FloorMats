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
import zed.d0c.floormats.blocks.floormats.*;

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

    public static final RegistryObject<Oak_FloorMat_Block> OAK_FLOORMAT_BLOCK = BLOCKS.register("oak_floormat_block", Oak_FloorMat_Block::new);
    public static final RegistryObject<Item> OAK_FLOORMAT_ITEM = ITEMS.register("oak_floormat_item", () -> new BlockItem(OAK_FLOORMAT_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Spruce_FloorMat_Block> SPRUCE_FLOORMAT_BLOCK = BLOCKS.register("spruce_floormat_block", Spruce_FloorMat_Block::new);
    public static final RegistryObject<Item> SPRUCE_FLOORMAT_ITEM = ITEMS.register("spruce_floormat_item", () -> new BlockItem(SPRUCE_FLOORMAT_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Birch_FloorMat_Block> BIRCH_FLOORMAT_BLOCK = BLOCKS.register("birch_floormat_block", Birch_FloorMat_Block::new);
    public static final RegistryObject<Item> BIRCH_FLOORMAT_ITEM = ITEMS.register("birch_floormat_item", () -> new BlockItem(BIRCH_FLOORMAT_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Jungle_FloorMat_Block> JUNGLE_FLOORMAT_BLOCK = BLOCKS.register("jungle_floormat_block", Jungle_FloorMat_Block::new);
    public static final RegistryObject<Item> JUNGLE_FLOORMAT_ITEM = ITEMS.register("jungle_floormat_item", () -> new BlockItem(JUNGLE_FLOORMAT_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<DarkOak_FloorMat_Block> DARKOAK_FLOORMAT_BLOCK = BLOCKS.register("darkoak_floormat_block", DarkOak_FloorMat_Block::new);
    public static final RegistryObject<Item> DARKOAK_FLOORMAT_ITEM = ITEMS.register("darkoak_floormat_item", () -> new BlockItem(DARKOAK_FLOORMAT_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Acacia_FloorMat_Block> ACACIA_FLOORMAT_BLOCK = BLOCKS.register("acacia_floormat_block", Acacia_FloorMat_Block::new);
    public static final RegistryObject<Item> ACACIA_FLOORMAT_ITEM = ITEMS.register("acacia_floormat_item", () -> new BlockItem(ACACIA_FLOORMAT_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Stone_FloorMat_Block> STONE_FLOORMAT_BLOCK = BLOCKS.register("stone_floormat_block", Stone_FloorMat_Block::new);
    public static final RegistryObject<Item> STONE_FLOORMAT_ITEM = ITEMS.register("stone_floormat_item", () -> new BlockItem(STONE_FLOORMAT_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Iron_FloorMat_Block> IRON_FLOORMAT_BLOCK = BLOCKS.register("iron_floormat_block", Iron_FloorMat_Block::new);
    public static final RegistryObject<Item> IRON_FLOORMAT_ITEM = ITEMS.register("iron_floormat_item", () -> new BlockItem(IRON_FLOORMAT_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

    public static final RegistryObject<Gold_FloorMat_Block> GOLD_FLOORMAT_BLOCK = BLOCKS.register("gold_floormat_block", Gold_FloorMat_Block::new);
    public static final RegistryObject<Item> GOLD_FLOORMAT_ITEM = ITEMS.register("gold_floormat_item", () -> new BlockItem(GOLD_FLOORMAT_BLOCK.get(), new Item.Properties().group(ModSetup.ITEM_GROUP)));

}
