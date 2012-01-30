package com.elaine.search.client.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

public class TreeNode<T> implements Serializable
{
	public T data;
	public List<TreeNode<T>> children;

	public TreeNode() 
	{
		super();
		children = new ArrayList<TreeNode<T>>();
	}

	public TreeNode(T data) 
	{
		this();
		setData(data);
	}

	public List<TreeNode<T>> getChildren() 
	{
		return this.children;
	}

	public int getNumberOfChildren() 
	{
		return getChildren().size();
	}

	public boolean hasChildren() 
	{
		return (getNumberOfChildren() > 0);
	}

	public void setChildren(List<TreeNode<T>> children) 
	{
		this.children = children;
	}

	public void addChild(TreeNode<T> child) 
	{
		children.add(child);
	}

	public void addChildAt(int index, TreeNode<T> child) throws IndexOutOfBoundsException 
	{
		children.add(index, child);
	}

	public void removeChildren() 
	{
		this.children = new ArrayList<TreeNode<T>>();
	}

	public void removeChildAt(int index) throws IndexOutOfBoundsException 
	{
		children.remove(index);
	}

	public TreeNode<T> getChildAt(int index) throws IndexOutOfBoundsException 
	{
		return children.get(index);
	}

	public T getData() 
	{
		return this.data;
	}

	public void setData(T data) 
	{
		this.data = data;
	}

	public String toString() 
	{
		return getData().toString();
	}

	public boolean equals(TreeNode<T> node) 
	{
		return node.getData().equals(getData());
	}

	public int hashCode() 
	{
		return getData().hashCode();
	}

	public Enumeration<TreeNode<?>> depthFirstEnumeration()
	{
		return new PostOrderEnumeration(this);
	}

	public Enumeration<TreeNode<?>> postOrderEnumeration()
	{
		return new PostOrderEnumeration(this);
	}

	public Enumeration<TreeNode<?>> preOrderEnumeration()
	{
		return new PreOrderEnumeration(this);
	}

	static class PostOrderEnumeration implements Enumeration<TreeNode<?>>
	{
		Stack<TreeNode<?>> nodes = new Stack<TreeNode<?>>();
		Stack childrenEnums = new Stack();

		PostOrderEnumeration(TreeNode<?> node)
		{
			nodes.push(node);
			childrenEnums.push(Collections.enumeration(node.getChildren()));
		}

		public boolean hasMoreElements()
		{
			return !nodes.isEmpty();
		}

		public TreeNode<?> nextElement()
		{
			if (nodes.isEmpty())
				throw new NoSuchElementException("No more elements left!");

			Enumeration children = (Enumeration)childrenEnums.peek();
			return traverse(children);
		}

		private TreeNode<?> traverse(Enumeration children)
		{
			if (children.hasMoreElements())
			{
				TreeNode<?> node = (TreeNode<?>)children.nextElement();
				nodes.push(node);

				Enumeration newChildren = Collections.enumeration(node.getChildren());
				childrenEnums.push(newChildren);

				return traverse(newChildren);
			}
			else
			{
				childrenEnums.pop();
				TreeNode<?> next = nodes.peek();
				nodes.pop();
				return next;
			}
		}
	}

	static class PreOrderEnumeration implements Enumeration<TreeNode<?>>
	{
		TreeNode<?> next;

		Stack childrenEnums = new Stack();

		PreOrderEnumeration(TreeNode<?> node)
		{
			next = node;
			childrenEnums.push(Collections.enumeration(node.getChildren()));
		}

		public boolean hasMoreElements()
		{
			return next != null;
		}

		public TreeNode<?> nextElement()
		{
			if (next == null)
				throw new NoSuchElementException("No more elements left.");

			TreeNode<?> current = next;

			Enumeration children = (Enumeration) childrenEnums.peek();

			// Retrieves the next element.
			next = traverse(children);

			return current;
		}

		private TreeNode<?> traverse(Enumeration children)
		{
			// If more children are available step down.
			if (children.hasMoreElements())
			{
				TreeNode<?> child = (TreeNode<?>) children.nextElement();
				childrenEnums.push(Collections.enumeration(child.getChildren()));

				return child;
			}

			// If no children are left, we return to a higher level.
			childrenEnums.pop();

			// If there are no more levels left, there is no next
			// element to return.
			if (childrenEnums.isEmpty())
				return null;
			else
			{
				return traverse((Enumeration) childrenEnums.peek());
			}
		}
	}


}