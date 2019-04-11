package AppliedIntegrations.Gui.ServerGUI.SubGui;

import static java.lang.Float.NaN;

/**
 * @Author Azazell
 */
public enum PortDirections {
    S(1,0),
    N(-1,0),
    E(1,1),
    W(-1,-1),
    U(-1,1),
    D(1,-1),
    // Stands for "Not a direction"
    NaD(0,0);

    public int offsetX, offsetY;

    PortDirections(int x,int y){
        this.offsetX =x;
        this.offsetY =y;
    }
}
