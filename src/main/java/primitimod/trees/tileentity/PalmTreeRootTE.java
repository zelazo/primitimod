package primitimod.trees.tileentity;

import java.util.stream.IntStream;

import net.minecraft.util.math.BlockPos;
import primitimod.core.PrimitiModBlocks;

public final class PalmTreeRootTE extends TreeRootTE {

	public PalmTreeRootTE() {
		this.leavesBlock = PrimitiModBlocks.PalmTree.leaves; 
		this.logBlock = PrimitiModBlocks.PalmTree.log; 
		this.growthRate = 0;
		this.trunkSectionMaxLength = new int[] { 3, 5, 4 };
		this.trunkMaxHeight = IntStream.of(trunkSectionMaxLength).sum();
		this.leavesMinHeight = trunkMaxHeight - 1;
		this.branchMinHeight = trunkMaxHeight - 1;
		this.branchHeightSpread = 2;
		this.branchGrowthSpeed = 80;
		this.brachCountPerLevel = 4;
		this.canBranchSplit = true;
		this.branchSplitSpread = 2;
		this.leavesDensity = 2;
		this.hasVines = true;
		
		this.resizeParams(random.nextFloat() + 1.0f);
	}
	
	public BlockPos getNextTrunkPos(BlockPos target) {
		return target.up();
	}
	
	public BlockPos getPrevTrunkPos(BlockPos target) {
		return target.down();
	}

	public int getBranchMaxLength(BlockPos target) {
		return 4;
	}
}

