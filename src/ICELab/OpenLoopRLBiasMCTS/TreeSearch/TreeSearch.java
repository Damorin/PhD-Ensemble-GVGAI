 package ICELab.OpenLoopRLBiasMCTS.TreeSearch;

import ICELab.OpenLoopRLBiasMCTS.Node;

public abstract class TreeSearch {
    public Node origin = null;
    public TreeSearch(Node origin) {
        this.origin = origin;
    }
    public abstract void search();
    public abstract void roll(Node origin);
}
