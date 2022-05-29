import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static java.lang.System.exit;

public class BTreeNode <T extends Comparable<T>, V> implements IBTreeNode<T, V>{
    private int numOfKeys;
    private ArrayList<T> keys;
    private ArrayList<V> values;
    private HashMap<T,V> map;
    private boolean isLeaf;
    private ArrayList<BTreeNode<T, V>> children;
    private BTreeNode parent;
    private Comparator<T> comparator;
    public BTreeNode(boolean isLeaf)
    {
        this.isLeaf = isLeaf;
        map = new HashMap<T, V>();
        keys = new ArrayList<T>();
        values = new ArrayList<V>();
        children = new ArrayList<BTreeNode<T, V>>();
        numOfKeys = 0;
    }
    //Set or add child at the desired index
    public void setChild(int idx, BTreeNode node)
    {
        if(idx >= this.children.size())
            this.children.add(idx, node);
        else
            this.children.set(idx, node);
    }
    //Get the node child at the desired index in list
    public BTreeNode getChild(int idx)
    {
        return this.children.get(idx);
    }
    //Set or add key at the desired index
    public void setKey(int idx, T key)
    {
        if(idx >= this.keys.size())
            this.keys.add(idx, key);
        else
            this.keys.set(idx, key);
        if(key == null)
        {
            this.map.remove(key);
        }
    }
    //Get the node key at the desired index in list
    public T getKey(int idx)
    {
        return this.keys.get(idx);
    }
    //Set or add value at the desired index in addition to putting it in map
    public void setValue(int idx, T key,V value)
    {
        if(idx >= this.values.size())
            this.values.add(idx, value);
        else
            this.values.set(idx, value);
        if(this.map.containsKey(key))
        {
            return;
        }
        this.map.put(key, value);
    }
    //Get the node value at the desired index in list
    public V getValue(int idx)
    {
        return this.values.get(idx);
    }
    //Set parent to the node
    public void setParent(BTreeNode parent)
    {
        this.parent = parent;
    }
    //get the node parent
    public BTreeNode getParent()
    {
        return parent;
    }
    //Adding key and value to the node at last index
    public void addPair(T key, V value)
    {

        int i = numOfKeys - 1;
        while (i >= 0 && keys.get(i).compareTo(key) > 0) {
            if(i + 1 >= this.keys.size())
            {
                keys.add(i + 1, keys.get(i));
                values.add(i + 1, values.get(i));
            }
            else
            {
                keys.set(i + 1, keys.get(i));
                values.set(i + 1, values.get(i));
            }
            i--;
        }
        if(i + 1 >= this.keys.size())
        {
            keys.add(i + 1, key);
            values.add(i + 1, value);
        }
        else
        {
            keys.set(i + 1, key);
            values.set(i + 1, value);
        }
        numOfKeys++;
    }
    //Remove the key and its related value from the node lists and maps
    public void removePair(T key)
    {
        numOfKeys--;
        keys.remove(key);
        values.remove(map.get(key));
        map.remove(key);
    }
    //Remove child at given index
    public void removeChild(int idx)
    {
        this.children.remove(idx);
    }
    //Get the node has right sibling
    public BTreeNode getRightSibling()
    {
        int idx = this.parent.children.indexOf(this);
        if(idx < this.parent.getNumOfKeys())
        {
            return this.parent.getChild(idx + 1);
        }
        return null;
    }
    //Get the node has left sibling
    public BTreeNode getLeftSibling()
    {
        int idx = this.parent.children.indexOf(this);
        if(idx > 0)
        {
            return this.parent.getChild(idx -1);
        }
        return null;
    }
    //Get the node child index
    public int getChildIdx(BTreeNode child)
    {
        return this.children.indexOf(child);
    }
    //Get the node key index
    public int getKeyIdx(T key)
    {
        return this.keys.indexOf(key);
    }
    private BTreeNode leftSubtree(T key)
    {
        return this.children.get(keys.indexOf(key));
    }
    private BTreeNode rightSubtree(T key)
    {
        return this.children.get(keys.indexOf(key) + 1);
    }
    //Get the node predecessor leaf
    public BTreeNode getPreLeaf(T key)
    {
        BTreeNode subtree = this.leftSubtree(key);
        while(!subtree.isLeaf())
        {
            subtree = subtree.getChild(subtree.getNumOfKeys());
        }
        return subtree;
    }
    //Get the node successor leaf
    public BTreeNode getSucLeaf(T key)
    {
        BTreeNode subtree = this.rightSubtree(key);
        while(!subtree.isLeaf())
        {
            subtree = subtree.getChild(0);
        }
        return subtree;
    }
    @Override
    public int getNumOfKeys()
    {
        return this.numOfKeys;
    }

    @Override
    public void setNumOfKeys(int numOfKeys)
    {
        this.numOfKeys = numOfKeys;
    }

    @Override
    public boolean isLeaf()
    {
        return this.isLeaf;
    }

    @Override
    public void setLeaf(boolean isLeaf)
    {
        this.isLeaf = isLeaf;
    }

    @Override
    public List<T> getKeys()
    {
        ArrayList<T> keyList = new ArrayList<>();
        for(int i =0; i < this.keys.size(); i++)
        {
            keyList.add(keys.get(i));
        }
        return keyList;
    }

    @Override
    public void setKeys(List<T> keys)
    {
        for(int i = 0; i < keys.size(); i++)
        {
            this.keys.set(i, keys.get(i));
        }
    }

    @Override
    public List<V> getValues()
    {
        return this.values;
    }

    @Override
    public void setValues(List<V> values)
    {
        if(values.size() != this.keys.size())
        {
            exit(-1);
        }
        for(int i = 0; i < keys.size(); i++)
        {
            map.put(keys.get(i), values.get(i));
        }
        for(int i = 0; i < values.size(); i++)
        {
            this.values.set(i, values.get(i));
        }
    }
    @Override
    public List<IBTreeNode<T, V>> getChildren()
    {
        ArrayList<IBTreeNode<T, V>> childrenList = new ArrayList<>();
        for(int i = 0; i < this.children.size(); i++)
        {
            childrenList.add(this.children.get(i));
        }
        return childrenList;
    }

    @Override
    public void setChildren(List<IBTreeNode<T, V>> children)
    {
        for(int i = 0; i < this.children.size(); i++)
        {
            this.children.add((BTreeNode<T, V>) children.get(i));
        }
    }
}
