package AppliedIntegrations.AEFeatures.ItemStackSource;

public enum ActivityState
{
    Enabled,
    Disabled;

    public static ActivityState from( final boolean enabled )
    {
        if( enabled )
        {
            return ActivityState.Enabled;
        }
        else
        {
            return ActivityState.Disabled;
        }
    }
}
