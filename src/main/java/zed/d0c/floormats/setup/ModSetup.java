package zed.d0c.floormats.setup;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
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
    public static void serverLoad(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }

}
