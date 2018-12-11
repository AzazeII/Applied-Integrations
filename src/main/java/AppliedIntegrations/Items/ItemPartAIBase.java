package AppliedIntegrations.Items;

import java.util.*;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Parts.PartEnum;
import appeng.api.AEApi;
import appeng.api.config.Upgrades;
import appeng.api.implementations.items.IItemGroup;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import org.omg.CORBA.Current;

/**
 * @Author Azazell
 */
public class ItemPartAIBase
		extends Item
		implements IPartItem, IItemGroup
{
	/**
	 * Constructor
	 */
	public ItemPartAIBase()
	{
		// Undamageable
		this.setMaxDamage( 0 );

		// Has sub types
		this.setHasSubtypes( true );

		// Can be rendered on a cable.
		AEApi.instance().partHelper().setItemBusRenderer( this );

		// Register parts who can take an upgrade card.
		Map<Upgrades, Integer> possibleUpgradesList;
		for( PartEnum part : PartEnum.VALUES )
		{
			possibleUpgradesList = part.getUpgrades();

			for( Upgrades upgrade : possibleUpgradesList.keySet() )
			{
				upgrade.registerItem( new ItemStack( this, 1, part.ordinal() ), possibleUpgradesList.get( upgrade ).intValue() );
			}
		}

	}
	@Override
	public IPart createPartFromItemStack( final ItemStack itemStack )
	{
		IPart newPart = null;

		// Get the part
		PartEnum part = PartEnum.getPartFromDamageValue( itemStack );

		// Attempt to create a new instance of the part
		try{
				newPart = part.createPartInstance(itemStack);
		}
		catch( Throwable e )
		{
			// Bad stuff, log the error.

		}

		// Return the part
		return newPart;

	}
	@Override
	public void addInformation( final ItemStack is, final EntityPlayer player, final List displayList, final boolean advancedItemTooltips ) {
	/**	String baseFormat = EnumChatFormatting.WHITE.toString();
		EnumChatFormatting CurrentFormat = EnumChatFormatting.DARK_RED;
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			switch (is.getItemDamage()){
				case 0:
					// IMPORT BUS
					displayList.add(EnumChatFormatting.DARK_PURPLE.toString() + "ME"+baseFormat+" Import bus can " + EnumChatFormatting.AQUA.toString() + "decrease "+baseFormat + "it's "+CurrentFormat.toString()+"Entropy,");
					displayList.add(baseFormat + "When system requests inject, it " + EnumChatFormatting.AQUA.toString() + "decrease " + baseFormat + "it's entropy, and holding");
					displayList.add(baseFormat + "it's energy, then annihilation core writes entropy states, and signature of energy to"+EnumChatFormatting.DARK_PURPLE.toString()+" ME"+baseFormat+" system");
					break;
				case 1:
					// STORAGE BUS
					displayList.add(EnumChatFormatting.DARK_PURPLE.toString() + "ME"+baseFormat
							+" Energy Storage bus"+baseFormat+" can operate " + CurrentFormat.toString() + "Entropy");
					displayList.add(baseFormat + "this value represents disorder of energy in system, or it can display the probability of system states.");
					displayList.add("");
					displayList.add(baseFormat + "When system requests extract energy, it " + EnumChatFormatting.AQUA.toString() + "decrease "+ baseFormat + "it's entropy with energy annihilation core");
					displayList.add(baseFormat + "Core concentrates energy, and then system can bus read it, and sync energy with system.");
					displayList.add("");
					displayList.add(baseFormat + "When system requests inject energy, it "+EnumChatFormatting.RED.toString()+"increase"+baseFormat+" it's entropy with energy formation core");
					displayList.add(baseFormat + "Core spreads energy, that it extracts, magnetic system directs it to output.");
					break;
				case 2:
					// EXPORT BUS
					displayList.add(EnumChatFormatting.DARK_PURPLE.toString() + "ME"+baseFormat+" Export bus"+baseFormat+" can " + EnumChatFormatting.RED.toString() + "increase "+baseFormat + "it's "+CurrentFormat.toString()+"Entropy,");
					displayList.add(baseFormat + "When system requests extract, it " + EnumChatFormatting.RED.toString() + "increase " + baseFormat + "it's entropy, and spreads");
					displayList.add(baseFormat + "it's energy, then formation core reads entropy states, and signature of energy from"+EnumChatFormatting.DARK_PURPLE.toString()+" ME"+baseFormat+" system");
					break;
				case 3:
					// TERMINAL
					displayList.add(EnumChatFormatting.DARK_PURPLE.toString() + "ME"+baseFormat+" Energy terminal can operate"+CurrentFormat+" Entropy,");
					displayList.add(baseFormat + "to inject/extract energies from items.");
					displayList.add("");
					displayList.add(baseFormat+ "When system data changes(also on construct in world), it tracks in terminal");
					displayList.add(baseFormat+ "You can see full list of energies in terminal");
					break;
				case 4:
					// INTERFACE
					displayList.add(EnumChatFormatting.DARK_PURPLE.toString() + "ME"+baseFormat+" Energy interface"+baseFormat+" can operate " + CurrentFormat.toString() + "Entropy");
					displayList.add(baseFormat + "this value represents disorder of energy in system, or it can display the probability of system states.");
					displayList.add("");
					displayList.add(baseFormat + "If entropy in system is low, it means energy more concentrated in this system,");
					displayList.add(baseFormat + "also it can represent order of system; Ex: if water entropy is very low it means,");
					displayList.add(baseFormat + "that water energy is concentrated, and in this case water state going to be ice,");
					displayList.add(baseFormat + "because atoms of 'ice' have lower kinetic energy, than atoms of water");
					displayList.add("");
					displayList.add(baseFormat + "If entropy in system is high, it means energy more spread, and system can share it with universe");
					displayList.add(baseFormat + "Water Rule: if entropy of water is high, it means that water heats up, because energy don't 'holds' water atoms.");
					displayList.add("");
					displayList.add(baseFormat + "WAIT?! But how it represents work of this machine?");
					displayList.add("");
					displayList.add(baseFormat + "When interface injects energy, it " + EnumChatFormatting.AQUA.toString() + "decrease "+ baseFormat + "it's entropy with energy annihilation core");
					displayList.add(baseFormat + "Core concentrates energy, and then system can easily read it, and sync energy with system.");
					displayList.add("");
					displayList.add(baseFormat + "When interface extracts energy, it "+EnumChatFormatting.RED.toString()+"increase"+baseFormat+" it's entropy with energy formation core");
					displayList.add(baseFormat + "Core spreads energy, that it extracts, magnetic system directs it to output.");
					displayList.add("");
					displayList.add("");
					displayList.add(EnumChatFormatting.YELLOW.toString() + "P.S. Lore is not in-game wiki, ok?");

					break;
				case 5:

					break;
			}

		} else {
			displayList.add(EnumChatFormatting.YELLOW.toString() + "Hold SHIFT to see" + EnumChatFormatting.GOLD.toString() + " Lore ");
		}*/
	}
	@Override
	public EnumRarity getRarity( final ItemStack itemStack )
	{
		return EnumRarity.epic;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getSpriteNumber()
	{
		return 0;
	}

	@Override
	public void getSubItems( final Item item, final CreativeTabs tab, final List itemList )
	{
		// Get the number of parts
		int count = PartEnum.VALUES.length;

		// Add each one to the list
		for( int i = 0; i < count; i++ )
		{
				itemList.add(new ItemStack(item, 1, i));
		}

	}

	@Override
	public String getUnlocalizedGroupName( final Set<ItemStack> arg0, final ItemStack itemStack )
	{
		return PartEnum.getPartFromDamageValue( itemStack ).getGroupName();
	}

	@Override
	public String getUnlocalizedName()
	{
		return "item.aeparts";
	}

	@Override
	public String getUnlocalizedName( final ItemStack itemStack )
	{
		return PartEnum.getPartFromDamageValue( itemStack ).getUnlocalizedName();
	}

	@Override
	public boolean onItemUse(	final ItemStack itemStack, final EntityPlayer player, final World world, final int x, final int y, final int z,
								 final int side, final float hitX, final float hitY, final float hitZ )
	{
		// Can we place the item on the bus?
		return AEApi.instance().partHelper().placeBus( itemStack, x, y, z, side, player, world );
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons( final IIconRegister par1IconRegister )
	{
	}
}
