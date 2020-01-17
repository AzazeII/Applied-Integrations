package AppliedIntegrations.api.Storage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

import java.util.LinkedHashMap;

/**
 * @Author Azazell
 */
public class LiquidAIEnergy extends Fluid {
	public static LinkedHashMap<String, LiquidAIEnergy> energies = new LinkedHashMap<>();
	public static LinkedHashMap<Integer, LiquidAIEnergy> linkedIndexMap = new LinkedHashMap<>();

	private String tag;
	private int index;
	private ResourceLocation image;
	private String modid;

	static {
		linkedIndexMap.put(0, null);
	}

	public LiquidAIEnergy(String modid, Integer index, String tag, ResourceLocation image) {
		super(tag, image, image);
		if (energies.containsKey(tag)) {
			throw new IllegalArgumentException(tag + " already registered!");
		}

		this.tag = tag;
		this.modid = modid;
		this.image = image;
		this.index = index;

		linkedIndexMap.put(index, this);
		energies.put(tag, this);
	}

	public static LiquidAIEnergy getEnergy(String tag) {
		return energies.get(tag);
	}

	public static LiquidAIEnergy readFromNBT(NBTTagCompound tag) {
		return linkedIndexMap.get(tag.getInteger("#AIEnergy"));
	}

	public String getEnergyName() {
		return tag;
	}

	public String getModid() {
		return modid;
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

	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("#AIEnergy", this.getIndex());
	}

	public int getIndex() {
		return this.index;
	}
}