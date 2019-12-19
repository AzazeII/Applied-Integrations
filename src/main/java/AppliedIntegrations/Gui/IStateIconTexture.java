package AppliedIntegrations.Gui;


import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * @Author Azazell
 */
public interface IStateIconTexture {
	/**
	 * @return Height of the icon.
	 */
	int getHeight();

	/**
	 * @return Texture the icon is in.
	 */
	@Nonnull
	ResourceLocation getTexture();

	/**
	 * @return U coordinate of the icon.
	 */
	int getU();

	/**
	 * @return V coordinate of the icon.
	 */
	int getV();

	/**
	 * @return Width of the icon.
	 */
	int getWidth();
}
