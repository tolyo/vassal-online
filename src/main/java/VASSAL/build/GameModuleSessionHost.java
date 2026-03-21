/*
 *
 * Copyright (c) 2000-2012 by Rodney Kinney, Brent Easton
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
package VASSAL.build;

import VASSAL.build.module.ServerConnection;
import VASSAL.build.module.SessionConnection;
import VASSAL.chat.AddressBookServerConfigurer;
import VASSAL.chat.ChatServerConnection;
import VASSAL.chat.ChatServerFactory;
import VASSAL.chat.DynamicClient;
import VASSAL.chat.HybridClient;
import VASSAL.chat.node.NodeClient;
import VASSAL.chat.node.NodeClientFactory;
import VASSAL.chat.node.OfficialNodeClientFactory;
import VASSAL.chat.node.PrivateNodeClientFactory;
import VASSAL.chat.peer2peer.P2PClientFactory;
import VASSAL.chat.ui.ChatServerControls;
import VASSAL.i18n.Resources;
import VASSAL.preferences.Prefs;

/**
 * Desktop-backed multiplayer session host that keeps concrete chat transport out of {@link
 * GameModule}.
 */
final class GameModuleSessionHost {
  private final GameModule gameModule;
  private ServerConnection serverConnection;
  private ChatServerControls serverControls;

  GameModuleSessionHost(GameModule gameModule) {
    this.gameModule = gameModule;
  }

  void initialize() {
    final OfficialNodeClientFactory officialNodeClientFactory = new OfficialNodeClientFactory();

    ChatServerFactory.register(OfficialNodeClientFactory.OFFICIAL_TYPE, officialNodeClientFactory);
    ChatServerFactory.register(
        PrivateNodeClientFactory.PRIVATE_TYPE, new PrivateNodeClientFactory());
    ChatServerFactory.register(P2PClientFactory.P2P_TYPE, new P2PClientFactory());

    ChatServerFactory.register(NodeClientFactory.NODE_TYPE, officialNodeClientFactory);
    ChatServerFactory.register("jabber", officialNodeClientFactory); // NON-NLS

    serverConnection = new DynamicClient();
    final AddressBookServerConfigurer config =
        new AddressBookServerConfigurer(
            "ServerSelected", "", (HybridClient) serverConnection); // NON-NLS
    Prefs.getGlobalPrefs().addOption(Resources.getString("Chat.server"), config); // $NON-NLS-1$
    serverControls = new ChatServerControls();
    serverControls.addTo(gameModule);

    serverConnection.addPropertyChangeListener(
        SessionConnection.CONNECTION_LOST,
        e -> gameModule.showWarning("GameModule.disconnect_warning")); // NON-NLS
  }

  SessionConnection getSessionConnection() {
    return serverConnection;
  }

  ServerConnection getServerConnection() {
    return serverConnection;
  }

  ChatServerControls getServerControls() {
    return serverControls;
  }

  ChatServerConnection getChatServerConnection() {
    if (serverConnection instanceof ChatServerConnection) {
      return (ChatServerConnection) serverConnection;
    }
    return null;
  }

  boolean isCurrentUserSessionOwner() {
    if (serverConnection == null || !serverConnection.isConnected()) {
      return true;
    }

    final ChatServerConnection connection;
    if (serverConnection instanceof HybridClient) {
      connection = ((HybridClient) serverConnection).getDelegate();
    } else if (serverConnection instanceof ChatServerConnection) {
      connection = (ChatServerConnection) serverConnection;
    } else {
      return true;
    }

    if (connection instanceof NodeClient) {
      return ((NodeClient) connection).isOwner();
    }

    return true;
  }
}
