package duke;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import duke.command.AddCommand;
import duke.command.Command;
import duke.command.DeleteCommand;
import duke.command.ExitCommand;
import duke.command.FindCommand;
import duke.command.ListCommand;
import duke.command.MarkCommand;
import duke.command.UnmarkCommand;
import duke.exception.DukeException;
import duke.exception.DukeIllegalArgumentsException;
import duke.exception.DukeUnknownCommandException;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.Todo;

/**
 * Represents a parser for the Duke chat-bot that parses the user input and returns the corresponding command.
 */
public class Parser {
    private static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");

    /**
     * Parses the user input and returns the corresponding command.
     *
     * @param fullCommand The user input.
     * @return The corresponding command.
     * @throws DukeException If the user input is invalid.
     */
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
            return new AddCommand(new Todo(splitInput[1].trim()));
        case "deadline":
            if (splitInput.length == 1) {
                throw new DukeIllegalArgumentsException("The description of an deadline cannot be empty\n");
            }

            String[] splitInputBy = fullCommand.split("/by", 2);
            String[] splitDeadlineDescription = splitInputBy[0].split(" ", 2);
            if (splitDeadlineDescription.length == 1 || splitDeadlineDescription[1].equals("")) {
                throw new DukeIllegalArgumentsException("The description of an deadline cannot be empty\n");
            }
            if (splitInputBy.length == 1 || splitInputBy[1].equals("")) {
                throw new DukeIllegalArgumentsException(
                        "The deadline date must be specified! (after /by)\n");
            }

            try {
                return new AddCommand(new Deadline(splitDeadlineDescription[1].trim(),
                        LocalDateTime.parse(splitInputBy[1].trim(), dateTimeFormat)));
            } catch (DateTimeParseException e) {
                throw new DukeIllegalArgumentsException(
                        "The deadline date provided must be in the format: dd/mm/yyyy HHmm (in 24h format)\n");
            }
        case "event":
            if (splitInput.length == 1) {
                throw new DukeIllegalArgumentsException("The description of an event cannot be empty\n");
            }

            String[] splitInputFrom = fullCommand.split("/from", 2);
            String[] splitEventDescription = splitInputFrom[0].split(" ", 2);
            if (splitEventDescription.length == 1 || splitEventDescription[1].equals("")) {
                throw new DukeIllegalArgumentsException("The description of an event cannot be empty\n");
            }
            if (splitInputFrom.length == 1 || splitInputFrom[1].equals("")) {
                throw new DukeIllegalArgumentsException(
                        "The start time of the event must be specified! (after /from)\n");
            }

            String[] splitInputTo = splitInputFrom[1].split("/to", 2);

            if (splitInputTo.length == 1 || splitInputTo[1].equals("")) {
                throw new DukeIllegalArgumentsException(
                        "The end time of the event must be specified! (after /to)\n");
            }

            try {
                return new AddCommand(
                        new Event(splitInputFrom[0].trim(),
                                LocalDateTime.parse(splitInputTo[0].trim(), dateTimeFormat),
                                LocalDateTime.parse(splitInputTo[1].trim(), dateTimeFormat)));
            } catch (DateTimeParseException e) {
                throw new DukeIllegalArgumentsException(
                        "The event dates provided must be in the format: dd/mm/yyyy HHmm (in 24h format)\n");
            }
        case "delete":
            return new DeleteCommand(Integer.parseInt(splitInput[1]) - 1);
        case "find":
            if (splitInput.length == 1) {
                throw new DukeIllegalArgumentsException("The keyword to search for cannot be empty!\n");
            }
            return new FindCommand(splitInput[1].trim());
        default:
            throw new DukeUnknownCommandException();
        }
    }
}
