import { OeChartTester } from "src/app/shared/genericComponents/shared/tester";
import { ChartComponent } from "./chart";
import { DummyConfig } from "src/app/shared/edge/edgeconfig.spec";
import { EdgeConfig } from "src/app/shared/shared";
import { TestContext, removeFunctions } from "src/app/shared/test/utils.spec";
import { History } from "src/app/edge/history/peakshaving/symmetric/chart/channels.spec";

export function expectView(config: EdgeConfig, testContext: TestContext, chartType: 'line' | 'bar', channels: History.OeChannels, view: OeChartTester.View, componentId: string): void {
    expect(removeFunctions(OeChartTester
        .apply(ChartComponent
            .getChartData(DummyConfig.convertDummyEdgeConfigToRealEdgeConfig(config), testContext.translate, componentId), chartType, channels, testContext))).toEqual(removeFunctions(view));

};

export const DATASET = (data: OeChartTester.Dataset.Data, labels: OeChartTester.Dataset.LegendLabel, options: OeChartTester.Dataset.Option) => ({
    data: data,
    labels: labels,
    options: options
});

export const DATA = (name: string, value: number[]): OeChartTester.Dataset.Data => ({
    type: "data",
    label: name,
    value: value
});

export const LABELS = (timestamps: string[]): OeChartTester.Dataset.LegendLabel => ({
    type: "label",
    timestamps: timestamps.map(element => new Date(element))
});

export const OPTIONS = (options: OeChartTester.Dataset.Option): OeChartTester.Dataset.Option => options;
