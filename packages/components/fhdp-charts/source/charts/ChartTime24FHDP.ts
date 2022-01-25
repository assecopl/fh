import {HTMLFormComponent} from "fh-forms-handler";
import Konva from 'konva';
import {LineConfig} from "konva/lib/shapes/Line";
import {Layer} from "konva/lib/Layer";

interface BarElement {
    from: string,
    to: string,
    position: string,
    color: string,
    tooltipText: string,
    legendText: string,
}

class ChartTime24FhDP extends HTMLFormComponent {
    //? base prop
    private hAxisName: string;
    private vAxisName: string;
    private bgColor: string;
    private tooltipBgColor: string;
    private axisLabelColor: string;
    private lineColor: string;
    private tooltipColor: string;
    private fontFamily: string;
    private barElements: BarElement[] = [];
    private highlightedValue: number;
    private highlightedStroke: number;
    private gradation: number;
    private maxValueX: number;

    //? container ids
    private mainElementId: string;
    private scrollContainerId: string;

    //? bar
    private barBreakWidth: number;
    private barHeight: number;
    private barBreakHeight: number;
    private percentDarkColorOnMove: number;

    //? base margin
    private leftMarginChart: number;
    private rightMarginChart: number;
    private topMarginChart: number;
    private bottomMarginChart: number;

    //? scroll container
    private scrollSizeChart: number;

    //? lines, value lines
    private protrudingLinesHeight: number;
    private labelTextHeight: number;
    private labelTextFontSize: number;
    private lineStrokeWidth: number;

    //? legend
    private topMarginLegend: number;
    private legendColorWidth: number;
    private legendColorHeight: number;
    private legendFontSize: number;
    private legendBreakColorText: number;
    private legendElementBreakWidth: number;
    private legendElementBreakHeight: number;

    //? tooltip
    private tooltipOpacity: number;
    private tooltipPointerWidth: number;
    private tooltipPointerHeight: number;
    private tooltipCornerRadius: number;
    private tooltipFontSize: number;
    private tooltipPadding: number;

    //? label name prop
    private labelNameFontSize: number;
    private labelNameBreak: number;

    //? resize event
    private resizeWindowBind = this.resizeWindow.bind(this);

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);
        
        //? base parameters
        this.hAxisName = componentObj.haxisName || '';
        this.vAxisName = componentObj.vaxisName || '';
        this.bgColor = componentObj.bgColor || '#f3f2ec';
        this.tooltipBgColor = componentObj.tooltipBgColor || 'black';
        this.axisLabelColor = componentObj.axisLabelColor || 'black';
        this.lineColor = componentObj.lineColor || 'LightSlateGrey';
        this.tooltipColor = componentObj.tooltipColor || 'white';
        this.fontFamily = componentObj.fontFamily || 'Calibri';
        this.barElements = componentObj.bars;
        this.highlightedValue = Number(componentObj.highlightedValue) || 22;
        this.highlightedStroke = Number(componentObj.highlightedStroke) || 3;
        this.gradation = Number(componentObj.gradation) || 2;
        this.maxValueX = Number(componentObj.maxValueX) || 26;
        
        //? containers ids
        this.mainElementId = componentObj.mainElementId || 'chartContainer';
        this.scrollContainerId = componentObj.scrollContainerId || 'scrollContainer';

        //? bar
        this.barBreakWidth = Number(componentObj.barBreakWidth) || 60;
        this.barHeight = Number(componentObj.barHeight) || 100;
        this.barBreakHeight = Number(componentObj.barBreakHeight) || 30;
        this.percentDarkColorOnMove = Number(componentObj.percentDarkColorOnMove) || 20;

        //? base margin
        this.leftMarginChart = Number(componentObj.leftMarginChart) || 100;
        this.rightMarginChart = Number(componentObj.rightMarginChart) || 110;
        this.topMarginChart = Number(componentObj.topMarginChart) || 50;
        this.bottomMarginChart = Number(componentObj.bottomMarginChart) || 50;

        //? scroll container
        this.scrollSizeChart = Number(componentObj.scrollSizeChart) || 23;

        //? lines, value lines
        this.protrudingLinesHeight = Number(componentObj.protrudingLinesHeight) || 10;
        this.labelTextHeight = Number(componentObj.labelTextHeight) || 3;
        this.labelTextFontSize = Number(componentObj.labelTextFontSize) || 22;
        this.lineStrokeWidth = Number(componentObj.lineStrokeWidth) || 1;

        //? legend
        this.topMarginLegend = Number(componentObj.topMarginLegend) || 20;
        this.legendColorWidth = Number(componentObj.legendColorWidth) || 40;
        this.legendColorHeight = Number(componentObj.legendColorHeight) || 20;
        this.legendFontSize = Number(componentObj.legendFontSize) || 18;
        this.legendBreakColorText = Number(componentObj.legendBreakColorText) || 5;
        this.legendElementBreakWidth = Number(componentObj.legendElementBreakWidth) || 25;
        this.legendElementBreakHeight = Number(componentObj.legendElementBreakHeight) || 3;

        //? tooltip
        this.tooltipOpacity = Number(componentObj.tooltipOpacity) || 0.8;
        this.tooltipPointerWidth = Number(componentObj.tooltipPointerWidth) || 10;
        this.tooltipPointerHeight = Number(componentObj.tooltipPointerHeight) || 20;
        this.tooltipCornerRadius = Number(componentObj.tooltipCornerRadius) || 5;
        this.tooltipFontSize = Number(componentObj.tooltipFontSize) || 18;
        this.tooltipPadding = Number(componentObj.tooltipPadding) || 10;

        //? label name prop
        this.labelNameFontSize = Number(componentObj.labelNameFontSize) || 22;
        this.labelNameBreak = Number(componentObj.labelNameBreak) || 13;

        this.resizeWindowBind = this.resizeWindow.bind(this);
        this.initResizeWindow();
    }

    create() {
        const mainElement = document.createElement('div');
        mainElement.id = this.mainElementId;
        mainElement.style.display = 'flex';

        this.component = mainElement;

        this.wrap(false, false);
        this.addStyles();
        this.display();

        this.buildChart(mainElement.id);
        this.display();
    }

    initResizeWindow() {
        $(window).on('resize', this.resizeWindowBind);
    }

    resizeWindow() {
        this.destroy(true);
        this.create();
        this.initResizeWindow();
    }

    destroy(removeFromParent) {
        $(window).off('resize', this.resizeWindowBind);
        super.destroy(removeFromParent);
    }

    update(change) {
        super.update(change);

        this.destroy(true);
        this.create();
        this.resizeWindowBind = this.resizeWindow.bind(this);
        this.initResizeWindow();
    }
  
    buildChart(id: string) {
        //? create hAxisLabels numbers
        const { hAxisLabels, maxX } = this.getHAxisLables();
        const maxXFullWidth = maxX + this.leftMarginChart + this.rightMarginChart;
        const stageWidth = maxXFullWidth < this.htmlElement.offsetWidth ? maxXFullWidth:this.htmlElement.offsetWidth;

        const chartLinesHeight = (this.barElements.length + 1) * this.barBreakHeight + this.barElements.length * this.barHeight;
        const chartHeight = chartLinesHeight + this.protrudingLinesHeight + this.labelTextFontSize + this.labelTextHeight;

        const stage = new Konva.Stage({
            container: id,
            width: stageWidth,
        })

        const { staticLayer, background } = this.createStaticLayer(stageWidth, chartLinesHeight);
        const legendLayer = new Konva.Layer({
            x: this.leftMarginChart,
        });

        stage.add(staticLayer, legendLayer);
        stage.draw();

        //? create div for scroll stage
        const baseContainer = document.getElementById(id);
        const baseConvasContainer = baseContainer.childNodes[0];

        const scrollLayer = this.createScrollLayer(hAxisLabels, maxX, chartLinesHeight);
        const { barsLayer, tooltipLayer, maxLegendY } = this.createBarsLayer(legendLayer, stageWidth);

        const scrollContainer = document.createElement('div');
        scrollContainer.id = this.scrollContainerId;
        scrollContainer.style.overflow = 'auto';
        scrollContainer.style.marginLeft = `${this.leftMarginChart}px`;
        scrollContainer.style.height = `${chartHeight + this.scrollSizeChart}px`;
        scrollContainer.style.position = 'relative';
        scrollContainer.style.marginTop = `${this.topMarginChart}px`;
        scrollContainer.style.marginRight = `${this.rightMarginChart}px`;
        baseConvasContainer.appendChild(scrollContainer)

        //? Resizze Legend
        const legendY = chartHeight + this.scrollSizeChart + this.topMarginChart + this.topMarginLegend;
        legendLayer.y(legendY);

        const legendWidth = maxLegendY + this.legendColorHeight;

        const allHeight = legendY + legendWidth + this.bottomMarginChart;
        stage.height(allHeight);
        background.height(allHeight);

        //? scroll stage
        const scrollStage = new Konva.Stage({
            container: scrollContainer.id,
            width: maxX,
            height: chartHeight,
            x: this.lineStrokeWidth,
        });

        scrollStage.add(scrollLayer, barsLayer, tooltipLayer);
        scrollStage.draw();
    }

    getHAxisLables() {
        const maxTo = this.barElements.reduce((prev, curr) => {
            return (Number(prev.to) > Number(curr.to)) ? prev:curr;
        })

        let yearStart = 1;
        const yearEnd = Number(maxTo.to) > this.maxValueX ? Math.ceil(Number(maxTo.to)/this.gradation) + 2: Math.ceil(this.maxValueX/2);
        const hAxisLabels = Array(yearEnd-yearStart+1).fill(0).map(() => this.gradation*yearStart++);
        const baseMaxX = hAxisLabels.length * this.barBreakWidth + this.barBreakWidth;

        const tooltipTextBox = new Konva.Text({
            text: maxTo.tooltipText.replace(/\\n/g,"\n"),
            fontFamily: this.fontFamily,
            fontSize: this.tooltipFontSize,
        });

        const valueOneGradation = this.barBreakWidth/this.gradation;
        const widthBar = (Number(maxTo.to)*valueOneGradation + this.leftMarginChart) - (Number(maxTo.from)*valueOneGradation + this.leftMarginChart);

        const barXMax = Number(maxTo.from)*valueOneGradation + widthBar;
        const maxXTooltip = barXMax + Math.ceil(tooltipTextBox.textWidth) + 2*this.tooltipPadding + this.tooltipPointerWidth;
        const maxX = baseMaxX > maxXTooltip ? baseMaxX:maxXTooltip;

        return { hAxisLabels, maxX }
    }

    createScrollLayer(hAxisLabels: number[], maxX: number, chartHeight: number) {
        const scrollLayer = new Konva.Layer();
        
        //? Create grid
        const baseGridLine = new Konva.Line({
            points: [0, 0, 0, chartHeight, maxX, chartHeight],
            stroke: this.lineColor,
            strokeWidth: this.lineStrokeWidth,
            lineCap: 'round',
            lineJoin: 'round',
        });
        scrollLayer.add(baseGridLine);

        //? labels and line OX
        hAxisLabels.forEach((el, i) => {
            const configLabel: LineConfig = {
                points: [(i+1) * this.barBreakWidth, 0, (i+1) * this.barBreakWidth, chartHeight + this.protrudingLinesHeight],
                stroke: this.lineColor,
                strokeWidth: this.lineStrokeWidth,
                lineCap: 'round',
                lineJoin: 'round',
            }
          
            if(el===this.highlightedValue) {
                configLabel.strokeWidth = this.highlightedStroke;
            }
          
            const labelText = new Konva.Text({
                x: (i+1) * this.barBreakWidth,
                y: chartHeight + this.protrudingLinesHeight + this.labelTextHeight,
                text: String(el),
                fontSize: this.labelTextFontSize,
                fontFamily: this.fontFamily,
                fill: this.axisLabelColor,
            });
            labelText.attrs.x -= (labelText.textWidth/2);
            
            const lineLabel = new Konva.Line(configLabel);
            scrollLayer.add(lineLabel);
            scrollLayer.add(labelText);
        })

        return scrollLayer;
    }

    createStaticLayer(width: number, chartHeight: number) {
        const staticLayer = new Konva.Layer();

        //? Create background
        const background = new Konva.Rect({
            width: width,
            fill: this.bgColor,
        });

        staticLayer.add(background)
        this.createAxisLabel(staticLayer, width, chartHeight);

        return { staticLayer, background };
    }

    createAxisLabel(staticLayer: Layer, width: number, chartHeight: number) {
        //? Label X NAME
        let labelXName = new Konva.Text({
            x: width - this.rightMarginChart + this.labelNameBreak,
            y: chartHeight + this.protrudingLinesHeight + this.labelTextHeight + this.topMarginChart,
            text: this.hAxisName,
            fontSize: this.labelNameFontSize,
            fontFamily: this.fontFamily,
            fill: this.axisLabelColor,
        });
        staticLayer.add(labelXName)
        
        //? Label Y NAME
        let labelYName = new Konva.Text({
            x: this.leftMarginChart - this.labelNameBreak,
            y: this.topMarginChart,
            text: this.vAxisName,
            fontSize: this.labelNameFontSize,
            fontFamily: this.fontFamily,
            fill: this.axisLabelColor,
        });
        
        labelYName.attrs.y -= labelYName.textHeight;
        labelYName.attrs.x -= labelYName.textWidth;
        staticLayer.add(labelYName)
    }

    createBarsLayer(legendLayer: Konva.Layer, stageWidth: number) {
        let legendX = 0;
        let legendY = 0;

        const barsLayer = new Konva.Layer();

        const tooltip = new Konva.Label({
            opacity: this.tooltipOpacity,
        });
        tooltip.add(
            new Konva.Tag({
                fill: this.tooltipBgColor,
                pointerDirection: 'left',
                pointerWidth: this.tooltipPointerWidth,
                pointerHeight: this.tooltipPointerHeight,
                lineJoin: 'round',
                cornerRadius: this.tooltipCornerRadius,
            })
        );

        const tooltipTextBox = new Konva.Text({
            text: '',
            fontFamily: this.fontFamily,
            fontSize: this.tooltipFontSize,
            padding: this.tooltipPadding,
            fill: this.tooltipColor,
        });
        tooltip.add(tooltipTextBox);
        tooltip.hide();

        this.barElements
            .sort((a, b) => (Number(a.position) > Number(b.position) ? 1:-1))
            .forEach((el, i) => {
                const fromValue = Number(el.from);
                const toValue = Number(el.to);
                const valueOneGradation = this.barBreakWidth/this.gradation;
                const barHeight = this.barHeight;
                const barBreakHeight = this.barBreakHeight;

                const bar = new Konva.Rect({
                    width: (toValue*valueOneGradation + this.leftMarginChart) - (fromValue*valueOneGradation + this.leftMarginChart),
                    height: barHeight,
                    fill: el.color,
                    x: fromValue*valueOneGradation,
                    y: barHeight*i + barBreakHeight*(i+1),
                });
    
                const darkColor = this.darkenColor(el.color, this.percentDarkColorOnMove);
              
                bar.on('mousemove', function (e) {
                    this.fill(darkColor)
                
                    //? positioning tooltip
                    tooltip.position({
                        x: (fromValue*valueOneGradation) + (toValue*valueOneGradation) - (fromValue*valueOneGradation),
                        y: barHeight*i + barBreakHeight*(i+1) + barHeight/2,
                    });
                    tooltipTextBox.text(el.tooltipText.replace(/\\n/g,"\n")); 
                    tooltip.show();
                });
                bar.on('mouseout', function () {
                  this.fill(el.color);
                  tooltip.hide();
                });
              
                barsLayer.add(bar)
    
                const legendParam = this.createLegendByBar(
                    legendX, legendY, el, legendLayer, darkColor, bar, tooltip, tooltipTextBox, i, fromValue, 
                    toValue, valueOneGradation, barHeight, barBreakHeight, stageWidth);
                legendX = legendParam.legendX;
                legendY = legendParam.legendY;
            })

        const tooltipLayer = new Konva.Layer();
        tooltipLayer.add(tooltip);

        return { barsLayer, tooltipLayer, maxLegendY: legendY }
    }

    createLegendByBar(
        legendX: number, legendY: number, el: BarElement, legendLayer: Layer, darkColor: string, bar: Konva.Rect, tooltip: Konva.Label, 
        tooltipTextBox: Konva.Text, iter: number, fromValue: number, toValue: number, valueOneGradation: number, 
        barHeight: number, barBreakHeight: number, stageWidth: number) {

        const legend = new Konva.Rect({
            width: this.legendColorWidth,
            height: this.legendColorHeight,
            fill: el.color,
            x: legendX,
            y: legendY,
        });
        
        const legendText = new Konva.Text({
            x: legendX + legend.attrs.width + this.legendBreakColorText,
            y: legendY,
            height: legend.attrs.height,
            text: el.legendText,
            fontSize: this.legendFontSize,
            fontFamily: this.fontFamily,
            fill: 'black',
            verticalAlign: 'middle'
        });

        const sumWidthLegend = legend.attrs.width + this.legendElementBreakWidth + legendText.textWidth + this.legendBreakColorText;
        const widthLegend = legendX + sumWidthLegend;
        const scrollWidth = stageWidth - this.leftMarginChart - this.rightMarginChart;

        if(widthLegend > scrollWidth) {
            legendY += this.legendColorHeight + this.legendElementBreakHeight;
            legendX = 0;
            legend.y(legendY);
            legend.x(legendX);
            legendText.y(legendY);
            legendText.x(legendX + legend.attrs.width + this.legendBreakColorText);
        }
      
        legendX += sumWidthLegend;
      
        legendLayer.add(legend);
        legendLayer.add(legendText);
      
        legend.on('mousemove', function (e) {
            this.fill(darkColor)
            bar.fill(darkColor)

            tooltip.position({
                x: (fromValue*valueOneGradation) + (toValue*valueOneGradation) - (fromValue*valueOneGradation),
                y: barHeight*iter + barBreakHeight*(iter+1) + barHeight/2,
            });

            tooltipTextBox.text(el.tooltipText.replace(/\\n/g,"\n"));
            tooltip.show();
        });
        legend.on('mouseout', function () {
            this.fill(el.color);
            bar.fill(el.color);
            tooltip.hide();
        });

        return { legendX, legendY };
    }
    
    darkenColor(color: string, percent: number) {
        var num = parseInt(color.replace("#",""),16),
        amt = Math.round(2.55 * percent),
        R = (num >> 16) - amt,
        B = (num >> 8 & 0x00FF) - amt,
        G = (num & 0x0000FF) - amt;
        return "#" + (0x1000000 + (R<255?R<1?0:R:255)*0x10000 + (B<255?B<1?0:B:255)*0x100 + (G<255?G<1?0:G:255)).toString(16).slice(1);
    };
}

export { ChartTime24FhDP }