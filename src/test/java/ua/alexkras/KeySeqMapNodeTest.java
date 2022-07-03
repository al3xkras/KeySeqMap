package ua.alexkras;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class KeySeqMapNodeTest {

    @Test
    public void testFindExact1(){

        KeySeqMapNode<Integer,String> keySeqMapNode = new KeySeqMapNode<>();

        keySeqMapNode.put(Arrays.asList(1,2,3),"A");
        keySeqMapNode.put(Arrays.asList(1,2),"B");
        keySeqMapNode.put(Arrays.asList(1,9,10),"C");

        Assert.assertEquals("C", keySeqMapNode.findExact(Arrays.asList(1,9,10)));

        Assert.assertNull(keySeqMapNode.findExact(Collections.singletonList(10)));
    }

    @Test
    public void testFindExact2(){

        KeySeqMapNode<Integer,String> keySeqMapNode = new KeySeqMapNode<>();

        keySeqMapNode.put(Arrays.asList(1,2,3),"A");
        keySeqMapNode.put(Arrays.asList(1,2),"B");
        keySeqMapNode.put(Arrays.asList(1,9,10),"C");
        keySeqMapNode.put(Arrays.asList(1,2),"D");

        Assert.assertEquals("D", keySeqMapNode.findExact(Arrays.asList(1,2)));

        Assert.assertNull(keySeqMapNode.findExact(Arrays.asList(1,2,3,4,5553943)));
    }

    @Test
    public void testFIndExact3(){
        KeySeqMapNode<Integer,String> keySeqMapNode = new KeySeqMapNode<>();

        keySeqMapNode.put(Arrays.asList(1,2,3,4,5,6),"A");
        keySeqMapNode.put(Arrays.asList(1,2,4),"B");
        keySeqMapNode.put(Arrays.asList(1,3),"C");
        keySeqMapNode.put(Arrays.asList(2,5),"D");
        keySeqMapNode.put(Arrays.asList(2,4,5),"E");
        keySeqMapNode.put(Arrays.asList(1,4,6,7),"F");
        keySeqMapNode.put(Arrays.asList(2,3,5,6),"G");
        keySeqMapNode.put(Arrays.asList(2,4,5,6,8),"H");
        keySeqMapNode.put(Arrays.asList(1,2,4,5,9),"I");
        keySeqMapNode.put(Arrays.asList(2,4,8,10),"J");
        keySeqMapNode.put(Arrays.asList(2,8,11),"K");
        keySeqMapNode.put(Arrays.asList(2,4,12),"L");
        keySeqMapNode.put(Arrays.asList(2,4,8,13),"M");
        keySeqMapNode.put(Arrays.asList(2,4,8,13,14,9),"N");
        keySeqMapNode.put(Arrays.asList(2,4,8,15),"O");
        keySeqMapNode.put(Arrays.asList(2,4,8,16),"P");
        keySeqMapNode.put(Arrays.asList(2,5,9,14,16,17),"Q");
        keySeqMapNode.put(Arrays.asList(4,8,16),"R");

        System.out.println(keySeqMapNode.findExact(Arrays.asList(2,5,9,14,16,17)));
    }

    @Test
    public void testFindAll1(){
        KeySeqMapNode<Integer,String> keySeqMapNode = new KeySeqMapNode<>();

        keySeqMapNode.put(Arrays.asList(1,2,3,4,5,16),"A");
        keySeqMapNode.put(Arrays.asList(1,2,4),"B");
        keySeqMapNode.put(Arrays.asList(1,3),"C");
        keySeqMapNode.put(Arrays.asList(2,5),"D");
        keySeqMapNode.put(Arrays.asList(2,4,5),"E");
        keySeqMapNode.put(Arrays.asList(1,4,6,7),"F");
        keySeqMapNode.put(Arrays.asList(1,3,5,6),"G");
        keySeqMapNode.put(Arrays.asList(2,4,5,6,8),"H");
        keySeqMapNode.put(Arrays.asList(1,2,4,5,9),"I");
        keySeqMapNode.put(Arrays.asList(2,4,8,10),"J");
        keySeqMapNode.put(Arrays.asList(2,8,11),"K");
        keySeqMapNode.put(Arrays.asList(2,4,12),"L");
        keySeqMapNode.put(Arrays.asList(2,4,8,13),"M");
        keySeqMapNode.put(Arrays.asList(2,4,8,13,14,9),"N");
        keySeqMapNode.put(Arrays.asList(2,4,8,15),"O");
        keySeqMapNode.put(Arrays.asList(2,4,8,16),"P");
        keySeqMapNode.put(Arrays.asList(2,5,9,17),"Q");
        keySeqMapNode.put(Arrays.asList(4,8,16),"R");
        keySeqMapNode.put(Arrays.asList(1,2,3,5,9,17),"S");
        keySeqMapNode.put(Arrays.asList(1,2,5,16),"T");

        //keySeqMapNode.findAll(Arrays.asList(1,3)).forEachRemaining(System.out::println);

        System.out.println(keySeqMapNode);
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

        KeySeqMapNode<Integer,Integer> testMap = new KeySeqMapNode<>();
        for (int i=0; i<entriesCount; i++){
            testMap.put(keys.get(i),entries[i]);
        }

        for (int testId = 0; testId<tests.length; testId++) {
            TreeSet<Integer> expected = Arrays.stream(tests[testId]).filter(x -> x > 0).boxed().collect(Collectors.toCollection(TreeSet::new));

            TreeSet<Integer> actual = new TreeSet<>();
            testMap.findAll(
                    Arrays.stream(tests[testId]).skip(1).boxed().collect(Collectors.toList())
            ).forEachRemaining(actual::add);

            actual=actual.stream().map(x->keys.get(x-1)).map(TreeSet::new).reduce((x, y)->{
                x.retainAll(y);
                return x;
            }).orElse(null);

            Assert.assertNotNull(actual);
            Assert.assertEquals(expected,actual);
        }

    }

    @Test
    public void testFindAll3(){
        KeySeqMapNode<Integer,String> keySeqMapNode = new KeySeqMapNode<>();
        keySeqMapNode.put(Arrays.asList(1),"B");
        keySeqMapNode.findAll(Arrays.asList(1)).forEachRemaining(System.out::println);
    }

    @Test
    public void testFindAll4(){
        KeySeqMapNode<Integer,String> keySeqMapNode = new KeySeqMapNode<>();

        keySeqMapNode.put(Arrays.asList(2,5,9,17),"Q");
        keySeqMapNode.put(Arrays.asList(1,2,3,5,9,17),"S");
        keySeqMapNode.put(Arrays.asList(1,3,5,9,17),"U");

        keySeqMapNode.findAll(Arrays.asList(1,3)).forEachRemaining(System.out::println);

        System.out.println(keySeqMapNode);
    }


    @Test
    public void testSize(){
        KeySeqMapNode<Integer,String> keySeqMapNode = new KeySeqMapNode<>();

        Assert.assertEquals(keySeqMapNode.size(),0);

        keySeqMapNode.put(Arrays.asList(1,2,3,4,5,16),"A");
        keySeqMapNode.put(Arrays.asList(1,2,4),"B");
        keySeqMapNode.put(Arrays.asList(1,3),"C");

        Assert.assertEquals(keySeqMapNode.size(),3);

        keySeqMapNode.put(Arrays.asList(1,2,4),"M");
        Assert.assertEquals(keySeqMapNode.size(),3);

        keySeqMapNode.remove(Arrays.asList(1,3));
        Assert.assertEquals(keySeqMapNode.size(),2);

        keySeqMapNode.remove(Arrays.asList(9,19));
        Assert.assertEquals(keySeqMapNode.size(),2);

        keySeqMapNode.put(Arrays.asList(2,5),"D");
        keySeqMapNode.put(Arrays.asList(2,4,5),"E");
        keySeqMapNode.put(Arrays.asList(1,4,6,7),"F");
        keySeqMapNode.put(Arrays.asList(1,3,5,6),"G");
        keySeqMapNode.put(Arrays.asList(1,3,5,6),"G1");
        keySeqMapNode.put(Arrays.asList(1,4,6,7),"F1");

        Assert.assertEquals(keySeqMapNode.size(),6);

        keySeqMapNode.remove(Arrays.asList(1,10,45));
        Assert.assertEquals(keySeqMapNode.size(),6);

        keySeqMapNode.remove(Arrays.asList(1,3,5,6));
        keySeqMapNode.remove(Arrays.asList(1,4,6,7));
        Assert.assertEquals(keySeqMapNode.size(),4);


        keySeqMapNode.put(Arrays.asList(1,3,5,6),"G2");
        Assert.assertEquals(keySeqMapNode.size(),5);

        keySeqMapNode.put(Arrays.asList(2,4,5,6,8),"H");
        keySeqMapNode.remove(Arrays.asList(2,4,5,6,8));
        keySeqMapNode.put(Arrays.asList(1,2,4,5,9),"I");

        Assert.assertEquals(keySeqMapNode.size(),6);

        keySeqMapNode.remove(Arrays.asList(2,5));
        keySeqMapNode.put(Arrays.asList(2,4,8,10),"J");
        keySeqMapNode.put(Arrays.asList(2,8,11),"K");
        Assert.assertEquals(keySeqMapNode.size(),7);

    }


    @Test
    public void testContainsKey(){
        KeySeqMapNode<Integer,String> keySeqMapNode = new KeySeqMapNode<>();

        keySeqMapNode.put(Arrays.asList(1,2,3,4,5,16),"A");
        keySeqMapNode.put(Arrays.asList(1,2,4),"B");
        keySeqMapNode.put(Arrays.asList(1,3),"C");

        Assert.assertFalse(keySeqMapNode.containsKey(null));
        Assert.assertFalse(keySeqMapNode.containsKey("invalid key"));
        Assert.assertTrue(keySeqMapNode.containsKey(Arrays.asList(1,2,4)));

        keySeqMapNode.put(Arrays.asList(1,2,4),"M");

        Assert.assertTrue(keySeqMapNode.containsKey(Arrays.asList(1,2,4)));
        Assert.assertTrue(keySeqMapNode.containsKey(Arrays.asList(1,3)));

        keySeqMapNode.remove(Arrays.asList(1,3));

        Assert.assertFalse(keySeqMapNode.containsKey(Arrays.asList(1,3)));
        Assert.assertFalse(keySeqMapNode.containsKey(Arrays.asList(9,19)));

        keySeqMapNode.remove(Arrays.asList(9,19));

        Assert.assertFalse(keySeqMapNode.containsKey(Arrays.asList(9,19)));

        keySeqMapNode.put(Arrays.asList(2,5),"D");
        keySeqMapNode.put(Arrays.asList(2,4,5),"E");
        keySeqMapNode.put(Arrays.asList(1,4,6,7),"F");
        keySeqMapNode.put(Arrays.asList(1,3,5,6),"G");
        keySeqMapNode.put(Arrays.asList(1,3,5,6),"G1");
        keySeqMapNode.put(Arrays.asList(1,4,6,7),"F1");

        Assert.assertTrue(keySeqMapNode.containsKey(Arrays.asList(1,3,5,6)));
        Assert.assertTrue(keySeqMapNode.containsKey(Arrays.asList(2,4,5)));

        keySeqMapNode.put(Arrays.asList(2,4,5,6,8),"H");
        keySeqMapNode.remove(Arrays.asList(2,4,5,6,8));
        keySeqMapNode.put(Arrays.asList(1,2,4,5,9),"I");

        Assert.assertFalse(keySeqMapNode.containsKey(Arrays.asList(2,4,5,6,8)));
    }


}

