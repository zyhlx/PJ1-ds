import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;



//在看代码前请先阅读以下内容

/*
先声明我对B+树的定义：（因为发现网上定义有不一样的，所以在实现我的B+树之前先申明我的定义）
如果degree=t;
则Every node other than the root must have at least t – 1 keys. Every internal node other than the root thus has at least t children;
Every node can contain at most 2t – 1 keys. An internal node can have at most 2t children
及关键字数量：t-1~2t-1 子树数量比关键字多1 t~2t
—————以上的话删除—————11/11
—————以下新视角———————
我又重新在网上查了资料以及仔细阅读助教的文档，我发现都是用的M来表能装几个关键字，尤其是助教给的文档上也是这样，而不是书上B树那么写用t来表示 所以我又重新修改全部用M表示了

叶节点之间是单向链接 注意：助教给的例子是双向链表 但是我之前写的时候看到网上的定义有单向的尤其是百度百科给的例图是单向的

 */

/*
* 插入逻辑整理：
* 首先BNode类里的insertOrUpdate(String key, String mValue, BplusTree tree)
 * 对于BNode中间节点向下走找到对应地方的叶子节点
 * 到叶子节点调用同名override函数insertOrUpdate
 * 在叶子节点的insertOrUpdate函数中对三种插入情况的处理：
 * 第一种leaf节点没有满中间节点也没有满 直接插入 没有任何问题结束、
 * 第二种leaf节点满了 中间节点没有满 在insertOrUpdate函数中完成分开叶子节点、加到parent节点的操作，符合if (node.getChildren().size() - 1 > node.getEntries().size()) 并调用leafParentInsertFixed把entry提上来注意叶子节点的提上来是下面保留再重复一个上来
 * 第三种leaf节点满了 中间节点也满了 在完成上面操作后if (node.getEntries().size() > tree.getOrder()) 触发调用node.getParent().internalNodeInsertFixed(node.getParent(), tree) 将中间节点分成两份 注意与叶子节点不同这边分成两份要把中间的数去掉提上去
 *  然后增加entry，这个可以用这种方法parent.getEntries().add(index,node.getEntries().get(leftSize));当然也可以调用函数insertOrUpdate(String key)*/

/*11/11
 *B+树逻辑新整理：一开始用两个类一个中间节点的类一个叶子节点的类 想用函数的重载来完成，上面的思路就是我原来的想法
 * 后来我产生了一个致命的错误，就是我的children是List<BNode>类型的，也只会返回BNode了，那么我无法调用到BLeafNode类里面的方法 一开始我的解决方法是再创建一个leafChildren类 但是这样的话会非常麻烦我的代码会非常冗余
 * 所以我还是这能放在一个类里面用Boolean属性去判断了。但是这样的话由于之前我的Entry是两个不一样，在叶节点的类属性隐藏掉中间节点的，一旦放进同一个类以后又需要修改了 当前我的办法是新一个leafEntry的类
 */

/*
* 由于结构大改产生的新的插入逻辑：
* 先是insertOrUpdate(String key, String mValue, BplusTree tree)进入 多了是否叶节点的判断，如果是叶结点的话 分三种情况 都没有满 insertOrUpdate(key, mValue)直接插入;分裂的情况判断需要分裂额外调用leafParentInsertFixed(BNode node, BplusTree tree, int index)修改父节点的entry
* 如果再满足if (node.getEntries().size() > tree.getOrder()) {的情况即父节点也满了就调用
                node.getParent().internalNodeInsertFixed(node.getParent(), tree);
            }
 *
*
* */





/*delete逻辑整理
* Leaf PageBelow FillFactor Index PageBelow FillFactor因为每个节点要满足最小关键字情况所以会有这种情况发生由于Fill Factor是50%所以最小关键字就是Minimum Keys in each page⌈m/2⌉
*中间节点的删除是找后继 如果直接是单个的叶节点就直接删了
*
*
*
*
* */









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
 * delete的三种情况
 *第一种情况：key值对应的叶节点和中间节点的关键字数是都大于m/2,那么直接删除，如果有空，找后继节点代替。
 * Delete the record from the leaf page. Arrange keys in ascending order to fill void. If the key of the deleted record appears in the index page, use the next key to replace it.
 *第一种情况也分好几种 首先是只有叶节点其次是还有中间节点的情况
 * 第二种情况：key值对应的叶节点不够数目，中间节点足够：将叶节点合并，中间节点做出相应修改，比如并一个过去之后 叶节点上面的父节点的entry就得做出修改变成下去的那个的后继
 * Combine the leaf page and its sibling. Change the index page to reflect the change.
 *第三种情况：中间节点数目也不够
 *叶节点合并分析：删除的时候合并必然就少了一个children，所以是上面下来一个这样正好，上面下来一个如果上面的数量不够那么上面节点也要进行合并，或从左右借上下借过来就又成了一个递归
 *1. Combine the leaf page and its sibling.
 * 2. Adjust the index page to reflect the change.
 * Combine the index page with its sibling.
 * Continue combining index pages until you reach a page with the
 * correct fill factor or you reach the root page.
 *
 *
 */








public class BNode {
//    private String mKey;
    //Node对应的value值 只有叶节点才有mValue
//    private String mValue;

    //左子节点和右子节点，初始状态的时候都为null.
//    private BNode mLeftChild;
//    private BNode mRightChild;

    /**
     * 是否为叶子节点
     */
    protected boolean isLeaf;

    /**
     * 是否为根节点
     */
    protected boolean isRoot;

    /**
     * 父节点
     */
    protected BNode parent;


    /**
     * 节点的关键字
     *///一个节点里有n个关键字了   注意：如果不是叶结点的话 不储存信息 节省空间，只有leaf节点有数据
    private List<String> entries;

    //Map.Entry是Map声明的一个内部接口，此接口为泛型，定义为Entry<K,V>。它表示Map中的一个实体（一个key-value对）。接口中有getKey(),getValue方法。我将叶节点设为这个来储存卫星数据
    private List<Entry<String, String>> leafentries;

    /**
     * 子节点
     *///现在就是一个节点里有n个子节点的指针在这边存放 叶节点没有这个
    protected List<BNode> children;

    /**
     * 叶节点的前节点
     */
    protected BNode previous;

    /**
     * 叶节点的后节点
     */
    protected BNode next;

//    protected List<BLeafNode> leafChildren;

    public BNode() {
    }

    public BNode(boolean isLeaf) {
        this.isLeaf = isLeaf;

        if (!isLeaf) {
            entries = new ArrayList<String>();
            children = new ArrayList<BNode>();
        }else {
            entries = new ArrayList<String>();
            leafentries = new ArrayList<Entry<String, String>>();
        }
    }

    public BNode(BNode Parent, boolean isLeaf, boolean isRoot) {
        this(isLeaf);
//        mKey = key;
//        mValue = value;//叶节点才有
//        this.mLeftChild = mLeftChild;
//        this.mRightChild = mRightChild;
        this.isRoot = isRoot;
        this.parent = Parent;
    }

    public BNode(BNode Parent, boolean isLeaf, boolean isRoot,String key) {
        this(isLeaf);
        this.isRoot = isRoot;
        this.parent = Parent;
        this.entries.add(key);
    }


//    public String getmKey() {
//        return mKey;
//    }
//
//    public void setmKey(String mKey) {
//        this.mKey = mKey;
//    }

    public String search(String key) {
        //搜索是从上面root节点下来，先通过最后面的锁定叶子节点，再在叶子节点里面找
        //如果是叶子节点
        if (isLeaf) {
            for (Map.Entry<String,String> entry : leafentries ) {
                if (entry.getKey().compareTo(key) == 0) {
                    //返回找到的对象 叶子节点这边才有对象
                    return entry.getValue();
                }
            }
            //未找到所要查询的对象
            return null;
            //children[0]<entry[0]<children[1]<entry[1]<……<children[n]<entry[n]<children[n+1]
            //如果不是叶子节点也就是中间节点往下找 比较关键字大小左小右大
        }else {
        //如果key小于等于节点最左边的key，沿第一个子节点继续往下搜索
        if (key.compareTo(entries.get(0)) < 0) {
            return children.get(0).search(key);
            //如果key大于节点最右边的key，沿最后一个子节点往下继续搜索
        } else if (key.compareTo(entries.get(entries.size() - 1)) >= 0) {
            return children.get(children.size() - 1).search(key);
            //否则沿比key大的前一个子节点继续搜索在自己这边中间搜索
        } else {
            for (int i = 0; i < entries.size() - 1; i++) {
                //关键字中间的节点 这边-1是照顾最后一个 size=n;i=n-1的时候 保证i+1有值
                //children[0]<entry[0]<children[1]<entry[1]<……<children[n]<entry[n]<children[n+1]
                if (entries.get(i).compareTo(key) <= 0 && entries.get(i + 1).compareTo(key) > 0) {
                    return children.get(i + 1).search(key);
                }
            }
        }
        }

        return null;
    }


    //插入就是要考虑拆分
    //首先插入往下找到要插入的节点，当发现经过的节点没法插入的时候，就拆分成两个，这个的临界条件是此节点内元素=m,拆分过后再往下走
    public void insertOrUpdate(String key, String mValue, BplusTree tree) {
        //如果是叶子节点
//        if (isLeaf){
//            //不需要分裂，直接插入或更新  如果entry里有这个数那么是要更新并且这个entry里的大小是有空位的这边只是判断小于m的情况，小于还需要判断要不要拆分
//            if (entries.contains(key) || entries.size() < tree.getOrder()){//插入就变成等于或者更新还是不变
//                insertOrUpdate(key, obj);//直接插入不考虑fixUp;
//                //看看是不是变成等于了
//                if (parent != null) {
//                    //更新父节点
//                    parent.updateInsert(tree);
//                }
//
//                //需要分裂
//            }else {
//                //分裂成左右两个节点
//                BNode left = new BNode(true);
//                BNode right = new BNode(true);
//                //设置链接
//                if (previous != null){
//                    previous.setNext(left);
//                    left.setPrevious(previous);
//                }
//                if (next != null) {
//                    next.setPrevious(right);
//                    right.setNext(next);
//                }
//                if (previous == null){
//                    tree.setHead(left);
//                }
//
//                left.setNext(right);
//                right.setPrevious(left);
//                previous = null;
//                next = null;
//
//                //左右两个节点关键字长度
//                int leftSize = (tree.getOrder() + 1) / 2 + (tree.getOrder() + 1) % 2;
//                int rightSize = (tree.getOrder() + 1) / 2;
//                //复制原节点关键字到分裂出来的新节点
//                insertOrUpdate(key, obj);
//                for (int i = 0; i < leftSize; i++){
//                    left.getEntries().add(entries.get(i));
//                }
//                for (int i = 0; i < rightSize; i++){
//                    right.getEntries().add(entries.get(leftSize + i));
//                }
//
//                //如果不是根节点
//                if (parent != null) {
//                    //调整父子节点关系
//                    int index = parent.getChildren().indexOf(this);
//                    parent.getChildren().remove(this);
//                    left.setParent(parent);
//                    right.setParent(parent);
//                    parent.getChildren().add(index,left);
//                    parent.getChildren().add(index + 1, right);
//                    setEntries(null);
//                    setChildren(null);
//
//                    //父节点插入或更新关键字
//                    parent.updateInsert(tree);
//                    setParent(null);
//                    //如果是根节点
//                }else {
//                    isRoot = false;
//              //      BNode parent = new BNode(false, true);
//                    tree.setmRoot(parent);
//                    left.setParent(parent);
//                    right.setParent(parent);
//                    parent.getChildren().add(left);
//                    parent.getChildren().add(right);
//                    setEntries(null);
//                    setChildren(null);
//
//                    //更新根节点
//                    parent.updateInsert(tree);
//                }
//
//
//            }
//
//
//        }else {
        //如果不是叶子节点 注：
        //如果key小于等于节点最左边的key，沿第一个子节点继续搜索


        if(isLeaf){
            //前三步的操作有两种可能性
            //不需要分裂，直接插入或更新  如果entry里有这个数那么是要更新并且这个entry里的大小是有空位的这边只是判断小于m的情况，小于还需要判断要不要拆分
            if (contains(key) || leafentries.size() < tree.getOrder()) {//当包含或者size<m的时候就说明都没有满的情况
                insertOrUpdate(key, mValue);//直接插入;

                if(tree.getmRoot()==null){
                    this.setRoot(true);
                    tree.setmRoot(this);
                }
//            if (parent != null) {
//                //更新父节点
//                parent.updateInsert(tree);
//            }

                //需要分裂
            } else {
                //leaf节点满了 中间节点没有满 那么没有后面的操作
                //分裂成左右两个节点
                BNode left = new BNode(true);
                BNode right = new BNode(true);
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
                    left.getLeafEntries().add(leafentries.get(i));
                    //叶节点没有children
                }
                for (int i = 0; i < rightSize; i++) {
                    right.getLeafEntries().add(leafentries.get(leftSize + i));
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

        }else {
            if (key.compareTo(entries.get(0)) < 0) {
                children.get(0).insertOrUpdate(key, mValue, tree);
                //如果key大于节点最右边的key，沿最后一个子节点继续搜索
            } else if (key.compareTo(entries.get(entries.size() - 1)) >= 0) {
                children.get(children.size() - 1).insertOrUpdate(key, mValue, tree);
                //否则沿比key大的前一个子节点继续搜索
            } else {
                for (int i = 0; i < entries.size() - 1; i++) {
                    //children[0]<entry[0]<children[1]<entry[1]<……<children[n]<entry[n]<children[n+1]
                    if (entries.get(i).compareTo(key) <= 0 && entries.get(i + 1).compareTo(key) > 0) {
                        children.get(i + 1).insertOrUpdate(key, mValue, tree);
                        break;
                    }
                }
            }
        }

    }
    protected void insertOrUpdate(String key, String mValue) {
        //现在是要按照顺序找到插入的位置
        Map.Entry<String, String> entry = new AbstractMap.SimpleEntry<String, String>(key, mValue);
        //如果关键字列表长度为0，则直接插入
        if (leafentries.size() == 0) {
            leafentries.add(entry);
            return;
        }
        //否则遍历列表
        for (int i = 0; i < leafentries.size(); i++) {
            //如果该关键字键值已存在，则更新
            if (leafentries.get(i).getKey().compareTo(key) == 0) {
                leafentries.get(i).setValue(mValue);
                return;
                //否则插入
                //>从前开始从<0开始到==0到>0第一个大于0的数说明就插在这个地方
            } else if (leafentries.get(i).getKey().compareTo(key) > 0) {
                //插入到链首
                if (i == 0) {
                    leafentries.add(0, entry);
                    return;
                    //插入到中间
                } else {
                    leafentries.add(i, entry);
                    return;
                }
            }
        }
        //插入到末尾
        leafentries.add(leafentries.size(), entry);
    }

    protected static void leafParentInsertFixed(BNode node, BplusTree tree, int index) {//叶节点上面的父节点增加孩子
        if (node.getChildren().size() - 1 > node.getEntries().size()) {//children增加一个后变成这样了判断需要修改entry
            node.getEntries().add(index, node.getChildren().get(index + 1).getLeafEntries().get(0).getKey());
            if (node.getEntries().size() > tree.getOrder()) {

                    node.internalNodeInsertFixed(node, tree);

            }
        }
    }

    protected void internalNodeInsertFixed(BNode node, BplusTree tree) {


        if (node.getEntries().size() > tree.getOrder()) {
            //如果子节点数超出阶数，则需要分裂该节点
            //分裂成左右两个节点
            BNode left = new BNode(false);
            BNode right = new BNode(false);
            //左右两个节点关键字长度
            int leftSize = (tree.getOrder() + 1) / 2;
            int rightSize = tree.getOrder()  / 2 ;
            //复制子节点到分裂出来的新节点，并更新关键字
            //注意中间节点的上升不用保留中间的值
            //将子树指向更新
//            int upNumberIndex = leftSize;
            for (int i = 0; i < leftSize; i++) {
                left.getChildren().add(children.get(i));
                left.getEntries().add(node.getEntries().get(i));
                children.get(i).setParent(left);
                if(i==leftSize-1){
                    left.getChildren().add(children.get(i+1));
                    children.get(i+1).setParent(left);
                }
            }
            for (int i = 0; i < rightSize; i++) {
                right.getChildren().add(children.get(leftSize + i+1));//那个要放上去的不用拷贝下来
                right.getEntries().add(node.getEntries() .get(i+leftSize+1));
                children.get(leftSize + i+1).setParent(right);
                if(i+1==rightSize){
                    right.getChildren().add(children.get(node.getChildren().size()-1));
                    children.get(node.getChildren().size()-1).setParent(right);
                }
            }

            //如果不是根节点
            if (parent != null) {
                //调整父子节点关系
                int index = parent.getChildren().indexOf(this);
                parent.getChildren().remove(this);
                left.setParent(parent);
                right.setParent(parent);
                parent.getChildren().add(index, left);
                parent.getChildren().add(index + 1, right);

                //父节点更新关键字
                parent.getEntries().add(index,node.getEntries().get(leftSize));
                //insertOrUpdate(String key)

                setEntries(null);
                setChildren(null);

               //检查上一个
                parent.internalNodeInsertFixed(parent,tree);
                setParent(null);
                //如果是根节点
            } else {
                isRoot = false;
                BNode parent = new BNode(null, false, true);
                tree.setmRoot(parent);
                left.setParent(parent);
                right.setParent(parent);
                parent.getChildren().add(left);
                parent.getChildren().add(right);


                //更新根节点
                parent.getEntries().add(0,node.getEntries().get(leftSize));

                //再删
                setEntries(null);
                setChildren(null);

                parent.internalNodeInsertFixed(parent,tree);


            }

        }


    }

    public void remove(String key, BplusTree tree) {
        if(tree.bTreeSearch(key)==null){
                System.out.println(key+"不存在");
        }else {
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
                    if (leafentries.size() > tree.getOrder() / 2 && leafentries.size() > 2) {
                        remove(key);
                    } else {
                        //如果自身关键字数小于M / 2，并且前节点关键字数大于M / 2，则从其处借补
                        if (previous != null
                                && previous.getLeafEntries().size() > tree.getOrder() / 2
                                && previous.getLeafEntries().size() > 2
                                && previous.getParent() == parent) {
                            int size = previous.getLeafEntries().size();
                            Entry<String,String> entry =  previous.getLeafEntries().get(size - 1);
                            previous.getLeafEntries().remove(entry);//前面最后一个
                            //添加到首位
                            leafentries.add(0, entry);
                            remove(key);
                            //如果自身关键字数小于M / 2，并且后节点关键字数大于M / 2，则从其处借补
                        } else if (next != null
                                && next.getLeafEntries().size() > tree.getOrder() / 2
                                && next.getLeafEntries().size() > 2
                                && next.getParent() == parent) {
                            Entry<String,String> entry = next.getLeafEntries().get(0);//后面第一个
                            next.getLeafEntries().remove(entry);
                            //添加到末尾
                            leafentries.add(entry);
                            remove(key);
                            //否则需要合并叶子节点
                        } else {
                            //同前面节点合并
                            if (previous != null
                                    && (previous.getLeafEntries().size() <= tree.getOrder() / 2 || previous.getLeafEntries().size() <= 2)
                                    && previous.getParent() == parent) {
                                int index=parent.getChildren().indexOf(this);
                                for (int i = previous.getLeafEntries().size() - 1; i >= 0; i--) {
                                    //从末尾开始添加到首位
                                    leafentries.add(0, previous.getLeafEntries().get(i));
                                }
                                remove(key);
                                previous.setParent(null);
                                previous.setLeafEntries(null);
                                parent.getChildren().remove(previous);
                                //更新链表
                                if (previous.getPrevious() != null) {
                                    BNode temp = previous;
                                    temp.getPrevious().setNext(this);
                                    previous = temp.getPrevious();
                                    temp.setPrevious(null);
                                    temp.setNext(null);
                                } else {
                                    tree.setHead(this);
                                    previous.setNext(null);
                                    previous = null;
                                }

                                parent.leafParentUpdateRemove(parent,tree,index-1);//因为存在前后合并所以会有children减少导致entry和children一样 所以最后有一个操作是修复entry
                                //同后面节点合并
                            } else if (next != null
                                    && (next.getLeafEntries().size() <= tree.getOrder() / 2 || next.getLeafEntries().size() <= 2)
                                    && next.getParent() == parent) {
                                int index=parent.getChildren().indexOf(this);
                                for (int i = 0; i < next.getLeafEntries().size(); i++) {
                                    //从首位开始添加到末尾
                                    leafentries.add(next.getLeafEntries().get(i));
                                }
                                remove(key);
                                next.setParent(null);
                                next.setLeafEntries(null);
                                parent.getChildren().remove(next);
                                //更新链表
                                if (next.getNext() != null) {
                                    BNode temp = next;
                                    temp.getNext().setPrevious(this);
                                    next = temp.getNext();
                                    temp.setPrevious(null);
                                    temp.setNext(null);
                                } else {
                                    next.setPrevious(null);
                                    next = null;
                                }
                                parent.leafParentUpdateRemove(parent,tree,index);//因为存在前后合并所以会有children减少导致entry和children一样 所以最后有一个操作是修复entry
                            }
                        }
                    }

                }
            }else {
                if (key.compareTo(entries.get(0)) < 0) {
                    children.get(0).remove(key, tree);
                    //如果key大于节点最右边的key，沿最后一个子节点继续搜索
                } else if (key.compareTo(entries.get(entries.size() - 1)) >= 0) {
                    children.get(children.size() - 1).remove(key, tree);
                    //否则沿比key大的前一个子节点继续搜索
                } else {
                    for (int i = 0; i < entries.size()-1; i++) {
                        if (entries.get(i).compareTo(key) <= 0 && entries.get(i + 1).compareTo(key) > 0) {
//                            if (entries.get(i).compareTo(key)==0){
//                                if(successor(tree,key)!=null){
//                                    entries.remove(entries.get(i));
//                                    insertOrUpdate(successor(tree,key));//找后继这边有点问题 你这个一样的就放后继删除，假如后继也在中间节点上，就不可以这样用了就有一个递推的过程
//                                }
//                            }
                            children.get(i+1).remove(key, tree);


                            break;
                        }
                    }

                }
            }
        }





    }


    //当子节点大于M / 2并且小于M，并且大于2 也就是现在一切正常的时候，当你删除了叶子节点 需要删除中间节点
//    public void removeInternalNode(BNode node,BplusTree tree) {
//        if (node.isRoot() && node.getChildren().size() >= 2
//                || node.getChildren().size() >= tree.getOrder() / 2
//                && node.getChildren().size() <= tree.getOrder()
//                && node.getChildren().size() >= 2) {
//            node.getEntries().clear();
//            for (int i = 0; i < node.getChildren().size(); i++) {
//                String key = node.getChildren().get(i).getEntries().get(0);
//                node.getEntries().add(key);
//                if (!node.isRoot()) {
//                    removeInternalNode(node.getParent(), tree);
//                }
//
//            }
//        }
//    }
//

    /**
     * 删除节点
     */
    //并没有删除相应的中间节点
    protected void remove(String key) {
        int index = -1;
        for (int i = 0; i < leafentries.size(); i++) {
            if (leafentries.get(i).getKey().compareTo(key) == 0) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            leafentries.remove(index);
        }
    }

    public void leafParentUpdateRemove(BNode node,BplusTree tree,int index){
        //前后合并之后会导致少一个children 所以父节点的那一个要下来合并
        //合并判断
        if(node.getChildren().size()==node.getEntries().size()){
            node.getEntries().remove(index);
            if(node.getEntries().size()<tree.getOrder() / 2){
                node.updateRemove(tree);//上面少一个之后不够用
            }

        }

    }

//    protected void removeEntryFixed(BNode node,BplusTree tree){
//        // 如果子节点数大于M / 2并且小于M，并且大于2  这边是做什么？是找后继吗
//        if ((node.isRoot() && node.getChildren().size() >= 2)
//                ||node.getChildren().size() >= tree.getOrder() / 2
//                && node.getChildren().size() <= tree.getOrder()
//                && node.getChildren().size() >= 2) {
//            node.getEntries().clear();
//            for (int i = 0; i < node.getChildren().size(); i++) {
//                String key = node.getChildren().get(i).getEntries().get(0);
//                node.getEntries().add(key);
//                if (!node.isRoot()) {
//                    removeEntryFixed(node.getParent(), tree);
//                }
//            }
//        }
//    }
    /** 删除节点后中间节点的更新*/
    //这边和B+树一样了就 我并没有删除中间节点的索引值，因为并不影响整棵树的操作 只是树的结构不一样而已
    //好吧 是找到后继有点麻烦 因为还要考虑第一个后继已经在索引页上的情况，我初步想法是设一个类属性来记录这件事，但是我的时间不多了，就没有这样尝试
    protected void updateRemove(BplusTree tree) {
//        removeEntryFixed(this,tree);

        // 如果子节点数小于M / 2或者小于2，则需要合并节点
        if (getEntries().size() < tree.getOrder() / 2 || getEntries().size() < 2) {//entry.size<tree.getorder/2
            if (isRoot) {
                // 如果是根节点并且子节点数大于等于2，OK
                if (children.size() >= 2) {
                    return;
                    // 否则与子节点合并
                } else {
                    //合并不对
                    BNode root = children.get(0);
                    tree.setmRoot(root);
                    root.setParent(null);
                    root.setRoot(true);
                    setEntries(null);
                    setChildren(null);
                }
            } else {
                //计算前后节点
                int currIdx = parent.getChildren().indexOf(this);
                int prevIdx = currIdx - 1;
                int nextIdx = currIdx + 1;
                BNode previous = null, next = null;
                if (prevIdx >= 0) {
                    previous = parent.getChildren().get(prevIdx);
                }
                if (nextIdx < parent.getChildren().size()) {
                    next = parent.getChildren().get(nextIdx);
                }

                // 如果前节点子节点数大于M / 2并且大于2，则从其处借补
                //父节点下来 兄弟节点上去
                if (previous != null
                        && previous.getChildren().size() > tree.getOrder() / 2
                        && previous.getChildren().size() > 2) {
                    //前叶子节点末尾节点添加到首位
                    int idx = previous.getChildren().size() - 1;
                    BNode borrow = previous.getChildren().get(idx);
                    previous.getChildren().remove(idx);
                    borrow.setParent(this);
                    children.add(0, borrow);

                    getEntries().add(0,parent.getEntries().get(currIdx-1));
                    parent.getEntries().remove(currIdx-1);
                    parent.getEntries().add(currIdx-1,previous.getEntries().get(idx));
                    previous.getEntries().remove(previous.getEntries().size()-1);
//                    validate(previous, tree);
//                    validate(this, tree);
 //                   parent.updateRemove(tree);

                    // 如果后节点子节点数大于M / 2并且大于2，则从其处借补
                    //父节点下来 兄弟节点上去
                } else if (next != null
                        && next.getChildren().size() > tree.getOrder() / 2
                        && next.getChildren().size() > 2) {
                    //后叶子节点首位添加到我这个节点的末尾
                    BNode borrow = next.getChildren().get(0);
                    children.add(borrow);
                    borrow.setParent(this);
                    next.getChildren().remove(0);//后面这个节点移除一个children和一个entry因为要上去

                    getEntries().add(parent.getEntries().get(currIdx));//我添加父节点的entry
                    parent.getEntries().remove(currIdx);
                    parent.getEntries().add(currIdx,next.getEntries().get(0));//entry放到父节点上
                    next.getEntries().remove(0);//next的entry移除

       //             validate(next, tree);
          //          validate(this, tree);
                   // parent.updateRemove(tree);

                    // 否则需要合并节点
                } else {
                    // 同前面节点合并
                    if (previous != null
                            && (previous.getChildren().size() <= tree.getOrder() / 2 || previous.getChildren().size() <= 2)) {

                        for (int i = previous.getChildren().size() - 1; i >= 0; i--) {
                            BNode child = previous.getChildren().get(i);
                            children.add(0, child);//新的大的children
                            child.setParent(this);
                            if(i==previous.children.size()-1){
                                getEntries().add(0,parent.getEntries().get(currIdx-1));
                            }
                            if(i<previous.getEntries().size()){
                                getEntries().add(0,previous.getEntries().get(i));
                            }
                        }
                        previous.setChildren(null);
                        previous.setEntries(null);
                        previous.setParent(null);
                        parent.getChildren().remove(previous);
                        parent.getEntries().remove(currIdx-1);
        //                validate(this, tree);
                        parent.updateRemove(tree);

                        // 同后面节点合并
                    } else if (next != null
                            && (next.getChildren().size() <= tree.getOrder() / 2 || next.getChildren().size() <= 2)) {

                        for (int i = 0; i < next.getChildren().size(); i++) {
                            BNode child = next.getChildren().get(i);
                            children.add(child);
                            if(i==0){
                                getEntries().add(parent.getEntries().get(currIdx));
                            }
                            if(i<next.getEntries().size()){
                                getEntries().add(next.getEntries().get(i));
                            }
                            child.setParent(this);
                        }
                        next.setChildren(null);
                        next.setEntries(null);
                        next.setParent(null);
                        parent.getChildren().remove(next);
                        parent.getEntries().remove(currIdx);
      //                  validate(this, tree);
                        parent.updateRemove(tree);
                    }
                }
            }
        }
    }

    protected void insertOrUpdate(String key) {
        //现在是要按照顺序找到插入的位置
        String entry = key;
        //如果关键字列表长度为0，则直接插入
        if (entries.size() == 0) {
            entries.add(entry);
            return;
        }
        //否则遍历列表
        for (int i = 0; i < entries.size(); i++) {
            //如果该关键字键值已存在，则更新
            if (entries.get(i).compareTo(key) == 0) {
//                entries.get(i).setValue(obj);
                return;
                //否则插入
                //>从前开始从<0开始到==0到>0第一个大于0的数说明就插在这个地方
            } else if (entries.get(i).compareTo(key) > 0) {
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

    protected String successor(BplusTree tree,String key){
        BNode node = tree.getHead();
        while (node!=null&&!node.contains(key)){
            node = node.getNext();
            if(node.contains(key)){
                break;
            }
        };
        if(node!=null){
            for (int i = 0;i<node.getLeafEntries().size();i++) {
                if (node.getLeafEntries().get(i).getKey().compareTo(key) == 0) {
                    if(i<node.getLeafEntries().size()-1){
                        return node.getLeafEntries().get(i+1).getKey();
                    }else {
                        if(node.getNext()!=null){
                            return node.getNext().getLeafEntries().get(0).getKey();
                        }else {
                            return null;
                        }
                    }
                }
            }
        }


        return null;
    }
    protected boolean contains(String key) {
        for (Map.Entry<String,String > entry : leafentries) {
            if (entry.getKey().compareTo(key) == 0) {
                return true;
            }
        }
        return false;
    }
    public boolean isRoot() {
        return isRoot;
    }

    public BNode getParent() {
        return parent;
    }

    public List<BNode> getChildren() {
        return children;
    }

    public List<String> getEntries() {
        return entries;
    }


    public void setChildren(List<BNode> children) {
        this.children = children;
    }


    public void setParent(BNode parent) {
        this.parent = parent;
    }

    public void setEntries(List<String> entries) {
        this.entries = entries;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public boolean isLeaf() {
        return isLeaf;
    }
    public void setNext(BNode next) {
        this.next = next;
    }

    public BNode getPrevious(){
        return previous;
    }

    public BNode getNext() {
        return next;
    }

    public void setPrevious(BNode previous) {
        this.previous = previous;
    }

    public List<Entry<String, String>> getLeafEntries() {
        return leafentries;
    }

    public void setLeafEntries(List<Entry<String, String>> entries) {
        this.leafentries = entries;
    }


//    public BNode getmLeftChild() {
//        return mLeftChild;
//    }
//
//
//    public void setmLeftChild(BNode mLeftChild) {
//        this.mLeftChild = mLeftChild;
//    }
//
//
//    public BNode getmRightChild() {
//        return mRightChild;
//    }
//
//
//    public void setmRightChild(BNode mRightChild) {
//        this.mRightChild = mRightChild;
//    }



    /**
     * 调整节点关键字
     */
//
//    protected static void validate(BNode node, BplusTree tree) {
//        //加了子节点后会多
//        if (node.getEntries().size() == node.getChildren().size()) {
//            for (int i = 0; i < node.getEntries().size(); i++) {
//                String key = node.getChildren().get(i+1).getEntries().get(0);//子树开头的那个节点 子树多一 第一个不是的
//                //如果父亲i序号的关键字和这个不一样
//                if (node.getEntries().get(i).compareTo(key) != 0) {
//                    node.getEntries().remove(i);
//                    node.getEntries().add(i, key);
//                    if(!node.isRoot()){
//                        validate(node.getParent(), tree);
//                    }
//                }
//            }
//            // 如果子节点数不等于关键字个数但仍大于M / 2并且小于M，并且大于2
//        } else if (node.isRoot() && node.getChildren().size() >= 2
//                ||node.getChildren().size() >= tree.getOrder() / 2
//                && node.getChildren().size() <= tree.getOrder()
//                && node.getChildren().size() >= 2) {
//            node.getEntries().clear();
//            for (int i = 1; i < node.getChildren().size(); i++) {//被我修改了
//                String key = node.getChildren().get(i).getEntries().get(0);
//                node.getEntries().add(key);
//                if (!node.isRoot()) {
//                    validate(node.getParent(), tree);
//                }
//            }
//        }
//    }


    //
//    protected void internalNodeEntryUpdateInsert(BNode node, BplusTree tree, int index) {
//        if (node.getChildren().size() - 1 > node.getEntries().size()) {//children增加一个后变成这样了判断需要修改entry
//            node.getEntries().add(index, node.getChildren().get(index + 1).getEntries().get(0));
//            if (node.getEntries().size() > tree.getOrder()) {
//                node.getParent().internalNodeInsertFixed(node.getParent(), tree);
//            }
//        }
//    }

    /**
     * 插入节点后中间节点的更新
     */
//    protected void updateInsert(BNode node,BplusTree tree){
//        if(node.getChildren().size()==node.getEntries().size()){//children增加一个后变成这样了判断需要修改entry
//            if(node.getChildren().get(0).isLeaf()) {//说明是叶节点上面的父节点增加孩子
//                for (int i = 0; i < node.getChildren().size()-1; i++) {//子树开头的那个节点 子树多一 第一个不是的
//                    String key = node.getChildren().get(i+1).getEntries().get(0);
//                    //如果父亲i序号的关键字和这个不一样
//                    if (node.getEntries().get(i).compareTo(key) != 0) {
//                        node.getEntries().remove(i);
//                        node.getEntries().add(i, key);
////                        if(!node.isRoot()){
////                            validate(node.getParent(), tree);//把上一个父节点解决，再看新增了一个关键字的父节点有没有满
////                        }
//                    }
//                }
//            }else {
//                for (int i = 0; i < node.getChildren().size()-1; i++) {//子树开头的那个节点 子树多一 第一个不是的
//
//                    }
//                }

//            }
//        }
//
//
//
////        validate(this, tree);
//
//        //如果子节点数超出阶数，则需要分裂该节点
//        if (children.size() > tree.getOrder()) {//再想想
//            //分裂成左右两个节点
//            BNode left = new BNode(false);
//            BNode right = new BNode(false);
//            //左右两个节点关键字长度
//            int leftSize = (tree.getOrder() + 1) / 2;
//            int rightSize =(tree.getOrder() + 1) / 2 + (tree.getOrder() + 1) % 2;
//            //复制子节点到分裂出来的新节点，并更新关键字
//            //将子树指向更新
//            for (int i = 0; i < leftSize; i++){
//                left.getChildren().add(children.get(i));
//                left.getEntries().add(children.get(i).getEntries().get(0));
//                children.get(i).setParent(left);
//            }
//            for (int i = 0; i < rightSize; i++){
//                right.getChildren().add(children.get(leftSize + i));
//                right.getEntries().add(children.get(leftSize + i).getEntries().get(0));
//                children.get(leftSize + i).setParent(right);
//            }
//
//            //如果不是根节点
//            if (parent != null) {
//                //调整父子节点关系
//                int index = parent.getChildren().indexOf(this);
//                parent.getChildren().remove(this);
//                left.setParent(parent);
//                right.setParent(parent);
//                parent.getChildren().add(index,left);
//                parent.getChildren().add(index + 1, right);
//                setEntries(null);
//                setChildren(null);
//
//                //父节点更新关键字
//                parent.updateInsert(tree);
//                setParent(null);
//                //如果是根节点
//            }else {
//                isRoot = false;
//                BNode parent = new BNode(null,null,null,false,true);
//                tree.setmRoot(parent);
//                left.setParent(parent);
//                right.setParent(parent);
//                parent.getChildren().add(left);
//                parent.getChildren().add(right);
//                setEntries(null);
//                setChildren(null);
//
//                //更新根节点
//                parent.updateInsert(tree);
//            }
//        }
//    }
}
