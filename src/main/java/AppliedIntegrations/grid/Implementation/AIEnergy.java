package AppliedIntegrations.grid.Implementation;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import net.minecraft.util.ResourceLocation;

public class AIEnergy {
    public static final LiquidAIEnergy RF = new LiquidAIEnergy("minecraft", 1,"RF",new ResourceLocation(AppliedIntegrations.modid,"textures/gui/energy.rf.bar.png")); // Redstone flux
    public static final LiquidAIEnergy J = new LiquidAIEnergy("mekanism",2,"J",new ResourceLocation(AppliedIntegrations.modid,"textures/fluids/empty.png")); // Mekansim joules
    public static final LiquidAIEnergy EU = new LiquidAIEnergy("ic2",3,"EU",new ResourceLocation(AppliedIntegrations.modid,"textures/fluids/empty.png")); // Energy units IC2
    public static final LiquidAIEnergy HU = new LiquidAIEnergy("ic2",4,"HU",new ResourceLocation(AppliedIntegrations.modid,"textures/fluids/empty.png")); // Heat units IC2
    public static final LiquidAIEnergy KU = new LiquidAIEnergy("ic2", 5,"KU",new ResourceLocation(AppliedIntegrations.modid,"textures/fluids/empty.png")); // Kinetic units IC2
    public static final LiquidAIEnergy FZ = new LiquidAIEnergy("ic2", 6,"Charge",new ResourceLocation(AppliedIntegrations.modid,"textures/fluids/empty.png")); // Factorization Charge
    public static final LiquidAIEnergy WA = new LiquidAIEnergy("rotarycraft",7,"WA",new ResourceLocation(AppliedIntegrations.modid,"textures/fluids/empty.png")); // Watts(from RotaryCraft)
    public static final LiquidAIEnergy AE = new LiquidAIEnergy("appliedenergistics2",8,"AE",new ResourceLocation(AppliedIntegrations.modid,"textures/fluids/empty.png")); // AE fluid energy
    public static final LiquidAIEnergy Ember = new LiquidAIEnergy("embers",9, "Ember", new ResourceLocation(AppliedIntegrations.modid,"textures/fluids/empty.png")); // Ember fluid energy
    public static final LiquidAIEnergy TESLA = new LiquidAIEnergy("tesla",10, "TESLA", new ResourceLocation(AppliedIntegrations.modid,"textures/fluids/empty.png")); // Tesla flui energy (From TESLA)
}
