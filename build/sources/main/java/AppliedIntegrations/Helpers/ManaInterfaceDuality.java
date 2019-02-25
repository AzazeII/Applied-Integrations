package AppliedIntegrations.Helpers;

import AppliedIntegrations.API.Botania.IManaInterface;
import AppliedIntegrations.API.IInterfaceDuality;
import AppliedIntegrations.API.IInterfaceStorageDuality;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.Utils.AILog;
import appeng.api.config.Actionable;
import appeng.api.exceptions.NullNodeConnectionException;
import appeng.api.util.AEPartLocation;

import static appeng.api.config.Actionable.MODULATE;

public class ManaInterfaceDuality implements IInterfaceDuality {

    private IManaInterface owner;
    public ManaInterfaceDuality(IManaInterface manaInterface) {
        owner = manaInterface;
    }

    @Override
    public double getMaxTransfer(AEPartLocation side) {
        return 10; // Only 10 max transfer, as mana is rich material
    }

    @Override
    public LiquidAIEnergy getFilteredEnergy(AEPartLocation side) {
        return null;
    }

    @Override
    public IInterfaceStorageDuality getEnergyStorage(LiquidAIEnergy energy, AEPartLocation side) {
        return null;
    }

    @Override
    public void DoInjectDualityWork(Actionable mode) throws NullNodeConnectionException {
        int ValuedReceive = (int) Math.min(owner.getManaStored(), getMaxTransfer(null));

        AILog.info("ValuedReceive: " + ValuedReceive);
        AILog.info("Diff: " + owner.InjectMana(ValuedReceive, Actionable.SIMULATE));
        if(owner.InjectMana(ValuedReceive, Actionable.SIMULATE) - getMaxTransfer(null) == 0){
            int injectedAmount = owner.InjectMana(ValuedReceive, MODULATE);

            AILog.info("Injected Mana: " + injectedAmount);
            // Remove only amount injected
            owner.modifyManaStorage(-injectedAmount);
        }
    }

    @Override
    public void DoExtractDualityWork(Actionable mode) throws NullNodeConnectionException {
        int ValuedExtract = (int) Math.min(owner.getManaStored(), getMaxTransfer(null));
        if(owner.InjectMana(ValuedExtract, Actionable.SIMULATE) - getMaxTransfer(null) == 0){
            int extractedAmount = owner.ExtractMana(ValuedExtract, MODULATE);

            // Add only amount extracted
            owner.modifyManaStorage(extractedAmount);
        }
    }
}
