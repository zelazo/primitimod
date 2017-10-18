package primitimod.core.client;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.biome.BiomeColorHelper;

public class ColorManager {

	public static void registerColourHandlers(Block... blocks) {
		registerBlockColourHandlers(blocks);
		registerItemColourHandlers(blocks);
	}

	private static void registerBlockColourHandlers(Block... blocks) {
		final BlockColors blockColors = Minecraft.getMinecraft().getBlockColors();
		
		final IBlockColor grassColourHandler = (state, blockAccess, pos, tintIndex) -> {
			if (blockAccess != null && pos != null) {
				return BiomeColorHelper.getGrassColorAtPos(blockAccess, pos);
			}

			return ColorizerGrass.getGrassColor(0.5D, 1.0D);
		};

		for(Block block : blocks) {
			blockColors.registerBlockColorHandler(grassColourHandler, block);
		}
	}

	private static void registerItemColourHandlers(Block... blocks) {
		final BlockColors blockColors = Minecraft.getMinecraft().getBlockColors();
		final ItemColors itemColors = Minecraft.getMinecraft().getItemColors();
		
		final IItemColor itemBlockColourHandler = (stack, tintIndex) -> {
			@SuppressWarnings("deprecation")
			final IBlockState state = ((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());
			return blockColors.colorMultiplier(state, null, null, tintIndex);
		};

		for(Block block : blocks) {
			itemColors.registerItemColorHandler(itemBlockColourHandler, block);
		}
	}
}