package AppliedIntegrations.Parts;

import AppliedIntegrations.Utils.EffectiveSide;
import AppliedIntegrations.Utils.WrenchUtil;
import appeng.api.config.SecurityPermissions;
import appeng.api.parts.PartItemStack;
import io.netty.buffer.ByteBuf;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;

import java.io.IOException;
/**
 * @Author Azazell
 */
public abstract class AIRotatablePart
        extends AIPart
{
    /**
     * NBT keys
     */
    private static final String NBT_KEY_ROT_DIR = "partRotation";

    /**
     * What direction should be rotated to.
     * Valid values are 0,1,2,3.
     */
    protected byte renderRotation = 0;

    public AIRotatablePart(final PartEnum associatedPart, final SecurityPermissions ... interactionPermissions )
    {
        super( associatedPart, interactionPermissions );
    }

    /**
     * Called when the part is right-clicked
     */
    @Override
    public boolean onActivate(final EntityPlayer player, EnumHand hand, final Vec3d position )
    {
        // Get the host tile entity
        TileEntity hte = this.getHostTile();

        // Is the player not sneaking and using a wrench?
        if( !player.isSneaking() && WrenchUtil.canWrench(player.getHeldItem(hand),player,(int)position.x,(int)position.y,(int)position.z) )
        {
            if( EffectiveSide.isServerSide() )
            {
                // Bounds check the rotation
                if( ( this.renderRotation > 3 ) || ( this.renderRotation < 0 ) )
                {
                    this.renderRotation = 0;
                }

                // Rotate
                switch ( this.renderRotation )
                {
                    case 0:
                        this.renderRotation = 1;
                        break;
                    case 1:
                        this.renderRotation = 3;
                        break;
                    case 2:
                        this.renderRotation = 0;
                        break;
                    case 3:
                        this.renderRotation = 2;
                        break;
                }

                // Mark for sync & save
                this.markForUpdate();
                this.markForSave();
            }
            return true;
        }

        return super.onActivate( player, hand, position );
    }

    /**
     * Reads the rotation
     */
    @Override
    public void readFromNBT( final NBTTagCompound data )
    {
        // Call super
        super.readFromNBT( data );

        // Read rotation
        if( data.hasKey( this.NBT_KEY_ROT_DIR ) )
        {
            this.renderRotation = data.getByte( this.NBT_KEY_ROT_DIR );
        }
    }

    /**
     * Reads the rotation from the stream.
     */
    @Override
    public boolean readFromStream( final ByteBuf stream ) throws IOException
    {
        boolean redraw = false;

        // Call super
        redraw |= super.readFromStream( stream );

        // Read the rotation
        byte streamRot = stream.readByte();

        // Did the rotaion change?
        if( this.renderRotation != streamRot )
        {
            this.renderRotation = streamRot;
            redraw |= true;
        }

        return redraw;
    }

    /**
     * Saves the rotation
     */
    @Override
    public void writeToNBT( final NBTTagCompound data, final PartItemStack saveType )
    {
        // Call super
        super.writeToNBT( data, saveType );

        // Write the rotation
        if( ( saveType == PartItemStack.WORLD ) && ( this.renderRotation != 0 ) )
        {
            data.setByte( this.NBT_KEY_ROT_DIR, this.renderRotation );
        }
    }

    /**
     * Writes the rotation to the stream.
     */
    @Override
    public void writeToStream( final ByteBuf stream ) throws IOException
    {
        // Call super
        super.writeToStream( stream );

        // Write the rotation
        stream.writeByte( this.renderRotation );
    }
}
