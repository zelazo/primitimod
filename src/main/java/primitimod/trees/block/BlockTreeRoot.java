package primitimod.trees.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import primitimod.PrimitiMod;
import primitimod.trees.tileentity.TreeRootTE;

public abstract class BlockTreeRoot extends Block implements ITileEntityProvider {

	public BlockTreeRoot(String registryName) {
        super(Material.GROUND);
        setCreativeTab(PrimitiMod.tab);
        setRegistryName(registryName);
        setUnlocalizedName(registryName);
//        setTickRandomly(true);
    }

	@Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        TileEntity te = worldIn.getTileEntity(pos);
        
        if(te != null && te instanceof TreeRootTE) {
        	TreeRootTE rootTE = (TreeRootTE)te;
        	rootTE.update();
        }
    }
    
	@Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        super.breakBlock(world, pos, state);
        world.removeTileEntity(pos);
    }
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
	
}
