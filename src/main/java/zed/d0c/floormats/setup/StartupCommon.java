package zed.d0c.floormats.setup;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import zed.d0c.floormats.FloorMats;
import zed.d0c.floormats.blocks.floormats.Camouflage_FloorMat_Block;

import java.util.Objects;

// @Mod.EventBusSubscriber(modid = FloorMats.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StartupCommon
{
    public static Camouflage_FloorMat_Block blockCamouflage;  // this holds the unique instance of your block
    public static BlockItem itemBlockCamouflage;  // this holds the unique instance of the ItemBlock corresponding to your block

    @SubscribeEvent
    public static void onBlocksRegistration(final RegistryEvent.Register<Block> blockRegisterEvent) {
        blockCamouflage = (Camouflage_FloorMat_Block)(new Camouflage_FloorMat_Block().setRegistryName(FloorMats.MODID, "camouflage_floormat_block"));
        blockRegisterEvent.getRegistry().register(blockCamouflage);
    }

    @SubscribeEvent
    public static void onItemsRegistration(final RegistryEvent.Register<Item> itemRegisterEvent) {
        // We need to create a BlockItem so the player can carry this block in their hand and it can appear in the inventory
        final int MAXIMUM_STACK_SIZE = 64;  // player can hold 64 of this block in their hand at once

        Item.Properties itemProperties = new Item.Properties()
                .maxStackSize(MAXIMUM_STACK_SIZE)
                .group(ItemGroup.BUILDING_BLOCKS);  // which inventory tab?
        itemBlockCamouflage = new BlockItem(blockCamouflage, itemProperties);
        itemBlockCamouflage.setRegistryName(FloorMats.MODID,"camouflage_floormat_item");
        itemRegisterEvent.getRegistry().register(itemBlockCamouflage);

    }

}