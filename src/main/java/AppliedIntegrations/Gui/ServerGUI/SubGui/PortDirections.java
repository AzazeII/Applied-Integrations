package AppliedIntegrations.Gui.ServerGUI.SubGui;

/**
 * @Author Azazell
 */
public enum PortDirections {
    S(1,0),
    N(-1,0),
    E(1,1),
    W(-1,-1),
    U(-1,1),
    D(1,-1);

    public int offsetX, offsetY;

    PortDirections(int x,int y){
        this.offsetX =x;
        this.offsetY =y;
    }
}
