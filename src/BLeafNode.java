import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import java.util.Map;
import java.util.Map.Entry;

public class BLeafNode extends BNode {
    private List<Entry<String, String>> entries;
    /**
     * 叶节点的前节点
     */
    protected BLeafNode previous;

    /**
     * 叶节点的后节点
     */
    protected BLeafNode next;

//    private String mValue;

    public BLeafNode(boolean isLeaf) {
        this.isLeaf = isLeaf;
        entries = new ArrayList<Entry<String, String>>();

        if (!isLeaf) {
            children = new ArrayList<BNode>();
        }
    }


    public BLeafNode(String mValue, BNode Parent, boolean isLeaf, boolean isRoot) {
        super(Parent,isLeaf, isRoot);
        entries = new ArrayList<Entry<String, String>>();
//        this.mValue = mValue;
    }


    public String search(String key) {
        //搜索是从上面root节点下来，先通过最后面的锁定叶子节点，再在叶子节点里面找
        //如果是叶子节点

        for (Map.Entry<String, String> entry : entries) {
            if (entry.getKey().compareTo(key) == 0) {
                //返回找到的对象 叶子节点这边才有对象
                return entry.getValue();
            }
        }
        //未找到所要查询的对象
        return null;

    }



/*
insert有三种情况
第一种leaf节点没有满中间节点也没有满 直接插入
第二种leaf节点满了 中间节点没有满
1. Split the leaf page        分开叶子节点
2. Place Middle Key in the index page in sorted order.    把中点的值放到中间节点上
3. Left leaf page contains records with keys below the middle key.     左边的叶子节点包含小于中间值的值
Right leaf page contains records with keys equal to or greater than the middle key.  右边的叶子节点包含大于等于中间值的值
第三种情况leaf节点满了 中间节点也满了
1. Split the leaf page.  分开叶子节点
2. Records with keys < middle key go to the left leaf page.左边的叶子节点包含小于中间值的值
3. Records with keys >= middle key go to the right leaf page.右边的叶子节点包含大于等于中间值的值
4. Split the index page. 分开中间节点
5. Keys < middle key go to the left index page. 小于中间关键码的放在左边
6. Keys > middle key go to the right index page. 大于中间关键码的在右边
The middle key goes to the next (higher level) index. 中间的上升
IF the next level index page is full, continue splitting the index pages. 如果上一级还是满的继续分开

 */

    /**
     * 这个函数解决前3步
     * 父节点满的判断和分裂交给后面的函数去做
     **/
    public void insertOrUpdate(String key, String mValue, BplusTree tree) {
        //前三步的操作有两种可能性
        //不需要分裂，直接插入或更新  如果entry里有这个数那么是要更新并且这个entry里的大小是有空位的这边只是判断小于m的情况，小于还需要判断要不要拆分
        if (contains(key) || entries.size() < tree.getOrder()) {//当包含或者size<m的时候就说明都没有满的情况
            insertOrUpdate(key, mValue);//直接插入;
            //叶节点这一步依然需要
            if (parent != null) {
                //更新父节点
               // parent.updateInsert(tree);
                if(tree.getmRoot()==null){
                    this.setRoot(true);
                    tree.setmRoot(this);
                }
            }

            //需要分裂
        } else {

            //leaf节点满了 中间节点没有满 那么没有后面的操作
            //分裂成左右两个节点
            BLeafNode left = new BLeafNode(true);
            BLeafNode right = new BLeafNode(true);
            //设置链接
            if (previous != null) {
                previous.setNext(left);//把前面的和左边的连起来
                left.setPrevious(previous);
            }
            if (next != null) {//把后面的和右边的连起来
                next.setPrevious(right);
                right.setNext(next);
            }
            if (previous == null) {//如果是第一个的情况
                tree.setHead(left);
            }

            left.setNext(right);//这两个连起来
            right.setPrevious(left);
            previous = null;//自己删除
            next = null;

            //左右两个节点关键字长度 因为叶节点是满了所以也就是叶节点的数量原来是2t-1再加一个变成2t
            //分裂是小于的在左 大于的在右 算中间
            int leftSize = (tree.getOrder() + 1) / 2;
            int rightSize = (tree.getOrder() + 1) / 2 + (tree.getOrder() + 1) % 2;
            ;//右边是大于等于
            //复制原节点关键字到分裂出来的新节点
            insertOrUpdate(key, mValue);//先直接插入
            for (int i = 0; i < leftSize; i++) {
                left.getLeafEntries().add(entries.get(i));
                //叶节点没有children
            }
            for (int i = 0; i < rightSize; i++) {
                right.getLeafEntries().add(entries.get(leftSize + i));
            }
            //有分裂的，调整父子关系
            //如果不是根节点
            if (parent != null) {
                //调整父子节点关系
                int index = parent.getChildren().indexOf(this);
                parent.getChildren().remove(this);//把我删了
                left.setParent(parent);
                right.setParent(parent);
                parent.getChildren().add(index, left);//把左右新子节点加到列表里但是分裂了以后就父节点的关键字不够用了
                parent.getChildren().add(index + 1, right);

                //父节点插入或更新关键字 父节点更新 要提一个关键字上去
                leafParentInsertFixed(parent, tree, index);//父节点一定是node

                setLeafEntries(null);//自己清空
                setChildren(null);//自己清空


                setParent(null);//自己清空
                //如果是根节点
            } else {
                isRoot = false;//自己要删掉不是root节点了
                BNode parent = new BNode(null,false, true);
                tree.setmRoot(parent);
                left.setParent(parent);
                right.setParent(parent);
                parent.getChildren().add(left);
                parent.getChildren().add(right);


                //更新根节点
                leafParentInsertFixed(parent, tree, 0);

                setEntries(null);
                setChildren(null);
            }

        }

    }


    protected void insertOrUpdate(String key, String mValue) {
        //现在是要按照顺序找到插入的位置
        Map.Entry<String, String> entry = new AbstractMap.SimpleEntry<String, String>(key, mValue);
        //如果关键字列表长度为0，则直接插入
        if (entries.size() == 0) {
            entries.add(entry);
            return;
        }
        //否则遍历列表
        for (int i = 0; i < entries.size(); i++) {
            //如果该关键字键值已存在，则更新
            if (entries.get(i).getKey().compareTo(key) == 0) {
                entries.get(i).setValue(mValue);
                return;
                //否则插入
                //>从前开始从<0开始到==0到>0第一个大于0的数说明就插在这个地方
            } else if (entries.get(i).getKey().compareTo(key) > 0) {
                //插入到链首
                if (i == 0) {
                    entries.add(0, entry);
                    return;
                    //插入到中间
                } else {
                    entries.add(i, entry);
                    return;
                }
            }
        }
        //插入到末尾
        entries.add(entries.size(), entry);
    }



    protected static void leafParentInsertFixed(BNode node, BplusTree tree, int index) {//叶节点上面的父节点增加孩子
        if (node.getChildren().size() - 1 > node.getEntries().size()) {//children增加一个后变成这样了判断需要修改entry
       //     node.getEntries().add(index, node.getChildren().get(index + 1).getEntries().get(0));
            if (node.getEntries().size() > tree.getOrder()) {
                node.getParent().internalNodeInsertFixed(node.getParent(), tree);
            }
        }
    }


    /*叶子节点delete步骤*/

    public void remove(String key, BplusTree tree) {
        //如果是叶子节点
        if (isLeaf) {

            //如果不包含该关键字，则直接返回
            if (!this.contains(key)) {
                return;
            }

            //如果既是叶子节点又是根节点，直接删除
            if (isRoot) {
                remove(key);
            } else {
                //如果关键字数大于M / 2，直接删除
                if (entries.size() > tree.getOrder() / 2 && entries.size() > 2) {
                    remove(key);
                } else {
                    //如果自身关键字数小于M / 2，并且前节点关键字数大于M / 2，则从其处借补
                    if (previous != null
                            && previous.getEntries().size() > tree.getOrder() / 2
                            && previous.getEntries().size() > 2
                            && previous.getParent() == parent) {
                        int size = previous.getEntries().size();
                        Entry<String,String> entry =  previous.getLeafEntries().get(size - 1);
                        previous.getLeafEntries().remove(entry);
                        //添加到首位
                        entries.add(0, entry);
                        remove(key);
                        //如果自身关键字数小于M / 2，并且后节点关键字数大于M / 2，则从其处借补
                    } else if (next != null
                            && next.getEntries().size() > tree.getOrder() / 2
                            && next.getEntries().size() > 2
                            && next.getParent() == parent) {
                        Entry<String,String> entry = next.getLeafEntries().get(0);
                        next.getLeafEntries().remove(entry);
                        //添加到末尾
                        entries.add(entry);
                        remove(key);
                        //否则需要合并叶子节点
                    } else {
                        //同前面节点合并
                        if (previous != null
                                && (previous.getEntries().size() <= tree.getOrder() / 2 || previous.getEntries().size() <= 2)
                                && previous.getParent() == parent) {
                            for (int i = previous.getEntries().size() - 1; i >= 0; i--) {
                                //从末尾开始添加到首位
                                entries.add(0, previous.getLeafEntries().get(i));
                            }
                            remove(key);
                            previous.setParent(null);
                            previous.setEntries(null);
                            parent.getChildren().remove(previous);
                            //更新链表
                            if (previous.getPrevious() != null) {
                                BLeafNode temp = previous;
                                temp.getPrevious().setNext(this);
                                previous = temp.getPrevious();
                                temp.setPrevious(null);
                                temp.setNext(null);
                            } else {
                                tree.setHead(this);
                                previous.setNext(null);
                                previous = null;
                            }
                            //同后面节点合并
                        } else if (next != null
                                && (next.getEntries().size() <= tree.getOrder() / 2 || next.getEntries().size() <= 2)
                                && next.getParent() == parent) {
                            for (int i = 0; i < next.getLeafEntries().size(); i++) {
                                //从首位开始添加到末尾
                                entries.add(next.getLeafEntries().get(i));
                            }
                            remove(key);
                            next.setParent(null);
                            next.setEntries(null);
                            parent.getChildren().remove(next);
                            //更新链表
                            if (next.getNext() != null) {
                                BLeafNode temp = next;
                                temp.getNext().setPrevious(this);
                                next = temp.getNext();
                                temp.setPrevious(null);
                                temp.setNext(null);
                            } else {
                                next.setPrevious(null);
                                next = null;
                            }
                        }
                    }
                }
                       parent.updateRemove(tree);//因为存在前后河并所以会有children减少导致entry和children一样
            }
        }
    }

    protected void removeEntryFixed(BNode node,BplusTree tree){
        if (node.isRoot() && node.getChildren().size() >= 2
                ||node.getChildren().size() >= tree.getOrder() / 2
                && node.getChildren().size() <= tree.getOrder()
                && node.getChildren().size() >= 2) {
            node.getEntries().clear();
            for (int i = 0; i < node.getChildren().size(); i++) {
                String key = node.getChildren().get(i).getEntries().get(0);
                node.getEntries().add(key);
                if (!node.isRoot()) {
                    removeEntryFixed(node.getParent(), tree);
                }
            }
        }
    }


    /** 判断当前节点是否包含该关键字*/
    protected boolean contains(String key) {
        for (Map.Entry<String,String > entry : entries) {
            if (entry.getValue().compareTo(key) == 0) {
                return true;
            }
        }
        return false;
    }


    public void setNext(BLeafNode next) {
        this.next = next;
    }

    public BLeafNode getPrevious(){
        return previous;
    }

    public BLeafNode getNext() {
        return next;
    }

    public void setPrevious(BLeafNode previous) {
        this.previous = previous;
    }

    public List<Entry<String, String>> getLeafEntries() {
        return entries;
    }

    public void setLeafEntries(List<Entry<String, String>> entries) {
        this.entries = entries;
    }

/*
    protected static void validate(BNode node, BplusTree tree) {
        //加了子节点后会多
        if (node.getEntries().size() == node.getChildren().size() - 1) {
            for (int i = 0; i < node.getEntries().size(); i++) {
                String key = node.getChildren().get(i + 1).getEntries().get(0);//子树开头的那个节点 子树多一 第一个不是的
                //如果父亲i序号的关键字和这个不一样
                if (node.getEntries().get(i).compareTo(key) != 0) {
                    node.getEntries().remove(i);
                    node.getEntries().add(i, key);
                    if (!node.isRoot()) {
                        validate(node.getParent(), tree);
                    }
                }
            }
            // 如果子节点数不等于关键字个数但仍大于M / 2并且小于M，并且大于2
        } else if (node.isRoot() && node.getChildren().size() >= 2
                || node.getChildren().size() >= tree.getOrder() / 2
                && node.getChildren().size() <= tree.getOrder()
                && node.getChildren().size() >= 2) {
            node.getEntries().clear();
            for (int i = 1; i < node.getChildren().size(); i++) {//被我修改了
                String key = node.getChildren().get(i).getEntries().get(0);
                node.getEntries().add(key);
                if (!node.isRoot()) {
                    validate(node.getParent(), tree);
                }
            }
        }
    }
    */
}
