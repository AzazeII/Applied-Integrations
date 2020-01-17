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
	protected byte renderRotation = 0;

	public AIRotatablePart(final PartEnum associatedPart) {

		super(associatedPart);
	}

	@Override
	public void readFromNBT(final NBTTagCompound data) {
		super.readFromNBT(data);

		if (data.hasKey(NBT_KEY_ROT_DIR)) {
			this.renderRotation = data.getByte(NBT_KEY_ROT_DIR);
		}
	}

	@Override
	public void writeToStream(final ByteBuf stream) throws IOException {
		super.writeToStream(stream);
		stream.writeByte(this.renderRotation);
	}

	@Override
	public boolean readFromStream(final ByteBuf stream) throws IOException {
		boolean redraw;

		redraw = super.readFromStream(stream);
		byte streamRot = stream.readByte();

		if (this.renderRotation != streamRot) {
			this.renderRotation = streamRot;
			redraw = true;
		}

		return redraw;
	}

	@Override
	public boolean onActivate(final EntityPlayer player, EnumHand hand, final Vec3d position) {
		TileEntity hte = this.getHostTile();

		if (!player.isSneaking() && Platform.isWrench(player, player.getHeldItem(hand), hte.getPos())) {
			if (Platform.isServer()) {
				if ((this.renderRotation > 3) || (this.renderRotation < 0)) {
					this.renderRotation = 0;
				}

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

				this.markForUpdate();
				this.markForSave();
			}
			return true;
		}

		return super.onActivate(player, hand, position);
	}

	@Override
	public void writeToNBT(final NBTTagCompound data, final PartItemStack saveType) {
		super.writeToNBT(data, saveType);
		if ((saveType == PartItemStack.WORLD) && (this.renderRotation != 0)) {
			data.setByte(NBT_KEY_ROT_DIR, this.renderRotation);
		}
	}
}
