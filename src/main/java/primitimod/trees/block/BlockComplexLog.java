package primitimod.trees.block;

import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import primitimod.PrimitiMod;

public class BlockComplexLog extends BlockLog {

	public static final PropertyEnum<BlockComplexLog.EnumLogType> TYPE = PropertyEnum.<BlockComplexLog.EnumLogType>create("type", BlockComplexLog.EnumLogType.class);
	
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
        int j = state.getValue(TYPE).getIndex();
        
        return BOUNDING_BOXES[i][j];
    }

    
    public BlockComplexLog(String registryName) {
        super();
        setCreativeTab(PrimitiMod.tab);
        setRegistryName(registryName);
        setUnlocalizedName(getRegistryName().toString());
        setDefaultState(this.blockState.getBaseState()
        		.withProperty(TYPE, EnumLogType.LARGE)
        		.withProperty(LOG_AXIS, BlockLog.EnumAxis.Y));
    }
    
    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
    	return 250;
    }
    
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer,
    		ItemStack stack) {
    	super.onBlockPlacedBy(world, pos, state, placer, stack);
    	
    	world.setBlockState(pos, state.withProperty(TYPE, EnumLogType.getByIndex(stack.getItemDamage())) );
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta) {
    	
    	IBlockState iblockstate = this.getDefaultState().withProperty(TYPE, EnumLogType.getByIndex(meta & 3));
    	
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
        int i = b0 | state.getValue(TYPE).getIndex();

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
        
        if(block == this) {
        
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
        else if(state.getMaterial() == Material.LEAVES ) {
        	return true;
        }
        
        return false;
    }
    

    public static String getUnlocalizedItemBlockName(String blockName, int meta) {
    	meta = meta % (EnumLogType.getCount());
    	String suffix = EnumLogType.getByIndex(meta).getName();
    	
    	return blockName + "." + suffix;
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
    	
        return new BlockStateContainer(this, new IProperty[] { LOG_AXIS, TYPE, UP, DOWN, NORTH, SOUTH, EAST, WEST });
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
    	
    	for(EnumLogType type : EnumSet.of(EnumLogType.LARGE, EnumLogType.MEDIUM, EnumLogType.SMALL)) {
    		System.out.println("registering BlockComplexLog("+getRegistryName()+") model variant: type=: "+type.getName());
    		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getIndex(), new ModelResourceLocation(getRegistryName()+"_item", "type="+type.getName()));
//    		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getIndex(), new ModelResourceLocation(getRegistryName()+"_item", "size="+type.getIndex()));
    	}
    }
    
    @Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
    	for(EnumLogType type : EnumSet.of(EnumLogType.LARGE, EnumLogType.MEDIUM, EnumLogType.SMALL)) {
    		items.add(new ItemStack(this, 1, type.getIndex()));
    	}
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
    
    public enum EnumLogType implements IStringSerializable {
    	LARGE(0, "large"),
    	MEDIUM(1, "medium"),
    	SMALL(2, "small"),
    	DAMAGED(3, "damaged");
    	
    	private int index;
    	private String name;
    	
    	private EnumLogType(int index, String name) {
			this.index = index;
			this.name = name;
		}

		public int getIndex() {
			return index;
		}
		
		public static int getCount() {
			return EnumLogType.values().length;
		}
		
		public static EnumLogType getByIndex(int index) {
			return values()[index];
		}
		
		@Override
		public String getName() {
			return name;
		}


		public EnumLogType getBigger() {
			switch(this) {
				case LARGE: 
				case MEDIUM:
					return LARGE;
				case SMALL:
					return MEDIUM;
				case DAMAGED:
					return DAMAGED;
				default:
					return this;
			}
		}
		
		public EnumLogType getSmaller() {
			switch(this) {
				case SMALL: 
				case MEDIUM:
					return SMALL;
				case LARGE:
					return MEDIUM;
				case DAMAGED:
					return DAMAGED;
				default:
					return this;
			}
		}

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