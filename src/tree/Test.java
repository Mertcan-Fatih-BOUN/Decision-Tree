package tree;

import java.io.IOException;

/**
 * Created by Fatih on 12-May-16.
 */
public class Test {
    public static void main(String[] args) throws IOException {
        TreeNode root = new TreeNode();
        TreeNode node1 = new TreeNode();
        node1.ids = new long[]{154,432,454};
        TreeNode node2 = new TreeNode();
        node2.ids = new long[]{24252,546,454};
        TreeNode node3 = new TreeNode();
        node3.ids = new long[]{154,546,524};
        TreeNode node4 = new TreeNode();
        node4.ids = new long[]{154,546,454};
        TreeNode node5 = new TreeNode();
        node5.ids = new long[]{154,546,4234};
        TreeNode node6 = new TreeNode();
        node6.ids = new long[]{3224,545,454};
        TreeNode node7 = new TreeNode();
        node7.ids = new long[]{3423,546,454};
        TreeNode node8 = new TreeNode();
        node8.ids = new long[]{154,4322,454};

        root.leftTreeNode = node1;
        root.rightTreeNode = node2;
        root.rightTreeNode.leftTreeNode = node3;
        root.rightTreeNode.rightTreeNode = node4;
        root.rightTreeNode.leftTreeNode.rightTreeNode = node5;
        root.rightTreeNode.leftTreeNode.leftTreeNode = node6;
        root.rightTreeNode.rightTreeNode.rightTreeNode = node7;
        root.rightTreeNode.rightTreeNode.leftTreeNode = node8;
        root.printToFile("deneme.png");
    }
}
