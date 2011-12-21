/* Copyright CNRS-CREATIS
 *
 * Rafael Silva
 * rafael.silva@creatis.insa-lyon.fr
 * http://www.rafaelsilva.com
 *
 * This software is a grid-enabled data-driven workflow manager and editor.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package fr.insalyon.creatis.vip.application.client.view.monitor;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.ColumnChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.rednels.ofcgwt.client.ChartWidget;
import com.rednels.ofcgwt.client.model.ChartData;
import com.rednels.ofcgwt.client.model.Text;
import com.rednels.ofcgwt.client.model.ToolTip;
import com.rednels.ofcgwt.client.model.ToolTip.MouseStyle;
import com.rednels.ofcgwt.client.model.axis.Keys;
import com.rednels.ofcgwt.client.model.axis.Label;
import com.rednels.ofcgwt.client.model.axis.XAxis;
import com.rednels.ofcgwt.client.model.axis.YAxis;
import com.rednels.ofcgwt.client.model.elements.BarChart;
import com.rednels.ofcgwt.client.model.elements.BarChart.BarStyle;
import com.rednels.ofcgwt.client.model.elements.StackedBarChart;
import com.rednels.ofcgwt.client.model.elements.StackedBarChart.Stack;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tile.TileGrid;
import com.smartgwt.client.widgets.tile.events.RecordClickEvent;
import com.smartgwt.client.widgets.tile.events.RecordClickHandler;
import com.smartgwt.client.widgets.viewer.DetailViewerField;
import fr.insalyon.creatis.vip.application.client.ApplicationConstants;
import fr.insalyon.creatis.vip.application.client.rpc.JobService;
import fr.insalyon.creatis.vip.application.client.rpc.JobServiceAsync;
import fr.insalyon.creatis.vip.core.client.view.ModalWindow;
import fr.insalyon.creatis.vip.core.client.view.application.ApplicationTileRecord;
import fr.insalyon.creatis.vip.core.client.view.property.PropertyRecord;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author Rafael Silva
 */
public class ChartsTab2 extends Tab {

    private ModalWindow modal;
    private String simulationID;
    private DynamicForm form;
    private SelectItem chartsItem;
    private TextItem binItem;
    private VLayout vLayout;
    private VLayout mainLayout;
    private VLayout chartLayout;
    private VLayout innerChartLayout;
    private ChartWidget chart;
    private ListGrid grid;
    private TileGrid tileGrid;
    private StringBuilder data;

    public ChartsTab2(String simulationID) {

        this.simulationID = simulationID;
        this.setTitle(Canvas.imgHTML(ApplicationConstants.ICON_CHART));
        this.setPrompt("Performance Statistics");

        configureForm();
        configureChart();

        vLayout = new VLayout(20);
        vLayout.setHeight100();
        vLayout.setOverflow(Overflow.AUTO);
        vLayout.setPadding(10);

        vLayout.addMember(form);
        vLayout.addMember(mainLayout);

        modal = new ModalWindow(vLayout);
        this.setPane(vLayout);
    }

    private void configureForm() {

        form = new DynamicForm();
        form.setWidth(500);
        form.setNumCols(5);

        LinkedHashMap<String, String> chartsMap = new LinkedHashMap<String, String>();
        chartsMap.put("1", "Job Flow");
        chartsMap.put("2", "Checkpoints per job");
        chartsMap.put("3", "Histogram of Execution Times");
        chartsMap.put("4", "Histogram of Download Times");
        chartsMap.put("5", "Histogram of Upload Times");
        chartsMap.put("6", "Site histogram");
        chartsItem = new SelectItem("charts", "Chart");
        chartsItem.setValueMap(chartsMap);
        chartsItem.setEmptyDisplayValue("Select a chart...");
        chartsItem.addChangedHandler(new ChangedHandler() {

            public void onChanged(ChangedEvent event) {
                int value = new Integer(chartsItem.getValueAsString());
                if (value == 1) {
                    binItem.setDisabled(true);
                } else {
                    binItem.setDisabled(false);
                }
            }
        });

        binItem = new TextItem("bin", "Bin Size");
        binItem.setWidth(50);
        binItem.setValue("100");
        binItem.setKeyPressFilter("[0-9.]");

        ButtonItem generateButtonItem = new ButtonItem("generateChart", " Generate Chart ");
        generateButtonItem.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                generateChart();
            }
        });
        generateButtonItem.setStartRow(false);

        form.setItems(chartsItem, binItem, generateButtonItem);
    }

    private void configureChart() {

        mainLayout = new VLayout(10);
        mainLayout.setWidth100();
        mainLayout.setHeight100();
        mainLayout.setOverflow(Overflow.AUTO);

        chartLayout = new VLayout();
        chartLayout.setWidth(800);
        chartLayout.setHeight(370);

        innerChartLayout = new VLayout();
        innerChartLayout.setWidth(800);
        innerChartLayout.setHeight(370);

        chartLayout.addMember(innerChartLayout);
        mainLayout.addMember(chartLayout);
    }

    private void generateChart() {

        int value = new Integer(chartsItem.getValueAsString());
        int binSize = 100;
        if (binItem.getValueAsString() != null && !binItem.getValueAsString().equals("")) {
            binSize = new Integer(binItem.getValueAsString().trim());
        }

        switch (value) {
            case 1:
                plotJobsPerTime();
                break;
            case 2:
                plotCkptsPerJob();
                break;
            case 3:
                plotExecutionPerNumberOfJobs(binSize);
                break;
            case 4:
                plotDownloadPerNumberOfJobs(binSize);
                break;
            case 5:
                plotUploadPerNumberOfJobs(binSize);
                break;
            case 6:
                plotSiteHistogram();
                break;
        }
    }

    private void plotJobsPerTime() {

        JobServiceAsync service = JobService.Util.getInstance();
        final AsyncCallback<List<String>> callback = new AsyncCallback<List<String>>() {

            public void onFailure(Throwable caught) {
                modal.hide();
                SC.warn("Unable to get chart data:<br />" + caught.getMessage());
            }

            public void onSuccess(List<String> result) {
                VisualizationUtils.loadVisualizationApi(getColumnStackChartRunnable(result), ColumnChart.PACKAGE);
                modal.hide();
            }
        };
        modal.show("Building job flow chart...", true);
        service.getJobFlow(simulationID, callback);
    }

    private void plotCkptsPerJob() {

        JobServiceAsync service = JobService.Util.getInstance();
        final AsyncCallback<List<String>> callback = new AsyncCallback<List<String>>() {

            public void onFailure(Throwable caught) {
                modal.hide();
                SC.warn("Unable to get chart data:<br />" + caught.getMessage());
            }

            public void onSuccess(List<String> result) {
                buildCkptChart(result);
                modal.hide();
            }
        };
        modal.show("Building chart...", true);
        service.getCkptsPerJob(simulationID, callback);
    }

    /**
     * 
     * @param binSize Size of the group
     */
    private void plotExecutionPerNumberOfJobs(final int binSize) {

        JobServiceAsync service = JobService.Util.getInstance();
        final AsyncCallback<List<String>> callback = new AsyncCallback<List<String>>() {

            public void onFailure(Throwable caught) {
                modal.hide();
                SC.warn("Unable to get chart data:<br />" + caught.getMessage());
            }

            public void onSuccess(List<String> result) {
                modal.hide();
                buildBarChartAndGrid(result, "Execution Time (sec)", "#00aa00", binSize);
            }
        };
        modal.show("Building chart...", true);
        service.getExecutionPerNumberOfJobs(simulationID, binSize, callback);
    }

    /**
     * 
     * @param binSize
     */
    private void plotDownloadPerNumberOfJobs(final int binSize) {

        JobServiceAsync service = JobService.Util.getInstance();
        final AsyncCallback<List<String>> callback = new AsyncCallback<List<String>>() {

            public void onFailure(Throwable caught) {
                modal.hide();
                SC.warn("Unable to get chart data:<br />" + caught.getMessage());
            }

            public void onSuccess(List<String> result) {
                modal.hide();
                buildBarChartAndGrid(result, "Download Time (sec)", "#6699CC", binSize);
            }
        };
        modal.show("Building chart...", true);
        service.getDownloadPerNumberOfJobs(simulationID, binSize, callback);
    }

    /**
     * 
     * @param binSize
     */
    private void plotUploadPerNumberOfJobs(final int binSize) {

        JobServiceAsync service = JobService.Util.getInstance();
        final AsyncCallback<List<String>> callback = new AsyncCallback<List<String>>() {

            public void onFailure(Throwable caught) {
                modal.hide();
                SC.warn("Unable to get chart data:<br />" + caught.getMessage());
            }

            public void onSuccess(List<String> result) {
                modal.hide();
                buildBarChartAndGrid(result, "Upload Time (sec)", "#CC9966", binSize);
            }
        };
        modal.show("Building chart...", true);
        service.getUploadPerNumberOfJobs(simulationID, binSize, callback);
    }

    private void plotSiteHistogram() {
        JobServiceAsync service = JobService.Util.getInstance();
        final AsyncCallback<List<String>> callback = new AsyncCallback<List<String>>() {

            public void onFailure(Throwable caught) {
                modal.hide();
                SC.warn("Unable to get chart data:<br />" + caught.getMessage());
            }

            public void onSuccess(List<String> result) {
                modal.hide();
                buildBarChartAndGrid(result, "Site", "#FF0000", 1);
            }
        };
        modal.show("Building chart...", true);
        service.getSiteHistogram(simulationID, callback);
    }

    private Runnable getColumnStackChartRunnable(final List<String> result) {

        return new Runnable() {

            public void run() {

                DataTable dataTable = DataTable.create();
                dataTable.addColumn(ColumnType.STRING, "Job");
                dataTable.addColumn(ColumnType.NUMBER, "Submitted");
                dataTable.addColumn(ColumnType.NUMBER, "Queued");
                dataTable.addColumn(ColumnType.NUMBER, "Input");
                dataTable.addColumn(ColumnType.NUMBER, "Execution");
                dataTable.addColumn(ColumnType.NUMBER, "Output");
                dataTable.addColumn(ColumnType.NUMBER, "Checkpoint Init");
                dataTable.addColumn(ColumnType.NUMBER, "Checkpoint Upload");
                dataTable.addColumn(ColumnType.NUMBER, "Error");

                dataTable.addRows(result.size());
                int row = 0;
                data = new StringBuilder();
                int max = 0;
                int nbJobs = 0;
                long cpuTime = 0;
                long waitingTime = 0;
                long sequentialTime = 0;

                for (String values : result) {
                    data.append(values.replaceAll("##", ","));
                    data.append("\n");
                    String[] v = values.split("##");

                    int creation = new Integer(v[1]);
                    int queued = new Integer(v[2]);
                    int input = new Integer(v[3]) >= 0 ? new Integer(v[3]) : 0;
                    int execution = new Integer(v[4]) >= 0 ? new Integer(v[4]) : 0;
                    int output = new Integer(v[5]) >= 0 ? new Integer(v[5]) : 0;
                    int checkpointInit = new Integer(v[6]) >= 0 ? new Integer(v[6]) : 0;
                    int checkpointUpload = new Integer(v[7]) >= 0 ? new Integer(v[7]) : 0;
                    int failedTime = new Integer(v[8]) >= 0 ? new Integer(v[8]) : 0;

                    dataTable.setValue(row, 0, "");
                    dataTable.setValue(row, 1, creation);
                    dataTable.setValue(row, 2, queued);
                    dataTable.setValue(row, 3, input);
                    dataTable.setValue(row, 4, execution);
                    dataTable.setValue(row, 5, output);
                    dataTable.setValue(row, 6, checkpointInit);
                    dataTable.setValue(row, 7, checkpointUpload);
                    dataTable.setValue(row, 8, failedTime);
                    row++;

                    int count = creation + queued + input + execution + output
                            + checkpointInit + checkpointUpload;
                    cpuTime += execution;
                    sequentialTime += input + execution + output;
                    nbJobs++;
                    waitingTime += queued;

                    if (count > max) {
                        max = count;
                    }
                }

                Options options = Options.create();
                options.setWidth(800);
                options.setHeight(370);
                options.setFontSize(10);
                options.setIsStacked(true);
                options.setColors("#8C8063", "#FFC682", "#2388E8", "#33AA82",
                        "#7F667F", "#3E6864", "#1F3533", "#7F263D");

                AxisOptions hAxisOptions = AxisOptions.create();
                hAxisOptions.setTitle("Jobs");
                options.setHAxisOptions(hAxisOptions);

                AxisOptions vAxisOptions = AxisOptions.create();
                vAxisOptions.setTitle("Time (s)");
                vAxisOptions.setMaxValue(max);
                options.setVAxisOptions(vAxisOptions);

                chartLayout.removeMember(innerChartLayout);
                innerChartLayout = new VLayout();
                innerChartLayout.setWidth(800);
                innerChartLayout.setHeight(370);
                innerChartLayout.addMember(new ColumnChart(dataTable, options));
                chartLayout.addMember(innerChartLayout);

                configureGrid();
                PropertyRecord[] data = new PropertyRecord[]{
                    new PropertyRecord("Makespan (s)", max + ""),
                    new PropertyRecord("Cumulated CPU time (s)", cpuTime + ""),
                    new PropertyRecord("Speed-up", (cpuTime / (float) max) + ""),
                    new PropertyRecord("Efficiency", (cpuTime / (float) sequentialTime) + ""),
                    new PropertyRecord("Mean waiting time", (waitingTime / (float) nbJobs) + "")
                };
                grid.setData(data);
            }
        };
    }

    private Runnable getColumnChartRunnable(final List<String> result,
            final String xAxis, final String color, final int binSize) {

        return new Runnable() {

            public void run() {

                DataTable dataTable = DataTable.create();
                dataTable.addColumn(ColumnType.NUMBER, "Time");
                dataTable.addColumn(ColumnType.NUMBER, "Jobs");

                dataTable.addRows(result.size());

                List<Integer> rangesList = new ArrayList<Integer>();
                List<Integer> numJobsList = new ArrayList<Integer>();
                int maxRange = 0;
                int maxNumJobs = 0;
                int min = Integer.MAX_VALUE;
                int max = 0;
                int sum = 0;
                int count = 0;
                data = new StringBuilder();
                
                for (String values : result) {
                    data.append(values.replace("##", ","));
                    data.append("\n");                   
                    String[] res = values.split("##");
                    
                    int range = new Integer(res[0]);
                    rangesList.add(range);
                    if (range > maxRange) {
                        maxRange = range;
                    }
                    if (res.length > 1) {
                        int numJobs = new Integer(res[1]);
                        numJobsList.add(numJobs);
                        if (numJobs > maxNumJobs) {
                            maxNumJobs = numJobs;
                        }
                        if (res.length > 2) {
                            if (new Integer(res[2]) < min) {
                                min = new Integer(res[2]);
                            }
                            if (res.length > 3) {
                                if (new Integer(res[3]) > max) {
                                    max = new Integer(res[3]);
                                }
                                if (res.length > 4) {
                                    sum += new Integer(res[4]);
                                }
                            }
                        }
                        count += new Integer(res[1]);
                    }
                }
            }
        };
    }

    private void buildCkptChart(List<String> result) {

        ChartData chartData = new ChartData();
        chartData.setBackgroundColour("#ffffff");

        StackedBarChart stack = new StackedBarChart();
        int max = 0;
        int occ_completed = 0;
        int occ_error = 0;
        int occ_stalled = 0;
        int occ_cancelled = 0;
        int nb_jobs = 0;
        int failed_jobs = 0;


        this.data = new StringBuilder();
        for (String values : result) {
            this.data.append(values + "\n");
            Stack s = new Stack();
            String[] v = values.split("##");

            int nb_occ = new Integer(v[1]);

            if (v[0].equals("COMPLETED")) {
                s.addStackValues(new StackedBarChart.StackValue(nb_occ, "#009966"));
                occ_completed = occ_completed + nb_occ;
                nb_jobs++;
            } else {
                if (v[0].equals("ERROR")) {
                    s.addStackValues(new StackedBarChart.StackValue(nb_occ, "#CC0033"));
                    occ_error = occ_error + nb_occ;
                    failed_jobs++;
                    nb_jobs++;
                } else {
                    if (v[0].equals("STALLED")) {
                        s.addStackValues(new StackedBarChart.StackValue(nb_occ, "#663366"));
                        occ_stalled = occ_stalled + nb_occ;
                        nb_jobs++;
                        failed_jobs++;
                    } else {
                        if (v[0].equals("CANCELLED")) {
                            s.addStackValues(new StackedBarChart.StackValue(nb_occ, "#FF9933"));
                            occ_cancelled = occ_cancelled + nb_occ;
                            nb_jobs++;
                        }
                    }
                }
            }
            stack.addStack(s);

            if (nb_occ > max) {
                max = nb_occ;
            }
        }

        stack.setKeys(
                new Keys("Completed", "#009966", 9),
                new Keys("Error", "#CC0033", 9),
                new Keys("Stalled", "#663366", 9),
                new Keys("Cancelled", "#FF9933", 9));


        chartData.addElements(stack);

        XAxis xa = new XAxis();
        xa.setRange(0, nb_jobs, (nb_jobs / 10));
        chartData.setXAxis(xa);
        chartData.setXLegend(new Text("Jobs", "{font-size: 10px; color: #000000}"));

        YAxis ya = new YAxis();
        ya.setRange(0, max, (max / 10));
        chartData.setYAxis(ya);
        chartData.setYLegend(new Text("Number of checkpoints", "{font-size: 10px; color: #000000}"));

        if (chart == null) {
            configureGrid();
        }

        PropertyRecord[] data = new PropertyRecord[]{
            new PropertyRecord("Total ckpts for completed jobs", occ_completed + ""),
            new PropertyRecord("Total ckpts for error jobs", occ_error + ""),
            new PropertyRecord("Total ckpts for stalled jobs", occ_stalled + ""),
            new PropertyRecord("Total ckpts for cancelled jobs", occ_cancelled + ""),
            new PropertyRecord("Failure rate", (failed_jobs / (float) nb_jobs) + "")
        };

        grid.setData(data);
        chart.setChartData(chartData);
    }

    /**
     * 
     * @param result
     * @param xAxis
     * @param color
     */
    private void buildBarChartAndGrid(List<String> result, String xAxis, String color, int binSize) {

        List<Integer> rangesList = new ArrayList<Integer>();
        List<Integer> numJobsList = new ArrayList<Integer>();
        int maxRange = 0;
        int maxNumJobs = 0;
        int min = Integer.MAX_VALUE;
        int max = 0;
        int sum = 0;
        int count = 0;
        this.data = new StringBuilder();
        for (String s : result) {
            this.data.append(s + "\n");
            String[] res = s.split("##");
            int range = new Integer(res[0]);
            rangesList.add(range);
            if (range > maxRange) {
                maxRange = range;
            }
            if (res.length > 1) {
                int numJobs = new Integer(res[1]);
                numJobsList.add(numJobs);
                if (numJobs > maxNumJobs) {
                    maxNumJobs = numJobs;
                }
                if (res.length > 2) {
                    if (new Integer(res[2]) < min) {
                        min = new Integer(res[2]);
                    }
                    if (res.length > 3) {
                        if (new Integer(res[3]) > max) {
                            max = new Integer(res[3]);
                        }
                        if (res.length > 4) {
                            sum += new Integer(res[4]);
                        }
                    }
                }
                count += new Integer(res[1]);
            }
        }

        ChartData chartData = new ChartData();
        chartData.setBackgroundColour("#ffffff");

        // XAxis
        XAxis xa = new XAxis();
        int rangeLabel = 0;
        int index = 0;
        List<Integer> indexesList = new ArrayList<Integer>();
        for (Integer i : rangesList) {
            while (rangeLabel < i) {
                xa.addLabels(new Label(rangeLabel + "", 45));
                indexesList.add(index++);
                rangeLabel += binSize;
            }
            if (rangeLabel == i) {
                xa.addLabels(new Label(i + "", 45));
                index++;
                rangeLabel += binSize;
            }
        }
        chartData.setXAxis(xa);
        chartData.setXLegend(new Text(xAxis, "{font-size: 10px; color: #000000}"));

        // YAxis
        YAxis ya = new YAxis();
        ya.setSteps(maxNumJobs / 10);
        ya.setMax(maxNumJobs);
        chartData.setYAxis(ya);
        chartData.setYLegend(new Text("Number of Jobs", "{font-size: 10px; color: #000000}"));

        BarChart bchart = new BarChart(BarStyle.GLASS);
        bchart.setBarwidth(.5);
        bchart.setColour(color);
        bchart.setTooltip("#val# jobs");
        int j = 0;
        for (int i = 0; i < index; i++) {
            if (indexesList.contains(i)) {
                bchart.addValues(0);
            } else {
                bchart.addValues(numJobsList.get(j++));
            }
        }
        chartData.addElements(bchart);
        chartData.setTooltipStyle(new ToolTip(MouseStyle.FOLLOW));

        PropertyRecord[] data;
        if (result.size() > 0) {
            data = new PropertyRecord[]{
                new PropertyRecord("Min (s)", min + ""),
                new PropertyRecord("Max (s)", max + ""),
                new PropertyRecord("Cumulated (s)", sum + ""),
                new PropertyRecord("Average (s)", (sum / (float) count) + "")
            };
        } else {
            data = new PropertyRecord[]{
                new PropertyRecord("Min (s)", "0"),
                new PropertyRecord("Max (s)", "0"),
                new PropertyRecord("Cumulated (s)", "0"),
                new PropertyRecord("Average (s)", "0")
            };
        }

        if (chart == null) {
            configureGrid();
        }
        grid.setData(data);
        chart.setChartData(chartData);


    }

    private void configureGrid() {

        if (grid == null) {

            grid = new ListGrid();
            grid.setWidth(300);
            grid.setHeight(140);
            grid.setShowAllRecords(true);
            grid.setShowEmptyMessage(true);
            grid.setEmptyMessage("<br>No data available.");

            ListGridField propertyField = new ListGridField("property", "Properties");
            ListGridField valueField = new ListGridField("value", "Value");

            grid.setFields(propertyField, valueField);

            tileGrid = new TileGrid();
            tileGrid.setWidth100();
            tileGrid.setHeight100();
            tileGrid.setTileWidth(110);
            tileGrid.setTileHeight(80);
            tileGrid.setShowAllRecords(true);
            tileGrid.setShowEdges(false);

            DetailViewerField pictureField = new DetailViewerField("picture");
            pictureField.setType("image");
            DetailViewerField commonNameField = new DetailViewerField("commonName");

            tileGrid.setFields(pictureField, commonNameField);

            tileGrid.addRecordClickHandler(new RecordClickHandler() {

                public void onRecordClick(RecordClickEvent event) {
                    new ViewerWindow("Data", simulationID,
                            data.toString()).show();
                }
            });
            tileGrid.setData(new ApplicationTileRecord[]{
                        new ApplicationTileRecord("Data", ApplicationConstants.APP_IMG_SIMULATION_OUT),});

            mainLayout.addMember(grid);
            mainLayout.addMember(tileGrid);
        }
    }
}