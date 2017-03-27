export class CoreMonitorStatus {
  id: number;
  category: string;
  metric: string;
  collect: CoreMonitorStatusCollect
  analyze?: CoreMonitorStatusAnalyze
  alarm?: CoreMonitorStatusAlarm
}

export class CoreMonitorStatusCollect {
  state: Boolean;
  timestamp: String;
  error?: String;
}

export class CoreMonitorStatusAnalyze {
  state: Boolean;
  timestamp: String;
  error?: String;
}

export class CoreMonitorStatusAlarm {
  state: Boolean;
  timestamp: String;
  error?: String;
}