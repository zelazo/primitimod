package primitimod.items;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import primitimod.PrimitiMod;
import primitimod.core.PrimitiModBlocks;

public class ItemStoneRock extends Item {

	public ItemStoneRock() {
		setCreativeTab(PrimitiMod.tab);
        setRegistryName("stonerock");
        setMaxStackSize(16);
        setUnlocalizedName(getRegistryName().toString());
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
    
//    @Override
//    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
//    	
//    	ItemStack itemStack = player.getHeldItem(hand);
//    	if(!world.isRemote) {
//
//    		if(!player.isSneaking()) {
//    			
////		    	EntityEgg entityegg = new EntityEgg(world, player);
////		        entityegg.setHeadingFromThrower(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
////		        world.spawnEntity(entityegg);
//		        
//		        EntityRock entityRock = new EntityRock(world, player);
//		        entityRock.setHeadingFromThrower(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
//		        world.spawnEntity(entityRock);
//    		}
//    	}
//    	return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);
//    }
    
    
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
    		EnumFacing facing, float hitX, float hitY, float hitZ) {
    	
    	if(!world.isRemote) {

//    		if(player.isSneaking()) {
    			
	    		EnumActionResult result = EnumActionResult.FAIL;
	    		
		    	BlockPos newPos = pos.offset(facing);
		    	IBlockState target = world.getBlockState(newPos);
		    	IBlockState targetBottom = world.getBlockState(newPos.down());
		    	
		    	if(targetBottom.isFullCube()) {
		    		
		    		if(target.getBlock().isAir(target, world, newPos)) {

		    			world.setBlockState(newPos, PrimitiModBlocks.blockRockPile.getDefaultState());
		    			
		    			player.getHeldItemMainhand().shrink(1);
		    			
		    			result = EnumActionResult.SUCCESS;
		    		}
		    		else if(target.getBlock().equals(PrimitiModBlocks.blockRockPile)) {
		    			
		    			target.getBlock().onBlockActivated(world, newPos, target, player, hand, facing, hitX, hitY, hitZ);
		    			result = EnumActionResult.SUCCESS;
		    		}
		    		
		        	return result;
		    	}
//    		}
    	}
    	
    	return EnumActionResult.PASS;
    }

}