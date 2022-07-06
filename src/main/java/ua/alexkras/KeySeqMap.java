package ua.alexkras;

import java.util.*;

public class KeySeqMap<K extends Comparable<K>,V> implements Map<Collection<K>,V> {

    private final TreeMap<K,Integer> keyMapping = new TreeMap<>();
    private final KeySeqMapNode<Integer,Object> nodes = new KeySeqMapNode<>();
    private int nextKeyMapping = 1;

    private long size = 0L;
    private final long scale;
    private final int nodeSize;

    public KeySeqMap(){
        nodeSize = 100;
        scale = (long) 1e10;
    }

    public KeySeqMap(long scale, int nodeSize){
        this.nodeSize = nodeSize;
        this.scale=scale;
    }

    public KeySeqMap(int nodeSize){
        this.nodeSize = nodeSize;
        this.scale= (long) 1e10;
    }

    public V findExact(Collection<K> keys){
        KeySeqMapNode<K,V> exactNode = findExactNode(keys);
        return exactNode==null?null:exactNode.findExact(keys);
    }

    private KeySeqMapNode<K,V> findExactNode(Collection<K> key){
        Stack<ArrayList<Integer>> keySeq = new Stack<>();
        ArrayList<Integer> iterKey = mapAndUpdateMapping(key);
        long currentScale = nodeSize;

        while (currentScale<scale){
            iterKey = splitImageIndices(iterKey,nodeSize);
            keySeq.push(iterKey);
            currentScale*=nodeSize;
        }

        KeySeqMapNode<Integer,Object> iterNodes = nodes;
        while (!keySeq.isEmpty()){
            Object nextNode = iterNodes.findExact(keySeq.pop());
            if (nextNode==null)
                return null;
            iterNodes = (KeySeqMapNode<Integer, Object>) nextNode;
        }

        KeySeqMapNode<K, V> exactNode = (KeySeqMapNode<K, V>)iterNodes.findExact(iterKey);
        return exactNode;
    }

    private LinkedList<KeySeqMapNode<K,V>> findAllNodes(Collection<K> key){
        //TODO test
        Stack<ArrayList<Integer>> keySeq = new Stack<>();
        ArrayList<Integer> iterKey = mapAndUpdateMapping(key);
        long currentScale = nodeSize;

        while (currentScale<scale){
            iterKey = splitImageIndices(iterKey,nodeSize);
            keySeq.push(iterKey);
            currentScale*=nodeSize;
        }

        LinkedList<KeySeqMapNode<Integer,Object>> iter = new LinkedList<>();
        iter.add(nodes);

        while (!keySeq.isEmpty()){
            int sizeInitial = iter.size();
            if (sizeInitial==0)
                return new LinkedList<>();
            ArrayList<Integer> keyIter = keySeq.pop();
            for (int i=0; i<sizeInitial; i++){
                KeySeqMapNode<Integer,Object> first = iter.removeFirst();
                first.findAll(keyIter).forEachRemaining(o->iter.addLast((KeySeqMapNode<Integer, Object>) o));
            }
        }

        LinkedList<KeySeqMapNode<K,V>> out = new LinkedList<>();

        int sizeInitial = iter.size();
        for (int i=0; i<sizeInitial; i++){
            KeySeqMapNode<Integer,Object> first = iter.removeFirst();
            first.findAll(iterKey).forEachRemaining(o->out.addLast((KeySeqMapNode<K, V>) o));
        }
        return out;
    }

    public List<V> findAll(Collection<K> keys){
        return findAll(keys,0,-1);
    }

    public List<V> findAll(Collection<K> keys, int skip, int count){
        //TODO test
        int skipped = 0;

        List<V> out = new LinkedList<>();
        for (KeySeqMapNode<K, V> m : findAllNodes(keys)) {

            Iterator<V> values = m.findAll(keys);

            while (values.hasNext()) {
                if (skip > 0 && skipped < skip) {
                    skipped++;
                    values.next();
                    continue;
                }
                if (count > 0 && out.size() + 1 > count) {
                    return out;
                }
                out.add(values.next());
            }
        }
        return out;
    }

    protected ArrayList<Integer> mapAndUpdateMapping(Collection<K> keys){
        ArrayList<Integer> image = new ArrayList<>(keys.size());

        keys.forEach(k->{
            Integer mapped = keyMapping.get(k);
            if (mapped==null){
                keyMapping.put(k,nextKeyMapping);
                image.add(nextKeyMapping);
                nextKeyMapping++;
                return;
            }
            image.add(mapped);
        });

        image.sort(Comparator.naturalOrder());
        return image;
    }


    protected static Iterator<ArrayList<Integer>> splitImage(ArrayList<Integer> image, long nodeSize){
        return new Iterator<ArrayList<Integer>>() {
            int imageIter = 0;
            int listIter = (int) (image.get(imageIter)/nodeSize + 1);
            @Override
            public boolean hasNext() {
                return imageIter<image.size();
            }
            @Override
            public ArrayList<Integer> next() {
                ArrayList<Integer> nextList = new ArrayList<>();
                Integer key;
                while ((key=image.get(imageIter))<= nodeSize * listIter){
                    nextList.add(key);
                    imageIter++;
                    if (imageIter>=image.size()) {
                        nextList.add(listIter-1);
                        return nextList;
                    }
                }
                nextList.add(listIter-1);
                listIter = (int) (image.get(imageIter)/nodeSize + 1);
                return nextList;
            }
        };
    }

    protected static ArrayList<Integer> splitImageIndices(ArrayList<Integer> image, long nodeSize){
        int imageIter = 0;
        int listIter = (int) (image.get(imageIter)/nodeSize + 1);
        ArrayList<Integer> out = new ArrayList<>();

        while (imageIter<image.size()){

            while (image.get(imageIter) <= nodeSize * listIter){
                imageIter++;
                if (imageIter>=image.size()) {
                    out.add(listIter-1);
                    return out;
                }
            }
            out.add(listIter-1);
            listIter = (int) (image.get(imageIter)/nodeSize + 1);
        }
        return out;
    }

    @Override
    public int size() {
        return (int) size;
    }

    @Override
    public boolean isEmpty() {
        return size==0;
    }

    @Override
    public boolean containsKey(Object key) {
        if (key instanceof Collection){
            try {
                return findExact((Collection<K>) key)!=null;
            } catch (ClassCastException e){
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V get(Object key) {
        if (key instanceof Collection){
            try {
                return findExact((Collection<K>) key);
            } catch (ClassCastException e){
                return null;
            }
        }
        return null;
    }

    @Override
    public V put(Collection<K> key, V value) {

        Stack<ArrayList<Integer>> keySeq = new Stack<>();
        ArrayList<Integer> iterKey = mapAndUpdateMapping(key);
        long currentScale = nodeSize;

        while (currentScale<scale){
            iterKey = splitImageIndices(iterKey,nodeSize);
            keySeq.push(iterKey);
            currentScale*=nodeSize;
        }

        KeySeqMapNode<Integer,Object> iterNodes = nodes;
        while (!keySeq.isEmpty()){
            Object nextNode = iterNodes.computeIfAbsent(keySeq.pop(),k->new KeySeqMapNode<Integer,Object>());
            iterNodes = (KeySeqMapNode<Integer, Object>) nextNode;
        }

        KeySeqMapNode<K, V> exactNode = (KeySeqMapNode<K, V>)iterNodes.computeIfAbsent(iterKey,k->new KeySeqMapNode<K,V>());

        V prev = exactNode.put(key,value);
        if (prev==null)
            size++;
        return prev;
    }

    @Override
    public V remove(Object key) {
        if (key instanceof Collection){
            try {
                KeySeqMapNode<K,V> node = findExactNode((Collection<K>) key);
                if (node==null)
                    return null;
                V last = node.remove(key);
                if (last!=null)
                    size--;
                return last;
            } catch (ClassCastException e){
                return null;
            }
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends Collection<K>, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        nodes.clear();
        size=0;
    }

    @Override
    public Set<Collection<K>> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<V> values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Entry<Collection<K>, V>> entrySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "KeySeqMap{" +
                "nodes=" + nodes +
                '}';
    }
}
