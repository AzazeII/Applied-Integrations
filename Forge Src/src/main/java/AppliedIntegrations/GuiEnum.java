package AppliedIntegrations;

import AppliedIntegrations.API.Parts.AIPart;
import AppliedIntegrations.Entities.AITile;
import AppliedIntegrations.Entities.TileEnergyInterface;
import AppliedIntegrations.Parts.IO.PartEnergyExport;
import AppliedIntegrations.Parts.IO.PartEnergyImport;
import AppliedIntegrations.Parts.EnergyInterface.PartEnergyInterface;
import AppliedIntegrations.Parts.EnergyStorageBus.PartEnergyStorage;
import AppliedIntegrations.Parts.PartEnergyTerminal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public enum GuiEnum {
    GuiInterfacePart(new PartEnergyInterface(),1),
    GuiInterfaceTile(new TileEnergyInterface(),2),
    GuiStoragePart(new PartEnergyStorage(),3),
    GuiImportPart(new PartEnergyImport(),4),
    GuiExportPart(new PartEnergyExport(),5),
    GuiTerminalPart(new PartEnergyTerminal(),7);
    AIPart part;
    AITile tile;
    boolean isPart;
    int ID;
    GuiEnum(int ID){
        this.part = null;
        this.ID = ID;
    }
    GuiEnum(AIPart _part, int ID){
        this.part = _part;
        this.ID = ID;
        this.isPart = true;
    }
    GuiEnum(AITile _tile, int ID){
        this.tile = _tile;
        this.ID = ID;
        this.isPart = false;
    }
    Object GetServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z,boolean isPart){
     if(isPart){
         return part.getServerGuiElement(player);
     }else{
         return tile.getServerGuiElement(player);
     }
    }
    Object GetClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z,boolean isPart){
        if(isPart){
            return part.getClientGuiElement(player);
        }else{
            return tile.getClientGuiElement(player);
        }
    }
    boolean isPart(){
        return this.isPart;
    }
}
