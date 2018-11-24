package AppliedIntegrations.Entities;

import AppliedIntegrations.AppliedIntegrations;


public enum TileEnum
{
    EnergyInterface ("EnergyInterface", TileEnergyInterface.class);

    /**
     * Unique ID of the tile entity
     */
    private String ID;

    /**
     * Tile entity class.
     */
    private Class clazz;

    private TileEnum( final String ID, final Class clazz )
    {
        this.ID = ID;
        this.clazz = clazz;
    }

    /**
     * Gets the tile entity's class.
     */
    public Class getTileClass()
    {
        return this.clazz;
    }

    /**
     * Gets the tile entity's ID.
     *
     * @return
     */
    public String getTileID()
    {
        return AppliedIntegrations.modid + "." + this.ID;
    }
}
