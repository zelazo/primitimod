package primitimod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import primitimod.PrimitiMod;
import primitimod.core.PrimitiModItems;

public class BlockRockPile extends Block {
		
	public static final int MAX_PILESIZE = 8;
	public static final PropertyInteger PILESIZE = PropertyInteger.create("pilesize", 0, MAX_PILESIZE-1);

	public static final AxisAlignedBB bb = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D);
	
    public BlockRockPile() {
        super(Material.ROCK);
        setCreativeTab(PrimitiMod.tab);
        setRegistryName("rockpile");
        setHardness(10f);
        setHarvestLevel("shovel", 0);
        setUnlocalizedName(getRegistryName().toString());
        
        setDefaultState(blockState.getBaseState().withProperty(PILESIZE, 0));
    }
    
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return bb;
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
//        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
    
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        world.setBlockState(pos, state.withProperty(PILESIZE, 0), 2);
    }
    
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
    		EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    	
    	if(!world.isRemote) {
    	
	    	int pileSize = state.getValue(PILESIZE);

	    	ItemStack playerItemStack = player.getHeldItemMainhand();
	    	Item playerItem = playerItemStack.getItem();
	    	Item pileItem = PrimitiModItems.itemStoneRock;
	    	
	    	if(playerItem.equals(pileItem)) {
	    		if(pileSize < MAX_PILESIZE - 1) {
	        		world.setBlockState(pos, state.withProperty(PILESIZE, pileSize + 1));
	        		playerItemStack.shrink(1);
	        	}
	    	}
	    	else if(playerItem.equals(Items.AIR)) {
	    		
    			if(pileSize > 0) {
    				world.setBlockState(pos, state.withProperty(PILESIZE, pileSize - 1));
    			}
    			else {
    				world.setBlockToAir(pos);
    			}
    			
        		BlockPos entityPos = pos.add(0.5d, 0.0d, 0.5d);//.up();
        		EntityItem spawnEntityItem = new EntityItem(world, entityPos.getX(), entityPos.getY(), entityPos.getZ(), 
        				new ItemStack(pileItem, 1));
        		
        		world.spawnEntity(spawnEntityItem);
	        	
	    	}
    	}
    	
    	return true;
    }
    
    @Override
    public boolean isOpaqueCube(IBlockState state) { 
    	return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) { 
    	return false;
    }
    
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) { 
    	return EnumBlockRenderType.MODEL;
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState()
                .withProperty(PILESIZE, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(PILESIZE);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, PILESIZE);
    }

}