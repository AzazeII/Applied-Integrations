package AppliedIntegrations.models;

import appeng.api.parts.IPartModel;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class AIPartModel implements IPartModel {
    private List<ResourceLocation> models;

    public AIPartModel(ResourceLocation models) {
        this.models = Arrays.asList(models);
    }

    public AIPartModel(ResourceLocation... models) {
        this.models = Arrays.asList(models);
    }

    @Nonnull
    @Override
    public List<ResourceLocation> getModels() {
        return this.models;
    }
}
