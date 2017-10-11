package primitimod.core;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import primitimod.PrimitiMod;
import primitimod.items.ItemStoneRock;
import primitimod.trees.item.ItemLogThin;

public class PrimitiModItems {

    @GameRegistry.ObjectHolder(PrimitiMod.MODID+":stonerock")
    public static ItemStoneRock itemStoneRock;
//    @GameRegistry.ObjectHolder(PrimitiMod.MODID+":logthin")
//    public static ItemLogThin itemLogThin;

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        itemStoneRock.initModel();
//        itemLogThin.initModel();
    }
    
}