package AppliedIntegrations.API;

/**
 * @Author Azazell
 */
public interface IEnergyInterface extends IInterfaceDuality{
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
