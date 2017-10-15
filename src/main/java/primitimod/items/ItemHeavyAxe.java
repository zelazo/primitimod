package primitimod.items;

import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import primitimod.PrimitiMod;
import primitimod.core.PrimitiModBlocks;
import primitimod.trees.block.BlockComplexLog;

public class ItemHeavyAxe extends Item {

	public ItemHeavyAxe() {
		setCreativeTab(PrimitiMod.tab);
        setRegistryName("lumberheavyaxe");
        setMaxStackSize(1);
        setMaxDamage(100);
        setUnlocalizedName(getRegistryName().toString());
        setHarvestLevel("axe", 1);
        
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
            EntityPlayer player = (EntityPlayer)entityLiving;
            
            

            int power = this.getMaxItemUseDuration(stack) - timeLeft;
            
            if(power > 25) {
                
	        	if (!world.isRemote) {
	                RayTraceResult ray = player.rayTrace(3.0d, 0.2f);
	                BlockPos hitPos = ray.getBlockPos();
	                IBlockState hitBlockState = world.getBlockState(hitPos);

	                hitBlock(player, world, ray.getBlockPos());

	                int damage = setToolDamage(world, player, hitBlockState, hitPos);
	                
	                if(damage > 0) {
	                	world.playEvent(2001, hitPos, Block.getStateId(hitBlockState));
	                }
	                
		            world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
		            spawnSweepParticles(world, player);
	                
	                
	        	}
            }
            else {
            	if (!world.isRemote) {
                	world.getMinecraftServer().getCommandManager().executeCommand(player, "/time set day");
                	world.getMinecraftServer().getCommandManager().executeCommand(player, "/weather clear");
            	}
            }
        }
    }
    
    public int setToolDamage(World world, EntityPlayer player, IBlockState state, BlockPos pos) {

    	String harvestTool = state.getBlock().getHarvestTool(state);
        ItemStack heldItem = player.getHeldItemMainhand();
        
		int damage = 1;
		
		if(getHarvestLevel(heldItem, harvestTool, player, state) < 0) {
			damage = getDamageFromHarvestLevel(state.getBlockHardness(world, pos));
		}
		
        System.out.println("damage: "+damage);
        
        heldItem.damageItem(damage, player);
        
        return damage;
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
    
    public void spawnSweepParticles(World world, EntityPlayer player) {
    	double d0 = (double)(-MathHelper.sin(player.rotationYaw * 0.017453292F));
        double d1 = (double)MathHelper.cos(player.rotationYaw * 0.017453292F);
        double d2 = (double)(-MathHelper.sin(player.rotationPitch * 0.017453292F));
        
        if (world instanceof WorldServer) {
            ((WorldServer)world).spawnParticle(EnumParticleTypes.SWEEP_ATTACK, player.posX + d0, player.posY + (double)player.eyeHeight * 0.9D * (1.0D + d2), player.posZ + d1, 0, d0, 0.0D, d1, 0.0D);
//            ((WorldServer)world).spawnParticle(EnumParticleTypes.SWEEP_ATTACK, player.posX + d0, player.posY + (double)player.eyeHeight * 0.9D + (double)player.eyeHeight * 0.9D * d2, player.posZ + d1, 0, d0, 0.0D, d1, 0.0D);
        }
        
    }
    
    public boolean hitBlock(EntityPlayer player, World world, BlockPos pos) {
    	if(!world.isRemote) {

	    	IBlockState target = world.getBlockState(pos);
	    	
	    	if( PrimitiModBlocks.isLog(target.getBlock()) ) {
	    		
	    		if(target.getValue(BlockComplexLog.TYPE) == BlockComplexLog.EnumLogType.DAMAGED) {
		    		System.out.println("axe hit!");
		    		BlockPos toCut = findBlockToCut(world, pos, target.getBlock());
		    		
		    		cutLog(world, toCut);
		    		
		    		return true;
		    	}
	    		else {
	    		
	    			world.setBlockState(pos, target
						.withProperty(BlockComplexLog.TYPE, BlockComplexLog.EnumLogType.DAMAGED ) 
					);
		    			
	    			return true;
		    	}
		    	
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
    
    public BlockPos findBlockToCut(World world, BlockPos hitBlock, Block blockType) {
    	BlockPos target = hitBlock;
    	BlockPos result = hitBlock;
    	IBlockState hitState = world.getBlockState(hitBlock);
    	IBlockState targetState = null;
    	boolean done = false;
    	
    	if(hitState.getBlock() == Blocks.AIR) {
    		return target;
    	}
    	
    	while(!done) {

			target = target.up();
    		targetState = world.getBlockState(target);
    		
    		done = true;
    		if(targetState.getBlock() == blockType && targetState.getValue(BlockComplexLog.LOG_AXIS) == BlockLog.EnumAxis.Y) 
    		{
    			result = target;
    			done = false;
    		}
    		else {
    			for(EnumFacing facing : EnumFacing.HORIZONTALS) {
    				
    				targetState = world.getBlockState(target.offset(facing));
    				
    				if(targetState.getBlock() == blockType && 
					   targetState.getValue(BlockComplexLog.LOG_AXIS) == BlockLog.EnumAxis.Y) 
    				{
    					target = target.offset(facing);
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
    	EnumFacing branchFacing = null;
    	
    	for(EnumFacing facing : EnumFacing.HORIZONTALS) {
    		offsetPos = target.offset(facing);
    		offsetState = world.getBlockState(offsetPos);

    		if(offsetState.getBlock() == targetState.getBlock()) {
    		   if (offsetState.getValue(BlockComplexLog.LOG_AXIS) == BlockLog.EnumAxis.fromFacingAxis(facing.getAxis()) ) {
	    			branchFacing = facing;
	    			break;
	    		}
    		}
    	}
    	
    	if(branchFacing == null) {
    		world.destroyBlock(target, true);
    	}
    	else {
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