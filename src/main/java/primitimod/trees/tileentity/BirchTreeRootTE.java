package primitimod.trees.tileentity;

import java.util.stream.IntStream;

import net.minecraft.util.math.BlockPos;
import primitimod.core.PrimitiModBlocks;

public final class BirchTreeRootTE extends TreeRootTE {

	public BirchTreeRootTE() {
		this.leavesBlock = PrimitiModBlocks.BirchTree.leaves; 
		this.logBlock = PrimitiModBlocks.BirchTree.log; 
		this.growthRate = 20;
		this.trunkSectionMaxLength = new int[] { 2, 8, 3 };
		this.trunkMaxHeight = IntStream.of(trunkSectionMaxLength).sum();
		this.leavesMinHeight = 4;
		this.branchMinHeight = 4;
		this.branchHeightSpread = 4;
		this.branchGrowthSpeed = 40;
		this.canGrowMultiBranch = true;
	}
	
	public BlockPos getNextTrunkPos(BlockPos target) {
		return target.up();
	}
	
	public BlockPos getPrevTrunkPos(BlockPos target) {
		return target.down();
	}

	public int getBranchMaxLength(BlockPos target) {
		return 3;
	}
}

