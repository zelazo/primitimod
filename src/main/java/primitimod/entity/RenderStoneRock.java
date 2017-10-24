package primitimod.entity;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import primitimod.core.PrimitiModItems;

public class RenderStoneRock extends RenderSnowball<EntityStoneRock> {

  public RenderStoneRock(RenderManager renderManagerIn, Item itemIn, RenderItem itemRendererIn) {
		super(renderManagerIn, itemIn, itemRendererIn);
	}

public static final IRenderFactory<EntityStoneRock> FACTORY = new Factory();

  @Nonnull
  @Override
  public ItemStack getStackToRender(EntityStoneRock entityIn) {
      return new ItemStack(item, 1, 0);
  }

  private static class Factory implements IRenderFactory<EntityStoneRock> {

    @Override
    public Render<? super EntityStoneRock> createRenderFor(RenderManager manager) {
      return new RenderStoneRock(manager, PrimitiModItems.itemStoneRock, Minecraft.getMinecraft().getRenderItem());
    }
  }
}
