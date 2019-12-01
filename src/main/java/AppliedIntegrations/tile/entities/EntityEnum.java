package AppliedIntegrations.tile.entities;


import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.tile.HoleStorageSystem.render.EntityBlackHoleRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.lang.reflect.InvocationTargetException;

/**
 * @Author Azazell
 */
public enum EntityEnum {
	BlackHole(EntityBlackHole.class, EntityBlackHoleRenderer.class);

	private Class<? extends Entity> clazz;

	private Class<? extends Render> renderClazz;

	EntityEnum(Class<? extends Entity> entity, Class<? extends Render> renderer) {
		this.clazz = entity;
		this.renderClazz = renderer;
	}

	public static void register() {

		int counter = 0;
		for (EntityEnum entityEnum : values()) {
			EntityRegistry.registerModEntity(new ResourceLocation(AppliedIntegrations.modid, entityEnum.name()), entityEnum.clazz, entityEnum.name(), counter, AppliedIntegrations.instance, 0, 0, false);
			counter++;
		}
	}

	public static void registerRenderer() {

		for (EntityEnum entityEnum : values()) {
			RenderingRegistry.registerEntityRenderingHandler(entityEnum.clazz, new IRenderFactory<Entity>() {
				@Override
				public Render<? super Entity> createRenderFor(RenderManager manager) {

					try {
						return entityEnum.renderClazz.getDeclaredConstructor(manager.getClass()).newInstance(manager);
					} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
						e.printStackTrace();
					}
					return null;
				}
			});
		}
	}
}
