package ua.alexkras;

import java.util.*;

public class KeySeqMap<K extends Comparable<K>,V> implements Map<Collection<K>,V> {

    private final TreeMap<K,Integer> keyMapping = new TreeMap<>();
    protected final KeySeqMapNode<Integer,KeySeqMapNode<K,V>> nodes = new KeySeqMapNode<>();
    private int nextKeyMapping = 1;

    private long size = 0L;
    private final int nodeSize;

    public KeySeqMap(){
        nodeSize = 1000;
    }

    public KeySeqMap(int nodeSize){
        this.nodeSize = nodeSize;
    }

    public V findExact(Collection<K> keys){
        ArrayList<Integer> key2 = mapAndUpdateMapping(keys);
        ArrayList<Integer> key1 = splitImageIndices(key2);
        return nodes.findExact(key1).findExact(key2);
    }

    public List<V> findAll(Collection<K> keys){
        return findAll(keys,0,-1);
    }

    public List<V> findAll(Collection<K> keys, int skip, int count){

        int skipped = 0;
        ArrayList<Integer> key1 = splitImageIndices(mapAndUpdateMapping(keys));

        List<V> out = new LinkedList<>();
        Iterator<KeySeqMapNode<K,V>> nodeIterator = nodes.findAll(key1);
        while (nodeIterator.hasNext()){

            KeySeqMapNode<K,V> m = nodeIterator.next();

            Iterator<V> values = m.findAll(keys);

            while (values.hasNext()){
                if (skip>0 && skipped<skip){
                    skipped++;
                    values.next();
                    continue;
                }
                if (count>0 && out.size()+1>count){
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


    protected Iterator<ArrayList<Integer>> splitImage(ArrayList<Integer> image){
        return new Iterator<ArrayList<Integer>>() {
            int imageIter = 0;
            Integer listIter = (image.get(imageIter)/nodeSize) + 1;
            @Override
            public boolean hasNext() {
                return imageIter<image.size();
            }
            @Override
            public ArrayList<Integer> next() {
                ArrayList<Integer> nextList = new ArrayList<>();
                Integer key;
                while ((key=image.get(imageIter))<= (Integer) nodeSize * listIter){
                    nextList.add(key);
                    imageIter++;
                    if (imageIter>=image.size()) {
                        nextList.add(listIter-1);
                        return nextList;
                    }
                }
                nextList.add(listIter-1);
                listIter = (image.get(imageIter)/nodeSize) + 1;
                return nextList;
            }
        };
    }

    protected ArrayList<Integer> splitImageIndices(ArrayList<Integer> image){
        int imageIter = 0;
        int listIter = image.get(imageIter)/nodeSize + 1;
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
            listIter = image.get(imageIter)/nodeSize + 1;
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
                ArrayList<Integer> key1 = splitImageIndices(mapAndUpdateMapping((Collection<K>) key));
                return nodes.containsKey(key1) && nodes.findExact(key1).containsKey(key);
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
                ArrayList<Integer> key2 = mapAndUpdateMapping((Collection<K>) key);
                ArrayList<Integer> key1 = splitImageIndices(key2);
                if (!nodes.containsKey(key1))
                    return null;
                return nodes.findExact(key1).findExact(key2);
            } catch (ClassCastException e){
                return null;
            }
        }
        return null;
    }

    @Override
    public V put(Collection<K> key, V value) {
        ArrayList<Integer> key1 = splitImageIndices(mapAndUpdateMapping(key));
        KeySeqMapNode<K,V> node = nodes.computeIfAbsent(key1,v->new KeySeqMapNode<>());
        V last = node.put(key,value);
        if (last==null)
            size++;
        return last;
    }

    @Override
    public V remove(Object key) {
        if (key instanceof Collection){
            try {
                ArrayList<Integer> key1 = splitImageIndices(mapAndUpdateMapping((Collection<K>) key));
                if (!nodes.containsKey(key1))
                    return null;

                KeySeqMapNode<K,V> node = nodes.findExact(key1);
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
