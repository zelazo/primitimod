package primitimod.core.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.biome.BiomeColorHelper;
import primitimod.core.PrimitiModBlocks;

public class ColorManager {

	public static void registerColourHandlers() {
		final BlockColors blockColors = Minecraft.getMinecraft().getBlockColors();
		final ItemColors itemColors = Minecraft.getMinecraft().getItemColors();

		registerBlockColourHandlers(blockColors);
		registerItemColourHandlers(blockColors, itemColors);
	}

	private static void registerBlockColourHandlers(final BlockColors blockColors) {

		final IBlockColor grassColourHandler = (state, blockAccess, pos, tintIndex) -> {
			if (blockAccess != null && pos != null) {
				return BiomeColorHelper.getGrassColorAtPos(blockAccess, pos);
			}

			return ColorizerGrass.getGrassColor(0.5D, 1.0D);
		};

		blockColors.registerBlockColorHandler(grassColourHandler, PrimitiModBlocks.blockOakLeaves);
	}

	private static void registerItemColourHandlers(final BlockColors blockColors, final ItemColors itemColors) {

		final IItemColor itemBlockColourHandler = (stack, tintIndex) -> {
			@SuppressWarnings("deprecation")
			final IBlockState state = ((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());
			return blockColors.colorMultiplier(state, null, null, tintIndex);
		};

		itemColors.registerItemColorHandler(itemBlockColourHandler, PrimitiModBlocks.blockOakLeaves);
	}
}