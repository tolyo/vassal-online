package VASSAL.build;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import VASSAL.tools.DataArchive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;

@Disabled
public class MockModuleTest {
  private static boolean initialized = false;

  @SuppressWarnings("unchecked")
  @BeforeEach
  public void init() throws Exception {
    if (initialized) {
      return;
    }

    final GameModule module = mock(GameModule.class);
    final DataArchive arch = mock(DataArchive.class);

    when(module.getDataArchive()).thenReturn(arch);

    GameModule.init(module);
    initialized = true;
  }
}
