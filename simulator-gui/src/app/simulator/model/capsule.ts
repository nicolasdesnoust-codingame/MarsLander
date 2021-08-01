import { Point } from "./point";

export interface Capsule extends Point {
  index: number;
  fuel: number;
}
