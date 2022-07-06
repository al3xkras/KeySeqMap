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
        KeySeqMap<Integer,String> keySeqMap = new KeySeqMap<>(10);

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
        keySeqMap.put(Arrays.asList(1,2,5,16),"T");
        keySeqMap.put(Arrays.asList(1,3,5,9,17),"U");

        keySeqMap.findAll(Arrays.asList(2,4)).forEach(System.out::println);
    }

    @Test
    public void testFindAll2(){
        KeySeqMap<Integer,String> keySeqMap = new KeySeqMap<>();
        keySeqMap.put(Arrays.asList(1),"B");
        keySeqMap.findAll(Arrays.asList(1)).forEach(System.out::println);
    }



    //Test Passed 06.07.2022
    @Test
    public void testFindAll3(){

        int[][] tests = new int[][]{
                {-50,1},
                {-20,50},
                {-10,2,10},
                {-10,3,8,9,12},
                {-35,3,4},
                {-30,9,11},
                {-35,10,45,34},
                {-40,10,100,1000,34},
                {-20,255,511,1023},
                {-50,999,124,63,34,66},
                {-15,3,5,7,9,13,100,300,563,59,567,478,235,1000},
                {-10,7,11,70,110,700,1100,777,1111,999,1199,214,562,895,124,155,156}
        };

        int entriesCount = 100000;
        int uniqueKeysCount = 10000000;
        int addMax = 10;

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

            Collection<Integer> found = testMap.findAll(
                    Arrays.stream(tests[testId]).skip(1).boxed().collect(Collectors.toList())
            );
            TreeSet<Integer> actual = new TreeSet<>(found);


            actual=actual.stream().map(x->keys.get(x-1)).map(TreeSet::new).reduce((x, y)->{
                x.retainAll(y);
                return x;
            }).orElse(null);

            Assert.assertNotNull(actual);
            Assert.assertEquals(expected,actual);
            Assert.assertEquals(found.size(),-tests[testId][0]);
            System.out.println(-tests[testId][0]+" "+found.size());
        }

    }

    @Test
    public void testSize(){
        KeySeqMap<Integer,String> keySeqMap = new KeySeqMap<>();

        Assert.assertEquals(keySeqMap.size(),0);

        keySeqMap.put(Arrays.asList(1,2,3,4,5,16),"A");
        keySeqMap.put(Arrays.asList(1,2,4),"B");
        keySeqMap.put(Arrays.asList(1,3),"C");

        Assert.assertEquals(3,keySeqMap.size());

        keySeqMap.put(Arrays.asList(1,2,4),"M");
        Assert.assertEquals(3,keySeqMap.size());

        keySeqMap.remove(Arrays.asList(1,3));
        Assert.assertEquals(2,keySeqMap.size());

        keySeqMap.remove(Arrays.asList(9,19));
        Assert.assertEquals(2,keySeqMap.size());

        keySeqMap.put(Arrays.asList(2,5),"D");
        keySeqMap.put(Arrays.asList(2,4,5),"E");
        keySeqMap.put(Arrays.asList(1,4,6,7),"F");
        keySeqMap.put(Arrays.asList(1,3,5,6),"G");
        keySeqMap.put(Arrays.asList(1,3,5,6),"G1");
        keySeqMap.put(Arrays.asList(1,4,6,7),"F1");

        Assert.assertEquals(6,keySeqMap.size());

        keySeqMap.remove(Arrays.asList(1,10,45));
        Assert.assertEquals(6,keySeqMap.size());

        keySeqMap.remove(Arrays.asList(1,3,5,6));
        keySeqMap.remove(Arrays.asList(1,4,6,7));
        Assert.assertEquals(4,keySeqMap.size());


        keySeqMap.put(Arrays.asList(1,3,5,6),"G2");
        Assert.assertEquals(5,keySeqMap.size());

        keySeqMap.put(Arrays.asList(2,4,5,6,8),"H");
        keySeqMap.remove(Arrays.asList(2,4,5,6,8));
        keySeqMap.put(Arrays.asList(1,2,4,5,9),"I");

        Assert.assertEquals(6,keySeqMap.size());

        keySeqMap.remove(Arrays.asList(2,5));
        keySeqMap.put(Arrays.asList(2,4,8,10),"J");
        keySeqMap.put(Arrays.asList(2,8,11),"K");
        Assert.assertEquals(7,keySeqMap.size());

    }


    @Test
    public void testContainsKey(){
        KeySeqMap<Integer,String> keySeqMap = new KeySeqMap<>();

        keySeqMap.put(Arrays.asList(1,2,3,4,5,16),"A");
        keySeqMap.put(Arrays.asList(1,2,4),"B");
        keySeqMap.put(Arrays.asList(1,3),"C");

        Assert.assertFalse(keySeqMap.containsKey(null));
        Assert.assertFalse(keySeqMap.containsKey("invalid key"));
        Assert.assertTrue(keySeqMap.containsKey(Arrays.asList(1,2,4)));

        keySeqMap.put(Arrays.asList(1,2,4),"M");

        Assert.assertTrue(keySeqMap.containsKey(Arrays.asList(1,2,4)));
        Assert.assertTrue(keySeqMap.containsKey(Arrays.asList(1,3)));

        keySeqMap.remove(Arrays.asList(1,3));

        Assert.assertFalse(keySeqMap.containsKey(Arrays.asList(1,3)));
        Assert.assertFalse(keySeqMap.containsKey(Arrays.asList(9,19)));

        keySeqMap.remove(Arrays.asList(9,19));

        Assert.assertFalse(keySeqMap.containsKey(Arrays.asList(9,19)));

        keySeqMap.put(Arrays.asList(2,5),"D");
        keySeqMap.put(Arrays.asList(2,4,5),"E");
        keySeqMap.put(Arrays.asList(1,4,6,7),"F");
        keySeqMap.put(Arrays.asList(1,3,5,6),"G");
        keySeqMap.put(Arrays.asList(1,3,5,6),"G1");
        keySeqMap.put(Arrays.asList(1,4,6,7),"F1");

        Assert.assertTrue(keySeqMap.containsKey(Arrays.asList(1,3,5,6)));
        Assert.assertTrue(keySeqMap.containsKey(Arrays.asList(2,4,5)));

        keySeqMap.put(Arrays.asList(2,4,5,6,8),"H");
        keySeqMap.remove(Arrays.asList(2,4,5,6,8));
        keySeqMap.put(Arrays.asList(1,2,4,5,9),"I");

        Assert.assertFalse(keySeqMap.containsKey(Arrays.asList(2,4,5,6,8)));
    }
}
