import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { HttpService } from './http.service';
import { configuration } from '../model/configuration';

@Injectable({
  providedIn: 'root',
})
export class GenerationsPreloader {
  private loadingPercentageSubject = new BehaviorSubject<number>(0);
  private generationLoadedCount = 0;

  constructor(private httpService: HttpService) {}

  get loadingPercentage$() {
    return this.loadingPercentageSubject.asObservable();
  }

  public preLoadGenerations() {
    for (
      let generationIndex = configuration.firstGeneration;
      generationIndex < configuration.generationCount;
      generationIndex += configuration.generationIncrement
    ) {
      this.httpService.getGeneration(generationIndex).subscribe(() => {
        console.log(`Generation ${generationIndex} loaded`);
        this.generationLoadedCount++;
        const loadingPercentage = this.calculateLoadingPercentage();
        this.loadingPercentageSubject.next(loadingPercentage);
      });
    }
  }

  private calculateLoadingPercentage(): number {
    const generationToLoadCount =
      (configuration.generationCount - configuration.firstGeneration) /
      configuration.generationIncrement;

    return this.generationLoadedCount / generationToLoadCount;
  }
}
