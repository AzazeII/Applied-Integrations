package AppliedIntegrations;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.Client.TextureEventManager;
import AppliedIntegrations.Gui.AIGuiHandler;
import AppliedIntegrations.Integration.Botania.BotaniaLoader;
import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.Blocks.BlocksEnum;
import AppliedIntegrations.Parts.PartModelEnum;
import AppliedIntegrations.Proxy.CommonProxy;
import AppliedIntegrations.Utils.AILog;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = AppliedIntegrations.modid, name="Applied Integrations", version = "9.0.10.3", dependencies = "required-after:appliedenergistics2")
/**
 * @Author Azazell
 */
public class AppliedIntegrations {
	public static final String modid = "appliedintegrations";

	@Mod.Instance(modid)
	public static AppliedIntegrations instance;

	public AppliedIntegrations() {
	instance = this;
}

	public static CreativeTabs AI = new CreativeTabs(I18n.translateToLocal(modid)) {
	    @Override
		public ItemStack getTabIconItem()
		{
			return new ItemStack(Item.getItemFromBlock(BlocksEnum.BEI.b), 1);
		}
	};

    @SidedProxy(clientSide="AppliedIntegrations.Proxy.ClientProxy", serverSide="AppliedIntegrations.Proxy.CommonProxy")
	public static CommonProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// Init config
		AIConfig.preInit();

		if(AIConfig.enableEnergyFeatures) {
			// For all liquidEnergies register it
			for (LiquidAIEnergy energy : LiquidAIEnergy.energies.values()) {
				FluidRegistry.registerFluid(energy);
			}
		}

		// Register models not in proxy, due to issue with item registration
		if(getLogicalSide() == Side.CLIENT)
			PartModelEnum.registerModels();

		proxy.SidedPreInit();

		//this.registerRecipes();

		AILog.info("Pre load Completed");
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {

		// Register GuiHandler
		NetworkRegistry.INSTANCE.registerGuiHandler(this,new AIGuiHandler());
		// Register objects, that can be moved by spatial cards io
		proxy.registerSpatialIOMovables();
		proxy.SidedInit(event);

		if(Loader.isModLoaded("botania") && AIConfig.enableManaFeatures)
			BotaniaLoader.initRecipes();

		FMLCommonHandler.instance().bus().register(instance);

		AILog.info( "init Completed");
	}

	@Mod.EventHandler
	public void postLoad(FMLPostInitializationEvent event) {
		AILog.info("Post load Completed");
	}

	public static AppliedIntegrations getInstance() {
		return instance;
		    }

	public static Side getLogicalSide(){
		Thread thr = Thread.currentThread();
		if ((thr.getName().equals("Server thread")))
		{
			return Side.SERVER;
		}

		return Side.CLIENT;
	}

	public static void launchGui(AIPart AIPart, EntityPlayer player, World worldObj, int xCoord, int yCoord,
								 int zCoord) {
		// TODO Auto-generated method stub
	}

	@Mod.EventHandler
	public static void metadata(FMLPreInitializationEvent event){
		ModMetadata meta = event.getModMetadata();
		meta.autogenerated  = false;
		meta.modId = modid;
		meta.name = "Applied Integrations";
		meta.version = "Alpha 7";
		meta.logoFile = "logo.png";
		meta.authorList.add("Azazell");
		meta.description = "Addon for Applied Energistics 2 for storing energy, mana. And some network things";
		meta.url = "https://github.com/AzazeII/Applied-Integrations/";
	}
}
