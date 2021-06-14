import { BrowserModule } from '@angular/platform-browser';
import { LOCALE_ID, NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from './material.module';
import { ChartsModule } from 'ng2-charts';
import { SimulatorLineChartComponent } from './simulator-line-chart/simulator-line-chart.component';
import { HttpClientModule } from '@angular/common/http';
import { SimulatorComponent } from './simulator/simulator.component';

@NgModule({
  declarations: [
    AppComponent,
    SimulatorLineChartComponent,
    SimulatorComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MaterialModule,
    ChartsModule,
    HttpClientModule,
  ],
  providers: [{ provide: LOCALE_ID, useValue: 'fr' }],
  bootstrap: [AppComponent],
})
export class AppModule {}
