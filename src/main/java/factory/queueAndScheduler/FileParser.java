package factory.queueAndScheduler;

import factory.controlledSystem.FactoryNode;
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
            LinkedList<Task> tasksToDo = new LinkedList<>();
            for(char c: input.trim().toUpperCase().toCharArray()){
                //define work stations required per task:
                switch (c){
                    case ('X'):
                        tasksToDo.add(new TaskX(null));
                    case ('Y'):
                        tasksToDo.add(new TaskY(null));
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
            String regex = "\\w+:[\\w[\\d\\w]+-]+";

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(input);

            if(!matcher.matches()){
                //some error handling
                System.out.println("Input not matching expectations in: " + file);
                return null;
            }
            //divide input in list of nodes[0] and node connections[1-length]
            LinkedList<FactoryNode> nodes = new LinkedList<>();
            String[] inputArray = input.split("[:-]");
            //add nodes to list
            for(char c: inputArray[0].toCharArray()){
                if(FactoryNode.alreadyPresentInList(c, nodes)){
                    //some more error handling
                    System.out.println("Duplicate factory node found in input in: " + file);
                    return null;
                }
                nodes.add(new FactoryNode(c));
            }
            System.out.println(nodes);
            //add connections to nodes
            for(int i = 1; i<inputArray.length; i++){
                String connections = inputArray[i];
                char currentNode = connections.charAt(0);
                HashMap<FactoryNode, Integer> neighbors = new HashMap<>();

                for(int j = 1; j < connections.length(); j += 2){
                    if(!FactoryNode.alreadyPresentInList(currentNode, connections.charAt(j+1), nodes)){
                        //error handling
                        System.out.println("Connection specified for node that doesnt exist in : " + file);
                        return null;
                    }
                    System.out.println(connections.charAt(j+1));
                    System.out.println(inputArray[0].indexOf(connections.charAt(j+1)));
                    System.out.println(nodes.get(2));
                    System.out.println(j);
                    neighbors.put(nodes.get(inputArray[0].indexOf(connections.charAt(j+1))) ,Integer.valueOf(Character.toString(connections.charAt(j))));
                }
                nodes.get(inputArray[0].indexOf(currentNode)).setNeighbors(neighbors);
            }
            return nodes;
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args){
        parseFileToFactoryNodeSetup(new File("nodetest.txt")).forEach(factoryNode -> System.out.println(factoryNode.getKey()+": " + factoryNode.getNeighbors()));
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
