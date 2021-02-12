package zed.d0c.floormats.plugin;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import zed.d0c.floormats.FloorMats;

import javax.annotation.Nonnull;
import java.util.Collections;

import static zed.d0c.floormats.setup.Registration.GILDED_TREATED_WOOD_FLOORMAT_ITEM;
import static zed.d0c.floormats.setup.Registration.TREATED_WOOD_FLOORMAT_ITEM;

@mezz.jei.api.JeiPlugin
public class JEIPlugin implements IModPlugin {

    private static final ResourceLocation UID = new ResourceLocation(FloorMats.MODID,"jei");

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
//      // Was going to base treated wood off the existence of an entry in this fluid tag, but instead went with IE
//      // needs to exist.
//      ITag<Fluid> creosoteTag = FluidTags.getCollection().get(new ResourceLocation("forge", "creosote"));
//      if ((creosoteTag == null) || creosoteTag.getAllElements().isEmpty()) {
        //noinspection SpellCheckingInspection
        if (!ModList.get().isLoaded("immersiveengineering")) {
            IIngredientManager manager = jeiRuntime.getIngredientManager();
            manager.removeIngredientsAtRuntime(VanillaTypes.ITEM, Collections.singleton(TREATED_WOOD_FLOORMAT_ITEM.get().getDefaultInstance()));
            manager.removeIngredientsAtRuntime(VanillaTypes.ITEM, Collections.singleton(GILDED_TREATED_WOOD_FLOORMAT_ITEM.get().getDefaultInstance()));
        }
    }

}
