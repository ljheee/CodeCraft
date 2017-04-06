package com.cacheserverdeploy.deploy;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;


public class AF {

	boolean isAF = true;
	Graph graph;
	int n;// 节点的总个数
	int start, end;
	Stack<Integer> theStack;
	StringBuilder output = new StringBuilder();

	private ArrayList<Integer> tempList;


	public AF(Graph graph, int start, int end) {
		this.graph = graph;
		this.start = start;
		this.end = end;
	}

	public String getResult() {
		n = StaticValues.MAX_VERTS;// 给节点的总个数进行赋值
		theStack = new Stack<Integer>();

		if (!isConnectable(start, end)) {
			isAF = false;
		} else {
			for (int j = 0; j < n; j++) {
				tempList = new ArrayList<Integer>();
				for (int i = 0; i < n; i++) {
					tempList.add(0);
				}
				graph.getVertexList()[j].setAllVisitedList(tempList);
			}

			isAF = af(start, end);
		}
		return new String(output);
	}

	private boolean af(int start, int end) {
		graph.getVertexList()[start].setWasVisited(true); // mark it
		theStack.push(start); // push it

		int realLinkCount = 0;
		while (!theStack.isEmpty()) {
			int v = getAdjUnvisitedVertex(theStack.peek());
			if (v == -1) // if no such vertex,
			{
				tempList = new ArrayList<Integer>();
				for (int j = 0; j < n; j++) {
					tempList.add(0);
				}
				graph.getVertexList()[theStack.peek()]
						.setAllVisitedList(tempList);// 把栈顶节点访问过的节点链表清空
				theStack.pop();
			} else { // if it exists,
				theStack.push(v); // push it
			}

			if (!theStack.isEmpty() && end == theStack.peek()) {
				realLinkCount++;
				graph.getVertexList()[end].setWasVisited(false); // mark it

				List<Integer> list = new LinkedList<Integer>();
				for (Integer integer : theStack) {
					list.add(integer);// 加入list是为了后面算实际走的流量

					// 整合的源点和终点不输出
					if (integer != start && integer != end) {
						// 消费点变成原来那种形式
						if (integer >= graph.getNetNodeCount()) {
							output.append(integer - graph.getNetNodeCount()
									+ " ");
						} else {
							output.append(integer + " ");
						}

					}
				}

				// 算实际流量
				int minFlow = 100000;
				for (int i = 0; i < list.size() - 1; i++) {
					int flow = graph.getAdjMat()[list.get(i)][list.get(i + 1)];
					if (flow < minFlow) {
						minFlow = flow;
					}
				}

				output.append(minFlow + "\r\n");
				theStack.pop();
			}

		}
		output.insert(0, realLinkCount + "\r\n\r\n");

		return isAF;
	}

	// 判断连个节点是否能连通
	private boolean isConnectable(int start, int end) {
		ArrayList<Integer> queue = new ArrayList<Integer>();
		ArrayList<Integer> visited = new ArrayList<Integer>();
		queue.add(start);
		while (!queue.isEmpty()) {
			for (int j = 0; j < n; j++) {
				if (graph.getAdjMat()[start][j] > 0 && !visited.contains(j)) {
					queue.add(j);
				}
			}
			if (queue.contains(end)) {
				return true;
			} else {
				visited.add(queue.get(0));
				queue.remove(0);
				if (!queue.isEmpty()) {
					start = queue.get(0);
				}
			}
		}
		return false;
	}

	// 与节点v相邻，并且这个节点没有被访问到，并且这个节点不在栈中
	public int getAdjUnvisitedVertex(int v) {
		ArrayList<Integer> arrayList = graph.getVertexList()[v]
				.getAllVisitedList();
		for (int j = 0; j < n; j++) {
			if (graph.getAdjMat()[v][j] > 0 && arrayList.get(j) == 0
					&& !theStack.contains(j)) {
				graph.getVertexList()[v].setVisited(j);
				return j;
			}
		}
		return -1;
	}

}
