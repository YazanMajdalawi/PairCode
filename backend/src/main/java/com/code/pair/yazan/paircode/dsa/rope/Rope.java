package com.code.pair.yazan.paircode.dsa.rope;

import java.util.ArrayList;
import java.util.List;

import static com.code.pair.yazan.paircode.dsa.rope.Node.*;
import static com.code.pair.yazan.paircode.dsa.rope.Util.*;

public final class Rope {
    private Node root;
    private int start;
    private int length;

    private Rope(Node root, int start, int length) {
        this.root = root;
        this.start = start;
        this.length = length;
    }

    public static Rope from(String s) {
        return Rope.fromNode(Node.fromString(s));
    }

    private static Rope fromNode(Node node) {
        return new Rope(node, 0, node.getLength());
    }

    private String extractString() {
        StringBuilder sb = new StringBuilder();
        this.toStringRec(sb);
        return sb.toString();
    }

    private boolean isFull() {
        return this.start == 0 && this.length == this.root.getLength();
    }

    public int length() {
        return this.length;
    }

    public Rope slice(int start, int end) {
        if (start < this.start || end > this.start + this.length) {
            throw new IllegalArgumentException(
                    "[" + start + ", " + end + ") interval is out of bounds for current rope");
        }
        Node root = this.root;
        start += this.start;
        end += this.start - 1;
        while (root.getHeight() > 0) {
            Node.ChildIndexOffset indexOffset = getChildIndexOffset(root.getChildren(), start, end);
            if (indexOffset != null) {
                int index = indexOffset.index;
                int offset = indexOffset.offset;
                root = root.getChildren().get(index);
                start -= offset;
                end -= offset;
            } else {
                break;
            }
        }
        return Rope.from(root.getString().substring(start, end - start + 1));
    }

    public Rope replace(int start, int end, String newString) {
        if (start < this.start || end > this.start + this.length) {
            throw new IllegalArgumentException(
                    "[" + start + ", " + end + ") interval is out of bounds for current rope");
        }
        if (this.isFull()) {
            Node newRoot = new Node(this.root.getNodeBody());
            newRoot.replaceString(start, end, newString, false);
            return Rope.fromNode(newRoot);
        } else {
            Builder builder = new Builder();
            this.root.subsequence(builder, this.start, this.start + start);
            builder.pushString(newString);
            this.root.subsequence(builder, this.start + end, this.start + this.length);
            return builder.build();
        }
    }

    public Rope concat(Rope anotherRope) {
        if (anotherRope == null) {
            throw new IllegalArgumentException("Attempting to concat this rope with null");
        }
        Node newRoot = new Node(this.root.getNodeBody());
        return Rope.fromNode(newRoot.concat(anotherRope.root));
    }

    private void toStringRec(StringBuilder sb) {
        this.root.toStringRec(sb);
    }

    private Rope normalize() {
        if (this.isFull()) {
            return this;
        } else {
            return new Builder()
                    .pushRope(this)
                    .build();
        }
    }

    @Override public String toString() {
        if (this.isFull()) {
            return this.root.getString();
        } else {
            return this.extractString();
        }
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Rope)) {
            return false;
        }
        Rope otherRope = (Rope) obj;
        return this.length == otherRope.length
                && this.start == otherRope.start
                && this.root.equals(otherRope.root);
    }

    @Override public int hashCode() {
        int hash = 17;
        hash += 31 * this.length + hash;
        hash += 31 * this.start + hash;
        hash += 31 * this.root.hashCode() + hash;
        return hash;
    }

    public static class Builder {
        private Node root;
        public Builder pushRope(Rope rope) {
            rope.root.subsequence(this, rope.start, rope.start + rope.length);
            return this;
        }
        public Builder pushString(String s) {
            if (s.length() <= MAX_LEAF) {
                if (!s.isEmpty()) {
                    return pushShortString(s);
                }
            }
            List<List<Node>> stack = new ArrayList<>();
            while (!s.isEmpty()) {
                int splitPoint = s.length() > MAX_LEAF ? findLeafSplitForBulk(s) : s.length();
                Node newNode = Node.fromStringPiece(s.substring(0, splitPoint));
                s = s.substring(splitPoint);
                while (true) {
                    Node finalNewNode = newNode;
                    if (stack.size() == 0 ||
                            stack.get(stack.size() - 1)
                                    .stream()
                                    .allMatch(node -> node.getHeight() != finalNewNode.getHeight())) {
                        stack.add(new ArrayList<>());
                    }
                    stack.get(stack.size() - 1).add(newNode);
                    if (stack.get(stack.size() - 1).size() < MAX_CHILDREN) {
                        break;
                    }
                    newNode = Node.fromPieces(stack.remove(stack.size() - 1));
                }
            }
            for (List<Node> list : stack) {
                for (Node node : list) {
                    push(node);
                }
            }
            return this;
        }
        Builder push(Node node) {
            if (this.root == null) {
                this.root = node;
            } else {
                this.root = Node.concat(root, node);
            }
            return this;
        }
        Builder pushShortString(String s) {
            if (s.length() > MAX_LEAF) {
                throw new IllegalArgumentException("string exceeds MAX_LEAF limit");
            }
            return push(Node.fromStringPiece(s));
        }
        Node getRootNode() {
            if (this.root == null) {
                this.root = Node.fromStringPiece("");
            }
            return this.root;
        }
        public Rope build() {
            return Rope.fromNode(getRootNode());
        }
    }
}