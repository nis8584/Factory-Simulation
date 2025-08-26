package factory.queueAndScheduler;

import java.io.*;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueueParser {

    public  Queue parseFileToQueue(File file){
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

    public static void main(String[] args){
        String s = new QueueParser().parseFileToQueue(new File("src/main/java/factory/queueAndScheduler/test.txt")).toString();
        System.out.println(s);
    }

    private String readFromInputStream(InputStream inputStream)
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
