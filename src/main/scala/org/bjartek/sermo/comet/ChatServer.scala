/*
 * Copyright 2007-2009 WorldWide Conferencing, LLC
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

import _root_.scala.actors.Actor
import Actor._
import _root_.net.liftweb._
import http._
import util._
import Helpers._
import _root_.java.util.Date
import _root_.org.bjartek.sermo.model._

/**
 * A chat server.  It gets messages and returns them
 */

object ChatServer extends Actor with ListenerManager {
  private var chats: List[Message] = Message.findAll().reverse

  override def lowPriority = {
    case ChatServerMsg(user, msg) if msg.length > 0 =>
      val message = Message.create.user(user).content(msg).createdAt(now)
        message.validate match {
          case Nil => 
            message.save
            chats ::= message
            chats = chats.take(50)
            updateListeners()
           case x => error(x.toString)
       }
    case _ =>
  }

  def createUpdate = ChatServerUpdate(chats.take(15))
  this.start
}

case class ChatServerMsg(user: User, msg: String)
case class ChatServerUpdate(msgs: List[Message])

