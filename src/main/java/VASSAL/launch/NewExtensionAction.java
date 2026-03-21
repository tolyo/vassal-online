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
package VASSAL.launch;

import VASSAL.build.GameModule;
import VASSAL.build.module.ModuleExtension;
import VASSAL.i18n.Resources;
import VASSAL.tools.ArchiveWriter;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;

/**
 * Creates a new module extension and opens an extension edit window
 *
 * @author rodneykinney
 */
public class NewExtensionAction extends GameModuleAction {
  private static final long serialVersionUID = 1L;

  public NewExtensionAction(Component comp) {
    super(Resources.getString("Editor.new_extension"), comp);
  }

  @Override
  public void performAction(ActionEvent e) {
    final ModuleExtension ext =
        new ModuleExtension(new ArchiveWriter((String) null, ".vmdx")); // NON-NLS
    ext.build();
    final GameModule gameModule = GameModule.getGameModule();
    final ExtensionEditorWindow w = new ExtensionEditorWindow(gameModule, ext);
    final Rectangle frameBounds = gameModule.getMainWindowBounds();
    final Rectangle screenBounds = gameModule.getMainWindowScreenBounds();
    w.setLocation(0, frameBounds.y + frameBounds.height);
    w.setSize(screenBounds.width / 2, w.getHeight());
    w.setVisible(true);
  }
}
