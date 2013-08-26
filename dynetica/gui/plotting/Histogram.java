
package dynetica.gui.plotting;


import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.EnumeratedData;
import de.erichseifert.gral.data.statistics.Histogram1D;
import de.erichseifert.gral.data.statistics.Statistics;
import de.erichseifert.gral.plots.BarPlot;
import de.erichseifert.gral.plots.axes.*;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.MathUtils;
import de.erichseifert.gral.util.Orientation;
import java.awt.Color;


public class Histogram {
   InteractivePanel plotPanel;
        
   BarPlot barPlot;
   double min = Double.MAX_VALUE;
   double max = - Double.MAX_VALUE;
   int numBins = 10;    
   Color color = Color.ORANGE;

        public Histogram(){
        }
	public Histogram(double [] dataPoints, int numBins, String label) {
              setData(dataPoints, numBins, label);
	}
        
        public void setData(double [] dataPoints, int numBins, String label){
           this.numBins = numBins;
           if (numBins <1) {
               System.out.println("# bins in histogram < 1.");
               return;
           }          
                
           min = Double.MAX_VALUE;
           max = - Double.MAX_VALUE;
                
           DataTable data = new DataTable(Double.class); //stores the data
                
	   for (int i = 0; i < dataPoints.length; i++) {
              data.add(dataPoints[i]);
              min = Math.min(min, dataPoints[i]);
              max = Math.max(max, dataPoints[i]);
           }

           // Create histogram from data
           Histogram1D histogram = new Histogram1D(data, Orientation.VERTICAL, numBins);
                
           //
           // computing the offset and step size of bars.
           //
           Number [] firstbin = histogram.getCellLimits(0, 0);
           Number [] finalbin = histogram.getCellLimits(0, numBins - 1);
           double binWidth = (finalbin[1].doubleValue() - firstbin[0].doubleValue())/numBins;
           double offset = firstbin[1].doubleValue() - binWidth/2;
                
           // Create a second dimension (x axis) for plotting
           DataSource histogram2d = new EnumeratedData(histogram, offset, binWidth);	
           if (barPlot == null) 
              barPlot = new BarPlot(histogram2d);
           else {
              barPlot.clear();
              barPlot.add(histogram2d);
           }
                
           barPlot.setSetting(BarPlot.BACKGROUND, Color.WHITE);

           // Format plot
           barPlot.setInsets(new Insets2D.Double(20.0, 90.0, 60.0, 20.0));
           barPlot.setSetting(BarPlot.BAR_WIDTH, binWidth * 0.95);
                              
           String title = String.format("%d points", data.getRowCount());
           double mean = data.getStatistics().get(Statistics.MEAN);
           double var = data.getStatistics().get(Statistics.VARIANCE);
           title += "; mean = " + dynetica.util.Numerics.displayFormattedValue(mean);
           title += "; var = " + dynetica.util.Numerics.displayFormattedValue(var);
           barPlot.setSetting(BarPlot.TITLE, title);
   
           // Format axes
           Axis xAxis = barPlot.getAxis(BarPlot.AXIS_X);     
           xAxis.setMax(max * 1.10);
           xAxis.setMin(min * 0.90);
           
           // Format x axis
           AxisRenderer axisRendererX = barPlot.getAxisRenderer(BarPlot.AXIS_X);
           axisRendererX.setSetting(AxisRenderer.TICKS_ALIGNMENT, 0.0);
           axisRendererX.setSetting(AxisRenderer.TICKS_MINOR, false);

           // Format y axis
           barPlot.getAxis(BarPlot.AXIS_Y).setRange(0.0,
                                     MathUtils.ceil(histogram.getStatistics().get(Statistics.MAX)*1.1, 25.0));
           barPlot.getAxisRenderer(BarPlot.AXIS_Y).setSetting(AxisRenderer.TICKS_ALIGNMENT, 0.0);
           barPlot.getAxisRenderer(BarPlot.AXIS_Y).setSetting(AxisRenderer.TICKS_MINOR, false);
           barPlot.getAxisRenderer(BarPlot.AXIS_Y).setSetting(AxisRenderer.INTERSECTION, 0.0);


           addXLabel(label);
           addYLabel();


	   // Format bars
           barPlot.getPointRenderer(histogram2d).setSetting(PointRenderer.COLOR,
			GraphicsUtils.deriveDarker(color));
//		plot.getPointRenderer(histogram2d).setSetting(PointRenderer.VALUE_DISPLAYED, true);

		// Add plot to Swing component
            if (plotPanel == null) {
                plotPanel = new InteractivePanel(barPlot);
                plotPanel.setPannable(false);
                plotPanel.setZoomable(false);
            }
                
        }
        
        public InteractivePanel getPlotPanel() {
            return plotPanel;
        }
        
         private void addXLabel(String xLabel){
        AxisRenderer axisRendererX = barPlot.getAxisRenderer(BarPlot.AXIS_X);
        axisRendererX.setSetting(AxisRenderer.LABEL, xLabel);
         }
    
        private void addYLabel(){
            AxisRenderer axisRendererY = barPlot.getAxisRenderer(BarPlot.AXIS_Y);
            axisRendererY.setSetting(AxisRenderer.LABEL, "Counts");
        }            
}
