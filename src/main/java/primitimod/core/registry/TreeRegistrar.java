package primitimod.core.registry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import primitimod.core.client.ColorManager;
import primitimod.trees.block.BlockComplexLog;
import primitimod.trees.block.BlockFallingLeaves;
import primitimod.trees.block.BlockLumberPile;
import primitimod.trees.block.BlockTreeSapling;
import primitimod.trees.block.BlockTreeRoot;
import primitimod.trees.item.ItemBlockLog;
import primitimod.trees.item.ItemLumber;
import primitimod.trees.tileentity.TreeRootTE;

public class TreeRegistrar {

	protected static final String treesDir = "trees/";
	protected static final String rootName = "root";
	protected static final String logName = "log";
	protected static final String leavesName = "leaves";
	protected static final String saplingName = "sapling";
	protected static final String lumberName = "lumber";
	protected static final String lumberPileName = "lumberpile";
	
	public static <T extends TreeRootTE> void registerBlocks(String treeName, Class<T> clazzTreeRootTE, IForgeRegistry<Block> registry) {
		String prefix = treesDir+treeName+"/";
		System.out.println("---> blocks registry prefix: "+prefix);
		
        registry.register(new BlockComplexLog(prefix+logName));
        registry.register(new BlockLumberPile(prefix+lumberPileName));
        registry.register(new BlockFallingLeaves(prefix+leavesName));
        
        BlockTreeRoot treeRoot = new BlockTreeRoot(prefix+rootName) {
			@Override
			public TileEntity createNewTileEntity(World world, int meta) {
				try { return clazzTreeRootTE.newInstance(); } catch (Exception e) { e.printStackTrace(); }
				return null;
			}
		};
        registry.register(treeRoot);
        registry.register(new BlockTreeSapling(prefix+saplingName, treeRoot));
        
        
        GameRegistry.registerTileEntity(clazzTreeRootTE, treeName+"_"+rootName+"_te");
	}
	
	public static void registerItems(String treeName, IForgeRegistry<Item> registry, BlockLumberPile lumberPile, BlockComplexLog log) {
		String prefix = treesDir+treeName+"/";
		
		lumberPile.setItemLumber(new ItemLumber(prefix+lumberName, lumberPile));
		log.setItemLumber(lumberPile.getItemLumber());
		
		registry.register(lumberPile.getItemLumber());
	}
	
	public static void registerItemBlocks(IForgeRegistry<Item> registry, BlockComplexLog logBlock, BlockFallingLeaves leavesBlock, BlockTreeRoot rootBlock, BlockTreeSapling sapling) {
		registry.register(new ItemBlockLog(logBlock));
		registry.register(new ItemBlock(leavesBlock).setRegistryName(leavesBlock.getRegistryName()));
		registry.register(new ItemBlock(rootBlock).setRegistryName(rootBlock.getRegistryName()));
		registry.register(new ItemBlock(sapling).setRegistryName(sapling.getRegistryName()));
	}
	
	@SideOnly(Side.CLIENT)
    public static void initItemModels(BlockComplexLog logBlock, BlockFallingLeaves leavesBlock, BlockTreeRoot rootBlock, 
    								  BlockTreeSapling treeSapling, BlockLumberPile lumberPileBlock, ItemLumber itemLumber) {
		
		logBlock.initModel();
		leavesBlock.initModel();
		rootBlock.initModel();
		treeSapling.initModel();
		lumberPileBlock.initModel();
		itemLumber.initModel();
    }

	@SideOnly(Side.CLIENT)
	public static void registerColourHandlers(BlockFallingLeaves leaves) {
		ColorManager.registerColourHandlers(leaves);
	}
	
}
