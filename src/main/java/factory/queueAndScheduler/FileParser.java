package factory.queueAndScheduler;

import factory.controlledSystem.DispenserStation;
import factory.controlledSystem.DropOffStation;
import factory.controlledSystem.FactoryNode;
import factory.controlledSystem.WorkStation;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileParser {

    public  static Queue parseFileToQueue(File file){
        try (InputStream in = new FileInputStream(file)){

            String input = readFromInputStream(in);
            //types of tasks
            String regex = "[XYZ]*";

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(input.trim().toUpperCase());

            if(!matcher.matches()){
                //some error handeling
                System.out.println("Input not matching expectations in: " + file);
                return null;
            }
            System.out.println("successfully loaded queue");
            LinkedList<Task> tasksToDo = new LinkedList<>();
            for(char c: input.trim().toUpperCase().toCharArray()){
                //define work stations required per task:
                switch (c){
                    case ('X'):
                        tasksToDo.add(new TaskX(null));
                        break;
                    case ('Y'):
                        tasksToDo.add(new TaskY(null));
                        break;
                }
            }
            return new Queue(tasksToDo);
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public static LinkedList<FactoryNode> parseFileToFactoryNodeSetup(File file){
        try (InputStream in = new FileInputStream(file)){
            //make sure the input is allowed
            String input = readFromInputStream(in).trim().toUpperCase();
            String regex = "(\\w\\w\\d,\\d~)*(\\w\\w\\d,\\d):(\\w(\\d\\w)+-)*(\\w(\\d\\w)+)";

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(input);

            if(!matcher.matches()){
                //some error handling
                System.out.println("Input not matching expectations in: " + file);
                return null;
            }
            System.out.println("successfully loaded Layout");
            //divide input in list of nodes[0] and node connections[1-length]
            LinkedList<FactoryNode> nodes = new LinkedList<>();
            String[] inputArray = input.split("[:-]");
            //add nodes to list
            for(String s: inputArray[0].split("~")){
                if(FactoryNode.alreadyPresentInList(s.charAt(0), nodes)){
                    //some more error handling
                    System.out.println("Duplicate factory node found in input in: " + file);
                    return null;
                }
                FactoryNode node = null;
                switch (s.charAt(1)){
                    case 'D':
                        node =  new DispenserStation(s.charAt(0));
                        break;
                    case 'B':
                        node = new DropOffStation(s.charAt(0));
                        break;
                    case 'W':
                        node = new WorkStation(s.charAt(0));
                        break;
                }
                if(node == null){
                    System.out.println("Input has unknown character in node type specification");
                    return null;
                }
                node.setPosition(s.substring(2));
                nodes.add(node);
            }
            nodes.forEach( s -> System.out.println(s.getClass()));
            //add connections to nodes
            for(int i = 1; i<inputArray.length; i++){
                String connections = inputArray[i];
                char currentNode = connections.charAt(0);
                HashMap<FactoryNode, Integer> neighbors = new HashMap<>();

                for(int j = 1; j < connections.length(); j += 2){
                    if(FactoryNode.getNodeByChar(connections.charAt(j+1),nodes) == null){
                        //error handling
                        System.out.println("Connection specified for node that doesnt exist in : " + file);
                        return null;
                    }

                    neighbors.put(FactoryNode.getNodeByChar(connections.charAt(j+1),nodes) ,Integer.valueOf(Character.toString(connections.charAt(j))));
                }
                FactoryNode.getNodeByChar(currentNode,nodes).setNeighbors(neighbors);
            }
            return nodes;
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args){
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
}
