import { Injectable } from '@angular/core';
import { Point } from '../model/point';

@Injectable({
  providedIn: 'root'
})
export class ShapeDrawer {

  widthRatio: number;
  heightRatio: number;

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
