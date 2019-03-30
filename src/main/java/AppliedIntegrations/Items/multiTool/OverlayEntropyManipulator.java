package AppliedIntegrations.Items.multiTool;

import AppliedIntegrations.AppliedIntegrations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class OverlayEntropyManipulator {
        private int displayTickCount;
        private long lastTick;
        private ResourceLocation texture = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/OverlayMTool.png");
        int zLevel = 0;
        // List of all states listed at right corner of player's screen
        private List<Object> ListOfStates = new ArrayList<Object>();
        public OverlayEntropyManipulator() {
            MinecraftForge.EVENT_BUS.register(this);
        }

        //@SubscribeEvent
        public void renderOverlay(RenderGameOverlayEvent event) {
            Minecraft minecraft = Minecraft.getMinecraft();
            ItemStack item = minecraft.player.getHeldItemMainhand();
            if(item == null)
                return;
            boolean doRender = item.getItem() instanceof AdvancedNetworkTool;
            ScaledResolution scaledresolution = new ScaledResolution(minecraft);
            int j = scaledresolution.getScaledHeight()/32;
            if(doRender){
                AdvancedNetworkTool toolEntropyManipulator = (AdvancedNetworkTool)item.getItem();

                Minecraft.getMinecraft().renderEngine.bindTexture(texture);
                //drawEntropyStates(10,200+toolEntropyManipulator.getMode().index*18,j,j*4+6);


            }


        }

        //@SubscribeEvent
        public void onMouseEvent(MouseEvent event) {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            if(player != null && player.isSneaking()) {
                if(event.getDwheel() < 0) {
                    ItemStack stack = player.getHeldItemMainhand();
                    if (stack != null) {
                        Item item = stack.getItem();
                        if (item instanceof AdvancedNetworkTool) {
                            AdvancedNetworkTool tool = (AdvancedNetworkTool) item;

                            //tool.nextMode(false);
                            event.setCanceled(true);
                        }
                    }
                }else if(event.getDwheel() != 0 || event.getDwheel() < 0){
                    ItemStack stack = player.getHeldItemMainhand();
                    if (stack != null) {
                        Item item = stack.getItem();
                        if (item instanceof AdvancedNetworkTool) {
                            AdvancedNetworkTool tool = (AdvancedNetworkTool) item;

                            //tool.nextMode(true);
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }

    }
