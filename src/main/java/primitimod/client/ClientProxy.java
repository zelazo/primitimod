package primitimod.client;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import primitimod.CommonProxy;
import primitimod.PrimitiMod;
import primitimod.PrimitiModBlocks;
import primitimod.PrimitiModItems;
import primitimod.entity.EntityRock;
import primitimod.entity.RenderFactory;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	
	@Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        
        OBJLoader.INSTANCE.addDomain(PrimitiMod.MODID);

        
    }
	
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        PrimitiModBlocks.initModels();
        PrimitiModItems.initModels();
        
        RenderingRegistry.registerEntityRenderingHandler(EntityRock.class, RenderFactory.INSTANCE);

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
