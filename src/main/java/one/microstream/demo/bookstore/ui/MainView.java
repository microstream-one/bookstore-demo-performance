
package one.microstream.demo.bookstore.ui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.Timer;

import org.apache.commons.lang3.mutable.MutableInt;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rapidclipse.framework.server.charts.AbstractChart;
import com.rapidclipse.framework.server.charts.Axis;
import com.rapidclipse.framework.server.charts.Background;
import com.rapidclipse.framework.server.charts.Chart;
import com.rapidclipse.framework.server.charts.ChartArea;
import com.rapidclipse.framework.server.charts.ChartModel;
import com.rapidclipse.framework.server.charts.Column;
import com.rapidclipse.framework.server.charts.Column.Role;
import com.rapidclipse.framework.server.charts.Column.Type;
import com.rapidclipse.framework.server.charts.Explorer;
import com.rapidclipse.framework.server.charts.GridLines;
import com.rapidclipse.framework.server.charts.HasBackground;
import com.rapidclipse.framework.server.charts.HasChartSize;
import com.rapidclipse.framework.server.charts.HasExplorer;
import com.rapidclipse.framework.server.charts.Legend;
import com.rapidclipse.framework.server.charts.Selection;
import com.rapidclipse.framework.server.charts.SelectionEvent;
import com.rapidclipse.framework.server.charts.SelectionMode;
import com.rapidclipse.framework.server.charts.TextPosition;
import com.rapidclipse.framework.server.charts.TextStyle;
import com.rapidclipse.framework.server.charts.Tooltip;
import com.rapidclipse.framework.server.charts.area.AreaChart;
import com.rapidclipse.framework.server.charts.line.LineChart;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.ThemableLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.selection.MultiSelect;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PageConfigurator;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import one.microstream.bytes.ByteMultiple;
import one.microstream.demo.bookstore.BookStoreDemo;
import one.microstream.demo.bookstore.app.Action;
import one.microstream.demo.bookstore.app.ActionExecutor;
import one.microstream.demo.bookstore.app.ClearAction;
import one.microstream.demo.bookstore.app.ExecutionCallback;
import one.microstream.demo.bookstore.app.QueryAction;
import one.microstream.demo.bookstore.app.QueryStats;
import one.microstream.demo.bookstore.jpa.dal.Repositories;


@Route
@Push
@Theme(value = Lumo.class, variant = Lumo.DARK)
@HtmlImport("styles/shared-styles.html")
public class MainView extends VerticalLayout implements PageConfigurator, ExecutionCallback
{
	private final static class MemoryStats
	{
		final LocalDateTime timestamp;
		final MemoryUsage   heapMemoryUsage;
		final MemoryUsage   nonHeapMemoryUsage;

		MemoryStats()
		{
			final MemoryMXBean bean = ManagementFactory.getMemoryMXBean();
			this.timestamp          = LocalDateTime.now();
			this.heapMemoryUsage    = bean.getHeapMemoryUsage();
			this.nonHeapMemoryUsage = bean.getNonHeapMemoryUsage();
		}
	}

	private final static class QueryEntry
	{
		final QueryAction action;

		QueryEntry(
			final QueryAction action
		)
		{
			super();
			this.action = action;
		}

		String description()
		{
			return this.action.description();
		}

		String microStreamTime(
			final QueryTimeUnit unit,
			final QueryAction runningQuery
		)
		{
			final QueryStats stats = this.action.queryStats();
			return stats == null
				? runningQuery == this.action ? "..." : ""
				: this.getTime(stats.msNanos(), unit);
		}

		String jpaTime(
			final QueryTimeUnit unit,
			final QueryAction runningQuery
		)
		{
			final QueryStats stats = this.action.queryStats();
			return stats == null
				? runningQuery == this.action ? "..." : ""
				: this.getTime(stats.jpaNanos(), unit);
		}

		String getTime(
			final long nanos,
			final QueryTimeUnit unit
		)
		{
			switch(unit)
			{
				case Millis:
					return decimalFormat(2).format(divide(nanos,  NANOS_PER_MILLI));

				case Seconds:
					return decimalFormat(4).format(divide(nanos,  NANOS_PER_SECOND));

				default:
					return integerFormat().format(nanos);
			}
		}

		String factor()
		{
			final QueryStats stats = this.action.queryStats();
			return stats == null
				? ""
				: decimalFormat(2).format(divide(stats.jpaNanos(), stats.msNanos()));
		}
	}

	private static enum ExportFormat
	{
		CSV,
		JSON
	}

	private static enum QueryTimeUnit
	{
		Nanos,
		Millis,
		Seconds
	}

	private static final long   NANOS_PER_SECOND  = 1_000_000_000L;
	private static final long   NANOS_PER_MILLI   = 1_000_000L;

	private static final String TEXT_COLOR        = "#D8E1EE";
	private static final String MICROSTREAM_COLOR = "#95D3FA";
	private static final String JPA_COLOR         = "#EF7E0C";

	private final BookStoreDemo  bookStoreDemo;
	private final Repositories   repositories;
	private final Query[]        queries;
	private final ActionExecutor actionExecutor;

	private AreaChart                    heapChart, nonHeapChart;
	private Grid<QueryEntry>             queryGrid;
	private ListDataProvider<QueryEntry> queryDataProvider;
	private List<QueryEntry>             queryEntries;
	private List<QueryEntry>             queryEntriesForProvider;
	private QueryAction                  runningQuery;
	private final Timer                  queryGridTimer;
	private LineChart                    diffChart, msChart, jpaChart;
	private final AtomicBoolean          clearMemoryStatistics = new AtomicBoolean(false);
	private final List<MemoryStats>      memoryStats;
	private ByteMultiple                 memoryUnit       = ByteMultiple.GB;
	private final AtomicBoolean          randomQueries    = new AtomicBoolean(false);
	private final List<QueryEntry>       queryStatistics;
	private QueryTimeUnit                queryTimeUnit    = QueryTimeUnit.Seconds;
	private final Timer                  queryChartTimer;


	public MainView(
		final BookStoreDemo bookStoreDemo,
		final Repositories repositories
	)
	{
		this.bookStoreDemo  = bookStoreDemo;
		this.repositories   = repositories;
		this.queries        = Query.All(bookStoreDemo.data(), repositories);
		this.actionExecutor = ActionExecutor.New(this);

		final VerticalLayout settingsLayout = new VerticalLayout(
			this.createDetails("Queries", this.createQueriesControl()),
			this.createDetails("Java VM", this.createJavaVMControl()),
			this.createDetails("MicroStream", this.createMicroStreamControl()),
			this.createDetails("JPA", this.createJPAControl()),
			this.createDetails("Statistics", this.createStatisticsControl())
		);
		settingsLayout.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);

		final VerticalLayout graphControl = this.createGraphControl();
		graphControl.setSizeFull();

		final SplitLayout centerLayout = new SplitLayout(
			compact(settingsLayout),
			compact(graphControl)
		);
		centerLayout.setSplitterPosition(30);
		centerLayout.setSizeFull();
		settingsLayout.getStyle().set("margin-right", "10px");
		graphControl.getStyle().set("margin-left", "10px");

		this.setSizeFull();
		this.add(this.createBanner(), new Hr());
		this.add(centerLayout);
		this.setHorizontalComponentAlignment(Alignment.STRETCH, centerLayout);

		this.memoryStats = new ArrayList<>();

		final Timer memoryTimer = new Timer(1000, event -> this.updateMemoryCharts());
		memoryTimer.setRepeats(true);

		this.queryStatistics = new ArrayList<>();

		this.queryGridTimer = new Timer(1000, event -> this.updateQueryGrid(this.runningQuery));
		this.queryGridTimer.setRepeats(false);

		this.queryChartTimer = new Timer(1000, event -> this.updateQueryCharts());
		this.queryChartTimer.setRepeats(false);

		this.addAttachListener(event ->
		{
			this.actionExecutor.start();
			memoryTimer.start();
		});
		this.addDetachListener(event ->
		{
			this.actionExecutor.clearQueue();
			this.actionExecutor.shutdown();
			memoryTimer.stop();
		});
	}

	@Override
	public void configurePage(
		final InitialPageSettings settings
	)
	{
		settings.addLink   ("shortcut icon", "frontend/images/icon.ico"           );
		settings.addFavIcon("icon"         , "frontend/images/icon.png", "256x256");
	}

	private Component createBanner()
	{
		final Label label = new Label();
		label.getElement().setProperty("innerHTML", "&bull; BookStore Performance Demo &bull;");
		label.getStyle()
			.set("font-size", "120%")
			.set("font-weight", "bold");

		final HorizontalLayout banner = new HorizontalLayout(
			new Image("frontend/images/logo.png", "Logo"),
			label);
		banner.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		return compact(banner);
	}

	private Component createQueriesControl()
	{
		final Select<Query> cmbQuery = new Select<>(this.queries);
		cmbQuery.setValue(this.queries[0]);

		final IntegerField txtIterations = new IntegerField();
		txtIterations.setHasControls(true);
		txtIterations.setAutocorrect(true);
		txtIterations.setValue(3);
		txtIterations.setMin(1);
		txtIterations.setMax(10000);

		final Button cmdSubmit = new Button(
			"Schedule",
			event -> this.submitQuery(cmbQuery.getValue(), txtIterations.getValue())
		);
		cmdSubmit.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

		final Checkbox chkRandom = new Checkbox("Random", false);
		chkRandom.addValueChangeListener(event -> {
			final boolean random = event.getValue();
			this.randomQueries.set(random);
			this.optRandomizeQueries();
			this.accessUI(() -> {
				cmbQuery.setEnabled(!random);
				txtIterations.setEnabled(!random);
				cmdSubmit.setEnabled(!random);
			});
		});

		final HorizontalLayout buttonsLayout = compact(new HorizontalLayout(cmdSubmit, chkRandom));
		buttonsLayout.setDefaultVerticalComponentAlignment(Alignment.BASELINE);

		final FormLayout form = new FormLayout();
		form.setResponsiveSteps(new ResponsiveStep(null, 1));
		form.addFormItem(cmbQuery, "Query");
		form.addFormItem(txtIterations, "Iterations");
		form.addFormItem(buttonsLayout, "");
		return form;
	}

	private Component createJavaVMControl()
	{
		return this.createClearActionControl(ClearAction.GarbageCollector());
	}

	private Component createMicroStreamControl()
	{
		final Component lazyRefsAndObjectCacheControl = this.createClearActionControl(
			ClearAction.LazyRefsAndObjectCache(this.bookStoreDemo.storageManager())
		);
		final Component storageCacheControl           = this.createClearActionControl(
			ClearAction.StorageCache(this.bookStoreDemo.storageManager())
		);
		return compact(new VerticalLayout(lazyRefsAndObjectCacheControl, storageCacheControl));
	}

	private Component createJPAControl()
	{
//		final Component sessionCacheControl     = this.createClearActionControl(
//			ClearAction.SessionCache(this.repositories.sessionFactory())
//		);
		final Component secondLevelCacheControl = this.createClearActionControl(
			ClearAction.SecondLevelCache(this.repositories.sessionFactory())
		);
		return compact(new VerticalLayout(
//			sessionCacheControl,
			secondLevelCacheControl
		));
	}

	private Component createClearActionControl(final ClearAction action)
	{
		final IntegerField txtInterval = new IntegerField();
		txtInterval.setHasControls(true);
		txtInterval.setAutocorrect(true);
		txtInterval.setValue(10);
		txtInterval.setMin(1);
		txtInterval.setMax(10000);

		final Checkbox chkInterval = new Checkbox(action.verb() + " every", false);
		chkInterval.addValueChangeListener(event ->
		{
			if(event.getValue())
			{
				this.scheduleClearAction(action, txtInterval.getValue());
			}
			else
			{
				this.unScheduleClearAction(action);
			}
		});

		final Button cmdRunNow = new Button(action.verb() + " Now", event -> this.submitClearAction(action));
		cmdRunNow.addThemeVariants(ButtonVariant.LUMO_ERROR);

		final HorizontalLayout controls = new HorizontalLayout(
			chkInterval, txtInterval, new Label("queries"), cmdRunNow
		);
		controls.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
		controls.setPadding(false);
		controls.setMargin(false);

		final VerticalLayout layout = new VerticalLayout(new Label(action.title()), controls);
		layout.setPadding(false);
		layout.setMargin(false);
		return layout;
	}

	private VerticalLayout createStatisticsControl()
	{
		final NumberFormat format = NumberFormat.getIntegerInstance();
		format.setGroupingUsed(true);

		final String bookCount      = format.format(this.repositories.bookRepository().count());
		final String authorCount    = format.format(this.repositories.authorRepository().count());
		final String publisherCount = format.format(this.repositories.publisherRepository().count());
		final String shopCount      = format.format(this.repositories.shopRepository().count());
		final String cityCount      = format.format(this.repositories.cityRepository().count());
		final String countryCount   = format.format(this.repositories.countryRepository().count());
		final String itemCount      = format.format(this.repositories.purchaseItemRepository().count());
		final String purchaseCount  = format.format(this.repositories.purchaseRepository().count());

		return compact(new VerticalLayout(
			new Label(bookCount + " different books of " + authorCount + " authors"),
			new Label("sold by " + publisherCount + " publishers"),
			new Label(shopCount + " shops in " + cityCount + " cities in " + countryCount + " countries"),
			new Label(itemCount + " items"),
			new Label("in " + purchaseCount + " purchases")
		));
	}

	private VerticalLayout createGraphControl()
	{
		final Button cmdClearMemory = new Button("Clear Statistics", event -> this.clearMemoryStatistics.set(true));
		cmdClearMemory.addThemeVariants(ButtonVariant.LUMO_ERROR);

		final Select<ByteMultiple> cmbMemoryUnit = new Select<>(ByteMultiple.MB, ByteMultiple.GB);
		cmbMemoryUnit.setValue(this.memoryUnit);
		cmbMemoryUnit.addValueChangeListener(event -> {
			this.memoryUnit = cmbMemoryUnit.getValue();
		});

		final HorizontalLayout memoryToolBar = new HorizontalLayout(
			new Label("Unit:"), cmbMemoryUnit, cmdClearMemory
		);
		memoryToolBar.setDefaultVerticalComponentAlignment(Alignment.BASELINE);

		this.heapChart    = this.createMemoryChart();
		this.nonHeapChart = this.createMemoryChart();

		this.queryGrid = new Grid<>();
		this.queryGrid.addColumn(QueryEntry::description).setHeader("Query").setResizable(true);
		this.queryGrid.addColumn(
			e -> e.microStreamTime(this.queryTimeUnit, this.runningQuery)
		).setHeader("MicroStream").setResizable(true);
		this.queryGrid.addColumn(
			e -> e.jpaTime(this.queryTimeUnit, this.runningQuery)
		).setHeader("JPA").setResizable(true);
		this.queryGrid.addColumn(QueryEntry::factor).setHeader("Factor").setResizable(true);
		this.queryEntries = new ArrayList<>();
		this.queryEntriesForProvider = new ArrayList<>();
		this.queryDataProvider = DataProvider.ofCollection(this.queryEntries);
		this.queryGrid.setDataProvider(this.queryDataProvider);
		this.queryGrid.setHeight("200px");
		this.queryGrid.setWidth("100%");

		this.diffChart = this.createQueryChart("MicroStream", "JPA");
		this.msChart   = this.createQueryChart("time");
		this.jpaChart  = this.createQueryChart("time");

		this.installQueryControlsSelectionSynchronization();

		this.diffChart.setColors(Arrays.asList(MICROSTREAM_COLOR, JPA_COLOR));
		this.msChart.setColors(Arrays.asList(MICROSTREAM_COLOR));
		this.jpaChart.setColors(Arrays.asList(JPA_COLOR));

		final VerticalLayout heapLayout = new VerticalLayout(new Label("Heap"),this.heapChart);
		final VerticalLayout nonHeapLayout = new VerticalLayout(new Label("Non-Heap"),this.nonHeapChart);

		final HorizontalLayout memoryChartLayout = new HorizontalLayout(
			compact(heapLayout),
			compact(nonHeapLayout)
		);
		heapLayout.setWidth("50%");
		nonHeapLayout.setWidth("50%");

		final VerticalLayout memoryLayout = new VerticalLayout(
			compact(memoryToolBar),
			compact(memoryChartLayout)
		);
		memoryLayout.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
		memoryLayout.setWidth("100%");

		final Details memoryPanel = this.createDetails("Memory", compact(memoryLayout));
		final Details queuePanel  = this.createDetails("Queue", this.queryGrid);
		final Details diffPanel   = this.createDetails("Diff", this.diffChart);
		final Details msPanel     = this.createDetails("MicroStream", this.msChart);
		final Details jpaPanel    = this.createDetails("JPA", this.jpaChart);

		final Button cmdClearQueryStatistics = new Button("Clear Statistics", event -> this.clearQueryStatistics());
		cmdClearQueryStatistics.addThemeVariants(ButtonVariant.LUMO_ERROR);

		final Button cmdClearQueryQueue = new Button("Cancel Scheduled Queries", event -> this.clearSubmittedQueries());
		cmdClearQueryQueue.addThemeVariants(ButtonVariant.LUMO_ERROR);

		final Select<QueryTimeUnit> cmbQueryTimeUnit = new Select<>(QueryTimeUnit.values());
		cmbQueryTimeUnit.setValue(this.queryTimeUnit);
		cmbQueryTimeUnit.addValueChangeListener(event -> {
			this.queryTimeUnit = cmbQueryTimeUnit.getValue();
			this.queryGridTimer.restart();
			this.queryChartTimer.restart();
		});

		final HorizontalLayout queryToolBar = compact(new HorizontalLayout(
			new Label("Unit:"), cmbQueryTimeUnit, cmdClearQueryStatistics, cmdClearQueryQueue, this.createExportMenu()
		));
		queryToolBar.setDefaultVerticalComponentAlignment(Alignment.BASELINE);

		final VerticalLayout queriesLayout = new VerticalLayout(
			queryToolBar, queuePanel, diffPanel, msPanel, jpaPanel
		);
		queriesLayout.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
		queriesLayout.setWidth("100%");

		final Details queriesPanel = this.createDetails("Queries", compact(queriesLayout));

		final VerticalLayout layout = new VerticalLayout(
			memoryPanel,
			queriesPanel);
		layout.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
		layout.setSizeFull();
		return compact(layout);
	}

	private Component createExportMenu()
	{
		final MenuBar menuBar = new MenuBar();
        final MenuItem exportItem = menuBar.addItem("Export");

        for(final ExportFormat format : ExportFormat.values())
        {
	        final Anchor download = new Anchor(new StreamResource(
	        		"queries." + format.name().toLowerCase(),
	        		() -> this.exportQueries(format)
	        	),
	        	""
	        );
	        download.getElement().setAttribute("download", true);
	        final Button button = new Button(format.name(), VaadinIcon.DOWNLOAD_ALT.create());
	        button.addThemeVariants(ButtonVariant.LUMO_SMALL);
			download.add(button);
	        exportItem.getSubMenu().addItem(download);
        }

        return menuBar;
	}

	private InputStream exportQueries(final ExportFormat format)
	{
		String content = "";

		synchronized(this.queryEntries)
		{
			switch(format)
			{
				case CSV:
				{
					content = this.queryEntries.stream()
						.map(this::toCsvLine)
						.collect(Collectors.joining(
							"\n",
							"Query,MicroStream,JPA,Factor\n",
							""));
				}
				break;

				case JSON:
				{
					final JsonArray array = new JsonArray();
					this.queryEntries.stream()
						.map(this::toJson)
						.forEach(array::add);
					content = new Gson().newBuilder()
						.setPrettyPrinting()
						.create()
						.toJson(array);
				}
				break;
			}
		}

		return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
	}

	private String toCsvLine(final QueryEntry e)
	{
		return Arrays.asList(
				e.description(),
				e.microStreamTime(this.queryTimeUnit, null),
				e.jpaTime(this.queryTimeUnit, null),
				e.factor()
			)
			.stream()
			.map(this::csvValue)
			.collect(Collectors.joining(","));
	}

	private String csvValue(final String value)
	{
		return '"' + value.replace("\"", "\\\"") + '"';
	}

	private JsonObject toJson(final QueryEntry e)
	{
		final JsonObject obj = new JsonObject();
		obj.addProperty("query", e.description());
		obj.addProperty("microstream", e.microStreamTime(this.queryTimeUnit, null));
		obj.addProperty("jpa", e.jpaTime(this.queryTimeUnit, null));
		obj.addProperty("factor", e.factor());
		return obj;
	}

	private void installQueryControlsSelectionSynchronization()
	{
		this.queryGrid.setSelectionMode(Grid.SelectionMode.MULTI);
		this.diffChart.setSelectionMode(SelectionMode.MULTIPLE);
		this.jpaChart.setSelectionMode(SelectionMode.MULTIPLE);
		this.msChart.setSelectionMode(SelectionMode.MULTIPLE);

		final MultiSelect<Grid<QueryEntry>, QueryEntry> gridSelect = this.queryGrid.asMultiSelect();
		gridSelect.addValueChangeListener(event ->
		{
			if(event.isFromClient())
			{
				final Selection selection;
				synchronized(this.queryEntries)
				{
					selection = Selection.New(
						event.getValue().stream()
							.map(this.queryEntries::indexOf)
							.filter(index -> index != -1)
							.map(index -> Selection.Item(index, null))
							.collect(Collectors.toList())
					);
				}
				this.accessUI(()-> {
					this.diffChart.setSelection(selection);
					this.jpaChart.setSelection(selection);
					this.msChart.setSelection(selection);
				});
			}
		});

		final ComponentEventListener<SelectionEvent<AbstractChart>> chartSelectionListener = event ->
		{
			if(event.isFromClient())
			{
				Set<QueryEntry> gridSelection;
				Selection chartSelection;
				synchronized(this.queryEntries)
				{
					gridSelection = event.getSelection().items().stream()
						.map(item -> this.queryEntries.get(item.row()))
						.collect(Collectors.toSet());
					chartSelection = Selection.New(
						event.getSelection().items().stream()
							.map(item -> item.row())
							.distinct()
							.map(row -> Selection.Item(row, null))
							.collect(Collectors.toList())
					);
				}
				final int scrollToIndex;
				synchronized(this.queryEntriesForProvider)
				{
					scrollToIndex = gridSelection.size() > 0
						? this.queryEntriesForProvider.indexOf(gridSelection.iterator().next())
						: -1;
				}
				this.accessUI(()-> {
					gridSelect.setValue(gridSelection);
					if(scrollToIndex != -1)
					{
						this.queryGrid.scrollToIndex(scrollToIndex);
					}
					this.diffChart.setSelection(chartSelection);
					this.jpaChart.setSelection(chartSelection);
					this.msChart.setSelection(chartSelection);
				});
			}
		};
		this.diffChart.addSelectionListener(chartSelectionListener);
		this.jpaChart.addSelectionListener(chartSelectionListener);
		this.msChart.addSelectionListener(chartSelectionListener);
	}

	private AreaChart createMemoryChart()
	{
		final AreaChart chart = new AreaChart();
		chart.initDefaultColumnsContinuous("t", Type.NUMBER)
			.addColumn(Column.New(Type.NUMBER, "commited"))
			.addColumn(Column.New(Type.STRING, "comittedTooltip", Role.TOOLTIP))
			.addColumn(Column.New(Type.NUMBER, "used"))
			.addColumn(Column.New(Type.STRING, "usedTooltip", Role.TOOLTIP));
		chart.setLegend(Legend.None());
		chart.setHAxis(null);
		chart.setVAxis(this.axisBuilder()
			.gridlines(GridLines.New("#787878"))
			.minorGridlines(GridLines.New("#565656"))
			.textPosition(TextPosition.IN)
			.build()
		);
		chart.setChartHeight(150);
		chart.setChartArea(ChartArea.New("0", "0", "100%", "100%"));
		chart.setColors(Arrays.asList("#444903","#A8BA09"));
		return this.configureChart(chart);
	}

	private LineChart createQueryChart(final String... valueColumns)
	{
		final LineChart chart = new LineChart();
		chart.initDefaultColumnsContinuous("query", Type.NUMBER);
		for(final String valueColumn : valueColumns)
		{
			chart.getModel().addColumn(Column.New(Type.NUMBER, valueColumn));
			chart.getModel().addColumn(Column.Builder()
				.type(Type.STRING)
				.id(valueColumn + "Tooltip")
				.role(Role.TOOLTIP)
				.property("html", true)
				.build());
		}
		if(valueColumns.length == 1)
		{
			chart.setLegend(Legend.None());
			chart.setChartArea(ChartArea.New("100", "10", "100%", "180"));
		}
		else
		{
			chart.setLegend(Legend.Builder()
				.textStyle(TextStyle.New(TEXT_COLOR))
				.position(Legend.Position.TOP)
				.build()
			);
			chart.setChartArea(ChartArea.New("100", null, "100%", null));
		}
		chart.setChartHeight(200);
		chart.setTooltip(Tooltip.Builder()
			.isHtml(true)
			.textStyle(TextStyle.New("black"))
			.build()
		);
		chart.setHAxis(this.queryChartHAxisBuilder().build());
		chart.setVAxis(this.axisBuilder()
			.gridlines(GridLines.New("#787878"))
			.minorGridlines(GridLines.New("#565656"))
			.build()
		);
		chart.setPointSize(5);
		return this.configureChart(chart);
	}

	private <C extends Chart & HasChartSize & HasBackground & HasExplorer> C configureChart(
		final C chart
	)
	{
		chart.setWidth("100%");
		chart.setExplorer(Explorer.Builder().axis(Explorer.Axis.HORIZONTAL).build());
		chart.setBackground(Background.Color("transparent"));
		return chart;
	}

	private Axis.Builder queryChartHAxisBuilder()
	{
		return this.axisBuilder()
			.gridlines(GridLines.New("transparent"))
			.textPosition(TextPosition.NONE);
	}

	private Axis.Builder axisBuilder()
	{
		return Axis.Builder()
			.textStyle(TextStyle.New(TEXT_COLOR))
			.titleTextStyle(TextStyle.New(TEXT_COLOR));
	}

	private Details createDetails(
		final String summary,
		final Component content
	)
	{
		final Details details = new Details(summary, content);
		details.setOpened(true);
		details.addThemeVariants(DetailsVariant.FILLED);
		return details;
	}

	private void submitQuery(final Query query, final int iterations)
	{
		query.actionSubmitter().accept(this.actionExecutor, iterations);
	}

	private void clearSubmittedQueries()
	{
		this.actionExecutor.clearQueue();
	}

	private void submitClearAction(final ClearAction action)
	{
		this.actionExecutor.submit(action);
	}

	private void scheduleClearAction(final ClearAction action, final int queryInterval)
	{
		this.actionExecutor.schedule(action, queryInterval);
	}

	private void unScheduleClearAction(final ClearAction action)
	{
		this.actionExecutor.unschedule(action);
	}

	@Override
	public void beforeExecution(
		final Action action
	)
	{
		if(action instanceof QueryAction)
		{
			this.runningQuery = (QueryAction)action;
			this.queryGridTimer.start();
		}
	}

	@Override
	public void afterExecution(
		final Action action
	)
	{
		if(action instanceof QueryAction)
		{
			synchronized(this.queryStatistics)
			{
				this.queryStatistics.add(new QueryEntry((QueryAction)action));
			}

			this.runningQuery = null;
			this.queryGridTimer.start();
			this.queryChartTimer.start();
		}
		else if(action instanceof ClearAction)
		{
			final ClearAction clearAction = (ClearAction)action;
			final String      verb        = clearAction.verb();
			final String      title       = clearAction.title();
			final String      text        = title + " " + verb + (verb.endsWith("e") ? "d" : "ed");
			this.accessUI(() ->
				Notification.show(text, 2500, Notification.Position.BOTTOM_START)
			);
		}
	}

	@Override
	public void queueUpdated()
	{
		this.optRandomizeQueries();

		this.queryGridTimer.start();
	}

	private synchronized void optRandomizeQueries()
	{
		if(!this.randomQueries.get() || this.actionExecutor.submittedQueries().size() > 0)
		{
			return;
		}

		final Random random     = new Random();
		final Query  query      = this.queries[random.nextInt(this.queries.length)];
		final int    iterations = 1 + random.nextInt(10);
		this.submitQuery(query, iterations);
	}

	private void updateMemoryCharts()
	{
		if(this.clearMemoryStatistics.getAndSet(false))
		{
			this.memoryStats.clear();
		}

		this.memoryStats.add(new MemoryStats());

		if(this.memoryStats.size() % 3 == 0)
		{
			this.updateMemoryChart(this.heapChart   , this.memoryUnit, s -> s.heapMemoryUsage   );
			this.updateMemoryChart(this.nonHeapChart, this.memoryUnit, s -> s.nonHeapMemoryUsage);

			this.accessUI(() ->
			{
				this.heapChart.refresh();
				this.nonHeapChart.refresh();
			});
		}
	}

	private void updateMemoryChart(
		final AreaChart chart,
		final ByteMultiple memoryUnit,
		final Function<MemoryStats, MemoryUsage> memoryUsageSelector
	)
	{
		final ChartModel        model         = chart.getModel().removeAllRows();
		final NumberFormat      numberFormat  = decimalFormat(2);
		final DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDateTime(
			FormatStyle.SHORT, FormatStyle.MEDIUM);
		int i = 1;
		for(final MemoryStats stats : this.memoryStats)
		{
			final MemoryUsage memoryUsage = memoryUsageSelector.apply(stats);
			final double      commited    = convertMemoryValue(memoryUsage.getCommitted(), memoryUnit);
			final double      used        = convertMemoryValue(memoryUsage.getUsed()     , memoryUnit);
			final String      tooltip     = numberFormat.format(used) + " of " +
				numberFormat.format(commited) + " " + memoryUnit.name() + " used @" +
				dateFormatter.format(stats.timestamp);
			model.addRow(i, commited, tooltip, used, tooltip);
			i++;
		}

		chart.setHAxis(this.axisViewWindow(Axis.Builder(), this.memoryStats.size(), 60));
	}

	private static double convertMemoryValue(final long bytes, final ByteMultiple memoryUnit)
	{
		return ByteMultiple.convert(bytes, ByteMultiple.B).to(memoryUnit);
	}

	private void clearQueryStatistics()
	{
		synchronized(this.queryStatistics)
		{
			this.queryStatistics.clear();
		}

		this.updateQueryGrid(null);
		this.updateQueryCharts();
	}

	private void updateQueryGrid(final QueryAction focusAction)
	{
		final MutableInt focusIndex = new MutableInt(-1);

		synchronized(this.queryEntries)
		{
			this.queryEntries.clear();

			synchronized(this.queryStatistics)
			{
				this.queryEntries.addAll(this.queryStatistics);
			}

			final List<QueryAction> submittedQueries = this.actionExecutor.submittedQueries();
			if(focusAction != null && !(
				submittedQueries.contains(focusAction) ||
				this.queryEntries.stream().anyMatch(e -> e.action == focusAction)
			))
			{
				this.queryEntries.add(new QueryEntry(focusAction));
			}
			submittedQueries.forEach(
				action -> this.queryEntries.add(new QueryEntry(action))
			);

			synchronized(this.queryEntriesForProvider)
			{
				this.queryEntriesForProvider.clear();
				this.queryEntriesForProvider.addAll(this.queryEntries);

				if(focusAction != null)
				{
					final QueryEntry focusEntry = this.queryEntriesForProvider.stream()
						.filter(e -> e.action == focusAction)
						.findAny()
						.orElse(null);
					if(focusEntry != null)
					{
						final int index = this.queryEntriesForProvider.indexOf(focusEntry);
						focusIndex.setValue(index > 0
							? index - Math.min(2, index) // scroll element roughly to center, better UX
							: index);
					}
				}
			}
		}

		this.accessUI(() ->
		{
			this.queryDataProvider.refreshAll();

			final int index = focusIndex.intValue();
			if(index != -1)
			{
				this.queryGrid.scrollToIndex(index);
			}
		});
	}

	private void updateQueryCharts()
	{
		synchronized(this.queryStatistics)
		{
			this.updateQueryChartModels();
		}

		this.accessUI(() ->
		{
			this.msChart.refresh();
			this.jpaChart.refresh();
			this.diffChart.refresh();
		});
	}

	private void updateQueryChartModels()
	{
		final ChartModel msModel   = this.msChart.getModel().removeAllRows();
		final ChartModel jpaModel  = this.jpaChart.getModel().removeAllRows();
		final ChartModel diffModel = this.diffChart.getModel().removeAllRows();

		int i = 1;
		for(final QueryEntry entry : this.queryStatistics)
		{
			final QueryStats stats = entry.action.queryStats();
			final String description  = stats.description();
			final long   msNanos      = stats.msNanos();
			final long   jpaNanos     = stats.jpaNanos();
			final double msSeconds    = divide(msNanos,  NANOS_PER_SECOND);
			final String msSecondsF   = decimalFormat(4).format(msSeconds);
			final double jpaSeconds   = divide(jpaNanos, NANOS_PER_SECOND);
			final String jpaSecondsF  = decimalFormat(4).format(jpaSeconds);
			final double msMillis     = divide(msNanos,  NANOS_PER_MILLI);
			final String msMillisF    = decimalFormat(2).format(msMillis);
			final double jpaMillis    = divide(jpaNanos, NANOS_PER_MILLI);
			final String jpaMillisF   = decimalFormat(2).format(jpaMillis);
			final String factor       = decimalFormat(2).format(divide(jpaNanos, msNanos));
			final String msTooltip    = htmlTooltip(description + "<br>" + msSecondsF  + " seconds<br>" + msMillisF  + " millis");
			final String jpaTooltip   = htmlTooltip(description + "<br>" + jpaSecondsF + " seconds<br>" + jpaMillisF + " millis");
			final String diffTooltip  = htmlTooltip(
				description + "<br>" +
				"<table>" +
				"<tr><th></th><th>MicroStream</th><th>JPA</th><th>Factor</th></tr>" +
				"<tr><th>Seconds</th><td>" + msSecondsF + "</td><td>" + jpaSecondsF + "</td><td rowspan='2'>" + factor + "</td></tr>" +
				"<tr><th>Millis</th><td>" + msMillisF + "</td><td>" + jpaMillisF + "</td></tr>" +
				"</table>"
			);

			double msValue  = 0;
			double jpaValue = 0;
			switch(this.queryTimeUnit)
			{
				case Nanos:
					msValue  = msNanos;
					jpaValue = jpaNanos;
					break;

				case Millis:
					msValue  = msMillis;
					jpaValue = jpaMillis;
					break;

				case Seconds:
					msValue  = msSeconds;
					jpaValue = jpaSeconds;
					break;
			}

			msModel  .addRow(i, msValue,  msTooltip);
			jpaModel .addRow(i, jpaValue, jpaTooltip);
			diffModel.addRow(i, msValue,  diffTooltip,
				                jpaValue, diffTooltip);

			i++;
		}

		final Axis hAxis = this.axisViewWindow(this.queryChartHAxisBuilder(), this.queryStatistics.size(), 10);
		this.diffChart.setHAxis(hAxis);
		this.msChart.setHAxis(hAxis);
		this.jpaChart.setHAxis(hAxis);
	}

	private Axis axisViewWindow(final Axis.Builder axisBuilder, final int size, final int max)
	{
		if(size <= max)
		{
			axisBuilder.viewWindowMin(1).viewWindowMax(max);
		}
		else
		{
			axisBuilder.viewWindowMin(size - max).viewWindowMax(size);
		}
		return axisBuilder.build();
	}

	private void accessUI(final Command command)
	{
		this.getUI().ifPresent(ui -> ui.access(command));
	}

	private static <L extends ThemableLayout> L compact(
		final L layout
	)
	{
		layout.setPadding(false);
		layout.setMargin(false);
		layout.setSpacing(true);
		return layout;
	}

	private static String htmlTooltip(final String text)
	{
		return "<div style='color:black'>" + text + "</div>";
	}

	private static double divide(final long dividend, final long divisor)
	{
		return new BigDecimal(dividend)
			.divide(new BigDecimal(divisor), 10, RoundingMode.HALF_UP)
			.doubleValue();
	}

	private static NumberFormat decimalFormat, integerFormat;

	private static NumberFormat decimalFormat(final int fractionDigits)
	{
		if(decimalFormat == null)
		{
			final NumberFormat format = NumberFormat.getNumberInstance();
			format.setGroupingUsed(false);
			format.setMinimumFractionDigits(fractionDigits);
			format.setMaximumFractionDigits(fractionDigits);
			decimalFormat = format;
		}
		return decimalFormat;
	}

	private static NumberFormat integerFormat()
	{
		if(integerFormat == null)
		{
			final NumberFormat format = NumberFormat.getIntegerInstance();
			format.setGroupingUsed(true);
			integerFormat = format;
		}
		return integerFormat;
	}

}
