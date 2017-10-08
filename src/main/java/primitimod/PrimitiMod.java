package primitimod;

import org.apache.logging.log4j.Logger;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = PrimitiMod.MODID, version = PrimitiMod.MODVERSION)
public class PrimitiMod
{
	public static final String MODID = "primitimod";
	public static final String MODNAME = "PrimitiMod";
    public static final String MODVERSION = "0.0.1";
    
    @SidedProxy( clientSide = "primitimod.client.ClientProxy", serverSide = "primitimod.ServerProxy", modId = PrimitiMod.MODID )
    public static CommonProxy proxy;
    
    @Mod.Instance
    public static PrimitiMod instance;
    
    public static CreativeTabs tab = new PrimitiModTab();
    
    public static Logger LOGGER;
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	LOGGER = event.getModLog();
    	LOGGER.info("Starting Pre-Initialization...");
    	proxy.preInit(event);
    }
    
    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
    	LOGGER.info("Starting Initialization...");
    	proxy.init(event);
    }
    
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	LOGGER.info("Starting Post-Initialization...");
    	proxy.postInit(event);
    }
}