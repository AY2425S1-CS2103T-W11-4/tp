---
layout: page
title: Developer Guide
---
* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* This project is based on the AddressBook-Level3 project created by the [SE-EDU initiative](https://se-education.org).
* Multiple areas of the code were completed with the help of generative AI including ChatGPT, Codeium and ClaudeAI.
--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

<div markdown="span" class="alert alert-primary">

:bulb: **Tip:** The `.puml` files used to create diagrams in this document `docs/diagrams` folder. Refer to the [_PlantUML Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit diagrams.
</div>

### Architecture

<img src="images/ArchitectureDiagram.png" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete John`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete John Doe")` API call as an example.

![Interactions Inside the Logic Component for the `delete John Doe` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</div>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<img src="images/ParserClasses.png" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<img src="images/ModelClassDiagram.png" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<div markdown="span" class="alert alert-info">:information_source: **Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<img src="images/BetterModelClassDiagram.png" width="450" />

</div>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Schedule Feature

#### Overview
The `schedule` command helps to set up appointments for a client, including any relevant notes.

The sequence diagram below models the interactions between the different components of PhysioPal for the execution of the `schedule` command.

![ScheduleSequenceDiagram](images/ScheduleSequenceDiagram.png)

#### Details
1. The user executes the command `schedule John Doe d/2024-10-28 1200 note/First Appointment` to schedule a new appointment for a client named john doe.
2. The `ScheduleCommandParser` object calls its `parse` method to interpret the user input.
3. A `ScheduleCommand` object is created.
4. The `ScheduleCommandParser` object returns the `ScheduleCommand` object.
5. The `LogicManager` object calls the `execute` method of `ScheduleCommand`.
6. The `execute` method then invokes the `setPerson` method of its `Model` argument to create a new appointment with the specified details.
7. The `execute` method then invokes the `updateFilteredPersonList` method of its `Model` argument to update the view of PhysioPal to show all contacts with their appointments.
8. The `execute` method returns a `CommandResult` which contains data indicating the completion of the `ScheduleCommand`.

#### Example Usage
1. User inputs the command `schedule Alice Tan d/2024-10-29 1200 note/Second Appointment`.
2. This creates an appointment for a person named "alice tan" on October 29, 2024, at 12:00pm, with the note "Second Appointment" attached.
3. The new appointment is then displayed in the UI, reflecting the updated schedule for "alice tan".

### View Client Feature

#### Overview
The `view` command enables users to access and display detailed information for a specific client in the address book.

The sequence diagram below models the interactions between the different components of PhysioPal for the execution of the `view` command.

![ViewClientSequenceDiagram](images/ViewSequenceDiagram.png)

#### Details
1. The user executes the command `view John Doe` to display the details of a client named john doe.
2. The `LogicManager` object receives this command and calls the `parseCommand` method of `AddressBookParser` to interpret the input.
3. The `AddressBookParser` then creates a `ViewClientCommandParser` object to handle parsing.
4. The `ViewClientCommandParser` object calls its `parse` method to extract the client name, and the `ClientUtil` utility class is used to confirm the full name "john doe."
5. The `ViewClientCommandParser` creates a `ViewClientCommand` object.
6. The `ViewClientCommandParser` returns the `ViewClientCommand` object to `AddressBookParser`, which then returns it to `LogicManager`.
7. The `LogicManager` object invokes the `execute` method of `ViewClientCommand`.
8. The `execute` method calls `getFilteredPersonList` on the `Model` to retrieve the details of the specified client.
9. The `execute` method creates a `CommandResult` object containing the client’s information.
10. The `CommandResult` object is returned to `LogicManager`, which then displays the client’s information to the user.

#### Example Usage
1. User inputs the command `view Alice Tan`.
2. The system displays the details of "alice tan," including name, contact information, address, condition, and any scheduled appointments.
3. This information is shown in a pop-up or a designated UI section for easy access by the user.

### Reminder Note Feature

#### Overview
The `reminder` command helps to set a reminder note for a client's most upcoming appointment.

The reminder diagram below models the interaction between the different components of PhysioPal for the execution of the `reminder` command.

![ReminderSequenceDiagram](images/ReminderSequenceDiagram.png)

#### Details
1. The user executes the command `reminder John Doe r/1 day` to set a reminder note for a client named john doe's appointment.
2. The `ReminderCommandParser` object calls its parse method to interpret the user input.
3. A `ReminderCommand` object is created.
4. The `ReminderCommandParser` object returns the `ReminderCommand` object.
5. The `LogicManager` object calls the `execute` method of `ReminderCommand`.
6. The `execute` method then invokes the `setPerson` method of its `Model` argument to create a reminder note with the specified reminder time.
7. The `execute` method then invokes the `updateFilteredPersonList` method of its `Model` argument to update the view of PhysioPal to show all contacts with their reminders.
8. The `execute` method returns a `CommandResult` which contains data including the completion of the `ReminderCommand`.

#### Example Usage
1. User inputs the command `reminder Alice Tan r/3 hours`.
2. This creates a reminder note for a person named "alice tan", with reminder time "3 hours".
3. The reminder is then displayed in the UI, reflecting that a reminder note has been set for "alice tan"'s appointment.

### Delete Appointment Feature

#### Overview
The `appointment-delete` command helps to delete scheduled appointments for a client.

The sequence diagram below models the interactions between the different components of PhysioPal for the execution of the `appointment-delete` command.

![DeleteAppointmentSequenceDiagram](images/DeleteAppointmentSequenceDiagram.png)

#### Details
1. The user executes the command `appointment-delete John Doe d/2024-10-28 1200` to delete a specified appointment for a client named john doe.
2. The `DeleteAppointmentCommandParser` object calls its `parse` method to interpret the user input.
3. A `DeleteAppointmentCommand` object is created.
4. The `DeleteAppointmentCommandParser` object returns the `DeleteAppointmentCommand` object.
5. The `LogicManager` object calls the `execute` method of `DeleteAppointmentCommand`.
6. The `execute` method then invokes the `deleteAppointment` method of its `Model` argument to delete the specified appointment.
7. The `execute` method then invokes the `updateFilteredPersonList` method of its `Model` argument to update the view of PhysioPal to show all contacts with their appointments.
8. The `execute` method returns a `CommandResult` which contains data indicating the completion of the `DeleteAppointmentCommand`.

#### Example Usage
1. User inputs the command `appointment-delete Alice Tan d/2024-10-29 1200`.
2. This deletes the appointment for a person named "alice tan" on October 29, 2024, at 12:00pm.
3. The updated schedule for "alice tan" is then displayed in the UI.



### Listing Upcoming Appointments Feature

#### Overview

The user can view upcoming appointments through the `appointment-list` command.
The command can be supplied with optional filters date and time to list
upcoming appointments on the specified date or date and time.

Command: `appointment-list [d/DATE][TIME]`

The following sequence diagram models the interactions between the different components of PhysioPal
for the execution of `appointment-list` command.

![ListAppointmentSequenceDiagram](images/ListAppointmentSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</div>

#### Details

1. The user executes the command `appointment-list` to view all upcoming appointments.
2. The `LogicManager` object receives this command and calls the `parseCommand` method of `AddressBookParser` to interpret the input.
3. The `AddressBookParser` then creates a `ListAppointmentsCommandParser` object to handle parsing.
4. The `ListAppointmentsCommandParser` object calls its `parse` method to extract any present date and time filters
5. The `ListAppointmentsCommandParser` creates a `ListAppointmentsCommand` object.
6. The `ListAppointmentsCommandParser` returns the `ListAppointmentsCommand` object to `AddressBookParser`, which then returns it to `LogicManager`.
7. The `LogicManager` object invokes the `execute` method of `ListAppointmentsCommand`.
8. The `execute` method calls `getFilteredPersonList` on the `Model` to retrieve the list of persons with upcoming appointments matching the filter(if any).
9. The `execute` method creates a `CommandResult` object containing the list of upcoming appointments.
10. The `CommandResult` object is returned to `LogicManager`, which then displays the list of upcoming appointments to the user.

#### Example Usage
1. User inputs the command `appointment-list`.
2. The system displays the number and list of all upcoming appointments," including notes of the appointments(if any)
3. This information is shown in the status message.

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Product name**: PhysioPal

**Target user**: Physiotherapists

**Target user profile**:
Physiotherapists with a large client base who prefer typing over other means of input, thus require an
organised system to manage the details of the clients.

**Value proposition**: A cost-effective, customisable solution for managing client contacts,
scheduling appointments, tracking treatment history, and generating health progress reports,
all without subscription or licensing fees. It saves time, money, and manpower on repetitive tasks,
allowing flexibility to tailor the address book to specific needs.



### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                                | I want to …​                                         | So that I can…​                                                  |
|----------|----------------------------------------|------------------------------------------------------|------------------------------------------------------------------|
| `* * *`  | new user                               | see usage instructions                               | refer to instructions when I forget how to use the App           |
| `* * *`  | physiotherapist                        | add new client contact information                   | retrieve client details when needed.                             |
| `* * *`  | physiotherapist                        | delete outdated or irrelevant client information     | keep my database clean and relevant                              |
| `* * *`  | physiotherapist                        | search for client contact information by name        | enables swift retrieval of specific client details               |
| `* * *`  | physiotherapist                        | search for client contact information by number      | access the client’s details when name is not readily recalled    |
| `* * *`  | physiotherapist                        | view client information in a neatly displayed format | look through the client's details in depth                       |
| `* * *`  | physiotherapist                        | schedule appointments for my clients                 | keep track of my daily sessions and avoid double bookings        |
| `* *`    | physiotherapist with many appointments | set reminders for myself for follow-up appointments  | ensure that no client is missed                                  |
| `* *`    | physiotherapist with many appointments | see upcoming appointments listed at the top          | prominently see what I need to do in order to manage my schedule |

### Use cases

(For all use cases below, the **System** is the `PhysioPal` and the **Actor** is the `Physiotherapist`, unless specified otherwise)

**Use case: UC01 - Delete client's information**

**MSS**

1.  Physiotherapist requests to list clients
2.  PhysioPal shows a list of clients
3.  Physiotherapist requests to delete a specific clients in the list
4.  PhysioPal deletes the client

    Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The given index is invalid.

    * 3a1. PhysioPal shows an error message.

      Use case resumes at step 2.

**Use case: UC02 - Schedule an appointment**

**MSS**

1. Physiotherapist requests to schedule a new appointment for a client.
2. PhysioPal creates appointment for client with the appointment details provided.
3. PhysioPal confirms creation of appointment and displays a success message.

    Use case ends.

**Extensions**

* 1a. PhysioPal detects an empty input for name.

    * 1a1. PhysioPal displays error message.
    * 1a2. Physiotherapist enters new data.
    * Steps 1a1-1a2 are repeated until a valid name is input by the Physiotherapist.
      <br>Use case resumes from step 2.<br>


* 1b. PhysioPal detects an invalid name.

    * 1b1. PhysioPal displays error message.
    * 1b2. Physiotherapist enters new data.
    * Steps 1b1-1b2 are repeated until a valid name is input by the Physiotherapist.
      <br>Use case resumes from step 2.<br>


* 1c. PhysioPal detects an empty input for date and time.

    * 1c1. PhysioPal displays error message.
    * 1c2. Physiotherapist enters new data.
    * Steps 1c1-1c2 are repeated until a valid date and time is input by the Physiotherapist.
      <br>Use case resumes from step 2.<br>


* 1d. PhysioPal detects an invalid date and time or the given date and time already contains an appointment.

    * 1d1. PhysioPal displays error message.
    * 1d2. Physiotherapist enters new data.
    * Steps 1d1-1d2 are repeated until a valid date and time is input by the Physiotherapist.
      <br>Use case resumes from step 2.<br>


* 1e. PhysioPal detects an empty input for note.

    * 1e1. PhysioPal displays error message.
    * 1e2. Physiotherapist enters new data.
    * Steps 1e1-1e2 are repeated until a valid note is input by the Physiotherapist.
      <br>Use case resumes from step 2.<br>

**Use case: UC03- Setting a reminder note for an appointment**

**Preconditions: Client must have at least one appointment.**

**MSS**

1. Physiotherapists sets a reminder note for an appointment made for a client.
2. PhysioPal creates a reminder note for the client with the reminder time provided.
3. PhysioPal confirms creation of reminder note and displays a success message.

   Use case ends.

**Extensions**

* 1a. PhysioPal detects an empty input for name.

    * 1a1. PhysioPal displays error message.
    * 1a2. Physiotherapist enters new data.
    * Steps 1a1-1a2 are repeated until a valid name is input by the Physiotherapist. Use case resumes from step 2.


* 1b. PhysioPal detects an invalid name.

    * 1b1. PhysioPal displays error message.
    * 1b2. Physiotherapist enters new data.
    * Steps 1b1-1b2 are repeated until a valid name is input by the Physiotherapist. Use case resumes from step 2.


* 1c. PhysioPal detects an empty input for reminder time.

    * 1c1. PhysioPal displays error message.
    * 1c2. Physiotherapist enters new data
    * Steps 1c1-1c2 are repeated until a valid reminder time is input by the Physiotherapist. Use case resumes from step 2.


* 1d. PhysioPal detects an invalid reminder time or the appointment already has the same reminder time.

    * 1d1. PhysioPal displays error message.
    * 1d2. Physiotherapist enters new data.
    * Steps 1d1-1d2 are repeated until a valid date and time is input by the Physiotherapist. Use case resumes from step 2.

**Use case: UC04 - Deleting a reminder note**

**MSS**

1. Physiotherapist requests to delete the reminder note for a client.
2. PhysioPal deletes the reminder note for client.
3. PhysioPal confirms deletion of reminder note and displays a success message.

   Use case ends.

**Extensions**

* 1a. PhysioPal detects an empty input for name.

    * 1a1. PhysioPal displays error message.
    * 1a2. Physiotherapist enters new data.
    * Steps 1a1-1a2 are repeated until a valid name is input by the Physiotherapist.
      <br>Use case resumes from step 2.<br>

* 1b. PhysioPal detects an invalid name.

    * 1b1. PhysioPal displays error message.
    * 1b2. Physiotherapist enters new data.
    * Steps 1b1-1b2 are repeated until a valid name is input by the Physiotherapist.
      <br>Use case resumes from step 2.<br>

* 1c. PhysioPal detects that the client does not have a reminder note set.

    * 1c1. PhysioPal displays error message.
      <br>Use case ends.<br>

**Use case: UC05 - Deleting an appointment**

**Preconditions: Client has more than one appointment.**

**MSS**

1. Physiotherapist requests to delete an appointment for a client.
2. PhysioPal deletes the corresponding appointment for client.
3. PhysioPal confirms deletion of appointment and displays a success message.

   Use case ends.

**Extensions**

* 1a. PhysioPal detects an empty input for name.

    * 1a1. PhysioPal displays error message.
    * 1a2. Physiotherapist enters new data.
    * Steps 1a1-1a2 are repeated until a valid name is input by the Physiotherapist.
      <br>Use case resumes from step 2.<br>

* 1b. PhysioPal detects an invalid name.

    * 1b1. PhysioPal displays error message.
    * 1b2. Physiotherapist enters new data.
    * Steps 1b1-1b2 are repeated until a valid name is input by the Physiotherapist.
      <br>Use case resumes from step 2.<br>

* 1c. PhysioPal detects an empty input for date and time.

    * 1c1. PhysioPal displays error message.
    * 1c2. Physiotherapist enters new data.
    * Steps 1c1-1c2 are repeated until a valid date and time is input by the Physiotherapist.
      <br>Use case resumes from step 2.<br>

* 1d. PhysioPal detects that the client does not have an appointment on given date and time.

    * 1d1. PhysioPal displays error message.
    * 1d2. Physiotherapist enters new data.
    * Steps 1d1-1d2 are repeated until a valid date and time is input by the Physiotherapist.
      <br>Use case resumes from step 2.<br>

**Use case: UC06 - Deleting an appointment**

**Preconditions: Client has one appointment.**

**MSS**

1. Physiotherapist <u>deletes a scheduled appointment for a client (UC05)</u>.
2. PhysioPal <u>deletes the reminder note for client (UC04)</u>.
3. PhysioPal confirms deletion of appointment and displays a success message.

   Use case ends.


**Use Case: UC07 - Displaying upcoming appointments on launch screen**

**MSS**
1. Physiotherapist launches PhysioPal.
2. PhysioPal retrieves the top three upcoming appointments.
3. PhysioPal displays the top three upcoming appointments in the result display box.

    Use case ends.

**Extensions**

* 2a. No upcoming appointments are found.
    * 2a1. PhysioPal displays a message indicating no upcoming appointments.
  
        Use case ends.

**Use case: UC08 - List all upcoming appointments**

**MSS**

1.  Physiotherapist requests to list all upcoming appointments
2.  PhysioPal displays a list of all upcoming appointments, ordered by date and time from the closest next appointment.

    Use case ends.


**Use case: UC09 - List upcoming appointments on a specified date**

**MSS**

1.  Physiotherapist requests to list all upcoming appointments on specified date.
2.  PhysioPal displays a list of upcoming appointments on the specified date, ordered by date and time from the closest next appointment

    Use case ends.


**Extensions**
* 1a. The format for date is wrong
  * 1a1. PhysioPal requests for correct data.
  * 1a2. Physiotherapist enters new data.
  * Steps 1a1-1a2 are repeated until the data entered are correct.
           Use case resumes from step 2.

**Use case: UC10 - Marking payment for appointment as paid**

**MSS**

1. Physiotherapist requests to mark payment for an appointment of a client as paid
2. PhysioPal marks payment status of the appointment as paid and displays a success status message.

   Use case ends.

**Extensions**
* 1a. Client do not exist.

    * 1a1. PhysioPal requests for correct data.
    * 1a2. Physiotherapist enters new data.
    * Steps 1a1-1a2 are repeated until the data entered are correct.
      Use case resumes from step 2.

* 1b. The format for date is wrong
    * 1b1. PhysioPal requests for correct data.
    * 1b2. Physiotherapist enters new data.
    * Steps 1a1-1a2 are repeated until the data entered are correct.
      Use case resumes from step 2.

* 1c. The appointment does not exist
    * 1c1. PhysioPal displays an error message that no such appointment found.
    
    Use case ends.



### Non-Functional Requirements

1.  Should be able to handle  all operations of at least 100 clients without a delay of more than 0.5 seconds
2.  Should be able to search for any client in less than 1 second.
3.  The system should comply with healthcare regulations like Private Hospital and Medical Clinics Act (PHMC)
and Personal Data Protection Act (PDPA), so that I manage client data in a compliant manner.
4.  Should be compatible with any Operating System supporting Java 17 (Windows, macOS, Linux).
5.  Should function completely offline.
6.  Jar file size should not exceed 100MB.


### Glossary

* **Client**: A person receiving services from the physiotherapist. He/she should have a unique name (not case-sensitive).
* **Client contact detail**: A contact detail that includes name, phone number, email address,
  address, appointment details, tags etc.
* **Appointment**: A 1-hour scheduled meeting between a physiotherapist and a client for treatment. It includes date and time.
* **Appointment details**: Information on the appointment including date, time, notes and payment details.
* **Condition**: The client's specific physical or functional impairment, injury, or disorder that affects movement, strength, flexibility, or overall physical function.
* **Tag**: A label to indicate the treatment type of the client.
* **Notes**: Additional information on the appointment (e.g. urgency, treatment record).
* **Reminder Note**: An entry saved for a specific time before a client's scheduled appointment in the address book, it serves as a record to help the phyisotherapist keep track of when they need to follow up with the client.
* **API**: Application Programming Interface.
* **GUI**: Graphical User Interface.
* **MSS**: Main Success Scenario.
--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown

1. Initial launch

    1. Download the jar file and copy into an empty folder

    1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

    1. Resize the window to an optimum size. Move the window to a different location. Close the window.

    1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

### Adding a person

1. Adding a person.

   1. **Test case**: `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01`<br>
      **Expected**: The person named john doe is added and displayed in the address book.

   1. **Test case**: `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01`<br>
      **Expected**: No person is added, duplicate contact. Error details shown in the status message.

   1. **Test case**: `add Jane p/98765432`<br>
      **Expected**: No person is added, invalid command format. Error details shown in the status message.

   1. Other incorrect add commands to try: `add`, `add n/John Doe n/John Doe`, `...`<br>
      **Expected**: Similar to previous.

### Deleting a person

1. Deleting a person while all persons are being shown.

    1. **Prerequisites**: List all persons using the `list` command. Multiple persons in the list.

    1. **Test case**: `delete John Doe`<br>
       **Expected**: The contact named John Doe is deleted from the list. Details of the deleted contact shown in the status message.

    1. **Test case**: `delete John Doe`<br>
       **Expected**: No person is deleted. Error details shown in the status message.

    1. Other incorrect delete commands to try: `delete`, `delete XXX`, `...` (where XXX is not a name in the address book)<br>
       **Expected**: Similar to previous.


### Locating Persons

1. Locating a person by name while all clients are being shown.

    1. **Prerequisites**: Ensure at least one contact with the name "john doe" exists in PhysioPal. If not, add the contact by using the command:<br>`add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01`

    2. **Test case**: `find John`<br>**Expected**: All contacts with names containing "john" (case-insensitive) should appear in the results, e.g., "john doe." The search should be able to display "john" and "john doe" regardless of casing.

    3. **Test case**: `find alex david`<br>**Expected**: Contacts containing "alex" or "david" in any order should appear in the results, e.g., "alex yeoh" and "david li." The results should display all contacts that match at least one keyword in a case-insensitive manner.

    4. **Test case**: `find Han`<br>**Expected**: No contact is shown in the results if only partial matches exist, such as "hans," since the system only matches full words.

2. Locating a person by phone number while all clients are being shown.

    1. **Test case**: `find p/88`<br>**Expected**: All contacts with phone numbers containing "88" should be shown. For instance, if "john doo" has the phone number "88765432," he should appear in the results, allowing partial phone number matches.

### Viewing a Person

1. Viewing a person’s details in the address book.

    1. **Prerequisites**: Ensure that the contact with the exact name exists in PhysioPal. And that he or she has a scheduled appointment. If not, add the required contact or appointment using a command similar to:<br>`add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01` and `schedule John Doe d/2024-10-29 1200 note/First Appointment` respectively

    2. **Test case**: `view John Doe`<br>**Expected**: A pop-up window should display the full details for john doe, including:
        - Name
        - Phone Number
        - Email
        - Address
        - Condition
        - Schedule
        - Reminder Note

    3. **Test case**: `view Betsy Crowe`<br>**Expected**: A pop-up window displays betsy crowe’s details with all specified fields shown.

    4. **Test case**: `view John`<br>**Expected**: An error message appears, as the name entered does not match the full name exactly.

    5. **Test case**: `view Nonexistent Name`<br>**Expected**: An error message appears indicating that no matching contact is found.
   
    1. **Test case**: `schedule John Doe d/2024-10-29 1200 note/First Appointment`<br>**Expected**: No contact is updated with the corresponding appointment. Error details shown in the status message.

    1. **Test case**: `schedule John Doe d/2024-10-29 1800 note/First Appointment`<br>**Expected**: Similar to previous.

### Scheduling an appointment

1. Scheduling an appointment for a client while all clients are being shown.

    1. **Prerequisites**: Only **one** contact with the name john doe should exist in PhysioPal. If not, run the appropriate command to add john doe to PhysioPal. PhysioPal is designed to handle names in a **case-insensitive** manner and does not accept duplicate names, so there will never be a case where more than one contact with the name john doe exists in the contact list.<br>`add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01`

    1. **Test case**: `schedule John Doe d/2024-10-29 1200 note/First Appointment`<br>**Expected**: Contact named john doe will be updated with an appointment on Oct 29 2024, 12:00 pm with the note "First Appointment" attached to it. Details of the appointment shown in the status message.

    1. **Test case**: `schedule John Doee d/2024-10-29 1200 note/First Appointment`<br>**Expected**: No contact is updated with the corresponding appointment. Error details shown in the status message.

    1. **Test case**: `schedule John Doe d/2024-10-29 1800 note/First Appointment`<br>**Expected**: Similar to previous.

### Deleting an appointment

1. Deleting an appointment for a client while all clients are being shown.

    1. **Prerequisites**: Name john doe should exist in the contact list. At least **one** appointment should exist for the client john doe. If not, run the appropriate command to schedule an appointment for john doe.<br>`schedule John Doe d/2024-10-29 1200 note/First Appointment`<br>

    1. **Test case**: `appointment-delete John Doe d/2024-10-29 1200`<br>**Expected**: Contact named john doe will be updated without the appointment on Oct 29 2024, 12:00 pm. Success message of the appointment deletion shown in the status message.

    1. **Test case**: `appointment-delete John Doe d/2024-10-29 1200`<br>**Expected**: No contact is updated. Error details shown in the status message.

    1. **Test case**: `appointment-delete John Doe d/2024-10-29 1800`<br>**Expected**: Similar to previous.

### Setting a reminder note for an appointment

1. Setting a reminder note for a scheduled appointment for a client while all clients are being shown.

    1. **Prerequisites**: Only **one** contact with the name john doe should exist in PhysioPal. If not, run the appropriate command to add john doe to PhysioPal. PhysioPal is designed to handle names in a **case-insensitive** manner and does not accept duplicate names, so there will never be a case where more than one contact with the name john doe exists in the contact list.<br>`add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01`
        
        At **least one** appointment must be scheduled for john doe. If not, run the appropriate command to add an appointment for john doe. <br>`schedule John Doe d/2024-10-29 1200 note/First Appointment`

    1. **Test case**: `reminder John Doe r/1 hour`<br>**Expected**: Contact name john doe will be updated with a reminder note of 1 hour before Oct 29 2024, 12:00pm

    1. **Test case**: `reminder John Doee r/1 hour`<br>**Expected**: No contact is updated with the corresponding reminder time. Error details shown in the status message.

    1. **Test case**: `reminder John Doe r/1 cycle`<br>**Expected**: No contact is updated with the corresponding reminder time due to invalid reminder format. Error details shown in the status message.

### Deleting a reminder note

1. Deleting a reminder note for a client while all clients are being shown.

    1. **Prerequisites**: Name john doe should exist in the contact list. A reminder note should be set for the client john doe. If not, run the appropriate command to set a reminder note for john doe.<br>`reminder John Doe r/1 day`<br>

    1. **Test case**: `reminder-delete John Doe`<br>**Expected**: Contact named john doe will be updated without the reminder note. Success message of the reminder note deletion shown in the status message.

    1. **Test case**: `reminder-delete John Doe`<br>**Expected**: No contact is updated. Error details shown in the status message.

### Listing upcoming appointments

1. Listing all upcoming appointments

   1. **Test case**: `appointment-list`<br>
      **Expected**: Lists all upcoming appointments in chronological order. Total number of appointments and 
    details of the appointments shown in the status message.
   
   2. **Test case**: `appointment-list d/2024-11-25`<br>
      **Expected**: Lists upcoming appointments on 25 Nov 2024. Total number of appointments and
    details of the appointments shown in the status message.
   
   3. **Test case**: `appointment-list d/2024-11-10 1000`<br>
      **Expected**: Lists upcoming appointments on 10 Nov 2024 at 10am. Total number of appointments and
      details of the appointments shown in the status message.
   
   4. Other incorrect appointment-list commands to try: `appointment-list d/`, `appointment-list d/2025-13-32`, `...`<br>
      **Expected**: No appointments listed. Error details shown in the status message.

### Making payment for an appointment

1. Marking payment as paid
    1. **Test case**: `payment John Doe d/2024-10-14 1200 pay/paid`<br>
       **Expected**: Payment status for john doe's appointment on 14 Oct 2024 at 12pm is marked as paid. 
       Details of the status change shown in the status message.
    2. Other incorrect payment commands to try: `payment John Doe d/2024-10-14 1200 pay/payment`, `payment John Doe`, `...`<br>
       **Expected**: No payment made. Error details shown in the status message.
2. Marking payment as unpaid
    1. **Test case**: `payment John Doe d/2024-10-14 1200 pay/unpaid`<br>
       **Expected**: Payment status for john doe's appointment on 14 Oct 2024 at 12pm is marked as unpaid.
       Details of the status change shown in the status message.
    2. Other incorrect payment commands to try: `payment John Doe d/2024-10-14 1200 pay/payment`, `payment John Doe`, `...`<br>
       **Expected**: No payment made. Error details shown in the status message.

### Showing Top Three Upcoming Appointments

1. Handling Data Retrieval
    1. If there are no upcoming appointments: A message indicating no upcoming appointments will be displayed to the user.
    2. If there are upcoming appointments: The top three upcoming appointments will be displayed in order.

### Listing all persons

1. Viewing all clients in PhysioPal

    1. **Prerequisites**: There are client contacts stored in PhysioPal. If not run the appropriate command to add clients to PhysioPal. PhsyioPal is designed to handle names in a **case-insensitive** manner and does not accept duplicate names, so there will never be a case where more than one contact with the same name exists in the contact list.
        <br>`add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01`
        <br>`add n/Betsy Crowe p/1234567 e/betsycrowe@example.com a/Newgate Prison`
   2. **Test Case**: `list`
        <br>Expected: All previously saved client contacts will be shown in the list. Success message is displayed.

### Clearing all contacts

1. Clearing the entire list of contacts in the JSON file that is being used as the data source.

    1. **Test case**: `clear`<br>
       **Expected**: All contacts in the data file are cleared.

### Saving data

1. Dealing with missing/corrupted data files

   1. If the data file is missing, a new empty client contact list is created in the given directory.
   2. If the data file is corrupted, the corrupted file is cleared and an empty client contact list is returned.
   3. The data file is saved automatically after each command that modifies the data file.

## **Appendix: Effort**

### Project Overview

Our project aims to provide an efficient contact management system for a physiotherapist 
with a large client base, building on AB3 (Address Book 3). Additional features include scheduling appointments,
setting reminder notes, tracking treatment history, type, and payment history. These improvements are
aimed to provide a cost-effective solution to help physiotherapists save time, money and manpower on
repetitive tasks.

### Difficulty Level and Challenges Faced

Developing PhysioPal presented unique challenges. Unlike AddressBook Level 3 (AB3), 
which handles only one entity type, PhysioPal extends heavily on the appointment feature: `Schedule`.
The need for seamless interaction between different entities and satisfying functionality requirements
demanded significant work to incorporate features such as reminder notes and appointment handling.
Furthermore, the pop-up window to display client details in an organized fashion added complexity
to the UI design.

Entity Management and Interaction: Handling multiple interdependent entities added complexity
in data storage and retrieval, along with maintaining consistent UI updates.
Error Management in Scheduling: Validating date and time inputs for appointments, 
especially in cases of overlaps and conflicts, required custom validation logic and extensive testing.
State Persistence Across Sessions: Ensuring that scheduled appointments and 
client information persist across sessions added layers of complexity to the storage component.

### Effort Required

The effort required for PhysioPal was extensive, encompassing phases of analysis,
design, development, testing, and documentation. The multidisciplinary team dedicated considerable
time and resources to understand the application's architecture, pinpoint areas for improvement, 
and implement new features while maintaining compatibility and stability. 
Utilizing Agile methodologies allowed the team to address challenges iteratively and integrate
stakeholder feedback throughout, leading to a streamlined and effective development process.


### Achievements

PhysioPal was able to achieve many milestones despite the difficulties faced. 
This majorly enhanced the contact management system’s functionality to be more tailored
to physiotherapists. Key achievements included:

1. Streamlined Contact Removal (delete): The delete command enables physiotherapists to 
remove a client by name instead of an ID, simplifying the process and reducing errors. 
This approach allows quick removal of inactive clients.

2. Enhanced Appointment Scheduling and Management (schedule, appointment-delete, appointment-list):
The schedule command allows quick setup of client appointments with date, time, and contextual notes,
ensuring well-prepared sessions. appointment-delete provides flexibility to cancel appointments by
name and time, preventing scheduling conflicts. appointment-list offers an overview of 
upcoming sessions, helping to manage and organize appointments effectively.

3. Effective Reminder Management (reminder and reminder-delete): The reminder command allows 
physiotherapists to set a reminder note in terms of hours and days before appointments, 
ensuring punctuality and reducing the risk of missed sessions. reminder-delete allows for easy 
removal of outdated reminders, providing flexibility. Together, these features help maintain a 
well-organized schedule, enhancing the client experience.

4. Integrated Payment Tracking (payment): The payment command marks specific appointments as paid, 
simplifying billing and ensuring financial clarity. By tracking payment status per appointment, 
this feature minimizes errors and reduces administrative workload. It provides a clear payment
record linked to each client’s treatment.

5. Focused Client Information Display (view Command with Pop-Up): The view command opens a pop-up 
with comprehensive client details, including contact info, appointments, reminder notes, and 
payment status. This consolidated view improves accessibility, allowing physiotherapists to 
quickly review all necessary client information. It streamlines workflow and supports a more 
personalized client experience.

### Effort Saved Through Reuse

For our project, approximately 10% of the effort was saved through reusing AB3's existing codebase.
Features such as adding and deleting contact were adapted from the existing implementation
to fit our users' needs.
