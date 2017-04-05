export interface CoreMonitorStatus {
  id: number;
  category: string;
  name: string;
  metric: string;
  collect?: CoreMonitorStageStatus;
  analyze?: CoreMonitorStageStatus;
  alarm?: CoreMonitorStageStatus;
}

export interface CoreMonitorStageStatus {
  state: Boolean;
  timestamp: String;
  result: String;
}