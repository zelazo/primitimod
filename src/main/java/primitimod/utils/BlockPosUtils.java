package primitimod.utils;

import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.util.math.BlockPos;

public class BlockPosUtils {
	
	public static EnumAxis getCommonAxis(BlockPos a, BlockPos b, boolean ignoreY) {
		
		BlockPos diff = a.subtract(b);
		EnumAxis result = EnumAxis.NONE;
		
		if(diff.getX() != 0) {
			result =  EnumAxis.X;
		}
		
		if(!ignoreY && diff.getY() != 0) {
			result = result == EnumAxis.NONE ? EnumAxis.Y : EnumAxis.NONE;
		}
		
		if(diff.getZ() != 0) {
			result = result == EnumAxis.NONE ? EnumAxis.Z : EnumAxis.NONE;
		}
		
		return result;
	}
	
}
