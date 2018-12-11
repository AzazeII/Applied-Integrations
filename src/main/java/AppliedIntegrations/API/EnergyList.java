package AppliedIntegrations.API;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.io.Serializable;
import java.util.LinkedHashMap;
/**
 * @Author Azazell
 */
public class EnergyList implements Serializable {

    public LinkedHashMap<LiquidAIEnergy,Integer> energies = new LinkedHashMap<LiquidAIEnergy,Integer>();//energies associated with this object



    public EnergyList(ItemStack stack) {
        try {

        } catch (Exception e) {}
    }

    public EnergyList() {
    }

    public EnergyList copy() {
        EnergyList out = new EnergyList();
        for (LiquidAIEnergy a:this.getEnergies())
            out.add(a, this.getAmount(a));
        return out;
    }

    /**
     * @return the amount of different energies in this collection
     */
    public int size() {
        return energies.size();
    }

    /**
     * @return the amount of total energy in this collection
     */
    public int Size() {
        int q = 0;

        for (LiquidAIEnergy as: energies.keySet()) {
            q+=this.getAmount(as);
        }

        return q;
    }

    /**
     * @return an array of all the energies in this collection
     */
    public LiquidAIEnergy[] getEnergies() {
        LiquidAIEnergy[] q = new LiquidAIEnergy[1];
        return energies.keySet().toArray(q);
    }

    /**
     * @return an array of all the energies in this collection
     */
    public LiquidAIEnergy[] getPrimalEnergies() {
        EnergyList t = new EnergyList();
        for (LiquidAIEnergy as: energies.keySet()) {

        }
        LiquidAIEnergy[] q = new LiquidAIEnergy[1];
        return t.energies.keySet().toArray(q);
    }

    /**
     * @return an array of all the energies in this collection sorted by name
     */
    public LiquidAIEnergy[] getEnergiesSorted() {
        try {
            LiquidAIEnergy[] out = energies.keySet().toArray(new LiquidAIEnergy[]{});
            boolean change=false;
            do {
                change=false;
                for(int a=0;a<out.length-1;a++) {
                    LiquidAIEnergy e1 = out[a];
                    LiquidAIEnergy e2 = out[a+1];
                    if (e1!=null && e2!=null && e1.getTag().compareTo(e2.getTag())>0) {
                        out[a] = e2;
                        out[a+1] = e1;
                        change = true;
                        break;
                    }
                }
            } while (change==true);
            return out;
        } catch (Exception e) {
            return this.getEnergies();
        }
    }

    /**
     * @return an array of all the energies in this collection sorted by amount
     */
    public LiquidAIEnergy[] getEnergiesSortedAmount() {
        try {
            LiquidAIEnergy[] out = energies.keySet().toArray(new LiquidAIEnergy[1]);
            boolean change=false;
            do {
                change=false;
                for(int a=0;a<out.length-1;a++) {
                    int e1 = getAmount(out[a]);
                    int e2 = getAmount(out[a+1]);
                    if (e1>0 && e2>0 && e2>e1) {
                        LiquidAIEnergy ea = out[a];
                        LiquidAIEnergy eb = out[a+1];
                        out[a] = eb;
                        out[a+1] = ea;
                        change = true;
                        break;
                    }
                }
            } while (change==true);
            return out;
        } catch (Exception e) {
            return this.getEnergies();
        }
    }

    /**
     * @param key
     * @return the amount associated with the given energy in this collection
     */
    public int getAmount(LiquidAIEnergy key) {
        return  energies.get(key)==null?0: energies.get(key);
    }

    /**
     * Reduces the amount of an energy in this collection by the given amount.
     * @param key
     * @param amount
     * @return
     */
    public boolean reduce(LiquidAIEnergy key, int amount) {
        if (getAmount(key)>=amount) {
            int am = getAmount(key)-amount;
            energies.put(key, am);
            return true;
        }
        return false;
    }

    /**
     * Reduces the amount of an energy in this collection by the given amount.
     * If reduced to 0 or less the Energy will be removed completely.
     * @param key
     * @param amount
     * @return
     */
    public EnergyList remove(LiquidAIEnergy key, int amount) {
        int am = getAmount(key)-amount;
        if (am<=0) energies.remove(key); else
            this.energies.put(key, am);
        return this;
    }


    public EnergyList remove(LiquidAIEnergy key) {
        energies.remove(key);
        return this;
    }

    /**
     * Adds this energy and amount to the collection.
     * If the Energy exists then its value will be increased by the given amount.
     * @param energy
     * @param amount
     * @return
     */
    public EnergyList add(LiquidAIEnergy energy, int amount) {
        if (this.energies.containsKey(energy)) {
            int oldamount = this.energies.get(energy);
            amount+=oldamount;
        }
        this.energies.put( energy, amount );
        return this;
    }


    /**
     * Adds this energy and amount to the collection.
     * If the Energy exists then only the highest of the old or new amount will be used.
     * @param energy
     * @param amount
     * @return
     */
    public EnergyList merge(LiquidAIEnergy energy, int amount) {
        if (this.energies.containsKey(energy)) {
            int oldamount = this.energies.get(energy);
            if (amount<oldamount) amount=oldamount;

        }
        this.energies.put( energy, amount );
        return this;
    }

    public EnergyList add(EnergyList in) {
        for (LiquidAIEnergy a:in.getEnergies())
            this.add(a, in.getAmount(a));
        return this;
    }

    public EnergyList merge(EnergyList in) {
        for (LiquidAIEnergy a:in.getEnergies())
            this.merge(a, in.getAmount(a));
        return this;
    }

    /**
     * Reads the list of energies from nbt
     * @param nbttagcompound
     * @return
     */
    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        energies.clear();
        NBTTagList tlist = nbttagcompound.getTagList("Energies",(byte)10);
        for (int j = 0; j < tlist.tagCount(); j++) {
            NBTTagCompound rs = (NBTTagCompound) tlist.getCompoundTagAt(j);
            if (rs.hasKey("key")) {
                add(	LiquidAIEnergy.getEnergy(rs.getString("key")),
                        rs.getInteger("amount"));
            }
        }
    }

    public void readFromNBT(NBTTagCompound nbttagcompound, String label)
    {
        energies.clear();
        NBTTagList tlist = nbttagcompound.getTagList(label,(byte)10);
        for (int j = 0; j < tlist.tagCount(); j++) {
            NBTTagCompound rs = (NBTTagCompound) tlist.getCompoundTagAt(j);
            if (rs.hasKey("key")) {
                add(	LiquidAIEnergy.getEnergy(rs.getString("key")),
                        rs.getInteger("amount"));
            }
        }
    }

    /**
     * Writes the list of energies to nbt
     * @param nbttagcompound
     * @return
     */
    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        NBTTagList tlist = new NBTTagList();
        nbttagcompound.setTag("Energies", tlist);
        for (LiquidAIEnergy Energy : getEnergies())
            if (Energy != null) {
                NBTTagCompound f = new NBTTagCompound();
                f.setString("key", Energy.getTag());
                f.setInteger("amount", getAmount(Energy));
                tlist.appendTag(f);
            }
    }

    public void writeToNBT(NBTTagCompound nbttagcompound, String label)
    {
        NBTTagList tlist = new NBTTagList();
        nbttagcompound.setTag(label, tlist);
        for (LiquidAIEnergy Energy : getEnergies())
            if (Energy != null) {
                NBTTagCompound f = new NBTTagCompound();
                f.setString("key", Energy.getTag());
                f.setInteger("amount", getAmount(Energy));
                tlist.appendTag(f);
            }
    }

}
