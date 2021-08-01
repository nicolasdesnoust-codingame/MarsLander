export interface Configuration {
  firstGeneration: number;
  generationCount: number;
  generationIncrement: number;
}

export const configuration: Configuration = {
  firstGeneration: 0,
  generationCount: 200,
  generationIncrement: 10,
};
