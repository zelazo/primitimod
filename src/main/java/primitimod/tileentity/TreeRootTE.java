package primitimod.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.stream.IntStream;

import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import primitimod.PrimitiModBlocks;
import primitimod.blocks.BlockComplexLog;

public class TreeRootTE extends TileEntity implements ITickable {

	private static Random random = new Random();
	
	private int tickCounter;
	private final int updatePace = 20;

	private int[] trunkSectionCount = new int[] { 0, 0, 0 };

	private static int[] trunkSectionMaxLength = new int[] { 5, 4, 6 };
	private static int trunkMaxHeight = IntStream.of(trunkSectionMaxLength).sum();
	
	private static final int branchMinHeight = 5;
	private static final int branchMaxLength = 4;
	private static final int branchLengthDiff = 1;
	private static final boolean canGrowMultiBranch = false;
	
	private Map<BlockPos, int[]> branchLength = new HashMap<>();
	
	@Override
	public void update() {
		if (!world.isRemote) {
			tickCounter++;
			
			if(tickCounter > updatePace) {
				System.out.println("tick!");
			
				grow(pos);
				tickCounter = 0;
			}
			
		}
	}
	
	public void grow(BlockPos pos) {
		
		System.out.println("pos:"+ pos +" tsc: "+trunkSectionCount[0]+" "+trunkSectionCount[1]+" "+trunkSectionCount[2]);
		
		BlockPos target = pos.offset(EnumFacing.UP);
		
		
//		IBlockState targetState = world.getBlockState(target);
		
		GrowPartResult result = GrowPartResult.STOP;
		Vector<GrowAction> actions = new Vector<>();
		actions.add(GrowAction.TRUNK_HEIGHT);
		actions.add(GrowAction.TRUNK_WIDTH);
		
		while(! (result == GrowPartResult.DONE ||  actions.isEmpty()) ) {
		
			GrowAction chosenAction = actions.get(random.nextInt(actions.size()));
			actions.remove(chosenAction);
		
			GrowPartResult tmp = growPart(target, chosenAction);
			
			result = tmp.ordinal() > result.ordinal() ? tmp : result;
			
			System.out.println("action: "+chosenAction.name() +" -> "+ result);
			
		}
		
		if(result == GrowPartResult.CONTINUE) {
			grow(target);
		}
		
	}
	
	public GrowPartResult growPart(BlockPos target, GrowAction action) {
		IBlockState targetState = world.getBlockState(target);
		
		if(action == GrowAction.TRUNK_HEIGHT) {
			if(world.isAirBlock(target)) {
				int size = 2;
				
				if(trunkSectionCount[size] < trunkSectionMaxLength[size]) {
					
					IBlockState newState = PrimitiModBlocks.blockComplexLog.getDefaultState()
							.withProperty(BlockComplexLog.LOG_AXIS, BlockLog.EnumAxis.fromFacingAxis(EnumFacing.UP.getAxis()))
							.withProperty(BlockComplexLog.SIZE, size);
					
					world.setBlockState(target, newState, 2);
					changeTrunkSectionCount(size, 1);
					
					return GrowPartResult.DONE;
				}
				
			}
			else if(targetState.getBlock() == PrimitiModBlocks.blockComplexLog) {
				return GrowPartResult.CONTINUE;
			}
		}
		else if(action == GrowAction.TRUNK_WIDTH) {
			
			
			if(targetState.getBlock() == PrimitiModBlocks.blockComplexLog) {
				
				int size = targetState.getValue(BlockComplexLog.SIZE);
				
				if(size != 0 && isTrunkSectionMax(size) && !isTrunkSectionMax(size-1)) {
					
					IBlockState newState = PrimitiModBlocks.blockComplexLog.getDefaultState()
							.withProperty(BlockComplexLog.SIZE, size - 1 );
					
					world.setBlockState(target, newState, 2);
					changeTrunkSectionCount(size-1	,  1);
					changeTrunkSectionCount(size	, -1);
					
					return GrowPartResult.DONE;
				}
				else {
					return GrowPartResult.CONTINUE;
				}
			}
			
		}
		else if(action == GrowAction.BRANCH) {
			
			
			if(targetState.getBlock() == PrimitiModBlocks.blockComplexLog) {
				
				int size = targetState.getValue(BlockComplexLog.SIZE);
			
				if(canGrowBranch(target)) {
					EnumFacing newDir = EnumFacing.HORIZONTALS[random.nextInt(4)];
					
					if(!canGrowMultiBranch) {
						
						for(EnumFacing e : EnumFacing.HORIZONTALS) {
							BlockPos offsetTarget = target.offset(e);
							IBlockState offsetState = world.getBlockState(offsetTarget);
							
							if(offsetState.getBlock() == PrimitiModBlocks.blockComplexLog) {

								BlockLog.EnumAxis axis = offsetState.getValue(BlockLog.LOG_AXIS);
								
								if(axis == BlockLog.EnumAxis.fromFacingAxis(e.getAxis()) ) {
									newDir = e;
									break;
								}
							}
							
						}
					}
					
					return growBranch(target, target, newDir, size);
				}
			}
			return GrowPartResult.CONTINUE;
		}
		
		
		return GrowPartResult.STOP;
	}
	
	

	public GrowPartResult growBranch(final BlockPos trunkPos, BlockPos pos, EnumFacing facing, int size) {
		BlockPos target = pos.offset(facing);
		IBlockState targetState = world.getBlockState(target);
		
		int[] branchLengths = branchLength.get(trunkPos);
		
		
		int currentBranchLength = 0;
		
		if(branchLengths == null) {
			branchLengths = new int[] {0, 0, 0, 0};
			branchLength.put(trunkPos, branchLengths);
		}
		else {
			System.out.println("bl: "+branchLengths.toString());
			currentBranchLength = branchLengths[facing.getHorizontalIndex()];
		}
		
		System.out.println("cbl: "+currentBranchLength + " -> " + branchLengths[facing.getHorizontalIndex()]);
		
		
		if(world.isAirBlock(target)) {
			
			if(currentBranchLength < branchMaxLength) {
				
				IBlockState newState = PrimitiModBlocks.blockComplexLog.getDefaultState()
						.withProperty(BlockComplexLog.LOG_AXIS, BlockLog.EnumAxis.fromFacingAxis(facing.getAxis()))
						.withProperty(BlockComplexLog.SIZE, size);
				
				world.setBlockState(target, newState, 2);
				
				branchLengths[facing.getHorizontalIndex()]++;
				branchLength.put(trunkPos, branchLengths);
				
				return GrowPartResult.DONE;
			}
			
		}
		else if(targetState.getBlock() == PrimitiModBlocks.blockComplexLog) {
			return growBranch(trunkPos, target, facing, size);
		}
		
		return GrowPartResult.STOP;
	}
	

	public boolean canGrowBranch(BlockPos target) {
		return target.getY() - this.pos.getY() > branchMinHeight;
	}
	
	public boolean isTrunkSectionMax(int size) {
		if(size >= 0 && size < trunkSectionCount.length) {
			return trunkSectionCount[size] == trunkSectionMaxLength[size];
		}
		return false;
	}
	
	public void changeTrunkSectionCount(int size, int value) {
		if(size >= 0 && size < trunkSectionCount.length) {
			trunkSectionCount[size] += value;
		}
	}
	

	@Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setInteger("tickCounter", this.tickCounter);
        
		return compound;
	}
	
	@Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.tickCounter = compound.getInteger("tickCounter");
	}
	
	private enum GrowAction {
		TRUNK_HEIGHT,
		TRUNK_WIDTH,
		BRANCH,
		;
		
		public static int getLength() {
			return values().length;
		}
	}
	
	private enum GrowPartResult {
		STOP,
		CONTINUE,
		DONE,
		;
		
	}
}
