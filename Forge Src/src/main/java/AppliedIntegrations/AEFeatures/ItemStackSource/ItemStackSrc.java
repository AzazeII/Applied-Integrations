package AppliedIntegrations.AEFeatures.ItemStackSource;

import com.google.common.base.Preconditions;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ItemStackSrc implements IStackSrc
{

    private final Item item;
    private final int damage;
    private final boolean enabled;

    public ItemStackSrc( final Item item, final int damage, final ActivityState state )
    {
        Preconditions.checkNotNull( item );
        Preconditions.checkArgument( damage >= 0 );
        Preconditions.checkNotNull( state );
        Preconditions.checkArgument( state == ActivityState.Enabled || state == ActivityState.Disabled );

        this.item = item;
        this.damage = damage;
        this.enabled = state == ActivityState.Enabled;
    }

    @Nullable
    @Override
    public ItemStack stack(final int i )
    {
        return new ItemStack( this.item, i, this.damage );
    }

    @Override
    public Item getItem()
    {
        return this.item;
    }

    @Override
    public int getDamage()
    {
        return this.damage;
    }

    @Override
    public boolean isEnabled()
    {
        return this.enabled;
    }
}