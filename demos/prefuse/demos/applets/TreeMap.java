package prefuse.demos.applets;

import prefuse.util.ui.JPrefuseApplet;


public class TreeMap extends JPrefuseApplet {

    @Override
	public void init() {
        this.setContentPane(
            prefuse.demos.TreeMap.demo("/chi-ontology.xml.gz", "name"));
    }

} // end of class TreeMap
