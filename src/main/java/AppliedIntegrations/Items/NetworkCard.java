package AppliedIntegrations.Items;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Gui.ServerGUI.SubGui.Buttons.GuiSecurityPermissionsButton;
import AppliedIntegrations.Gui.ServerGUI.SubGui.Buttons.GuiStorageChannelButton;
import AppliedIntegrations.api.AIApi;
import AppliedIntegrations.AIConfig;
import AppliedIntegrations.api.AIApi.IStackDecoder;
import AppliedIntegrations.api.Storage.IChannelWidget;
import AppliedIntegrations.tile.Server.TileServerPort;
import appeng.api.AEApi;
import appeng.api.config.IncludeExclude;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGrid;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.AEPartLocation;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
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

import static AppliedIntegrations.Gui.ServerGUI.SubGui.Buttons.GuiSecurityPermissionsButton.getPermissionList;
import static AppliedIntegrations.Gui.ServerGUI.SubGui.Buttons.GuiStorageChannelButton.getChannelList;

/**
 * @Author Azazell
 */
public class NetworkCard extends AIItemRegistrable {
    public static final String NBT_KEY_HAS_NET = "#HAS_NETWORK";
    public static final String KEY_SUB = "#SUB_TAG";
    public static final String NBT_KEY_NET_SIDE = "#NET_SIDE";

    private static final String NBT_KEY_PERMISSIONS = "#PERMISSIONS";
    private static final String NBT_KEY_LIST_SIZE = "#LIST_SIZE";
    private static final String NBT_KEY_LIST_MODE = "#LIST_MODE";

    public NetworkCard(String registry) {
        super(registry);

        // Change stack size
        this.setMaxStackSize(1);

        // Add property for mode animation
        this.addPropertyOverride(new ResourceLocation(AppliedIntegrations.modid,"bit"), (stack, worldIn, entityIn) -> {
            // Get stack tag
            NBTTagCompound tag = Platform.openNbtData(stack);

            // Check if tag has no network
            if (!tag.getBoolean(NBT_KEY_HAS_NET)){
                // Change tag to INTERNAL
                tag.setInteger(NBT_KEY_NET_SIDE, AEPartLocation.INTERNAL.ordinal());
            }

            return (((float) tag.getInteger(NBT_KEY_NET_SIDE) + 1) / (float) 10);
        });
    }

    public static Pair<LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IAEStack<? extends IAEStack>>>>,
                       LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude>>> decodeDataFromTag(NBTTagCompound tag) {
        // Create initial maps
        LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IAEStack<? extends IAEStack>>>> channelStackMap = new LinkedHashMap<>(); // (1)
        LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude>> channelTypeMap = new LinkedHashMap<>(); // (2)

        // Pre-fill map with mode values
        // Iterate for each security permissions
        GuiSecurityPermissionsButton.getPermissionList().forEach((securityPermissions -> {
            // Temp map
            LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude> tempMap = new LinkedHashMap<>();

            // Iterate for each storage channel
            GuiStorageChannelButton.getChannelList().forEach((chan -> {
                // Add default list mode to map
                tempMap.put(chan, AIConfig.defaultListMode);
            }));

            // Put temp map in permission channel map
            channelTypeMap.put(securityPermissions, tempMap);
        }));

        // Iterate for each permission
        for (int i = 0; i < getPermissionList().size(); i++) {
            // Get security-sub-tag
            NBTTagCompound securityTag = (NBTTagCompound) tag.getTag(KEY_SUB + "_" + getPermissionList().get(i).name());

            // Create temp channel map
            LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IAEStack<? extends IAEStack>>> stackMap = new LinkedHashMap<>(); // (1)
            LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude> typeMap = new LinkedHashMap<>(); // (2)

            // Iterate for each storage channel
            for (int j = 0; j < getChannelList().size(); j++) {
                // Create temp stack list
                List<IAEStack<? extends IAEStack>> list = new ArrayList<>();

                if (securityTag == null)
                    // Break cycle
                    break;

                // Get channel-sub-tag
                NBTTagCompound channelTag = (NBTTagCompound) securityTag.getTag(KEY_SUB + j);

                // Iterate until i = size
                for (int k = 0; k < channelTag.getInteger(NBT_KEY_LIST_SIZE); k++) {
                    // Get stack-sub-tag
                    NBTTagCompound stackTag = (NBTTagCompound) channelTag.getTag(KEY_SUB + k);

                    // Get decoder for stack
                    IStackDecoder decoder = Objects.requireNonNull(AIApi.instance()).getStackDecoder(getChannelList().get(j));

                    try {
                        // Decode stack
                        IAEStack<?> stack = decoder.decode(stackTag);

                        // Put stack in list
                        list.add(stack);
                    } catch (IOException e) {
                        throw new IllegalStateException("Unexpected error");
                    }
                }

                // Get Include/Exclude mode
                IncludeExclude mode = IncludeExclude.values()[channelTag.getInteger(NBT_KEY_LIST_MODE)];

                // Put mode in map
                typeMap.put(getChannelList().get(j), mode);

                // Put list in map
                stackMap.put(getChannelList().get(j), list);
            }

            // Put map in map
            channelStackMap.put(getPermissionList().get(i), stackMap); // (1)
            channelTypeMap.put(getPermissionList().get(i), typeMap); // (2)
        }

        return Pair.of(channelStackMap, channelTypeMap);
    }

    public static NBTTagCompound encodeDataInTag(LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IChannelWidget<?>>>> permissionChannelWidgetMap,
                                                 LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude>> permissionChannelModeMap,
                                                 ItemStack stack){
        // Create stack compound
        NBTTagCompound tag = Platform.openNbtData(stack);

        // Pointer to permissions list
        List<SecurityPermissions> permissionList = getPermissionList();

        // Iterate for each permission
        permissionList.forEach((securityPermissions -> {
            // Create permissions sub-nbt
            NBTTagCompound securityNBT = new NBTTagCompound();

            // Iterate for each storage channel
            for (int j = 0; j < getChannelList().size(); j++){
                // Get channel
                IStorageChannel<? extends IAEStack<?>> chan = getChannelList().get(j);

                // Create channel sub-nbt
                NBTTagCompound channelNBT = new NBTTagCompound();

                // Serialize black/white list mode
                channelNBT.setInteger(NBT_KEY_LIST_MODE, permissionChannelModeMap.get(securityPermissions).get(chan).ordinal());

                // Get current list
                List<? extends IChannelWidget<?>> list = permissionChannelWidgetMap.get(securityPermissions).get(chan);

                // Write size of list
                channelNBT.setInteger(NBT_KEY_LIST_SIZE, list.size());

                // Iterate until i = size
                for (int i = 0; i < list.size(); i++) {
                    // Get widget at i
                    IChannelWidget<?> widget = list.get(i);

                    // Create stack sub-nbt
                    NBTTagCompound stackNBT = new NBTTagCompound();

                    try {
                        // Check not null
                        if (widget.getAEStack() != null) {
                            // Serialize stack with Api into stack sub-nbt
                            Objects.requireNonNull(AIApi.instance()).getStackEncoder(chan).encode(stackNBT, widget.getAEStack());
                        } else {
                            // Encode "NaN" (for stack)
                            stackNBT.setLong("Cnt", -1);
                        }
                    } catch (IOException e) {
                        throw new IllegalStateException("Unexpected error");
                    }

                    // Add stack nbt to channel nbt. Total $(stacks count) tags
                    channelNBT.setTag(KEY_SUB + i, stackNBT);
                }

                // Encode sub-tag Total $(channel count) tags in one security
                securityNBT.setTag(KEY_SUB + j, channelNBT);
            }

            // Encode sub-tag. Total 3 security tags
            tag.setTag(KEY_SUB + "_" + securityPermissions.name(), securityNBT);
        }));

        return tag;
    }

    @SideOnly( Side.CLIENT )
    @Override
    public void addInformation( final ItemStack stack, final World world, final List<String> lines, final ITooltipFlag advancedTooltips ) {
        // Get stack tag
        NBTTagCompound tag = Platform.openNbtData(stack);

        // Check not null
        if (tag == null)
            return;

        // Get side
        AEPartLocation side = AEPartLocation.values()[tag.getInteger(NBT_KEY_NET_SIDE)];

        // Check if side isn't internal
        if (side == AEPartLocation.INTERNAL)
            return;

        // Get formatted string
        String str = side.name().toLowerCase();

        // Transform name
        String name = str.substring(0, 1).toUpperCase() + str.substring(1);

        // Add formatted side name
        lines.add("Port - " + name);

        // Add permissions info
        lines.add(tag.getString(NBT_KEY_PERMISSIONS));

        // Tables enumeration
        String tables = "";

        // Iterate for each permission
        for (int i = 0; i < getPermissionList().size(); i++) {
            // Get security-sub-tag
            NBTTagCompound securityTag = (NBTTagCompound) tag.getTag(KEY_SUB + getPermissionList().get(i));

            // Iterate for each storage channel
            for (int j = 0; j < getChannelList().size(); j++) {
                // Check not null
                if (securityTag == null || securityTag.getTag(KEY_SUB + j) == null)
                    return;

                // Get channel-sub-tag
                NBTTagCompound channelTag = (NBTTagCompound) securityTag.getTag(KEY_SUB + j);

                // Iterate until i = size
                for (int k = 0; k < tag.getInteger(NBT_KEY_LIST_SIZE); k++) {
                    // Get stack-sub-tag
                    NBTTagCompound stackTag = (NBTTagCompound) channelTag.getTag(KEY_SUB + k);

                    // Check if long "cnt" not null
                    if (stackTag.getLong("Cnt") == -1){
                        // Check if tables isn't empty
                        if (!tables.equals(""))
                            // Update tables
                            tables = tables.concat(" " + I18n.format(getPermissionList().get(i).getUnlocalizedName()));
                        else
                            // Update tables
                            tables = I18n.format(getPermissionList().get(i).getUnlocalizedName());
                    }
                }
            }
        }

        // Add tables info
        lines.add("Tables: " + tables);
    }

    @Override
    public EnumActionResult onItemUseFirst(final EntityPlayer player, final World world, final BlockPos pos, final EnumFacing side,
                                           final float hitX, final float hitY, final float hitZ, final EnumHand hand ) {
        // Get stack tag
        NBTTagCompound tag = Platform.openNbtData(player.getHeldItem(hand));

        // Get tile entity clicked
        TileEntity tile = world.getTileEntity(pos);

        // Check if tile instance of server port
        if (tile instanceof TileServerPort){
            // Get port
            TileServerPort port = (TileServerPort) tile;

            // Update NBT tag data
            tag.setBoolean(NBT_KEY_HAS_NET, port.getSideVector() != AEPartLocation.INTERNAL);
            tag.setInteger(NBT_KEY_NET_SIDE, port.getSideVector().ordinal());

            // Success!
            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.FAIL;
    }
}
