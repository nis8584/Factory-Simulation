package factory.controlledSystem;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Factory {
    FactoryNode fn1 = new FactoryNode();
    FactoryNode fn2 = new FactoryNode();
    FactoryNode fn3 = new FactoryNode();
    FactoryNode fn4 = new FactoryNode();
    FactoryNode fn5 = new FactoryNode();
    FactoryNode fn6 = new FactoryNode();
    FactoryNode fn7 = new FactoryNode();
    FactoryNode fn8 = new FactoryNode();
    FactoryNode fn9 = new FactoryNode();

    public Factory() {
        //individual connections and costs
        Map<FactoryNode, Integer> m1 = new HashMap<>();
        m1.put(fn2, 1);
        fn1.setNeighbors(m1);

        Map<FactoryNode, Integer> m2 = new HashMap<>();
        m2.put(fn3, 1);
        fn2.setNeighbors(m2);

        Map<FactoryNode, Integer> m3 = new HashMap<>();
        m3.put(fn4, 1);
        fn3.setNeighbors(m3);

        Map<FactoryNode, Integer> m4 = new HashMap<>();
        m4.put(fn5, 1);
        m4.put(fn9, 1);
        fn4.setNeighbors(m4);

        Map<FactoryNode, Integer> m5 = new HashMap<>();
        m5.put(fn6, 1);
        fn5.setNeighbors(m5);

        Map<FactoryNode, Integer> m6 = new HashMap<>();
        m6.put(fn7, 1);
        fn6.setNeighbors(m6);

        Map<FactoryNode, Integer> m7 = new HashMap<>();
        m7.put(fn8, 1);
        fn8.setNeighbors(m7);

        Map<FactoryNode, Integer> m8 = new HashMap<>();
        m8.put(fn1, 1);
        fn8.setNeighbors(m8);

        Map<FactoryNode, Integer> m9 = new HashMap<>();
        m9.put(fn8, 1);
        fn9.setNeighbors(m9);

        //position for gui grid
        fn1.setPosition("0,0");
        fn2.setPosition("0,2");
        fn3.setPosition("0,4");
        fn4.setPosition("2,4");
        fn5.setPosition("4,4");
        fn6.setPosition("4,2");
        fn7.setPosition("4,0");
        fn8.setPosition("2,0");
        fn9.setPosition("2,2");
        System.out.println(FactoryNode.findPath(fn2,fn1,0).toString());
       /* LinkedList<FactoryNode> test = new LinkedList<>();
        test.add(fn3);
        test.add(fn4);
        test.add(fn9);
        System.out.println(FactoryNode.costPerPath(test));
        */
    }

    public List<FactoryNode> getFactoryNodes(){
        List<FactoryNode> list = new LinkedList();
        list.add(fn1);
        list.add(fn2);
        list.add(fn3);
        list.add(fn4);
        list.add(fn5);
        list.add(fn6);
        list.add(fn7);
        list.add(fn8);
        list.add(fn9);
        return list;
    }

}
