import { Capsule } from "./capsule";

export interface Individual {
  individualId: number;
  capsules: Capsule[];
  evaluation: number;
}
