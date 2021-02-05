package zed.d0c.floormats.plugin;

import mezz.jei.api.JeiPlugin;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import zed.d0c.floormats.FloorMats;

import javax.annotation.Nonnull;

import java.util.Collections;

import static zed.d0c.floormats.setup.Registration.*;

@mezz.jei.api.JeiPlugin
public class JEIPlugin implements IModPlugin {

    private static final ResourceLocation UID = new ResourceLocation(FloorMats.MODID,"jei");

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    //    static IModIngredientRegistration iModIngredientRegistration;
    /*
    @Override
    public void registerIngredients(@Nonnull IModIngredientRegistration registration) {
        iModIngredientRegistration = registration;
    }
    */

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        IIngredientManager manager = jeiRuntime.getIngredientManager();
        if (!ModList.get().isLoaded("immersiveengineering")) {
            manager.removeIngredientsAtRuntime(VanillaTypes.ITEM, Collections.singleton(TREATED_WOOD_FLOORMAT_ITEM.get().getDefaultInstance()));
            manager.removeIngredientsAtRuntime(VanillaTypes.ITEM, Collections.singleton(GILDED_TREATED_WOOD_FLOORMAT_ITEM.get().getDefaultInstance()));
        }
    }

}
