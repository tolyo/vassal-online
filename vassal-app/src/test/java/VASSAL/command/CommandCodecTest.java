package VASSAL.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;

class CommandCodecTest {
  private static final char COMMAND_SEPARATOR = 27;

  @Test
  void encodeCombinesCompoundCommands() {
    final CommandCodec codec = new CommandCodec(COMMAND_SEPARATOR);
    codec.add(new TestEncoder());

    final NamedCommand first = new NamedCommand("one");
    first.append(new NamedCommand("two"));

    assertThat(codec.encode(first), is(equalTo("one" + COMMAND_SEPARATOR + "two")));
  }

  @Test
  void decodeRebuildsCompoundCommands() {
    final CommandCodec codec = new CommandCodec(COMMAND_SEPARATOR);
    codec.add(new TestEncoder());

    final Command decoded = codec.decode("one" + COMMAND_SEPARATOR + "two");

    assertThat(decoded, is(instanceOf(NamedCommand.class)));
    assertThat(((NamedCommand) decoded).getName(), is("one"));
    assertThat(decoded.getSubCommands().length, is(1));
    assertThat(decoded.getSubCommands()[0], is(instanceOf(NamedCommand.class)));
    assertThat(((NamedCommand) decoded.getSubCommands()[0]).getName(), is("two"));
  }

  @Test
  void returnsNullWhenNoEncoderMatches() {
    final CommandCodec codec = new CommandCodec(COMMAND_SEPARATOR);

    assertThat(codec.decode("unknown"), is(nullValue()));
  }

  private static final class TestEncoder implements CommandEncoder {
    @Override
    public Command decode(String command) {
      return command.startsWith("skip:") ? null : new NamedCommand(command);
    }

    @Override
    public String encode(Command c) {
      return c instanceof NamedCommand ? ((NamedCommand) c).getName() : null;
    }
  }

  private static final class NamedCommand extends Command {
    private final String name;

    private NamedCommand(String name) {
      this.name = name;
    }

    private String getName() {
      return name;
    }

    @Override
    protected void executeCommand() {}

    @Override
    protected Command myUndoCommand() {
      return null;
    }
  }
}
