import java.util.*;

public class KeySeqMap<K extends Comparable<K>,V> {

    private final Node<V> zero = new Node<>();
    private final Node<V> head = new Node<>(zero);

    private final TreeMap<K,Long> keyMapping = new TreeMap<>();
    private final TreeMap<Long,K> inverseKeyMapping = new TreeMap<>();
    private long nextKeyMapping = 1L;

    private ArrayList<Long> mapAndUpdateMapping(Collection<K> keys){
        //TODO test
        final LinkedList<K> notMapped = new LinkedList<>();
        ArrayList<Long> image = new ArrayList<>(keys.size());

        keys.forEach(k->{
            Long mapped = keyMapping.get(k);
            if (mapped==null){
                notMapped.add(k);
                return;
            }
            image.add(mapped);
        });

        notMapped.forEach(n->{
            keyMapping.put(n,nextKeyMapping);
            inverseKeyMapping.put(nextKeyMapping,n);
            image.add(nextKeyMapping);
            nextKeyMapping++;
        });

        image.sort(Comparator.naturalOrder());
        return image;
    }

    private Node<V> createOrFindNode(ArrayList<Long> keys){
        //TODO test
        Node<V> iter = head;
        long currentKey=1L;
        for (long k : keys){

            for (long kToSkip = currentKey; kToSkip<k; kToSkip++){
                if (iter.left==null){
                    iter.left = new Node<>(iter);
                }
                iter = iter.left;
            }
            if (iter.right==null){
                iter.right = new Node<>(iter);
            }
            iter = iter.right;
            currentKey = k;
        }
        return iter;
    }

    public void add(List<K> keys, V value){
        //TODO test
        ArrayList<Long> image = mapAndUpdateMapping(keys);
        Node<V> node = createOrFindNode(image);
        node.value=value;
    }

    public V findExact(Collection<K> keys){
        //TODO test
        Node<V> node = createOrFindNode(mapAndUpdateMapping(keys));
        return node.value;
    }


    private LinkedList<Node<V>> findAllRecursive(ArrayList<Long> keys, int start, int count){

        long max = keys.get(keys.size()-1);
        Set<Long> keySet = new HashSet<>(keys);

        int skipped = 0;
        int found = 0;

        Node<V> iter = createOrFindNode(keys);
        long rightMostKey = 1L;
        Node<V> rightMost = iter;
        while (rightMost.right!=null && rightMostKey<=max) {
            rightMost=rightMost.right;
            rightMostKey++;
        }

        LinkedList<Node<V>> out = new LinkedList<>();

        boolean descend = false;

        long currentKey = 1L;
        while (found<count){

            if (iter.equals(zero)) {
                descend=true;
                continue;
            }

            if (!descend){
                if (iter.parent.left==iter && iter.parent.right!=null){
                    iter = iter.parent.right;
                    descend=true;
                }
                iter=iter.parent;
                currentKey--;
                continue;
            }

            if (keySet.contains(currentKey)){
                if (iter.right!=null){
                    iter = iter.right;
                    if (currentKey==max){
                        if (skipped<start){
                            skipped++;
                        } else {
                            out.add(iter);
                            found++;
                        }
                        descend = false;
                        continue;
                    }
                    currentKey++;
                } else {
                    descend = false;
                }

            } else {
                if (iter.left!=null){
                    iter = iter.left;
                    if (currentKey==max){
                        if (skipped<start){
                            skipped++;
                        } else {
                            out.add(iter);
                            found++;
                        }
                        descend = false;
                        continue;
                    }
                    currentKey++;
                } else {
                    if (iter==rightMost){
                        break;
                    }
                    descend = false;
                }
            }
        }

        return out;
    }

    public Iterator<V> findALl(List<K> keys){
        ArrayList<Long> image = mapAndUpdateMapping(keys);


        //TODO implement
        return null;
    }

    protected static class Node<V>{
        private Node<V> parent;
        private Node<V> left;
        private Node<V> right;
        private V value;

        public Node(Node<V> parent) {
            this.parent = parent;
        }
        public Node() {
        }
    }

    protected static class KeySeq<K extends Comparable<K>>
            implements Comparable<KeySeq<K>>{

        protected ArrayList<K> keys;

        public KeySeq(Collection<K> keys) {
            this.keys = new ArrayList<>(keys);
            this.keys.sort(K::compareTo);
        }

        @Override
        public int compareTo(KeySeq<K> o) {
            int min = Math.min(keys.size(),o.keys.size());
            int max = Math.max(keys.size(),o.keys.size());

            for (int i=0; i<min; i++){
                int c = keys.get(i).compareTo(o.keys.get(i));
                if (c!=0){
                    return -c;
                }
            }
            return max==keys.size()
                    ?min==max
                    ? 0
                    : 1 : -1;
        }

        @Override
        public String toString() {
            return keys.toString();
        }
    }


}
