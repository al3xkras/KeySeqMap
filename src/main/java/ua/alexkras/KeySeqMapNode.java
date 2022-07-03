package ua.alexkras;

import java.util.*;
import java.util.stream.Collectors;

public class KeySeqMapNode<K extends Comparable<K>,V> implements Map<Collection<K>,V>{

    private Node head = new Node();

    private final TreeMap<K,Integer> keyMapping;
    private final TreeMap<Integer,K> inverseKeyMapping;
    private int nextKeyMapping = 1;

    private final TreeMap<Integer,HashSet<Integer>> connections = new TreeMap<>();

    private int size = 0;

    public KeySeqMapNode() {
        this.keyMapping = new TreeMap<>();
        this.inverseKeyMapping = new TreeMap<>();
    }

    private void updateConnections(ArrayList<Integer> keysMapped){
        keysMapped.forEach(k->{
            connections.get(k);
            HashSet<Integer> conn = connections.computeIfAbsent(k,x->new HashSet<>());
            conn.addAll(keysMapped);
            conn.remove(k);
        });
    }

    protected TreeSet<Integer> updateAndGetConnections(ArrayList<Integer> keysMapped){
        HashSet<Integer> first = connections.computeIfAbsent(keysMapped.get(0),k->new HashSet<>());
        first.addAll(keysMapped);
        first.remove(keysMapped.get(0));
        TreeSet<Integer> intersection =  new TreeSet<>(first);

        keysMapped.stream()
                .skip(1)
                .map(k->{
                    HashSet<Integer> conn = connections.computeIfAbsent(k,x->new HashSet<>());
                    conn.addAll(keysMapped);
                    conn.remove(k);
                    return conn;
                })
                .forEach(intersection::retainAll);

        return intersection;
    }

    protected ArrayList<Integer> mapAndUpdateMapping(Collection<K> keys){
        ArrayList<Integer> image = new ArrayList<>(keys.size());

        keys.forEach(k->{
            Integer mapped = keyMapping.get(k);
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



    private Node createOrFindNode(ArrayList<Integer> keys){
        Node iter = head;
        int lastKey=1;
        for (Integer k : keys){

            for (int kToSkip = lastKey; kToSkip<k; kToSkip++){
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

    protected V findExact(ArrayList<Integer> image){
        Node node = createOrFindNode(image);
        return node.value;
    }

    public V findExact(Collection<K> keys){
        Node node = createOrFindNode(mapAndUpdateMapping(keys));
        return node.value;
    }

    protected Iterator<V> findAll(ArrayList<Integer> keysMapped, int skip, int count){
        return new Iterator<V>() {
            private int skipped = 0;
            private int found = 0;
            private final HashSet<Integer> keysMappedSet = new HashSet<>(keysMapped);
            private final Iterator<Integer> conn;
            private final Integer maxKey = keysMapped.get(keysMapped.size()-1);
            private final LinkedList<Node> iterNodes = new LinkedList<>();
            private final Queue<V> lastIterFound = new LinkedList<>();
            private final boolean exact;
            private boolean foundExact = false;
            {
                iterNodes.add(head);
                TreeSet<Integer> connections = updateAndGetConnections(keysMapped);
                conn = connections.iterator();
                exact = connections.isEmpty();
            }
            int relatedKeyLast = 0;
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
                    Integer relatedKey = conn.next();

                    for (Integer key = relatedKeyLast+1; key<relatedKey; key++){

                        int sizeInitial = iterNodes.size();
                        for (int i=0; i<sizeInitial; i++) {
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

                    int sizeInitial = iterNodes.size();
                    for (int i=0; i<sizeInitial; i++) {
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

                for (int key = relatedKeyLast+1; key<maxKey; key++){
                    int sizeInitial = iterNodes.size();
                    for (int i=0; i<sizeInitial; i++) {
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
                    int sizeInitial = iterNodes.size();
                    for (int i=0; i<sizeInitial; i++) {
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
        ArrayList<Integer> keysMapped = mapAndUpdateMapping(keys);
        return findAll(keysMapped,0,-1);
    }

    public List<V> findAll(Collection<K> keys, int skip, int count){
        ArrayList<Integer> keysMapped = mapAndUpdateMapping(keys);
        ArrayList<V> out = new ArrayList<>(count);
        findAll(keysMapped,skip,count).forEachRemaining(out::add);
        return out;
    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size==0;
    }

    @Override
    public boolean containsKey(Object key) {
        if (key instanceof Collection){
            try {
                ArrayList<Integer> image = mapAndUpdateMapping((Collection<K>) key);
                Node iter = head;

                int lastKey=1;
                for (Integer k : image){

                    for (int kToSkip = lastKey; kToSkip<k; kToSkip++){
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
        ArrayList<Integer> image = mapAndUpdateMapping(key);
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
                ArrayList<Integer> image = mapAndUpdateMapping(keys);
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
