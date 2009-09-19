package org.bjartek.sermo.model

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util._
import _root_.net.liftweb.textile._
import _root_.org.bjartek.sermo.model._

object Message extends Message with LongKeyedMetaMapper[Message]

class Message  extends LongKeyedMapper[Message] with IdPK {
  def getSingleton = Message 
  
  object user extends MappedLongForeignKey(this, User)

  object createdAt extends MappedDateTime(this)

  object content extends MappedTextarea(this, 2048) {
    override def textareaRows  = 10
    override def textareaCols = 50
    override def displayName = "Message"
  }

  def contentAsHtml = TextileParser.paraFixer(TextileParser.toHtml(content, Empty))
}
