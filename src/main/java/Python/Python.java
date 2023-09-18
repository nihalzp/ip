package Python;

import Python.Task.Deadline;
import Python.Task.Event;
import Python.Task.Task;
import Python.Task.Todo;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileWriter;

public class Python {
    final private static String BOT_NAME = "Python";
    final private static String PYTHON_ASCII_ART =
                    "\t ____        _   _\n" +
                    "\t|  _ \\ _   _| |_| |__   ___  _ __\n" +
                    "\t| |_) | | | | __|  _ \\ / _ \\|  _ \\\n" +
                    "\t|  __/| |_| | |_| | | | (_) | | | |\n" +
                    "\t|_|    \\__, |\\__|_| |_|\\___/|_| |_|\n" +
                    "\t       |___/";
    final private static String PYTHON_EMOJI = "\uD83D\uDC0D";

    final static private List<Task> tasks = new ArrayList<>();

    final private static Scanner in = new Scanner(System.in);

    public static final String COMMAND_BYE = "bye";
    public static final String COMMAND_LIST = "list";
    public static final String COMMAND_MARK = "mark";
    public static final String COMMAND_UNMARK = "unmark";
    public static final String COMMAND_DELETE = "delete";
    public static final String COMMAND_TODO = "todo";
    public static final String COMMAND_DEADLINE = "deadline";
    public static final String COMMAND_EVENT = "event";

    public static final String FILENAME_TASKS_LIST = "python.txt";
    public static final String DIR_TASKS_LIST = "./data/";

    final private static int HORIZONTAL_LINE_LENGTH = 80;
    private static void printHorizontalLine() {
        String horizontalLine = "—".repeat(HORIZONTAL_LINE_LENGTH);
        System.out.println("\t" + horizontalLine);
    }

    private static void writeToFile(File file, String textToAdd) throws IOException {
        FileWriter fw = new FileWriter(file);
        fw.write(textToAdd);
        fw.close();
    }

    private static void saveTasksToDisk() {
        File directory = new File(DIR_TASKS_LIST);
        File file = new File(directory, FILENAME_TASKS_LIST);

        // Check if directory exists, if not, create it
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                System.out.printf("\t%s: Unable to save the tasks.\n", PYTHON_EMOJI);
                System.out.printf("\t%s: Failed to create the directory: %s.\n", PYTHON_EMOJI,
                        DIR_TASKS_LIST);
                return;
            }
        }

        try {
            if (file.createNewFile()) {
                System.out.printf("\t%s: Created file to store tasks: %s.\n", PYTHON_EMOJI,
                        DIR_TASKS_LIST + FILENAME_TASKS_LIST);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        StringBuilder contentFile = new StringBuilder();
        for (Task task : tasks) {
            String taskDetails = task.toDiskSaveFormat() + System.lineSeparator();
            contentFile.append(taskDetails);
        }

        try {
            writeToFile(file, contentFile.toString());
        } catch (IOException e) {
            System.out.printf("\t%s: An error occurred while writing to the file: %s.\n", PYTHON_EMOJI,
                    e.getMessage());
        }
    }

    private static void loadTasksFromDisk() {
        File directory = new File(DIR_TASKS_LIST);
        File file = new File(directory, FILENAME_TASKS_LIST);

        Scanner s;
        try {
            s = new Scanner(file);
        } catch (FileNotFoundException e) {
            return;
        }

        while (s.hasNext()) {
            String[] taskDetails = s.nextLine().split(" \\| ");
            String typeIcon = taskDetails[0];
            if (typeIcon.equals(Todo.TYPE_ICON)) {
                String description = taskDetails[2];
                String isDone = taskDetails[1];
                Todo todo = new Todo(description);
                if (isDone.equals("[X]")) {
                    todo.setDone(true);
                }
                tasks.add(todo);
            } else if (typeIcon.equals(Deadline.TYPE_ICON)) {
                String description = taskDetails[2];
                String isDone = taskDetails[1];
                String by = taskDetails[3];
                Deadline deadline = new Deadline(description, by);
                if (isDone.equals("[X]")) {
                    deadline.setDone(true);
                }
                tasks.add(deadline);
            } else {
                String description = taskDetails[2];
                String isDone = taskDetails[1];
                String from = taskDetails[3];
                String to = taskDetails[4];
                Event event = new Event(description, from, to);
                if (isDone.equals("[X]")) {
                    event.setDone(true);
                }
                tasks.add(event);
            }
        }
    }


    public static void main(String[] args) {
        System.out.println(PYTHON_ASCII_ART);
        printHorizontalLine();
        System.out.printf("\t%s: Hello! I am a short Java Bot %s!\n", PYTHON_EMOJI, BOT_NAME);
        loadTasksFromDisk();
        System.out.printf("\t%s: You currently have %d tasks!\n", PYTHON_EMOJI, tasks.size());
        System.out.printf("\t%s: What can I do for you?\n", PYTHON_EMOJI);
        printHorizontalLine();

        String inputLine;
        do {
            inputLine = in.nextLine();

            // Trim extra whitespace characters between words while splitting
            String inputCommand = inputLine.split("\\s+")[0];
            printHorizontalLine();

            switch (inputCommand) {
            case COMMAND_BYE:
                System.out.printf("\t%s: Bye. See you again when you run the program again!\n", PYTHON_EMOJI);
                break;
            case COMMAND_LIST:
                System.out.printf("\t%s: You have %d tasks!\n", PYTHON_EMOJI, tasks.size());
                for (int taskNo = 1; taskNo <= tasks.size(); taskNo++) {
                    System.out.printf("\t\t\t%d. %s\n", taskNo, tasks.get(taskNo - 1));
                }
                break;
            case COMMAND_MARK: {
                int taskNo;
                try {
                    taskNo = Integer.parseInt(inputLine.split(" ")[1]);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.out.println("\t" + COMMAND_MARK + " command must be followed by an integer (task" +
                            " id)!");
                    break;
                }

                // Handle unintended usage
                if (taskNo > tasks.size()) {
                    System.out.printf("\t%s: Are you from the future?\n", PYTHON_EMOJI);
                    break;
                }
                if (tasks.get(taskNo - 1).isDone()) {
                    System.out.printf("\t%s: Are you from the past?\n", PYTHON_EMOJI);
                    System.out.printf("\t\tTask: %s\n \t\t is already done!!!\n",
                            tasks.get(taskNo - 1));
                    break;
                }

                tasks.get(taskNo - 1).setDone(true);

                System.out.printf("\t%s: Good job completing the task!\n", PYTHON_EMOJI);
                System.out.printf("\t\t\t %s\n", tasks.get(taskNo - 1));
                saveTasksToDisk();
                break;
            }
            case COMMAND_UNMARK: {
                int taskNo;
                try {
                    taskNo = Integer.parseInt(inputLine.split(" ")[1]);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.out.println("\t" + COMMAND_UNMARK + " command must be followed by an integer (task" +
                            " id).");
                    break;
                }

                // Handle unintended usage
                if (taskNo > tasks.size()) {
                    System.out.printf("\t%s: Are you from the future?\n", PYTHON_EMOJI);
                    break;
                }
                if (!tasks.get(taskNo - 1).isDone()) {
                    System.out.printf("\t%s: Alas! Only the completed tasks can be unmarked!\n", PYTHON_EMOJI);
                    System.out.printf("\t\tTask: %s\n \t\tis already sitting idle. Get started...!!!\n",
                            tasks.get(taskNo - 1));
                    break;
                }

                tasks.get(taskNo - 1).setDone(false);

                System.out.printf("\t%s: Its okay! To err is human! Unmarked!\n", PYTHON_EMOJI);
                System.out.printf("\t\t\t %s\n", tasks.get(taskNo - 1));
                saveTasksToDisk();
                break;
            }
            case COMMAND_DELETE: {
                int taskNo;
                try {
                    taskNo = Integer.parseInt(inputLine.split(" ")[1]);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.out.println("\t" + COMMAND_DELETE + " command must be followed by an integer (task" +
                            " id).");
                    break;
                }

                // Handle unintended usage
                if (taskNo > tasks.size()) {
                    System.out.printf("\t%s: Are you from the future?\n", PYTHON_EMOJI);
                    System.out.printf("\t%s: You have %d tasks only!\n", PYTHON_EMOJI, tasks.size());
                    break;
                }

                System.out.printf("\t%s: Okay. Deleting this task...!\n", PYTHON_EMOJI);
                System.out.printf("\t\t\t %s\n", tasks.get(taskNo - 1));
                tasks.remove(taskNo - 1);
                System.out.printf("\t%s: Now, you have %d tasks!\n", PYTHON_EMOJI, tasks.size());
                break;
            }
            case COMMAND_TODO: {
                String todoDescription;
                try {
                    todoDescription = inputLine.split(" ", 2)[1];
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("\t" + COMMAND_TODO + " command must be followed by a task " +
                            "description!");
                    break;
                }

                System.out.printf("\t%s: %s\n", PYTHON_EMOJI, "New Todo! You have added this todo:");

                Todo todo = new Todo(todoDescription);
                tasks.add(todo);

                System.out.printf("\t\t\t %s\n", todo);
                System.out.printf("\t\tYou have %d tasks in total!\n", tasks.size());
                saveTasksToDisk();
                break;
            }
            case COMMAND_DEADLINE: {
                String deadlineDetails, deadlineDescription, deadlineBy;
                try {
                    deadlineDetails = inputLine.split(" ", 2)[1];
                    deadlineDescription = deadlineDetails.split(" /by ")[0];
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("\t" + COMMAND_DEADLINE + " command must be followed by a task " +
                            "description!");
                    break;
                }
                try {
                    deadlineBy = deadlineDetails.split(" /by ")[1];
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("\t" + COMMAND_DEADLINE + " command must have /by clause followed by " +
                            "time its due!");
                    break;
                }

                System.out.printf("\t%s: %s\n", PYTHON_EMOJI, "New Deadline! You have added this deadline:");

                Deadline deadline = new Deadline(deadlineDescription, deadlineBy);
                tasks.add(deadline);

                System.out.printf("\t\t\t %s\n", deadline);
                System.out.printf("\t\tYou have %d tasks in total!\n", tasks.size());
                saveTasksToDisk();
                break;
            }
            case COMMAND_EVENT: {
                String eventDetails, eventDescription, eventFrom, eventTo;
                try {
                    eventDetails = inputLine.split(" ", 2)[1];
                    eventDescription = eventDetails.split("\\s+/from\\s+|\\s+/to\\s+", 3)[0];
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("\t" + COMMAND_EVENT + " command must be followed by a task " +
                            "description!");
                    break;
                }
                try {
                    eventFrom = eventDetails.split("\\s+/from\\s+|\\s+/to\\s+", 3)[1];
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("\t" + COMMAND_EVENT + " command must have /from clause followed by " +
                            "time it starts!");
                    break;
                }
                try {
                    eventTo = eventDetails.split("\\s+/from\\s+|\\s+/to\\s+", 3)[2];
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("\t" + COMMAND_EVENT + " command must have /to clause followed by " +
                            "time it ends!");
                    break;
                }

                System.out.printf("\t%s: %s\n", PYTHON_EMOJI, "New Event! You have added this event:");

                Event event = new Event(eventDescription, eventFrom, eventTo);
                tasks.add(event);

                System.out.printf("\t\t\t %s\n", event);
                System.out.printf("\t\tYou have %d tasks in total!\n", tasks.size());
                saveTasksToDisk();
                break;
            }
            default:
                if (inputLine.isEmpty()) {
                    System.out.printf("\t%s: %s\n", PYTHON_EMOJI, "Nothing for me?");
                    break;
                }
                System.out.printf("\t%s: %s\n", PYTHON_EMOJI, "I cannot understand the command!");
                break;
            }
            printHorizontalLine();
        } while (!inputLine.equals(COMMAND_BYE));
    }
}
