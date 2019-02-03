package AppliedIntegrations;
import AppliedIntegrations.API.LiquidAIEnergy;
import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.API.PlatformEvent;
import AppliedIntegrations.Blocks.BlocksEnum;
import AppliedIntegrations.Entities.TileEnum;
import AppliedIntegrations.Gui.resetData;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Proxy.CommonProxy;
import AppliedIntegrations.Utils.AILog;
import appeng.api.AEApi;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import AppliedIntegrations.Items.*;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;

@Mod(modid = "appliedintegrations", name="Applied Integrations", version = "3.1", dependencies = "required-after:appliedenergistics2 ; required-after:CoFHAPI")
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

	// Note - Just ROFLing
	@Nullable
	@resetData
	@PlatformEvent
	public static final Object AbsoluteNULL = null; // Absolute ROFL

	public AppliedIntegrations() {
	instance = this;
}

	public static CreativeTabs AI = new CreativeTabs(I18n.translateToLocal(modid)) {
	    @Override
		public ItemStack getTabIconItem()
		{
			return new ItemStack(Item.getItemFromBlock(EInterface), 1);
		}
	};
	public static Block EInterface;



    @SidedProxy(clientSide="AppliedIntegrations.Proxy.ClientProxy", serverSide="AppliedIntegrations.Proxy.CommonProxy")
	public static CommonProxy proxy;

	@Mod.EventHandler
	public void preLoad(FMLPreInitializationEvent event)
	{
		NetworkHandler.registerPackets();

		// instanciate interface
		EInterface = BlocksEnum.BEI.b;
		registerBlocks();
		registerTiles();

		// For all liquidEnergies register it
		for (LiquidAIEnergy energy : LiquidAIEnergy.energies.values()) {
				FluidRegistry.registerFluid(energy);
		}
		// Register all items
		registerItems();

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

		// Register Cache, and monitor
		//AEApi.instance().registries().gridCache().registerGridCache( IEnergyAIGrid.class, GridEnergyCache.class );


		FMLCommonHandler.instance().bus().register(instance);

		AILog.info(this.modid + ":init Completed");
	}
	@Mod.EventHandler
	public void postLoad(FMLPostInitializationEvent event) {

		if (!(Loader.isModLoaded("AppliedEnergestics"))){
			AILog.info(this.modid + ":Applied energistics 2 not loaded, you cannot play with AppliedIntegrations(addon) without core mod ");
		}
		AILog.info(this.modid + ":Post load Completed");


	}
	public void registerItems() {
		for (ItemEnum current : ItemEnum.values()) {
			ForgeRegistries.ITEMS.register(current.getItem());
		}
	}
	public void registerBlocks(){
		for(BlocksEnum b : BlocksEnum.values()) {
			// blocks
			ForgeRegistries.BLOCKS.register(b.b);
			b.b.setCreativeTab(this.AI);
		}
	}
	public void registerTiles(){
		for(TileEnum t : TileEnum.values()){
			try {
				GameRegistry.registerTileEntity(t.getTileClass(), t.getTileID());
			}catch (Exception e){

			}
		}
	}
	public static AppliedIntegrations getInstance() {
		return instance;
		    }
	public static final int getNewID(){
		AI_ID++;
		return AI_ID;
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
}
