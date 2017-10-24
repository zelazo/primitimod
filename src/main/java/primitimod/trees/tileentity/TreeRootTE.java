package primitimod.trees.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.stream.IntStream;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import primitimod.core.PrimitiModBlocks;
import primitimod.trees.block.BlockComplexLog;
import primitimod.trees.block.BlockComplexLog.EnumLogType;
import primitimod.utils.BlockPosUtils;

public class TreeRootTE extends TileEntity  implements ITickable {

	protected Block leavesBlock = Blocks.LEAVES; 
	protected Block logBlock = Blocks.LOG; 
	protected Block vineBlock = Blocks.VINE; 
	protected int   growthRate = 20;
	protected int[] trunkSectionMaxLength = new int[] { 2, 3, 4 };
	protected int 	leavesMinHeight = 3;
	protected int 	branchMinHeight = 3;
	protected int 	branchHeightSpread = 2;
	protected int   branchGrowthSpeed = 20;
	protected int brachCountPerLevel = 4;
	protected boolean canBranchSplit = true;
	protected int branchSplitSpread = 2;
	protected int leavesDensity = 1;
	protected boolean hasVines = false;
	
	protected int trunkMaxHeight = IntStream.of(trunkSectionMaxLength).sum();

	protected static final Random random = new Random();
	private static final int branchMaxGrowthSpeed = 100;
	
	/*NBTTag "tickCounter" */ private int tickCounter;
	
	protected void resizeParams(float factor) {
		assert factor > 1.0f && factor < 2.0f;
		
		this.trunkSectionMaxLength[0] = (int)(trunkSectionMaxLength[0] * factor);
		this.trunkSectionMaxLength[1] = (int)(trunkSectionMaxLength[1] * factor);
		this.trunkSectionMaxLength[2] = (int)(trunkSectionMaxLength[2] * factor);
		this.trunkMaxHeight = IntStream.of(trunkSectionMaxLength).sum();
		this.leavesMinHeight = (int)(leavesMinHeight * factor);
		this.branchMinHeight = (int)(branchMinHeight * factor);
		this.branchHeightSpread = (int)(branchHeightSpread * factor);
	}
	
//	@Override
	public void update() {
		if (!world.isRemote) {
			tickCounter++;
			
			if(tickCounter > growthRate) {

				int[] trunkSectionCount = new int[] { 0, 0, 0 };
				
				grow(pos, trunkSectionCount);
				
				growWidth(pos, trunkSectionCount);
				
				tickCounter = 0;
			}
		}
	}
	
	public void growWidth(BlockPos pos, int[] trunkSectionCount) {

		BlockPos target = getNextTrunkPos(pos);
		IBlockState targetState = world.getBlockState(target);
		
		while(getHeight(target) < trunkMaxHeight) {
			
			targetState = world.getBlockState(target);
			
			if(targetState.getBlock() == logBlock) {
				BlockComplexLog.EnumLogType logType = targetState.getValue(BlockComplexLog.TYPE);

				if(logType != EnumLogType.LARGE && 
				   isTrunkSectionMax(trunkSectionCount, logType.getIndex()) && 
				   !isTrunkSectionMax(trunkSectionCount, logType.getBigger().getIndex())
				) {
					
					IBlockState newState = logBlock.getDefaultState()
							.withProperty(BlockComplexLog.TYPE, logType.getBigger() );
					
					world.setBlockState(target, newState, 2);
					
					BlockPos supportTarget = target.down();
					IBlockState supportState = world.getBlockState(supportTarget);
					
					if(supportState.getBlock() == logBlock) {
						world.setBlockState(supportTarget, supportState.withProperty(BlockComplexLog.TYPE, logType.getBigger()), 2);
					}
					
					break;
				}
				else {
					target = getNextTrunkPos(target);
				}
			}
			else {
				break;
			}
			
		}
		
	}
	

	public void grow(BlockPos pos, int[] trunkSectionCount) {
		GrowPartResult result = GrowPartResult.CONTINUE;
		BlockPos target = pos;
		
		while(result == GrowPartResult.CONTINUE) {
			
			target = getNextTrunkPos(target);
//			System.out.println("height:"+ getHeight(target) +" tsc: "+trunkSectionCount[0]+" "+trunkSectionCount[1]+" "+trunkSectionCount[2]);
			
			if(getHeight(target) > trunkMaxHeight) {
				return;
			}
			
			Vector<GrowAction> actions = new Vector<>();
			actions.add(GrowAction.TRUNK_HEIGHT);
			actions.add(GrowAction.BRANCH);
		
			while(! (result == GrowPartResult.DONE ||  actions.isEmpty()) ) {
				
				GrowAction chosenAction = actions.get(random.nextInt(actions.size()));
				actions.remove(chosenAction);
			
				GrowPartResult tmp = growPart(target, chosenAction, trunkSectionCount);
				
				result = tmp.ordinal() > result.ordinal() ? tmp : result;
				
//				System.out.println("action: "+chosenAction.name() +" -> "+ result);
				
			}
		}
	}
	
	public GrowPartResult growPart(BlockPos target, GrowAction action, int[] trunkSectionCount) {
		IBlockState targetState = world.getBlockState(target);
		Block targetBlock = targetState.getBlock();
		
		if(action == GrowAction.TRUNK_HEIGHT) {
			if(canPlaceLog(targetBlock)) {
				BlockComplexLog.EnumLogType logType = BlockComplexLog.EnumLogType.SMALL;
				
				if(trunkSectionCount[logType.getIndex()] < trunkSectionMaxLength[logType.getIndex()]) {
					
					addLog(target, EnumFacing.UP, logType, getHeight(target) > leavesMinHeight && leavesDensity > 2);
					
					changeTrunkSectionCount(trunkSectionCount, logType.getIndex(), 1);
					
					return GrowPartResult.DONE;
				}
				
			}
			else if(targetState.getBlock() == logBlock) {
				changeTrunkSectionCount(trunkSectionCount, targetState.getValue(BlockComplexLog.TYPE).getIndex(), 1);
				return GrowPartResult.CONTINUE;
			}
		}
		else if(action == GrowAction.BRANCH) {
			
			if(targetState.getBlock() == logBlock) {
				
				if(canGrowBranch(target)) {
					EnumFacing newDir = EnumFacing.HORIZONTALS[random.nextInt(4)];
					
					if(brachCountPerLevel < 4) {
						List<EnumFacing> branches = new ArrayList<>();
						
						for(EnumFacing e : EnumFacing.HORIZONTALS) {
							BlockPos offsetTarget = target.offset(e);
							IBlockState offsetState = world.getBlockState(offsetTarget);
							
							if(offsetState.getBlock() == logBlock) {

								BlockLog.EnumAxis axis = offsetState.getValue(BlockLog.LOG_AXIS);
								
								if(axis == BlockLog.EnumAxis.fromFacingAxis(e.getAxis()) ) {
									branches.add(e);
								}
							}
						}
						
						if(branches.size() >= brachCountPerLevel) {
							newDir = branches.get(random.nextInt(branches.size()));
						}
					}
					
					return growBranch(target, target, newDir, targetState.getValue(BlockComplexLog.TYPE), 0);
				}
			}
			else {
				return GrowPartResult.CONTINUE;
			}
		}
		
		
		return GrowPartResult.STOP;
	}
	
	public GrowPartResult growBranch(BlockPos trunkPos, BlockPos pos, EnumFacing facing, BlockComplexLog.EnumLogType logType, int currentLength) {
		BlockPos target = pos.offset(facing);
		IBlockState targetState = world.getBlockState(target);
		Block targetBlock = targetState.getBlock();
		
		if(canPlaceLog(targetBlock)) {

			int diff = getBranchMaxLength(target) - currentLength;
			
			if(diff > 0) {
				addLog(target, facing, logType, leavesDensity > 1);
				
				return GrowPartResult.DONE;
			}
			
		}
		else if(targetState.getBlock() == logBlock) {
			
			if(targetState.getValue(BlockComplexLog.TYPE) != logType) {
				world.setBlockState(target, targetState.withProperty(BlockComplexLog.TYPE, logType), 2);
			}
			
			if( canBranchSplit && (currentLength % branchSplitSpread == 1) ) {
				EnumFacing sideway = random.nextBoolean() ? facing.rotateY() : facing.rotateYCCW();
				addLog(target.offset(sideway), sideway, BlockComplexLog.EnumLogType.SMALL, leavesDensity > 0);
			}
			
			return growBranch(trunkPos, target, facing, logType, currentLength + 1);
		}
		
		return GrowPartResult.STOP;
	}
	
	public boolean canPlaceLog(Block targetBlock) {
		return targetBlock == Blocks.AIR || targetBlock == leavesBlock || targetBlock == vineBlock || targetBlock == Blocks.SNOW_LAYER;
	}
	
	public void addLog(BlockPos target, EnumFacing facing, BlockComplexLog.EnumLogType logType, boolean addLeaves) {
		
		IBlockState state = logBlock.getDefaultState()
				.withProperty(BlockComplexLog.LOG_AXIS, BlockLog.EnumAxis.fromFacingAxis(facing.getAxis()))
				.withProperty(BlockComplexLog.TYPE, logType);
		
		world.setBlockState(target, state, 2);


		if(world.isAirBlock(target.offset(facing))) { 
			addLeaves(target.offset(facing), facing);
		}
		
		if(facing == EnumFacing.UP) {
			
			
			if(canPlaceLog( world.getBlockState(target.down()).getBlock() )  ) {
		
				EnumAxis axis = BlockPosUtils.getCommonAxis(getPrevTrunkPos(target), target, true);
				
				IBlockState supportState = logBlock.getDefaultState()
						.withProperty(BlockComplexLog.LOG_AXIS, axis == EnumAxis.NONE ? EnumAxis.X : axis)
						.withProperty(BlockComplexLog.TYPE, logType);
				
				world.setBlockState(target.offset(EnumFacing.DOWN), supportState, 2);
			}
		}
		
		if(addLeaves) {
			for(EnumFacing e : EnumFacing.values()) {
				BlockPos targetOffset = target.offset(e);
				if(world.isAirBlock(targetOffset) && random.nextInt(leavesDensity) == 0) { 
					addLeaves(targetOffset, e);
				}
			}
		}
	}
	
	public void addLeaves(BlockPos target, EnumFacing facing) {
		if(world.isAirBlock(target)) { 
			world.setBlockState(target, leavesBlock.getDefaultState(), 2);
		}
		if(hasVines && facing != EnumFacing.UP) {
			BlockPos vinesPos = target.offset(facing);
			if(world.isAirBlock(vinesPos)) { 
				@SuppressWarnings("deprecation")
				IBlockState vineState = vineBlock.getStateForPlacement(null, vinesPos, facing, 0, 0, 0, 0, null);
				world.setBlockState(vinesPos, vineState, 2); //Blocks.VINE.getDefaultState()
			}
		}
	}
	
	public BlockPos getNextTrunkPos(BlockPos target) {
		return target.offset(EnumFacing.UP).offset(EnumFacing.HORIZONTALS[getHeight(target)%4]);
	}
	
	public BlockPos getPrevTrunkPos(BlockPos target) {
		return target.offset(EnumFacing.DOWN).offset( EnumFacing.HORIZONTALS[(getHeight(target.down()))%4].getOpposite() );
	}

	public BlockPos getNextBranchPos(BlockPos target, EnumFacing branchDir, int currentBranchLength) {
		return null;
	}
	
	public BlockPos getPrevBranchPos(BlockPos target, EnumFacing branchDir, int currentBranchLength) {
		return null;
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
		return result < 0 ? 0 : result;
	}
	
	public boolean isTrunkSectionMax(int[] trunkSectionCount, int size) {
		if(size >= 0 && size < trunkSectionCount.length) {
			return trunkSectionCount[size] == trunkSectionMaxLength[size];
		}
		return false;
	}
	
	public void changeTrunkSectionCount(int[] trunkSectionCount, int size, int value) {
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
	}
	
	private enum GrowPartResult {
		STOP,
		CONTINUE,
		DONE,
		;
	}
}
