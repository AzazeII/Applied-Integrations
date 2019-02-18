package AppliedIntegrations.Parts;

import AppliedIntegrations.AppliedIntegrations;
import appeng.api.AEApi;
import appeng.api.parts.IPartModel;
import appeng.api.parts.IPartModels;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;

public enum PartModelEnum implements IPartModel {
    EXPORT_BASE("export_base"),
    EXPORT_HAS_CHANNEL(EXPORT_BASE, "export_has_channel"),
    EXPORT_OFF(EXPORT_BASE, "export_off"),
    EXPORT_ON(EXPORT_BASE, "export_on"),

    IMPORT_BASE("import_base"),
    IMPORT_HAS_CHANNEL(IMPORT_BASE, "import_has_channel"),
    IMPORT_OFF(IMPORT_BASE, "import_off"),
    IMPORT_ON(IMPORT_BASE, "import_on"),

    DISPLAY_BASE("display_base"),
    TERMINAL_OFF(DISPLAY_BASE, "display_status_off", "terminal_off"),
    TERMINAL_ON(DISPLAY_BASE, "display_status_on", "terminal_on"),
    TERMINAL_HAS_CHANNEL(TERMINAL_ON, "display_has_channel"),

    STORAGE_BUS_BASE("storage_base"),
    STORAGE_BUS_OFF(STORAGE_BUS_BASE, "storage_off"),
    STORAGE_BUS_ON(STORAGE_BUS_BASE, "storage_on"),
    STORAGE_BUS_HAS_CHANNEL(STORAGE_BUS_BASE, "storage_has_channel"),

    STORAGE_INTERFACE_BASE("interface_base"),
    STORAGE_INTERFACE_OFF(STORAGE_INTERFACE_BASE, "interface_off"),
    STORAGE_INTERFACE_ON(STORAGE_INTERFACE_BASE, "interface_on"),
    STORAGE_INTERFACE_HAS_CHANNEL(STORAGE_INTERFACE_BASE, "interface_has_channel");

    List<ResourceLocation> locations;

    PartModelEnum(Object... modelNames) {
        ImmutableList.Builder builder = new ImmutableList.Builder();
        for (Object o : modelNames) {
            if (o instanceof IPartModel) {
                builder.addAll(((IPartModel) o).getModels());
            } else {
                builder.add(new ResourceLocation(AppliedIntegrations.modid, "part/" + o.toString()));
            }
        }
        locations = builder.build();
    }

    @Override
    public boolean requireCableConnection() {
        return true;
    }

    @Nonnull
    @Override
    public List<ResourceLocation> getModels() {
        return locations;
    }

    public static void registerModels() {
        IPartModels partModels = AEApi.instance().registries().partModels();
        for (PartModelEnum model : values()) {
            partModels.registerModels(model.getModels());
        }
    }
}
