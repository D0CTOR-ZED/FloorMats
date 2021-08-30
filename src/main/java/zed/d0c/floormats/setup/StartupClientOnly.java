package zed.d0c.floormats.setup;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zed.d0c.floormats.blocks.floormats.Camouflage_FloorMat_Block;
import zed.d0c.floormats.render.CamouflageBakedModel;

import static zed.d0c.floormats.setup.StartupCommon.blockCamouflage;

public class StartupClientOnly
{
    // Called after all the other baked block models have been added to the modelRegistry
    // Allows us to manipulate the modelRegistry before BlockModelShapes caches them.
    @SubscribeEvent
    public static void onModelBakeEvent(ModelBakeEvent event)
    {
        // Find the existing mappings for CamouflageBakedModel - they will have been added automatically because
        //  of our blockstates file for the BlockCamouflage.
        // Replace the mapping with our CamouflageBakedModel.

        for (BlockState blockState : blockCamouflage.getStateContainer().getValidStates()) {
            ModelResourceLocation variantMRL = BlockModelShapes.getModelLocation(blockState);
            IBakedModel existingModel = event.getModelRegistry().get(variantMRL);
            if (existingModel == null) {
                LOGGER.warn("Did not find the expected vanilla baked model(s) for blockCamouflage in registry");
            } else if (existingModel instanceof CamouflageBakedModel) {
                LOGGER.warn("Tried to replace CamouflagedBakedModel twice");
            } else {
                CamouflageBakedModel customModel = new CamouflageBakedModel(existingModel);
                event.getModelRegistry().put(variantMRL, customModel);
            }
        }

    }

    @SubscribeEvent
    public static void onClientSetupEvent(FMLClientSetupEvent event) {
        RenderTypeLookup.setRenderLayer(blockCamouflage, RenderType.getCutoutMipped());
        // RenderTypeLookup.setRenderLayer(StartupCommon.blockCamouflage, RenderType.getTranslucent());
    }

    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onBlockColorHandlerEvent(final ColorHandlerEvent.Block event)
    {   event.getBlockColors().register((state, world, pos, tintIndex) -> {
                return ( (!(state.getBlock() instanceof Camouflage_FloorMat_Block)) || (world == null) || (pos == null) )
                        ? -1
                        : event.getBlockColors().getColor(world.getBlockState(pos.down()), world, pos.down(), tintIndex);
            }, blockCamouflage);
    }

}