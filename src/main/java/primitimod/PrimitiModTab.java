package primitimod;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class PrimitiModTab extends CreativeTabs {

	public PrimitiModTab() {
		super(PrimitiMod.MODID);
	}

	@Override
	public ItemStack getTabIconItem() {
		
		return new ItemStack(Items.BONE);
	}
	

}
