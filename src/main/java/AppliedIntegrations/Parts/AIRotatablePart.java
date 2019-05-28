package AppliedIntegrations.Parts;


import appeng.api.parts.PartItemStack;
import appeng.util.Platform;
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
public abstract class AIRotatablePart extends AIPart {

	private static final String NBT_KEY_ROT_DIR = "partRotation";

	// Rotation value. Each byte gives +90 degree to rotation
	private byte renderRotation = 0;

	public AIRotatablePart(final PartEnum associatedPart) {

		super(associatedPart);
	}

	@Override
	public void readFromNBT(final NBTTagCompound data) {
		// Call super
		super.readFromNBT(data);

		// Read rotation
		if (data.hasKey(this.NBT_KEY_ROT_DIR)) {
			this.renderRotation = data.getByte(this.NBT_KEY_ROT_DIR);
		}
	}

	@Override
	public void writeToStream(final ByteBuf stream) throws IOException {
		// Call super
		super.writeToStream(stream);

		// Write the rotation
		stream.writeByte(this.renderRotation);
	}

	@Override
	public boolean readFromStream(final ByteBuf stream) throws IOException {

		boolean redraw = false;

		// Call super
		redraw |= super.readFromStream(stream);

		// Read the rotation
		byte streamRot = stream.readByte();

		// Did the rotaion change?
		if (this.renderRotation != streamRot) {
			this.renderRotation = streamRot;
			redraw |= true;
		}

		return redraw;
	}

	@Override
	public boolean onActivate(final EntityPlayer player, EnumHand hand, final Vec3d position) {
		// Get the host tile entity
		TileEntity hte = this.getHostTile();

		// Is the player not sneaking and using a wrench
		if (!player.isSneaking() && Platform.isWrench(player, player.getHeldItem(hand), hte.getPos())) {
			// Call only on server
			if (!getHostWorld().isRemote) {
				// Bounds check the rotation
				if ((this.renderRotation > 3) || (this.renderRotation < 0)) {
					// Move to first rotation value
					this.renderRotation = 0;
				}

				// Switch for current rotation
				switch (this.renderRotation) {
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
				this.markForUpdate(); // Sync
				this.markForSave(); // Save
			}
			return true;
		}

		return super.onActivate(player, hand, position);
	}

	@Override
	public void writeToNBT(final NBTTagCompound data, final PartItemStack saveType) {
		// Call super
		super.writeToNBT(data, saveType);

		// Write the rotation
		if ((saveType == PartItemStack.WORLD) && (this.renderRotation != 0)) {
			data.setByte(this.NBT_KEY_ROT_DIR, this.renderRotation);
		}
	}
}
