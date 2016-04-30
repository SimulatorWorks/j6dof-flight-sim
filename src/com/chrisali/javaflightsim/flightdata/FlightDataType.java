package com.chrisali.javaflightsim.flightdata;

/**
 *	Used by {@link FlightData} as the key to its flightData EnumMap
 */
public enum FlightDataType {
	PITCH        ("Pitch Angle", "deg"),
	ROLL		 ("Roll Angle", "deg"),
	IAS			 ("Indicated Airspeed", "kts"),
	VERT_SPEED	 ("Vertical Speed", "ft/min"),
	HEADING		 ("Heading Angle", "deg"),
	TURN_RATE	 ("Turn Rate", "deg/sec"),
	TURN_COORD	 ("Lateral Acceleration", "g"),
	ALTITUDE	 ("Altitude", "ft"),
	LATITUDE	 ("Latitude","deg"),
	LONGITUDE	 ("Longitude","deg"),
	NORTH	 	 ("North","ft"),
	EAST	 	 ("East","ft"),
	RPM_L		 ("Left RPM","1/min"),
	RPM_R		 ("Right RPM","1/min");
	
	private final String dataType;
	private final String unit;
	
	FlightDataType(String dataType, String unit) {
		this.dataType = dataType;
		this.unit = unit;
	}
	
	public String toString() {return dataType;}
	
	public String getUnit() {return unit;}
}
