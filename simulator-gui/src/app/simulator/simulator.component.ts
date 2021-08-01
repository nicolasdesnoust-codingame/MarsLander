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
    if (this.generationPrinted != this.currentGeneration) {
      this.printCurrentGeneration();
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

  private loadCurrentGeneration() {
    this.httpService
      .getGeneration(this.currentGeneration)
      .subscribe((generation) => {
        this.currentBestSolutions = generation.slice(0, 6);
        const otherSolutions = generation.slice(6, generation.length);

        otherSolutions.forEach((individual) =>
          this.shapeDrawer.drawPath(
            this.graphics,
            individual.capsules,
            0xedf2f4
          )
        );
        this.currentBestSolutions.forEach((individual) =>
          this.shapeDrawer.drawPath(
            this.graphics,
            individual.capsules,
            0xffcc00
          )
        );
      });
  }
}
