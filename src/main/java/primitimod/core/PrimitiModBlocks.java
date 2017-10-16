package primitimod.core;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import primitimod.PrimitiMod;
import primitimod.blocks.BlockLumberPile;
import primitimod.blocks.BlockRockPile;
import primitimod.blocks.BlockSimple;
import primitimod.trees.block.BlockComplexLog;
import primitimod.trees.block.BlockFallingLeaves;
import primitimod.trees.block.BlockOakTreeRoot;
import primitimod.trees.block.BlockPalmTreeRoot;

public class PrimitiModBlocks {

    @GameRegistry.ObjectHolder(PrimitiMod.MODID+":rockpile")
    public static BlockRockPile blockRockPile;
    
    @GameRegistry.ObjectHolder(PrimitiMod.MODID+":lumberpile")
    public static BlockLumberPile blockLumberPile;
    
    @GameRegistry.ObjectHolder(PrimitiMod.MODID+":simple")
    public static BlockSimple blockSimple;

    @GameRegistry.ObjectHolder(PrimitiMod.MODID+":oaktreeroot")
    public static BlockOakTreeRoot blockOakTreeRoot;
    
    @GameRegistry.ObjectHolder(PrimitiMod.MODID+":palmtreeroot")
    public static BlockPalmTreeRoot blockPalmTreeRoot;
    

    @GameRegistry.ObjectHolder(PrimitiMod.MODID+":oaklog")
    public static BlockComplexLog blockOakLog;
    
    @GameRegistry.ObjectHolder(PrimitiMod.MODID+":palmlog")
    public static BlockComplexLog blockPalmLog;

    @GameRegistry.ObjectHolder(PrimitiMod.MODID+":barelog")
    public static BlockComplexLog blockBareLog;
    

    @GameRegistry.ObjectHolder(PrimitiMod.MODID+":oakleaves")
    public static BlockFallingLeaves blockOakLeaves;
    
    @GameRegistry.ObjectHolder(PrimitiMod.MODID+":palmleaves")
    public static BlockFallingLeaves blockPalmLeaves;
    
    @SideOnly(Side.CLIENT)
    public static void initModels() {
    	blockRockPile.initModel();
    	blockLumberPile.initModel();
    	blockSimple.initModel();
    	blockOakTreeRoot.initModel();
    	blockPalmTreeRoot.initModel();
    	
    	blockPalmLog.initModel();
    	blockOakLog.initModel();
    	blockBareLog.initModel();

    	blockPalmLeaves.initModel();
    	blockOakLeaves.initModel();
    }
    
    public static boolean isLog(Block block) {
    	return block == blockOakLog ||
    		   block == blockPalmLog
    		   ;
    }
}