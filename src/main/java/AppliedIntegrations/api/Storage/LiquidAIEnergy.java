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

	// Energies mapped by tag
	public static LinkedHashMap<String, LiquidAIEnergy> energies = new LinkedHashMap<>();

	// Energies mapped by index
	public static LinkedHashMap<Integer, LiquidAIEnergy> linkedIndexMap = new LinkedHashMap<>();

	private String tag;

	private int index;

	private ResourceLocation image;

	private LiquidAIEnergy lastEnergy;

	private String modid;

	static {
		linkedIndexMap.put(0, null);
	}

	public LiquidAIEnergy(String modid, Integer index, String tag, ResourceLocation image) {
		super(tag, image, image);

		// Check if energy is already registered
		if (energies.containsKey(tag)) {
			throw new IllegalArgumentException(tag + " already registered!");
		}

		// Set tag
		this.tag = tag;

		// Set modid
		this.modid = modid;

		// Set image
		this.image = image;

		// Set index
		this.index = index;

		// Map energy by index
		linkedIndexMap.put(index, this);

		// Map energy by tag
		energies.put(tag, this);
	}

	// Get energy from it's tag
	public static LiquidAIEnergy getEnergy(String tag) {
		return energies.get(tag);
	}

	public static LiquidAIEnergy readFromNBT(NBTTagCompound tag) {
		return linkedIndexMap.get(tag.getInteger("#AIEnergy"));
	}

	public void onChange(Consumer<LiquidAIEnergy> action) {
		// Check if last recorded energy not synced
		if (lastEnergy != this) {
			// Accept action
			action.accept(this);

			// Record last energy
			lastEnergy = this;
		}
	}

	public String getEnergyName() {

		return tag;
	}

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

	public void writeToNBT(NBTTagCompound tag) {
		// Put energy in key
		tag.setInteger("#AIEnergy", this.getIndex());
	}

	// #Getter for index
	public int getIndex() {

		return this.index;
	}
}