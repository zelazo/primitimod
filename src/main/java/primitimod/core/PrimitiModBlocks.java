package primitimod.core;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import primitimod.PrimitiMod;
import primitimod.blocks.BlockRockPile;
import primitimod.blocks.BlockSimple;
import primitimod.core.registry.TreeRegistrar;
import primitimod.trees.block.BlockComplexLog;
import primitimod.trees.block.BlockFallingLeaves;
import primitimod.trees.block.BlockLumberPile;
import primitimod.trees.block.BlockTreeRoot;
import primitimod.trees.item.ItemLumber;

@GameRegistry.ObjectHolder(PrimitiMod.MODID)
public class PrimitiModBlocks {

	@GameRegistry.ObjectHolder(PrimitiMod.MODID)
	public static class OakTree extends TreeRegistrar {
		protected static final String treeName = "oak";
		protected static final String prefix = treesDir+treeName+"/";
	    @GameRegistry.ObjectHolder(prefix+rootName)
	    public static BlockTreeRoot root;
	    @GameRegistry.ObjectHolder(prefix+logName)
	    public static BlockComplexLog log;
	    @GameRegistry.ObjectHolder(prefix+leavesName)
	    public static BlockFallingLeaves leaves;
	    @GameRegistry.ObjectHolder(prefix+lumberPileName)
	    public static BlockLumberPile lumberPile;
	    @GameRegistry.ObjectHolder(prefix+lumberName)
	    public static ItemLumber lumber;
	};

	@GameRegistry.ObjectHolder(PrimitiMod.MODID)
	public static class PalmTree extends TreeRegistrar {
		protected static final String treeName = "palm";
		protected static final String prefix = treesDir+treeName+"/";
	    @GameRegistry.ObjectHolder(prefix+rootName)
	    public static BlockTreeRoot root;
	    @GameRegistry.ObjectHolder(prefix+logName)
	    public static BlockComplexLog log;
	    @GameRegistry.ObjectHolder(prefix+leavesName)
	    public static BlockFallingLeaves leaves;
	    @GameRegistry.ObjectHolder(prefix+lumberPileName)
	    public static BlockLumberPile lumberPile;
	    @GameRegistry.ObjectHolder(prefix+lumberName)
	    public static ItemLumber lumber;
	};

    @GameRegistry.ObjectHolder("rockpile")
    public static BlockRockPile blockRockPile;
    
    
    @SideOnly(Side.CLIENT)
    public static void initModels() {
    	blockRockPile.initModel();
    }
    
    public static boolean isLog(Block block) {
    	return block == OakTree.log ||
    		   block == PalmTree.log
    		   ;
    }
}