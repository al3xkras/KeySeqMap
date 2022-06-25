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

    protected HashSet<Long> updateAndGetConnections(ArrayList<Long> keysMapped){
        //TODO test
        HashSet<Long> first = connections.computeIfAbsent(keysMapped.get(0),k->new HashSet<>());
        first.addAll(keysMapped);
        first.remove(keysMapped.get(0));
        HashSet<Long> intersection =  new HashSet<>(first);

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
                    iter.left = new Node<>(iter,kToSkip+1);
                }
                iter = iter.left;
            }
            if (iter.right==null){
                iter.right = new Node<>(iter, k+1);
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
        HashSet<Long> conn = updateAndGetConnections(keysMapped);
        Long maxKey = keysMapped.get(keysMapped.size()-1);

        LinkedList<Node<V>> iterNodes = new LinkedList<>();
        iterNodes.add(head);

        int keysIter = 0;
        long relatedKeyLast = 1L;
        for (long relatedKey : conn) {

            long currentKey = keysMapped.get(keysIter);

            for (long key = relatedKeyLast+1; key<relatedKey; key++){

                int sizeInitial = iterNodes.size();

                for (int i=0; i<sizeInitial; i++) {
                    Node<V> ithNode = iterNodes.removeFirst();
                    if (keysIter>=keysMapped.size()-1 && ithNode.value!=null){
                        out.add(ithNode.value);
                    }

                    if (key==currentKey){
                        if (ithNode.right!=null)
                            iterNodes.addLast(ithNode.right);
                    } else if (ithNode.left!=null) {
                        iterNodes.addLast(ithNode.left);
                    }
                }

                if (key==currentKey && keysIter<keysMapped.size()-1)
                    keysIter++;

            }



            int sizeInitial = iterNodes.size();
            for (int i=0; i<sizeInitial; i++) {
                Node<V> ithNode = iterNodes.removeFirst();
                if (keysIter>=keysMapped.size()-1 && ithNode.value!=null){
                    out.add(ithNode.value);
                }
                if (ithNode.left!=null) {
                    //System.out.println("left: "+ithNode.left.key);
                    iterNodes.addLast(ithNode.left);
                }
                if (ithNode.right!=null) {
                    //System.out.println("right: "+ithNode.right.key);
                    iterNodes.addLast(ithNode.right);
                }
            }



            if (relatedKey==currentKey && keysIter<keysMapped.size()-1)
                keysIter++;

            //System.out.println(currentKey);
            //System.out.println(relatedKey);
            //System.out.println(iterNodes.stream().map(x->x.key+" = "+x.value).collect(Collectors.toList()));

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
        final Long key;

        public Node(Node<V> parent, Long key) {
            this.parent = parent;
            this.key = key;
        }
        public Node() {
            key = 1L;
        }

        @Override
        public String toString() {
            return "Node("+key+':'+value+"){\n left = "+left+"\n right = "+right+"\n}";
        }
    }

}
