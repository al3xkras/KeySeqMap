import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public class KeySeqMapTest {

    @Test
    public void testFindExact1(){

        KeySeqMap<Integer,String> keySeqMap = new KeySeqMap<>();

        keySeqMap.add(Arrays.asList(1,2,3),"A");
        keySeqMap.add(Arrays.asList(1,2),"B");
        keySeqMap.add(Arrays.asList(1,9,10),"C");

        Assert.assertEquals("C", keySeqMap.findExact(Arrays.asList(1,9,10)));

        Assert.assertNull(keySeqMap.findExact(Collections.singletonList(10)));

    }

    @Test
    public void testFindExact2(){

        KeySeqMap<Integer,String> keySeqMap = new KeySeqMap<>();

        keySeqMap.add(Arrays.asList(1,2,3),"A");
        keySeqMap.add(Arrays.asList(1,2),"B");
        keySeqMap.add(Arrays.asList(1,9,10),"C");
        keySeqMap.add(Arrays.asList(1,2),"D");

        Assert.assertEquals("D", keySeqMap.findExact(Arrays.asList(1,2)));

        Assert.assertNull(keySeqMap.findExact(Arrays.asList(1,2,3,4,5553943)));

    }

    @Test
    public void testFIndExact3(){
        KeySeqMap<Integer,String> keySeqMap = new KeySeqMap<>();

        keySeqMap.add(Arrays.asList(1,2,3,4,5,6),"A");
        keySeqMap.add(Arrays.asList(1,2,4),"B");
        keySeqMap.add(Arrays.asList(1,3),"C");
        keySeqMap.add(Arrays.asList(2,5),"D");
        keySeqMap.add(Arrays.asList(2,4,5),"E");
        keySeqMap.add(Arrays.asList(1,4,6,7),"F");
        keySeqMap.add(Arrays.asList(2,3,5,6),"G");

        keySeqMap.add(Arrays.asList(2,4,5,6,8),"H");

        keySeqMap.add(Arrays.asList(1,2,4,5,9),"I");

        keySeqMap.add(Arrays.asList(2,4,8,10),"J");
        keySeqMap.add(Arrays.asList(2,8,11),"K");
        keySeqMap.add(Arrays.asList(2,4,12),"L");
        keySeqMap.add(Arrays.asList(2,4,8,13),"M");
        keySeqMap.add(Arrays.asList(2,4,8,13,14,9),"N");

        keySeqMap.add(Arrays.asList(2,4,8,15),"O");

        keySeqMap.add(Arrays.asList(2,4,8,16),"P");

        keySeqMap.add(Arrays.asList(2,5,9,14,16,17),"Q");
        keySeqMap.add(Arrays.asList(4,8,16),"R");

        System.out.println(keySeqMap.findExact(Arrays.asList(2,5,9,14,16,17)));
    }

    @Test
    public void testFindAll1(){
        KeySeqMap<Integer,String> keySeqMap = new KeySeqMap<>();

        keySeqMap.add(Arrays.asList(1,2,3,4,5,16),"A");
        keySeqMap.add(Arrays.asList(1,2,4),"B");
        keySeqMap.add(Arrays.asList(1,3),"C");
        keySeqMap.add(Arrays.asList(2,5),"D");
        keySeqMap.add(Arrays.asList(2,4,5),"E");
        keySeqMap.add(Arrays.asList(1,4,6,7),"F");
        keySeqMap.add(Arrays.asList(2,3,5,6),"G");

        keySeqMap.add(Arrays.asList(2,4,5,6,8),"H");

        keySeqMap.add(Arrays.asList(1,2,4,5,9),"I");

        keySeqMap.add(Arrays.asList(2,4,8,10),"J");
        keySeqMap.add(Arrays.asList(2,8,11),"K");
        keySeqMap.add(Arrays.asList(2,4,12),"L");
        keySeqMap.add(Arrays.asList(2,4,8,13),"M");
        keySeqMap.add(Arrays.asList(2,4,8,13,14,9),"N");

        keySeqMap.add(Arrays.asList(2,4,8,15),"O");

        keySeqMap.add(Arrays.asList(2,4,8,16),"P");

        keySeqMap.add(Arrays.asList(2,5,9,17),"Q");
        keySeqMap.add(Arrays.asList(4,8,16),"R");

        //Iterator<String> all = keySeqMap.findALl();

        //System.out.println(keySeqMap.head);

        keySeqMap.findAll(Arrays.asList(2,16)).stream().sorted().forEach(System.out::println);

    }
}

