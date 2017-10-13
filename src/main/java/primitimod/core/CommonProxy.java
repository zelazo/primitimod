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
import primitimod.core.registry.LogItemBlock;
import primitimod.entity.EntityRock;
import primitimod.items.ItemAxe;
import primitimod.items.ItemStoneRock;
import primitimod.trees.block.BlockComplexLog;
import primitimod.trees.block.BlockFallingLeaves;
import primitimod.trees.block.BlockOakTreeRoot;
import primitimod.trees.block.BlockPalmTreeRoot;
import primitimod.trees.tileentity.OakTreeRootTE;
import primitimod.trees.tileentity.PalmTreeRootTE;

@Mod.EventBusSubscriber
public class CommonProxy {
	
    public void preInit(FMLPreInitializationEvent event) {
    	GameRegistry.registerTileEntity(OakTreeRootTE.class, "oak_tree_root_te");
    	GameRegistry.registerTileEntity(PalmTreeRootTE.class, "palm_tree_root_te");
    }

    public void init(FMLInitializationEvent event) {
    	
    }

    public void postInit(FMLPostInitializationEvent event) {
    	
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new BlockRockPile());
        event.getRegistry().register(new BlockSimple());
        event.getRegistry().register(new BlockComplexLog("oaklog"));
        event.getRegistry().register(new BlockComplexLog("palmlog"));
        event.getRegistry().register(new BlockComplexLog("barelog"));
        event.getRegistry().register(new BlockOakTreeRoot("oaktreeroot"));
        event.getRegistry().register(new BlockPalmTreeRoot("palmtreeroot"));

        event.getRegistry().register(new BlockFallingLeaves("oakleaves"));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
    	
    	event.getRegistry().register(new ItemStoneRock());
    	event.getRegistry().register(new ItemAxe());
    }
    
    @SubscribeEvent
    public static void registerItemBlocks(final RegistryEvent.Register<Item> event) {
    	event.getRegistry().register(new LogItemBlock(PrimitiModBlocks.blockOakLog));
    	event.getRegistry().register(new LogItemBlock(PrimitiModBlocks.blockPalmLog));
    	event.getRegistry().register(new LogItemBlock(PrimitiModBlocks.blockBareLog));

    	event.getRegistry().register(new ItemBlock(PrimitiModBlocks.blockOakTreeRoot).setRegistryName(PrimitiModBlocks.blockOakTreeRoot.getRegistryName()));
    	event.getRegistry().register(new ItemBlock(PrimitiModBlocks.blockPalmTreeRoot).setRegistryName(PrimitiModBlocks.blockPalmTreeRoot.getRegistryName()));
        event.getRegistry().register(new ItemBlock(PrimitiModBlocks.blockSimple).setRegistryName(PrimitiModBlocks.blockSimple.getRegistryName()));
        
        event.getRegistry().register(new ItemBlock(PrimitiModBlocks.blockOakLeaves).setRegistryName(PrimitiModBlocks.blockOakLeaves.getRegistryName()));
        
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