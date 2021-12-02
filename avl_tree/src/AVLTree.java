
/**
 *
 * AVLTree
 *
 * An implementation of a AVL Tree with distinct integer keys and info.
 *
 */

public class AVLTree {

	private final IAVLNode externalLeaf = new AVLNode();

	private IAVLNode root = externalLeaf;
	private int size = 0;

	/**
	 * public boolean empty()
	 *
	 * Returns true if and only if the tree is empty.
	 *
	 */
	public boolean empty() {
		return (root == null || !root.isRealNode()); // to be replaced by student code
	}

	/**
	 * public String search(int k)
	 *
	 * Returns the info of an item with key k if it exists in the tree. otherwise,
	 * returns null.
	 */
	public String search(int k) {
		if (empty())
			return null;
		IAVLNode res = search_rec(k, this.root, this.root);
		if (res.getKey() == k)
			return res.getValue();
		return null;

		// to be replaced by student code
	}

	// @pre: AVLTree !empty()

	private IAVLNode search_rec(int k, IAVLNode pointer, IAVLNode pointerForInsert) {
		if (pointer.getKey() < 0)
			return pointerForInsert;
		if (pointer.getKey() == k)
			return pointer;
		if (pointer.getKey() < k)
			return search_rec(k, pointer.getRight(), pointer);
		return search_rec(k, pointer.getLeft(), pointer);

	}

	/**
	 * public int insert(int k, String i)
	 *
	 * Inserts an item with key k and info i to the AVL tree. The tree must remain
	 * valid, i.e. keep its invariants. Returns the number of re-balancing
	 * operations, or 0 if no re-balancing operations were necessary. A
	 * promotion/rotation counts as one re-balance operation, double-rotation is
	 * counted as 2. Returns -1 if an item with key k already exists in the tree.
	 */
	public int insert(int k, String i) {	
		IAVLNode leaf = new AVLNode(k, i);
		return insert_node(leaf);
	}
	private int insert_node(IAVLNode leaf) { 
		int k=leaf.getKey();
		if (empty()) {
			this.size++;
			this.root = leaf;
			return 0;
		}
		IAVLNode pointer = search_rec(k, this.root, this.root);
		if (pointer.getKey() == k)
			return -1;

		leaf.setParent(pointer);
		this.size++;

		if (pointer.getKey() < k)
			pointer.setRight(leaf);
		else
			pointer.setLeft(leaf);

		if (pointer.getHeight() == 1) // the father was a unary node
			return 0;

		if (pointer.getHeight() == 0) {  // the father was a leaf. we need to promote him and all of his parents.
			pointer.setHeight(1);
			return 1 + balance_rec(pointer);
		}
		return -2;
	

	}
	
	
	

	private int balance_rec(IAVLNode pointer) {
		
		if (pointer==null)
			return 0;	
		int pointer_bf = balanceFactor(pointer);
		int k=pointer.getHeight();
		if (pointer_bf == 0 || pointer_bf==1 || pointer_bf==-1) {
			fixHeight(pointer);
			if (pointer.getHeight()!=k)
				return 1 + balance_rec(pointer.getParent());
			else
				return 0;
		}
		if (pointer_bf==2) {
			int leftSon_bf = balanceFactor(pointer.getLeft());
			if(leftSon_bf>-1) 
				LL_rotate(pointer);			
			if(leftSon_bf == -1) {
				pointer = pointer.getLeft();
				RR_rotate(pointer);		
			}
		}
		if (pointer_bf==-2) {
			int rightSon_bf=balanceFactor (pointer.getRight());
			if (rightSon_bf<1)
				RR_rotate(pointer);
			if (rightSon_bf==1) {
				pointer = pointer.getRight();
				LL_rotate(pointer);		
			}
		}
		k=pointer.getHeight();
		fixHeight(pointer);
		if (pointer.getHeight()!=k)
			return 2 + balance_rec(pointer.getParent());
		else
			return 1 + balance_rec(pointer.getParent());
		
	}

	private int balanceFactor(IAVLNode pointer) {
		return pointer.getLeft().getHeight() - pointer.getRight().getHeight();
	}

	private void LL_rotate(IAVLNode pointer) {
		IAVLNode pointer_parent = pointer.getParent(); 
		IAVLNode pointer_son = pointer.getLeft();

		if (pointer_parent != null) {
			if (pointer_parent.getRight() == pointer)
				pointer_parent.setRight(pointer_son);
			else
				pointer_parent.setLeft(pointer_son);
		} else
			this.root = pointer_son;
		pointer_son.setParent(pointer_parent);
		pointer.setLeft(pointer_son.getRight());
		pointer.getLeft().setParent(pointer);

		pointer_son.setRight(pointer);
		pointer.setParent(pointer_son);
	}
	
	
	private void RR_rotate(IAVLNode pointer) {
		IAVLNode pointer_parent = pointer.getParent(); 
		IAVLNode pointer_son = pointer.getRight();

		if (pointer_parent != null) {
			if (pointer_parent.getRight() == pointer)
				pointer_parent.setRight(pointer_son);
			else
				pointer_parent.setLeft(pointer_son);
		} else
			this.root = pointer_son;
		pointer_son.setParent(pointer_parent);
		pointer.setRight(pointer_son.getLeft());
		pointer.getRight().setParent(pointer);

		pointer_son.setLeft(pointer);
		pointer.setParent(pointer_son);
	}

	
	private void fixHeight(IAVLNode pointer) {
		pointer.setHeight(1 + Math.max(pointer.getLeft().getHeight(), pointer.getLeft().getHeight()));
	}
	
	
	/**
	 * public int delete(int k)
	 *
	 * Deletes an item with key k from the binary tree, if it is there. The tree
	 * must remain valid, i.e. keep its invariants. Returns the number of
	 * re-balancing operations, or 0 if no re-balancing operations were necessary. A
	 * promotion/rotation counts as one re-balance operation, double-rotation is
	 * counted as 2. Returns -1 if an item with key k was not found in the tree.
	 */
	public int delete(int k) {
		if (empty())
			return -1;
		IAVLNode pointer = search_rec(k, this.root, this.root);
		if (pointer.getKey()!=k)
			return -1;
		
		if (pointer.getLeft()!=externalLeaf && pointer.getRight()!=externalLeaf) {
			IAVLNode successor=pointer.getRight(),temp,parent=pointer.getParent();
			while (successor.getLeft()!=externalLeaf)
				successor=successor.getLeft();
			switchW(pointer , successor);
		}
		IAVLNode parent=pointer.getParent();
		if (pointer.getLeft()==externalLeaf || pointer.getRight()==externalLeaf) { //the deleted note is a leaf/unary
			if (parent==null) { //the deleted node is the root of the tree
				if (pointer.getLeft()==externalLeaf)  
					root=pointer.getRight();
				else
					root=pointer.getLeft();
				root.setParent(null);
				return 0; //no need of demoting
			}		
			if (pointer.getLeft()==externalLeaf && pointer.getRight()==externalLeaf) { //the deleted node is a leaf
				if (parent.getRight()==pointer)
					parent.setRight(externalLeaf);
				else
					parent.setLeft(externalLeaf);
				return balance_rec(parent);
			}
		
			if (pointer.getLeft()==externalLeaf) { //the deleted node is a unary node with a right son
				if (parent.getRight()==pointer)
					parent.setRight(pointer.getRight()); //we skip the deleted node
				else
					parent.setLeft(pointer.getRight()); 
			}
			else { //the deleted node is a unary node with a left son
				if (parent.getRight()==pointer)
					parent.setRight(pointer.getLeft());
				else
					parent.setLeft(pointer.getLeft());
			}
			return balance_rec(parent);
				
		}
		return -2;
	}
		
	
	// @pre: A must have a two sons 
	// @pre: B must have a parent

	private void switchW(IAVLNode A , IAVLNode B) {
		
		IAVLNode B_left = B.getLeft();
		IAVLNode B_right = B.getRight();
		IAVLNode B_parent = B.getParent();
		int B_height = B.getHeight();
			
		if (A.getParent()==null) 
			root=B;
		else { 
			B.setParent(A.getParent());
			if (A==A.getParent().getRight())
				A.getParent().setRight(B);
			else
				A.getParent().setLeft(B);
		}
		B.setLeft(A.getLeft());
		B.getLeft().setParent(B);
		B.setRight(B.getRight());
		B.getRight().setParent(B);
				
		A.setRight(B_right);
		A.getRight().setParent(A);
		A.setLeft(B_left);
		A.getLeft().setParent(A);
		if (B == B_parent.getRight())
			B_parent.setRight(A);
		else
			B_parent.setLeft(A);
		A.setParent(B_parent);
		
		B.setHeight(A.getHeight());
		A.setHeight(B_height);	
			
	}
	/**
	 * public String min()
	 *
	 * Returns the info of the item with the smallest key in the tree, or null if
	 * the tree is empty.
	 */
	public String min() {
		if (empty())
			return null;
		IAVLNode pointer = this.root;
		while (pointer.getLeft() != externalLeaf)
			pointer = pointer.getLeft();
		return pointer.getValue();
	}

	/**
	 * public String max()
	 *
	 * Returns the info of the item with the largest key in the tree, or null if the
	 * tree is empty.
	 */
	public String max() {
		if (empty())
			return null;
		IAVLNode pointer = this.root;
		while (pointer.getRight() != externalLeaf)
			pointer = pointer.getRight();
		return pointer.getValue();
	}

	/**
	 * public int[] keysToArray()
	 *
	 * Returns a sorted array which contains all keys in the tree, or an empty array
	 * if the tree is empty.
	 */
	public int[] keysToArray() {
		if (empty())
			return new int[0];
		int[] arr = new int[size];
		keysToArray_rec(this.root, arr, 0);
		return arr;

	}

	private int keysToArray_rec(IAVLNode pointer, int[] arr, int i) {
		if (pointer == externalLeaf)
			return i;

		int j = keysToArray_rec(pointer.getLeft(), arr, i);
		arr[j] = pointer.getKey();
		j++;
		return keysToArray_rec(pointer.getRight(), arr, j);

	}

	/**
	 * public String[] infoToArray()
	 *
	 * Returns an array which contains all info in the tree, sorted by their
	 * respective keys, or an empty array if the tree is empty.
	 */
	public String[] infoToArray() {
		if (empty())
			return new String[0];
		String[] arr = new String[size];
		infoToArray_rec(this.root, arr, 0);
		return arr;
	}

	private int infoToArray_rec(IAVLNode pointer, String[] arr, int i) {
		if (pointer == externalLeaf)
			return i;

		int j = infoToArray_rec(pointer.getLeft(), arr, i);
		arr[j] = pointer.getValue();
		j++;
		return infoToArray_rec(pointer.getRight(), arr, j);

	}

	/**
	 * public int size()
	 *
	 * Returns the number of nodes in the tree.
	 */
	public int size() {
		return size; // to be replaced by student code
	}

	/**
	 * public int getRoot()
	 *
	 * Returns the root AVL node, or null if the tree is empty
	 */
	public IAVLNode getRoot() {
		return root;
	}

	/**
	 * public AVLTree[] split(int x)
	 *
	 * splits the tree into 2 trees according to the key x. Returns an array [t1,
	 * t2] with two AVL trees. keys(t1) < x < keys(t2).
	 * 
	 * precondition: search(x) != null (i.e. you can also assume that the tree is
	 * not empty) postcondition: none
	 */

	public AVLTree[] split(int x) {
		return null;
	}

	/**
	 * public int join(IAVLNode x, AVLTree t)
	 *
	 * joins t and x with the tree. Returns the complexity of the operation
	 * (|tree.rank - t.rank| + 1).
	 *
	 * precondition: keys(t) < x < keys() or keys(t) > x > keys(). t/tree might be
	 * empty (rank = -1). postcondition: none
	 */

	public int join(IAVLNode x, AVLTree t) {
		AVLTree big, small,left,right;
		IAVLNode pointer,temp;
		if (t.root.getHeight() > this.root.getHeight()) {
			big = t;
			small = this;
		} else {
			big = this;
			small = t;
		}
		if (t.root.getKey()>this.root.getKey()) {
			left=this;
			right=t; }
		else {
			left=t;
			right=this;
		}
		int k=small.getRoot().getHeight();
		int res = big.root.getHeight() - small.root.getHeight() + 1;
		x.setHeight(0);
		x.setParent(null);
		x.setRight(externalLeaf);
		x.setLeft(externalLeaf);
		if (big.empty()) { //Both trees are actually empty
			this.root = x;
			this.size = 1;
			return res;
		}

		if (small.empty()) { //only need to add x to the bigger tree
			pointer = big.root;
			big.insert_node (x);
			this.root=big.root;
			this.size=big.size;
			return res;
			}
		
		if (res<=2) {
			x.setLeft(left.getRoot());
			x.setRight(right.getRoot());
			x.setHeight(big.root.getHeight()+1);
			x.getRight().setParent(x);
			x.getLeft().setParent(x);
			this.root=x;
		}
		if (right==small) {
			pointer=big.root;
			while (pointer.getHeight()>k)
				pointer=pointer.getRight();
			temp=pointer; //Storing the subtree that we replace in t
			pointer.getParent().setRight(x);
			x.setParent(pointer);
			x.setLeft(temp);
			temp.setParent(x);
			x.setRight(small.getRoot());
			small.getRoot().setParent(x);
			x.setHeight(k+1);
			balance_rec(x);
			this.root=big.getRoot();
		}
		if (left==small) {
			pointer=big.root;
			while (pointer.getHeight()>k)
				pointer=pointer.getLeft();
			temp=pointer; //Storing the subtree that we replace in t
			pointer.getParent().setLeft(x);
			x.setParent(pointer);
			x.setRight(temp);
			temp.setParent(x);
			x.setLeft(small.getRoot());
			small.getRoot().setParent(x);
			x.setHeight(k+1);
			balance_rec(x);
			this.root=big.getRoot();
		}
	this.size=big.size+small.size+1;
	return res;
		}

	
	
	
	
	
	
	
	
	
	

	/**
	 * public interface IAVLNode ! Do not delete or modify this - otherwise all
	 * tests will fail !
	 */
	public interface IAVLNode {
		public int getKey(); // Returns node's key (for virtual node return -1).

		public String getValue(); // Returns node's value [info], for virtual node returns null.

		public void setLeft(IAVLNode node); // Sets left child.

		public IAVLNode getLeft(); // Returns left child, if there is no left child returns null.

		public void setRight(IAVLNode node); // Sets right child.

		public IAVLNode getRight(); // Returns right child, if there is no right child return null.

		public void setParent(IAVLNode node); // Sets parent.

		public IAVLNode getParent(); // Returns the parent, if there is no parent return null.

		public boolean isRealNode(); // Returns True if this is a non-virtual AVL node.

		public void setHeight(int height); // Sets the height of the node.

		public int getHeight(); // Returns the height of the node (-1 for virtual nodes).
	}

	/**
	 * public class AVLNode
	 *
	 * If you wish to implement classes other than AVLTree (for example AVLNode), do
	 * it in this file, not in another file.
	 * 
	 * This class can and MUST be modified (It must implement IAVLNode).
	 */
	public class AVLNode implements IAVLNode {
		private String info;
		private int key;
		private int height;
		private IAVLNode left;
		private IAVLNode right;
		private IAVLNode parent;

		public AVLNode() { // the default value of a new node will be an external leaf
			this.info = null;
			this.key = -1;
			this.height = -1;
			this.left = null;
			this.right = null;
			this.parent = null;
		}

		public AVLNode(int key, String info) { // the default value of a new node will be a leaf
			this.info = info;
			this.key = key;
			this.height = 0;
			this.left = externalLeaf;
			this.right = externalLeaf;
			this.parent = null;
		}

		public int getKey() {
			return key; // to be replaced by student code
		}

		public String getValue() {
			return info; // to be replaced by student code
		}

		public void setLeft(IAVLNode node) {
			left = node;
			return; // to be replaced by student code
		}

		public IAVLNode getLeft() {
			return left; // to be replaced by student code
		}

		public void setRight(IAVLNode node) {
			right = node;
			return; // to be replaced by student code
		}

		public IAVLNode getRight() {
			return right; // to be replaced by student code
		}

		public void setParent(IAVLNode node) {
			parent = node;
			return; // to be replaced by student code
		}

		public IAVLNode getParent() {
			return parent; // to be replaced by student code
		}

		public boolean isRealNode() {
			return (key >= 0); // to be replaced by student code
		}

		public void setHeight(int height) {
			this.height = height; // to be replaced by student code
		}

		public int getHeight() {
			return height; // to be replaced by student code
		}

	}

}
