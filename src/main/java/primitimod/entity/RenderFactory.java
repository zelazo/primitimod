package primitimod.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Items;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderFactory implements IRenderFactory<EntityRock> {

    public static final RenderFactory INSTANCE = new RenderFactory();

    @Override
    public Render<? super EntityRock> createRenderFor(RenderManager manager) {
        // TODO Auto-generated method stub
        return new RenderSnowball<EntityRock>(manager, Items.SNOWBALL, Minecraft.getMinecraft().getRenderItem());
    }
    
//    @Override
//	public Render<EntityRock> createRenderFor(RenderManager manager) {
//		return new RenderSnowball<EntityRock>(manager, Items.SNOWBALL, Minecraft.getMinecraft().getRenderItem());
////		return new RenderSnowball<EntityRock>(manager, PrimitiModItems.itemStoneRock, Minecraft.getMinecraft().getRenderItem());
//	}

}