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

import static VASSAL.preferences.Prefs.MAIN_WINDOW_HEIGHT;
import static VASSAL.preferences.Prefs.MAIN_WINDOW_REMEMBER;
import static VASSAL.preferences.Prefs.MAIN_WINDOW_WIDTH;

import VASSAL.build.module.Chatter;
import VASSAL.build.module.GlobalOptions;
import VASSAL.launch.PlayerWindow;
import VASSAL.preferences.PositionOption;
import VASSAL.preferences.Prefs;
import VASSAL.tools.KeyStrokeSource;
import VASSAL.tools.swing.SwingUtils;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;

/** Desktop-backed window host for {@link GameModule}. */
final class GameModuleDesktopWindowHost {
  private final PlayerWindow frame;

  GameModuleDesktopWindowHost(PlayerWindow frame) {
    this.frame = frame;
  }

  void initializeFrame(GameModule gameModule) {
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    frame.addWindowListener(
        new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent e) {
            gameModule.quit();
          }
        });

    gameModule.addKeyStrokeSource(
        new KeyStrokeSource(frame.getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW));
  }

  void layoutWindow(GameModule gameModule) {
    final Rectangle screen = SwingUtils.getScreenBounds(frame);

    if (GlobalOptions.getInstance().isUseSingleWindow()) {
      frame.setLocation(screen.getLocation());

      final Prefs p = Prefs.getGlobalPrefs();

      if (Boolean.FALSE.equals(p.getOption(MAIN_WINDOW_REMEMBER).getValue())) {
        p.getOption(MAIN_WINDOW_WIDTH).setValue(-1);
        p.getOption(MAIN_WINDOW_HEIGHT).setValue(-1);
      }

      final int ph = (Integer) p.getOption(MAIN_WINDOW_HEIGHT).getValue();
      final int pw = (Integer) p.getOption(MAIN_WINDOW_WIDTH).getValue();
      final int h = (ph > 0) ? ph : screen.height;
      final int w = (pw > 0) ? pw : screen.width;
      frame.setSize(w, h / 3);
    } else {
      final String key = "BoundsOfGameModule"; // NON-NLS
      final Rectangle r = new Rectangle(0, 0, screen.width, screen.height / 4);
      gameModule.getPrefs().addOption(new PositionOption(key, frame, r));
    }
  }

  JComponent getControlPanel(GameModule gameModule) {
    return frame.getControlPanel();
  }

  JToolBar getToolBar(GameModule gameModule) {
    return frame.getToolBar();
  }

  void updateTitle(GameModule gameModule, String title) {
    frame.setTitle(title);
  }

  Frame getDialogOwner(GameModule gameModule) {
    return frame;
  }

  void setVisible(GameModule gameModule, boolean visible) {
    frame.setVisible(visible);
  }

  void setCursor(GameModule gameModule, Cursor cursor) {
    frame.setCursor(cursor);
  }

  Point getLocation(GameModule gameModule) {
    return frame.getLocation();
  }

  Rectangle getBounds(GameModule gameModule) {
    return frame.getBounds();
  }

  Rectangle getScreenBounds(GameModule gameModule) {
    return SwingUtils.getScreenBounds(frame);
  }

  void setSize(GameModule gameModule, int width, int height) {
    frame.setSize(width, height);
  }

  void attachChatter(GameModule gameModule, Chatter chatter) {
    frame.addChatter(chatter);
  }

  JMenuBar getMenuBar(GameModule gameModule) {
    return frame.getJMenuBar();
  }
}
