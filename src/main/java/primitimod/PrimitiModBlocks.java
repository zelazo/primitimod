package primitimod;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import primitimod.blocks.BlockComplexLog;
import primitimod.blocks.BlockRockPile;
import primitimod.blocks.BlockSimple;
import primitimod.blocks.BlockTreeRoot;

public class PrimitiModBlocks {

    @GameRegistry.ObjectHolder(PrimitiMod.MODID+":rockpile")
    public static BlockRockPile blockRockPile;
    
    @GameRegistry.ObjectHolder(PrimitiMod.MODID+":simple")
    public static BlockSimple blockSimple;

    @GameRegistry.ObjectHolder(PrimitiMod.MODID+":complexlog")
    public static BlockComplexLog blockComplexLog;

    @GameRegistry.ObjectHolder(PrimitiMod.MODID+":treeroot")
    public static BlockTreeRoot blockTreeRoot;
    
    @SideOnly(Side.CLIENT)
    public static void initModels() {
    	blockRockPile.initModel();
    	blockSimple.initModel();
    	blockComplexLog.initModel();
    	blockTreeRoot.initModel();
    }
}