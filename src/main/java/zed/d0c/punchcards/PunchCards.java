/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  package/Class: PunchCards
 *      Handles saving and loading data.
 *  Copyright (c) 2020 D0CTOR ZED
 *  This code is licensed under the MIT License, available in the root folder.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package zed.d0c.punchcards;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import zed.d0c.clusters.Clusters;
import zed.d0c.clusters.ClustersNode;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/*  ***********************************************************************************
 *
 *  PunchCards - Handles saving and loading data.
 *
 *  ***********************************************************************************
 *  WORLD_SAVED_DATA NBT STRUCTURE
 *
 *  Structure       KEY                 Data Type
 *  ------------    ----------------    -----------------------------------------------
 *  REGISTRY_NBT    REGISTRY_NBT_KEY    CompoundNBT (Key: DIM_NAME, Value: DIM_CLUSTER)
 *      DIM_NAME                        Key (String: Name of Dimension)
 *      CLUSTERS    DIM_NAME            List of CompoundNBT (List of ClustersNode)
 *  ***********************************************************************************/

public class PunchCards extends WorldSavedData
{
    private static PunchCards DECK_OF_CARDS;
    private static final String DECK_BOX_NAME = "PUNCH_CARDS_DATA";
    private static final String REGISTRY_NBT_KEY = "Registry";

    public PunchCards() { super(DECK_BOX_NAME); }

    public static String getDataName() {
        return DECK_BOX_NAME;
    }

    public static void setInstance(PunchCards pcInstance) {
        DECK_OF_CARDS = pcInstance;
        DECK_OF_CARDS.markDirty(); // probably didn't get passed data, but could happen.
    }

    public static void setDirty() {
        if (DECK_OF_CARDS != null) {
            DECK_OF_CARDS.markDirty();
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void read(CompoundNBT nbt) {
        synchronized (Clusters.getClustersRegistry()) {
            Clusters.getClustersRegistry().clear();
            CompoundNBT registryNBT = nbt.getCompound(REGISTRY_NBT_KEY);
            for (String dimName : registryNBT.keySet()) { // func_240903_a_ = getOrCreateKey
                RegistryKey<World> dimType = RegistryKey.func_240903_a_(Registry.WORLD_KEY, new ResourceLocation(dimName));
                ListNBT clustersList = registryNBT.getList(dimName, Constants.NBT.TAG_COMPOUND);
                Clusters.ClustersSet dimRegistry = new Clusters.ClustersSet();
                for (int clustersIndex = 0; clustersIndex < clustersList.size(); clustersIndex++) {
                    CompoundNBT entry = clustersList.getCompound(clustersIndex);
                    dimRegistry.add(new ClustersNode(entry));
                }
                Clusters.getClustersRegistry().put(dimType, dimRegistry);
            }
        }
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public CompoundNBT write(CompoundNBT nbt) {
        CompoundNBT registryNBT = new CompoundNBT();
        synchronized (Clusters.getClustersRegistry()) {
            for (RegistryKey<World> dimType : Clusters.getClustersRegistry().keySet()) {
                if (dimType != null) {
                    ListNBT clustersList = new ListNBT();
                    for (ClustersNode node : Clusters.getClustersRegistry().get(dimType)) {
                        clustersList.add(node.serializeNBT());
                    } // func_240901_a_ = getLocation
                    registryNBT.put(dimType.func_240901_a_().toString(), clustersList);
                }
            }
        }
        nbt.put(REGISTRY_NBT_KEY, registryNBT);
        return nbt;
    }
}


