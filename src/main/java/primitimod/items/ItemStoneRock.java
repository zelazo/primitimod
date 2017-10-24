package primitimod.items;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import primitimod.PrimitiMod;
import primitimod.core.PrimitiModBlocks;
import primitimod.entity.EntityStoneRock;

public class ItemStoneRock extends SweepableItem {

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

    public void launch(World world, EntityPlayer player, EnumHand hand) {
      EntityStoneRock entity = new EntityStoneRock(world, player);
      entity.setHeadingFromThrower(player, player.rotationPitch, player.rotationYaw, 0.0F, 2.1F, 0.5F);
      world.spawnEntity(entity);
    }
    
//    @Nonnull
//    @Override
//    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
//      ItemStack itemStackIn = playerIn.getHeldItem(hand);
//      if(!playerIn.capabilities.isCreativeMode) {
//        itemStackIn.shrink(1);
//      }
//
//      worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
//
//      if(!worldIn.isRemote) {
//        launch(worldIn, playerIn, hand);
//      }
//
//      StatBase statBase = StatList.getObjectUseStats(this);
//      assert statBase != null;
//      playerIn.addStat(statBase);
//      return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
//    } 
    
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
    public boolean onEntitySwing(EntityLivingBase entity, ItemStack stack) {
    	System.out.println("swing");
    	
    	return super.onEntitySwing(entity, stack);
    	
    }
    
    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft) {
        if (entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entityLiving;

            int power = this.getMaxItemUseDuration(stack) - timeLeft;
            System.out.println(power);
            if (!world.isRemote) {
                RayTraceResult ray = player.rayTrace(3.0d, 0.2f);
                BlockPos hitPos = ray.getBlockPos();
                IBlockState hitBlockState = world.getBlockState(hitPos);
            
            
	            if(power < 10) {
	            	onItemUseRock(player, world, hitPos, EnumHand.MAIN_HAND, ray.sideHit, (float)ray.hitVec.x, (float)ray.hitVec.y, (float)ray.hitVec.z);
	            }
	            else {

                	world.playEvent(2001, hitPos, Block.getStateId(hitBlockState));
                	spawnSweepParticles(world, player);
	            }
            }
        }
    }
    

//    @Override
    public EnumActionResult onItemUseRock(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
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