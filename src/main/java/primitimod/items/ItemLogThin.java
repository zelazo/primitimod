package primitimod.items;

import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import primitimod.PrimitiMod;
import primitimod.PrimitiModBlocks;
import primitimod.blocks.BlockComplexLog;

public class ItemLogThin extends Item {

	public ItemLogThin() {
		setCreativeTab(PrimitiMod.tab);
        setMaxStackSize(64);
        setRegistryName("logthin");
        setUnlocalizedName(getRegistryName().toString());
        setHasSubtypes(true);
    }
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		items.add(new ItemStack(this, 1, 0));
		items.add(new ItemStack(this, 1, 1));
		items.add(new ItemStack(this, 1, 2));
		items.add(new ItemStack(this, 1, 3));
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return getUnlocalizedName() + "." + stack.getItemDamage() % (BlockComplexLog.MAX_SIZE + 1);
	}

    @SideOnly(Side.CLIENT)
    public void initModel() {
    	ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName()+"_item", "size=0"));
    	ModelLoader.setCustomModelResourceLocation(this, 1, new ModelResourceLocation(getRegistryName()+"_item", "size=1"));
    	ModelLoader.setCustomModelResourceLocation(this, 2, new ModelResourceLocation(getRegistryName()+"_item", "size=2"));
        ModelLoader.setCustomModelResourceLocation(this, 3, new ModelResourceLocation(getRegistryName()+"_item", "size=3"));
//        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName() + "_0", "inventory"));
//        ModelLoader.setCustomModelResourceLocation(this, 1, new ModelResourceLocation(getRegistryName() + "_1", "inventory"));
    }
    
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
    		EnumFacing facing, float hitX, float hitY, float hitZ) {
    	
    	super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    	
    	if(!world.isRemote) {
	    		
	    	BlockPos newPos = pos.offset(facing);
	    	IBlockState target = world.getBlockState(newPos);
	    	ItemStack itemStack = player.getHeldItemMainhand();

    		if(target.getBlock().isAir(target, world, newPos)) {

    			world.setBlockState(newPos, PrimitiModBlocks.blockComplexLog.getDefaultState()
    					.withProperty(BlockComplexLog.SIZE, itemStack.getItemDamage())
    					.withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.fromFacingAxis(facing.getAxis()))
				);
    				
    			itemStack.shrink(1);
    			
    			return EnumActionResult.SUCCESS;
    		}
    	}
    	
    	return EnumActionResult.PASS;
    }

}