package VASSAL.chat;

import VASSAL.command.Command;
import VASSAL.command.CommandEncoder;
import org.junit.jupiter.api.Test;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class CommandDecoderTest {
  @Test
  void retainsPublicNoArgConstructorForBinaryCompatibility() throws NoSuchMethodException {
    assertThat(Modifier.isPublic(CommandDecoder.class.getConstructor().getModifiers()), is(true));
  }

  @Test
  void forwardsDecodedCommandsToExecutor() {
    final AtomicReference<Command> executed = new AtomicReference<>();
    final TestCommand decoded = new TestCommand();
    final CommandDecoder commandDecoder =
      new CommandDecoder(new StubEncoder(decoded), executed::set);

    commandDecoder.propertyChange(
      new PropertyChangeEvent(this, ChatServerConnection.INCOMING_MSG, null, "encoded")
    );

    assertThat(executed.get(), is(decoded));
  }

  @Test
  void ignoresMessagesThatDoNotDecode() {
    final AtomicReference<Command> executed = new AtomicReference<>();
    final CommandDecoder commandDecoder =
      new CommandDecoder(new StubEncoder(null), executed::set);

    commandDecoder.propertyChange(
      new PropertyChangeEvent(this, ChatServerConnection.INCOMING_MSG, null, "encoded")
    );

    assertThat(executed.get(), is(nullValue()));
  }

  private static final class StubEncoder implements CommandEncoder {
    private final Command decoded;

    private StubEncoder(Command decoded) {
      this.decoded = decoded;
    }

    @Override
    public Command decode(String command) {
      return decoded;
    }

    @Override
    public String encode(Command c) {
      return null;
    }
  }

  private static final class TestCommand extends Command {
    @Override
    protected void executeCommand() {
    }

    @Override
    protected Command myUndoCommand() {
      return null;
    }
  }
}
