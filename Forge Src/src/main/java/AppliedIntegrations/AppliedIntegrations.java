package AppliedIntegrations;
import AppliedIntegrations.API.LiquidAIEnergy;
import AppliedIntegrations.API.Parts.AIPart;
import AppliedIntegrations.API.PlatformEvent;
import AppliedIntegrations.Blocks.BlockEnergyInterface;
import AppliedIntegrations.Entities.TileEnergyInterface;
import AppliedIntegrations.Gui.resetData;
import AppliedIntegrations.Layers.LayerRotaryCraft;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Proxy.CommonProxy;
import AppliedIntegrations.Render.TextureManager;
import AppliedIntegrations.Utils.AILog;
import AppliedIntegrations.grid.GridEnergyCache;
import AppliedIntegrations.grid.IEnergyAIGrid;
import appeng.api.AEApi;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import extracells.api.ECApi;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import AppliedIntegrations.Items.*;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import javax.annotation.Nullable;

@Mod (modid = "appliedintegrations", name="Applied Integrations", version = "2.5", dependencies = "required-after:appliedenergistics2 ; required-after:CoFHAPI")
/**
 * @Author Azazell
 */
public class AppliedIntegrations implements IGuiHandler {

	public static Configuration AIConfig;
	public static int Difficulty;
	public static final EnumRarity LEGENDARY = EnumHelper.addRarity("Legendary", EnumChatFormatting.GOLD, "Legendary");

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

	public static CreativeTabs AI = new CreativeTabs(StatCollector.translateToLocal(modid)) {
	    @Override
		public Item getTabIconItem()
		{
			return Item.getItemFromBlock(EInterface);
		}
	};
	public static Block EInterface;



    @SidedProxy(clientSide="AppliedIntegrations.Proxy.ClientProxy", serverSide="AppliedIntegrations.Proxy.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preLoad(FMLPreInitializationEvent event)
	{
		NetworkHandler.registerPackets();
		// instancinate map
		TextureMap map = new TextureMap(1,this.modid);
		// register textures of parts
		for( TextureManager texture : TextureManager.ALLVALUES )
		{
			texture.registerTexture( map );
		}

		// instanciate interface
		EInterface = new BlockEnergyInterface();
		// register energy interface in block form
		GameRegistry.registerBlock(EInterface, "EInterface");
		// register energy interface in tile form
		GameRegistry.registerTileEntity(TileEnergyInterface.class, "TileEnergyInterface");
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
	@Optional.Method(modid = "extracells")
	public void addLiquidToBlackList(){
		for(LiquidAIEnergy energy : LiquidAIEnergy.energies.values()){
			ECApi.instance().addFluidToStorageBlacklist(energy);
			ECApi.instance().addFluidToShowBlacklist(energy);
		}
	}
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		// Register GuiHandler
		NetworkRegistry.INSTANCE.registerGuiHandler(this,this);
		// Register objects, that can be moved by spatial cards io
		proxy.registerSpatialIOMovables();
		// Register recipes
		if(Loader.isModLoaded("extracells"))
		// Register Blacklisted Fluids to EC list
			this.addLiquidToBlackList();
		// Register Cache, and monitor
		AEApi.instance().registries().gridCache().registerGridCache( IEnergyAIGrid.class, GridEnergyCache.class );
		AEApi.instance().partHelper().registerNewLayer(LayerRotaryCraft.class.getName(), LayerRotaryCraft.class.getName());
		proxy.addRecipes();

		FMLCommonHandler.instance().bus().register(instance);

		AILog.info(this.modid + ":init Completed");
	}
	@EventHandler
	public void postLoad(FMLPostInitializationEvent event) {

		if (!(Loader.isModLoaded("AppliedEnergestics"))){
			AILog.info(this.modid + ":Applied energistics 2 not loaded, you cannot play with AppliedIntegrations(addon) without core mod ");
		}
		AILog.info(this.modid + ":Post load Completed");


	}
	@SubscribeEvent
	public void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent event){
		if(event.modID.equals(modid)){
			AIConfigOPT.syncMe();
		}
	}
	public void registerItems() {
		for (ItemEnum current : ItemEnum.values()) {
			GameRegistry.registerItem(current.getItem(), current.getStatName());
		}
	}
	public void registerBlock(){

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
			if (guiEnum.ID == ID)
				return guiEnum.GetServerGuiElement(ID,player,world,x,y,z,guiEnum.isPart);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		for(GuiEnum guiEnum : GuiEnum.values()){
			if (guiEnum.ID == ID)
		return	guiEnum.GetClientGuiElement(ID,player,world,x,y,z,guiEnum.isPart);
		}
		return null;
	}
	public static void launchGui(AIPart AIPart, EntityPlayer player, World worldObj, int xCoord, int yCoord,
								 int zCoord) {
		// TODO Auto-generated method stub
		
	}
	public boolean isEnergyWhiteListed(Fluid energy) {
		return energy.getClass() == LiquidAIEnergy.class;
	}
}
