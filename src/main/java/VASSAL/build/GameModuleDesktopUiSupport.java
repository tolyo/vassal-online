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

import VASSAL.build.module.GlobalOptions;
import VASSAL.build.module.map.Flare;
import VASSAL.i18n.Resources;
import VASSAL.preferences.Prefs;
import VASSAL.tools.ProblemDialog;
import VASSAL.tools.ReadErrorDialog;
import VASSAL.tools.WarningDialog;
import VASSAL.tools.WriteErrorDialog;
import VASSAL.tools.filechooser.FileChooser;
import java.awt.Frame;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/** Desktop-backed dialog, chooser, and other non-window UI support for {@link GameModule}. */
final class GameModuleDesktopUiSupport {
  private final GameModule gameModule;

  GameModuleDesktopUiSupport(GameModule gameModule) {
    this.gameModule = gameModule;
  }

  void showAlert(String message) {
    SwingUtilities.invokeLater(
        () ->
            JOptionPane.showMessageDialog(
                GameModule.getGameModule() == null ? null : gameModule.getDialogOwner(), message));
  }

  void playAudioClip(String clipName) {
    try {
      if (!GlobalOptions.getInstance().isSoundGlobalMute()
          && !gameModule.getGameState().isFastForwarding()) {
        gameModule.getDataArchive().getCachedAudioClip(clipName).play();
      }
    } catch (IOException e) {
      gameModule.showReadError(e, clipName);
    }
  }

  void startFlare(Flare flare, Point clickPoint) {
    flare.setClickPoint(clickPoint);
    flare.startAnimation(false);
  }

  void showOutdatedModule(String usage) {
    ProblemDialog.showOutdatedModule(usage);
  }

  void showWarning(String messageKey, Object... args) {
    WarningDialog.show(messageKey, args);
  }

  int confirmSaveModule() {
    return JOptionPane.showConfirmDialog(
        gameModule.getDialogOwner(),
        Resources.getString("GameModule.save_module"),
        "",
        JOptionPane.YES_NO_CANCEL_OPTION);
  }

  void showReadError(IOException error, String target) {
    ReadErrorDialog.error(error, target);
  }

  void showWriteError(IOException error, String target) {
    WriteErrorDialog.error(error, target);
  }

  void showWriteError(IOException error, File file, String messageKey) {
    WriteErrorDialog.showError(gameModule.getDialogOwner(), error, file, messageKey);
  }

  void updateButtonLabels(GameModule gameModule) {
    SwingUtilities.invokeLater(gameModule::finishMutableButtonLabelUpdate);
  }

  void initializePrefsDialog(Prefs prefs, Frame dialogOwner) {
    prefs.getEditor().initDialog(dialogOwner);
  }

  FileChooser getSavedGameFileChooser(GameModule gameModule, FileChooser existingChooser) {
    return prepareFileChooser(
        existingChooser,
        () ->
            FileChooser.createFileChooser(
                gameModule.getDialogOwner(),
                gameModule.getGameState().getSavedGameDirectoryPreference()));
  }

  FileChooser getEditorImageFileChooser(GameModule gameModule, FileChooser existingChooser) {
    return prepareFileChooser(
        existingChooser,
        () ->
            FileChooser.createFileChooser(
                gameModule.getDialogOwner(),
                gameModule.getGameState().getEditorImageDirectoryPreference()));
  }

  FileChooser getEditorSoundFileChooser(GameModule gameModule, FileChooser existingChooser) {
    return prepareFileChooser(
        existingChooser,
        () ->
            FileChooser.createFileChooser(
                gameModule.getDialogOwner(),
                gameModule.getGameState().getEditorImageDirectoryPreference()));
  }

  private FileChooser prepareFileChooser(
      FileChooser existingChooser, Supplier<FileChooser> chooserSupplier) {
    if (existingChooser == null) {
      return chooserSupplier.get();
    }

    existingChooser.resetChoosableFileFilters();
    existingChooser.rescanCurrentDirectory();
    return existingChooser;
  }
}
