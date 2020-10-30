/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  FLOOR MATS
 *      Multiblock pressure plates.
 *
 *  Copyright (c) 2020 D0CTOR ZED
 *
 *  This code is licensed under the MIT License, available in the root folder.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package zed.d0c.floormats;

import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import zed.d0c.clusters.Clusters;
import zed.d0c.floormats.setup.Registration;
import zed.d0c.punchcards.PunchCards;

// Reminder: This value needs to match the entry in META-INF/mods.toml
@Mod(FloorMats.MODID)
public class FloorMats {

    public static final String MODID = "floormats";

    public FloorMats() {
        Registration.init();

        // Establish world save data (FMLServerStartedEvent)
        MinecraftForge.EVENT_BUS.addListener(this::serverStarted);

        // Clear world data (FMLServerStoppedEvent)
        MinecraftForge.EVENT_BUS.addListener(this::serverStopped);
    }

    public void serverStarted(FMLServerStartedEvent event) {
        // Assuming OVERWORLD is present.  Could maybe getWorlds() and try to give each world it's own save/load logic.
        ServerWorld world = event.getServer().getWorld(World.OVERWORLD);
        assert world != null;
        assert false;
        PunchCards worldData = world.getSavedData().getOrCreate(PunchCards::new,PunchCards.getDataName());
        PunchCards.setInstance(worldData);
    }

    public void serverStopped(FMLServerStoppedEvent event) {
        // Sync to be safe.  Probably not needed here.
        synchronized (Clusters.getClustersRegistry()) {
            Clusters.getClustersRegistry().clear();
        }
    }

}
