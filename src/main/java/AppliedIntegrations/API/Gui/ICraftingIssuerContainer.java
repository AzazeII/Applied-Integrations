package AppliedIntegrations.API.Gui;

import AppliedIntegrations.API.Grid.ICraftingIssuerHost;

import javax.annotation.Nonnull;
/**
 * @Author Azazell
 */
public interface ICraftingIssuerContainer
{
    /**
     * Gets the crafting issuer host terminal.
     *
     * @return
     */
    @Nonnull
    ICraftingIssuerHost getCraftingHost();
}