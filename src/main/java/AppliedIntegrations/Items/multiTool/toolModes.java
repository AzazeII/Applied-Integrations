package AppliedIntegrations.Items.multiTool;

import java.util.LinkedHashMap;

public enum toolModes {
    entropyManipulator(0),
    networkTool(1),
    colorApplicator(2),
    memoryCard(3);

    LinkedHashMap<Integer,toolModes> modeMap = new LinkedHashMap<Integer, toolModes>();
    int index;

    toolModes(int i){
     modeMap.put(i,this);
     index=i;
    }
    public toolModes getNext(toolModes current, boolean reverse){
        if(reverse) {
            if (current == entropyManipulator) {
                return memoryCard;
            } else if (current == networkTool) {
                return entropyManipulator;
            } else if (current == colorApplicator) {
                return networkTool;
            } else if (current == memoryCard) {
                return colorApplicator;
            }
        }else{
            if (current == entropyManipulator) {
                return networkTool;
            } else if (current == networkTool) {
                return colorApplicator;
            } else if (current == colorApplicator) {
                return memoryCard;
            } else if (current == memoryCard) {
                return entropyManipulator;
            }
        }
      return null;
    }
}
