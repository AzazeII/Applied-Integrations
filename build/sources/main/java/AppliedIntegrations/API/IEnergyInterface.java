package AppliedIntegrations.API;

import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import appeng.api.networking.IGridNode;

/**
 * @Author Azazell
 */
public interface IEnergyInterface extends IInterfaceDuality, INetworkManipulator{
    IGridNode getGridNode();

    // Work mode
    enum DualityMode{
        Inject,
        Extract;
    }
    // Packet work mode
    enum FlowMode{
        Gui, // send data from part to gui
        Machine; // send data from gui to machine
    }
         LiquidAIEnergy getFilter(int index);
};
