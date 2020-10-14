package zed.d0c.floormats.setup;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import zed.d0c.floormats.FloorMats;
import zed.d0c.floormats.commands.ModCommands;

@Mod.EventBusSubscriber(modid = FloorMats.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModSetup {

    public static final ItemGroup ITEM_GROUP = new ItemGroup("floormats") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Registration.OAK_FLOORMAT_ITEM.get());
        }
    };

    @SubscribeEvent
    public static void serverLoad(FMLServerStartingEvent event) {
        ModCommands.register(event.getCommandDispatcher());
    }

}
