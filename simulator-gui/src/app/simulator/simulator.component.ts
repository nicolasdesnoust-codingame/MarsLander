import {
  Component,
  ElementRef,
  NgZone,
  OnDestroy,
  OnInit,
} from '@angular/core';
import * as PIXI from 'pixi.js';
import { HttpService } from '../http.service';
import { Individual } from '../model/individual';
import { Point } from '../model/point';

@Component({
  selector: 'app-simulator',
  templateUrl: './simulator.component.html',
  styleUrls: ['./simulator.component.scss'],
})
export class SimulatorComponent implements OnInit, OnDestroy {
  private app: PIXI.Application;
  private graphics: PIXI.Graphics;
  private ticker: PIXI.Ticker;
  //private mainContainer = null;

  widthRatio: number;
  heightRatio: number;

  logicalWidth: number = 1920;
  logicalHeight: number = 1080;

  firstGeneration: number = 0;
  currentGeneration: number = this.firstGeneration;
  generationCount: number = 500;
  generationIncrement: number = 10;
  generationPrinted: number = -1;
  generationTimeout: number = null;
  generationsLoadingPercentage: number = 0;
  bestSolutionFound: boolean;
  currentBestSolutions: Individual[];

  constructor(
    private httpService: HttpService,
    private ngZone: NgZone,
    private elementRef: ElementRef
  ) {}

  ngOnDestroy(): void {
    this.graphics.destroy();
  }

  ngOnInit(): void {
    this.ngZone.runOutsideAngular(() => {
      this.app = new PIXI.Application({
        antialias: true,
        //   width: window.innerWidth,
        //  height: window.innerHeight,
        resolution: window.devicePixelRatio || 1,
      });
    });

    //window.addEventListener('resize', this.resize.bind(this), false);
    //this.resize();

    //this.app.stage.width = window.innerWidth;
    //this.app.stage.height = window.innerHeight;

    // PIXI.settings.RESOLUTION = devicePixelRatio || 1;
    (PIXI.settings as any).SCALE_MODE = PIXI.SCALE_MODES.LINEAR;

    this.elementRef.nativeElement.appendChild(this.app.view);

    (this.app.renderer as any).autoResize = true;

    this.graphics = new PIXI.Graphics();
    const scaleFactor = Math.min(
      window.innerWidth / this.logicalWidth,
      window.innerHeight / this.logicalHeight
    );
    const newWidth = Math.ceil(this.logicalWidth * scaleFactor);
    const newHeight = Math.ceil(this.logicalHeight * scaleFactor);
    this.widthRatio = newWidth / 7000;
    this.heightRatio = newHeight / 3000;
    console.log(this.app.renderer.width + ' ' + this.app.renderer.height);
    this.app.stage.addChild(this.graphics);

    window.addEventListener('resize', this.resizeHandler.bind(this), false);
    this.resizeHandler();

    this.getGenerationCount();
    this.preLoadGenerations();
    this.printCurrentGeneration();
    this.initializeTicker();
  }

  resizeHandler() {
    const scaleFactor = Math.min(
      window.innerWidth / this.logicalWidth,
      window.innerHeight / this.logicalHeight
    );
    const newWidth = Math.ceil(this.logicalWidth * scaleFactor);
    const newHeight = Math.ceil(this.logicalHeight * scaleFactor);

    this.app.renderer.view.style.width = `${newWidth}px`;
    this.app.renderer.view.style.height = `${newHeight}px`;

    this.app.renderer.resize(window.innerWidth, window.innerHeight);

    // this.app.stage.scale.set(
    //   this.app.renderer.width / this.logicalWidth,
    //   this.app.renderer.height / this.logicalHeight
    // );

    this.graphics.width = newWidth;
    this.graphics.height = newHeight;
    //this.graphics.scale.set(scaleFactor);
  }

  private resize() {
    let width: number, height: number;
    const canvasRatio = 3000 / 7000;
    const windowRatio = window.innerHeight / window.innerWidth;

    if (windowRatio < canvasRatio) {
      height = window.innerHeight;
      width = height / canvasRatio;
    } else {
      width = window.innerWidth;
      height = width * canvasRatio;
    }
    this.app.view.style.width = width + 'px';
    this.app.view.style.height = height + 'px';
  }

  private initializeTicker() {
    this.ticker = new PIXI.Ticker();
    this.ticker.start();
    this.ticker.add(this.animate, this);
  }

  private getGenerationCount() {
    // this.httpService.getGenerationCount().subscribe((generationCount) => {
    //   this.generationCount = 200;
    //   console.log(this.generationCount);
    // });
  }

  playAnimation() {
    this.generationTimeout = this.updateCurrentGenerationEach(150);
  }

  isAnimationRunning() {
    return this.generationTimeout != null;
  }

  private updateCurrentGenerationEach(timeInterval: number): number {
    return window.setInterval(() => {
      this.currentGeneration =
        (this.currentGeneration + this.generationIncrement) %
        this.generationCount;
      if (this.currentGeneration == 0) {
        this.currentGeneration = this.firstGeneration;
      }
    }, timeInterval);
  }

  stopAnimation() {
    window.clearInterval(this.generationTimeout);
    this.generationTimeout = null;
  }

  animate() {
    if (this.generationPrinted != this.currentGeneration) {
      this.printCurrentGeneration();
      this.updateCurrentBestSolutions();
    }
  }

  updateCurrentBestSolutions() {
    // this.httpService.getEvaluations(this.currentGeneration).subscribe((evaluations: any[]) => {
    //   this.currentBestSolutions = evaluations
    //   .sort((e1, e2) => e2.evaluation - e1.evaluation)
    //   .slice(0, 6);
    // });
  }

  private printCurrentGeneration() {
    this.graphics.clear();
    this.loadMarsSurface();
    this.loadBestSolution();
    this.loadCurrentGeneration();
    this.loadPath();
    //this.loadPathPoints();
  }

  private loadMarsSurface() {
    this.httpService.getMarsSurface().subscribe((surface) => {
      this.drawPath(surface, 0xde3249);
    });
  }

  private loadPath() {
    this.httpService.getPath().subscribe((path) => {
      this.drawPath(path, 0x349beb);
    });
  }

  private loadPathPoints() {
    this.httpService.getPathPoints().subscribe((pathPoints) => {
      this.drawPath(pathPoints, 0x922fbd);
    });
  }

  private loadBestSolution() {
    this.httpService.getBestSolution().subscribe((bestSolution) => {
      if (bestSolution.length == 0) {
        this.bestSolutionFound = false;
      } else {
        this.bestSolutionFound = true;
        const bestSolutionPath = bestSolution.map(
          (e: any) => e.capsule.position
        );
        this.drawPath(bestSolutionPath, 0x3cb371);
      }
    });
  }

  private loadCurrentGeneration() {
    this.httpService
      .getGeneration(this.currentGeneration)
      .subscribe((generation) => {
        generation.forEach((individual) => {
          individual = this.filterSamePoints(individual);
          individual.genes.sort((p1: any, p2: any) => p1.index - p2.index);
        });

        generation.sort((e1, e2) => e2.evaluation - e1.evaluation);

        this.currentBestSolutions = generation.slice(0, 6);
        const otherSolutions = generation.slice(6, generation.length);

        this.currentBestSolutions.forEach(individual => this.drawPath(individual.genes, 0xffcc00));
        otherSolutions.forEach(individual => this.drawPath(individual.genes, 0xedf2f4));
      });
  }

  private preLoadGenerations() {
    this.generationsLoadingPercentage = 0;
    let generationLoadedCount = 0;

    for (
      let i = this.firstGeneration;
      i < this.generationCount;
      i += this.generationIncrement
    ) {
      this.httpService.getGeneration(i).subscribe(() => {
        console.log(i + ' loaded');
        generationLoadedCount++;
        this.generationsLoadingPercentage = Math.round(
          (generationLoadedCount /
            (this.generationCount - this.firstGeneration)) *
            100
        );
      });
    }
  }

  filterSamePoints(individual: Individual): Individual {
    individual.genes = individual.genes.filter(
      (point, i, a) =>
        a.findIndex((t) => t.x === point.x && t.y === point.y) === i
    );
    return individual;
  }

  drawPath(points: Point[], color: number) {
    this.graphics
      .lineStyle(0)
      .beginFill(color, 1)
      .drawCircle(
        points[0].x * this.widthRatio,
        (3000 - points[0].y) * this.heightRatio,
        0.5
      )
      .endFill()
      .moveTo(
        points[0].x * this.widthRatio,
        (3000 - points[0].y) * this.heightRatio
      );
    for (let i = 1; i < points.length; i++) {
      this.graphics
        .lineStyle(2, color, 1)
        .lineTo(
          points[i].x * this.widthRatio,
          (3000 - points[i].y) * this.heightRatio
        )
        .lineStyle(0)
        .beginFill(color, 1)
        .drawCircle(
          points[i].x * this.widthRatio,
          (3000 - points[i].y) * this.heightRatio,
          0.5
        )
        .endFill()
        .moveTo(
          points[i].x * this.widthRatio,
          (3000 - points[i].y) * this.heightRatio
        );
    }
  }
}
