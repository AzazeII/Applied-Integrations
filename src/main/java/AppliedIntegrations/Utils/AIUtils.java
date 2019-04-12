package AppliedIntegrations.Utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
/**
 * @Author Azazell
 */
public final class AIUtils
{

    private static final double SQUARED_REACH = 64.0D;

    /**
     * Returns true if the Tile still exists and the player is within reach range.
     *
     * @param player
     * @param tile
     * @return
     */
    public static final boolean canPlayerInteractWith(@Nonnull final EntityPlayer player, @Nonnull final TileEntity tile )
    {
        TileEntity tileAtCoords = tile.getWorld().getTileEntity( tile.getPos() );

        // Null check
        if( tileAtCoords == null )
        {
            return false;
        }

        // Range check
        return( player.getDistanceSq( tile.getPos().getX() + 0.5D, tile.getPos().getY() + 0.5D, tile.getPos().getZ() + 0.5D ) <= AIUtils.SQUARED_REACH );

    }
}
