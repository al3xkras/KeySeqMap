package ua.alexkras;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class KeySeqMapTest {

    @Test
    public void testFindExact1(){

        KeySeqMap<Integer,String> keySeqMap = new KeySeqMap<>();

        keySeqMap.put(Arrays.asList(1,2,3),"A");
        keySeqMap.put(Arrays.asList(1,2),"B");
        keySeqMap.put(Arrays.asList(1,9,10),"C");

        Assert.assertEquals("C", keySeqMap.findExact(Arrays.asList(1,9,10)));

        Assert.assertNull(keySeqMap.findExact(Collections.singletonList(10)));

    }

    @Test
    public void testFindExact2(){

        KeySeqMap<Integer,String> keySeqMap = new KeySeqMap<>();

        keySeqMap.put(Arrays.asList(1,2,3),"A");
        keySeqMap.put(Arrays.asList(1,2),"B");
        keySeqMap.put(Arrays.asList(1,9,10),"C");
        keySeqMap.put(Arrays.asList(1,2),"D");

        Assert.assertEquals("D", keySeqMap.findExact(Arrays.asList(1,2)));

        Assert.assertNull(keySeqMap.findExact(Arrays.asList(1,2,3,4,5553943)));

    }

    @Test
    public void testFIndExact3(){
        KeySeqMap<Integer,String> keySeqMap = new KeySeqMap<>();

        keySeqMap.put(Arrays.asList(1,2,3,4,5,6),"A");
        keySeqMap.put(Arrays.asList(1,2,4),"B");
        keySeqMap.put(Arrays.asList(1,3),"C");
        keySeqMap.put(Arrays.asList(2,5),"D");
        keySeqMap.put(Arrays.asList(2,4,5),"E");
        keySeqMap.put(Arrays.asList(1,4,6,7),"F");
        keySeqMap.put(Arrays.asList(2,3,5,6),"G");

        keySeqMap.put(Arrays.asList(2,4,5,6,8),"H");

        keySeqMap.put(Arrays.asList(1,2,4,5,9),"I");

        keySeqMap.put(Arrays.asList(2,4,8,10),"J");
        keySeqMap.put(Arrays.asList(2,8,11),"K");
        keySeqMap.put(Arrays.asList(2,4,12),"L");
        keySeqMap.put(Arrays.asList(2,4,8,13),"M");
        keySeqMap.put(Arrays.asList(2,4,8,13,14,9),"N");

        keySeqMap.put(Arrays.asList(2,4,8,15),"O");

        keySeqMap.put(Arrays.asList(2,4,8,16),"P");

        keySeqMap.put(Arrays.asList(2,5,9,14,16,17),"Q");
        keySeqMap.put(Arrays.asList(4,8,16),"R");

        System.out.println(keySeqMap.findExact(Arrays.asList(2,5,9,14,16,17)));
    }

    @Test
    public void testFindAll1(){
        KeySeqMap<Integer,String> keySeqMap = new KeySeqMap<>();

        keySeqMap.put(Arrays.asList(1,2,3,4,5,16),"A");
        keySeqMap.put(Arrays.asList(1,2,4),"B");
        keySeqMap.put(Arrays.asList(1,3),"C");
        keySeqMap.put(Arrays.asList(2,5),"D");
        keySeqMap.put(Arrays.asList(2,4,5),"E");
        keySeqMap.put(Arrays.asList(1,4,6,7),"F");
        keySeqMap.put(Arrays.asList(1,3,5,6),"G");

        keySeqMap.put(Arrays.asList(2,4,5,6,8),"H");

        keySeqMap.put(Arrays.asList(1,2,4,5,9),"I");

        keySeqMap.put(Arrays.asList(2,4,8,10),"J");
        keySeqMap.put(Arrays.asList(2,8,11),"K");
        keySeqMap.put(Arrays.asList(2,4,12),"L");
        keySeqMap.put(Arrays.asList(2,4,8,13),"M");
        keySeqMap.put(Arrays.asList(2,4,8,13,14,9),"N");

        keySeqMap.put(Arrays.asList(2,4,8,15),"O");

        keySeqMap.put(Arrays.asList(2,4,8,16),"P");

        keySeqMap.put(Arrays.asList(2,5,9,17),"Q");
        keySeqMap.put(Arrays.asList(4,8,16),"R");
        keySeqMap.put(Arrays.asList(1,2,3,5,9,17),"S");

        //Iterator<String> all = keySeqMap.findALl();

        //System.out.println(keySeqMap.head);

        keySeqMap.findAll(Arrays.asList(4,16)).stream().sorted().forEach(System.out::println);

    }


    //Test passed 26.06.2022
    @Test
    public void testFindAll2(){

        int[][] tests = new int[][]{
                {-50,1},
                {-20,50},
                {-10,2,10},
                {-10,3,4,9,11},
                {-35,3,4},
                {-30,9,11},
                {-35,10,45,34},
                {-40,10,100,1000,34},
                {-20,255,511,1023},
                {-50,999,124,63,34,66},
                {-15,1,3,5,7,9,13,100,300,563,59,567,478,235,1000},
                {-10,7,11,70,110,700,1100,777,1111,999,1199,214,562,895,124,155,156}
        };

        int entriesCount = 100000;
        int uniqueKeysCount = 10000000;
        int addMax = 1;

        int unused = entriesCount;
        Set<Integer> used = new HashSet<>();

        for (int[] test : tests) {
            for (int i : test) {
                if (i>0){
                    used.add(i);
                } else {
                    unused+=i;
                }
            }
        }

        int[] entries = new int[entriesCount];
        ArrayList<ArrayList<Integer>> keys = new ArrayList<>(entriesCount);
        for (int i=0;i<entries.length;i++) {
            entries[i]=i+1;
        }


        for (int[] test : tests) {


            for (int i = 0; i < -test[0]; i++) {

                int randomKeysCount = -test[0] + ThreadLocalRandom.current().nextInt(1, addMax+1);
                Set<Integer> toAdd = new HashSet<>(randomKeysCount);
                for (int i1 : test) {
                    if (i1 > 0)
                        toAdd.add(i1);
                }

                for (int j = test[0] - 1; j < randomKeysCount; j++) {
                    int k = -1;
                    while (k < 0 || toAdd.contains(k)) {
                        k = ThreadLocalRandom.current().nextInt(0, uniqueKeysCount);
                    }
                    toAdd.add(k);
                }
                keys.add(new ArrayList<>(toAdd));
            }
        }

        for (int i=0; i<unused; i++){
            int randomKeysCount = ThreadLocalRandom.current().nextInt(1,addMax+1);
            Set<Integer> toAdd = new HashSet<>(randomKeysCount);

            for (int j=0; j<randomKeysCount; j++){
                int k = -1;
                while (k<0 || used.contains(k) || toAdd.contains(k)){
                    k=ThreadLocalRandom.current().nextInt(0,uniqueKeysCount);
                }
                toAdd.add(k);
            }
            keys.add(new ArrayList<>(toAdd));
        }

        KeySeqMap<Integer,Integer> testMap = new KeySeqMap<>();
        for (int i=0; i<entriesCount; i++){
            testMap.put(keys.get(i),entries[i]);
        }

        for (int testId = 0; testId<tests.length; testId++) {
            TreeSet<Integer> expected = Arrays.stream(tests[testId]).filter(x -> x > 0).boxed().collect(Collectors.toCollection(TreeSet::new));

            TreeSet<Integer> actual = new TreeSet<>(testMap.findAll(
                    Arrays.stream(tests[testId]).skip(1).boxed().collect(Collectors.toList())
            ));

            actual=actual.stream().map(x->keys.get(x-1)).map(TreeSet::new).reduce((x, y)->{
                x.retainAll(y);
                return x;
            }).orElse(null);

            Assert.assertNotNull(actual);
            Assert.assertEquals(expected,actual);
        }

    }

}

