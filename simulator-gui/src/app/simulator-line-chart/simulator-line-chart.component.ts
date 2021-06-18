import { Component, ViewChild } from '@angular/core';
import {
  ChartData,
  ChartDataSets,
  ChartOptions,
  ChartTooltipItem,
  ChartType,
} from 'chart.js';
import { Color, BaseChartDirective, Label } from 'ng2-charts';
import * as pluginAnnotations from 'chartjs-plugin-annotation';
import { HttpService } from '../http.service';
import { concat } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { Individual } from '../model/individual';

@Component({
  selector: 'app-simulator-line-chart',
  templateUrl: './simulator-line-chart.component.html',
  styleUrls: ['./simulator-line-chart.component.scss'],
})
export class SimulatorLineChartComponent {
  public lineChartData: ChartDataSets[] = [];
  public lineChartLabels: Label[];
  public simulationRestarting: boolean = false;
  public lineChartOptions: ChartOptions & { annotation: any } = {
    responsive: true,
    elements: {
      line: {
        tension: 0,
      },
    },
    plugins: {
      zoom: {
        zoom: {
          wheel: {
            enabled: true,
          },
          pinch: {
            enabled: true,
          },
          mode: 'xy',
        },
      },
    },
    tooltips: {
      callbacks: {
        label: this.getGeneTooltip,
      },
    },
    scales: {
      // We use this empty structure as a placeholder for dynamic theming.
      xAxes: [{}],
      yAxes: [
        {
          id: 'y-axis-0',
          position: 'left',
        },
      ],
    },
    annotation: {
      annotations: [
        {
          type: 'line',
          mode: 'vertical',
          scaleID: 'x-axis-0',
          value: 'March',
          borderColor: 'orange',
          borderWidth: 2,
          label: {
            enabled: true,
            fontColor: 'orange',
            content: 'LineAnno',
          },
        },
      ],
    },
  };
  public lineChartColors: Color[] = [
    {
      // red
      backgroundColor: 'rgba(255,0,0,0.3)',
      borderColor: 'rgba(255,0,0,0.3)',
      pointBackgroundColor: 'rgba(148,159,177,0.3)',
      pointBorderColor: '#fff',
      pointHoverBackgroundColor: '#fff',
      pointHoverBorderColor: 'rgba(148,159,177,0.8)',
    },
  ];
  public lineChartLegend = true;
  public lineChartType: ChartType = 'scatter';
  public lineChartPlugins = [pluginAnnotations];
  public currentGeneration: number = 0;

  public generation: any[];

  @ViewChild(BaseChartDirective, { static: true }) chart: BaseChartDirective;

  constructor(private httpService: HttpService) {
    this.getMarsSurface();
    this.loadCurrentGeneration();
  }

  private getMarsSurface() {
    this.httpService.getMarsSurface().subscribe((surface) => {
      this.lineChartData = this.lineChartData.filter(
        (dataset) =>
          dataset.label !== 'Surface de Mars' && dataset.label !== undefined
      );
      this.lineChartData.push({
        label: 'Surface de Mars',
        data: surface,
        showLine: true,
      });
    });
  }

  loadCurrentGeneration() {
    // this.httpService
    //   .getEvaluations(this.currentGeneration)
    //   .subscribe((evaluations) => (this.generation = evaluations));

    this.httpService
      .getGeneration(this.currentGeneration)
      .subscribe((generations) => {
        this.lineChartData = this.lineChartData.filter(
          (dataset) => !dataset.label?.startsWith('individual')
        );
        generations.forEach((generation) => {
          generation = this.filterSamePoints(generation);
          if (((generation[0] as any).type as string).indexOf('D') != -1) {
            generation.genes.sort((p1: any, p2: any) => p1.index - p2.index);
          } else {
            generation.genes.sort((p1: any, p2: any) => p1.geneIndex - p2.geneIndex);
          }

          const borderDash =
            ((generation[0] as any).type as string).indexOf('D') != -1
              ? [10, 5]
              : [];

          this.lineChartData.push({
            label: (generation[0] as any).type,
            data: generation.genes,
            showLine: true,
            fill: false,
            borderDash: borderDash,
            borderWidth: 1,
            pointBorderWidth: 1,
            pointRadius: 2,
            //hidden: true,
            ...this.generateRandomChartColor(),
          });
        });
      });
  }

  filterSamePoints(individual: Individual): Individual {
    individual.genes = individual.genes.filter(
      (point, i, a) =>
        a.findIndex((t) => t.x === point.x && t.y === point.y) === i
    );
    return individual;
  }

  generateRandomChartColor() {
    const r = Math.round(Math.random() * 255);
    const g = Math.round(Math.random() * 255);
    const b = Math.round(Math.random() * 255);

    return {
      borderColor: `rgba(${r}, ${g}, ${b}, 1)`,
      backgroundColor: `rgba(${r}, ${g}, ${b}, 0.2)`,
      pointBackgroundColor: `rgba(${r}, ${g}, ${b}, 0.2)`,
      pointBorderColor: `rgba(${r}, ${g}, ${b}, 0.2)`,
      pointHoverBackgroundColor: `rgba(${r}, ${g}, ${b}, 0.2)`,
      pointHoverBorderColor: `rgba(${r}, ${g}, ${b}, 0.2)`,
    };
  }

  formatLabel(value: number) {
    if (value >= 1000) {
      return Math.round(value / 1000) + 'k';
    }

    return value;
  }

  reset() {
    this.simulationRestarting = true;
    concat(this.httpService.reset(), this.httpService.runSimulation())
      .pipe(finalize(() => (this.simulationRestarting = false)))
      .subscribe(() => {
        this.getMarsSurface();
        this.loadCurrentGeneration();
      });
  }

  getGeneTooltip(tooltipItem: ChartTooltipItem, data: ChartData): string {
    const tooltipData: any =
      data.datasets[tooltipItem.datasetIndex].data[tooltipItem.index];
    const capsule = tooltipData.capsule;
    const type = tooltipData.type;
    if(capsule) {
      return JSON.stringify({
        vSpeed: Math.round(capsule.vSpeed * 100) / 100,
        hSpeed: Math.round(capsule.hSpeed * 100) / 100,
        rotate: capsule.rotate,
        power: capsule.power,
        landingState: capsule.landingState,
        fuel: capsule.fuel,
        type: type,
      });
    }
    else {
      return JSON.stringify({
        type: type
      });
    }
  }

  seriesHidden: boolean = false;
  toggleSeriesVisibility() {
    this.seriesHidden = !this.seriesHidden;
    this.lineChartData.forEach((ds) => {
      ds.hidden = this.seriesHidden;
    });
    this.chart.update();
  }

  distancesHidden: boolean = false;
  toggleDistancesVisibility() {
    this.distancesHidden = !this.distancesHidden;
    this.lineChartData.forEach((ds) => {
      if (ds.label.startsWith('individualD-')) {
        ds.hidden = this.distancesHidden;
      }
    });
    this.chart.update();
  }

  bestIndividualsHidden: boolean = false;
  toggleBestIndividualsVisibility() {
    this.bestIndividualsHidden = !this.bestIndividualsHidden;
    const top = this.generation.slice(0, 10);
    const topSet = new Set<string>();
    top.forEach((t) => {
      const individualPart = (t.message as string).split(':')[0];
      topSet.add(individualPart.toLowerCase().replace(' ', '-'));
      topSet.add(individualPart.toLowerCase().replace(' ', 'D-'));
    });

    this.lineChartData.forEach((ds) => {
      if (topSet.has(ds.label)) {
        ds.hidden = this.bestIndividualsHidden;
      }
    });
    this.chart.update();
  }
}
