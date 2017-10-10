package primitimod.trees.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import primitimod.PrimitiMod;
import primitimod.trees.tileentity.OakTreeRootTE;

public class BlockOakTreeRoot extends BlockTreeRoot {
	
	public BlockOakTreeRoot(String registryName) {
        super(registryName);
        setCreativeTab(PrimitiMod.tab);
    }
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new OakTreeRootTE();
	}
}
