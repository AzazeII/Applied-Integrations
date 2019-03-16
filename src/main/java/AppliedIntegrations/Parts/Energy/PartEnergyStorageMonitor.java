package AppliedIntegrations.Parts.Energy;

import AppliedIntegrations.API.LiquidAIEnergy;
import AppliedIntegrations.API.Utils;
import AppliedIntegrations.Parts.AIRotatablePart;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Render.TextureManager;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import AppliedIntegrations.Utils.WrenchUtil;
import appeng.api.AEApi;
import appeng.api.implementations.IPowerChannelState;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.storage.IStackWatcher;
import appeng.api.networking.storage.IStackWatcherHost;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartHost;
import appeng.api.parts.IPartRenderHelper;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.api.util.AEColor;
import appeng.client.texture.CableBusTextures;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;

/**
 * @Author Azazell
 */

public class PartEnergyStorageMonitor extends AIRotatablePart implements IStackWatcherHost,IPowerChannelState {

    public static CableBusTextures darkCornerTexture = CableBusTextures.PartConversionMonitor_Dark;
    public static CableBusTextures lightCornerTexture = CableBusTextures.PartConversionMonitor_Bright;
    private LiquidAIEnergy energy = null;
    private long amount = 0L;
    private Object dspList;

    private boolean locked = false;

    private IStackWatcher watcher = null;

    public PartEnergyStorageMonitor() {
        super(PartEnum.EnergyStorageMonitor);
    }

    @Override
    protected AIGridNodeInventory getUpgradeInventory() {
        return null;
    }

    @Override
    public int cableConnectionRenderTo() {
        return 1;
    }

    @Override
    public void getBoxes(IPartCollisionHelper bch) {
        bch.addBox(2, 2, 14, 14, 14, 16);
        bch.addBox(4, 4, 13, 12, 12, 14);
        bch.addBox(5, 5, 12, 11, 11, 13);
    }

    @Override
    public IIcon getBreakingTexture() {
        return null;
    }

    @Override
    public double getIdlePowerUsage() {
        return 0.5D;
    }

    @Override
    public int getLightLevel() {
        return this.isActive() ? 0 : 1;
    }

    @Override
    public NBTTagCompound getWailaTag(NBTTagCompound tag) {
        super.getWailaTag(tag);
        tag.setBoolean("locked", this.locked);
        tag.setLong("amount", this.amount);
        if (this.energy == null)
            tag.setString("Energy", "");
        else
            tag.setString("Energy", this.energy.getTag());
        return tag;
    }

    @Override
    public boolean onActivate(EntityPlayer player, Vec3 pos) {
        if (player == null || player.worldObj == null)
            return false;
        if (player.worldObj.isRemote)
            return false;
        ItemStack s = player.getCurrentEquippedItem();
        if (s == null) {
            if (this.locked)
                return false;
            if (this.energy == null)
                return false;
            if (this.watcher != null)
                this.watcher.remove(AEApi.instance().storage().createFluidStack(new FluidStack(energy, 1)));
            this.energy = null;
            this.amount = 0L;
            IPartHost host = getHost();
            if (host != null)
                host.markForUpdate();
            return true;
        }
        if (WrenchUtil.canWrench(s, player, this.hostTile.xCoord, this.hostTile.yCoord,
                this.hostTile.zCoord)) {
            this.locked = !this.locked;
            WrenchUtil.wrenchUsed(s, player, this.hostTile.xCoord,
                    this.hostTile.zCoord, this.hostTile.yCoord);
            IPartHost host = getHost();
            if (host != null)
                host.markForUpdate();
            if (this.locked)
                player.addChatMessage(new ChatComponentTranslation(
                        "chat.appliedenergistics2.isNowLocked"));
            else
                player.addChatMessage(new ChatComponentTranslation(
                        "chat.appliedenergistics2.isNowUnlocked"));
            return true;
        }
        if (this.locked)
            return false;
        if (Utils.getEnergyFromItemStack(s) != null) {
            if (this.energy != null && this.watcher != null)
                this.watcher.remove(AEApi.instance().storage().createFluidStack(new FluidStack(this.energy, 1)));
            this.energy = Utils.getEnergyFromItemStack(s);
            if (this.watcher != null)
                this.watcher.add(AEApi.instance().storage().createFluidStack(new FluidStack(this.energy, 1)));
            IPartHost host = getHost();
            if (host != null)
                host.markForUpdate();
            return true;
        }
        return false;
    }

    @Override
    public void onStackChange(IItemList arg0, IAEStack arg1, IAEStack arg2,
                              BaseActionSource arg3, StorageChannel arg4) {
        if (this.energy != null) {
            IGridNode n = getGridNode();
            if (n == null)
                return;
            IGrid g = n.getGrid();
            if (g == null)
                return;
            IStorageGrid storage = g.getCache(IStorageGrid.class);
            if (storage == null)
                return;
            IMEMonitor<IAEFluidStack> Energies = super.getEnergyProvidingInventory();
            if (Energies == null)
                return;
            for (IAEFluidStack s : Energies.getStorageList()) {
                if (s.getFluid() == this.energy) {
                    this.amount = s.getStackSize();
                    IPartHost host = getHost();
                    if (host != null)
                        host.markForUpdate();
                    return;
                }
            }
            this.amount = 0L;
            IPartHost host = getHost();
            if (host != null)
                host.markForUpdate();
        }

    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        if (data.hasKey("amount"))
            this.amount = data.getLong("amount");
        if (data.hasKey("Energy")) {
            String id = data.getString("Energy");
            if (id == "")
                this.energy = null;
            else
                this.energy = LiquidAIEnergy.getEnergy(data.getString("Energy"));
        }
        if (data.hasKey("locked"))
            this.locked = data.getBoolean("locked");
    }

    @Override
    public boolean readFromStream(ByteBuf data) throws IOException {
        super.readFromStream(data);
        this.amount = data.readLong();
        int id = data.readInt();
        if (id == -1)
            this.energy = null;
        else
            this.energy = LiquidAIEnergy.getEnergy(id + "");
        this.locked = data.readBoolean();
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(double x, double y, double z,
                              IPartRenderHelper rh, RenderBlocks renderer) {
        if (this.energy == null)
            return;

        if (this.dspList == null)
            this.dspList = GLAllocation.generateDisplayLists(1);

        Tessellator tess = Tessellator.instance;

        if (!isActive())
            return;

        IAEFluidStack ais = AEApi.instance().storage().createFluidStack(new FluidStack(this.energy, 1));
        ais.setStackSize(this.amount);
        if (ais != null) {
            GL11.glPushMatrix();
            GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);

            GL11.glNewList((Integer) this.dspList, GL11.GL_COMPILE_AND_EXECUTE);
            this.renderEnergy(tess, (LiquidAIEnergy) ais.getFluid());
            GL11.glEndList();

            GL11.glPopMatrix();
        }
    }

    @SideOnly(Side.CLIENT)
    private void renderEnergy(final Tessellator tessellator, final LiquidAIEnergy Energy) {
        // Get the Energy color
        Color EnergyColor = new Color(Energy.getColor());

        // Disable lighting
        GL11.glDisable(GL11.GL_LIGHTING);

        // Only draw if the image alpha is greater than the magic number
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.004F);

        // Enable blending
        GL11.glEnable(GL11.GL_BLEND);

        // Specify the blending mode
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Set the color
        GL11.glColor4f(EnergyColor.getRed() / 255.0F, EnergyColor.getGreen() / 255.0F, EnergyColor.getBlue() / 255.0F, 0.9F);
        tessellator.setColorRGBA_F(EnergyColor.getRed() / 255.0F, EnergyColor.getGreen() / 255.0F, EnergyColor.getBlue() / 255.0F, 0.9F);

        // Center the Energy
        GL11.glTranslated(-0.20D, -0.25D, 0.0D);

        // Bind the Energy image
        Minecraft.getMinecraft().renderEngine.bindTexture(Energy.getImage());

        // Add the vertex points
        double size = 0.38D;
        double zDepth = -0.265D;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(0.0D, size, zDepth, 0.0D, 1.0D); // Bottom left
        tessellator.addVertexWithUV(size, size, zDepth, 1.0D, 1.0D); // Bottom right
        tessellator.addVertexWithUV(size, 0.0D, zDepth, 1.0D, 0.0D); // Top right
        tessellator.addVertexWithUV(0.0D, 0.0D, zDepth, 0.0D, 0.0D); // Top left

        // Draw!
        tessellator.draw();

        // Disable blending
        GL11.glDisable(GL11.GL_BLEND);

        // Enable lighting
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderInventory(final IPartRenderHelper helper, final RenderBlocks renderer) {
        Tessellator ts = Tessellator.instance;

        IIcon side = TextureManager.ENERGY_TERMINAL.getTextures()[3];

        helper.setTexture(side);
        helper.setBounds(4.0F, 4.0F, 13.0F, 12.0F, 12.0F, 14.0F);
        helper.renderInventoryBox(renderer);

        helper.setTexture(side, side, side, TextureManager.ENERGY_TERMINAL.getTextures()[4], side, side);
        helper.setBounds(2.0F, 2.0F, 14.0F, 14.0F, 14.0F, 16.0F);
        helper.renderInventoryBox(renderer);

        ts.setBrightness(0xD000D0);

        helper.setInvColor(0xFFFFFF);

        helper.setInvColor(AEColor.Transparent.blackVariant);
        helper.renderInventoryFace(TextureManager.ENERGY_TERMINAL.getTextures()[5], ForgeDirection.SOUTH, renderer);

        helper.renderInventoryFace(TextureManager.ENERGY_TERMINAL.getTextures()[4], ForgeDirection.SOUTH, renderer);

        helper.setBounds(3.0F, 3.0F, 15.0F, 13.0F, 13.0F, 16.0F);


        helper.setBounds(5.0F, 5.0F, 12.0F, 11.0F, 11.0F, 13.0F);
        this.renderInventoryBusLights(helper, renderer);

    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderStatic(final int x, final int y, final int z, final IPartRenderHelper helper, final RenderBlocks renderer) {
        Tessellator tessellator = Tessellator.instance;

        IIcon side = TextureManager.ENERGY_TERMINAL.getTextures()[3];

        // Main block
        helper.setTexture(side, side, side, side, side, side);
        helper.setBounds(2.0F, 2.0F, 14.0F, 14.0F, 14.0F, 16.0F);
        helper.renderBlock(x, y, z, renderer);

        // Light up if active
        if (this.isActive()) {
            Tessellator.instance.setBrightness(super.ACTIVE_FACE_BRIGHTNESS);
        }

        // Dark corners
        tessellator.setColorOpaque_I(this.getHost().getColor().blackVariant);
        helper.renderFace(x, y, z, this.darkCornerTexture.getIcon(), ForgeDirection.SOUTH, renderer);

        // Light corners
        tessellator.setColorOpaque_I(this.getHost().getColor().mediumVariant);
        helper.renderFace(x, y, z, this.lightCornerTexture.getIcon(), ForgeDirection.SOUTH, renderer);

        // Main face
        tessellator.setColorOpaque_I(this.getHost().getColor().whiteVariant);
        helper.renderFace(x, y, z, CableBusTextures.PartConversionMonitor_Bright.getIcon(), ForgeDirection.SOUTH, renderer);

        if (this.energy != null) {
            tessellator.setColorOpaque_I(this.getHost().getColor().mediumVariant);
            helper.renderFace(x, y, z, TextureManager.ENERGY_TERMINAL.getTexture(), ForgeDirection.SOUTH, renderer);
            // Borders
            helper.renderFace(x, y, z, TextureManager.ENERGY_TERMINAL.getTextures()[4], ForgeDirection.SOUTH, renderer);
        }
        // Cable lights
        helper.setBounds(5.0F, 5.0F, 13.0F, 11.0F, 11.0F, 14.0F);
        this.renderStaticBusLights(x, y, z, helper, renderer);

    }


    @Override
    public boolean requireDynamicRender() {
        return true;
    }

    @Override
    public void updateWatcher(IStackWatcher w) {
        this.watcher = w;
        if (this.energy != null)
            w.add(AEApi.instance().storage().createFluidStack(new FluidStack(energy, 1)));
        onStackChange(null, null, null, null, null);
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setLong("amount", this.amount);
        if (this.energy == null)
            data.setString("Energy", "");
        else
            data.setString("Energy", this.energy.getTag());
        data.setBoolean("locked", this.locked);
    }

    @Override
    public void writeToStream(ByteBuf data) throws IOException {
        super.writeToStream(data);
        data.writeLong(this.amount);
        if (this.energy == null)
            data.writeInt(-1);
        else
            data.writeInt(this.energy.getID());
        data.writeBoolean(this.locked);

    }

}