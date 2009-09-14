package org.bjartek.sermo.model

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util._
import _root_.org.bjartek.sermo.model._

/**
 * The singleton that has methods for accessing the database
 */
object Message extends Message with LongKeyedMetaMapper[Message]

class Message  extends LongKeyedMapper[Author] with IdPK {
  def getSingleton = Message // what's the "meta" server
  
  object user extends MappedLongForeignKey(this, User)

  object createdAt extends MappedDateTime(this)

  // define an additional field for a personal essay
  object text extends MappedTextarea(this, 2048) {
    override def textareaRows  = 10
    override def textareaCols = 50
    override def displayName = "Message"
  }
}
