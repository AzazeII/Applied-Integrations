package AppliedIntegrations.Items.multiTool;

import java.util.LinkedHashMap;

public enum AdvanncedToolModes {
    entropyManipulator(0),
    networkTool(1),
    colorApplicator(2),
    memoryCard(3);

    public int index;

    AdvanncedToolModes(int i){
        index=i;
    }
    public AdvanncedToolModes getNext(AdvanncedToolModes current, boolean reverse){
        if(reverse) {
            return values()[current.index-1];
        }else {
            return values()[current.index+1];
        }
    }
}
