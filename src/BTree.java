import java.util.Comparator;

public class BTree <T extends Comparable<T>, V> implements IBTree<T, V>
{
    private BTreeNode root;
    private int T;
    private int minKeys;
    private int minChildren;
    private int maxKeys;
    public BTree(int t)
    {
        this.T = t;
        this.minChildren = T;
        this.minKeys = T - 1;
        this.maxKeys = 2 * T - 1;
        this.root = new BTreeNode(true);
    }
    @Override
    public int getMinimumDegree() {
        return this.minChildren;
    }

    @Override
    public IBTreeNode<T, V> getRoot() {
        return this.root;
    }

    private void split(BTreeNode node, int idx)
    {
        BTreeNode a = node.getChild(idx);
        BTreeNode b = new BTreeNode(a.isLeaf());
        b.setParent(node);
        b.setNumOfKeys(minKeys);
        // move half of the keys from a to b
        for (int i = 0; i < minKeys; i++)
        {
            b.setKey(i, a.getKey(i + minKeys + 1));
            b.setValue(i, a.getKey(i + minKeys + 1),a.getValue(i + minKeys + 1));
            a.setValue(i + minKeys + 1, a.getKey(i + minKeys + 1), null);
            a.setKey(i + minKeys + 1, null);
//            a.removePair(a.getKey(i + minKeys + 1));
        }
        a.setNumOfKeys(minKeys);
        // if b is not leaf
        if (!b.isLeaf()){
            //we move children from a to b
            for (int i = 0; i < minChildren; i++)
            {
                b.setChild(i, a.getChild(i + minKeys + 1));
                a.setChild(i + T, null);
                b.getChild(i).setParent(b);
            }
        }
        // insert b as node child
        for (int i = node.getNumOfKeys(); i >= idx; i--)
        {
            node.setChild(i + 1, node.getChild(i));
        }
        node.setChild(idx + 1, b);
        // insert new key to parent node
        for (int i = node.getNumOfKeys() - 1; i >= idx; i--)
        {
            node.setKey(i + 1, node.getKey(i));
            node.setValue(i + 1, node.getKey(i), node.getValue(i +1));
        }
        node.setKey(idx, a.getKey(minKeys));
        node.setValue(idx, node.getKey(idx), a.getValue(minKeys));
        a.setValue(minKeys, a.getKey(minKeys), null);
        a.setKey(minKeys, null);
//        a.removePair(a.getKey(minKeys));
        node.setNumOfKeys(node.getNumOfKeys() + 1);
    }
    private void insertKey(BTreeNode node, T key, V value) {
        int idx = node.getNumOfKeys() - 1;
        if (node.isLeaf()) {
            node.addPair(key, value);
        }
        else
        {
            // look for the child
            while (idx >= 0 && node.getKey(idx).compareTo(key) > 0)
                idx--;
            idx++;
            // if child is full we split it
            if (node.getChild(idx).getNumOfKeys() == maxKeys)
            {
                {
                    split(node, idx);
                }
                if (node.getKey(idx).compareTo(key) < 0)
                    idx++;
            }
            // insert key to the right child
            insertKey(node.getChild(idx), key, value);
        }
    }
    @Override
    public void insert(T key, V value)
    {
        // if root is full we split it and make additional level in our tree
        if(searchNode(this.root, key) != null) {
            System.out.println("repeated");
            return;
        }
        if (this.root.getNumOfKeys() == maxKeys)
        {
            BTreeNode temp = new BTreeNode(false);
            root.setParent(temp);
            temp.setChild(0, root);
            root = temp;
            split(temp, 0);
            insertKey(temp, key, value);
        }
        else
        {
            insertKey(root, key, value);
        }

    }
    private BTreeNode searchNode(BTreeNode node, T key) {
        int i = 0;
        if (node == null)
            return node;
        for (i = 0; i < node.getNumOfKeys(); i++) {
            if (node.getKey(i).compareTo(key) > 0) {
                break;
            }
            if (node.getKey(i).compareTo(key) == 0) {
                return node;
            }
        }
        if (node.isLeaf()) {
            return null;
        }
        else
        {
            return searchNode(node.getChild(i), key);
        }
    }
    @Override
    public V search(T key) {
        BTreeNode temp = searchNode(this.root, key);
        if(temp != null)
        {
            for (int i = 0; i < temp.getNumOfKeys(); i++)
            {
                if (temp.getKey(i).compareTo(key) == 0) {
                    return (V) temp.getValue(i);
                }
            }
        }
        return null;
    }
    //Rotate the nodes to left
    private void leftRotate(BTreeNode node, BTreeNode rightSibling)
    {
        BTreeNode parent = node.getParent();
        node.addPair(parent.getKey(parent.getChildIdx(rightSibling) - 1), parent.getValue(parent.getChildIdx(rightSibling) - 1));
        parent.removePair(parent.getKey(parent.getChildIdx(rightSibling) - 1));
        parent.addPair(rightSibling.getKey(0), rightSibling.getValue(0));
        rightSibling.removePair(rightSibling.getKey(0));
        if(!node.isLeaf())
        {
            node.setChild(node.getNumOfKeys(), rightSibling.getChild(0));
            rightSibling.getChild(0).setParent(node);
            rightSibling.removeChild(0);
        }
    }
    //Rotate the nodes to right
    private void rightRotate(BTreeNode node, BTreeNode leftSibling)
    {
        BTreeNode parent = node.getParent();
        node.addPair(parent.getKey(parent.getChildIdx(leftSibling)), parent.getValue(parent.getChildIdx(leftSibling)));
        parent.removePair(parent.getKey(parent.getChildIdx(leftSibling)));
        parent.addPair(leftSibling.getKey(leftSibling.getNumOfKeys() - 1), leftSibling.getValue(leftSibling.getNumOfKeys() - 1));
        leftSibling.removePair(leftSibling.getKey(leftSibling.getNumOfKeys() -1));
        if(!node.isLeaf())
        {
            node.setChild(node.getNumOfKeys(), leftSibling.getChild(leftSibling.getNumOfKeys()));
            leftSibling.getChild(leftSibling.getNumOfKeys()).setParent(node);
            leftSibling.removeChild(leftSibling.getNumOfKeys());
        }
    }
    //Merge to given nodes
    private void merge(BTreeNode leftNode, BTreeNode rightNode)
    {
        BTreeNode parent = leftNode.getParent();
        parent.removeChild(parent.getChildIdx(rightNode));
        leftNode.addPair(parent.getKey(parent.getChildIdx(leftNode)), parent.getValue(parent.getChildIdx(leftNode)));
        parent.removePair(parent.getKey(parent.getChildIdx(leftNode)));
        //Check if the left node is leaf to move keys and values only
        if(leftNode.isLeaf())
        {
            for(int i = 0; i < rightNode.getNumOfKeys(); i++)
            {
                leftNode.addPair(rightNode.getKey(i), rightNode.getValue(i));
            }
        }
        else
        {
            //Move children, keys and values
            for(int i = 0; i < rightNode.getNumOfKeys(); i++)
            {
                leftNode.setChild(leftNode.getNumOfKeys(), rightNode.getChild(i));
                rightNode.getChild(i).setParent(leftNode);
                leftNode.addPair(rightNode.getKey(i), rightNode.getValue(i));
            }
            leftNode.setChild(leftNode.getNumOfKeys(), rightNode.getChild(rightNode.getNumOfKeys()));
            rightNode.getChild(rightNode.getNumOfKeys()).setParent(leftNode);
        }
        //Shrink the tree in case the root is empty
        if (parent == root && parent.getNumOfKeys() == 0)
        {
            leftNode.setParent(null);
            root = leftNode;
        }
        else if(parent != root && parent.getNumOfKeys() < minKeys)
        {
            balance(parent);
        }
    }
    private void balance(BTreeNode node) {
        BTreeNode rightSibling = node.getRightSibling();
        //Check the node right sibling
        if (rightSibling != null && rightSibling.getNumOfKeys() > minKeys) {
            leftRotate(node, rightSibling);
            return;
        }
        //Check the node left sibling
        BTreeNode leftSibling = node.getLeftSibling();
        if (leftSibling != null && leftSibling.getNumOfKeys() > minKeys) {
            rightRotate(node, leftSibling);
            return;
        }
        //Merge in case there is no sibling to borrow from
        if (leftSibling != null)
            merge(leftSibling, node);
        else if (rightSibling != null)
            merge(node, rightSibling);
        // if node doesn't have any siblings it means it is the root and we don't need to merge anything
    }
    //Remove leaf node
    private void removeLeaf(BTreeNode node, T key)
    {
        node.removePair(key);
        //Check if node is root and it has number of keys less than other node in order to skip balancing the tree
        if(node == this.root && node.getChildren().size() == 0 && node.getNumOfKeys() < minKeys)
            return;
        if (node.getNumOfKeys() < minKeys)
        {
            balance(node);
        }
    }
    //Remove internal node
    private void removeInternal(BTreeNode node, T key)
    {
        BTreeNode preLeaf = node.getPreLeaf(key);
        if (preLeaf.getNumOfKeys() > minKeys)
        {
            //Replace key with predecessor key
            node.setKey(node.getKeyIdx(key), preLeaf.getKey(preLeaf.getNumOfKeys() - 1));
            node.setValue(node.getKeyIdx(key), preLeaf.getKey(preLeaf.getNumOfKeys() - 1), preLeaf.getValue(preLeaf.getNumOfKeys() - 1));
            preLeaf.removePair(preLeaf.getKey(preLeaf.getNumOfKeys() - 1));
        }
        else
        {
            // Lock for successor after key remover if the predecessor leaf is deficient
            BTreeNode sucLeaf = node.getSucLeaf(key);
            node.setKey(node.getKeyIdx(key), sucLeaf.getKey(0));
            node.setValue(node.getKeyIdx(key), sucLeaf.getKey(0), sucLeaf.getValue(0));
            sucLeaf.removePair(sucLeaf.getKey(0));
            //Balance the tree if it is still deficient
            if (sucLeaf.getNumOfKeys() < minKeys)
                balance(sucLeaf);
        }
    }
    @Override
    public boolean delete(T key) {
        BTreeNode temp = searchNode(this.root, key);
        if (temp == null)
            return false;
        if (temp.isLeaf())
        {
            removeLeaf(temp, key);
        }
        else
        {
            removeInternal(temp, key);
        }
        return true;
    }
    public void display()
    {
        display(root);
    }
    // Display the tree
    private void display(BTreeNode x) {
        assert (x == null);
        for (int i = 0; i < x.getNumOfKeys(); i++)
        {
            System.out.print(x.getKey(i) + " ");
        }
        if (!x.isLeaf())
        {
            for (int i = 0; i < x.getNumOfKeys() + 1; i++)
            {
                display(x.getChild(i));
            }
        }
    }
}
