package evemanutool.gui.corp;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Paint;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.swing.BorderFactory;
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

import evemanutool.data.cache.IndustryStatsEntry;
import evemanutool.data.display.CorpMember;
import evemanutool.gui.corp.components.CorpMemberModel;
import evemanutool.gui.general.tabel.ScrollableTablePanel;
import evemanutool.utils.databases.CorpApiDB;
import evemanutool.utils.datahandling.GUIUpdater;

@SuppressWarnings("serial")
public class StatsPanel extends JPanel implements GUIUpdater {
	
	//DB:s
	private CorpApiDB cdb;

	//Internal panels.
	private ScrollableTablePanel<CorpMember> memberPanel;

	//Plot.
	private JFreeChart chart;
	private XYPlot plot;
	
	public StatsPanel(CorpApiDB cdb) {
		
		//Set DB refs.
		this.cdb = cdb;

		//Main layout.
		setLayout(new GridLayout(1, 2));
		JPanel statsPanel = new JPanel(new BorderLayout());
		
		//Create Industry stats chart.
		JPanel p1 = new JPanel(new GridLayout(1, 1));
		
		ChartPanel chartPanel = new ChartPanel(createChart());
		//Disable popup.
		chartPanel.setPopupMenu(null);
		statsPanel.add(chartPanel, BorderLayout.CENTER);
		p1.add(statsPanel);

		//Create character panel.
		JPanel p2 = new JPanel(new GridLayout(1, 1));
		
		memberPanel = new ScrollableTablePanel<>(new CorpMemberModel());
		memberPanel.setBorder(BorderFactory.createTitledBorder("Corp Members"));
		p2.add(memberPanel);
		
		
		//Add top level components.
		add(p1);
		add(p2);
	}
	
	private JFreeChart createChart() {
		
		//Create chart.
		chart = ChartFactory.createTimeSeriesChart(
	            "Corporation Statistics", "Date", "ISK", new TimeSeriesCollection(TimeZone.getTimeZone("UTC")), true, true, false);

		//Set appearance.
		chart.setBackgroundPaint(getBackground());
		chart.setPadding(new RectangleInsets(10, 10, 10, 10));
		
		//Override colors.
		chart.getPlot().setDrawingSupplier(new DefaultDrawingSupplier( 
				new Paint[] {Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA}, 
				DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE, 
				DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE, 
				DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE, 
				DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE)); 
				
		//Set plot settings.
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
        DateAxis axisX = (DateAxis) plot.getDomainAxis();
        axisX.setDateFormatOverride(new SimpleDateFormat("YYYY-MM-dd"));
        
        NumberAxis axisY = (NumberAxis) plot.getRangeAxis();
        axisY.setNumberFormatOverride(NumberFormat.getIntegerInstance());
		
		return chart;
	}

	private XYDataset createDataSet() {
		
		TimeSeriesCollection dataSet = new TimeSeriesCollection(TimeZone.getTimeZone("UTC"));
		
		TimeSeries s1 = new TimeSeries("Total Capital");
		TimeSeries s2 = new TimeSeries("Industry Wallet");
		TimeSeries s3 = new TimeSeries("Material Value");
		TimeSeries s4 = new TimeSeries("Market Orders Value");
		
		for (IndustryStatsEntry iS : cdb.getIndustryStats()) {
			s1.add(new Millisecond(iS.getDate()), iS.getTotalCapital());
			s2.add(new Millisecond(iS.getDate()), iS.getIndustryWallet());
			s3.add(new Millisecond(iS.getDate()), iS.getMaterialValue());
			s4.add(new Millisecond(iS.getDate()), iS.getMarketOrdersValue());
		}
		
		dataSet.addSeries(s1);
		dataSet.addSeries(s2);
		dataSet.addSeries(s3);
		dataSet.addSeries(s4);
		
		return dataSet;
	}
	
	@Override
	public void updateGUI(){
		//Plot.
		plot.setDataset(createDataSet());
		chart.fireChartChanged();
		
		//Update layout.
		revalidate();
		
		//Corp members.
		memberPanel.setData(cdb.getCorpMembers());
	}
}
