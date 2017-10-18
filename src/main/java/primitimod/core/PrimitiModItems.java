package primitimod.core;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import primitimod.PrimitiMod;
import primitimod.items.ItemHeavyAxe;
import primitimod.items.ItemStoneRock;

public class PrimitiModItems {

    @GameRegistry.ObjectHolder(PrimitiMod.MODID+":stonerock")
    public static ItemStoneRock itemStoneRock;
    
    @GameRegistry.ObjectHolder(PrimitiMod.MODID+":lumberheavyaxe")
    public static ItemHeavyAxe itemHeavyAxe;

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        itemStoneRock.initModel();
        itemHeavyAxe.initModel();
    }
    
}