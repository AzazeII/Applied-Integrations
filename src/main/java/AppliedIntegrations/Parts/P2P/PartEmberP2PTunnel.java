package AppliedIntegrations.Parts.P2P;

import AppliedIntegrations.Integration.Embers.IEmberIntegrated;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import teamroots.embers.power.EmberCapabilityProvider;
import teamroots.embers.power.IEmberCapability;
import teamroots.embers.tileentity.TileEntityEmitter;
import teamroots.embers.tileentity.TileEntityReceiver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// TODO: 2019-02-17 Integration with Embers
/**
 * @Author Azazell
 */
public class PartEmberP2PTunnel extends AIP2PTunnel<PartEmberP2PTunnel> implements IEmberIntegrated {

    private IEmberCapability outputHandler = new EmberOutputCapability();
    private IEmberCapability inputHandler = new EmberInputCapability();
    private IEmberCapability NULLHandler = new EmberNullCapability();

    public PartEmberP2PTunnel() {
        super(PartEnum.P2PEmber);
    }

    @Override
    protected AIGridNodeInventory getUpgradeInventory() {
        return null;
    }


    @Override
    public double getIdlePowerUsage() {
        return 0;
    }

    @Override
    public int getLightLevel() {
        return 0;
    }

    @Override
    public void onEntityCollision(Entity entity) {

    }

    @Override
    public boolean hasCapability( @Nonnull Capability<?> capability )
    {
        if( capability == EmberCapabilityProvider.emberCapability )
        {
            return true;
        }
        return super.hasCapability( capability );
    }

    @Nullable
    @Override
    public <T> T getCapability( @Nonnull Capability<T> capability )
    {
        if( capability == EmberCapabilityProvider.emberCapability)
        {
            if( this.isOutput() )
            {
                return (T) this.outputHandler;
            }
            return (T) this.inputHandler;
        }
        return super.getCapability( capability );
    }

    private class EmberInputCapability implements IEmberCapability {
        @Override
        public double getEmber() {
            int capacity = 0;

            for(PartEmberP2PTunnel tunnel : PartEmberP2PTunnel.this.getOutputs()){
                if(tunnel.getOperatedTile(TileEntityReceiver.class) != null)
                    capacity+=((TileEntityReceiver)tunnel.getOperatedTile(TileEntityReceiver.class)).getCapability(EmberCapabilityProvider.emberCapability,
                            null).
                            getEmber();
            }
            return capacity;
        }

        @Override
        public double getEmberCapacity(){
            int capacity = 0;

            for(PartEmberP2PTunnel tunnel : PartEmberP2PTunnel.this.getOutputs()){
                if(tunnel.getOperatedTile(TileEntityReceiver.class) != null)
                    capacity+=((TileEntityReceiver)tunnel.getOperatedTile(TileEntityReceiver.class)).getCapability(EmberCapabilityProvider.emberCapability
                    , null).
                            getEmberCapacity();
            }
            return capacity;
        }

        @Override
        public void setEmber(double v) {

        }

        @Override
        public void setEmberCapacity(double v) {

        }

        @Override
        public double addAmount(double v, boolean b) {
            return 0;
        }

        @Override
        public double removeAmount(double v, boolean b) {
            return 0;
        }

        @Override
        public void writeToNBT(NBTTagCompound nbtTagCompound) {

        }

        @Override
        public void readFromNBT(NBTTagCompound nbtTagCompound) {

        }

        @Override
        public void onContentsChanged() {

        }
    }

    private class EmberOutputCapability implements IEmberCapability {
        @Override
        public double getEmber() {
            return PartEmberP2PTunnel.this.getOperatedTile(TileEntityEmitter.class).getCapability(EmberCapabilityProvider.emberCapability
            , null).getEmber();
        }

        @Override
        public double getEmberCapacity() {
            return 0;
        }

        @Override
        public void setEmber(double v) {

        }

        @Override
        public void setEmberCapacity(double v) {

        }

        @Override
        public double addAmount(double v, boolean b) {
            return 0;
        }

        @Override
        public double removeAmount(double v, boolean b) {
            return 0;
        }

        @Override
        public void writeToNBT(NBTTagCompound nbtTagCompound) {

        }

        @Override
        public void readFromNBT(NBTTagCompound nbtTagCompound) {

        }

        @Override
        public void onContentsChanged() {

        }
    }


    private class EmberNullCapability implements IEmberCapability {
        @Override
        public double getEmber() {
            return 0;
        }

        @Override
        public double getEmberCapacity() {
            return 0;
        }

        @Override
        public void setEmber(double v) {

        }

        @Override
        public void setEmberCapacity(double v) {

        }

        @Override
        public double addAmount(double v, boolean b) {
            return 0;
        }

        @Override
        public double removeAmount(double v, boolean b) {
            return 0;
        }

        @Override
        public void writeToNBT(NBTTagCompound nbtTagCompound) {

        }

        @Override
        public void readFromNBT(NBTTagCompound nbtTagCompound) {

        }

        @Override
        public void onContentsChanged() {

        }
    }
}
