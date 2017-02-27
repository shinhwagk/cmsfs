package org.wex.cmsfs.collection.impl

import java.util.UUID

import akka.Done
import com.datastax.driver.core._
import com.datastax.driver.core.utils.UUIDs
import com.lightbend.lagom.scaladsl.persistence.ReadSideProcessor
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraReadSide, CassandraSession}
import org.wex.cmsfs.collection.api.Collection

import scala.concurrent.{ExecutionContext, Future}

private[impl] class CollectionItemRepository(session: CassandraSession)(implicit ec: ExecutionContext) {

}

private[impl] class CollectionEventProcessor(session: CassandraSession, readSide: CassandraReadSide)(implicit ec: ExecutionContext)
  extends ReadSideProcessor[CollectionEvent] {
  private var insertItemCreatorStatement: PreparedStatement = null
  private var insertCollectionItemHistoryStatement: PreparedStatement = null

  override def buildHandler() = {
    readSide.builder[CollectionEvent]("collectionEventOffset")
      .setGlobalPrepare(createTables)
      .setPrepare(_ => prepareStatements())
      .setEventHandler[CollectionItemCreated](e => Future(List(insertItemCreator(e.event.cItem))))
      .setEventHandler[CollectionItemCompleted](e => Future(List(insertCollectionItemHistory(e.event.cItem, e.event.result))))
      .build
  }

  override def aggregateTags = CollectionEvent.Tag.allTags


  private def createTables() = {
    for {
      _ <- session.executeCreateTable(
        """
        CREATE TABLE IF NOT EXISTS collectItem (
          id UUID,
          cron text,
          mode text,
          monitorId int,
          connectorId int,
          PRIMARY KEY (id)
        )
      """)
      _ <- session.executeCreateTable(
        """
        CREATE TABLE IF NOT EXISTS collectItemHistory (
          itemId UUID,
          historyId timeuuid,
          status text,
          data text,
          PRIMARY KEY(itemId,historyId)
        )
        """)
    } yield Done
  }

  private def prepareStatements() = {
    for {
      insertItemCreator <- session.prepare(
        """
        INSERT INTO collectItem(id, cron, mode, monitorId, connectorId) VALUES (?, ?, ?, ?, ?)
      """)
      insertCollectionItemHistory <- session.prepare(
        """
          INSERT INTO collectItemHistory(itemId, historyId, status, data) VALUES (?, ?, ?, ?)
        """
      )
    } yield {
      insertItemCreatorStatement = insertItemCreator
      insertCollectionItemHistoryStatement = insertCollectionItemHistory
      Done
    }
  }

  private def insertItemCreator(item: Collection) = {
    insertItemCreatorStatement.bind(item.id.get, item.cron, item.mode, Integer.valueOf(item.monitorId), Integer.valueOf(item.connectorId))
  }

  private def insertCollectionItemHistory(item: Collection, result: String) = {
    println(item, "insertCollectionItemHistory")
    insertCollectionItemHistoryStatement.bind(item.id.get, UUIDs.timeBased(), "xx", result)
  }
}