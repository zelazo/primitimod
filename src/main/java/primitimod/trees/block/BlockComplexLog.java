package primitimod.trees.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import primitimod.PrimitiMod;
import primitimod.core.PrimitiModBlocks;

public class BlockComplexLog extends BlockLog {

	public static final int MAX_SIZE = 3;
	public static final PropertyInteger SIZE = PropertyInteger.create("size", 0, MAX_SIZE);

    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");
    public static final PropertyBool UP = PropertyBool.create("up");
    public static final PropertyBool DOWN = PropertyBool.create("down");
	
    protected static final AxisAlignedBB[][] BOUNDING_BOXES = new AxisAlignedBB[][] {
    	{ //LOG_AXIS X
/*SIZE=0*/ new AxisAlignedBB(0.0D, 0.000D, 0.000D, 1.0D, 1.000D, 1.000D),
/*SIZE=1*/ new AxisAlignedBB(0.0D, 0.125D, 0.125D, 1.0D, 0.875D, 0.875D),
/*SIZE=2*/ new AxisAlignedBB(0.0D, 0.250D, 0.250D, 1.0D, 0.750D, 0.750D),
/*SIZE=3*/ new AxisAlignedBB(0.0D, 0.250D, 0.250D, 1.0D, 0.750D, 0.750D)
    	},
    	{ //LOG_AXIS Y
/*SIZE=0*/ new AxisAlignedBB(0.000D, 0.0D, 0.000D, 1.000D, 1.0D, 1.000D),
/*SIZE=1*/ new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D),
/*SIZE=2*/ new AxisAlignedBB(0.250D, 0.0D, 0.250D, 0.750D, 1.0D, 0.750D),
/*SIZE=3*/ new AxisAlignedBB(0.250D, 0.0D, 0.250D, 0.750D, 1.0D, 0.750D)
      	}, 
    	{ //LOG_AXIS Z
/*SIZE=0*/ new AxisAlignedBB(0.000D, 0.000D, 0.0D, 1.000D, 1.000D, 1.0D),
/*SIZE=1*/ new AxisAlignedBB(0.125D, 0.125D, 0.0D, 0.875D, 0.875D, 1.0D),
/*SIZE=2*/ new AxisAlignedBB(0.250D, 0.250D, 0.0D, 0.750D, 0.750D, 1.0D),
/*SIZE=3*/ new AxisAlignedBB(0.250D, 0.250D, 0.0D, 0.750D, 0.750D, 1.0D)
      	}
    };

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        state = this.getActualState(state, source, pos);
        int i = state.getValue(BlockLog.LOG_AXIS).ordinal();
        int j = state.getValue(SIZE);
        
        return BOUNDING_BOXES[i][j];
    }

    
    public BlockComplexLog() {
        super();
        setCreativeTab(PrimitiMod.tab);
        setRegistryName("complexlog");
        setUnlocalizedName(getRegistryName().toString());
        setDefaultState(this.blockState.getBaseState()
        		.withProperty(SIZE, 0)
        		.withProperty(LOG_AXIS, BlockLog.EnumAxis.Y));
        
    }
    
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer,
    		ItemStack stack) {
//    	super.onBlockPlacedBy(world, pos, state, placer, stack);
    	world.setBlockState(pos, state.withProperty(SIZE, 0), 2);
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta) {
    	
    	IBlockState iblockstate = this.getDefaultState().withProperty(SIZE, meta & 3);
    	
        switch (meta & 12)
        {
            case 0:
                iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.Y);
                break;
            case 4:
                iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.X);
                break;
            case 8:
                iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.Z);
                break;
            default:
                iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.NONE);
        }

        return iblockstate;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
    	
    	byte b0 = 0;
        int i = b0 | state.getValue(SIZE);

        switch (BlockComplexLog.SwitchEnumAxis.AXIS_LOOKUP[((BlockComplexLog.EnumAxis)state.getValue(LOG_AXIS)).ordinal()])
        {
            case 1:
                i |= 4;
                break;
            case 2:
                i |= 8;
                break;
            case 3:
                i |= 12;
        }

        return i;
    	
    }
    
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return state.withProperty(NORTH, canLogConnectTo(world, pos, EnumFacing.NORTH))
                    .withProperty(EAST,  canLogConnectTo(world, pos, EnumFacing.EAST))
                    .withProperty(SOUTH, canLogConnectTo(world, pos, EnumFacing.SOUTH))
                    .withProperty(WEST,  canLogConnectTo(world, pos, EnumFacing.WEST))
                    .withProperty(UP,  	 canLogConnectTo(world, pos, EnumFacing.UP))
                    .withProperty(DOWN,  canLogConnectTo(world, pos, EnumFacing.DOWN));
    }
    
    private boolean canLogConnectTo(IBlockAccess world, BlockPos pos, EnumFacing facing)
    {
        BlockPos other = pos.offset(facing);
        IBlockState state = world.getBlockState(other);
        Block block = state.getBlock();
        
        if(block == PrimitiModBlocks.blockComplexLog) {
        
	        switch(facing) {
	        	case DOWN:
	        	case UP:
	        		return state.getValue(LOG_AXIS) == BlockLog.EnumAxis.Y;
	        	case NORTH:
	        	case SOUTH:
	        		return state.getValue(LOG_AXIS) == BlockLog.EnumAxis.Z;
	        	case EAST:
	        	case WEST:
	        		return state.getValue(LOG_AXIS) == BlockLog.EnumAxis.X;
	        }
        }
        
        return false;
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
    	
        return new BlockStateContainer(this, new IProperty[] { LOG_AXIS, SIZE, UP, DOWN, NORTH, SOUTH, EAST, WEST });
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
//        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName()+"_item", "type=0"));
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
    

    static final class SwitchEnumAxis
        {
            static final int[] AXIS_LOOKUP = new int[BlockLog.EnumAxis.values().length];

            static
            {
                try
                {
                    AXIS_LOOKUP[BlockLog.EnumAxis.X.ordinal()] = 1;
                }
                catch (NoSuchFieldError var3)
                {
                    ;
                }

                try
                {
                    AXIS_LOOKUP[BlockLog.EnumAxis.Z.ordinal()] = 2;
                }
                catch (NoSuchFieldError var2)
                {
                    ;
                }

                try
                {
                    AXIS_LOOKUP[BlockLog.EnumAxis.NONE.ordinal()] = 3;
                }
                catch (NoSuchFieldError var1)
                {
                    ;
                }
            }
        }
   
}