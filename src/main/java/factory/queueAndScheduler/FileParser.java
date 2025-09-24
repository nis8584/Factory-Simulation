package factory.queueAndScheduler;

import factory.LoggingService;
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

public class FileParser {
    private static final Logger LOG = LogManager.getLogger(FileParser.class);

    public static void parseFactoryAndQueue(File file){
        try (InputStream in = new FileInputStream(file)){
            //check if input is in allowed form
            String input = readFromInputStream(in).toUpperCase();
            String regex =
                    "(\\w+:(\\w+,)*\\w+-)*\\w+:(\\w+,)*\\w+\n" +         //definition of tasks + steps of tasks in format (taskname:step,step2,step3-taskname2:....)
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
            Map<String,LinkedList<String>> tasksAndSteps = getTasksAndSteps(inp[0]);
            //assemble to queue
            LinkedList<Task> tasksToDo = new LinkedList<>();
            String[] queueStrings = inp[1].split(",");
            for(String s: queueStrings){
                if(!tasksAndSteps.containsKey(s)){
                    //handle task not predefined
                    System.out.println("error");
                }
                tasksToDo.add(new TaskX(new LinkedList<>(tasksAndSteps.get(s))));
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

    private static String readFromInputStream(InputStream inputStream)
            throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

    public static void main(String[] args){

    }
    private static Map<String,LinkedList<String>> getTasksAndSteps(String input){
        Map<String,LinkedList<String>> result = new TreeMap<>();
        String[] tasks = input.split("-");
        for(String s: tasks){
            result.put(s.split(":")[0], new LinkedList<>(List.of(s.split(":")[1].split(","))));
        }
        return result;
    }
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
