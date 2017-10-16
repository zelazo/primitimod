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
import primitimod.blocks.BlockLumberPile;
import primitimod.core.PrimitiModBlocks;

public class ItemLumber extends Item {

	private BlockLumberPile lumberPile;
	
	public ItemLumber() {//(String registryName, BlockLumberPile lumberPile) {
		setCreativeTab(PrimitiMod.tab);
        setRegistryName("lumber");
        setMaxStackSize(16);
        setUnlocalizedName(getRegistryName().toString());
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
    
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
    		EnumFacing facing, float hitX, float hitY, float hitZ) {
    	
    	if(!world.isRemote) {

    		EnumActionResult result = EnumActionResult.FAIL;
    		
	    	BlockPos newPos = pos.offset(facing);
	    	IBlockState target = world.getBlockState(newPos);
	    	IBlockState targetBottom = world.getBlockState(newPos.down());
	    	
	    	if(targetBottom.isSideSolid(world, newPos.down(), EnumFacing.UP)) {
	    		
	    		if(target.getBlock().isAir(target, world, newPos)) {

	    			int itemAmount = 1;
	    			if(player.isSneaking()) {
	    				itemAmount = player.getHeldItemMainhand().getCount();
	    			}
	    			world.setBlockState(newPos, PrimitiModBlocks.blockLumberPile.getDefaultState()
	    					.withProperty(BlockLumberPile.PILESIZE, itemAmount - 1));
	    			
	    			player.getHeldItemMainhand().shrink(itemAmount);
	    			
	    			result = EnumActionResult.SUCCESS;
	    		}
	    		else if(target.getBlock().equals(PrimitiModBlocks.blockLumberPile)) {
	    			System.out.println("lumberpile!");
	    			target.getBlock().onBlockActivated(world, newPos, target, player, hand, facing, hitX, hitY, hitZ);
	    			result = EnumActionResult.SUCCESS;
	    		}
	    		
	        	return result;
	    	}
    	}
    	
    	return EnumActionResult.PASS;
    }

}