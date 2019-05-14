package AppliedIntegrations.tile.Server;

import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.Server.PacketRibSync;
import AppliedIntegrations.Utils.ChangeHandler;
import AppliedIntegrations.tile.IAIMultiBlock;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.EnumSet;


/**
 * @Author Azazell
 */
public class TileServerRib extends AIServerMultiBlockTile implements IAIMultiBlock, ITickable {

    // Used only client
    public boolean isActive;

    // Did activity of grid node changed?
    private ChangeHandler<Boolean> activityChangeHandler = new ChangeHandler<>();

    private void notifyListeners() {
        // Sync with client
        NetworkHandler.sendToDimension(new PacketRibSync(this, getGridNode().isActive()), world.provider.getDimension());
    }

    public IGrid getMainNetwork() {
        // Check not null
        if (getGridNode() == null)
            return null;

        return getNetwork();
    }

    @Override
    public void update() {
        super.update();

        // Check if grid node is not null
        if (getGridNode() != null) {
            // Call onchange of handler
            activityChangeHandler.onChange(getGridNode().isActive(), (activity -> {
                // Pass call to function
                notifyListeners();
            }));
        }
    }

    @Override
    public EnumSet<GridFlags> getFlags() {
        return EnumSet.of(GridFlags.DENSE_CAPACITY);
    }
}
