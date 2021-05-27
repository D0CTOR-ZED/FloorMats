package zed.d0c.floormats.render;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
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
import org.jetbrains.annotations.NotNull;
import zed.d0c.floormats.blocks.floormats.Camouflage_FloorMat_Block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class CamouflageBakedModel implements IBakedModel {

    public CamouflageBakedModel(IBakedModel unCamouflagedModel)
    {
        modelWhenNotCamouflaged = unCamouflagedModel;
    }

    private static final int MAX_QUAD_CACHE = 1000;

    public static ModelProperty<Optional<BlockState>> COPIED_BLOCK = new ModelProperty<>();

    // cache baked quads.  Each BlockState has an array list using the Direction index with null using UP.
    private static final HashMap<BlockState, ArrayList<ArrayList<BakedQuad>>> quadCache = new HashMap<>();

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
            LightUtil.unpack(vertexData, position_3f[vertex],   DefaultVertexFormats.BLOCK, vertex, 0);
            LightUtil.unpack(vertexData, color_4ub[vertex],     DefaultVertexFormats.BLOCK, vertex, 1);
            LightUtil.unpack(vertexData, tex_2f[vertex],        DefaultVertexFormats.BLOCK, vertex, 2);
            LightUtil.unpack(vertexData, tex_2sb[vertex],       DefaultVertexFormats.BLOCK, vertex, 3);
            LightUtil.unpack(vertexData, normal_3b[vertex],     DefaultVertexFormats.BLOCK, vertex, 4);
        }

        float thickness = 1F/16;
        float textureScale = 1F/18; // some of the next pixel was showing.  Needs to be less than 1/16.
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
            LightUtil.pack(position_3f[vert],   vertexData, DefaultVertexFormats.BLOCK, vert, 0);
            LightUtil.pack(color_4ub[vert],     vertexData, DefaultVertexFormats.BLOCK, vert, 1);
            LightUtil.pack(tex_2f[vert],        vertexData, DefaultVertexFormats.BLOCK, vert, 2);
            LightUtil.pack(tex_2sb[vert],       vertexData, DefaultVertexFormats.BLOCK, vert, 3);
            LightUtil.pack(normal_3b[vert],     vertexData, DefaultVertexFormats.BLOCK, vert, 4);
        }
        quadList.add(newQuad);
    }

    @Override
    @Nonnull
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        Optional<BlockState> copiedBlock = data.getData(COPIED_BLOCK);
        BlockState copiedState = ( (copiedBlock != null) && (copiedBlock.isPresent()) ) ? copiedBlock.get() : null;

        if (side == Direction.UP) {
            return Collections.emptyList();
        }
        if ( (copiedState != null) && (quadCache.containsKey(copiedState)) ) {
            return quadCache.get(copiedState).get(side == null ? Direction.UP.getIndex() : side.getIndex());
        }
        if ( (copiedState != null) && (quadCache.size()<MAX_QUAD_CACHE) ) {
            ArrayList<ArrayList<BakedQuad>> cache = new ArrayList<>(Direction.values().length);
            for (Direction direction : Direction.values()) {
                ArrayList<BakedQuad> transformedQuads = new ArrayList<>();
                if (direction == Direction.UP) {
                    for (BakedQuad quad : getActualBakedModelFromIModelData(data).getQuads(state, null, rand, data)) {
                        alterQuad(quad, transformedQuads);
                    }
                }
                for (BakedQuad quad : getActualBakedModelFromIModelData(data).getQuads(state, direction, rand, data)) {
                    alterQuad(quad, transformedQuads);
                }
                cache.add(direction.getIndex(),transformedQuads);
            }
            quadCache.put(copiedState,cache);
            return cache.get(side == null ? Direction.UP.getIndex() : side.getIndex());
        }
        List<BakedQuad> transformedQuads = new ArrayList<>();
        if (side == null) {
            for (BakedQuad quad : getActualBakedModelFromIModelData(data).getQuads(state, Direction.UP, rand, data)) {
                alterQuad(quad, transformedQuads);
            }
        }
        for (BakedQuad quad : getActualBakedModelFromIModelData(data).getQuads(state, side, rand, data)) {
            alterQuad(quad, transformedQuads);
        }
        return transformedQuads;
    }

    @Override
    @Nonnull
    public IModelData getModelData(@Nonnull IBlockDisplayReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData data) {
        ModelDataMap modelDataMap = getEmptyIModelData();
        modelDataMap.setData(COPIED_BLOCK, Camouflage_FloorMat_Block.appearanceBlock(world, pos));
        return modelDataMap;
    }

    @Override
    public TextureAtlasSprite getParticleTexture(@Nonnull IModelData data) {
        return modelWhenNotCamouflaged.getParticleTexture(data); // trying to fix stack overflow on particle data
        // return ((IForgeBakedModel)getActualBakedModelFromIModelData(data)).getParticleTexture(data);
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
        if ( (copiedBlock == null) || (!copiedBlock.isPresent()) ) return retval;

        Minecraft mc = Minecraft.getInstance();
        BlockRendererDispatcher blockRendererDispatcher = mc.getBlockRendererDispatcher();
        retval = blockRendererDispatcher.getModelForState(copiedBlock.get());

        return retval;
    }

    private final IBakedModel modelWhenNotCamouflaged;

    // ---- All these methods are required by the interface but we don't do anything special with them.

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull Random rand) {
        throw new AssertionError("IBakedModel::getQuads should never be called, only IForgeBakedModel::getQuads");
    }

    // getTexture is used directly when player is inside the block.  The game will crash if you don't use something
    //   meaningful here.
    @Override
    @SuppressWarnings("deprecation")
    public @NotNull TextureAtlasSprite getParticleTexture() {
        return modelWhenNotCamouflaged.getParticleTexture();
    }

    // ideally, this should be changed for different blocks being camouflaged, but this is not supported by vanilla or forge
    @Override
    public boolean isAmbientOcclusion()
    {
        return modelWhenNotCamouflaged.isAmbientOcclusion(); // false seems better on the sides?
        //return false;
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
    public @NotNull ItemOverrideList getOverrides()
    {
        return modelWhenNotCamouflaged.getOverrides();
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull ItemCameraTransforms getItemCameraTransforms()
    {
        return modelWhenNotCamouflaged.getItemCameraTransforms();
    }

    private static final Logger LOGGER = LogManager.getLogger();
    private static boolean loggedError = false; // prevent spamming console
}