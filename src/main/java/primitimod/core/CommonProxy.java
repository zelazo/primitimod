package primitimod.core;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import primitimod.PrimitiMod;
import primitimod.blocks.BlockRockPile;
import primitimod.entity.EntityStoneRock;
import primitimod.items.ItemHeavyAxe;
import primitimod.items.ItemStoneRock;
import primitimod.trees.tileentity.BirchTreeRootTE;
import primitimod.trees.tileentity.OakTreeRootTE;
import primitimod.trees.tileentity.PalmTreeRootTE;
/*#TreeGenerator_importTE*/






@Mod.EventBusSubscriber
public class CommonProxy {
	
    public void preInit(FMLPreInitializationEvent event) {

    }

    public void init(FMLInitializationEvent event) {
    	
    }

    public void postInit(FMLPostInitializationEvent event) {
    	
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
    	PrimitiModBlocks.OakTree.registerBlocks(PrimitiModBlocks.OakTree.treeName, OakTreeRootTE.class, event.getRegistry());
    	PrimitiModBlocks.PalmTree.registerBlocks(PrimitiModBlocks.PalmTree.treeName, PalmTreeRootTE.class, event.getRegistry());
    	PrimitiModBlocks.BirchTree.registerBlocks(PrimitiModBlocks.BirchTree.treeName, BirchTreeRootTE.class, event.getRegistry());
    	/*#TreeGenerator_registerBlocks*/

        event.getRegistry().register(new BlockRockPile());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
    	PrimitiModBlocks.OakTree.registerItems(PrimitiModBlocks.OakTree.treeName, event.getRegistry(), PrimitiModBlocks.OakTree.lumberPile, PrimitiModBlocks.OakTree.log);
    	PrimitiModBlocks.PalmTree.registerItems(PrimitiModBlocks.PalmTree.treeName, event.getRegistry(), PrimitiModBlocks.PalmTree.lumberPile, PrimitiModBlocks.PalmTree.log);
    	PrimitiModBlocks.BirchTree.registerItems(PrimitiModBlocks.BirchTree.treeName, event.getRegistry(), PrimitiModBlocks.BirchTree.lumberPile, PrimitiModBlocks.BirchTree.log);
    	/*#TreeGenerator_registerItems*/



    	event.getRegistry().register(new ItemStoneRock());
    	event.getRegistry().register(new ItemHeavyAxe());
    	
    }
    
    @SubscribeEvent
    public static void registerItemBlocks(final RegistryEvent.Register<Item> event) {
    	PrimitiModBlocks.OakTree.registerItemBlocks(event.getRegistry(), PrimitiModBlocks.OakTree.log, PrimitiModBlocks.OakTree.leaves, PrimitiModBlocks.OakTree.root, PrimitiModBlocks.OakTree.sapling);
    	PrimitiModBlocks.PalmTree.registerItemBlocks(event.getRegistry(), PrimitiModBlocks.PalmTree.log, PrimitiModBlocks.PalmTree.leaves, PrimitiModBlocks.PalmTree.root, PrimitiModBlocks.PalmTree.sapling);
    	PrimitiModBlocks.BirchTree.registerItemBlocks(event.getRegistry(), PrimitiModBlocks.BirchTree.log, PrimitiModBlocks.BirchTree.leaves, PrimitiModBlocks.BirchTree.root, PrimitiModBlocks.BirchTree.sapling);
    	/*#TreeGenerator_registerItemBlocks*/



    }
    
    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {

    	String entityName = "entityrock";
//    	EntityEntry entityEntry = new EntityEntry(EntityStoneRock.class, entityName);
//    	entityEntry.setRegistryName(new ResourceLocation(PrimitiMod.MODID,entityName));
    	
//    	EntityRegistry.registerModEntity(
//			new ResourceLocation(PrimitiMod.MODID,entityName.toLowerCase()), 
//			EntityStoneRock.class, entityName, 1, PrimitiMod.instance, 80, 20, true
//		);
    	
    	
    	EntityRegistry.registerModEntity(new ResourceLocation(PrimitiMod.MODID,entityName), EntityStoneRock.class, 
    			entityName, 1, PrimitiMod.instance, 80, 20, true); 

    	
//    	event.getRegistry().register(entityEntry.setRegistryName(PrimitiMod.MODID, "entityrock"));
    }
}