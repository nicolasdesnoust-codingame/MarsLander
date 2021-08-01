import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { Capsule } from '../model/capsule';
import { Individual } from '../model/individual';
import { Point } from '../model/point';

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
    return this.http.get<Point[]>(`${this.baseURL}/search/surface`);
  }

  getGeneration(generation: number): Observable<Individual[]> {
    if (!this.cache.has(`generation-${generation}`)) {
      this.cache.set(
        `generation-${generation}`,
        this.requestGeneration(generation).pipe(shareReplay(1))
      );
    }

    return this.cache.get(`generation-${generation}`);
  }

  private requestGeneration(generation: number): Observable<Individual[]> {
    return this.http.get<Individual[]>(
      `${this.baseURL}/search/generations/${generation}`
    );
  }

  getPath(): Observable<Point[]> {
    if (!this.cache.has(`path`)) {
      this.cache.set(`path`, this.requestPath().pipe(shareReplay(1)));
    }

    return this.cache.get(`path`);
  }

  private requestPath(): Observable<Point[]> {
    return this.http.get<Point[]>(`${this.baseURL}/search/path`);
  }

  getPathPoints(): Observable<Point[]> {
    if (!this.cache.has(`path-points`)) {
      this.cache.set(
        `path-points`,
        this.requestPathPoints().pipe(shareReplay(1))
      );
    }

    return this.cache.get(`path-points`);
  }

  private requestPathPoints(): Observable<Point[]> {
    return this.http.get<Point[]>(`${this.baseURL}/search/path/points`);
  }

  reset(): Observable<void> {
    return this.http.post<void>(`${this.baseURL}/search/reset`, {});
  }

  runSimulation(): Observable<void> {
    return this.http.post<void>(`${this.baseURL}/simulations`, {
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
}
