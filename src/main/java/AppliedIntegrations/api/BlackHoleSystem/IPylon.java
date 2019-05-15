package AppliedIntegrations.api.BlackHoleSystem;

public interface IPylon {
    void setSingularity(ISingularity o);

    void setDrain(boolean b);

    void postCellInventoryEvent();
}
