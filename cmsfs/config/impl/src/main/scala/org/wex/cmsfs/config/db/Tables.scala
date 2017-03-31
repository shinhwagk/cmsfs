package org.wex.cmsfs.config.db

import slick.jdbc.MySQLProfile.api._

object Tables {

  val coreMonitorDetails = TableQuery[CoreMonitorDetails]

  val coreConnectorJdbcs = TableQuery[CoreConnectorJdbcs]

  val coreConnectorSshs = TableQuery[CoreConnectorSshs]

  val coreCollects = TableQuery[CoreCollects]

  val coreFormatAlarms = TableQuery[CoreFormatAlarms]

  val coreFormatAnalyzes = TableQuery[CoreFormatAnalyzes]

//  val coreMonitorStatuses = TableQuery[CoreMonitorStatuses]

}