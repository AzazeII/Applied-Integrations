package AppliedIntegrations.Container.part;
import AppliedIntegrations.Container.ContainerWithUpgradeSlots;
import AppliedIntegrations.Container.slot.SlotFilter;
import AppliedIntegrations.Parts.Interaction.PartInteractionPlane;
import AppliedIntegrations.api.ISyncHost;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @Author Azazell
 */
public class ContainerInteractionPlane extends ContainerWithUpgradeSlots {
	private static final int SLOT_AREA = 3;
	private PartInteractionPlane plane;

	public ContainerInteractionPlane(EntityPlayer player, PartInteractionPlane interaction) {
		super(player);
		this.plane = interaction;

		this.bindPlayerInventory(player.inventory, 149, 207);

		int index = 0;
		for (int x = 0; x < SLOT_AREA; x++ ) {
			for (int y = 0; y < SLOT_AREA; y++) {
				this.addSlotToContainer(new SlotFilter(interaction.filterInventory, index, 62 + (x * 18), 22 + (y * 18)));
				index++;
			}
		}
	}

	@Override
	public ISyncHost getSyncHost() {
		return plane;
	}

	@Override
	public void setSyncHost(ISyncHost host) {
		this.plane = (PartInteractionPlane) host;
	}
}
