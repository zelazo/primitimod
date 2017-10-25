package primitimod.items;

import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import primitimod.PrimitiMod;
import primitimod.trees.block.BlockComplexLog;

public class ItemHeavyAxe extends SweepableItem {

	private static final String[] handleTypes = { "birch", "oak", "palm" };
	private static final String[] headTypes = { "andesite", "diorite", "granite" };
	
	public ItemHeavyAxe() {
		setCreativeTab(PrimitiMod.tab);
        setRegistryName("lumberheavyaxe");
        setMaxStackSize(1);
        setMaxDamage(100);
        setUnlocalizedName(getRegistryName().toString());
        setHarvestLevel("axe", 1);
        setHasSubtypes(true);
    }

	@Override
    public String getUnlocalizedName(ItemStack stack) {

        String itemName = "item."+getRegistryName();
        
        if (stack.hasTagCompound()) {
        	
            NBTTagCompound itemData = stack.getTagCompound();
            
            if (itemData.hasKey("handletype")) {
            	itemName += "."+itemData.getString("handletype");
            }
            
            if(itemData.hasKey("headtype")) {
            	itemName += "."+itemData.getString("headtype");
            }
        }
        
        return itemName;
     }


    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
    	
        for (String handleType : handleTypes) {
            for (String headType : headTypes) {
	            ItemStack stack = new ItemStack(this);
	            stack.setTagCompound(new NBTTagCompound());
	            stack.getTagCompound().setString("handletype", handleType);
	            stack.getTagCompound().setString("headtype", headType);
	            items.add(stack);
            
            }
//            http://www.minecraftforge.net/forum/topic/33823-18-multiple-texture-item/
        }
    }
	
    @SideOnly(Side.CLIENT)
    public void initModel() {
//        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    	
    	for (String handleType : handleTypes) {
    		for (String headType : headTypes) {
//    			ModelLoader.registerItemVariants(this, new ModelResourceLocation(getRegistryName(), "headtype="+headType));
            	ModelLoader.registerItemVariants(this, new ModelResourceLocation(getRegistryName(), "handletype="+handleType+",headtype="+headType));
        	}
//    		ModelLoader.registerItemVariants(this, new ModelResourceLocation(getRegistryName(), "handletype="+handleType));
    	}
    	
    	
    	
//        ModelBakery.registerItemVariants(this, new ModelResourceLocation(getRegistryName(), "handletype=oak"));
//        ModelBakery.registerItemVariants(this, new ModelResourceLocation(getRegistryName(), "handletype=birch"));
//        ModelBakery.registerItemVariants(this, new ModelResourceLocation(getRegistryName(), "handletype=palm"));
//      ModelBakery.registerItemVariants(this, ModelLoader.getInventoryVariant(getRegistryName().getResourceDomain()+":"+getRegistryName().getResourcePath()+"#"+"handletype=oak"));
//        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().reg

      ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition() {
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack) {
				if (stack.hasTagCompound()) {
					
					String handleVariant = "handletype=";
					String headVariant = "headtype=";
		            NBTTagCompound itemData = stack.getTagCompound();
		            
		            if (itemData.hasKey("headtype")) {
		            	headVariant += itemData.getString("headtype");
		            }
		            
		            if(itemData.hasKey("handletype")) {
		            	handleVariant += itemData.getString("handletype");
		            }
//		            return new ModelResourceLocation(getRegistryName(), headVariant);
//		            return new ModelResourceLocation(getRegistryName(), handleVariant);
		            return new ModelResourceLocation(getRegistryName(), handleVariant+","+headVariant);
//		            return new ModelResourceLocation(getRegistryName(), "handletype=oak,headtype=andesite");
		        }
				return null;
			}
		});
    }
    
    /**
     * Called when the player stops using an Item (stops holding the right mouse button).
     */
    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft) {
        if (entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entityLiving;
            
            int power = this.getMaxItemUseDuration(stack) - timeLeft;
            
            if(power > 25) {
                
	        	if (!world.isRemote) {
	                RayTraceResult ray = player.rayTrace(3.0d, 0.2f);
	                BlockPos hitPos = ray.getBlockPos();
	                IBlockState hitBlockState = world.getBlockState(hitPos);
	                
//	                if(ray.sideHit == EnumFacing.UP) {
//	                	splitLog(player, world, ray.getBlockPos());
//	                }
//	                else {
	                	hitTree(player, world, ray.getBlockPos());
//	                }
	                
	                int damage = setToolDamage(world, player, hitBlockState, hitPos);
	                
	                if(damage > 0) {
	                	world.playEvent(2001, hitPos, Block.getStateId(hitBlockState));
	                }
	                
		            spawnSweepParticles(world, player);
	        	}
	        	KeyBinding.onTick(Minecraft.getMinecraft().gameSettings.keyBindAttack.getKeyCode());
            }
            else {
            	if (!world.isRemote) {
                	world.getMinecraftServer().getCommandManager().executeCommand(player, "/time set day");
                	world.getMinecraftServer().getCommandManager().executeCommand(player, "/weather clear");
            	}
            }
        }
    }
    
    public int setToolDamage(World world, EntityPlayer player, IBlockState state, BlockPos pos) {

    	String harvestTool = state.getBlock().getHarvestTool(state);
        ItemStack heldItem = player.getHeldItemMainhand();
        
		int damage = 1;
		
		if(getHarvestLevel(heldItem, harvestTool, player, state) < 0) {
			damage = getDamageFromHarvestLevel(state.getBlockHardness(world, pos));
		}
		
        System.out.println("damage: "+damage);
        
        heldItem.damageItem(damage, player);
        
        return damage;
    }
    

    public boolean splitLog(EntityPlayer player, World world, BlockPos pos, IBlockState state) {
    	
    	int itemAmount = state.getValue(BlockComplexLog.TYPE).getLumberDropAmount();
    	

    	if(state.getBlock() instanceof BlockComplexLog) {
    		BlockComplexLog log = (BlockComplexLog) state.getBlock();
    		
	    	BlockPos entityPos = pos;//.add(0.5d, 0.0d, 0.5d);//.up();
			EntityItem spawnEntityItem = new EntityItem(world, entityPos.getX(), entityPos.getY(), entityPos.getZ(), 
					new ItemStack(log.getItemLumber(), itemAmount));
			
			world.spawnEntity(spawnEntityItem);
    	
    	}
    	world.destroyBlock(pos, false);
    	
    	return false;
    }
    
    public boolean hitTree(EntityPlayer player, World world, BlockPos pos) {
    	if(!world.isRemote) {

	    	IBlockState target = world.getBlockState(pos);
	    	
	    	if( target.getBlock() instanceof BlockComplexLog ) {
	    		
	    		if(target.getValue(BlockComplexLog.TYPE) == BlockComplexLog.EnumLogType.DAMAGED) {
		    		BlockPos toCut = findBlockToCut(world, pos, target.getBlock());
		    		cutLog(world, toCut);
		    		
		    		return true;
		    	}
	    		else {
	    		
	    			BlockPos toCut = findBlockToCut(world, pos, target.getBlock());
		    		
	    			if(toCut == pos) {
	    				splitLog(player, world, pos, target);
	    			}
	    			else {
		    			world.setBlockState(pos, target
							.withProperty(BlockComplexLog.TYPE, BlockComplexLog.EnumLogType.DAMAGED ) 
						);
	    			}
		    			
	    			return true;
		    	}
		    	
	    	}
    	}
    	
    	return false;
    }
    
    public BlockPos findBlockToCut(World world, BlockPos hitBlock, Block blockType) {
    	BlockPos target = hitBlock;
    	BlockPos result = hitBlock;
    	IBlockState hitState = world.getBlockState(hitBlock);
    	IBlockState targetState = null;
    	boolean done = false;
    	
    	if(hitState.getBlock() == Blocks.AIR) {
    		return target;
    	}
    	
    	while(!done) {

			target = target.up();
    		targetState = world.getBlockState(target);
    		
    		done = true;
    		if(targetState.getBlock() == blockType && targetState.getValue(BlockComplexLog.LOG_AXIS) == BlockLog.EnumAxis.Y) 
    		{
    			result = target;
    			done = false;
    		}
    		else {
    			for(EnumFacing facing : EnumFacing.HORIZONTALS) {
    				
    				targetState = world.getBlockState(target.offset(facing));
    				
    				if(targetState.getBlock() == blockType && 
					   targetState.getValue(BlockComplexLog.LOG_AXIS) == BlockLog.EnumAxis.Y) 
    				{
    					target = target.offset(facing);
    					result = target;
    	    			done = false;
    	    			break;
    	    		}
        		}
    		}

    	}
    	
    	return result;
    }
    
    public void cutLog(World world, BlockPos target) {
    	IBlockState targetState = world.getBlockState(target);
    	Block blockType = targetState.getBlock();
    	BlockPos offsetPos = null;
    	IBlockState offsetState = null;
    	EnumFacing branchFacing = null;
    	
    	for(EnumFacing facing : EnumFacing.HORIZONTALS) {
    		offsetPos = target.offset(facing);
    		offsetState = world.getBlockState(offsetPos);

    		if(offsetState.getBlock() == targetState.getBlock()) {
    		   if (offsetState.getValue(BlockComplexLog.LOG_AXIS) == BlockLog.EnumAxis.fromFacingAxis(facing.getAxis()) ) {
	    			branchFacing = facing;
	    			break;
	    		}
    		}
    	}
    	
    	if(branchFacing == null) {
    		world.destroyBlock(target, true);
    	}
    	else {
    		while(true) {
    			target = target.offset(branchFacing);
				targetState = world.getBlockState(target);
				
				if(targetState.getBlock() != blockType) {
    				break;
    			}
    			else {
    				world.destroyBlock(target, true);
    			}
				
				for(EnumFacing facing : EnumSet.of(EnumFacing.DOWN, EnumFacing.UP, branchFacing.rotateY(), branchFacing.rotateYCCW())) 
				{
					offsetPos = target.offset(facing);
					offsetState = world.getBlockState(offsetPos);
					
					if(offsetState.getBlock() == blockType) {
				    	if(offsetState.getValue(BlockComplexLog.LOG_AXIS) == BlockLog.EnumAxis.fromFacingAxis(facing.getAxis())) {
				    		world.destroyBlock(offsetPos, true);
				    	}
					}
				}
    		}
    	}
    }
}