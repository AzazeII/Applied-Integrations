package AppliedIntegrations.api.Storage;

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

    public LiquidAIEnergy(String modid, Integer index, String tag, ResourceLocation image) {
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

    public void writeToNBT(NBTTagCompound tag) {
        tag.setInteger("#AIEnergy", this.getIndex());
    }

    public static LiquidAIEnergy readFromNBT(NBTTagCompound tag){
        return linkedIndexMap.get(tag.getInteger("#AIEnergy"));
    }
}