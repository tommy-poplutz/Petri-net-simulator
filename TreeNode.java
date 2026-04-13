import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class TreeNode {
	private TreeNode parent;
	private ArrayList<TreeNode> children = new ArrayList<TreeNode>();
	private HashMap<String,Place> place;
	public TreeNode(TreeNode parent, HashMap<String, Place> data) {
		super();
		this.parent = parent;
		this.place = data;
	}
	public TreeNode getParent() {
		return parent;
	}
	public HashMap<String, Place> getData() {
		return place;
	}
	public ArrayList<TreeNode> getChildren() {
		return children;
	}
	public void addChild(TreeNode child) {
		this.children.add(child);
	}
	
	public String toString() {
		return toString(0);
	}
	
	private String toString(int depth) {
		StringBuilder sp = new StringBuilder();
		Collection<Place> values = place.values();
		Iterator<Place> iter = values.iterator();
		for (int i = 0;i< depth;i++) {
			sp.append("\t");
		}
		sp.append("Data:");
		sp.append("\n");
		while(iter.hasNext()) {
			for (int i = 0;i< depth;i++) {
				sp.append("\t");
			}
			sp.append(iter.next().toString());
		}
		sp.append("\n");
		for (TreeNode child : children) {
			sp.append(child.toString(depth+1));
			sp.append("\n");
		}
		
		return sp.toString();
	}
	
}
