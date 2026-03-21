package VASSAL.build.module;

/*
 *
 * Copyright (c) 2000-2003 by Rodney Kinney
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
/** Represents the connection to a live server */
public interface ServerConnection extends SessionConnection {
  /** Compatibility alias for the session connection status property. */
  String CONNECTED = SessionConnection.CONNECTED;

  /** Compatibility alias for the connection lost property. */
  String CONNECTION_LOST = SessionConnection.CONNECTION_LOST;
}
