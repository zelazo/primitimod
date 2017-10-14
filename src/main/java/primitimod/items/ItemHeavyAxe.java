package primitimod.items;

import java.util.EnumSet;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import primitimod.PrimitiMod;
import primitimod.core.PrimitiModBlocks;
import primitimod.trees.block.BlockComplexLog;
import primitimod.utils.RandUtil;

public class ItemHeavyAxe extends Item {

	public ItemHeavyAxe() {
		setCreativeTab(PrimitiMod.tab);
        setRegistryName("lumberheavyaxe");
        setMaxStackSize(1);
        setMaxDamage(100);
        setUnlocalizedName(getRegistryName().toString());
        
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
    
    /**
     * Called when the player stops using an Item (stops holding the right mouse button).
     */
    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft) {
        if (entityLiving instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer)entityLiving;

            int power = this.getMaxItemUseDuration(stack) - timeLeft;
            
            if(power > 25) {
                
	        	if (!world.isRemote) {
	                RayTraceResult ray = entityplayer.rayTrace(3.0d, 0.2f);
	                BlockPos hitPos = ray.getBlockPos();
	                IBlockState hitBlockState = world.getBlockState(hitPos);
//	                
	                world.playEvent(2001, hitPos, Block.getStateId(hitBlockState));

		            world.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, entityplayer.getSoundCategory(), 1.0F, 1.0F);
		            entityplayer.spawnSweepParticles();
	                
	                hitBlock(entityplayer, world, ray.getBlockPos());
	        	}
            }
        }
    }
    
    public boolean hitBlock(EntityPlayer player, World world, BlockPos pos) {
    	if(!world.isRemote) {

	    	IBlockState target = world.getBlockState(pos);
	    	
	    	if( PrimitiModBlocks.isLog(target.getBlock()) ) {
	    		
	    		int size = target.getValue(BlockComplexLog.SIZE);
	    		
    			world.setBlockState(pos, PrimitiModBlocks.blockBareLog.getDefaultState()
					.withProperty(BlockComplexLog.LOG_AXIS, target.getValue(BlockComplexLog.LOG_AXIS))
					.withProperty(BlockComplexLog.SIZE, size < 2 ? size + 1 : size ) 
				);
	    			
    			player.getHeldItemMainhand().damageItem(1, player);
    			return true;
	    	}
	    	else if(target.getBlock() == PrimitiModBlocks.blockBareLog) {
	    		System.out.println("axe hit!");
	    		BlockPos toCut = findBlockToCut(world, pos);
	    		
	    		cutLog(world, toCut);
	    		
	    		return true;
	    	}
    	}
    	
    	return false;
    }
    
  
    /**
     * Called when the equipped item is right clicked.
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
    	
        ItemStack itemstack = player.getHeldItem(hand);
        player.setActiveHand(hand);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
    }
    
    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }
    
    /**
     * How long it takes to use or consume an item
     */
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 50;
    }
    
    public BlockPos findBlockToCut(World world, BlockPos hitBlock) {
    	Block logBlockType = null;
    	BlockPos target = hitBlock;
    	BlockPos result = hitBlock;
    	IBlockState hitState = world.getBlockState(hitBlock);
    	IBlockState targetState = null;
    	boolean done = false;
    	
    	if(hitState.getBlock() == Blocks.AIR) {
    		return hitBlock;
    	}
    	
    	while(!done) {
    		
    		target = target.up();
    		targetState = world.getBlockState(target);
    		
    		done = true;
    		if(targetState.getBlock() == logBlockType && targetState.getValue(BlockComplexLog.LOG_AXIS) == BlockLog.EnumAxis.Y) 
    		{
    			result = target;
    			done = false;
    		}
    		else {
    			for(EnumFacing facing : EnumFacing.HORIZONTALS) {
    				target = target.offset(facing);
    				targetState = world.getBlockState(target);

    				if(logBlockType == null && PrimitiModBlocks.isLog(targetState.getBlock())) {
    					logBlockType = targetState.getBlock();
    				}
    				
    				
    				if(targetState.getBlock() == logBlockType && 
					   targetState.getValue(BlockComplexLog.LOG_AXIS) == BlockLog.EnumAxis.Y) 
    				{
    					result = target;
    	    			done = false;
    	    			break;
    	    		}
        		}
    		}
    	}
    	
    	return result;
    }
    
    public void cutLog(World world, BlockPos target) {
    	IBlockState targetState = world.getBlockState(target);
    	Block blockType = targetState.getBlock();
    	BlockPos offsetPos = null;
    	IBlockState offsetState = null;
    	BlockLog.EnumAxis axis = null;
    	EnumFacing branchFacing = null;
    	
    	for(EnumFacing facing : EnumFacing.HORIZONTALS) {
    		offsetPos = target.offset(facing);
    		offsetState = world.getBlockState(offsetPos);

    		if(offsetState.getBlock() == targetState.getBlock()) {
    		   if (offsetState.getValue(BlockComplexLog.LOG_AXIS) == BlockLog.EnumAxis.fromFacingAxis(facing.getAxis()) ) {
//	    			target = offsetPos;
	    			branchFacing = facing;
	    			
	    			break;
	    		}
    		}
    		
    	}
    	
    	System.out.println("branch facing: "+branchFacing);
    	
    	if(branchFacing == null) {
    		world.destroyBlock(target, true);
    	}
    	else {
//    		target = offsetPos;
    		while(true) {
    			target = target.offset(branchFacing);
				targetState = world.getBlockState(target);
				
				if(targetState.getBlock() != blockType) {
    				break;
    			}
    			else {
    				world.destroyBlock(target, true);
    			}
				
				for(EnumFacing facing : EnumSet.of(EnumFacing.DOWN, EnumFacing.UP, branchFacing.rotateY(), branchFacing.rotateYCCW())) 
				{
					offsetPos = target.offset(facing);
					offsetState = world.getBlockState(offsetPos);
					
					if(offsetState.getBlock() == blockType) {
				    	if(offsetState.getValue(BlockComplexLog.LOG_AXIS) == BlockLog.EnumAxis.fromFacingAxis(facing.getAxis())) {
				    		world.destroyBlock(offsetPos, true);
				    	}
					}
				}
				
    			
    			
    		}
    	}
    	
    	
    }
    

}