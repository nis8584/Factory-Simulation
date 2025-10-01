package factory.queueAndScheduler;

import factory.communication.GlobalConstants;
import factory.communication.PostingService;
import factory.communication.message.SetFactoryMessage;
import factory.controlledSystem.DispenserStation;
import factory.controlledSystem.DropOffStation;
import factory.controlledSystem.FactoryNode;
import factory.controlledSystem.WorkStation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.greenrobot.eventbus.EventBus;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that converts input text to a queue and a list of FactoryNodes
 */
public class FileParser {
    private static final Logger LOG = LogManager.getLogger(FileParser.class);

    /**
     * main parser method to create factory data
     * <p>
     *     first checks validity of input with a regex
     *     then using various helper methods the input is dissected into a queue, a list of tasks, all FactoryNodes, connections and costs
     * </p>
     * @param file selected file in GUI
     */
    public static void parseFactoryAndQueue(File file){
        try (InputStream in = new FileInputStream(file)){
            //check if input is in allowed form
            String input = readFromInputStream(in).toUpperCase();
            String regex =
                    "(\\w+:(\\w+[,+])*\\w+-)*\\w+:(\\w+[,+])*\\w+\n" +         //definition of tasks + steps of tasks in format (taskname:step,step2,step3-taskname2:....)
                    "(\\w*,)*\\w*\n" +                                   //queue in format of (task1,task2,task3,task1,task2,task1,task2)
                    "(\\w\\w\\d,\\d-)*(\\w\\w\\d,\\d)\n" +               //define stationnames + types + location in gui grid in format (stationssymbol stationtype "digit,digit")+
                    "(\\w(\\d\\w)+-)*(\\w(\\d\\w)+)\n" +                 //define connections between stations in format: (fromStation (cost toStation)+)-...(fromStation(cost toStation)+)
                    "(\\w(\\d\\w+:)*(\\d\\w+)-)*\\w(\\d\\w+:)*(\\d\\w+)";//define which stations can do which task at which cost in format: (station(cost step)+)-... (station(cost step)+)
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(input.trim().toUpperCase());
            if(!matcher.matches()){
                //some error  handling
                PostingService.log("Input not matching expectations in: ");
                return;
            }
            String[] inp = input.split("\n");
            //  get types of steps + which tasks they belong to
            Map<String,LinkedList<LinkedList<String>>> tasksAndSteps = getTasksAndSteps(inp[0]);
            //assemble to queue
            LinkedList<Task> tasksToDo = new LinkedList<>();
            String[] queueStrings = inp[1].split(",");
            for(String s: queueStrings){
                if(!tasksAndSteps.containsKey(s)){
                    //handle task not predefined
                    LOG.error("Cannot handle a task that is not specified");
                }else {
                    tasksToDo.add(new TaskX(getDeepListCopy(tasksAndSteps.get(s)),s, GlobalConstants.getTaskId()));
                }
            }
            Queue queue = new Queue(tasksToDo);
            //get nodes + types
            List<FactoryNode> nodes  = getFactoryNodes(inp[2]);
            // get connections of nodes between each other
            createConnections(inp[3], (LinkedList<FactoryNode>) nodes);
            // get possible work steps and costs per station
            setStepsAndCost(inp[4], nodes);
            EventBus.getDefault().post(new SetFactoryMessage((LinkedList<FactoryNode>) nodes, queue, tasksAndSteps));
        } catch (IOException e){
            LOG.error("An error occurred while loading factory data");
        }
    }

    private static LinkedList<LinkedList<String>> getDeepListCopy(LinkedList<LinkedList<String>> input){
        LinkedList<LinkedList<String>> result = new LinkedList<>();
        for(LinkedList<String> list: input){
            result.add(new LinkedList<>(list));
        }
        return result;
    }


    /**
     * Helper method to convert text file to String of text
     * @param inputStream InputStream that is reading the file
     * @return String of file content
     */
    private static String readFromInputStream(InputStream inputStream)
            throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

    /**
     * Helper method that creates a Map of task names pointing to a List of steps for that task
     * @param input the text row that holds the information
     * @return Map of tasks and their steps
     */
    private static Map<String,LinkedList<LinkedList<String>>> getTasksAndSteps(String input){
        Map<String,LinkedList<LinkedList<String>>> result = new TreeMap<>();
        String[] tasks = input.split("-");
        for(String s: tasks){
            String allSteps = (s.split(":")[1]);
            List<String> orderedSteps = List.of(allSteps.split(","));
            LinkedList<LinkedList<String>> subList = new LinkedList<>();
            for(String steps: orderedSteps){
                subList.add(new LinkedList<>(List.of(steps.split("\\+"))));
            }
            result.put(s.split(":")[0], subList);
        }
        return result;
    }

    /**
     * Helper method that creates a List of all FactoryNodes
     * @param input text row that holds the information about all nodes
     * @return List of Factory nodes
     */
    private static List<FactoryNode> getFactoryNodes(String input) {
        String[] stations = input.split("-");
        List<FactoryNode> nodes = new LinkedList<>();
        for(String s: stations){
            FactoryNode node = switch (s.charAt(1)) {
                case 'D' -> new DispenserStation(s.charAt(0));
                case 'B' -> new DropOffStation(s.charAt(0));
                case 'W' -> new WorkStation(s.charAt(0));
                default -> null;
            };
            if(node == null) continue;
            node.setPosition(s.substring(2));
            nodes.add(node);
        }
        return nodes;
    }

    /**
     * Helper method that connects all nodes according to the specified costs in a given String
     * @param rawInput input String that specifies the connections of a factory
     * @param nodes a List of nodes that need to be connected
     */
    private static void createConnections(String rawInput, LinkedList<FactoryNode> nodes){
        String[] connections = rawInput.split("-");
        for(String s: connections){
            FactoryNode node = FactoryNode.getNodeByChar(s.charAt(0), nodes);
            if(node == null){
                LOG.error("There seems to be a problem with the connection specification");
                return;
            }
            Map<FactoryNode,Integer> neighbors = new HashMap<>();
            for(int i = 1; i< s.length(); i+=2){
                neighbors.put(FactoryNode.getNodeByChar(s.charAt(i+1),nodes) , Integer.valueOf(String.valueOf(s.charAt(i))));
            }
            node.setNeighbors(neighbors);
        }
    }

    /**
     * Helper method that sets the possible steps and their associated costs to each workstation
     * @param rawInput input String that specifies the steps and costs
     * @param nodes List of nodes that need to have the costs put in
     */
    private static void setStepsAndCost(String rawInput, List<FactoryNode> nodes){
        String[] stepsAndCosts = rawInput.split("-");
        for(String s: stepsAndCosts){
            WorkStation node = WorkStation.getNodeByChar(s.charAt(0), (LinkedList<FactoryNode>) nodes);
            if(node == null){
                LOG.error("There seems to be a problem with the workstep and cost specification");
                return;
            }
            String[] a = s.substring(1).split(":");
            for(String s2: a){
                node.addTypeOfWork(s2.substring(1),Integer.valueOf(String.valueOf(s2.charAt(0))));
            }
        }
    }
}
