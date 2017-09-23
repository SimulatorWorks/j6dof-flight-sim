/*******************************************************************************
 * Copyright (C) 2016-2017 Christopher Ali
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *  If you have any questions about this project, you can visit
 *  the project's GitHub repository at: http://github.com/chris-ali/j6dof-flight-sim/
 ******************************************************************************/
package com.chrisali.javaflightsim.simulation.hidcontrollers;

import java.util.ArrayList;
import java.util.Map;

import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControlType;

import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Component.Identifier.Axis;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

/**
 * The Mouse object uses JInput to integrate mouse functionality into the simulation as a joystick substitute.
 * It works by generating an ArrayList of mice connected to the computer, polling each one's active components 
 * (buttons, axes), using the polled data to calculate control deflections, and assigning these to each respective key 
 * in the controls EnumMap. These deflections are limited by the constants defined in {@link FlightControlType}. Ailerons 
 * and Elevator are controlled by horizontal and vertical mouse movement, respectively, and all throttles are controlled 
 * by the mouse wheel.
 * @see AbstractController
 */
public class Mouse extends AbstractController {
	
	// Since mouse axes are measured relative to the stopped position, these fields store the control deflection, 
	// and the mouse axis value is added to these
	private double tempElev  = 0.0;
	private double tempAil   = 0.0;
	private double tempThrot = 0.0;

	/**
	 *  Constructor for Joystick class; creates list of controllers using searchForControllers()
	 * @param controls
	 */
	public Mouse(Map<FlightControlType, Double> controls) {
		this.controllerList = new ArrayList<>();

		// Get initial trim values from initial values in controls EnumMap (rad)
		trimElevator = controls.get(FlightControlType.ELEVATOR);
		trimAileron = controls.get(FlightControlType.AILERON);
		trimRudder = controls.get(FlightControlType.RUDDER);
		
		logger.debug("Setting up mouse...");
		
		searchForControllers();
	}
	
	/**
	 * Search for and add controllers of type Controller.Type.KEYBOARD to controllerList
	 */
	@Override
	public void searchForControllers() {
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();

		for (Controller controller : controllers) {
			if (controller.getType() == Controller.Type.MOUSE)
				controllerList.add(controller);
		}

		// If no mice available, exit function
		if (controllerList.isEmpty()) {
			logger.error("No mice found!");
			return;
		}
	}
	
	/**
	 *  Get button, mouse wheel and axis values from mouse, and return a Map for updateFlightControls()
	 *  in {@link AbstractController}
	 *  
	 *  @return controls Map
	 */
	@Override
	protected Map<FlightControlType, Double> calculateControllerValues(Map<FlightControlType, Double> controls) {
		// Iterate through all controllers connected
		for (Controller controller : controllerList) {
			
			// Poll controller for data; if disconnected, break out of componentIdentification loop
			if(!controller.poll()) 
				break;
			
			// Iterate through all components of the controller.
			for(Component component : controller.getComponents()) {
				Identifier componentIdentifier = component.getIdentifier();

				// Buttons
				if(componentIdentifier.getName().matches("^[0-9]*$")) { // If the component identifier contains only numbers, it is a button
					if(component.getPollData() == 1.0f) {
						// Button index (nothing implemented yet)
					}
					continue; // Go to next component
				}

				// Mouse Axes - Read raw mouse relative value, add relative value to temp* variable, and add trim value
				// to control deflection
				if(component.isRelative()){
					double axisValue = (double)component.getPollData()/10000;
					
					// Y axis (Elevator)
					if(componentIdentifier == Axis.Y) {
						if(axisValue != 0) {
							tempElev += axisValue;
							controls.put(FlightControlType.ELEVATOR, -(tempElev+trimElevator));
						}
						continue; // Go to next component
					}
					// X axis (Aileron)
					if(componentIdentifier == Axis.X) {
						if(axisValue != 0) {
							tempAil += axisValue;
							controls.put(FlightControlType.AILERON, -(tempAil+trimAileron));
						}
						continue; // Go to next component
					}
					// Z axis (Throttle)
					if(componentIdentifier == Axis.Z) {
						if(axisValue != 0) {
							tempThrot += axisValue;
							controls.put(FlightControlType.THROTTLE_1, tempThrot*250);
							controls.put(FlightControlType.THROTTLE_2, tempThrot*250);
						}
						continue; // Go to next component
					}
				}
			}
		}
		
		return controls;
	}

}
