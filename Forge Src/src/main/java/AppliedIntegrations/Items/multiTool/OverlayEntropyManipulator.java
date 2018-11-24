package AppliedIntegrations.Items.multiTool;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Items.ItemEnum;
import AppliedIntegrations.Items.multiTool.toolChaosManipulator;
import appeng.items.tools.powered.ToolEntropyManipulator;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;

public class OverlayEntropyManipulator {
        private int displayTickCount;
        private long lastTick;
        private ResourceLocation texture = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/OverlayMTool.png");
        private ResourceLocation selection = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/OverlaySelection.png");
        int zLevel = 0;
        // List of all states listed at right corner of player's screen
        private List<Object> ListOfStates = new ArrayList<Object>();
        public OverlayEntropyManipulator() {
            MinecraftForge.EVENT_BUS.register(this);
        }

        @SubscribeEvent
        public void renderOverlay(RenderGameOverlayEvent event) {
            Minecraft minecraft = Minecraft.getMinecraft();
            ItemStack item = minecraft.thePlayer.getCurrentEquippedItem();
            if(item == null)
                return;
            boolean doRender = item.getItem() instanceof toolChaosManipulator;
            ScaledResolution scaledresolution = new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight);
            int j = scaledresolution.getScaledHeight()/32;
            if(doRender){
                toolChaosManipulator toolEntropyManipulator = (toolChaosManipulator)item.getItem();

                Minecraft.getMinecraft().renderEngine.bindTexture(texture);
                drawEntropyStates(10,200+toolEntropyManipulator.getMode().index*18,j,j*4+6);

                Minecraft.getMinecraft().renderEngine.bindTexture(selection);
                drawSelection(10,200,j,j*4+6);
            }


        }
        @SubscribeEvent
        public void onMouseEvent(MouseEvent event) {
            EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
            if(player != null && player.isSneaking()) {
                if(event.dwheel < 0) {
                    ItemStack stack = player.getCurrentEquippedItem();
                    if (stack != null) {
                        Item item = stack.getItem();
                        if (item instanceof toolChaosManipulator) {
                            toolChaosManipulator tool = (toolChaosManipulator) item;

                            tool.nextMode(false);
                            event.setCanceled(true);
                        }
                    }
                }else if(event.dwheel != 0 || event.dwheel < 0){
                    ItemStack stack = player.getCurrentEquippedItem();
                    if (stack != null) {
                        Item item = stack.getItem();
                        if (item instanceof toolChaosManipulator) {
                            toolChaosManipulator tool = (toolChaosManipulator) item;

                            tool.nextMode(true);
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
        private void drawEntropyStates(float x, float y, int w, int h){
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();

            tessellator.addVertexWithUV(x, y + h, this.zLevel,2,1);
            tessellator.addVertexWithUV(x + w, y + h, this.zLevel,3,1);
            tessellator.addVertexWithUV(x + w, y,zLevel,3,0);
            tessellator.addVertexWithUV(x, y, this.zLevel,2,0);

            tessellator.draw();
        }
        private void drawSelection(float x, float y, int w, int h){
                Tessellator tessellator = Tessellator.instance;
                tessellator.startDrawingQuads();

                tessellator.addVertex(x,y+h,this.zLevel);
                tessellator.addVertex(x+w,y+h,this.zLevel);
                tessellator.addVertex(x+w,y,zLevel);
                tessellator.addVertex(x,y,zLevel);

                tessellator.draw();
        }
            private toolChaosManipulator itemEquipped() {
                EntityPlayer player = Minecraft.getMinecraft().thePlayer;
                ItemStack equipped = player.getCurrentEquippedItem();
                if (equipped != null && equipped.getItem() == ItemEnum.CHAOSMANIPULATOR.getItem()) {
                    return (toolChaosManipulator)equipped.getItem();
                }
                return null;
            }

    }
