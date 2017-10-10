package primitimod.trees.tileentity;

import java.util.stream.IntStream;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import primitimod.core.PrimitiModBlocks;

public final class PalmTreeRootTE extends TreeRootTE {

	public PalmTreeRootTE() {
		this.leavesBlock = Blocks.LEAVES; 
		this.logBlock = PrimitiModBlocks.blockComplexLog; 
		this.growthRate = 20;
		this.trunkSectionMaxLength = new int[] { 4, 3, 2 };
		this.trunkMaxHeight = IntStream.of(trunkSectionMaxLength).sum();
		this.leavesMinHeight = 5;
		this.branchMinHeight = trunkMaxHeight - 4;
		this.branchHeightSpread = 2;
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
		return getHeight(target) / 2;
	}
}
