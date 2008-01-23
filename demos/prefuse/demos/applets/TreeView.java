package prefuse.demos.applets;

import prefuse.util.ui.JPrefuseApplet;


public class TreeView extends JPrefuseApplet {

    @Override
	public void init() {
        this.setContentPane(
            prefuse.demos.TreeView.demo("/chi-ontology.xml.gz", "name"));
    }

} // end of class TreeView
