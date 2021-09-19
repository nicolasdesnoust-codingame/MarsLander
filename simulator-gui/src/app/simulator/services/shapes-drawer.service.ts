import { Injectable } from '@angular/core';
import { Container, Graphics, System } from 'pixi.js';
import { Individual } from '../model/individual';
import { Point } from '../model/point';

@Injectable({
  providedIn: 'root',
})
export class ShapeDrawer {
  private generations: Map<number, Container> = new Map();

  widthRatio: number;
  heightRatio: number;

  getGenerationContainer(generationIndex: number) {
    return this.generations.get(generationIndex);
  }

  drawGeneration(
    generation: Individual[],
    generationIndex: number
  ) {
    const generationContainer = new Container();

    const bestSolutions = generation.slice(0, 6);
    const otherSolutions = generation.slice(6, generation.length);

    otherSolutions.forEach((individual) => {
      const individualPath = new Graphics();
      this.drawPath(individualPath, individual.capsules, 0xedf2f4);
      generationContainer.addChild(individualPath);
    });
    bestSolutions.forEach((individual) => {
      const individualPath = new Graphics();
      this.drawPath(individualPath, individual.capsules, 0xffcc00);
      generationContainer.addChild(individualPath);
    });

    this.generations.set(generationIndex, generationContainer);
    console.log(
      `generation ${generationIndex} done. ${generationContainer.children.length}`
    );
  }

  drawPath(graphics: any, points: Point[], color: number) {
    graphics
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
      graphics
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
