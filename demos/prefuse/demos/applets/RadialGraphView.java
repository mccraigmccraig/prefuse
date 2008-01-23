package prefuse.demos.applets;

import prefuse.util.ui.JPrefuseApplet;


public class RadialGraphView extends JPrefuseApplet {

    @Override
	public void init() {
        this.setContentPane(
            prefuse.demos.RadialGraphView.demo("/socialnet.xml", "name"));
    }

} // end of class RadialGraphView
