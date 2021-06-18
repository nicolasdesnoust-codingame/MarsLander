import { Gene } from "./gene";

export interface Individual {
  type: string;
  turn: number;
  generation: number;
  genes: Gene[];
  evaluation: number;
}
