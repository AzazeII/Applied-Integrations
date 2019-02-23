package AppliedIntegrations.API.Storage;

import AppliedIntegrations.AppliedIntegrations;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

import java.util.LinkedHashMap;

/**
 * @Author Azazell
 */
public class LiquidAIEnergy extends Fluid {

    String tag;
    int index;
    ResourceLocation image;

    public LiquidAIEnergy(Integer index,String tag, ResourceLocation image) {
        super(tag, null, null);
        if (energies.containsKey(tag)) throw new IllegalArgumentException(tag + " already registered!");
        this.tag = tag;
        this.image = image;
        this.index = index;
        linkedIndexMap.put(index,this);
        energies.put(tag, this);
    }
    public String getEnergyName() {
        return tag;
    }

    public LiquidAIEnergy getEnergy(){
        return this;
    }
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public ResourceLocation getImage() {
        return image;
    }
    public int getIndex(){
        return this.index;
    }

    public static LiquidAIEnergy getEnergy(String tag) {
        return energies.get(tag);
    }
    public static LinkedHashMap<String, LiquidAIEnergy> energies = new LinkedHashMap<String, LiquidAIEnergy>();
    public static LinkedHashMap<Integer, LiquidAIEnergy> linkedIndexMap = new LinkedHashMap<Integer, LiquidAIEnergy>();

    static {
        linkedIndexMap.put(0,null);
    }

    public static final LiquidAIEnergy RF = new LiquidAIEnergy(1,"RF",new ResourceLocation(AppliedIntegrations.modid,"textures/fluids/RF.png")); // Redstone flux
    public static final LiquidAIEnergy J = new LiquidAIEnergy(2,"J",new ResourceLocation(AppliedIntegrations.modid,"textures/fluids/J.png")); // Mekansim joules
    public static final LiquidAIEnergy EU = new LiquidAIEnergy(3,"EU",new ResourceLocation(AppliedIntegrations.modid,"textures/fluids/EU.png")); // Energy units IC2
    public static final LiquidAIEnergy HU = new LiquidAIEnergy(4,"HU",new ResourceLocation(AppliedIntegrations.modid,"textures/fluids/HU.png")); // Heat units IC2
    public static final LiquidAIEnergy KU = new LiquidAIEnergy(5,"KU",new ResourceLocation(AppliedIntegrations.modid,"textures/fluids/KU.png")); // Kinetic units IC2
    public static final LiquidAIEnergy FZ = new LiquidAIEnergy(6,"Charge",new ResourceLocation(AppliedIntegrations.modid,"textures/fluids/FZ.png")); // Factorization Charge
    public static final LiquidAIEnergy WA = new LiquidAIEnergy(7,"WA",new ResourceLocation(AppliedIntegrations.modid,"textures/fluids/WA.png")); // Watts(from RotaryCraft)
    public static final LiquidAIEnergy AE = new LiquidAIEnergy(8,"AE",null); // AE fluid energy
    public static final LiquidAIEnergy Ember = new LiquidAIEnergy(9, "Ember", null);
    public static final LiquidAIEnergy TESLA = new LiquidAIEnergy(10, "TESLA", null);
}