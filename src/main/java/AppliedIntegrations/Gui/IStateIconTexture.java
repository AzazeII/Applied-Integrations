package AppliedIntegrations.Gui;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * @Author Azazell
 */
public interface IStateIconTexture {
	/**
	 * Height of the icon.
	 *
	 * @return
	 */
	int getHeight();

	/**
	 * Texture the icon is in.
	 *
	 * @return
	 */
	@Nonnull
	ResourceLocation getTexture();

	/**
	 * U coordinate of the icon.
	 *
	 * @return
	 */
	int getU();

	/**
	 * V coordinate of the icon.
	 *
	 * @return
	 */
	int getV();

	/**
	 * Width of the icon.
	 *
	 * @return
	 */
	int getWidth();
}
