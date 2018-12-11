package AppliedIntegrations.Layers;

import Reika.RotaryCraft.API.Interfaces.Transducerable;
import Reika.RotaryCraft.API.Power.AdvancedShaftPowerReceiver;
import Reika.RotaryCraft.API.Power.ShaftPowerReceiver;
import appeng.api.parts.IPart;
import appeng.api.parts.LayerBase;
import appeng.integration.IntegrationType;
import appeng.transformer.annotations.Integration;
import cpw.mods.fml.common.Optional;
import ic2.api.energy.tile.IHeatSource;
import ic2.api.energy.tile.IKineticSource;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;

@Optional.InterfaceList(value = {
        @Optional.Interface(iface = "ic2.api.energy.event.EnergyTileLoadEvent",modid = "IC2",striprefs = true),
        @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink",modid = "IC2",striprefs = true),
        @Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyStorage",modid = "Mekanism",striprefs = true),
        @Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyAcceptor",modid = "Mekanism",striprefs = true),
        @Optional.Interface(iface = "cofh.api.energy.EnergyStorage",modid = "CoFHAPI",striprefs = true),
        @Optional.Interface(iface = "cofh.api.energy.IEnergyReceiver",modid = "CoFHAPI",striprefs = true),
        @Optional.Interface(iface = "Reika.RotaryCraft.API.Interfaces.Transducerable",modid = "RotaryCraft",striprefs = true),
        @Optional.Interface(iface = "Reika.RotaryCraft.API.Power.AdvancedShaftPowerReceiver",modid = "RotaryCraft",striprefs = true)}

)
public class LayerRotaryCraft extends LayerBase implements AdvancedShaftPowerReceiver, Transducerable {
    // Duality: RotaryCraft
    @Override
    public boolean canReadFrom(ForgeDirection side) {
        IPart part = this.getPart(side);
        if(part instanceof AdvancedShaftPowerReceiver ){
            return ((AdvancedShaftPowerReceiver)part).canReadFrom(side);
        }
        return false;
    }

    @Override
    public boolean isReceiving() {
        for(ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            IPart part = this.getPart(side);
            if (part instanceof AdvancedShaftPowerReceiver) {
                return ((AdvancedShaftPowerReceiver) part).isReceiving();
            }
        }
        return false;
    }

    @Override
    public int getMinTorque(int i) {
        for(ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            IPart part = this.getPart(side);
            if (part instanceof AdvancedShaftPowerReceiver) {
                return ((AdvancedShaftPowerReceiver) part).getMinTorque(i);
            }
        }
        return 0;
    }

    @Override
    public int getOmega() {
        for(ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            IPart part = this.getPart(side);
            if (part instanceof AdvancedShaftPowerReceiver) {
                return ((AdvancedShaftPowerReceiver) part).getOmega();
            }
        }
        return 0;
    }

    @Override
    public int getTorque() {
        for(ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            IPart part = this.getPart(side);
            if (part instanceof AdvancedShaftPowerReceiver) {
                return ((AdvancedShaftPowerReceiver) part).getTorque();
            }
        }
        return 0;
    }

    @Override
    public long getPower() {
        for(ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            IPart part = this.getPart(side);
            if (part instanceof AdvancedShaftPowerReceiver) {
                return ((AdvancedShaftPowerReceiver) part).getPower();
            }
        }
        return 0;
    }

    @Override
    public String getName() {
        for(ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            IPart part = this.getPart(side);
            if (part instanceof AdvancedShaftPowerReceiver) {
                return ((AdvancedShaftPowerReceiver) part).getName();
            }
        }
        return null;
    }

    @Override
    public int getIORenderAlpha() {
        for(ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            IPart part = this.getPart(side);
            if (part instanceof AdvancedShaftPowerReceiver) {
                return ((AdvancedShaftPowerReceiver) part).getIORenderAlpha();
            }
        }
        return 0;
    }

    @Override
    public void setIORenderAlpha(int i) {
        for(ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            IPart part = this.getPart(side);
            if (part instanceof AdvancedShaftPowerReceiver) {
                ((AdvancedShaftPowerReceiver) part).setIORenderAlpha(i);
            }
        }
    }

    @Override
    public boolean addPower(int i, int i1, long l, ForgeDirection forgeDirection) {
        IPart part = this.getPart(forgeDirection);
        if(part instanceof AdvancedShaftPowerReceiver){
            ((AdvancedShaftPowerReceiver)part).addPower(i,i1,l,forgeDirection);
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<String> getMessages(World world, int i, int i1, int i2, int i3) {
        for(ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            IPart part = this.getPart(side);
            if (part instanceof Transducerable) {
                return ((Transducerable)part).getMessages(world,i,i1,i2,i3);
            }
        }
        return null;
    }

}
