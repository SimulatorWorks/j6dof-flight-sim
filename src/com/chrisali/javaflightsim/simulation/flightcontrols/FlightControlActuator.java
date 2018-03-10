package com.chrisali.javaflightsim.simulation.flightcontrols;

import static com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl.AILERON;
import static com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl.BRAKE_L;
import static com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl.BRAKE_R;
import static com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl.ELEVATOR;
import static com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl.FLAPS;
import static com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl.GEAR;
import static com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl.MIXTURE_1;
import static com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl.MIXTURE_2;
import static com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl.MIXTURE_3;
import static com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl.MIXTURE_4;
import static com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl.PROPELLER_1;
import static com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl.PROPELLER_2;
import static com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl.PROPELLER_3;
import static com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl.PROPELLER_4;
import static com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl.RUDDER;
import static com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl.THROTTLE_1;
import static com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl.THROTTLE_2;
import static com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl.THROTTLE_3;
import static com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl.THROTTLE_4;
import static com.chrisali.javaflightsim.simulation.setup.KeyCommand.AILERON_LEFT;
import static com.chrisali.javaflightsim.simulation.setup.KeyCommand.AILERON_RIGHT;
import static com.chrisali.javaflightsim.simulation.setup.KeyCommand.AILERON_TRIM_LEFT;
import static com.chrisali.javaflightsim.simulation.setup.KeyCommand.AILERON_TRIM_RIGHT;
import static com.chrisali.javaflightsim.simulation.setup.KeyCommand.BRAKES;
import static com.chrisali.javaflightsim.simulation.setup.KeyCommand.CENTER_CONTROLS;
import static com.chrisali.javaflightsim.simulation.setup.KeyCommand.DECREASE_FLAPS;
import static com.chrisali.javaflightsim.simulation.setup.KeyCommand.DECREASE_MIXTURE;
import static com.chrisali.javaflightsim.simulation.setup.KeyCommand.DECREASE_PROPELLER;
import static com.chrisali.javaflightsim.simulation.setup.KeyCommand.DECREASE_THROTTLE;
import static com.chrisali.javaflightsim.simulation.setup.KeyCommand.ELEVATOR_DOWN;
import static com.chrisali.javaflightsim.simulation.setup.KeyCommand.ELEVATOR_TRIM_DOWN;
import static com.chrisali.javaflightsim.simulation.setup.KeyCommand.ELEVATOR_TRIM_UP;
import static com.chrisali.javaflightsim.simulation.setup.KeyCommand.ELEVATOR_UP;
import static com.chrisali.javaflightsim.simulation.setup.KeyCommand.GEAR_DOWN;
import static com.chrisali.javaflightsim.simulation.setup.KeyCommand.GEAR_UP;
import static com.chrisali.javaflightsim.simulation.setup.KeyCommand.GEAR_UP_DOWN;
import static com.chrisali.javaflightsim.simulation.setup.KeyCommand.INCREASE_FLAPS;
import static com.chrisali.javaflightsim.simulation.setup.KeyCommand.INCREASE_MIXTURE;
import static com.chrisali.javaflightsim.simulation.setup.KeyCommand.INCREASE_PROPELLER;
import static com.chrisali.javaflightsim.simulation.setup.KeyCommand.INCREASE_THROTTLE;
import static com.chrisali.javaflightsim.simulation.setup.KeyCommand.RUDDER_LEFT;
import static com.chrisali.javaflightsim.simulation.setup.KeyCommand.RUDDER_RIGHT;
import static com.chrisali.javaflightsim.simulation.setup.KeyCommand.RUDDER_TRIM_LEFT;
import static com.chrisali.javaflightsim.simulation.setup.KeyCommand.RUDDER_TRIM_RIGHT;

import com.chrisali.javaflightsim.simulation.setup.IntegratorConfig;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;

/**
 * Handles setting of values in the Maps in {@link FlightControlsState} for a given {@link ControlParameter} 
 * 
 * @author Christopher
 *
 */
public class FlightControlActuator implements ControlParameterActuator {
	
	FlightControlsState controlsState;
	
	private double dt = 0.05;
	
	// Add trim values to getDeflection() to emulate trim deflections
	private double trimElevator = 0.0;
	private double trimAileron  = 0.0;
	private double trimRudder   = 0.0;
	private double flaps   	    = 0.0;
	
	// Keep track if button is pressed, so events occur only once if button held down 
	private static boolean gearPressed = false;
	
	public FlightControlActuator(SimulationConfiguration configuration, FlightControlsState controlsState) {
		dt = configuration.getIntegratorConfig().get(IntegratorConfig.DT);
		
		this.controlsState = controlsState;
		
		trimAileron  = controlsState.getTrimFlightControls().get(AILERON);
		trimElevator = controlsState.getTrimFlightControls().get(ELEVATOR);
		trimRudder   = controlsState.getTrimFlightControls().get(RUDDER);
	}
	
	// Need to find a better way of selecting the right method to call
	@Override
	public void handleParameterChange(ControlParameter parameter, float value) {
		if(parameter.isRelative()) {
			if (parameter.equals(AILERON_LEFT)) {
				if (isPressed(value)) aileronLeft();
			} else if (parameter.equals(AILERON_RIGHT)) {
				if (isPressed(value)) aileronRight();
			} else if (parameter.equals(AILERON_TRIM_LEFT)) {
				if (isPressed(value)) aileronTrimLeft();
			} else if (parameter.equals(AILERON_TRIM_RIGHT)) {
				if (isPressed(value)) aileronTrimRight();
			} else if (parameter.equals(BRAKES)) {
				brakeLeft(BRAKE_L.getMaximum());
				brakeRight(BRAKE_R.getMaximum());
			} else if (parameter.equals(CENTER_CONTROLS)) {
				if (isPressed(value)) centerControls();
			} else if (parameter.equals(DECREASE_FLAPS)) {
				if (isPressed(value)) retractFlaps();
			} else if (parameter.equals(DECREASE_MIXTURE)) {
			} else if (parameter.equals(DECREASE_PROPELLER)) {
			} else if (parameter.equals(DECREASE_THROTTLE)) {
				if (isPressed(value)) decreaseThrottle();
			} else if (parameter.equals(ELEVATOR_DOWN)) {
				if (isPressed(value)) elevatorDown();
			} else if (parameter.equals(ELEVATOR_UP)) {
				if (isPressed(value)) elevatorUp();
			} else if (parameter.equals(ELEVATOR_TRIM_DOWN)) {
				if (isPressed(value)) elevatorTrimDown();
			} else if (parameter.equals(ELEVATOR_TRIM_UP)) {
				if (isPressed(value)) elevatorTrimUp();
			} else if (parameter.equals(GEAR_UP_DOWN)) {
				cycleGear(isPressed(value));
			} else if (parameter.equals(GEAR_DOWN)) {
				if (isPressed(value)) extendGear();
			} else if (parameter.equals(GEAR_UP)) {
				if (isPressed(value)) retractGear();
			} else if (parameter.equals(INCREASE_FLAPS)) {
				if (isPressed(value)) extendFlaps();
			} else if (parameter.equals(INCREASE_MIXTURE)) {
			} else if (parameter.equals(INCREASE_PROPELLER)) {
			} else if (parameter.equals(INCREASE_THROTTLE)) {
				if (isPressed(value)) increaseThrottle();
			} else if (parameter.equals(RUDDER_LEFT)) {
				if (isPressed(value)) rudderLeft();
			} else if (parameter.equals(RUDDER_RIGHT)) {
				if (isPressed(value)) rudderRight();
			} else if (parameter.equals(RUDDER_TRIM_LEFT)) {
				if (isPressed(value)) rudderTrimLeft();
			} else if (parameter.equals(RUDDER_TRIM_RIGHT)) {
				if (isPressed(value)) rudderRight();
			}
		}
		else {
			if (parameter.equals(AILERON)) {
				aileron(value);
			} else if (parameter.equals(BRAKE_L)) {
				brakeLeft(value);
			} else if (parameter.equals(BRAKE_R)) {
				brakeRight(value);
			} else if (parameter.equals(ELEVATOR)) {
				elevator(value);
			} else if (parameter.equals(FLAPS)) {
			} else if (parameter.equals(GEAR)) {
			} else if (parameter.equals(MIXTURE_1)) {
				mixture1(value);
			} else if (parameter.equals(MIXTURE_2)) {
				mixture2(value);
			} else if (parameter.equals(MIXTURE_3)) {
			} else if (parameter.equals(MIXTURE_4)) {
			} else if (parameter.equals(PROPELLER_1)) {
				propeller1(value);
			} else if (parameter.equals(PROPELLER_2)) {
				propeller2(value);
			} else if (parameter.equals(PROPELLER_3)) {
			} else if (parameter.equals(PROPELLER_4)) {
			} else if (parameter.equals(RUDDER)) {
				rudder(value);
			} else if (parameter.equals(THROTTLE_1)) {
				throttle1(value);
			} else if (parameter.equals(THROTTLE_2)) {
				throttle2(value);
			} else if (parameter.equals(THROTTLE_3)) {
			} else if (parameter.equals(THROTTLE_4)) {
			}
		}
	}
	
	private boolean isPressed(float value){
		return value == 1.0;
	}
	
	/** 
	 * Cycles Landing Gear Down/Up. Use gearPressed to prevent numerous cycles of gear up/down if key held down;
	 * need to release key to extend or retract gear again
	 * 
	 * @param controls
	 * @param buttonPressed
	 */
	public void cycleGear(boolean buttonPressed) {
		if (!gearPressed && controlsState.get(FlightControl.GEAR) < 0.5) {
			if(buttonPressed) {
				controlsState.set(FlightControl.GEAR, 1.0);
				gearPressed = true;
			}
		} else if (!gearPressed && controlsState.get(FlightControl.GEAR) > 0.5) {
			if(buttonPressed) {
				controlsState.set(FlightControl.GEAR, 0.0);
				gearPressed = true;
			}
		} else if (gearPressed && !buttonPressed) {
			gearPressed = false;
		} 
	}
	
	public void retractGear() {
		controlsState.set(FlightControl.GEAR, FlightControl.GEAR.getMinimum());	
	}

	public void extendGear() {
		controlsState.set(FlightControl.GEAR, FlightControl.GEAR.getMaximum());
	}

	public void retractFlaps() {
		if (flaps >= FlightControl.FLAPS.getMinimum())
			controlsState.set(FlightControl.FLAPS, (flaps -= getRate(FlightControl.FLAPS)));
	}
	
	public void extendFlaps() {
		if (flaps <= FlightControl.FLAPS.getMaximum()) 
			controlsState.set(FlightControl.FLAPS, (flaps += getRate(FlightControl.FLAPS)));
	}
	
	public void aileronTrimLeft() {
		if(trimAileron >= FlightControl.AILERON.getMinimum()) 
			trimAileron += getRate(FlightControl.AILERON)/10;
	}
	
	public void aileronTrimRight() {
		if(trimAileron <= FlightControl.AILERON.getMaximum()) 
			trimAileron -= getRate(FlightControl.AILERON)/10;
	}
	
	public void rudderTrimRight() {
		if(trimRudder >= FlightControl.RUDDER.getMinimum()) 
			trimRudder -= getRate(FlightControl.RUDDER)/10;
	}
	
	public void rudderTrimLeft() {
		if(trimRudder <= FlightControl.RUDDER.getMaximum()) 
			trimRudder += getRate(FlightControl.RUDDER)/10;
	}
	
	public void elevatorTrimDown() {
		if (trimElevator <= FlightControl.ELEVATOR.getMaximum()) 
			trimElevator += getRate(FlightControl.ELEVATOR)/10;
	}
	
	public void elevatorTrimUp() {
		if (trimElevator >= FlightControl.ELEVATOR.getMinimum()) 
			trimElevator -= getRate(FlightControl.ELEVATOR)/10;
	}
	
	public void elevator(double value) {
		double deflection = calculateDeflection(FlightControl.ELEVATOR, negativeSquare(value));	
		controlsState.set(FlightControl.ELEVATOR, (deflection + trimElevator));
	}
	
	public void aileron(double value) {
		double deflection = calculateDeflection(FlightControl.AILERON, negativeSquare(value));
		controlsState.set(FlightControl.AILERON, (deflection + trimAileron));
	}
	
	public void rudder(double value) {
		double deflection = calculateDeflection(FlightControl.RUDDER, negativeSquare(value));
		controlsState.set(FlightControl.RUDDER, (deflection + trimRudder));
	}
	
	public void elevatorDown() {
		if (controlsState.get(FlightControl.ELEVATOR) <= FlightControl.ELEVATOR.getMaximum())
			controlsState.set(FlightControl.ELEVATOR, controlsState.get(FlightControl.ELEVATOR) + getRate(FlightControl.ELEVATOR));
	}
	
	public void elevatorUp() {
		if (controlsState.get(FlightControl.ELEVATOR) >= FlightControl.ELEVATOR.getMinimum())
			controlsState.set(FlightControl.ELEVATOR, controlsState.get(FlightControl.ELEVATOR) - getRate(FlightControl.ELEVATOR));
	}
	
	public void aileronLeft() {
		if (controlsState.get(FlightControl.AILERON) >= FlightControl.AILERON.getMinimum())
			controlsState.set(FlightControl.AILERON, controlsState.get(FlightControl.AILERON) + getRate(FlightControl.AILERON));
	}
	
	public void aileronRight() {
		if (controlsState.get(FlightControl.AILERON) <= FlightControl.AILERON.getMaximum())
			controlsState.set(FlightControl.AILERON, controlsState.get(FlightControl.AILERON) - getRate(FlightControl.AILERON));
	}
	
	public void rudderLeft() {
		if (controlsState.get(FlightControl.RUDDER) >= FlightControl.RUDDER.getMinimum())
			controlsState.set(FlightControl.RUDDER, controlsState.get(FlightControl.RUDDER) - getRate(FlightControl.RUDDER));
	}
	
	public void rudderRight() {
		if (controlsState.get(FlightControl.RUDDER) <= FlightControl.RUDDER.getMaximum())
			controlsState.set(FlightControl.RUDDER, controlsState.get(FlightControl.RUDDER) + getRate(FlightControl.RUDDER));
	}
	
	public void centerControls() {
		controlsState.set(FlightControl.ELEVATOR, trimElevator);
		controlsState.set(FlightControl.AILERON, trimAileron);
		controlsState.set(FlightControl.RUDDER, trimRudder);
	}
	
	public void brakeRight(double value) {
		controlsState.set(FlightControl.BRAKE_R, negativeSquare(value));
	}
	
	public void brakeLeft(double value) {
		controlsState.set(FlightControl.BRAKE_L, negativeSquare(value));
	}
	
	public void throttle1(double value) {
		controlsState.set(FlightControl.THROTTLE_1, -(value-1)/2);
	}
	
	public void throttle2(double value) {
		controlsState.set(FlightControl.THROTTLE_2, -(value-1)/2);
	}
	
	public void propeller1(double value) {
		controlsState.set(FlightControl.PROPELLER_1, -(value-1)/2);
	}
	
	public void propeller2(double value) {
		controlsState.set(FlightControl.PROPELLER_2, -(value-1)/2);
	}
	
	public void mixture1(double value) {
		controlsState.set(FlightControl.MIXTURE_1, -(value-1)/2);
	}
	
	public void mixture2(double value) {
		controlsState.set(FlightControl.MIXTURE_2, -(value-1)/2);
	}
	
	public void increaseThrottle() {
		if (controlsState.get(FlightControl.THROTTLE_1) <= FlightControl.THROTTLE_1.getMaximum() &&
			controlsState.get(FlightControl.THROTTLE_2) <= FlightControl.THROTTLE_2.getMaximum() &&
			controlsState.get(FlightControl.THROTTLE_3) <= FlightControl.THROTTLE_3.getMaximum() &&
			controlsState.get(FlightControl.THROTTLE_4) <= FlightControl.THROTTLE_4.getMaximum()) {
			
			controlsState.set(FlightControl.THROTTLE_1, controlsState.get(FlightControl.THROTTLE_1) + getRate(FlightControl.THROTTLE_1));
			controlsState.set(FlightControl.THROTTLE_2, controlsState.get(FlightControl.THROTTLE_2) + getRate(FlightControl.THROTTLE_2));
			controlsState.set(FlightControl.THROTTLE_3, controlsState.get(FlightControl.THROTTLE_3) + getRate(FlightControl.THROTTLE_3));
			controlsState.set(FlightControl.THROTTLE_4, controlsState.get(FlightControl.THROTTLE_4) + getRate(FlightControl.THROTTLE_4));
		}
	}
	
	public void decreaseThrottle() {
		if (controlsState.get(FlightControl.THROTTLE_1) >= FlightControl.THROTTLE_1.getMinimum() &&
			controlsState.get(FlightControl.THROTTLE_2) >= FlightControl.THROTTLE_2.getMinimum() &&
			controlsState.get(FlightControl.THROTTLE_3) >= FlightControl.THROTTLE_3.getMinimum() &&
			controlsState.get(FlightControl.THROTTLE_4) >= FlightControl.THROTTLE_4.getMinimum()) {
			
			controlsState.set(FlightControl.THROTTLE_1, controlsState.get(FlightControl.THROTTLE_1) - getRate(FlightControl.THROTTLE_1));
			controlsState.set(FlightControl.THROTTLE_2, controlsState.get(FlightControl.THROTTLE_2) - getRate(FlightControl.THROTTLE_2));
			controlsState.set(FlightControl.THROTTLE_3, controlsState.get(FlightControl.THROTTLE_3) - getRate(FlightControl.THROTTLE_3));
			controlsState.set(FlightControl.THROTTLE_4, controlsState.get(FlightControl.THROTTLE_4) - getRate(FlightControl.THROTTLE_4));
		}
	}

	/**
	 * Standardizes rate of control deflection of keyboard and joystick button inputs regardless of the 
	 * simulation update rate based on the {@link FlightControl} argument provided and the 
	 * 
	 * @param type
	 */
	private double getRate(FlightControl type) {
		switch (type) {
		case AILERON:
		case ELEVATOR:
		case RUDDER:
			return 0.12 * dt;
		case THROTTLE_1:
		case THROTTLE_2:
		case THROTTLE_3:
		case THROTTLE_4:
		case PROPELLER_1:
		case PROPELLER_2:
		case PROPELLER_3:
		case PROPELLER_4:
		case MIXTURE_1:
		case MIXTURE_2:
		case MIXTURE_3:
		case MIXTURE_4:
			return 0.5 * dt;
		case FLAPS:
			return 0.15 * dt;
		default:
			return 0;
		}
	}
		
	/**
	 *  Uses maximum and minimum values defined in {@link FlightControl} to convert normalized 
	 *  joystick axis value to actual control deflection 
	 *  
	 * @param controlType
	 * @param value
	 * @return Actual control deflection
	 */
	private double calculateDeflection(FlightControl controlType, double value) {
		// Calculate positive and negative slope
		// (elevator has different values for positive/negative max)
		if (value <= 0) 
			return (controlType.getMaximum()*Math.abs(value));
		else
			return (controlType.getMinimum()*value);
	}
	
	/**
	 * Squares a value without removing its sign if negative
	 * 
	 * @param value
	 * @return value squared that retains its original sign
	 */
	private double negativeSquare(double value) {
		if (value < 0)
			return -(Math.pow(value, 2));
		else
			return Math.pow(value, 2);
	}
}
