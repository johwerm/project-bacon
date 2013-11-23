package evemanutool.gui.corp.components;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Paint;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.TimeZone;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

import evemanutool.data.cache.TradeEntry;
import evemanutool.data.cache.TradeEntry.HistoryType;
import evemanutool.data.cache.TradeHistoryEntry;

@SuppressWarnings("serial")
public class TradeHistoryPanel extends JPanel {

	private JFreeChart chart;
	private XYPlot plot;

	public TradeHistoryPanel(String label, String numUnit) {
		
		//Create Industry stats chart.
		setLayout(new BorderLayout());
		ChartPanel chartPanel = new ChartPanel(createChart(label, numUnit));
		//Disable popup.
		chartPanel.setPopupMenu(null);
		
		add(chartPanel, BorderLayout.CENTER);
	}
	
	public void setTradeHistory(TradeHistoryEntry l, HistoryType key) {
		
		plot.setDataset(createDataSet(l.getHistory(), key));
		chart.fireChartChanged();
	}

	private JFreeChart createChart(String label, String numUnit) {
		
		//Create chart.
		chart = ChartFactory.createTimeSeriesChart(
	            label, "Date", numUnit, new TimeSeriesCollection(TimeZone.getTimeZone("UTC")), false, true, false);
		//Set appearance.
		chart.setBackgroundPaint(getBackground());
		chart.setPadding(new RectangleInsets(10, 10, 10, 15));
		
		//Override colors.
		chart.getPlot().setDrawingSupplier(new DefaultDrawingSupplier( 
				new Paint[] {Color.BLUE}, 
				DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE, 
				DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE, 
				DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE, 
				DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE)); 
		
		plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.BLACK);
        plot.setRangeGridlinePaint(Color.BLACK);
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(false);
        XYItemRenderer renderer = plot.getRenderer();
        if (renderer instanceof StandardXYItemRenderer) {
            renderer.setSeriesStroke(0, new BasicStroke(2.0f));
            renderer.setSeriesStroke(1, new BasicStroke(2.0f));
          }
        
        //Formatting.
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("YYYY-MM-dd"));
        
        NumberAxis axisY = (NumberAxis) plot.getRangeAxis();
        axisY.setAutoRangeMinimumSize(10);
        axisY.setNumberFormatOverride(NumberFormat.getIntegerInstance());
		
		return chart;
	}

	private XYDataset createDataSet(Collection<TradeEntry> l, HistoryType key) {
		
		TimeSeriesCollection dataSet = new TimeSeriesCollection(TimeZone.getTimeZone("UTC"));
		
		TimeSeries s1 = new TimeSeries("Trend");
		
		for (TradeEntry tHE : l) {
			s1.add(new Millisecond(tHE.getDate()), tHE.getValue(key));
		}
		dataSet.addSeries(s1);
		
		return dataSet;
	}
}
