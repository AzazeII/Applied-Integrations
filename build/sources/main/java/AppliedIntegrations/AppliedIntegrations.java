package AppliedIntegrations;
import AppliedIntegrations.API.LiquidAIEnergy;
import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.Blocks.BlocksEnum;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Parts.PartModelEnum;
import AppliedIntegrations.Proxy.CommonProxy;
import AppliedIntegrations.Utils.AILog;
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
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = "appliedintegrations", name="Applied Integrations", version = "8", dependencies = "required-after:appliedenergistics2;required-after:redstoneflux")
/**
 * @Author Azazell
 */
public class AppliedIntegrations implements IGuiHandler {

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
		NetworkHandler.registerPackets();

		// For all liquidEnergies register it
		for (LiquidAIEnergy energy : LiquidAIEnergy.energies.values()) {
				FluidRegistry.registerFluid(energy);
		}

		proxy.SidedPreInit();
		registerPartModels();

		// Register HUD for advanced entropy manipulator
		//MinecraftForge.EVENT_BUS.register(new GuiEntropyManipulator(Minecraft.getMinecraft()));
		//this.registerRecipes();

		AIConfig = new Configuration(event.getSuggestedConfigurationFile());
		AIConfigOPT.syncMe();
		AILog.info(this.modid + ":Pre load Completed");
	}
	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		// Register GuiHandler
		NetworkRegistry.INSTANCE.registerGuiHandler(this,this);
		// Register objects, that can be moved by spatial cards io
		proxy.registerSpatialIOMovables();
		proxy.SidedInit();
		// Register Cache, and monitor
		//AEApi.instance().registries().gridCache().registerGridCache( IEnergyAIGrid.class, GridEnergyCache.class );

		FMLCommonHandler.instance().bus().register(instance);

		AILog.info(this.modid + ":init Completed");
	}
	@Mod.EventHandler
	public void postLoad(FMLPostInitializationEvent event) {
		AILog.info(this.modid + ":Post load Completed");
	}

	public static AppliedIntegrations getInstance() {
		return instance;
		    }
	public static final int getNewID(){
		AI_ID++;
		return AI_ID;
	}

	@SideOnly(Side.CLIENT)
	void registerPartModels(){
		PartModelEnum.registerModels();
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		for(GuiEnum guiEnum : GuiEnum.values()){
			if (guiEnum.ID == ID) {
					return guiEnum.GetServerGuiElement(ID, player, world, x, y, z, guiEnum.isPart);
			}
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		for(GuiEnum guiEnum : GuiEnum.values()){
			if (guiEnum.ID == ID) {
				return guiEnum.GetClientGuiElement(ID, player, world, x, y, z, guiEnum.isPart);
			}
		}
		return null;
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
		meta.description = "Addon for Applied Energistics 2 for storing energy, and few more things";
		meta.url = "https://github.com/AzazeII/Applied-Integrations/";
	}
}
