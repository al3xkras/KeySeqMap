package ua.alexkras;

import java.util.*;

public class KeySeqMap<K extends Comparable<K>,V> implements Map<Collection<K>,V>{

    private Node<V> head = new Node<>();

    private final TreeMap<K,Long> keyMapping = new TreeMap<>();
    private long nextKeyMapping = 1L;

    private final TreeMap<Long,HashSet<Long>> connections = new TreeMap<>();

    private int size = 0;

    private void updateConnections(ArrayList<Long> keysMapped){
        keysMapped.forEach(k->{
            connections.get(k);
            HashSet<Long> conn = connections.computeIfAbsent(k,x->new HashSet<>());
            conn.addAll(keysMapped);
            conn.remove(k);
        });
    }

    protected TreeSet<Long> updateAndGetConnections(ArrayList<Long> keysMapped){
        HashSet<Long> first = connections.computeIfAbsent(keysMapped.get(0),k->new HashSet<>());
        first.addAll(keysMapped);
        first.remove(keysMapped.get(0));
        TreeSet<Long> intersection =  new TreeSet<>(first);

        keysMapped.stream()
                .skip(1)
                .map(k->{
                    HashSet<Long> conn = connections.computeIfAbsent(k,x->new HashSet<>());
                    conn.addAll(keysMapped);
                    conn.remove(k);
                    return conn;
                })
                .forEach(intersection::retainAll);

        //System.out.println(intersection);
        return intersection;
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

    private Node<V> createOrFindNode(ArrayList<Long> keys){
        Node<V> iter = head;
        long lastKey=1L;
        for (long k : keys){

            for (long kToSkip = lastKey; kToSkip<k; kToSkip++){
                if (iter.left==null){
                    ArrayList<Long> index = new ArrayList<>(iter.key);

                    iter.left = new Node<>(index);
                }
                iter = iter.left;
            }
            if (iter.right==null){
                ArrayList<Long> index = new ArrayList<>(iter.key);
                index.add(k);
                iter.right = new Node<>(index);
            }
            iter = iter.right;
            lastKey = k+1;
        }
        return iter;
    }

    public V findExact(Collection<K> keys){
        Node<V> node = createOrFindNode(mapAndUpdateMapping(keys));
        return node.value;
    }

    public List<V> findAll(List<K> keys){
        LinkedList<V> out = new LinkedList<>();
        ArrayList<Long> keysMapped = mapAndUpdateMapping(keys);
        HashSet<Long> keysMappedSet = new HashSet<>(keysMapped);
        TreeSet<Long> conn = updateAndGetConnections(keysMapped);
        Long maxKey = keysMapped.get(keysMapped.size()-1);
        LinkedList<Node<V>> iterNodes = new LinkedList<>();

        iterNodes.add(head);

        long relatedKeyLast = 0L;
        for (long relatedKey : conn) {

            for (long key = relatedKeyLast+1; key<relatedKey; key++){

                int sizeInitial = iterNodes.size();

                for (int i=0; i<sizeInitial; i++) {
                    Node<V> ithNode = iterNodes.removeFirst();
                    if (key>maxKey && ithNode.value!=null){
                        out.add(ithNode.value);
                    }

                    if (keysMappedSet.contains(key)){
                        if (ithNode.right!=null)
                            iterNodes.addLast(ithNode.right);
                    } else if (ithNode.left!=null) {
                        iterNodes.addLast(ithNode.left);
                    }
                }
            }

            int sizeInitial = iterNodes.size();
            for (int i=0; i<sizeInitial; i++) {
                Node<V> ithNode = iterNodes.removeFirst();
                if (relatedKey>=maxKey && ithNode.value!=null){
                    out.add(ithNode.value);
                }
                if (ithNode.left!=null && !keysMappedSet.contains(relatedKey)) {
                    iterNodes.addLast(ithNode.left);
                }
                if (ithNode.right!=null) {
                    iterNodes.addLast(ithNode.right);
                }
            }
            relatedKeyLast = relatedKey;
        }

        for (long key = relatedKeyLast+1; key<maxKey; key++){
            int sizeInitial = iterNodes.size();
            for (int i=0; i<sizeInitial; i++) {
                Node<V> ithNode = iterNodes.removeFirst();
                if (ithNode.left!=null)
                    iterNodes.addLast(ithNode.left);

            }
        }

        if (relatedKeyLast+1<maxKey){
            int sizeInitial = iterNodes.size();
            for (int i=0; i<sizeInitial; i++) {
                Node<V> ithNode = iterNodes.removeFirst();
                if (ithNode.right!=null)
                    iterNodes.addLast(ithNode.right);
            }
        }

        for (Node<V> n : iterNodes){
            if (n.value!=null){
                out.add(n.value);
            }
        }
        return out;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
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
        ArrayList<Long> image = mapAndUpdateMapping(key);
        updateConnections(image);
        Node<V> node = createOrFindNode(image);
        V prev = node.value;
        node.value=value;
        if (prev==null)
            size++;
        return prev;
    }


    @Override
    public V remove(Object key) {
        if (key instanceof Collection){
            try {
                Collection<K> keys = (Collection<K>) key;
                ArrayList<Long> image = mapAndUpdateMapping(keys);
                Node<V> node = createOrFindNode(image);
                V prev = node.value;
                node.value=null;
                return prev;
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
        head=new Node<>();
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

    protected static class Node<V>{
        private Node<V> left;
        private Node<V> right;
        public V value;
        final ArrayList<Long> key;

        public Node(ArrayList<Long> key) {
            this.key = key;
        }
        public Node() {
            key = new ArrayList<>();
        }

        @Override
        public String toString() {
            return "Node("+key+':'+value+"){\n left = "+left+"\n right = "+right+"\n}";
        }
    }

}
