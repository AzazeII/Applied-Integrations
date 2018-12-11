package AppliedIntegrations.API;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
/**
 * @Author Azazell
 */
public class EnergyStack
		implements IEnergyStack
{
	private static final String NBTKEY_Energy_TAG = "energyTag", NBTKEY_Energy_AMOUNT = "Amount";

	/**
	 * The Energy this stack contains.
	 */
	@Nullable
	protected LiquidAIEnergy energy;

	/**
	 * The amount this stack contains
	 */
	protected long stackSize;


	/**
	 * Creates an empty stack
	 */
	public EnergyStack()
	{
		this( null, 0, false );
	}

	/**
	 * Creates a stack using the specified Energy and amount.
	 * Defaults to not craftable.
	 *

	 * What Energy this stack will have.
	 * @param amount
	 * How much this stack will have.
	 */
	public EnergyStack(final LiquidAIEnergy energy, final long amount )
	{
		this( energy, amount, false );
	}

	/**
	 * Creates a stack using the specified Energy and amount, and
	 * sets if it is craftable.
	 *

	 * What Energy this stack will have.
	 * @param size
	 * How much this stack will have.
	 * @param craftable
	 * Is the stack craftable.
	 */
	public EnergyStack(final LiquidAIEnergy energy, final long size, final boolean craftable )
	{
		this.setAll( energy, size );
	}

	/**
	 * Creates a new stack from the passed stack.
	 * If a new stack is not needed you can also use copyFrom().
	 *

	 */
	public EnergyStack( final EnergyStack stack )
	{
		this.setAll( stack );
	}

	/**
	 * Creates an Energy stack from a NBT compound tag.
	 *
	 * @param data
	 * Tag to load from
	 * @return Created stack, or null.
	 */
	public static EnergyStack loadEnergyStackFromNBT( final NBTTagCompound data )
	{
		LiquidAIEnergy energy = null;

		// Does the tag have the tag?
		if( data.hasKey( EnergyStack.NBTKEY_Energy_TAG ) )
		{
			// Attempt to get the Energy
			energy = LiquidAIEnergy.energies.get( data.getString( EnergyStack.NBTKEY_Energy_TAG ) );
		}

		// Is there an Energy?
		if( energy == null )
		{
			return null;
		}

		// Load the amount
		long amount = 0;
		if( data.hasKey( EnergyStack.NBTKEY_Energy_AMOUNT ) )
		{
			amount = data.getLong( EnergyStack.NBTKEY_Energy_AMOUNT );
		}

		// Return a newly created stack.
		return new EnergyStack( energy, amount );
	}

	/**
	 * Creates an Energy stack from the stream.
	 *
	 * @param stream
	 * @return
	 */
	public static EnergyStack loadEnergyStackFromStream( final ByteBuf stream )
	{
		// Create the stack
		EnergyStack stack = new EnergyStack();

		// Read in the values
		stack.readFromStream( stream );

		// Return the stack
		return stack;
	}

	@Override
	public long adjustStackSize( final long delta )
	{
		this.stackSize += delta;
		return this.stackSize;
	}

	@Override
	public IEnergyStack copy()
	{
		return new EnergyStack( this );
	}

	@Override
	public LiquidAIEnergy getEnergy()
	{
		return this.energy;
	}

	@Override
	public String getEnergyName()
	{
		// Ensure there is an Energy
		if( this.energy == null )
		{
			return "";
		}

		// Return the name of the Energy
		return this.energy.getName();
	}

	@Override
	public String getEnergyName( final EntityPlayer player )
	{

		return this.getEnergyName();
	}

	@Nonnull
	@Override
	public String getChatColor() {
		return null;
	}


	@Override
	public long getStackSize()
	{
		return this.stackSize;
	}

	@Override
	public boolean hasEnergy()
	{
		return( this.energy != null );
	}

	@Override
	public boolean isEmpty()
	{
		return( this.stackSize <= 0 );
	}

	@Override
	public void readFromStream( final ByteBuf stream )
	{
		// Read the Energy

		// Read the amount
		this.stackSize = stream.readLong();

	}

	@Override
	public void setAll(final LiquidAIEnergy Energy, final long size )
	{
		this.energy = Energy;
		this.stackSize = size;
	}

	@Override
	public void setAll( final IEnergyStack stack )
	{
		if( stack != null )
		{
			this.energy = stack.getEnergy();
			this.stackSize = stack.getStackSize();
		}
		else
		{
			this.energy = null;
			this.stackSize = 0;
		}
	}

	@Override
	public void setEnergy(final LiquidAIEnergy energy )
	{
		this.energy = energy;
	}

	@Override
	public void setStackSize( final long size )
	{
		this.stackSize = size;
	}

	@Override
	public NBTTagCompound writeToNBT( final NBTTagCompound data )
	{
		// Is there an Energy?
		if( this.energy != null )
		{
			// Write the tag
			data.setString( this.NBTKEY_Energy_TAG, this.energy.getTag() );

			// Write the amount
			if( this.stackSize > 0 )
			{
				data.setLong( this.NBTKEY_Energy_AMOUNT, this.stackSize );
			}


		}

		return data;
	}

	@Override
	public void writeToStream( final ByteBuf stream )
	{


		// Write the stored amount
		stream.writeLong( this.stackSize );
	}
}