package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;
import java.util.Set;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.Reminder;
import seedu.address.model.person.Schedule;

/**
 * Sets a reminder for an appointment for a person in the address book
 * The command allows users to specify how far in advance they want to be reminded
 */
public class ReminderCommand extends Command {
    public static final String COMMAND_WORD = "reminder";

    public static final String MESSAGE_SUCCESS = "Reminder set successfully for: %s. "
            + "You will be reminded %s before.";
    public static final String MESSAGE_INVALID_REMINDER_TIME = "Invalid reminder time: "
            + "Please enter a valid time expression (e.g '1 day', '2 hours').";
    public static final String MESSAGE_INVALID_NAME = "Person not found";
    public static final String MESSAGE_INVALID_APPOINTMENT = "Appointment not found";
    public static final String MESSAGE_REMINDER_EXISTS = "This reminder already exists";
    private String name;
    private String reminderTime;

    /**
     * Constructs a ReminderCommand to set a reminder for a specific person and appointment.
     *
     * @param name The name of the person.
     * @param reminderTime The reminder time.
     */
    public ReminderCommand(String name, String reminderTime) {
        this.name = name;
        this.reminderTime = reminderTime;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();
        Person personToEdit = findPersonByName(lastShownList, name);

        if (personToEdit == null) {
            throw new CommandException(MESSAGE_INVALID_NAME);
        }

        // Check if the appointment exists
        Set<Schedule> appointmentSchedule = personToEdit.getSchedules();
        if (appointmentSchedule.toString().isEmpty()) {
            throw new CommandException(MESSAGE_INVALID_APPOINTMENT);
        }

        // Check if the reminder time is valid (e.g 1 hour or 1 day)
        if (!isValidReminderTime(reminderTime)) {
            throw new CommandException(MESSAGE_INVALID_REMINDER_TIME);
        }

        // Check if the reminder already exists
        Reminder existingReminder = personToEdit.getReminder();
        if (existingReminder != null
                && existingReminder.getReminderTime().equals(reminderTime)) {
            throw new CommandException(MESSAGE_REMINDER_EXISTS);
        }

        Reminder newReminder = new Reminder(reminderTime);
        Person editedPerson = new Person(
                personToEdit.getName(), personToEdit.getPhone(), personToEdit.getEmail(),
                personToEdit.getAddress(), personToEdit.getSchedules(), newReminder, personToEdit.getTags());
        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_SUCCESS, name, this.reminderTime));
    }

    /**
     * Finds a person by name from the given list.
     *
     * @param personList The list of persons to search.
     * @param name The name of the person to find.
     * @return The person with the specified name, or null if not found
     */
    private Person findPersonByName(List<Person> personList, String name) {
        return personList.stream()
                .filter(person -> person.getName().toString().equals(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Validates the reminderTime format.
     *
     * @param reminderTime The reminder time to validate
     * @return True if the reminder time format is valid, false otherwise
     */
    private boolean isValidReminderTime(String reminderTime) {
        return reminderTime.matches("\\d+\\s(day|hour)(s)?");
    }

    @Override
    public boolean equals(Object other) {
        // Short circuit if same object
        if (other == this) {
            return false;
        }

        // instanceOf handles nulls
        if (!(other instanceof ReminderCommand)) {
            return false;
        }

        // State check
        ReminderCommand otherCommand = (ReminderCommand) other;
        return name.equals(otherCommand.name)
                && reminderTime.equals(otherCommand.reminderTime);
    }
}
