package primitimod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import primitimod.PrimitiMod;

public class BlockSimple extends Block {
		

    public BlockSimple() {
        super(Material.ROCK);
        setCreativeTab(PrimitiMod.tab);
        setRegistryName("simple");
//        setUnlocalizedName(PrimitiMod.MODID + ".rock");
        setUnlocalizedName(getRegistryName().toString());
       
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
    
    @Override
    public boolean isOpaqueCube(IBlockState state) { 
    	return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) { 
    	return false;
    }
    
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) { 
    	return EnumBlockRenderType.MODEL;
    }
   
}