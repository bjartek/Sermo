/*
 * Copyright 2007-2008 WorldWide Conferencing, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.bjartek.sermo.comet

import _root_.scala.actors._
import Actor._
import _root_.net.liftweb._
import http._
import util._
import Helpers._
import _root_.scala.xml._
import S._
import SHtml._
import js._
import JsCmds._
import JE._
import _root_.net.liftweb.textile._
import textile.TextileParser
import net.liftweb.http.js.jquery.JqJsCmds._
import _root_.org.bjartek.sermo.model._

class Chat extends CometActor with CometListener {
  private val user = User.currentUser.open_!
  
  private var chats: List[Message] = Nil
  private lazy val infoId = uniqueId + "_info"
  private lazy val infoIn = uniqueId + "_in"
  private lazy val inputArea = findKids(defaultXml, "chat", "input")
  private lazy val bodyArea = findKids(defaultXml, "chat", "body")
  private lazy val singleLine = deepFindKids(bodyArea, "chat", "list")

  // handle an update to the chat lists
  // by diffing the lists and then sending a partial update
  // to the browser
  override def lowPriority = {
    case ChatServerUpdate(value) =>
      val update = (value -- chats).reverse.map(b => AppendHtml(infoId, line(b)))
      partialUpdate(update)
      chats = value
  }

  // render the input area by binding the
  // appropriate dynamically generated code to the
  // view supplied by the template
  override lazy val fixedRender: Box[NodeSeq] =
    ajaxForm(After(100, SetValueAndFocus(infoIn, "")),
           bind("chat", inputArea,
                "input" -> text("", sendMessage _, "id" -> infoIn)))

  // send a message to the chat server
  private def sendMessage(msg: String) = ChatServer ! ChatServerMsg(user, msg.trim)

  // display a line
  private def line(m: Message) = bind("list", singleLine,
                                       "when" -> hourFormat(m.createdAt),
                                       "who" -> m.user.obj.open_!.username,
                                       "msg" -> toHtml(m.content))

  private def toHtml(msg: String): NodeSeq = TextileParser.paraFixer(TextileParser.toHtml(msg, Empty))


  // display a list of chats
  private def displayList(in: NodeSeq): NodeSeq = chats.reverse.flatMap(line)

  // render the whole list of chats
  override def render =
    bind("chat", bodyArea,
       "name" -> user.username,
       AttrBindParam("id", Text(infoId), "id"),
       "list" -> displayList _)

  // register as a listener
  def registerWith = ChatServer

}
