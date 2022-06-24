import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

public class KeySeqMapTest {

    @Test
    public void testCompareKeys(){

        Assert.assertEquals(
                0,(new KeySeqMap.KeySeq<>(Arrays.asList(1, 2, 3))).compareTo(
                        new KeySeqMap.KeySeq<>(Arrays.asList(1, 2, 3)
                )));

        Assert.assertEquals(
                0,(new KeySeqMap.KeySeq<>(Arrays.asList(6,3,1001))).compareTo(
                        new KeySeqMap.KeySeq<>(Arrays.asList(1001, 3, 6)
                        )));

        Assert.assertEquals(
                1,(new KeySeqMap.KeySeq<>(Arrays.asList(1, 2, 3))).compareTo(
                        new KeySeqMap.KeySeq<>(Arrays.asList(1, 2, 1002)
                        )));

        Assert.assertEquals(
                1,(new KeySeqMap.KeySeq<>(Arrays.asList(7,49,3))).compareTo(
                        new KeySeqMap.KeySeq<>(Arrays.asList(49,3)
                        )));

        Assert.assertEquals(
                -1,(new KeySeqMap.KeySeq<>(Arrays.asList(7,49,3))).compareTo(
                        new KeySeqMap.KeySeq<>(Arrays.asList(7,49,3,10)
                        )));
    }

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
    public void testFindAll1(){
        KeySeqMap<Integer,String> keySeqMap = new KeySeqMap<>();

        keySeqMap.add(Arrays.asList(1,2,3),"A");
        keySeqMap.add(Arrays.asList(1,2,7,8),"B");
        keySeqMap.add(Arrays.asList(1,9),"C");
        keySeqMap.add(Arrays.asList(1,2,4),"D");
        keySeqMap.add(Arrays.asList(2,4,8,9,10),"E");
        keySeqMap.add(Arrays.asList(3,7,9),"F");
        keySeqMap.add(Arrays.asList(3,4,6,10,12,14),"G");
        keySeqMap.add(Arrays.asList(2,4,8,9),"H");
        keySeqMap.add(Arrays.asList(2,8,9),"I");
        keySeqMap.add(Arrays.asList(2,4),"J");
        keySeqMap.add(Arrays.asList(2,4,8,13,14),"K");
        keySeqMap.add(Arrays.asList(2,4,8,13,14,9),"L");

        keySeqMap.add(Arrays.asList(2,4,8,496,34,22),"M");

        keySeqMap.add(Arrays.asList(2,4,8,92,3,12,9),"N");

        keySeqMap.add(Arrays.asList(2,492,45,192,229,439,5,9),"O");

        Iterator<String> all = keySeqMap.findALl(Arrays.asList(2,8,9));

        System.out.println(keySeqMap.findExact(Arrays.asList(2,4,8,9,10)));
        while (all.hasNext()){
            System.out.println(all.next());
        }
    }
}

