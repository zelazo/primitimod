package primitimod.trees.tileentity;

import java.util.Random;
import java.util.Vector;
import java.util.stream.IntStream;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
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
import primitimod.utils.BlockPosUtils;

public class TreeRootTE extends TileEntity implements ITickable {

	protected Block leavesBlock = Blocks.LEAVES; 
	protected Block logBlock = PrimitiModBlocks.blockOakLog; 
	protected int   growthRate = 20;
	protected int[] trunkSectionMaxLength = new int[] { 2, 3, 4 };
	protected int 	leavesMinHeight = 3;
	protected int 	branchMinHeight = 3;
	protected int 	branchHeightSpread = 2;
	protected int   branchGrowthSpeed = 20;
	protected boolean canGrowMultiBranch = true;
	protected boolean canBranchSplit = true;
	protected int branchSplitSpread = 2;
	
	protected int trunkMaxHeight = IntStream.of(trunkSectionMaxLength).sum();

	private static final Random random = new Random();
	private static final int branchMaxGrowthSpeed = 100;
	
	/*NBTTag "tickCounter" */ private int tickCounter;
	
	@Override
	public void update() {
		if (!world.isRemote) {
			tickCounter++;
			
			if(tickCounter > growthRate) {
				System.out.println("tick!");

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
				
				int size = targetState.getValue(BlockComplexLog.SIZE);
				
				if(size != 0 && 
				   isTrunkSectionMax(trunkSectionCount, size) && 
				   !isTrunkSectionMax(trunkSectionCount, size-1)
				) {
					
					IBlockState newState = logBlock.getDefaultState()
							.withProperty(BlockComplexLog.SIZE, size - 1 );
					
					world.setBlockState(target, newState, 2);
					
					BlockPos supportTarget = target.down();
					IBlockState supportState = world.getBlockState(supportTarget);
					
					if(supportState.getBlock() == logBlock) {
						world.setBlockState(supportTarget, supportState.withProperty(BlockComplexLog.SIZE, size - 1), 2);
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
			System.out.println("height:"+ getHeight(target) +" tsc: "+trunkSectionCount[0]+" "+trunkSectionCount[1]+" "+trunkSectionCount[2]);
			
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
				
				System.out.println("action: "+chosenAction.name() +" -> "+ result);
				
			}
		}
	}
	
	public GrowPartResult growPart(BlockPos target, GrowAction action, int[] trunkSectionCount) {
		IBlockState targetState = world.getBlockState(target);
		Block targetBlock = targetState.getBlock();
		
		if(action == GrowAction.TRUNK_HEIGHT) {
			if(canPlaceLog(targetBlock)) {
				int size = 2;
				
				if(trunkSectionCount[size] < trunkSectionMaxLength[size]) {
					
					addLog(target, EnumFacing.UP, size, getHeight(target) > leavesMinHeight);
					
					changeTrunkSectionCount(trunkSectionCount, size, 1);
					
					return GrowPartResult.DONE;
				}
				
			}
			else if(targetState.getBlock() == logBlock) {
				changeTrunkSectionCount(trunkSectionCount, targetState.getValue(BlockComplexLog.SIZE), 1);
				return GrowPartResult.CONTINUE;
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
							
							if(offsetState.getBlock() == logBlock) {

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
		
		if(canPlaceLog(targetBlock)) {

			int diff = getBranchMaxLength(target) - currentLength;
			
			if(diff > 0) {
				addLog(target, facing, size, !canBranchSplit);
				
				return GrowPartResult.DONE;
			}
			
		}
		else if(targetState.getBlock() == logBlock) {
			
			if(targetState.getValue(BlockComplexLog.SIZE) != size) {
				world.setBlockState(target, targetState.withProperty(BlockComplexLog.SIZE, size), 2);
			}
			
			if( canBranchSplit && (currentLength % branchSplitSpread == 1) ) {
				EnumFacing sideway = random.nextBoolean() ? facing.rotateY() : facing.rotateYCCW();
				addLog(target.offset(sideway), sideway, 2, true);
			}
			
			return growBranch(trunkPos, target, facing, size, currentLength + 1);
		}
		
		return GrowPartResult.STOP;
	}
	
	public boolean canPlaceLog(Block targetBlock) {
		return targetBlock == Blocks.AIR || targetBlock == leavesBlock;
	}
	
	public void addLog(final BlockPos target, final EnumFacing facing, final int size, boolean addLeaves) {
		
		IBlockState state = logBlock.getDefaultState()
				.withProperty(BlockComplexLog.LOG_AXIS, BlockLog.EnumAxis.fromFacingAxis(facing.getAxis()))
				.withProperty(BlockComplexLog.SIZE, size);
		
		world.setBlockState(target, state, 2);

		if(facing == EnumFacing.UP && canPlaceLog( world.getBlockState(target.down()).getBlock() )  ) {
			EnumAxis axis = BlockPosUtils.getCommonAxis(getPrevTrunkPos(target), target, true);
			
			IBlockState supportState = logBlock.getDefaultState()
					.withProperty(BlockComplexLog.LOG_AXIS, axis == EnumAxis.NONE ? EnumAxis.X : axis)
					.withProperty(BlockComplexLog.SIZE, size);
			
			world.setBlockState(target.offset(EnumFacing.DOWN), supportState, 2);
			
		}
		
		if(addLeaves) {
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
