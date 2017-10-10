package primitimod.core;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import primitimod.PrimitiMod;
import primitimod.blocks.BlockRockPile;
import primitimod.blocks.BlockSimple;
import primitimod.trees.block.BlockComplexLog;
import primitimod.trees.block.BlockOakTreeRoot;
import primitimod.trees.block.BlockPalmTreeRoot;

public class PrimitiModBlocks {

    @GameRegistry.ObjectHolder(PrimitiMod.MODID+":rockpile")
    public static BlockRockPile blockRockPile;
    
    @GameRegistry.ObjectHolder(PrimitiMod.MODID+":simple")
    public static BlockSimple blockSimple;

    @GameRegistry.ObjectHolder(PrimitiMod.MODID+":complexlog")
    public static BlockComplexLog blockComplexLog;

    @GameRegistry.ObjectHolder(PrimitiMod.MODID+":oaktreeroot")
    public static BlockOakTreeRoot blockOakTreeRoot;
    
    @GameRegistry.ObjectHolder(PrimitiMod.MODID+":palmtreeroot")
    public static BlockPalmTreeRoot blockPalmTreeRoot;
    
    @SideOnly(Side.CLIENT)
    public static void initModels() {
    	blockRockPile.initModel();
    	blockSimple.initModel();
    	blockComplexLog.initModel();
    	blockOakTreeRoot.initModel();
    	blockPalmTreeRoot.initModel();
    }
}