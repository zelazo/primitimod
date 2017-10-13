package primitimod.items;

import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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
import primitimod.trees.block.BlockComplexLog;

public class ItemAxe extends Item {

	public ItemAxe() {
		setCreativeTab(PrimitiMod.tab);
        setRegistryName("lumberaxe");
        setMaxStackSize(1);
        setMaxDamage(100);
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

	    	IBlockState target = world.getBlockState(pos);
	    	
	    	if( PrimitiModBlocks.isLog(target.getBlock()) ) {
	    		
    			world.setBlockState(pos, PrimitiModBlocks.blockBareLog.getDefaultState()
					.withProperty(BlockComplexLog.LOG_AXIS, target.getValue(BlockComplexLog.LOG_AXIS))
					.withProperty(BlockComplexLog.SIZE, target.getValue(BlockComplexLog.SIZE) + 1 ) 
				);
	    			
    			player.getHeldItemMainhand().damageItem(1, player);
    			return EnumActionResult.SUCCESS;
	    	}
	    	else if(target.getBlock() == PrimitiModBlocks.blockBareLog) {
	    		System.out.println("axe hit!");
	    		BlockPos toCut = findBlockToCut(world, pos);
	    		
	    		cutLog(world, toCut);
//	    		world.destroyBlock(toCut, true);
//	    		world.setBlockState(toCut, Blocks.AIR.getDefaultState());
//	    		world.setBlockToAir(toCut);
	    		
	    		return EnumActionResult.SUCCESS;
	    	}
    	}
    	
    	return EnumActionResult.PASS;
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