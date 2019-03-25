package AppliedIntegrations.Inventory.Handlers;

import AppliedIntegrations.API.Storage.*;
import AppliedIntegrations.Parts.Energy.PartEnergyStorage;
import AppliedIntegrations.Utils.AILog;
import AppliedIntegrations.grid.AEEnergyStack;
import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IItemList;


import net.minecraft.tileentity.TileEntity;

/**
 * @Author Azazell
 */
public class HandlerEnergyStorageBusContainer
        implements IMEInventoryHandler<IAEEnergyStack>
{

    private TileEntity storage;
    private EnumCapabilityType type;
    private PartEnergyStorage owner;

    public HandlerEnergyStorageBusContainer(PartEnergyStorage owner, TileEntity operand, EnumCapabilityType type) {
        this.storage = operand;
        this.type = type;
        this.owner = owner;
        AILog.info("Initialized new HandlerEnergyStorageBusContainer for " + operand.toString());
    }
    /**
     * Store new items, or simulate the addition of new items into the ME Inventory.
     *
     * @param input item to add.
     * @param type action type
     * @param src action source
     *
     * @return returns the number of items not added.
     */
    @Override
    public IAEEnergyStack injectItems(IAEEnergyStack input, Actionable type, IActionSource src) {
        if(input == null)
            return null;
        if(!input.isMeaningful())
            return null;
        AILog.info("Inject request; StackSize: " + input.getStack().amount + " EnergyName: " + input.getStack().getEnergyName());
        CapabilityHelper helper = new CapabilityHelper(storage, owner.getSide());

        int number = helper.receiveEnergy(input.getStackSize(), type == Actionable.SIMULATE, this.type.energy);
        int notAdded = (int)input.getStackSize() - number;

        if(notAdded > 0)
            return input.copy().setStackSize(notAdded);
        return null;
    }

    /**
     * Extract the specified item from the ME Inventory
     *
     * @param request item to request ( with stack size. )
     * @param mode simulate, or perform action?
     *
     * @return returns the number of items extracted, null
     */
    @Override
    public IAEEnergyStack extractItems(IAEEnergyStack request, Actionable mode, IActionSource src) {
        if(request == null)
            return null;
        if(!request.isMeaningful())
            return null;

        CapabilityHelper helper = new CapabilityHelper(storage, owner.getSide());

        int added = helper.extractEnergy(request.getStackSize(), mode == Actionable.SIMULATE, this.type.energy);

        if(added > 0)
            return request.copy().setStackSize(added);
        return null;
    }

    /**
     * request a full report of all available items, storage.
     *
     * @param out the IItemList the results will be written too
     *
     * @return returns same list that was passed in, is passed out
     */
    @Override
    public IItemList<IAEEnergyStack> getAvailableItems(IItemList<IAEEnergyStack> out) {
        CapabilityHelper helper = new CapabilityHelper(storage, owner.getSide());

        int stored = helper.getStored(type.energy);

        // Ignore tesla
        // TODO: 2019-02-27 Add full tesla, ember, mekanism, eu capability
        int amount = stored;
        out.add(AEEnergyStack.fromStack(new EnergyStack(type.energy, amount)));

        return out;
    }

    @Override
    public AccessRestriction getAccess() {
        return AccessRestriction.READ_WRITE;
    }

    @Override
    public boolean isPrioritized(IAEEnergyStack input) {
        return false;
    }

    @Override
    public boolean canAccept(IAEEnergyStack input) {
        if (this.storage == null)
            return false;
        return false;
    }

    @Override
    public int getPriority() {
        // TODO: 2019-02-27 Priority
        return 0;
    }

    @Override
    public int getSlot() {
        return 0;
    }

    @Override
    public boolean validForPass(int i) {
        return true;
    }

    /**
     * @return the type of channel your handler should be part of
     */
    @Override
    public IStorageChannel<IAEEnergyStack> getChannel() {
        return AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class);
    }
}
