package AppliedIntegrations.API.Storage;

import AppliedIntegrations.AppliedIntegrations;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

import java.util.LinkedHashMap;
import java.util.function.Consumer;

/**
 * @Author Azazell
 */
public class LiquidAIEnergy extends Fluid {

    private String tag;
    private int index;
    private ResourceLocation image;
    private LiquidAIEnergy lastEnergy;
    private String modid;

    private LiquidAIEnergy(String modid, Integer index,String tag, ResourceLocation image) {
        super(tag, image, image);

        // Check if energy is already registered
        if (energies.containsKey(tag)) throw new IllegalArgumentException(tag + " already registered!");

        // Set tag
        this.tag = tag;

        // Set modid
        this.modid = modid;

        // Set image
        this.image = image;

        // Set index
        this.index = index;

        // Map energy by index
        LiquidAIEnergy.linkedIndexMap.put(index,this);

        // Map energy by tag
        LiquidAIEnergy.energies.put(tag, this);
    }

    public void onChange(Consumer<LiquidAIEnergy> action){
        // Check if last recorded energy not synced
        if(lastEnergy != this) {
            // Accept action
            action.accept(this);

            // Record last energy
            lastEnergy = this;
        }
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

    // #Getter for mod id
    public String getModid() {
        return modid;
    }

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

    public void writeToNBT(NBTTagCompound tag) {
        tag.setInteger("#AIEnergy", this.getIndex());
    }

    public static LiquidAIEnergy readFromNBT(NBTTagCompound tag){
        return linkedIndexMap.get(tag.getInteger("#AIEnergy"));
    }
}