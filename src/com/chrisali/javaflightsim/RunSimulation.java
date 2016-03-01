package com.chrisali.javaflightsim;

import java.util.EnumSet;

import com.chrisali.javaflightsim.aircraft.AircraftBuilder;
import com.chrisali.javaflightsim.setup.Options;
import com.chrisali.javaflightsim.utilities.integration.Integrate6DOFEquations;

public class RunSimulation {

	public static void main(String[] args) {
		
		//TODO move instrument panel initialization here

		// TODO gather all initial conditions/controls from trim routine

		// TODO Put settings into own class/GUI
		EnumSet<Options> runOptions = EnumSet.noneOf(Options.class);
		runOptions.add(Options.ANALYSIS_MODE);
//		runOptions.add(Options.TRIM_MODE);
//		runOptions.add(Options.UNLIMITED_FLIGHT);
//		runOptions.add(Options.CONSOLE_DISPLAY);
//		runOptions.add(Options.USE_JOYSTICK);
//		runOptions.add(Options.USE_MOUSE);
		
		// Create simulation thread using default aircraft, and start it
		new Thread(new Integrate6DOFEquations(new AircraftBuilder("LookupNavion"), 			   // Default to Navion with Lycoming IO-360
											  runOptions)).start();
	}
}
