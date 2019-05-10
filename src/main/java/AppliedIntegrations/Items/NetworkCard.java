package AppliedIntegrations.Items;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.tile.Server.TileServerPort;
import appeng.api.networking.IGrid;
import appeng.api.util.AEPartLocation;
import appeng.util.Platform;
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

import java.util.List;
import java.util.Objects;

public class NetworkCard extends AIItemRegistrable {
    public static final String NBT_KEY_HAS_NET = "#HAS_NETWORK";
    private static final String NBT_KEY_NET_SIDE = "#NET_SIDE";
    private static final String NBT_KEY_PERMISSIONS = "#PERMISSIONS";

    public NetworkCard(String registry) {
        super(registry);

        // Change stack size
        this.setMaxStackSize(1);

        // Add property for mode animation
        this.addPropertyOverride(new ResourceLocation(AppliedIntegrations.modid,"bit"), (stack, worldIn, entityIn) -> {
            // Get stack tag
            NBTTagCompound tag = Platform.openNbtData(stack);

            return (((float) tag.getInteger(NBT_KEY_NET_SIDE) + 1) / (float) 10);
        });
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

        // Add formatted side name
        lines.add("Port - " + side.name().toLowerCase());

        // Add permissions info
        lines.add(tag.getString(NBT_KEY_PERMISSIONS));
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
