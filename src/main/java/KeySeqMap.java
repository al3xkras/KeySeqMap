import java.util.*;
import java.util.stream.Collectors;

public class KeySeqMap<K extends Comparable<K>,V> {

    protected final Node<V> head = new Node<>();

    private final TreeMap<K,Long> keyMapping = new TreeMap<>();
    private final TreeMap<Long,K> inverseKeyMapping = new TreeMap<>();
    private long nextKeyMapping = 1L;

    private final TreeMap<Long,HashSet<Long>> connections = new TreeMap<>();

    private void updateConnections(ArrayList<Long> keysMapped){
        keysMapped.forEach(k->{
            connections.get(k);
            HashSet<Long> conn = connections.computeIfAbsent(k,x->new HashSet<>());
            conn.addAll(keysMapped);
            conn.remove(k);
        });
    }

    protected TreeSet<Long> updateAndGetConnections(ArrayList<Long> keysMapped){
        //TODO test
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

        System.out.println(intersection);
        return intersection;
    }

    protected ArrayList<Long> mapAndUpdateMapping(Collection<K> keys){
        //TODO test
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

    private Node<V> createOrFindNode(ArrayList<Long> keys){
        //TODO test
        Node<V> iter = head;
        long lastKey=1L;
        for (long k : keys){

            for (long kToSkip = lastKey; kToSkip<k; kToSkip++){
                if (iter.left==null){
                    ArrayList<Long> index = new ArrayList<>(iter.key);

                    iter.left = new Node<>(iter,index);
                }
                iter = iter.left;
            }
            if (iter.right==null){
                ArrayList<Long> index = new ArrayList<>(iter.key);
                index.add(k);
                iter.right = new Node<>(iter, index);
            }
            iter = iter.right;
            lastKey = k+1;
        }
        return iter;
    }

    public void add(List<K> keys, V value){
        //TODO test
        ArrayList<Long> image = mapAndUpdateMapping(keys);
        updateConnections(image);
        Node<V> node = createOrFindNode(image);
        node.value=value;
    }

    public V findExact(Collection<K> keys){
        //TODO test
        Node<V> node = createOrFindNode(mapAndUpdateMapping(keys));
        return node.value;
    }


    protected LinkedList<V> findAll(List<K> keys){

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
                        System.out.println(maxKey);
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
                    System.out.println(maxKey);
                    out.add(ithNode.value);
                }
                if (ithNode.left!=null && !keysMappedSet.contains(relatedKey)) {
                    //System.out.println("left: "+ithNode.left.key);
                    iterNodes.addLast(ithNode.left);
                }
                if (ithNode.right!=null) {
                    //System.out.println("right: "+ithNode.right.key);
                    iterNodes.addLast(ithNode.right);
                }
            }

            //System.out.println(currentKey);
            //System.out.println(relatedKey);
            System.out.println(iterNodes.stream().map(x->x.key+" = "+x.value).collect(Collectors.toList()));

            relatedKeyLast = relatedKey;
        }


        for (long key = relatedKeyLast+1; key<maxKey; key++){
            System.out.println(iterNodes.stream().map(x->x.key+" = "+x.value).collect(Collectors.toList()));
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
            System.out.println(iterNodes.stream().map(x->x.key+" = "+x.value).collect(Collectors.toList()));
        }

        for (Node<V> n : iterNodes){
            if (n.value!=null){
                out.add(n.value);
            }
        }

        return out;
    }

    protected static class Node<V>{
        private Node<V> parent;
        private Node<V> left;
        private Node<V> right;
        public V value;
        final ArrayList<Long> key;

        public Node(Node<V> parent, ArrayList<Long> key) {
            this.parent = parent;
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
