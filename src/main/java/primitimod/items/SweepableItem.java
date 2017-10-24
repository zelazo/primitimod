package primitimod.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class SweepableItem extends Item {
	
	public abstract void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft);
	
	public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }
	
	public int getMaxItemUseDuration(ItemStack stack) {
        return 1000;
    }
	
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        player.setActiveHand(hand);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
    }
	
	public static void spawnSweepParticles(World world, EntityPlayer player) {
    	double d0 = (double)(-MathHelper.sin(player.rotationYaw * 0.017453292F));
        double d1 = (double)MathHelper.cos(player.rotationYaw * 0.017453292F);
        double d2 = (double)(-MathHelper.sin(player.rotationPitch * 0.017453292F));
        
        if (world instanceof WorldServer) {
            ((WorldServer)world).spawnParticle(EnumParticleTypes.SWEEP_ATTACK, player.posX + d0, player.posY + (double)player.eyeHeight * 0.9D * (1.0D + d2), player.posZ + d1, 0, d0, 0.0D, d1, 0.0D);
            world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
        }
        
    }
	
	public int getDamageFromHarvestLevel(float hardness) {
    	int damage = 1;
    	
		if(hardness > 50) {
			damage = 0;
		}
		else if(hardness > 20) {
			damage = 10;
		}
		else if(hardness > 4) {
			damage = 5;
		}
		else if(hardness >= 2) {
			damage = 3;
		}
		else if(hardness > 0) {
			damage = 2;
		}
		else {
			damage = 0; 
		}
		
		return damage;
    }
        
}
