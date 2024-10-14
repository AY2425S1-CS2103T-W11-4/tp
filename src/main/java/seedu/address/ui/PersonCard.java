package seedu.address.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;

/**
 * An UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Person person;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    Label schedule;
    @FXML
    Label note;
    @FXML
    Label reminder;
    @FXML
    FlowPane tags;

    /**
     * Creates a {@code PersonCode} with the given {@code Person} and index to display.
     */
    public PersonCard(Person person, int displayedIndex) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ". ");
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        address.setText(person.getAddress().value);
        email.setText(person.getEmail().value);

        setDateField(person);
        setNoteField(person);
        setReminderField(person);

        person.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));
    }

    /**
     * Sets the text for the reminder field.
     * @param person
     */
    private void setReminderField(Person person) {
        if (person.getReminder() != null && !person.getReminder().toString().isEmpty()) {
            String formattedDateTime = LocalDateTime.parse(
                            person.getReminder().getAppointmentDateTime(),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"))
                    .format(DateTimeFormatter.ofPattern("MMM d yyyy, h:mm a"));

            // Combine formatted date-time with the reminder time
            reminder.setText(String.format("Reminder on %s, %s before",
                    formattedDateTime,
                    person.getReminder().getReminderTime()));
        } else {
            reminder.setText("");
        }
    }

    /**
     * Sets the text for the notes field.
     * @param person
     */
    private void setNoteField(Person person) {
        if (person.getSchedule().getNotes() == null || person.getSchedule().getNotes().isEmpty()) {
            note.setText("");
        } else {
            note.setText("Notes: " + person.getSchedule().getNotes());
        }
    }

    /**
     * Sets the text for the date field.
     * @param person
     */
    private void setDateField(Person person) {
        if (person.getSchedule().toString().isEmpty()) {
            schedule.setText(person.getSchedule().toString());
        } else {
            schedule.setText(
                    LocalDateTime.parse(person.getSchedule().toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"))
                            .format(DateTimeFormatter.ofPattern("MMM d yyyy, h:mm a")));
        }
    }
}
