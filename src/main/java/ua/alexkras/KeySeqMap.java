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

    private KeySeqMapNode<K,V> findExactNode(Collection<K> keys){
        ArrayList<Integer> keyInitial = mapAndUpdateMapping(keys);
        KeySeqMapNode<Integer,Object> iterNodes = nodes;

        long currentScale = scale;
        while (currentScale>nodeSize){
            Object nextNode = iterNodes.findExact(splitImageIndices(keyInitial,currentScale));
            if (nextNode==null)
                return null;
            iterNodes = (KeySeqMapNode<Integer, Object>) nextNode;
            currentScale/=2;
        }
       return (KeySeqMapNode<K, V>) iterNodes.findExact(splitImageIndices(keyInitial,currentScale));
    }

    private LinkedList<KeySeqMapNode<K,V>> findAllNodes(Collection<K> keys){
        //TODO test
        ArrayList<Integer> keyInitial = mapAndUpdateMapping(keys);

        LinkedList<KeySeqMapNode<K,Object>> iter = new LinkedList<>();

        long currentScale = scale;
        nodes.findAll(splitImageIndices(keyInitial,currentScale),0,-1)
             .forEachRemaining(o->iter.add((KeySeqMapNode<K, Object>) o));
        currentScale/=2;

        while (currentScale>nodeSize){
            int sizeInitial = iter.size();
            for (int i=0;i<sizeInitial;i++){
                iter.removeFirst().findAll(splitImageIndices(keyInitial,currentScale),0,-1)
                        .forEachRemaining(o->iter.add((KeySeqMapNode<K, Object>) o));
            }
            currentScale/=2;
        }

        LinkedList<KeySeqMapNode<K,V>> out = new LinkedList<>();
        long finalCurrentScale = currentScale;
        iter.forEach(
                o->o.findAll(splitImageIndices(keyInitial, finalCurrentScale),0,-1)
                        .forEachRemaining(o1->out.add((KeySeqMapNode<K, V>) o1))
        );
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
        ArrayList<Integer> keyInitial = mapAndUpdateMapping(key);
        KeySeqMapNode<Integer,Object> iterNodes = nodes;

        long currentScale = scale;
        while (currentScale>nodeSize){
            Object nextNode = iterNodes.computeIfAbsent(splitImageIndices(keyInitial,currentScale),k->new KeySeqMapNode<Integer,Object>());
            iterNodes = (KeySeqMapNode<Integer, Object>) nextNode;
            currentScale/=2;
        }
        KeySeqMapNode<K, V> exactNode = (KeySeqMapNode<K, V>) iterNodes.computeIfAbsent(splitImageIndices(keyInitial,currentScale),k->new KeySeqMapNode<K,V>());

        V last = exactNode.put(key,value);
        if (last==null)
            size++;
        return last;
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
