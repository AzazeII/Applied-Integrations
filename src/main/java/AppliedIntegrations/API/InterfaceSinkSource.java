package AppliedIntegrations.API;

import appeng.me.helpers.BaseActionSource;
import ic2.api.energy.prefab.BasicSinkSource;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InterfaceSinkSource extends BasicSinkSource implements IInterfaceStorageDuality{

    public InterfaceSinkSource(World world, BlockPos pos, double capacity, int sinkTier, int sourceTier) {
        super(world, pos, capacity, sinkTier, sourceTier);
    }

    @Override
    public boolean acceptsEnergyFrom(IEnergyEmitter iEnergyEmitter, EnumFacing enumFacing) {
        return true;
    }

    @Override
    public boolean emitsEnergyTo(IEnergyAcceptor iEnergyAcceptor, EnumFacing enumFacing) {
        return true;
    }

    @Override
    public void modifyEnergyStored(int i) {
        if(energyStored + i < getMaxStored())
            this.energyStored = i;
        else
            this.energyStored = getMaxStored();
    }

    @Override
    public double getStored() {
        return getEnergyStored();
    }

    @Override
    public double getMaxStored() {
        return getCapacity();
    }

    @Override
    public double receive(double value, boolean simulate) {
        return injectEnergy(null, value, 4);
    }

    @Override
    public double extract(double value, boolean simulate) {
        double storedBefore = getEnergyStored();
        drawEnergy(value);
        double storedAfter = getEnergyStored();

        return storedAfter - storedAfter;
    }
}
