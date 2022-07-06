package ua.alexkras;

import java.util.*;
import java.util.stream.Collectors;

public class KeySeqMapNode<K extends Comparable<K>,V> implements Map<Collection<K>,V>{

    private Node head = new Node();

    private final TreeMap<K,Long> keyMapping;
    private final TreeMap<Long,K> inverseKeyMapping;
    private long nextKeyMapping = 1;

    private final TreeMap<Long,HashSet<Long>> connections = new TreeMap<>();

    private long size = 0;

    public KeySeqMapNode() {
        this.keyMapping = new TreeMap<>();
        this.inverseKeyMapping = new TreeMap<>();
    }

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
        TreeSet<Long> longersection =  new TreeSet<>(first);

        keysMapped.stream()
                .skip(1)
                .map(k->{
                    HashSet<Long> conn = connections.computeIfAbsent(k,x->new HashSet<>());
                    conn.addAll(keysMapped);
                    conn.remove(k);
                    return conn;
                })
                .forEach(longersection::retainAll);

        return longersection;
    }

    protected ArrayList<Long> mapAndUpdateMapping(Collection<K> keys){
        ArrayList<Long> image = new ArrayList<>(keys.size());

        keys.forEach(k->{
            Long mapped = keyMapping.get(k);
            if (mapped==null){
                keyMapping.put(k,nextKeyMapping);
                inverseKeyMapping.put(nextKeyMapping,k);
                image.add(nextKeyMapping);
                nextKeyMapping++;
                return;
            }
            image.add(mapped);
        });

        image.sort(Comparator.naturalOrder());
        return image;
    }



    private Node createOrFindNode(ArrayList<Long> keys){
        Node iter = head;
        long lastKey=1;
        for (Long k : keys){

            for (long kToSkip = lastKey; kToSkip<k; kToSkip++){
                if (iter.left==null){
                    ArrayList<K> index = new ArrayList<>(iter.key);

                    iter.left = new Node(index);
                }
                iter = iter.left;
            }
            if (iter.right==null){
                ArrayList<K> index = new ArrayList<>(iter.key);
                index.add(inverseKeyMapping.get(k));
                iter.right = new Node(index);
            }
            iter = iter.right;
            lastKey = k+1;
        }
        return iter;
    }

    protected V findExact(ArrayList<Long> image){
        Node node = createOrFindNode(image);
        return node.value;
    }

    public V findExact(Collection<K> keys){
        Node node = createOrFindNode(mapAndUpdateMapping(keys));
        return node.value;
    }

    protected Iterator<V> findAll(ArrayList<Long> keysMapped, long skip, long count){
        return new Iterator<V>() {
            private long skipped = 0;
            private long found = 0;
            private final HashSet<Long> keysMappedSet = new HashSet<>(keysMapped);
            private final Iterator<Long> conn;
            private final Long maxKey = keysMapped.get(keysMapped.size()-1);
            private final LinkedList<Node> iterNodes = new LinkedList<>();
            private final Queue<V> lastIterFound = new LinkedList<>();
            private final boolean exact;
            private boolean foundExact = false;
            {
                iterNodes.add(head);
                TreeSet<Long> connections = updateAndGetConnections(keysMapped);
                conn = connections.iterator();
                exact = connections.isEmpty();
            }
            long relatedKeyLast = 0;
            boolean lastIter = false;

            @Override
            public boolean hasNext() {
                if (lastIterFound.isEmpty())
                    nextIter();
                return (!exact && !lastIterFound.isEmpty() && (count<0 | found<count)) ||
                        (exact && !foundExact && !lastIterFound.isEmpty());
            }

            @Override
            public V next() {
                nextIter();
                if (lastIterFound.isEmpty()){
                    throw new NoSuchElementException();
                }
                if (exact){
                    foundExact=true;
                }
                return lastIterFound.remove();
            }

            private void nextIter() {
                if (!lastIterFound.isEmpty()){
                    return;
                }
                if (exact && !foundExact){
                    V val = findExact(keysMapped);
                    if (val!=null){
                        lastIterFound.add(val);
                    }
                    return;
                }
                while (conn.hasNext()) {
                    Long relatedKey = conn.next();

                    for (Long key = relatedKeyLast+1; key<relatedKey; key++){

                        long sizeInitial = iterNodes.size();
                        for (long i=0; i<sizeInitial; i++) {
                            Node ithNode = iterNodes.removeFirst();
                            if (key>maxKey && ithNode.value!=null){
                                if (skipped<skip){
                                    skipped++;
                                } else {
                                    found++;
                                    lastIterFound.add(ithNode.value);
                                }
                            }

                            if (keysMappedSet.contains(key)){
                                if (ithNode.right!=null) {
                                    iterNodes.addLast(ithNode.right);
                                }
                            } else if (ithNode.left!=null) {
                                iterNodes.addLast(ithNode.left);
                            }
                        }

                    }

                    long sizeInitial = iterNodes.size();
                    for (long i=0; i<sizeInitial; i++) {
                        Node ithNode = iterNodes.removeFirst();
                        if (relatedKey>=maxKey && ithNode.value!=null){
                            if (skipped<skip){
                                skipped++;
                            } else {
                                found++;
                                lastIterFound.add(ithNode.value);
                            }
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
                    long sizeInitial = iterNodes.size();
                    for (long i=0; i<sizeInitial; i++) {
                        Node ithNode = iterNodes.removeFirst();
                        if (keysMappedSet.contains(key)){
                            if (ithNode.right!=null) {
                                iterNodes.addLast(ithNode.right);
                            }
                        } else if (ithNode.left!=null) {
                            iterNodes.addLast(ithNode.left);
                        }
                    }

                }

                if (relatedKeyLast+1<=maxKey){
                    long sizeInitial = iterNodes.size();
                    for (long i=0; i<sizeInitial; i++) {
                        Node ithNode = iterNodes.removeFirst();
                        if (ithNode.right!=null)
                            iterNodes.addLast(ithNode.right);
                    }
                }

                if (lastIter)
                    return;
                lastIter=true;

                for (Node n : iterNodes) {
                    if (n.value!=null){
                        if (skipped<skip){
                            skipped++;
                        } else {
                            found++;
                            lastIterFound.add(n.value);
                        }
                    }
                }
            }
        };
    }

    public Iterator<V> findAll(Collection<K> keys){
        ArrayList<Long> keysMapped = mapAndUpdateMapping(keys);
        return findAll(keysMapped,0,-1);
    }

    public List<V> findAll(Collection<K> keys, int skip, int count){
        ArrayList<Long> keysMapped = mapAndUpdateMapping(keys);
        ArrayList<V> out = new ArrayList<>(count);
        findAll(keysMapped,skip,count).forEachRemaining(out::add);
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
                ArrayList<Long> image = mapAndUpdateMapping((Collection<K>) key);
                Node iter = head;

                long lastKey=1;
                for (Long k : image){

                    for (long kToSkip = lastKey; kToSkip<k; kToSkip++){
                        if (iter.left==null){
                            return false;
                        }
                        iter = iter.left;
                    }
                    if (iter.right==null){
                        return false;
                    }
                    iter = iter.right;
                    lastKey = k+1;
                }
                return iter.value!=null;
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
        ArrayList<Long> image = mapAndUpdateMapping(key);
        updateConnections(image);
        Node node = createOrFindNode(image);
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
                Node node = createOrFindNode(image);
                V prev = node.value;
                node.value=null;
                if (prev!=null)
                    size--;
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
        head=new Node();
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
        return "KeySeqMapNode{" +
                "head=" + head +
                '}';
    }

    protected class Node{
        private Node left;
        private Node right;
        public V value;
        final ArrayList<K> key;

        public Node(ArrayList<K> key) {
            this.key = key;
        }
        public Node() {
            key = new ArrayList<>();
        }

        private String nodeString(){
            String left = "null";
            if (this.left!=null){
                left = this.left.nodeString();
            }

            String right = "null";
            if (this.right!=null){
                right = this.right.nodeString();
            }

            return "Node("+key+key.stream().map(keyMapping::get).collect(Collectors.toList())+':'+value+"){" +
                    "\n left = "+left.replaceAll("\n","\n    ")+
                    "\n right = "+right.replaceAll("\n","\n    ")+
                    "\n}";
        }
        @Override
        public String toString() {
            return nodeString();
        }
    }

}
