package AppliedIntegrations.API;


/**
 * @Author Azazell
 */
@Deprecated
public enum FluidEnum {
    RF(1,LiquidAIEnergy.RF);
    LiquidAIEnergy AssociatedEnergyFluid;
    int id;
    FluidEnum(int id,LiquidAIEnergy associatedEnergyFluid) {
this.AssociatedEnergyFluid =associatedEnergyFluid;
this.id = id;
    }
    public LiquidAIEnergy getFluidById(int id){
    return null;
    }
}
