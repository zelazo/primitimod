package primitimod.trees.tileentity;

import java.util.stream.IntStream;

import net.minecraft.util.math.BlockPos;
import primitimod.core.PrimitiModBlocks;

public final class OakTreeRootTE extends TreeRootTE {

	public OakTreeRootTE() {
		this.leavesBlock = PrimitiModBlocks.OakTree.leaves; 
		this.logBlock = PrimitiModBlocks.OakTree.log; 
		this.growthRate = 0;
		this.trunkSectionMaxLength = new int[] { 8, 2, 2 };
		this.trunkMaxHeight = IntStream.of(trunkSectionMaxLength).sum();
		this.leavesMinHeight = 4;
		this.branchMinHeight = 3;
		this.branchHeightSpread = 2;
		this.branchGrowthSpeed = 60;
		this.brachCountPerLevel = 3;
		this.canBranchSplit = true;
		this.branchSplitSpread = 2;
		this.leavesDensity = 3;
		

		this.resizeParams(random.nextFloat() + 1.0f);
	}
	
	public BlockPos getNextTrunkPos(BlockPos target) {
		return target.up();
	}
	
	public BlockPos getPrevTrunkPos(BlockPos target) {
		return target.down();
	}

	public int getBranchMaxLength(BlockPos target) {
		int x = 0;
		
		if(getHeight(target) < leavesMinHeight + (trunkMaxHeight-leavesMinHeight) / 2 ) {
			x = getHeight(target) - leavesMinHeight + 1;
		}
		else {
			x = trunkMaxHeight - getHeight(target) + 1;
		}
		
		
		return x;
	}
}

