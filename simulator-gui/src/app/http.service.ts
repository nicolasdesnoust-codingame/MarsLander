import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { Point } from './model/point';

@Injectable({
  providedIn: 'root',
})
export class HttpService {
  private cache: Map<string, Observable<any>> = new Map();
  private baseURL = environment.backendBaseURL;
  private readonly MARS_SURFACE_KEY = 'marsSurface';

  constructor(private http: HttpClient) {}

  getMarsSurface(): Observable<Point[]> {
    if (!this.cache.has(this.MARS_SURFACE_KEY)) {
      this.cache.set(
        this.MARS_SURFACE_KEY,
        this.requestMarsSurface().pipe(shareReplay(1))
      );
    }

    return this.cache.get(this.MARS_SURFACE_KEY);
  }

  private requestMarsSurface(): Observable<Point[]> {
    return this.http
      .get<Point[]>(`${this.baseURL}/search/surface`)
      .pipe(
        map((surface) => surface.sort((p1: Point, p2: Point) => p1.x - p2.x))
      );
  }

  getGeneration(generation: number): Observable<Point[][]> {
    if (!this.cache.has(`generation-${generation}`)) {
      this.cache.set(
        `generation-${generation}`,
        this.requestGeneration(generation).pipe(shareReplay(1))
      );
    }

    return this.cache.get(`generation-${generation}`);
  }

  private requestGeneration(generation: number): Observable<Point[][]> {
    return this.http.get<Point[][]>(
      `${this.baseURL}/search/generations/${generation}`
    );
  }

  getBestSolution(): Observable<Point[]> {
    if (!this.cache.has(`bestSolution`)) {
      this.cache.set(
        `bestSolution`,
        this.requestBestSolution().pipe(shareReplay(1))
      );
    }

    return this.cache.get(`bestSolution`);
  }

  private requestBestSolution(): Observable<Point[]> {
    return this.http.get<Point[]>(`${this.baseURL}/search/best-solution`);
  }

  reset(): Observable<void> {
    return this.http.post<void>(`${this.baseURL}/search/reset`, {});
  }

  runSimulation(): Observable<void> {
    return this.http.post<void>(`${this.baseURL}/search/_run-simulation`, {
      testCase: 'test-case-04',
      populationSize: 150,
      generations: 1,
      numberOfSelections: 125,
      numberOfGenesPerIndividual: 100,
    });
  }

  getGenerationCount(): Observable<number> {
    return this.http.get<number>(`${this.baseURL}/search/generations/_count`);
  }

  getEvaluations(generation: number): Observable<any> {
    return this.http.get<any>(
      `${this.baseURL}/search/generations/${generation}/individuals`
    );
  }

  getDistances(generation: number): Observable<any> {
    return this.http.get<any>(
      `${this.baseURL}/search/generations/${generation}/distances`
    );
  }
}
