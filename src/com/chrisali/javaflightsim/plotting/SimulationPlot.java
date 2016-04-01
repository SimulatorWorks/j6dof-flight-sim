package com.chrisali.javaflightsim.plotting;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.chrisali.javaflightsim.simulation.integration.SimOuts;

/**
 * Contains a {@link CombinedDomainXYPlot} object, consisting of group of {@link XYPlot} objects.   
 * It generates a plot in Swing as a JComponent used in the JTabbedPane of {@link PlotWindow}. 
 * The plot created depends on the windowTitle String argument passed in. 
 */
public class SimulationPlot extends JComponent {

	private static final long serialVersionUID = 1L;
	
	private EnumMap<PlotType, XYPlot> plotLists = new EnumMap<PlotType, XYPlot>(PlotType.class);
	private ChartPanel chartPanel;
	
	// XY series objects for each set of data
	
	private	XYSeries uData        = new XYSeries("u");
	private	XYSeries vData        = new XYSeries("v");
	private	XYSeries wData        = new XYSeries("w");
			
	private	XYSeries posData      = new XYSeries("Position");
	private	XYSeries altData      = new XYSeries("Altitude");
			
	private	XYSeries altDotData   = new XYSeries("Alt Dot");
			
	private	XYSeries phiData      = new XYSeries("Phi");
	private	XYSeries thetaData    = new XYSeries("Theta");
	private	XYSeries psiData      = new XYSeries("Psi");
			
	private	XYSeries pData        = new XYSeries("p");
	private	XYSeries qData        = new XYSeries("q");
	private	XYSeries rData        = new XYSeries("r");
					
	private	XYSeries axData       = new XYSeries("a_x");
	private	XYSeries ayData       = new XYSeries("a_y");
	private	XYSeries azData       = new XYSeries("a_z");		
			
	private	XYSeries lData        = new XYSeries("L");
	private	XYSeries mData        = new XYSeries("M");
	private	XYSeries nData        = new XYSeries("N");
			
	private	XYSeries tasData      = new XYSeries("TAS");
			
	private	XYSeries betaData     = new XYSeries("Beta");
	private	XYSeries alphaData    = new XYSeries("Alpha");
			
	private	XYSeries elevData     = new XYSeries("Elevator");
	private	XYSeries ailData      = new XYSeries("Aileron");
	private	XYSeries rudData      = new XYSeries("Rudder");
	private	XYSeries throtData    = new XYSeries("Throttle");
	private	XYSeries flapData     = new XYSeries("Flaps");
			
	private	XYSeries alphaDotData = new XYSeries("Alpha Dot");
	private	XYSeries machData     = new XYSeries("Mach");
	
	// Domain Axes
	
	private NumberAxis timeAxis   = new NumberAxis("Time [sec]");
	private NumberAxis eastAxis   = new NumberAxis("East [ft]");
	
	// Range Axes
	
	private NumberAxis linVelAxis 		= new NumberAxis("Body Velocity [ft/sec]");
	private NumberAxis northAxis  		= new NumberAxis("North [ft]");
	private NumberAxis altAxis 			= new NumberAxis("Altitude [ft]");
	private NumberAxis altDotAxis 		= new NumberAxis("Vertical Speed [ft/min]"); 
	private NumberAxis psiAxis 			= new NumberAxis("Heading [rad]");
	private NumberAxis eulerAnglesAxis 	= new NumberAxis("Angle [rad]");
	private NumberAxis angularRatesAxis = new NumberAxis("Angular Rate [rad/sec]");
	private NumberAxis linearAccelAxis 	= new NumberAxis("Acceleration [g]");
	private NumberAxis totalMomentAxis 	= new NumberAxis("Moment [ft*lb]");
	private NumberAxis tasAxis 			= new NumberAxis("True Airspeed [ft/sec]");
	private NumberAxis windParamAxis 	= new NumberAxis("Angle [rad]");
	private NumberAxis elevAxis 		= new NumberAxis("Deflection [rad]");
	private NumberAxis ailAxis 			= new NumberAxis("Deflection [rad]");
	private NumberAxis rudAxis 			= new NumberAxis("Deflection [rad]");
	private NumberAxis throtAxis 		= new NumberAxis("Position [norm]");
	private NumberAxis flapAxis 		= new NumberAxis("Deflection [rad]");
	private NumberAxis alphDotAxis 		= new NumberAxis("Rate [rad/sec]");
	private NumberAxis machAxis 		= new NumberAxis("Mach Number");
	
	private CombinedDomainXYPlot ratesPlot      = new CombinedDomainXYPlot(timeAxis);
	private CombinedDomainXYPlot positionPlot   = new CombinedDomainXYPlot(eastAxis);
	private CombinedDomainXYPlot instrumentPlot = new CombinedDomainXYPlot(timeAxis);
	private CombinedDomainXYPlot miscPlot       = new CombinedDomainXYPlot(timeAxis);
	private CombinedDomainXYPlot controlsPlot   = new CombinedDomainXYPlot(timeAxis);

	/**
	 * Creates plots for variables monitored in the logsOut ArrayList
	 * 
	 * @param logsOut
	 * @param windowTitle
	 */
	public SimulationPlot(List<EnumMap<SimOuts, Double>> logsOut, String windowTitle) {
		
		makePlotLists(logsOut);
		
		setLayout(new BorderLayout());
		
		// Select from methods below to create a chart panels to populate AWT window 
		switch (windowTitle) {
			case "Rates":
				chartPanel = new ChartPanel(makeRatesPlots());
				setPreferredSize(new Dimension(1000, 950));
				add(chartPanel, BorderLayout.CENTER);
				break;
			case "Position":
				chartPanel = new ChartPanel(makePositionPlot());
				setPreferredSize(new Dimension(750, 750));
				add(chartPanel, BorderLayout.CENTER);
				break;
			case "Instruments":
				chartPanel = new ChartPanel(makeInstrumentPlots());
				setPreferredSize(new Dimension(1000, 950));
				add(chartPanel, BorderLayout.CENTER);
				break;
			case "Miscellaneous":
				chartPanel = new ChartPanel(makeMiscPlots());
				setPreferredSize(new Dimension(1000, 950));
				add(chartPanel, BorderLayout.CENTER);
				break;
			case "Controls":
				chartPanel = new ChartPanel(makeControlsPlots());
				setPreferredSize(new Dimension(1000, 950));
				add(chartPanel, BorderLayout.CENTER);
				break;	
			default:
				System.err.println("Invalid plot type selected!");
				break;
		}
	}
	
	/**
	 *  Generates a {@link JFreeChart} object associated with rates and accelerations (Angular Rates, Linear Velocities and Linear Accelerations) on a {@link CombinedDomainXYPlot}.
	 */
	private JFreeChart makeRatesPlots() {
		ratesPlot.add(plotLists.get(PlotType.ANGULAR_RATE), 1);
		ratesPlot.add(plotLists.get(PlotType.VELOCITY),     1);
		ratesPlot.add(plotLists.get(PlotType.ACCELERATION), 1);
		ratesPlot.setOrientation(PlotOrientation.VERTICAL);
		ratesPlot.setGap(20);
		
		return new JFreeChart("Rates", 
					 	      JFreeChart.DEFAULT_TITLE_FONT, 
					 	      ratesPlot, 
					          true);
	}
	
	/**
	 *  Generates a {@link JFreeChart} object associated with aircraft position (North vs East) on a {@link CombinedDomainXYPlot}.
	 */
	private JFreeChart makePositionPlot() {
		positionPlot.add(plotLists.get(PlotType.POSITION), 1);
		
		positionPlot.setOrientation(PlotOrientation.VERTICAL);
		positionPlot.setGap(20);
		
		return new JFreeChart("Position", 
					 	      JFreeChart.DEFAULT_TITLE_FONT, 
					 	      positionPlot, 
					          true);
	}
	
	/**
	 *  Generates a {@link JFreeChart} object associated with instrumentation data (Pitch, Roll, Airspeed, Heading, Altitude and Vertical Speed) on a {@link CombinedDomainXYPlot}.
	 */
	private JFreeChart makeInstrumentPlots() {
		instrumentPlot.add(plotLists.get(PlotType.EULER_ANGLES), 1);
		instrumentPlot.add(plotLists.get(PlotType.TAS), 		 1);
		instrumentPlot.add(plotLists.get(PlotType.HEADING),      1);
		instrumentPlot.add(plotLists.get(PlotType.ALTITUDE),     1);
		instrumentPlot.add(plotLists.get(PlotType.VERT_SPEED),   1);
		
		instrumentPlot.setOrientation(PlotOrientation.VERTICAL);
		instrumentPlot.setGap(20);
		
		return new JFreeChart("Instruments", 
					 	      JFreeChart.DEFAULT_TITLE_FONT, 
					 	      instrumentPlot, 
					          true);
	}
	
	/**
	 *  Generates a {@link JFreeChart} object associated with miscellaneous air data (Alpha, Beta, Alphadot and Mach) on a {@link CombinedDomainXYPlot}.
	 */
	private JFreeChart makeMiscPlots() {
		miscPlot.add(plotLists.get(PlotType.WIND_PARAM), 1);
		miscPlot.add(plotLists.get(PlotType.ALPHA_DOT),  1);
		miscPlot.add(plotLists.get(PlotType.MACH),  1);
		
		miscPlot.setOrientation(PlotOrientation.VERTICAL);
		miscPlot.setGap(20);
		
		return new JFreeChart("Miscellaneous", 
					 	      JFreeChart.DEFAULT_TITLE_FONT, 
					 	      miscPlot, 
					          true);
	}
	
	/**
	 *  Generates a {@link JFreeChart} object associated with aircraft controls (Elevator, Aileron, Rudder, Throttle, Flaps) on a {@link CombinedDomainXYPlot}.
	 */
	private JFreeChart makeControlsPlots() {
		controlsPlot.add(plotLists.get(PlotType.ELEVATOR), 1);
		controlsPlot.add(plotLists.get(PlotType.AILERON),  1);
		controlsPlot.add(plotLists.get(PlotType.RUDDER),   1);
		controlsPlot.add(plotLists.get(PlotType.THROTTLE), 1);
		controlsPlot.add(plotLists.get(PlotType.FLAPS),    1);
		
		controlsPlot.setOrientation(PlotOrientation.VERTICAL);
		controlsPlot.setGap(20);
		
		return new JFreeChart("Controls", 
					 	      JFreeChart.DEFAULT_TITLE_FONT, 
					 	      controlsPlot, 
					          true);
	}
	
	/**
	 * Populates the {@link plotLists} EnumMap with XYPlot objects created from the logsOut ArrayList that is passed in. 
	 * It first creates {@link XYSeries} objects with data from logsOut, adds those series to {@link XYSeriesCollection}, adds those 
	 * series collections to {@link XYPlot} objects, and finally puts the XYPlot objects into {@link plotLists}
	 */
	private void makePlotLists(List<EnumMap<SimOuts, Double>> logsOut) {
		
		updateXYSeriesData(logsOut);
		
		// Create XYSeriesCollections for each desired plot and add series to them
		
		XYSeriesCollection linearVelSeries    = new XYSeriesCollection();
		linearVelSeries.addSeries(uData);
		linearVelSeries.addSeries(vData);
		linearVelSeries.addSeries(wData);
		
		XYSeriesCollection positionSeries     = new XYSeriesCollection();
		positionSeries.addSeries(posData);
		
		XYSeriesCollection altitudeSeries     = new XYSeriesCollection();
		altitudeSeries.addSeries(altData);
		
		XYSeriesCollection altDotSeries       = new XYSeriesCollection();
		altDotSeries.addSeries(altDotData);
		
		XYSeriesCollection headingSeries      = new XYSeriesCollection();
		headingSeries.addSeries(psiData);
		
		XYSeriesCollection eulerAnglesSeries  = new XYSeriesCollection();
		eulerAnglesSeries.addSeries(phiData);
		eulerAnglesSeries.addSeries(thetaData);
		
		XYSeriesCollection angularRatesSeries = new XYSeriesCollection();
		angularRatesSeries.addSeries(pData);
		angularRatesSeries.addSeries(qData);
		angularRatesSeries.addSeries(rData);
		
		XYSeriesCollection linearAccelSeries  = new XYSeriesCollection();
		linearAccelSeries.addSeries(axData);
		linearAccelSeries.addSeries(ayData);
		linearAccelSeries.addSeries(azData);
		
		XYSeriesCollection totalMomentSeries  = new XYSeriesCollection();
		totalMomentSeries.addSeries(lData);
		totalMomentSeries.addSeries(mData);
		totalMomentSeries.addSeries(nData);
		
		XYSeriesCollection tasSeries          = new XYSeriesCollection();
		tasSeries.addSeries(tasData);
		
		XYSeriesCollection windParamSeries    = new XYSeriesCollection();
		windParamSeries.addSeries(betaData);
		windParamSeries.addSeries(alphaData);
		
		XYSeriesCollection elevSeries		  = new XYSeriesCollection();
		elevSeries.addSeries(elevData);
		
		XYSeriesCollection ailSeries		  = new XYSeriesCollection();
		ailSeries.addSeries(ailData);
		
		XYSeriesCollection rudSeries		  = new XYSeriesCollection();
		rudSeries.addSeries(rudData);
		
		XYSeriesCollection throtSeries		  = new XYSeriesCollection();
		throtSeries.addSeries(throtData);
		
		XYSeriesCollection flapSeries		  = new XYSeriesCollection();
		flapSeries.addSeries(flapData);
		
		XYSeriesCollection alphaDotSeries	  = new XYSeriesCollection();
		alphaDotSeries.addSeries(alphaDotData);
		
		XYSeriesCollection machSeries		  = new XYSeriesCollection();
		machSeries.addSeries(machData);
		
		// Create plots, add series collections to them and put the plots into a HashMap with an enum key
		
		XYPlot linearVelPlot    = new XYPlot(linearVelSeries,    
											 timeAxis,
											 linVelAxis, 
									 		 new StandardXYItemRenderer()); 
		
		plotLists.put(PlotType.VELOCITY, linearVelPlot);
		
		XYPlot positionPlot     = new XYPlot(positionSeries, 
											 eastAxis, 
											 northAxis, 
											 new StandardXYItemRenderer());
		
		plotLists.put(PlotType.POSITION, positionPlot);
		
		XYPlot altitudePlot     = new XYPlot(altitudeSeries, 
											 timeAxis, 
											 altAxis, 
											 new StandardXYItemRenderer());

		plotLists.put(PlotType.ALTITUDE, altitudePlot);
		
		XYPlot altDotPlot       = new XYPlot(altDotSeries, 
											 timeAxis, 
										     altDotAxis, 
										     new StandardXYItemRenderer());

		plotLists.put(PlotType.VERT_SPEED, altDotPlot);
		
		XYPlot headingPlot      = new XYPlot(headingSeries, 
											 timeAxis, 
											 psiAxis, 
											 new StandardXYItemRenderer());
							
		plotLists.put(PlotType.HEADING, headingPlot);
		
		XYPlot eulerAnglesPlot  = new XYPlot(eulerAnglesSeries, 
											 timeAxis, 
											 eulerAnglesAxis, 
											 new StandardXYItemRenderer());
		
		plotLists.put(PlotType.EULER_ANGLES, eulerAnglesPlot);
		
		XYPlot angularRatesPlot = new XYPlot(angularRatesSeries, 
											 timeAxis, 
									     	 angularRatesAxis, 
									     	 new StandardXYItemRenderer());
		
		plotLists.put(PlotType.ANGULAR_RATE, angularRatesPlot);
		
		XYPlot linearAccelPlot  = new XYPlot(linearAccelSeries, 
										   	 timeAxis, 
											 linearAccelAxis, 
											 new StandardXYItemRenderer());
		
		plotLists.put(PlotType.ACCELERATION, linearAccelPlot);
		
		XYPlot totalMomentPlot  = new XYPlot(totalMomentSeries, 
											 timeAxis, 
											 totalMomentAxis, 
											 new StandardXYItemRenderer()); 
		
		plotLists.put(PlotType.MOMENT, totalMomentPlot);
		
		XYPlot tasPlot          = new XYPlot(tasSeries, 
											 timeAxis, 
										     tasAxis, 
										     new StandardXYItemRenderer());

		plotLists.put(PlotType.TAS, tasPlot);
		
		XYPlot windParamPlot    = new XYPlot(windParamSeries, 
											 timeAxis, 
											 windParamAxis, 
											 new StandardXYItemRenderer());
		
		plotLists.put(PlotType.WIND_PARAM, windParamPlot);
		
		XYPlot elevPlot    		= new XYPlot(elevSeries, 
											 timeAxis, 
											 elevAxis, 
											 new StandardXYItemRenderer());

		plotLists.put(PlotType.ELEVATOR, elevPlot);
		
		XYPlot ailPlot		    = new XYPlot(ailSeries, 
											 timeAxis, 
											 ailAxis, 
											 new StandardXYItemRenderer());
		
		plotLists.put(PlotType.AILERON, ailPlot);
		
		XYPlot rudPlot    		= new XYPlot(rudSeries, 
											 timeAxis, 
											 rudAxis, 
											 new StandardXYItemRenderer());
		
		plotLists.put(PlotType.RUDDER, rudPlot);
		
		XYPlot throtPlot        = new XYPlot(throtSeries, 
											 timeAxis, 
											 throtAxis, 
											 new StandardXYItemRenderer());
		
		plotLists.put(PlotType.THROTTLE, throtPlot);
		
		XYPlot flapPlot         = new XYPlot(flapSeries, 
											 timeAxis, 
											 flapAxis, 
											 new StandardXYItemRenderer());
		
		plotLists.put(PlotType.FLAPS, flapPlot);
		
		XYPlot alphaDotPlot     = new XYPlot(alphaDotSeries, 
											 timeAxis, 
											 alphDotAxis, 
											 new StandardXYItemRenderer());
							
		plotLists.put(PlotType.ALPHA_DOT, alphaDotPlot);

		XYPlot machPlot         = new XYPlot(machSeries, 
											 timeAxis, 
											 machAxis, 
											 new StandardXYItemRenderer());

		plotLists.put(PlotType.MACH, machPlot);
	}
	
	/**
	 * Updates all XYSeries objects with new data from logsOut list
	 * 
	 * @param logsOut
	 */
	protected void updateXYSeriesData(List<EnumMap<SimOuts, Double>> logsOut) {
		
		// Clear all data from series
		
		uData.clear();
		vData.clear();
		wData.clear();
		
		posData.clear();
		altData.clear();
		
		altDotData.clear();
		
		phiData.clear();
		thetaData.clear();
		psiData.clear();
		
		pData.clear();
		qData.clear();
		rData.clear();
		
		axData.clear();
		ayData.clear();
		azData.clear();
		
		lData.clear();
		mData.clear();
		nData.clear();
		
		tasData.clear();
		
		betaData.clear();
		alphaData.clear();
		
		elevData.clear();
		ailData.clear();
		rudData.clear();
		throtData.clear();
		flapData.clear();
		
		alphaDotData.clear();
		machData.clear();
		
		// Add data from logsOut to each XYSeries
		
		for (Iterator<EnumMap<SimOuts, Double>> logsOutItr = logsOut.iterator(); logsOutItr.hasNext();) {
			EnumMap<SimOuts, Double> simOut = logsOutItr.next();
			
			uData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.U));
			vData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.V));
			wData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.W));
			
			posData.add(simOut.get(SimOuts.EAST),simOut.get(SimOuts.NORTH));
			altData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.ALT));
			
			altDotData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.ALT_DOT));
			
			phiData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.PHI));
			thetaData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.THETA));
			psiData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.PSI));
			
			pData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.P));
			qData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.Q));
			rData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.R));
			
			axData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.AN_X));
			ayData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.AN_Y));
			azData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.AN_Z));
			
			lData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.L));
			mData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.M));
			nData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.N));
			
			tasData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.TAS));
			
			betaData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.BETA));
			alphaData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.ALPHA));
			
			elevData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.ELEVATOR));
			ailData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.AILERON));
			rudData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.RUDDER));
			throtData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.THROTTLE_1));
			flapData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.FLAPS));
			
			alphaDotData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.ALPHA_DOT));
			machData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.MACH));
		}
		
		// Bounds the minimum X Axis value to the first time value in the data series 
		
		timeAxis.setRange(uData.getMinX(), uData.getMaxX());
		
		// Updates each CombinedDomainXYPlot with the new time axis
		
		ratesPlot.setDomainAxis(timeAxis);
		for (int i = 0; i < ratesPlot.getRangeAxisCount(); i++) {
			if (ratesPlot.getRangeAxis(i) != null) {
				ratesPlot.getRangeAxis(i).setAutoRange(false);
				ratesPlot.getRangeAxis(i).resizeRange(3);
			}
		}
		
		instrumentPlot.setDomainAxis(timeAxis);
		for (int i = 0; i < instrumentPlot.getRangeAxisCount(); i++) {
			if (instrumentPlot.getRangeAxis(i) != null)	{
				instrumentPlot.getRangeAxis(i).setAutoRange(false);
				instrumentPlot.getRangeAxis(i).resizeRange(3);
			}
		}
		
		miscPlot.setDomainAxis(timeAxis);
		for (int i = 0; i < miscPlot.getRangeAxisCount(); i++) {
			if (miscPlot.getRangeAxis(i) != null) {
				miscPlot.getRangeAxis(i).setAutoRange(false);
				miscPlot.getRangeAxis(i).resizeRange(3);
			}
		}
		
		controlsPlot.setDomainAxis(timeAxis);
		for (int i = 0; i < controlsPlot.getRangeAxisCount(); i++) {
			if (controlsPlot.getRangeAxis(i) != null) {
				controlsPlot.getRangeAxis(i).setAutoRange(false);
				controlsPlot.getRangeAxis(i).resizeRange(3);
			}
		}
	}
	
	/**
	 * @return ChartPanel object so that it can be updated with new data
	 */
	protected ChartPanel getChartPanel() {return chartPanel;}
}