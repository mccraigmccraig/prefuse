/*
 * Created on Sep 11, 2004
 */
package edu.berkeley.guir.prefuse.timeline;

/**
 * @author Jack Li jackli(AT)cs_D0Tcmu_D0Tedu
 */
public class TTypeWrapper {
    private final String typeId;
    private boolean shown;

    public TTypeWrapper(final String typeId, final boolean shown) {
        this.typeId = typeId;
        this.shown = shown;
    }
    
    public String getTypeId() {
        return typeId;
    }

    /**
     * @return Returns the shown.
     */
    public boolean isShown() {
        return shown;
    }
    
    /**
     * @param shown The shown to set.
     */
    public void setShown(boolean shown) {
        this.shown = shown;
    }
    
    /**
     * Capitalize the first letter
     */
    public String toString() {
        return typeId.substring(0, 1).toUpperCase() + typeId.substring(1);
    }
}
