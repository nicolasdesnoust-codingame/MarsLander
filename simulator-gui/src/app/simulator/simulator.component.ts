import {
  Component,
  ElementRef,
  NgZone,
  OnDestroy,
  OnInit,
} from '@angular/core';
import * as PIXI from 'pixi.js';
import { Observable } from 'rxjs';
import { GenerationsPreloader } from './services/generations-preloader.service';
import { HttpService } from './services/http.service';
import { Individual } from './model/individual';
import { ShapeDrawer } from './services/shapes-drawer.service';
import { configuration } from './model/configuration';

@Component({
  selector: 'app-simulator',
  templateUrl: './simulator.component.html',
  styleUrls: ['./simulator.component.scss'],
})
export class SimulatorComponent implements OnInit, OnDestroy {
  private app: PIXI.Application;
  private graphics: PIXI.Graphics;
  private ticker: PIXI.Ticker;

  logicalWidth: number = 1920;
  logicalHeight: number = 1080;

  currentGeneration: number = configuration.firstGeneration;
  generationCount = configuration.generationCount;
  generationIncrement = configuration.generationIncrement;
  firstGeneration = configuration.firstGeneration;
  generationPrinted: number = -1;
  generationTimeout: number = null;
  currentBestSolutions: Individual[];
  loadingPercentage$: Observable<number>;

  constructor(
    private httpService: HttpService,
    private ngZone: NgZone,
    private elementRef: ElementRef,
    private generationsPreloader: GenerationsPreloader,
    private shapeDrawer: ShapeDrawer
  ) {}

  ngOnDestroy(): void {
    this.graphics.destroy();
  }

  ngOnInit(): void {
    this.loadingPercentage$ = this.generationsPreloader.loadingPercentage$;

    this.ngZone.runOutsideAngular(() => {
      this.app = new PIXI.Application({
        antialias: true,
        resolution: window.devicePixelRatio || 1,
      });
    });

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
    this.shapeDrawer.widthRatio = newWidth / 7000;
    this.shapeDrawer.heightRatio = newHeight / 3000;
    console.log(this.app.renderer.width + ' ' + this.app.renderer.height);
    this.app.stage.addChild(this.graphics);

    window.addEventListener('resize', this.resizeHandler.bind(this), false);
    this.resizeHandler();

    this.generationsPreloader.preLoadGenerations();
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

    this.graphics.width = newWidth;
    this.graphics.height = newHeight;
  }

  private initializeTicker() {
    this.ticker = new PIXI.Ticker();
    this.ticker.start();
    this.ticker.add(this.animate, this);
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
        (this.currentGeneration + configuration.generationIncrement) %
        configuration.generationCount;
      if (this.currentGeneration == 0) {
        this.currentGeneration = configuration.firstGeneration;
      }
    }, timeInterval);
  }

  stopAnimation() {
    window.clearInterval(this.generationTimeout);
    this.generationTimeout = null;
  }

  animate() {
    console.log(this.generationPrinted + '  ' + this.currentGeneration);
    if (this.generationPrinted != this.currentGeneration) {
      this.printCurrentGeneration();
      this.generationPrinted = this.currentGeneration;
    }
  }

  private printCurrentGeneration() {
    this.graphics.clear();
    this.loadMarsSurface();
    this.loadCurrentGeneration();
    // this.loadPath();
  }

  private loadMarsSurface() {
    this.httpService.getMarsSurface().subscribe((surface) => {
      this.shapeDrawer.drawPath(this.graphics, surface, 0xde3249);
    });
  }

  private loadPath() {
    this.httpService.getPath().subscribe((path) => {
      this.shapeDrawer.drawPath(this.graphics, path, 0x349beb);
    });
  }

  generationContainer: PIXI.Container;

  private loadCurrentGeneration() {
    if(this.generationContainer)
      this.app.stage.removeChild(this.generationContainer);
    this.generationContainer = this.shapeDrawer.getGenerationContainer(this.currentGeneration);
    if(this.generationContainer) {
      this.app.stage.addChild(this.generationContainer);
    } else {
      this.httpService
      .getGeneration(this.currentGeneration)
      .subscribe((generation) => {
        this.shapeDrawer.drawGeneration(generation, this.currentGeneration);
      });
    }

    for(let i = 1; i <= 5; i++) {
      if(!this.shapeDrawer.getGenerationContainer(this.currentGeneration + i)) {
        this.httpService
        .getGeneration(this.currentGeneration + i)
        .subscribe((generation) => {
          this.shapeDrawer.drawGeneration(generation, this.currentGeneration + i);
        });
      }
    }

    this.httpService
      .getGeneration(this.currentGeneration)
      .subscribe((generation) => {
        const sum = generation.reduce(
          (sum, individual) => sum + individual.evaluation,
          0
        );
        const avgEvaluation = sum / generation.length;
        const bestEvaluation = generation[0].evaluation;
        console.log('avgEvaluation: ' + avgEvaluation);
        console.log('bestEvaluation: ' + bestEvaluation);
        console.log('ratio: ' + avgEvaluation / bestEvaluation);
        console.log('popSize: ' + generation.length);

        this.currentBestSolutions = generation.slice(0, 6);
      });
  }
}
