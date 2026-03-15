/*
 * Copyright (c) 2000-2007 by Rodney Kinney
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License (LGPL) as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, copies are available
 * at http://www.opensource.org.
 */
package VASSAL.chat;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import VASSAL.command.Command;
import VASSAL.command.CommandEncoder;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Listens for incoming messages (PropertyChangeEvents with name {ChatServerConnection.INCOMING_MSG}) and
 * interprets the message as a command to be executed
 *
 * @author rodneykinney
 *
 */
public class CommandDecoder implements PropertyChangeListener {
  private final CommandEncoder commandDecoder;
  private final Consumer<Command> commandExecutor;

  public CommandDecoder(CommandEncoder commandDecoder, Consumer<Command> commandExecutor) {
    this.commandDecoder = Objects.requireNonNull(commandDecoder);
    this.commandExecutor = Objects.requireNonNull(commandExecutor);
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    final Command c = commandDecoder.decode((String) evt.getNewValue());
    if (c != null) {
      commandExecutor.accept(c);
    }
  }
}
