package primitimod.tileentity;

import java.util.EnumSet;
import java.util.Random;
import java.util.Vector;
import java.util.stream.IntStream;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import primitimod.PrimitiModBlocks;
import primitimod.blocks.BlockComplexLog;

public class TreeRootTE extends TileEntity implements ITickable {

	private static final Block leavesBlock = Blocks.LEAVES; 
	private static final Block logBlock = PrimitiModBlocks.blockComplexLog; 
	
	private static Random random = new Random();
	
	private int tickCounter;
	private final int updatePace = 20;

	private int[] trunkSectionCount = new int[] { 0, 0, 0 };

	private static int[] trunkSectionMaxLength = new int[] { 2, 3, 4 };
	private static int trunkMaxHeight = IntStream.of(trunkSectionMaxLength).sum();
	
	private static final int branchMinHeight = 3;
//	private static final int branchMaxLength = 4;
	private static final int branchLengthDiff = 1;
	private static final int branchHeightSpread = 2;
	private static final int branchGrowthSpeed = 20;
	private static final int branchMaxGrowthSpeed = 100;
	private static final boolean canGrowMultiBranch = true;
	
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
		
		BlockPos target = pos.offset(EnumFacing.UP);
		
		System.out.println("height:"+ getHeight(target) +" tsc: "+trunkSectionCount[0]+" "+trunkSectionCount[1]+" "+trunkSectionCount[2]);
		
		if(getHeight(target) > trunkMaxHeight) {
			return;
		}
		
//		IBlockState targetState = world.getBlockState(target);
		
		GrowPartResult result = GrowPartResult.STOP;
		Vector<GrowAction> actions = new Vector<>();
		actions.add(GrowAction.TRUNK_HEIGHT);
		actions.add(GrowAction.TRUNK_WIDTH);
		actions.add(GrowAction.BRANCH);
		
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
		Block targetBlock = targetState.getBlock();
		
		if(action == GrowAction.TRUNK_HEIGHT) {
			if(targetBlock == Blocks.AIR || targetBlock == leavesBlock ) {
				int size = 2;
				
				if(trunkSectionCount[size] < trunkSectionMaxLength[size]) {
					
					addLog(target, EnumFacing.UP, size);
					
					changeTrunkSectionCount(size, 1);
					
					return GrowPartResult.DONE;
				}
				
			}
			else if(targetState.getBlock() == logBlock) {
				return GrowPartResult.CONTINUE;
			}
		}
		else if(action == GrowAction.TRUNK_WIDTH) {
			
			
			if(targetState.getBlock() == logBlock) {
				
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
			
			
			if(targetState.getBlock() == logBlock) {
				
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
					
					return growBranch(target, target, newDir, size, 0);
				}
			}
			else {
				return GrowPartResult.CONTINUE;
			}
		}
		
		
		return GrowPartResult.STOP;
	}
	
	

	public GrowPartResult growBranch(final BlockPos trunkPos, final BlockPos pos, final EnumFacing facing, final int size, final int currentLength) {
		BlockPos target = pos.offset(facing);
		IBlockState targetState = world.getBlockState(target);
		Block targetBlock = targetState.getBlock();
		
		if(targetBlock == Blocks.AIR || targetBlock == leavesBlock) {

			int diff = getBranchMaxLength(target) - currentLength;
			
			if(diff > 0) {
				addLog(target, facing, size);
				return GrowPartResult.DONE;
			}
			
		}
		else if(targetState.getBlock() == PrimitiModBlocks.blockComplexLog) {
			
			if(targetState.getValue(BlockComplexLog.SIZE) != size) {
				world.setBlockState(target, targetState.withProperty(BlockComplexLog.SIZE, size), 2);
			}
			
			return growBranch(trunkPos, target, facing, size, currentLength + 1);
		}
		
		return GrowPartResult.STOP;
	}
	
	public void addLog(final BlockPos target, final EnumFacing facing, final int size) {
		
		IBlockState state = logBlock.getDefaultState()
				.withProperty(BlockComplexLog.LOG_AXIS, BlockLog.EnumAxis.fromFacingAxis(facing.getAxis()))
				.withProperty(BlockComplexLog.SIZE, size);
		
		world.setBlockState(target, state, 2);
		
		for(EnumFacing e : EnumFacing.values()) {
			BlockPos targetOffset = target.offset(e);
			if(world.isAirBlock(targetOffset)) { 
				state = leavesBlock.getDefaultState()
						.withProperty(BlockLeaves.DECAYABLE, false)
						.withProperty(BlockLeaves.CHECK_DECAY, false);
				
				world.setBlockState(targetOffset, state, 2);
			}
		}
	}
	
	
	public boolean canGrowBranch(BlockPos target) {
		int height = getHeight(target);
		
		return (height > branchMinHeight) && 
			   (height % branchHeightSpread == 0) && 
			   (random.nextInt(branchMaxGrowthSpeed) < branchGrowthSpeed);
	}
	
	public int getBranchMaxLength(BlockPos target) {
		return (trunkMaxHeight - getHeight(target)) / 2;
	}
	
	public int getHeight(BlockPos target) {
		int result = target.getY() - this.pos.getY();
		return result < 1 ? 1 : result;
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
