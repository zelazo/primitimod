package primitimod.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityStoneRock extends EntityThrowable {

	public EntityStoneRock(World world)
    {
        super(world);
    }

    public EntityStoneRock(World world, EntityLivingBase thrower)
    {
        super(world, thrower);
    }

    public EntityStoneRock(World world, double x, double y, double z)
    {
        super(world, x, y, z);
    }

	@Override
	protected void onImpact(RayTraceResult result) {
		
		if(this.getThrower() != null && this.getThrower() instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP)this.getThrower();
            
            if(player.connection.getNetworkManager().isChannelOpen() && player.world == this.world) {
                EntityTNTPrimed tnt = new EntityTNTPrimed(player.world);
                tnt.world.createExplosion(tnt, this.posX, this.posY, this.posZ, 2.0F, true);
            }
        }
        this.setDead();
		
//		System.out.println("hit:"+result.getBlockPos());
		
	}
	
//	@Override
//	public void onUpdate()
//    {
//
//        super.onUpdate();
////        System.out.println("rock!");
//        
////        EntityLivingBase entitylivingbase = this.getThrower();
////
////        if (entitylivingbase != null && entitylivingbase instanceof EntityPlayer && !entitylivingbase.isEntityAlive())
////        {
////            this.setDead();
////        }
////        else
////        {
////        }
//    }
	
}
