package primitimod.trees.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import primitimod.PrimitiMod;
import primitimod.trees.tileentity.PalmTreeRootTE;

public class BlockPalmTreeRoot extends BlockTreeRoot {
	
	public BlockPalmTreeRoot(String registryName) {
        super(registryName);
        setCreativeTab(PrimitiMod.tab);
    }
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new PalmTreeRootTE();
	}
}
