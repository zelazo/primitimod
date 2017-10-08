package primitimod.tileentity;

import java.util.Random;

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

	private static int[] trunkSectionMaxLength = new int[] { 5, 4, 6 };
	private int[] trunkSectionCount = new int[] { 0, 0, 0 };
	
	@Override
	public void update() {
		if (!world.isRemote) {
			tickCounter++;
			
			if(tickCounter > updatePace) {
				grow(pos);
				tickCounter = 0;
			}
			
		}
	}
	
	public void grow(BlockPos pos) {
		
//		System.out.println("tick!");
		System.out.println("tsc: "+trunkSectionCount[0]+" "+trunkSectionCount[1]+" "+trunkSectionCount[2]);
		
		BlockPos target = pos.offset(EnumFacing.UP);
		IBlockState targetState = world.getBlockState(target);
		
		if(world.isAirBlock(target)) {
			int size = 2;
			
			if(trunkSectionCount[size] < trunkSectionMaxLength[size]) {
				
				IBlockState newState = PrimitiModBlocks.blockComplexLog.getDefaultState()
						.withProperty(BlockComplexLog.LOG_AXIS, BlockLog.EnumAxis.Y)
						.withProperty(BlockComplexLog.SIZE, size);
				
				world.setBlockState(target, newState, 2);
				changeTrunkSectionCount(size, 1);
			}

			
			
		}
		else if(targetState.getBlock() == PrimitiModBlocks.blockComplexLog) {
			int size = targetState.getValue(BlockComplexLog.SIZE);
//
//			if(size == 0 || random.nextBoolean()) {
//				grow(target);
//			}
//			else {
				
				if(size != 0 && isTrunkMax(size) && !isTrunkMax(size-1)) {
				
					IBlockState newState = PrimitiModBlocks.blockComplexLog.getDefaultState()
							.withProperty(BlockComplexLog.SIZE, size - 1 );
					
					world.setBlockState(target, newState, 2);
					changeTrunkSectionCount(size-1	,  1);
					changeTrunkSectionCount(size	, -1);
				}
				else {
					grow(target);
				}
			
			
		}
	}
	
	public boolean isTrunkMax(int size) {
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
}
