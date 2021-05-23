package zed.d0c.floormats.render;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.client.model.pipeline.LightUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zed.d0c.floormats.blocks.floormats.Camouflage_FloorMat_Block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class CamouflageBakedModel implements IBakedModel {

    public CamouflageBakedModel(IBakedModel unCamouflagedModel)
    {
        modelWhenNotCamouflaged = unCamouflagedModel;
    }

    public static ModelProperty<Optional<BlockState>> COPIED_BLOCK = new ModelProperty<>();

    public static ModelDataMap getEmptyIModelData() {
        return (new ModelDataMap.Builder()).withInitial(COPIED_BLOCK, Optional.empty()).build();
    }

    // public static final VertexFormat BLOCK = new VertexFormat(ImmutableList.<VertexFormatElement>builder()
    // .add(POSITION_3F)
    // .add(COLOR_4UB)
    // .add(TEX_2F)
    // .add(TEX_2SB)
    // .add(NORMAL_3B)
    // .add(PADDING_1B)
    // .build());

    public static void alterQuad(BakedQuad quad, List<BakedQuad> quadList)
    {
        int[] vertexData = quad.getVertexData();
        BakedQuad newQuad = new BakedQuad(Arrays.copyOf(vertexData, vertexData.length), quad.getTintIndex(), quad.getFace(), quad.getSprite(), quad.applyDiffuseLighting() );
        vertexData = newQuad.getVertexData();

        float[][] position_3f = new float[4][3];
        float[][] color_4ub = new float[4][4];
        float[][] tex_2f    = new float[4][2];
        float[][] tex_2sb   = new float[4][2];
        float[][] normal_3b = new float[4][3];

        for (int vertex = 0; vertex < 4; vertex++) {
            LightUtil.unpack(vertexData, position_3f[vertex], DefaultVertexFormats.BLOCK, vertex, 0);
            LightUtil.unpack(vertexData, color_4ub[vertex], DefaultVertexFormats.BLOCK, vertex, 1);
            LightUtil.unpack(vertexData, tex_2f[vertex], DefaultVertexFormats.BLOCK, vertex, 2);
            LightUtil.unpack(vertexData, tex_2sb[vertex], DefaultVertexFormats.BLOCK, vertex, 3);
            LightUtil.unpack(vertexData, normal_3b[vertex], DefaultVertexFormats.BLOCK, vertex, 4);
        }

        float thickness = 1F/4;
        float textureScale = 1F/18; // some of the next pixel was showing.  No such thing as too thin here. Needs to be less than 1/16.
        switch (newQuad.getFace()) {
            case UP:
                if (position_3f[0][1] < 1) { return; }
                for (int i = 0; i < 4; i++) {
                    position_3f[i][1] = thickness;
                }
                break;
            case DOWN:
                for (int i = 0; i < 4; i++) {
                    position_3f[i][1] *= 0.01F;
                }
                break;
            default:
                LOGGER.warn("SIDE QUAD: {},{},{} ; {},{},{} ; {},{},{} ; {},{},{}",
                        normal_3b[0][0], normal_3b[0][1], normal_3b[0][2],
                        normal_3b[1][0], normal_3b[1][1], normal_3b[1][2],
                        normal_3b[2][0], normal_3b[2][1], normal_3b[2][2],
                        normal_3b[3][0], normal_3b[3][1], normal_3b[3][2]
                );
                if (position_3f[0][1] < 0.9999998) { return; }
                position_3f[0][1] = thickness;
                position_3f[1][1] = 0;
                position_3f[2][1] = 0;
                position_3f[3][1] = thickness;
                tex_2f[1][0] = tex_2f[0][0] + (tex_2f[1][0] - tex_2f[0][0]) * textureScale;
                tex_2f[1][1] = tex_2f[0][1] + (tex_2f[1][1] - tex_2f[0][1]) * textureScale;
                tex_2f[2][0] = tex_2f[3][0] + (tex_2f[2][0] - tex_2f[3][0]) * textureScale;
                tex_2f[2][1] = tex_2f[3][1] + (tex_2f[2][1] - tex_2f[3][1]) * textureScale;
        }

        for (int vert = 0; vert < 4; vert++)
        {
            LightUtil.pack(position_3f[vert], vertexData, DefaultVertexFormats.BLOCK, vert, 0);
            LightUtil.pack(color_4ub[vert], vertexData, DefaultVertexFormats.BLOCK, vert, 1);
            LightUtil.pack(tex_2f[vert], vertexData, DefaultVertexFormats.BLOCK, vert, 2);
            LightUtil.pack(tex_2sb[vert], vertexData, DefaultVertexFormats.BLOCK, vert, 3);
            LightUtil.pack(normal_3b[vert], vertexData, DefaultVertexFormats.BLOCK, vert, 4);
        }
        quadList.add(newQuad);
    }


    @Override
    @Nonnull
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        List<BakedQuad> listOfBakedQuads = getActualBakedModelFromIModelData(extraData).getQuads(state, side, rand);
        List<BakedQuad> transformedQuads = new ArrayList<>();
        for (BakedQuad quad : listOfBakedQuads) {
            alterQuad(quad,transformedQuads);
        }
        return transformedQuads;
    }


    @Override
    @Nonnull
    public IModelData getModelData(@Nonnull IBlockDisplayReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
        ModelDataMap modelDataMap = getEmptyIModelData();
        modelDataMap.setData(COPIED_BLOCK, Camouflage_FloorMat_Block.appearanceBlock(world, pos));
        return modelDataMap;
    }

    @Override
    public TextureAtlasSprite getParticleTexture(@Nonnull IModelData data) {
        return getActualBakedModelFromIModelData(data).getParticleTexture();
    }

    private IBakedModel getActualBakedModelFromIModelData(@Nonnull IModelData data) {
        IBakedModel retval = modelWhenNotCamouflaged;  // default
        if (!data.hasProperty(COPIED_BLOCK)) {
            if (!loggedError) {
                LOGGER.error("IModelData did not have expected property COPIED_BLOCK");
                loggedError = true;
            }
            return retval;
        }
        Optional<BlockState> copiedBlock = data.getData(COPIED_BLOCK);
        if (!copiedBlock.isPresent()) return retval;

        Minecraft mc = Minecraft.getInstance();
        BlockRendererDispatcher blockRendererDispatcher = mc.getBlockRendererDispatcher();
        retval = blockRendererDispatcher.getModelForState(copiedBlock.get());

        return retval;
    }

    private IBakedModel modelWhenNotCamouflaged;

    // ---- All these methods are required by the interface but we don't do anything special with them.

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        throw new AssertionError("IBakedModel::getQuads should never be called, only IForgeBakedModel::getQuads");
    }

    // getTexture is used directly when player is inside the block.  The game will crash if you don't use something
    //   meaningful here.
    @Override
    public TextureAtlasSprite getParticleTexture() {
        return modelWhenNotCamouflaged.getParticleTexture();
    }


    // ideally, this should be changed for different blocks being camouflaged, but this is not supported by vanilla or forge
    @Override
    public boolean isAmbientOcclusion()
    {
        return modelWhenNotCamouflaged.isAmbientOcclusion(); // false seems better on the sides?
    }

    @Override
    public boolean isGui3d()
    {
        return modelWhenNotCamouflaged.isGui3d();
    }

    @Override
    public boolean isSideLit() {
        return modelWhenNotCamouflaged.isSideLit();  // related to item "diffuselighting"
    }

    @Override
    public boolean isBuiltInRenderer()
    {
        return modelWhenNotCamouflaged.isBuiltInRenderer();
    }

    @Override
    public ItemOverrideList getOverrides()
    {
        return modelWhenNotCamouflaged.getOverrides();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms()
    {
        return modelWhenNotCamouflaged.getItemCameraTransforms();
    }

    private static final Logger LOGGER = LogManager.getLogger();
    private static boolean loggedError = false; // prevent spamming console
}