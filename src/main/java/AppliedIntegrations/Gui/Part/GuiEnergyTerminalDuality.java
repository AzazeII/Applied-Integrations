package AppliedIntegrations.Gui.Part;


/**
 * @Author Azazell
 */
import AppliedIntegrations.API.IEnergySelectorContainer;
import AppliedIntegrations.API.ISyncHost;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.part.ContainerEnergyTerminal;
import AppliedIntegrations.Gui.AIBaseGui;
import AppliedIntegrations.Gui.IEnergySelectorGui;
import AppliedIntegrations.Gui.Widgets.WidgetEnergySelector;
import AppliedIntegrations.Parts.Energy.PartEnergyTerminal;
import AppliedIntegrations.grid.EnergyList;
import appeng.api.config.SortOrder;
import appeng.api.storage.data.IItemList;
import appeng.fluids.client.gui.GuiFluidTerminal;
import com.google.common.collect.Ordering;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static AppliedIntegrations.API.Storage.LiquidAIEnergy.RF;

@SideOnly(Side.CLIENT)
public class GuiEnergyTerminalDuality extends AIBaseGui implements IEnergySelectorGui {
    private ResourceLocation mainTexture = new ResourceLocation(AppliedIntegrations.modid,"textures/gui/energy.terminal.png");

    @Nonnull
    private static ContainerEnergyTerminal LinkedContainer;

    private EntityPlayer player;

    @Nullable
    private LiquidAIEnergy selectedEnergy = null;
    private Long amount = 0L;

    private PartEnergyTerminal part;
    public IItemList<IAEEnergyStack> list = new EnergyList();

    private static final int WIDGETS_PER_ROW = 9;

    private static final int WIDGET_ROWS_PER_PAGE = 4;

    private final List<WidgetEnergySelector> widgetEnergySelectors = new ArrayList<>();

    private SortOrder sortMode = SortOrder.NAME;

    public GuiEnergyTerminalDuality(ContainerEnergyTerminal container,PartEnergyTerminal partEnergyTerminal, EntityPlayer player) {
        super(container);

        this.LinkedContainer = container;

        this.player = player;
        this.part = partEnergyTerminal;

        this.xSize = 195;
        this.ySize = 204;

        // Rows
        for(int y = 0; y < WIDGET_ROWS_PER_PAGE; y++ ) {
            // Columns
            for(int x = 0; x < WIDGETS_PER_ROW; x++ ) {
                // Update widget in array
                this.widgetEnergySelectors.add(new WidgetEnergySelector( this,
                        7 + ( x * 18 ),
                        17 + ( y * 18 )));

            }
        }
    }

    @Nonnull
    @Override
    public IEnergySelectorContainer getContainer() {
        return LinkedContainer;
    }

    @Nullable
    @Override
    public LiquidAIEnergy getSelectedEnergy() {
        return this.selectedEnergy;
    }

    @Override
    public void setSelectedEnergy(@Nullable LiquidAIEnergy energy) {
        this.selectedEnergy = energy;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
        // Full white
        GL11.glColor4f( 1.0F, 1.0F, 1.0F, 1.0F );

        // Set the texture to the gui's texture
        Minecraft.getMinecraft().renderEngine.bindTexture( this.mainTexture );

        // Draw the gui
        this.drawTexturedModalRect( this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize );
    }
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX,int mouseY) {
        // Check not null
        if (this.selectedEnergy != null)
            // Draw energy name
            this.fontRenderer.drawString("Energy: " + this.selectedEnergy.getEnergyName(),
                    45, 101, 0);

        // Check stack size greater than zero
        if (this.amount > 0)
            // Draw energy amount
            this.fontRenderer.drawString("Amount: " + this.amount,
                    45, 91, 0);

        // Iterate for each widget
        // Draw each widget
        widgetEnergySelectors.forEach((WidgetEnergySelector::drawWidget));
    }

    @Override
    public ISyncHost getSyncHost() {
        return part;
    }

    @Override
    public void setSyncHost(ISyncHost host) {
        if(host instanceof PartEnergyTerminal)
            part = (PartEnergyTerminal)host;
    }

    public void updateList(IItemList<IAEEnergyStack> list) {
        // Create comparator for
        Ordering<IAEEnergyStack> sorter = new Ordering<IAEEnergyStack>() {
            @Override
            public int compare(@Nullable IAEEnergyStack left, @Nullable IAEEnergyStack right) {
                // Check both energies not null
                if( left == null || right == null)
                    // Same place in slots
                    return 0;

                //------------ Alphabet Sorting ------------//
                if(sortMode == SortOrder.NAME){
                    // Get left energy name or "null"
                    String leftEnergyName = left.getEnergy() == null ? "null" : left.getEnergy().getEnergyName();

                    // Get right energy name or "null"
                    String rightEnergyName = right.getEnergy() == null ? "null" : right.getEnergy().getEnergyName();

                    // Compare first energy to second by default method of class String
                    return leftEnergyName.compareTo(rightEnergyName);

                //------------ Amount Sorting ------------//
                }else if(sortMode == SortOrder.AMOUNT){
                    // Get left energy amount
                    Long leftAmount = left.getStackSize();

                    // Get right energy amount
                    Long rightAmount = right.getStackSize();

                    // Compare first energy to second by default method of class Long
                    return leftAmount.compareTo(rightAmount);

                //------------ Mod Sorting ------------//
                }else if(sortMode == SortOrder.MOD){
                    // Get mod id of left energy
                    String leftModid = left.getEnergy() == null ? "null" : left.getEnergy().getModid();

                    // Get mod id of right energy
                    String rightModid = right.getEnergy() == null ? "null" : right.getEnergy().getModid();

                    return leftModid.compareTo(rightModid);
                }

                // Random sorting
                return 0;
            }
        };
        // Create sorted list
        List<IAEEnergyStack> sorted = sorter.sortedCopy(list);

        // Iterate for each entry of sorted copy of list
        // Add entry in order of list
        sorted.forEach(this.list::add);

        // Iterate until i = list.size
        for (int i = 0; i < list.size(); i++){
            // Get selector at (i)
            widgetEnergySelectors.get(i).setCurrentEnergy(sorted.get(i).getEnergy());
        }
    }
}
