package com.code.pair.yazan.paircode.dsa.rope;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.code.pair.yazan.paircode.dsa.rope.Util.*;


final class Node {
    static final int MIN_LEAF = 511;
    static final int MAX_LEAF = 1024;

    static final int MIN_CHILDREN = 4;
    static final int MAX_CHILDREN = 8;

    private NodeBody nodeBody;

    Node(NodeBody nodeBody) {
        this.nodeBody = nodeBody;
    }

    static Node fromString(String s) {
        Rope.Builder builder = new Rope.Builder();
        builder.pushString(s);
        return builder.getRootNode();
    }

    static Node fromStringPiece(String piece) {
        if (piece.length() > MAX_LEAF) {
            throw new IllegalArgumentException("String piece exceeds MAX_LEAF limit");
        }

        NodeBody nodeBody = new NodeBody.Builder()
                .height(0)
                .length(piece.length())
                .newlineCount(Util.countOccurrence(piece, NEW_LINE))
                .val(new NodeBody.Leaf(piece))
                .build();

        return new Node(nodeBody);
    }

    static Node fromPieces(List<Node> pieces) {
        if (pieces.size() < 2 || pieces.size() > MAX_CHILDREN) {
            throw new IllegalArgumentException("Nodes exceeds MAX_CHILDREN limit");
        }

        int height = pieces.getFirst().getHeight() + 1;
        int length = pieces.stream().mapToInt(Node::getLength).sum();
        int newlineCount = pieces.stream().mapToInt(Node::getNewlineCount).sum();
        NodeBody nodeBody = new NodeBody.Builder()
                .height(height)
                .length(length)
                .newlineCount(newlineCount)
                .val(new NodeBody.Internal(pieces))
                .build();

        return new Node(nodeBody);
    }

    static Node mergeNodes(List<Node> children1, List<Node> children2) {
        int totalChildren = children1.size() + children2.size();
        List<Node> children =
                Stream.concat(children1.stream(), children2.stream()).collect(Collectors.toList());
        if (totalChildren <= MAX_CHILDREN) {
            return Node.fromPieces(children);
        } else {
            // Splitting at midpoint is also an option
            int splitPoint = Math.min(MAX_CHILDREN, totalChildren - MIN_CHILDREN);
            List<Node> left = children.subList(0, splitPoint);
            List<Node> right = children.subList(splitPoint, children.size());
            List<Node> parent = Arrays.asList(Node.fromPieces(left), Node.fromPieces(right));
            return Node.fromPieces(parent);
        }
    }

    static Node mergeLeaves(Node rope1, Node rope2) {
        if (!rope1.isLeaf() || !rope2.isLeaf()) {
            throw new IllegalArgumentException("mergeLeaves() called with non-leaf node");
        }
        if (rope1.getLength() >= MIN_LEAF && rope2.getLength() >= MIN_LEAF) {
            return Node.fromPieces(Arrays.asList(rope1, rope2));
        }
        String rope1String = rope1.getLeaf();
        String rope2String = rope2.getLeaf();
        String ropeString = rope1String + rope2String;
        if (ropeString.length() <= MAX_LEAF) {
            return Node.fromStringPiece(ropeString);
        } else {
            int splitPoint = findLeafSplitForMerge(ropeString);
            String leftString = ropeString.substring(0, splitPoint);
            String rightString = ropeString.substring(splitPoint);
            Node leftNode = Node.fromStringPiece(leftString);
            Node rightNode = Node.fromStringPiece(rightString);
            return Node.fromPieces(Arrays.asList(leftNode, rightNode));
        }
    }

    static Node concat(Node rope1, Node rope2) {
        int rope1Height = rope1.getHeight();
        int rope2Height = rope2.getHeight();

        switch (compare(rope1Height, rope2Height)) {
            case LESS: {
                List<Node> rope2Children = rope2.getChildren();
                if (rope1Height == rope2Height - 1 && rope1.isValidNode()) {
                    return mergeNodes(Collections.singletonList(rope1), rope2Children);
                }
                Node newRope = concat(rope1, rope2Children.getFirst());
                List<Node> rope2ChildrenSubList = rope2Children.subList(1, rope2Children.size());
                if (newRope.getHeight() == rope2Height - 1) {
                    return mergeNodes(Collections.singletonList(newRope), rope2ChildrenSubList);
                } else {
                    return mergeNodes(newRope.getChildren(), rope2ChildrenSubList);
                }
            }

            case EQUAL: {
                if (rope1.isValidNode() && rope2.isValidNode()) {
                    return Node.fromPieces(Arrays.asList(rope1, rope2));
                }
                if (rope1Height == 0) {
                    return mergeLeaves(rope1, rope2);
                }
                return mergeNodes(rope1.getChildren(), rope2.getChildren());
            }

            case GREATER: {
                List<Node> rope1Children = rope1.getChildren();
                if (rope2Height == rope1Height - 1) {
                    return Node.mergeNodes(rope1Children, Collections.singletonList(rope2));
                }
                int lastChildIndex = rope1Children.size() - 1;
                Node newRope = Node.concat(rope1Children.get(lastChildIndex), rope2);
                List<Node> rope1ChildrenSubList = rope1Children.subList(1, lastChildIndex + 1);
                if (newRope.getHeight() == rope1Height - 1) {
                    return mergeNodes(rope1ChildrenSubList, Collections.singletonList(newRope));
                } else {
                    return mergeNodes(rope1ChildrenSubList, newRope.getChildren());
                }
            }

            default:
                throw new IllegalStateException("unreachable state");
        }
    }

    static ChildIndexOffset getChildIndexOffset(List<Node> children, int start, int end) {
        int offset = 0;
        for (int i = 0; i < children.size(); i++) {
            int nextOffset = offset + children.get(i).getLength();
            if (nextOffset >= start) {
                if (nextOffset >= end) {
                    return new ChildIndexOffset(i, offset);
                } else {
                    return null;
                }
            }
            offset = nextOffset;
        }
        return null;
    }

    Node mergeLeaf(Node rope) {
        if (!this.isLeaf() || !rope.isLeaf()) {
            throw new UnsupportedOperationException("mergeLeaf() called on/with non-leaf node");
        }
        if (this.getLength() >= MIN_LEAF && rope.getLength() >= MIN_LEAF) {
            return Node.fromPieces(Arrays.asList(this, rope));
        }
        String thisString = this.getLeaf();
        String ropeString = rope.getLeaf();
        String mergedString = thisString + ropeString;
        if (mergedString.length() <= MAX_LEAF) {
            return Node.fromStringPiece(mergedString);
        } else {
            int splitPoint = findLeafSplitForMerge(mergedString);
            String leftString = mergedString.substring(0, splitPoint);
            String rightString = mergedString.substring(splitPoint);
            Node leftNode = Node.fromStringPiece(leftString);
            Node rightNode = Node.fromStringPiece(rightString);
            return Node.fromPieces(Arrays.asList(leftNode, rightNode));
        }
    }

    Node concat(Node anotherRope) {
        int rope1Height = this.getHeight();
        int rope2Height = anotherRope.getHeight();

        switch (compare(rope1Height, rope2Height)) {
            case LESS: {
                List<Node> rope2Children = anotherRope.getChildren();
                if (rope1Height == rope2Height - 1 && this.isValidNode()) {
                    return mergeNodes(Collections.singletonList(this), rope2Children);
                }
                Node newRope = concat(this, rope2Children.getFirst());
                List<Node> rope2ChildrenSubList = rope2Children.subList(1, rope2Children.size());
                if (newRope.getHeight() == rope2Height - 1) {
                    return mergeNodes(Collections.singletonList(newRope), rope2ChildrenSubList);
                } else {
                    return mergeNodes(newRope.getChildren(), rope2ChildrenSubList);
                }
            }

            case EQUAL: {
                if (this.isValidNode() && anotherRope.isValidNode()) {
                    return Node.fromPieces(Arrays.asList(this, anotherRope));
                }
                if (rope1Height == 0) {
                    return mergeLeaves(this, anotherRope);
                }
                return mergeNodes(this.getChildren(), anotherRope.getChildren());
            }

            case GREATER: {
                List<Node> rope1Children = this.getChildren();
                if (rope2Height == rope1Height - 1) {
                    return Node.mergeNodes(rope1Children, Collections.singletonList(anotherRope));
                }
                int lastChildIndex = rope1Children.size() - 1;
                Node newRope = Node.concat(rope1Children.get(lastChildIndex), anotherRope);
                List<Node> rope1ChildrenSubList = rope1Children.subList(1, lastChildIndex + 1);
                if (newRope.getHeight() == rope1Height - 1) {
                    return mergeNodes(rope1ChildrenSubList, Collections.singletonList(newRope));
                } else {
                    return mergeNodes(rope1ChildrenSubList, newRope.getChildren());
                }
            }

            default:
                throw new IllegalStateException("unreachable state");
        }
    }

    void subsequence(Rope.Builder builder, int start, int end) {
        if (start == 0 && this.getLength() == end) {
            builder.push(this);
            return;
        }
        NodeBody.NodeVal val = this.nodeBody.val();
        if (val instanceof NodeBody.Leaf) {
            String leafString = getLeaf();
            builder.pushShortString(leafString.substring(start, end));
        } else if (val instanceof NodeBody.Internal) {
            int offset = 0;
            List<Node> children = getChildren();
            for (Node child : children) {
                if (end <= offset) {
                    break;
                }
                if (offset + child.getLength() > start) {
                    int childStart = Math.max(offset, start) - offset;
                    int childEnd = Math.min(child.getLength(), end - offset);
                    child.subsequence(builder, childStart, childEnd);
                }
                offset += child.getLength();
            }
        } else {
            throw new IllegalStateException("unreachable state");
        }
    }

    private void replace(int start, int end, Node node) {
        NodeBody.NodeVal val = node.nodeBody.val();
        String s = ((String) val.get());
        if (s.length() < MIN_LEAF) {
            replaceString(start, end, s, false);
            return;
        }
        Rope.Builder builder = new Rope.Builder();
        this.subsequence(builder, 0, start);
        builder.push(node);
        this.subsequence(builder, end, this.getLength());
        this.nodeBody = builder.getRootNode().nodeBody;
    }

    void replaceString(int start, int end, String s, boolean tryReplaceInplace) {
        if (s.length() < MIN_LEAF && tryReplaceInplace && tryReplaceString(start, end, s)) {
            return;
        }
        Rope.Builder builder = new Rope.Builder();
        this.subsequence(builder, 0, start);
        builder.pushString(s);
        this.subsequence(builder, end, this.getLength());
        this.nodeBody = builder.getRootNode().nodeBody;
    }

    private boolean tryReplaceLeafString(int start, int end, String s) {
        if (!this.isLeaf()) {
            throw new IllegalArgumentException("tryReplaceLeafString() called with internal node");
        }
        int newLength = this.getLength() + s.length();
        if (newLength < MIN_LEAF + (end - start) || newLength > MAX_LEAF + (end - start)) {
            return false;
        }
        String leafString = this.getLeaf();
        String newString =
                leafString.substring(0, start) + s + leafString.substring(end, leafString.length());
        Node newNode = Node.fromStringPiece(newString);
        this.nodeBody = newNode.nodeBody;
        return true;
    }

    private boolean tryReplaceString(int start, int end, String newString) {
        if (this.isLeaf()) {
            return tryReplaceLeafString(start, end, newString);
        }
        boolean success = false;
        if (this.nodeBody.val() instanceof NodeBody.Internal) {
            List<Node> children = getChildren();
            ChildIndexOffset childIndexOffset = getChildIndexOffset(children, start, end);
            if (childIndexOffset != null) {
                int index = childIndexOffset.index;
                int offset = childIndexOffset.offset;
                int oldNewLineCount = children.get(index).getNewlineCount();
                success = children.get(index).tryReplaceString(start - offset, end - offset, newString);
                if (success) {
                    this.nodeBody = new NodeBody.Builder()
                            .length(this.getLength() - (end - start) + newString.length())
                            .newlineCount(
                                    this.getNewlineCount() - oldNewLineCount + children.get(index).getNewlineCount())
                            .height(this.getHeight())
                            .val(this.nodeBody.val())
                            .build();
                }
            }
        } else if (this.nodeBody.val() instanceof NodeBody.Leaf) {
            throw new IllegalStateException("height and node val type are inconsistent");
        }
        return success;
    }

    void toStringRec(StringBuilder sb) {
        if (this.nodeBody.val() instanceof NodeBody.Leaf) {
            String val = this.getLeaf();
            sb.append(val);
        } else if (this.nodeBody.val() instanceof NodeBody.Internal) {
            List<Node> children = this.getChildren();
            for (Node child : children) {
                child.toStringRec(sb);
            }
        } else {
            throw new IllegalStateException("unreachable state");
        }
    }

    String getString() {
        if (this.getHeight() == 0) {
            if (nodeBody.val() instanceof NodeBody.Leaf) {
                return this.getLeaf();
            } else {
                throw new IllegalStateException("height and node type inconsistent");
            }
        }
        StringBuilder sb = new StringBuilder();
        this.toStringRec(sb);
        return sb.toString();
    }

    int getHeight() {
        return this.nodeBody.height();
    }

    int getLength() {
        return this.nodeBody.length();
    }

    int getNewlineCount() {
        return this.nodeBody.newlineCount();
    }

    NodeBody getNodeBody() {
        return this.nodeBody;
    }

    private boolean isLeaf() {
        return this.getHeight() == 0;
    }

    List<Node> getChildren() {
        if (this.nodeBody.val() instanceof NodeBody.Leaf) {
            throw new UnsupportedOperationException("getChildren() called on leaf");
        }
        @SuppressWarnings("unchecked")
        List<Node> nodes = (List<Node>) this.nodeBody.val().get();
        return nodes;
    }

    private String getLeaf() {
        if (this.nodeBody.val() instanceof NodeBody.Internal) {
            throw new UnsupportedOperationException("getLeaf() called on internal node");
        }
        return (String) this.nodeBody.val().get();
    }

    private boolean isValidNode() {
        if (this.nodeBody.val() instanceof NodeBody.Leaf) {
            return this.getLeaf().length() >= MIN_LEAF;
        } else if (this.nodeBody.val() instanceof NodeBody.Internal) {
            List<Node> nodes = this.getChildren();
            return nodes.stream().allMatch((node) -> node.getLength() >= MIN_CHILDREN);
        } else {
            throw new UnsupportedOperationException("Unreachable state");
        }
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Node)) {
            return false;
        }
        Node otherNode = (Node) o;
        return this.nodeBody.equals(otherNode.nodeBody);
    }

    @Override public int hashCode() {
        int hash = 17;
        hash += 31 * this.nodeBody.hashCode() + hash;
        return hash;
    }

    @Override public String toString() {
        return "Node: {" + "\n\t" + this.nodeBody.toString() + "\n}";
    }

    static class ChildIndexOffset {
        int index;
        int offset;

        ChildIndexOffset(int index, int offset) {
            this.index = index;
            this.offset = offset;
        }

        @Override public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof ChildIndexOffset)) {
                return false;
            }
            ChildIndexOffset cio = (ChildIndexOffset) obj;
            return this.index == cio.index
                    && this.offset == cio.offset;
        }

        @Override public int hashCode() {
            int hash = 17;
            hash += 31 * this.index + hash;
            hash += 31 * this.offset + hash;
            return hash;
        }

        @Override public String toString() {
            return "ChildIndexOffset: {"
                    + "\n\tindex: " + this.index
                    + "\n\toffset: " + this.offset
                    + "\n}";
        }
    }
}