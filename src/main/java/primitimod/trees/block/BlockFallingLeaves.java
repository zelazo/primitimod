package primitimod.trees.block;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import primitimod.PrimitiMod;
import primitimod.core.PrimitiModBlocks;

public class BlockFallingLeaves extends BlockFalling {

    protected boolean leavesFancy = true;
    
	public BlockFallingLeaves(String registryName) {
        super(Material.LEAVES);
        setCreativeTab(PrimitiMod.tab);
        setRegistryName(registryName);
        setUnlocalizedName(registryName);
        setHardness(0.2F);
        setLightOpacity(1);
        setSoundType(SoundType.PLANT);
    }
	
	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
    {
        return true;
    }
	
	@Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isRemote) {
        	
        	boolean hasLogSupport = false;
        	
        	for(EnumFacing facing : EnumFacing.VALUES) {
        		
        		if(PrimitiModBlocks.isLog(worldIn.getBlockState(pos.offset(facing)).getBlock())) {
        			hasLogSupport = true;
        			break;
        		}
        	}
        		
        	if(!hasLogSupport)	
        		super.updateTick(worldIn, pos, state, rand);
        }
    }
	
	@Override
    public void onEndFalling(World worldIn, BlockPos pos, IBlockState p_176502_3_, IBlockState p_176502_4_) {
    }
    
    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
    	return 250;
    }
	
	/**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return !this.leavesFancy;
    }
    
    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }
    
    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }
    
    /**
     * Called When an Entity Collided with the Block
     */
    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        entityIn.motionX *= 0.7D;
        entityIn.motionX *= 0.7D;
        entityIn.motionZ *= 0.7D;
    }

    /**
     * Pass true to draw this block using fancy graphics, or false for fast graphics.
     */
    @SideOnly(Side.CLIENT)
    public void setGraphicsLevel(boolean fancy)
    {
        this.leavesFancy = fancy;
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return this.leavesFancy ? BlockRenderLayer.CUTOUT_MIPPED : BlockRenderLayer.SOLID;
    }

    public boolean causesSuffocation(IBlockState state)
    {
        return false;
    }
    

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return !this.leavesFancy && blockAccess.getBlockState(pos.offset(side)).getBlock() == this ? false : super.shouldSideBeRendered(blockState, blockAccess, pos, side);
    }
	
    @SideOnly(Side.CLIENT)
    public int getBlockColor()
    {
        return ColorizerFoliage.getFoliageColor(0.5D, 1.0D);
    }

    @SideOnly(Side.CLIENT)
    public int getRenderColor(IBlockState state)
    {
        return ColorizerFoliage.getFoliageColorBasic();
    }

    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass)
    {
        return BiomeColorHelper.getFoliageColorAtPos(worldIn, pos);
    }
    
    @Override
    public int getDustColor(IBlockState state) {
    	return getBlockColor();//getRenderColor(state);//MapColor.FOLIAGE.colorValue;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (worldIn.isRainingAt(pos.up()) && !worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos, EnumFacing.UP) && rand.nextInt(15) == 1)
        {
            double d0 = (double)((float)pos.getX() + rand.nextFloat());
            double d1 = (double)pos.getY() - 0.05D;
            double d2 = (double)((float)pos.getZ() + rand.nextFloat());
            worldIn.spawnParticle(EnumParticleTypes.DRIP_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
        else {
        	super.randomDisplayTick(stateIn, worldIn, pos, rand);
        }
    }
    
    
	@SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
	
}
