package AppliedIntegrations;
import AppliedIntegrations.API.AIApi;
import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.API.Storage.IEnergyTunnel;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.Gui.AIGuiHandler;
import AppliedIntegrations.Integration.Botania.BotaniaLoader;
import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.Blocks.BlocksEnum;
import AppliedIntegrations.Parts.PartModelEnum;
import AppliedIntegrations.Proxy.CommonProxy;
import AppliedIntegrations.Utils.AILog;
import appeng.api.AEApi;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = "appliedintegrations", name="Applied Integrations", version = "8.0.7", dependencies = "required-after:appliedenergistics2")
/**
 * @Author Azazell
 */
public class AppliedIntegrations {

	public static Configuration AIConfig;
	public static int Difficulty;
	public static final EnumRarity LEGENDARY = EnumHelper.addRarity("Legendary", TextFormatting.GOLD, "Legendary");

	public static final String modid = "appliedintegrations";
	@Mod.Instance(modid)
	public static AppliedIntegrations instance;
	private static int AI_ID = 1;

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
	public void preInit(FMLPreInitializationEvent event)
	{
		// For all liquidEnergies register it
		for (LiquidAIEnergy energy : LiquidAIEnergy.energies.values()) {
				FluidRegistry.registerFluid(energy);
		}

		// Register models not in proxy, due to issue with item registration
		if(getLogicalSide() == Side.CLIENT)
			PartModelEnum.registerModels();

		proxy.SidedPreInit();

		// Register HUD for advanced entropy manipulator
		//MinecraftForge.EVENT_BUS.register(new GuiEntropyManipulator(Minecraft.getMinecraft()));
		//this.registerRecipes();

		AIConfig = new Configuration(event.getSuggestedConfigurationFile());
		AIConfigOPT.syncMe();
		AILog.info("Pre load Completed");
	}
	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		// Register GuiHandler
		NetworkRegistry.INSTANCE.registerGuiHandler(this,new AIGuiHandler());
		// Register objects, that can be moved by spatial cards io
		proxy.registerSpatialIOMovables();
		proxy.SidedInit(event);

		if(Loader.isModLoaded("botania"))
			BotaniaLoader.initRecipes();

		AIApi.instance().addStorageChannelToPylon(IAEItemStack.class,
					AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));

		AIApi.instance().addStorageChannelToPylon(IAEFluidStack.class,
				AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class));

		AIApi.instance().addStorageChannelToPylon(IAEEnergyStack.class,
				AEApi.instance().storage().getStorageChannel(IEnergyTunnel.class));

		// Register Cache, and monitor
		//AEApi.instance().registries().gridCache().registerGridCache( IEnergyAIGrid.class, GridEnergyCache.class );

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
	public boolean isEnergyWhiteListed(Fluid energy) {
		return energy.getClass() == LiquidAIEnergy.class;
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
