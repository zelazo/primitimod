package primitimod;

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
import primitimod.blocks.BlockComplexLog;
import primitimod.blocks.BlockRockPile;
import primitimod.blocks.BlockSimple;
import primitimod.entity.EntityRock;
import primitimod.items.ItemLogThin;
import primitimod.items.ItemStoneRock;

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
        event.getRegistry().register(new BlockRockPile());
        event.getRegistry().register(new BlockSimple());
        event.getRegistry().register(new BlockComplexLog());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
    	
    	event.getRegistry().register(new ItemStoneRock());
    	event.getRegistry().register(new ItemLogThin());
    	
//    	event.getRegistry().register(new ItemLogThin().setRegistryName(PrimitiMod.MODID, "logthinner"));
//        event.getRegistry().register(new ItemBlock(PrimitiModBlocks.blockRockPile).setRegistryName(PrimitiModBlocks.blockRockPile.getRegistryName()));
        event.getRegistry().register(new ItemBlock(PrimitiModBlocks.blockSimple).setRegistryName(PrimitiModBlocks.blockSimple.getRegistryName()));
//        event.getRegistry().register(new ItemBlock(PrimitiModBlocks.blockComplexLog).setRegistryName(PrimitiModBlocks.blockComplexLog.getRegistryName()+"_1"));
//        event.getRegistry().register(new ItemBlock(PrimitiModBlocks.blockComplexLog).setRegistryName(PrimitiModBlocks.blockComplexLog.getRegistryName()+"_2"));
//        event.getRegistry().register(new ItemBlock(PrimitiModBlocks.blockComplexLog).setRegistryName(PrimitiModBlocks.blockComplexLog.getRegistryName()+"=1"));
    }
    
    @SubscribeEvent
    public static void registerItemBlocks(final RegistryEvent.Register<Item> event) {
    	event.getRegistry().register(new ItemBlock(PrimitiModBlocks.blockComplexLog).setRegistryName(PrimitiModBlocks.blockComplexLog.getRegistryName()+"_item"));
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