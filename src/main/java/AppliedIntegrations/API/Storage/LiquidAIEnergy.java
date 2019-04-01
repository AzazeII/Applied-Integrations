package AppliedIntegrations.API.Storage;

import AppliedIntegrations.AppliedIntegrations;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

import java.util.LinkedHashMap;

/**
 * @Author Azazell
 */
public class LiquidAIEnergy extends Fluid {

    private String tag;
    private int index;
    private ResourceLocation image;

    public LiquidAIEnergy(Integer index,String tag, ResourceLocation image) {
        super(tag, image, image);

        // Check if energy is already registered
        if (energies.containsKey(tag)) throw new IllegalArgumentException(tag + " already registered!");

        // Set tag
        this.tag = tag;

        // Set image
        this.image = image;

        // Set index
        this.index = index;

        // Map energy by index
        LiquidAIEnergy.linkedIndexMap.put(index,this);

        // Map energy by tag
        LiquidAIEnergy.energies.put(tag, this);
    }
    public String getEnergyName() {
        return tag;
    }

    // #Getter for #Getter for #Getter for #Get.... and so on.
    /*
        void getGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGet(){
            return this.getGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGetGet();
        }
     */

    // #Getter for tag
    public String getTag() {
        return tag;
    }

    // #Setter for tag
    public void setTag(String tag) {
        this.tag = tag;
    }

    // #Getter for image
    public ResourceLocation getImage() {
        return image;
    }

    // #Getter for index
    public int getIndex(){
        return this.index;
    }

    // Get energy from it's tag
    public static LiquidAIEnergy getEnergy(String tag) {
        return energies.get(tag);
    }

    // Energies mapped by tag
    public static LinkedHashMap<String, LiquidAIEnergy> energies = new LinkedHashMap<String, LiquidAIEnergy>();

    // Energies mapped by index
    public static LinkedHashMap<Integer, LiquidAIEnergy> linkedIndexMap = new LinkedHashMap<Integer, LiquidAIEnergy>();

    static {
        linkedIndexMap.put(0,null);
    }

    public static final LiquidAIEnergy RF = new LiquidAIEnergy(1,"RF",new ResourceLocation(AppliedIntegrations.modid,"textures/gui/energy.rf.bar.png")); // Redstone flux
    public static final LiquidAIEnergy J = new LiquidAIEnergy(2,"J",new ResourceLocation(AppliedIntegrations.modid,"textures/fluids/empty.png")); // Mekansim joules
    public static final LiquidAIEnergy EU = new LiquidAIEnergy(3,"EU",new ResourceLocation(AppliedIntegrations.modid,"textures/fluids/empty.png")); // Energy units IC2
    public static final LiquidAIEnergy HU = new LiquidAIEnergy(4,"HU",new ResourceLocation(AppliedIntegrations.modid,"textures/fluids/empty.png")); // Heat units IC2
    public static final LiquidAIEnergy KU = new LiquidAIEnergy(5,"KU",new ResourceLocation(AppliedIntegrations.modid,"textures/fluids/empty.png")); // Kinetic units IC2
    public static final LiquidAIEnergy FZ = new LiquidAIEnergy(6,"Charge",new ResourceLocation(AppliedIntegrations.modid,"textures/fluids/empty.png")); // Factorization Charge
    public static final LiquidAIEnergy WA = new LiquidAIEnergy(7,"WA",new ResourceLocation(AppliedIntegrations.modid,"textures/fluids/empty.png")); // Watts(from RotaryCraft)
    public static final LiquidAIEnergy AE = new LiquidAIEnergy(8,"AE",new ResourceLocation(AppliedIntegrations.modid,"textures/fluids/empty.png")); // AE fluid energy
    public static final LiquidAIEnergy Ember = new LiquidAIEnergy(9, "Ember", new ResourceLocation(AppliedIntegrations.modid,"textures/fluids/empty.png")); // Ember fluid energy
    public static final LiquidAIEnergy TESLA = new LiquidAIEnergy(10, "TESLA", new ResourceLocation(AppliedIntegrations.modid,"textures/fluids/empty.png")); // Tesla flui energy (From TESLA)

    public void writeToNBT(NBTTagCompound tag) {
        tag.setInteger("#AIEnergy", this.getIndex());
    }

    public static LiquidAIEnergy readFromNBT(NBTTagCompound tag){
        return linkedIndexMap.get(tag.getInteger("#AIEnergy"));
    }
}