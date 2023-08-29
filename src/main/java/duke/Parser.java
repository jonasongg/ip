package duke;

import duke.command.*;
import duke.exception.DukeException;
import duke.exception.DukeIllegalArgumentsException;
import duke.exception.DukeUnknownCommandException;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.Todo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Parser {
    private static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");

    public static Command parse(String fullCommand) throws DukeException {
        String[] splitInput = fullCommand.split(" ", 2);
        String commandWord = splitInput[0];
        switch (commandWord) {
            case "bye":
                return new ExitCommand();
            case "list":
                return new ListCommand();
            case "mark":
                return new MarkCommand(Integer.parseInt(splitInput[1]) - 1);
            case "unmark":
                return new UnmarkCommand(Integer.parseInt(splitInput[1]) - 1);
            case "todo":
                if (splitInput.length == 1) {
                    throw new DukeIllegalArgumentsException("The description of a todo cannot be empty\n");
                }
                return new AddCommand(new Todo(splitInput[1]));
            case "deadline":
                if (splitInput.length == 1 || !splitInput[1].contains(" /by ")) {
                    throw new DukeIllegalArgumentsException("The description of a deadline cannot be empty\n");
                }

                String[] splitInputBy = splitInput[1].split(" /by ", 2);

                if (splitInputBy.length == 1) {
                    throw new DukeIllegalArgumentsException("A deadline must be specified! (after /by)\n");
                }

                try {
                    return new AddCommand(
                            new Deadline(splitInputBy[0], LocalDateTime.parse(splitInputBy[1], dateTimeFormat)));
                } catch (DateTimeParseException e) {
                    throw new DukeIllegalArgumentsException(
                            "The deadline must be in the format: dd/mm/yyyy HHmm (in 24h format)\n");
                }
            case "event":
                if (splitInput.length == 1 || !splitInput[1].contains(" /from ")) {
                    throw new DukeIllegalArgumentsException("The description of an event cannot be empty\n");
                }

                String[] splitInputFrom = splitInput[1].split(" /from ", 2);

                if (splitInputFrom.length == 1) {
                    throw new DukeIllegalArgumentsException(
                            "The start time of the event must be specified! (after /from)\n");
                }

                String[] splitInputTo = splitInputFrom[1].split(" /to ", 2);

                if (splitInputTo.length == 1) {
                    throw new DukeIllegalArgumentsException(
                            "The end time of the event must be specified! (after /to)\n");
                }

                try {
                    return new AddCommand(
                            new Event(splitInputFrom[0], LocalDateTime.parse(splitInputTo[0], dateTimeFormat),
                                    LocalDateTime.parse(splitInputTo[1], dateTimeFormat)));
                } catch (DateTimeParseException e) {
                    throw new DukeIllegalArgumentsException(
                            "The event must be in the format: dd/mm/yyyy HHmm (in 24h format)\n");
                }
            case "delete":
                return new DeleteCommand(Integer.parseInt(splitInput[1]) - 1);
            default:
                throw new DukeUnknownCommandException();
        }
    }
}
