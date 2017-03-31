export interface CoreMonitorStatus {
  // constructor(id: number, category: string, name: String, metric: string,
  //   collect: CoreMonitorStageStatus,
  //   analyze?: CoreMonitorStageStatus,
  //   alarm?: CoreMonitorStageStatus) {
  //   this.id = id
  //   this.category = category
  //   this.name = name
  //   this.metric = metric
  //   this.collect = collect
  //   this.analyze = analyze
  //   this.alarm = alarm
  // }
  id: number
  category: string
  name: String
  metric: string
  collect?: CoreMonitorStageStatus
  analyze?: CoreMonitorStageStatus
  alarm?: CoreMonitorStageStatus
}

export interface CoreMonitorStageStatus {
  state: Boolean;
  timestamp: String;
  result: String;
}

export class CoreMonitorStatusCollect implements CoreMonitorStageStatus {
  constructor(state: Boolean, timestamp: String, result: String) {
    this.state = state; this.timestamp = timestamp; this.result = result
  }
  state: Boolean;
  timestamp: String;
  result: String;
}

export class CoreMonitorStatusAnalyze implements CoreMonitorStageStatus {
  state: Boolean;
  timestamp: String;
  result: String;
}

export class CoreMonitorStatusAlarm implements CoreMonitorStageStatus {
  state: Boolean;
  timestamp: String;
  result: String;
}

// export const testData: CoreMonitorStatus[] = [
//   new CoreMonitorStatus(1, "oracle", "dev2", "tablespace",
//     new CoreMonitorStatusCollect(true, "2017-01-11", "success\naaaa\"aaaaa"),
//     new CoreMonitorStatusCollect(false, "2017-01-11", "success\naaa\"aaaaaaa")),
//   new CoreMonitorStatus(2, "oracle", "dev2", "session",
//     new CoreMonitorStatusCollect(true, "2017-01-11", "success\naaa\"aaaaaaa"),
//     new CoreMonitorStatusCollect(true, "2017-01-11", "success\naaa\"aaaaaaa"),
//     new CoreMonitorStatusCollect(true, "2017-01-11", "success\naaaa\"aaaaaa"))
// ]