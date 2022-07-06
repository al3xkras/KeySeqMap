package ua.alexkras;

import java.util.*;

public class KeySeqMap<K extends Comparable<K>,V> implements Map<Collection<K>,V> {

    private final TreeMap<K,Long> keyMapping = new TreeMap<>();
    private final KeySeqMapNode<Long,Object> nodes = new KeySeqMapNode<>();
    private long nextKeyMapping = 1;

    private long size = 0L;
    private final long scale;
    private final long nodeSize;

    public KeySeqMap(){
        nodeSize = 100;
        scale = (long) 1e10;
    }

    public KeySeqMap(long scale, long nodeSize){
        if (nodeSize<=0 || scale<=0 || nodeSize>=scale)
            throw new IllegalArgumentException("scale = "+scale+", nodeSize = "+nodeSize);
        this.nodeSize = nodeSize;
        this.scale=scale;
    }

    public KeySeqMap(long nodeSize){
        this.nodeSize = nodeSize;
        this.scale= (long) 1e10;
    }

    public V findExact(Collection<K> keys){
        KeySeqMapNode<K,V> exactNode = findExactNode(keys);
        return exactNode==null?null:exactNode.findExact(keys);
    }

    protected KeySeqMapNode<K,V> findExactNode(Collection<K> key){
        Stack<ArrayList<Long>> keySeq = new Stack<>();
        ArrayList<Long> iterKey = mapAndUpdateMapping(key);
        long currentScale = nodeSize;

        while (currentScale<scale){
            iterKey = splitImageIndices(iterKey,nodeSize);
            keySeq.push(iterKey);
            currentScale*=nodeSize;
        }

        KeySeqMapNode<Long,Object> iterNodes = nodes;
        while (!keySeq.isEmpty()){
            Object nextNode = iterNodes.findExact(keySeq.pop());
            if (nextNode==null)
                return null;
            iterNodes = (KeySeqMapNode<Long, Object>) nextNode;
        }

        return (KeySeqMapNode<K, V>)iterNodes.findExact(iterKey);
    }

    protected LinkedList<KeySeqMapNode<K,V>> findAllNodes(Collection<K> key){
        Stack<ArrayList<Long>> keySeq = new Stack<>();
        ArrayList<Long> iterKey = mapAndUpdateMapping(key);
        long currentScale = nodeSize;

        while (currentScale<scale){
            iterKey = splitImageIndices(iterKey,nodeSize);
            keySeq.push(iterKey);
            currentScale*=nodeSize;
        }

        LinkedList<KeySeqMapNode<Long,Object>> iter = new LinkedList<>();
        iter.add(nodes);

        while (!keySeq.isEmpty()){
            long sizeInitial = iter.size();
            if (sizeInitial==0)
                return new LinkedList<>();
            ArrayList<Long> keyIter = keySeq.pop();
            for (long i=0; i<sizeInitial; i++){
                KeySeqMapNode<Long,Object> first = iter.removeFirst();
                first.findAll(keyIter).forEachRemaining(o->iter.addLast((KeySeqMapNode<Long, Object>) o));
            }
        }

        LinkedList<KeySeqMapNode<K,V>> out = new LinkedList<>();

        long sizeInitial = iter.size();
        for (long i=0; i<sizeInitial; i++){
            KeySeqMapNode<Long,Object> first = iter.removeFirst();
            first.findAll(iterKey).forEachRemaining(o->out.addLast((KeySeqMapNode<K, V>) o));
        }
        return out;
    }

    public List<V> findAll(Collection<K> keys){
        return findAll(keys,0,-1);
    }

    public List<V> findAll(Collection<K> keys, long skip, long count){
        long skipped = 0;

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

    protected ArrayList<Long> mapAndUpdateMapping(Collection<K> keys){
        ArrayList<Long> image = new ArrayList<>(keys.size());

        keys.forEach(k->{
            Long mapped = keyMapping.get(k);
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

    protected static Iterator<ArrayList<Long>> splitImage(ArrayList<Long> image, long scale){
        return new Iterator<ArrayList<Long>>() {
            int imageIter = 0;
            long listIter = (long) (image.get(imageIter)/ scale + 1);
            @Override
            public boolean hasNext() {
                return imageIter<image.size();
            }
            @Override
            public ArrayList<Long> next() {
                ArrayList<Long> nextList = new ArrayList<>();
                Long key;
                while ((key=image.get(imageIter))<= scale * listIter){
                    nextList.add(key);
                    imageIter++;
                    if (imageIter>=image.size()) {
                        nextList.add(listIter-1);
                        return nextList;
                    }
                }
                nextList.add(listIter-1);
                listIter = (long) (image.get(imageIter)/ scale + 1);
                return nextList;
            }
        };
    }

    protected static ArrayList<Long> splitImageIndices(ArrayList<Long> image, long scale){
        int imageIter = 0;
        long listIter = image.get(imageIter)/ scale + 1;
        ArrayList<Long> out = new ArrayList<>();

        while (imageIter<image.size()){

            while (image.get(imageIter) <= scale * listIter){
                imageIter++;
                if (imageIter>=image.size()) {
                    out.add(listIter-1);
                    return out;
                }
            }
            out.add(listIter-1);
            listIter = image.get(imageIter)/ scale + 1;
        }
        return out;
    }

    @Override
    public int size() {
        if (size>Integer.MAX_VALUE)
            return Integer.MAX_VALUE;
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

        Stack<ArrayList<Long>> keySeq = new Stack<>();
        ArrayList<Long> iterKey = mapAndUpdateMapping(key);
        long currentScale = nodeSize;

        while (currentScale<scale){
            iterKey = splitImageIndices(iterKey,nodeSize);
            keySeq.push(iterKey);
            currentScale*=nodeSize;
        }

        KeySeqMapNode<Long,Object> iterNodes = nodes;
        while (!keySeq.isEmpty()){
            Object nextNode = iterNodes.computeIfAbsent(keySeq.pop(),k->new KeySeqMapNode<Long,Object>());
            iterNodes = (KeySeqMapNode<Long, Object>) nextNode;
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
