package primitimod.core;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import primitimod.PrimitiMod;
import primitimod.blocks.BlockRockPile;
import primitimod.blocks.BlockSimple;
import primitimod.core.PrimitiModBlocks.OakTree;
import primitimod.entity.EntityRock;
import primitimod.items.ItemHeavyAxe;
import primitimod.items.ItemStoneRock;
import primitimod.trees.block.BlockComplexLog;
import primitimod.trees.block.BlockFallingLeaves;
import primitimod.trees.block.BlockPalmTreeRoot;
import primitimod.trees.item.ItemBlockLog;
import primitimod.trees.tileentity.OakTreeRootTE;
import primitimod.trees.tileentity.PalmTreeRootTE;

@Mod.EventBusSubscriber
public class CommonProxy {
	
    public void preInit(FMLPreInitializationEvent event) {
    	GameRegistry.registerTileEntity(PalmTreeRootTE.class, "palm_tree_root_te");
    }

    public void init(FMLInitializationEvent event) {
    	
    }

    public void postInit(FMLPostInitializationEvent event) {
    	
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
    	OakTree.registerBlocks(OakTree.treeName, OakTreeRootTE.class, event.getRegistry());
    	
        event.getRegistry().register(new BlockRockPile());
        event.getRegistry().register(new BlockSimple());
        event.getRegistry().register(new BlockComplexLog("palmlog"));
        event.getRegistry().register(new BlockComplexLog("barelog"));
        event.getRegistry().register(new BlockPalmTreeRoot("palmtreeroot"));
        event.getRegistry().register(new BlockFallingLeaves("palmleaves"));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
    	OakTree.registerItems(OakTree.treeName, event.getRegistry(), OakTree.lumberPile);
    	
    	
    	event.getRegistry().register(new ItemStoneRock());
    	event.getRegistry().register(new ItemHeavyAxe());
    }
    
    @SubscribeEvent
    public static void registerItemBlocks(final RegistryEvent.Register<Item> event) {
    	OakTree.registerItemBlocks(event.getRegistry(), OakTree.log, OakTree.leaves, OakTree.root);
    	
    	
    	event.getRegistry().register(new ItemBlockLog(PrimitiModBlocks.blockPalmLog));
    	event.getRegistry().register(new ItemBlockLog(PrimitiModBlocks.blockBareLog));
    	event.getRegistry().register(new ItemBlock(PrimitiModBlocks.blockPalmTreeRoot).setRegistryName(PrimitiModBlocks.blockPalmTreeRoot.getRegistryName()));
        event.getRegistry().register(new ItemBlock(PrimitiModBlocks.blockSimple).setRegistryName(PrimitiModBlocks.blockSimple.getRegistryName()));
        event.getRegistry().register(new ItemBlock(PrimitiModBlocks.blockPalmLeaves).setRegistryName(PrimitiModBlocks.blockPalmLeaves.getRegistryName()));
        
    }
    
    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {

    	String entityName = "entityrock";
    	EntityEntry entityEntry = new EntityEntry(EntityRock.class, entityName);
    	entityEntry.setRegistryName(new ResourceLocation(PrimitiMod.MODID,entityName));
    	
    	EntityRegistry.registerModEntity(
			new ResourceLocation(PrimitiMod.MODID,entityName.toLowerCase()), 
			EntityRock.class, entityName, 1, PrimitiMod.instance, 80, 20, true
		);

    	
//    	event.getRegistry().register(entityEntry.setRegistryName(PrimitiMod.MODID, "entityrock"));
    }
}