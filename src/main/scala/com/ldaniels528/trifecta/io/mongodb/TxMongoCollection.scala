package com.ldaniels528.trifecta.io.mongodb

import com.ldaniels528.trifecta.modules.MongoResultHandler._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoCollection
import net.liftweb.json.JsonAST.JValue

/**
 * Trifecta MongoDB Collection
 * @author Lawrence Daniels <lawrence.daniels@gmail.com>
 */
case class TxMongoCollection(mc: MongoCollection) {
  var writeConcern: WriteConcern = WriteConcern.Safe // Safe | JournalSafe

  /**
   * Retrieves documents matching the given criteria
   * @param criteria the given criteria
   * @return the resultant documents
   */
  def find(criteria: JValue): Iterator[JValue] = {
    mc.find(toDocument(criteria)).map(documentToJson)
  }

  /**
   * Retrieves a document matching the given criteria
   * @param criteria the given criteria
   * @return the resultant document
   */
  def findOne(criteria: JValue, fields: JValue): Option[JValue] = {
    mc.findOne(o = toDocument(criteria), fields = toDocument(fields)) map documentToJson
  }

  /**
   * Retrieves a document matching the given criteria
   * @param criteria the given criteria
   * @return the resultant document
   */
  def findOne(criteria: JValue): Option[JValue] = {
    mc.findOne(o = toDocument(criteria)) map documentToJson
  }

  /**
   * Inserts the given values
   * @param values the given values
   * @tparam T the template type of the values
   * @return the result of the operation
   */
  def insert[T](values: JValue): WriteResult = {
    mc.insert(toDocument(values), writeConcern)
  }

}
