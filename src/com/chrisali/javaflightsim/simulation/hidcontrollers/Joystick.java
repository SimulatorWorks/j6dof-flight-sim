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

import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl;
import com.chrisali.javaflightsim.simulation.interfaces.SimulationController;
import com.chrisali.javaflightsim.simulation.setup.ControlsConfiguration;
import com.chrisali.javaflightsim.simulation.setup.JoystickAxis;
import com.chrisali.javaflightsim.simulation.setup.KeyCommand;

import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Component.Identifier.Axis;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

/**
 * The Joystick object uses JInput to integrate joystick functionality into the simulation.
 * It works by generating an ArrayList of joysticks, gamepads and steering wheels connected
 * to the computer, polling each one's active components (buttons, axes, POV hat), using 
 * the polled data to calculate control deflections, and assigning these to each respective key 
 * in the controls EnumMap. These deflections are limited by the constants defined in the 
 * {@link FlightControl}. Aileron and Elevator trim are handled by the POV hat switch, and all
 * throttles are controlled by the throttle slider.
 * @see AbstractController
 */
public class Joystick extends AbstractController {
	
	private Map<String, Map<String, JoystickAxis>> joystickAxisAssignments;
	
	private Map<String, Map<String, KeyCommand>> joystickButtonAssignments;
	
	private Map<String, Map<Float, KeyCommand>> joystickHatAssignments;
	
	/**
	 *  Constructor for Joystick class creates list of controllers using searchForControllers()
	 * @param flightControls
	 */
	public Joystick(Map<FlightControl, Double> flightControls, SimulationController simController) {
		logger.debug("Setting up joystick...");

		this.flightControls = flightControls;
		this.simController = simController;
		
		controlsConfig = new ControlsConfiguration();
		joystickAxisAssignments = controlsConfig.getJoystickAxisAssignments();
		joystickButtonAssignments = controlsConfig.getJoystickButtonAssignments();
		joystickHatAssignments = controlsConfig.getJoystickHatAssignments();
		
		options = simController.getConfiguration().getSimulationOptions();
		
		searchForControllers();
	}
	
	/**
	 * Search for and add controllers of type Controller.Type.STICK or Controller.Type.GAMEPAD
	 * to controllerList
	 */ 
	@Override
	public void searchForControllers() {
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		controllerList = new ArrayList<>();
		
		for(Controller controller : controllers){
			if (controller.getType() == Controller.Type.STICK || controller.getType() == Controller.Type.GAMEPAD) {
				controllerList.add(controller);
				logger.debug("Found a joystick: " + controller.getName());
			}
		}
		
		if (controllerList.isEmpty()) {
			logger.error("No joysticks found!");
			return;
		}
	}

	/**
	 *  Get button, POV and axis values from joystick(s), and return a Map for updateFlightControls() 
	 *  in {@link AbstractController}
	 *  @return flightControls Map
	 */
	@Override
	public Map<FlightControl, Double> calculateControllerValues() {
		for (Controller controller : controllerList) {
			
			String controllerName = controller.getName();
			
			Map<String, JoystickAxis> axisAssignments = joystickAxisAssignments.get(controllerName);
			Map<String, KeyCommand> buttonAssignments = joystickButtonAssignments.get(controllerName);
			Map<Float, KeyCommand> hatAssignments = joystickHatAssignments.get(controllerName);
			
			// Poll controller for data
			if(!controller.poll()) 
				continue;
			
			for(Component component : controller.getComponents()) {
				Identifier componentIdentifier = component.getIdentifier();
				String componentName = componentIdentifier.getName();
				float pollValue = component.getPollData();

				// Buttons
				if(buttonAssignments != null && componentIdentifier.getName().matches("^[0-9]*$")) { // If the component name contains only numbers, it is a button
					boolean isPressed = component.getPollData() == 1.0f;
									
					KeyCommand command = buttonAssignments.get(componentName);
					
					if(command != null)					
						executeKeyButtonEventForCommand(command, isPressed);					
					
					continue;
				}

				// Hat Switch - Control elevator and aileron trim 
				if(axisAssignments != null && componentIdentifier == Axis.POV) {										
					KeyCommand command = hatAssignments.get(pollValue);
					
					if(command != null)					
						executeKeyButtonEventForCommand(command, true);					
					
					continue;
				}

				// Joystick Axes
				if(hatAssignments != null && component.isAnalog()){
					JoystickAxis axis = axisAssignments.get(componentName);
					
					if(axis != null)					
						executeAxisEventForCommand(axis.getAxisAssignment(), pollValue);				
					
					continue;
				}
			}
		}
		
		return limitControls(flightControls);
	}
}
