package primitimod.core.registry;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import primitimod.core.PrimitiModBlocks.OakTree;
import primitimod.core.client.ColorManager;
import primitimod.trees.block.BlockComplexLog;
import primitimod.trees.block.BlockFallingLeaves;
import primitimod.trees.block.BlockLumberPile;
import primitimod.trees.block.BlockTreeRoot;
import primitimod.trees.item.ItemBlockLog;
import primitimod.trees.item.ItemLumber;
import primitimod.trees.tileentity.TreeRootTE;

public class BlockTree {

	protected static final String treesDir = "trees/";
	protected static final String rootName = "root";
	protected static final String logName = "log";
	protected static final String leavesName = "leaves";
	protected static final String lumberName = "lumber";
	protected static final String lumberPileName = "lumberpile";
	
	public static <T extends TreeRootTE> void registerBlocks(String treeName, Class<T> clazzTreeRootTE, IForgeRegistry<Block> registry) {
		String prefix = treesDir+treeName+"/";
		System.out.println("---> blocks registry prefix: "+prefix);
		
        registry.register(new BlockComplexLog(prefix+logName));
        registry.register(new BlockLumberPile(prefix+lumberPileName));
        registry.register(new BlockFallingLeaves(prefix+leavesName));
        registry.register(new BlockTreeRoot(prefix+rootName) {
			@Override
			public TileEntity createNewTileEntity(World world, int meta) {
				try { return clazzTreeRootTE.newInstance(); } catch (Exception e) { e.printStackTrace(); }
				return null;
			}
		});
        
        GameRegistry.registerTileEntity(clazzTreeRootTE, treeName+"_"+rootName+"_te");
	}
	
	public static void registerItems(String treeName, IForgeRegistry<Item> registry, BlockLumberPile lumberPile) {
		String prefix = treesDir+treeName+"/";
		
		registry.register(new ItemLumber(prefix+lumberName, lumberPile));
	}
	
	public static void registerItemBlocks(IForgeRegistry<Item> registry, BlockComplexLog logBlock, BlockFallingLeaves leavesBlock, Block rootBlock) {
		registry.register(new ItemBlockLog(logBlock));
		registry.register(new ItemBlock(leavesBlock).setRegistryName(leavesBlock.getRegistryName()));
		registry.register(new ItemBlock(rootBlock).setRegistryName(rootBlock.getRegistryName()));
	}
	
	@SideOnly(Side.CLIENT)
    public static void initItemModels(BlockComplexLog logBlock, BlockFallingLeaves leavesBlock, BlockTreeRoot rootBlock, 
    								  BlockLumberPile lumberPileBlock, ItemLumber itemLumber) {
		
		logBlock.initModel();
		leavesBlock.initModel();
		rootBlock.initModel();
		lumberPileBlock.initModel();
		itemLumber.initModel();
    }

	@SideOnly(Side.CLIENT)
	public static void registerColourHandlers(BlockFallingLeaves leaves) {
		ColorManager.registerColourHandlers(leaves);
	}
	
}
