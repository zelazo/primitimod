package primitimod.trees.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import primitimod.PrimitiMod;
import primitimod.trees.block.BlockComplexLog;

public class ItemBlockLog extends ItemBlock {

	public ItemBlockLog(Block block) {
		super(block);
		this.setRegistryName(block.getRegistryName());
		this.setHasSubtypes(true);
		this.setCreativeTab(PrimitiMod.tab);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
	    return BlockComplexLog.getUnlocalizedItemBlockName(super.getUnlocalizedName(), stack.getItemDamage());
	}
	
}
