package AppliedIntegrations.Container.tile;
import AppliedIntegrations.Container.ContainerWithPlayerInventory;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.tile.LogicBus.TileLogicBusCore;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @Author Azazell
 */
public class ContainerLogicBus extends ContainerWithPlayerInventory {
	private TileLogicBusCore core;

	public ContainerLogicBus(EntityPlayer player, TileLogicBusCore core) {
		super(player);

		this.core = core;
		this.bindPlayerInventory(player.inventory, 134, 182);

        /*int counter = 0;
        // Add 6 columns
        for(int y = 0; y < 5; y++) {
            // And 9 slots for each column
            for (int x = 0; x < DualityInterface.NUMBER_OF_PATTERN_SLOTS; x++) {
                // Actually adding slot
                this.addSlotToContainer(new SlotRestrictedInput(SlotRestrictedInput.PlacableItemType.ENCODED_PATTERN,
                        core.getInterfaceDuality().getPatterns(), counter, 8 + 18 * x, 90 + 7 + 16 * y, player.inventory));
                counter++;
            }
        }*/
	}

	@Override
	public ISyncHost getSyncHost() {
		return this.core;
	}

	@Override
	public void setSyncHost(ISyncHost host) {
		if (host instanceof TileLogicBusCore) {
			this.core = (TileLogicBusCore) host;
		}
	}
}
