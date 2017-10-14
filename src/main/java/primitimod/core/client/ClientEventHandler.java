package primitimod.core.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import primitimod.PrimitiMod;
import primitimod.items.ItemHeavyAxe;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = PrimitiMod.MODID)
public class ClientEventHandler {
	
	@SubscribeEvent
	public static void onFOVUpdate(final FOVUpdateEvent event) {
		if (event.getEntity().isHandActive() && event.getEntity().getActiveItemStack().getItem() instanceof ItemHeavyAxe) {
			
			float fovModifier = event.getEntity().getItemInUseMaxCount() / 20.0f;

			if (fovModifier > 1.0f) {
				fovModifier = 1.0f;
			} else {
				fovModifier *= fovModifier;
			}

			event.setNewfov(event.getFov() * (1.0f - fovModifier * 0.15f));
		}
	}
	

	@SubscribeEvent
	  public void playerRenderEvent(RenderPlayerEvent event) {

		System.out.println("handRenderEvent");
	}
	
//	@SubscribeEvent
//	  public void handRenderEvent(RenderSpecificHandEvent event) {
//	    EntityPlayer player = Minecraft.getMinecraft().player;
//
//	    if(event.getHand() == EnumHand.OFF_HAND && player.isHandActive()) {
//	      ItemStack stack = player.getActiveItemStack();
//	      if(!stack.isEmpty() && stack.getItemUseAction() == EnumAction.BOW) {
//	        event.setCanceled(true);
//	      }
//	    }
//
//		
//	    ItemStack mainStack = player.getHeldItemMainhand();
//	    RayTraceResult rt = Minecraft.getMinecraft().objectMouseOver;
//	    if(!mainStack.isEmpty()
//	       && rt != null
//	       && rt.typeOfHit == RayTraceResult.Type.BLOCK
//	    ) {
//
//	      event.setCanceled(true);
//
//	      EnumHand hand;
//	      ItemStack itemStack;
//	      if(event.getHand() == EnumHand.MAIN_HAND) {
//	        hand = EnumHand.OFF_HAND;
//	        itemStack = player.getHeldItemOffhand();
//	      }
//	      else {
//	        hand = EnumHand.MAIN_HAND;
//	        itemStack = player.getHeldItemMainhand();
//	      }
//
//	      ItemRenderer itemRenderer = Minecraft.getMinecraft().getItemRenderer();
//	      itemRenderer.renderItemInFirstPerson(
//	          Minecraft.getMinecraft().player,
//	          event.getPartialTicks(),
//	          event.getInterpolatedPitch(),
//	          hand,
//	          event.getSwingProgress(),
//	          itemStack,
//	          event.getEquipProgress());
//	    }
//	}
}