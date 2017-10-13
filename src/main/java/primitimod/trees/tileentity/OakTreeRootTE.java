package primitimod.trees.tileentity;

import java.util.stream.IntStream;

import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import primitimod.core.PrimitiModBlocks;

public final class OakTreeRootTE extends TreeRootTE {

	public OakTreeRootTE() {
		this.leavesBlock = PrimitiModBlocks.blockOakLeaves;//Blocks.LEAVES; 
		this.logBlock = PrimitiModBlocks.blockOakLog; 
		this.growthRate = 20;
		this.trunkSectionMaxLength = new int[] { 3, 4, 5 };
		this.trunkMaxHeight = IntStream.of(trunkSectionMaxLength).sum();
		this.leavesMinHeight = 4;
		this.branchMinHeight = 4;
		this.branchHeightSpread = 2;
		this.branchGrowthSpeed = 40;
		this.canGrowMultiBranch = false;
	}
	
	public BlockPos getNextTrunkPos(BlockPos target) {
		return target.offset(EnumFacing.UP).offset(EnumFacing.HORIZONTALS[getHeight(target)%4]);
	}
	
	public BlockPos getPrevTrunkPos(BlockPos target) {
		return target.offset(EnumFacing.DOWN).offset( EnumFacing.HORIZONTALS[(getHeight(target.down()))%4].getOpposite() );
	}

	public int getBranchMaxLength(BlockPos target) {
		return (trunkMaxHeight - getHeight(target)) / 2;
	}
}
