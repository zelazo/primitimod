package primitimod.core.client;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import primitimod.PrimitiMod;
import primitimod.core.CommonProxy;
import primitimod.core.PrimitiModBlocks;
import primitimod.core.PrimitiModItems;
import primitimod.entity.EntityStoneRock;
import primitimod.entity.RenderStoneRock;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	
	@Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        
        OBJLoader.INSTANCE.addDomain(PrimitiMod.MODID);
        
    }
	
	@Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        
    	PrimitiModBlocks.OakTree.registerColourHandlers(PrimitiModBlocks.OakTree.leaves);
    	PrimitiModBlocks.PalmTree.registerColourHandlers(PrimitiModBlocks.PalmTree.leaves);
    	PrimitiModBlocks.BirchTree.registerColourHandlers(PrimitiModBlocks.BirchTree.leaves);
    	/*#TreeGenerator_registerColour*/







    }
	
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
    	PrimitiModBlocks.OakTree.initItemModels(PrimitiModBlocks.OakTree.log, PrimitiModBlocks.OakTree.leaves, PrimitiModBlocks.OakTree.root, PrimitiModBlocks.OakTree.sapling, PrimitiModBlocks.OakTree.lumberPile, PrimitiModBlocks.OakTree.lumber);
    	PrimitiModBlocks.PalmTree.initItemModels(PrimitiModBlocks.PalmTree.log, PrimitiModBlocks.PalmTree.leaves, PrimitiModBlocks.PalmTree.root, PrimitiModBlocks.PalmTree.sapling, PrimitiModBlocks.PalmTree.lumberPile, PrimitiModBlocks.PalmTree.lumber);
    	PrimitiModBlocks.BirchTree.initItemModels(PrimitiModBlocks.BirchTree.log, PrimitiModBlocks.BirchTree.leaves, PrimitiModBlocks.BirchTree.root, PrimitiModBlocks.BirchTree.sapling, PrimitiModBlocks.BirchTree.lumberPile, PrimitiModBlocks.BirchTree.lumber);
    	/*#TreeGenerator_initItemModels*/







    	
        PrimitiModBlocks.initModels();
        PrimitiModItems.initModels();
        
        RenderingRegistry.registerEntityRenderingHandler(EntityStoneRock.class, RenderStoneRock.FACTORY);
        
//        RenderingRegistry.registerEntityRenderingHandler(EntityRock.class, RenderFactory.INSTANCE);

//        RenderingRegistry.registerEntityRenderingHandler(EntitySnowball.class, new IRenderFactory<EntitySnowball>() {
//
//			@Override
//			public Render<EntitySnowball> createRenderFor(RenderManager manager) {
//				return new RenderSnowball<EntitySnowball>(manager, Items.SNOWBALL, Minecraft.getMinecraft().getRenderItem());
//			}
//			
//        });
    }
    
}
