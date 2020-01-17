package AppliedIntegrations.Items;
import AppliedIntegrations.AIConfig;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.tile.MultiController.ContainerMultiControllerTerminal;
import AppliedIntegrations.Gui.MultiController.SubGui.Buttons.GuiSecurityPermissionsButton;
import AppliedIntegrations.api.AIApi;
import AppliedIntegrations.api.AIApi.IStackDecoder;
import AppliedIntegrations.api.Storage.IChannelWidget;
import AppliedIntegrations.tile.MultiController.TileMultiControllerPort;
import appeng.api.config.IncludeExclude;
import appeng.api.config.SecurityPermissions;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.AEPartLocation;
import appeng.util.Platform;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import static AppliedIntegrations.Gui.MultiController.SubGui.Buttons.GuiSecurityPermissionsButton.getPermissionList;

/**
 * @Author Azazell
 */
public class NetworkCard extends AIItemRegistrable {
	public static final String NBT_KEY_HAS_NET = "#HAS_NETWORK";
	public static final String KEY_SUB = "#SUB_TAG";
	public static final String NBT_KEY_PORT_SIDE = "#NET_SIDE";
	public static final String NBT_KEY_PORT_ID = "#PORT_ID";
	private static final String NBT_KEY_PERMISSIONS = "#PERMISSIONS";
	private static final String NBT_KEY_LIST_SIZE = "#LIST_SIZE";
	private static final String NBT_KEY_LIST_MODE = "#LIST_MODE";

	public NetworkCard(String registry) {
		super(registry);
		this.setMaxStackSize(1);
		this.addPropertyOverride(new ResourceLocation(AppliedIntegrations.modid, "bit"), (stack, worldIn, entityIn) -> {
			NBTTagCompound tag = Platform.openNbtData(stack);

			if (!tag.getBoolean(NBT_KEY_HAS_NET)) {
				tag.setInteger(NBT_KEY_PORT_SIDE, AEPartLocation.INTERNAL.ordinal());
			}

			return (((float) tag.getInteger(NBT_KEY_PORT_SIDE) + 1) / (float) 10);
		});
	}

	public static Pair<LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IAEStack<? extends IAEStack>>>>, LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude>>> decodeDataFromTag(NBTTagCompound tag) {
		LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IAEStack<? extends IAEStack>>>> channelStackMap
				= new LinkedHashMap<>();
		LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude>> channelTypeMap
				= new LinkedHashMap<>();

		// Iterate for each security permissions
		GuiSecurityPermissionsButton.getPermissionList().forEach((securityPermissions -> {
			LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude> tempMap = new LinkedHashMap<>();
			ContainerMultiControllerTerminal.channelList.forEach((chan -> {
				tempMap.put(chan, AIConfig.defaultListMode);
			}));

			channelTypeMap.put(securityPermissions, tempMap);
		}));

		for (int i = 0; i < getPermissionList().size(); i++) {
			NBTTagCompound securityTag = (NBTTagCompound) tag.getTag(KEY_SUB + "_" + getPermissionList().get(i).name());
			LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IAEStack<? extends IAEStack>>> stackMap = new LinkedHashMap<>(); // (1)
			LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude> typeMap = new LinkedHashMap<>(); // (2)

			for (int j = 0; j < ContainerMultiControllerTerminal.channelList.size(); j++) {
				List<IAEStack<? extends IAEStack>> list = new ArrayList<>();
				if (securityTag == null) {
					break;
				}

				NBTTagCompound channelTag = (NBTTagCompound) securityTag.getTag(KEY_SUB + j);
				if (channelTag == null) {
					break;
				}

				for (int k = 0; k < channelTag.getInteger(NBT_KEY_LIST_SIZE); k++) {
					NBTTagCompound stackTag = (NBTTagCompound) channelTag.getTag(KEY_SUB + k);
					IStackDecoder decoder = Objects.requireNonNull(AIApi.instance()).getStackDecoder(ContainerMultiControllerTerminal.channelList.get(j));

					try {
						IAEStack<?> stack = decoder.decode(stackTag);

						list.add(stack);
					} catch (IOException e) {
						throw new IllegalStateException("Unexpected error");
					}
				}

				// Get Include/Exclude mode
				IncludeExclude mode = IncludeExclude.values()[channelTag.getInteger(NBT_KEY_LIST_MODE)];
				typeMap.put(ContainerMultiControllerTerminal.channelList.get(j), mode);
				stackMap.put(ContainerMultiControllerTerminal.channelList.get(j), list);
			}

			channelStackMap.put(getPermissionList().get(i), stackMap);
			channelTypeMap.put(getPermissionList().get(i), typeMap);
		}

		return Pair.of(channelStackMap, channelTypeMap);
	}

	public static NBTTagCompound encodeDataInTag(LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IChannelWidget<?>>>> permissionChannelWidgetMap, LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude>> permissionChannelModeMap, ItemStack stack) {
		NBTTagCompound tag = Platform.openNbtData(stack);
		List<SecurityPermissions> permissionList = getPermissionList();

		permissionList.forEach((securityPermissions -> {
			NBTTagCompound securityNBT = new NBTTagCompound();

			for (int j = 0; j < ContainerMultiControllerTerminal.channelList.size(); j++) {
				IStorageChannel<? extends IAEStack<?>> chan = ContainerMultiControllerTerminal.channelList.get(j);
				NBTTagCompound channelNBT = new NBTTagCompound();
				channelNBT.setInteger(NBT_KEY_LIST_MODE, permissionChannelModeMap.get(securityPermissions).get(chan).ordinal());

				List<? extends IChannelWidget<?>> list = permissionChannelWidgetMap.get(securityPermissions).get(chan);
				channelNBT.setInteger(NBT_KEY_LIST_SIZE, list.size());

				for (int i = 0; i < list.size(); i++) {
					IChannelWidget<?> widget = list.get(i);
					NBTTagCompound stackNBT = new NBTTagCompound();

					try {
						if (widget.getAEStack() != null) {
							Objects.requireNonNull(AIApi.instance()).getStackEncoder(chan).encode(stackNBT, widget.getAEStack());
						} else {
							stackNBT.setLong("Cnt", -1);
						}
					} catch (IOException e) {
						throw new IllegalStateException("Unexpected error");
					}

					channelNBT.setTag(KEY_SUB + i, stackNBT);
				}

				securityNBT.setTag(KEY_SUB + j, channelNBT);
			}

			tag.setTag(KEY_SUB + "_" + securityPermissions.name(), securityNBT);
		}));

		return tag;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(final ItemStack stack, final World world, final List<String> lines, final ITooltipFlag advancedTooltips) {
		NBTTagCompound tag = Platform.openNbtData(stack);
		if (tag == null) {
			return;
		}

		AEPartLocation side = AEPartLocation.values()[tag.getInteger(NBT_KEY_PORT_SIDE)];
		if (side == AEPartLocation.INTERNAL) {
			return;
		}

		String str = side.name().toLowerCase();
		String name = str.substring(0, 1).toUpperCase() + str.substring(1);

		int id = tag.getInteger(NBT_KEY_PORT_ID);

		lines.add(I18n.format("network_card_port_side.name") + " - " + name);
		lines.add(I18n.format("network_card_port_id.name") + " - " + id);
		lines.add(tag.getString(NBT_KEY_PERMISSIONS));
		String tables = "";

		for (int i = 0; i < getPermissionList().size(); i++) {
			NBTTagCompound securityTag = (NBTTagCompound) tag.getTag(KEY_SUB + getPermissionList().get(i));

			for (int j = 0; j < ContainerMultiControllerTerminal.channelList.size(); j++) {
				if (securityTag == null || securityTag.getTag(KEY_SUB + j) == null) {
					return;
				}

				NBTTagCompound channelTag = (NBTTagCompound) securityTag.getTag(KEY_SUB + j);
				for (int k = 0; k < tag.getInteger(NBT_KEY_LIST_SIZE); k++) {
					NBTTagCompound stackTag = (NBTTagCompound) channelTag.getTag(KEY_SUB + k);
					if (stackTag.getLong("Cnt") == -1) {
						if (!tables.equals("")) {
							tables = tables.concat(" " + I18n.format(getPermissionList().get(i).getUnlocalizedName()));
						} else {
							tables = I18n.format(getPermissionList().get(i).getUnlocalizedName());
						}
					}
				}
			}
		}

		lines.add("Tables: " + tables);
	}

	@Override
	public EnumActionResult onItemUseFirst(final EntityPlayer player, final World world, final BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ, final EnumHand hand) {
		NBTTagCompound tag = Platform.openNbtData(player.getHeldItem(hand));

		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileMultiControllerPort) {
			TileMultiControllerPort port = (TileMultiControllerPort) tile;

			tag.setBoolean(NBT_KEY_HAS_NET, port.getSideVector() != AEPartLocation.INTERNAL);
			tag.setInteger(NBT_KEY_PORT_SIDE, port.getSideVector().ordinal());
			tag.setInteger(NBT_KEY_PORT_ID, port.getPortID());
			return EnumActionResult.SUCCESS;
		}

		if (player.isSneaking()) {
			player.getHeldItem(hand).setTagCompound(new NBTTagCompound());
		}

		return EnumActionResult.FAIL;
	}
}
