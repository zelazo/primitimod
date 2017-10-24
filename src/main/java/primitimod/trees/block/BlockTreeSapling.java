package primitimod.trees.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import primitimod.PrimitiMod;

public class BlockTreeSapling extends BlockBush {

	public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 1);
	protected static final AxisAlignedBB SAPLING_AABB = new AxisAlignedBB(0.09999999403953552D, 0.0D, 0.09999999403953552D, 0.8999999761581421D, 0.800000011920929D, 0.8999999761581421D);

	private BlockTreeRoot treeRoot;
	public BlockTreeRoot getTreeRoot() {
		return treeRoot;
	}
	public void setTreeRoot(BlockTreeRoot treeRoot) {
		this.treeRoot = treeRoot;
	}

	protected int lightLevelRequired = 9;
	
	public BlockTreeSapling(String registryName, BlockTreeRoot treeRoot) {
        super(Material.PLANTS);
        setCreativeTab(PrimitiMod.tab);
        setRegistryName(registryName);
        setUnlocalizedName(registryName);
        setDefaultState(blockState.getBaseState().withProperty(STAGE, 0));
        setSoundType(SoundType.PLANT);
        
        this.treeRoot = treeRoot;
    }
	
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isRemote) {
            super.updateTick(worldIn, pos, state, rand);

            if (worldIn.getLightFromNeighbors(pos.up()) >= lightLevelRequired) {// && rand.nextInt(7) == 0) {
                this.grow(worldIn, pos, state, rand);
            }
        }
    }

    public void grow(World world, BlockPos pos, IBlockState state, Random rand) {
    	System.out.println("sapling grow! "+ state.getValue(STAGE));
        if (((Integer)state.getValue(STAGE)).intValue() == 0) {
            world.setBlockState(pos, state.cycleProperty(STAGE));
        }
        else {
            this.generateTree(world, pos, state, rand);
        }
    }
    
    public void generateTree(World world, BlockPos pos, IBlockState state, Random rand) {

    	world.destroyBlock(pos, false);
    	world.setBlockState(pos.down(), treeRoot.getDefaultState());
    	
    }
    
    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState()
        		.withProperty(STAGE, Integer.valueOf(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | state.getValue(STAGE);
        return i;
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] { STAGE });
    }
    
	@SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
	
}
