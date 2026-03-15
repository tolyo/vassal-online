/*
 *
 * Copyright (c) 2000-2026 by Rodney Kinney, Brent Easton
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
package VASSAL.command;

import VASSAL.tools.SequenceEncoder;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Objects;

/**
 * Encodes and decodes compound VASSAL commands while managing the registered
 * single-command encoders that participate in the process.
 */
public final class CommandCodec implements CommandEncoder {
  private final char commandSeparator;
  private CommandEncoder[] commandEncoders = new CommandEncoder[0];

  public CommandCodec(char commandSeparator) {
    this.commandSeparator = commandSeparator;
  }

  public void add(CommandEncoder commandEncoder) {
    commandEncoders = ArrayUtils.add(commandEncoders, Objects.requireNonNull(commandEncoder));
  }

  public void remove(CommandEncoder commandEncoder) {
    commandEncoders = ArrayUtils.removeElement(commandEncoders, commandEncoder);
  }

  public CommandEncoder[] getCommandEncoders() {
    return commandEncoders.clone();
  }

  @Override
  public Command decode(String command) {
    if (command == null) {
      return null;
    }

    Command decoded;
    final SequenceEncoder.Decoder decoder =
      new SequenceEncoder.Decoder(command, commandSeparator);
    final String first = decoder.nextToken();

    if (command.equals(first)) {
      decoded = decodeSubCommand(first);
    }
    else {
      decoded = decode(first);
      while (decoder.hasMoreTokens()) {
        final Command next = decode(decoder.nextToken());
        decoded = decoded == null ? next : decoded.append(next);
      }
    }

    return decoded;
  }

  @Override
  public String encode(Command command) {
    if (command == null) {
      return null;
    }

    String encoded = encodeSubCommand(command);
    final Command[] subCommands = command.getSubCommands();

    if (subCommands.length > 0) {
      final SequenceEncoder sequenceEncoder =
        new SequenceEncoder(encoded, commandSeparator);
      for (final Command subCommand : subCommands) {
        final String subCommandString = encode(subCommand);
        if (subCommandString != null) {
          sequenceEncoder.append(subCommandString);
        }
      }
      encoded = sequenceEncoder.getValue();
    }

    return encoded;
  }

  private Command decodeSubCommand(String subCommand) {
    Command decoded = null;
    for (int i = 0; i < commandEncoders.length && decoded == null; ++i) {
      decoded = commandEncoders[i].decode(subCommand);
    }
    return decoded;
  }

  private String encodeSubCommand(Command command) {
    String encoded = null;
    for (int i = 0; i < commandEncoders.length && encoded == null; ++i) {
      encoded = commandEncoders[i].encode(command);
    }
    return encoded;
  }
}
