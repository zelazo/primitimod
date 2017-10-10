package primitimod.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import primitimod.PrimitiMod;

public class PrimitiModTab extends CreativeTabs {

	public PrimitiModTab() {
		super(PrimitiMod.MODID);
	}

	@Override
	public ItemStack getTabIconItem() {
		
		return new ItemStack(Items.BONE);
	}
	

}
