package com.cacheserverdeploy.deploy;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Deploy {
	private static boolean[] vis = new boolean[StaticValues.N];// 是否压入栈中
	private static int[] p = new int[StaticValues.N];// 上一条弧
	private static int[] a = new int[StaticValues.N];// 可改进量
	private static int[] d = new int[StaticValues.N];// 距离
	private static ArrayList<ArrayList<Integer>> g = new ArrayList<ArrayList<Integer>>();
	private static List<Edge> edges = new LinkedList<Edge>();

	static int maxflow = 0;
	static int mincost = 0;

	private static List<Edge> pathList = new ArrayList<Edge>();

	public static void init() {
		for (int i = 0; i < StaticValues.N; i++) {
			vis[i] = false;
			p[i] = 0;
			a[i] = 0;
			d[i] = 0;
		}
		g.clear();
		for (int i = 0; i < StaticValues.N; i++) {
			g.add(new ArrayList<Integer>());
		}
		edges.clear();
	}

	public static void addedge(int from, int to, int cap, int cost) {
		Edge temp1 = new Edge(from, to, 0, cap, cost);
		Edge temp2 = new Edge(to, from, 0, 0, -cost);// 允许反向增广
		edges.add(temp1);
		edges.add(temp2);
		int len = edges.size();

		g.get(from).add(len - 2);// 加入的元素就是下标，edges通过该下标可以在里面找到所有以from起始的边
		g.get(to).add(len - 1);// 加入的元素就是下标，edges通过该下标可以在里面找到所有以to起始的边

	}

	public static boolean bellmanford(int s, int t, FlowAndCost fc) {
		// 先把所有变量都初始化一下
		for (int i = 0; i < StaticValues.N; i++)
			d[i] = StaticValues.inf;
		d[s] = 0;
		for (int i = 0; i < p.length; i++) {
			p[i] = -1;
		}
		p[s] = -1;
		a[s] = StaticValues.inf;
		Queue<Integer> que = new LinkedList<Integer>();
		que.offer(s);
		vis[s] = true;// s点放入队列了
		while (!que.isEmpty()) {
			int u = que.poll();
			vis[u] = false;// 取出之后，u点就不在队列了
			Edge e = null;
			for (int i = 0; i < g.get(u).size(); i++) {
				e = edges.get(g.get(u).get(i));
				// 进行松弛，寻找最短路径也就是最小费用
				// 满足增广，且可变短
				if (e.cap > e.flow && d[e.to] > d[u] + e.cost) {
					d[e.to] = d[u] + e.cost;
					p[e.to] = g.get(u).get(i);
					a[e.to] = Math.min(a[u], e.cap - e.flow);
					if (!vis[e.to]) {
						que.offer(e.to);
						vis[e.to] = true;
					}

				}
				// if (e.flow > 0) {
				// String path = e.from + "->" + e.to;
				// pathMap.put(path, e.flow);
				// }
			}
		}
		if (d[t] == StaticValues.inf)
			return false;
		fc.flow += a[t];
		fc.cost += d[t] * a[t];
		for (int i = t; i != s; i = edges.get(p[i]).from) {
			edges.get(p[i]).flow += a[t];
			edges.get(p[i] ^ 1).flow -= a[t];
		}

		return true;
	}

	public static FlowAndCost minCostMaxFlow(int s, int t) {
		FlowAndCost fc = new FlowAndCost();
		while (bellmanford(s, t, fc))
			continue;

		return fc;
	}

	/**
	 * 整合服务器，到一个源点
	 */
	public static void MixServerNet(int[] servers, int sourceId) {
		for (int i = 0; i < servers.length; i++) {
			addedge(sourceId, servers[i], Integer.MAX_VALUE, 0);
			addedge(servers[i], sourceId, Integer.MAX_VALUE, 0);
		}
	}

	/**
	 * 你需要完成的入口 <功能详细描述>
	 * 
	 * @param graphContent
	 *            用例信息文件
	 * @return [参数说明] 输出结果信息
	 * @see [类、类#方法、类#成员]
	 */
	public static String[] deployServer(String[] reads) {
		/** do your work here **/
		init();
		// 消费总需求
		int consumerSum = FlowUtil.getFlow(reads);

		String[] firstLine = reads[0].split(" ");
		int netNodeCount = Integer.parseInt(firstLine[0]);// 网络节点数
		int linkCount = Integer.parseInt(firstLine[1]);// 链路数
		int consumerNodeCount = Integer.parseInt(firstLine[2]);// 消费节点数
		int price = Integer.parseInt(reads[2].split(" ")[0]);// 部署一个服务器的成本

		StaticValues.MAX_VERTS = netNodeCount + consumerNodeCount + 2;

		// 获取服务器节点
		List<Integer> serverNodes = new LinkedList<Integer>();

		// 网络节点间的边
		for (int i = 4; i < 4 + linkCount; i++) {
			String[] netLines = reads[i].split(" ");
			int u = Integer.parseInt(netLines[0]);
			int v = Integer.parseInt(netLines[1]);
			int cap = Integer.parseInt(netLines[2]);
			int cos = Integer.parseInt(netLines[3]);
			addedge(u, v, cap, cos);
			addedge(v, u, cap, cos);
		}
		// 和消费节点间的边
		for (int i = 5 + linkCount; i < reads.length; i++) {
			String[] netLines = reads[i].split(" ");
			int u = Integer.parseInt(netLines[1]);
			int v = Integer.parseInt(netLines[0]) + netNodeCount;
			int cap = Integer.parseInt(netLines[2]);
			addedge(u, v, cap, 0);
			addedge(v, u, cap, 0);
		}
		// 将消费点整合
		for (int i = 0; i < consumerNodeCount; i++) {
			addedge(i + netNodeCount, netNodeCount + consumerNodeCount + 1, Integer.MAX_VALUE, 0);
			addedge(netNodeCount + consumerNodeCount + 1, i + netNodeCount, Integer.MAX_VALUE, 0);
		}
		
		int[] myServerNodes = new int[serverNodes.size()];
		for (int i = 0; i < serverNodes.size(); i++) {
			myServerNodes[i] = serverNodes.get(i);
		}

		serverNodes.add(0);
		int tempMaxFlow = 0;// 临时存储最大流
		int tempNetNode = 0;// 临时存储网络节点
		int tempMinCost = 0;// 临时最小费用
		int temp = 0;
		int index = 0;
		while (maxflow < consumerSum) {
			for (int j = 0; j < netNodeCount; j++) {
				init();
				serverNodes.remove(index);
				serverNodes.add(j);

				// 网络节点间的边
				for (int i = 4; i < 4 + linkCount; i++) {
					String[] netLines = reads[i].split(" ");
					int u = Integer.parseInt(netLines[0]);
					int v = Integer.parseInt(netLines[1]);
					int cap = Integer.parseInt(netLines[2]);
					int cos = Integer.parseInt(netLines[3]);
					addedge(u, v, cap, cos);
					addedge(v, u, cap, cos);
				}
				// 和消费节点间的边
				for (int i = 5 + linkCount; i < reads.length; i++) {
					String[] netLines = reads[i].split(" ");
					int u = Integer.parseInt(netLines[1]);
					int v = Integer.parseInt(netLines[0]) + netNodeCount;
					int cap = Integer.parseInt(netLines[2]);
					addedge(u, v, cap, 0);
					addedge(v, u, cap, 0);
				}
				// 将消费点整合
				for (int i = 0; i < consumerNodeCount; i++) {
					addedge(i + netNodeCount, netNodeCount + consumerNodeCount + 1, Integer.MAX_VALUE, 0);
					addedge(netNodeCount + consumerNodeCount + 1, i + netNodeCount, Integer.MAX_VALUE, 0);
				}

				// 将源点整合
				myServerNodes = new int[serverNodes.size()];
				for (int i = 0; i < serverNodes.size(); i++) {
					myServerNodes[i] = serverNodes.get(i);
				}
				MixServerNet(myServerNodes, netNodeCount + consumerNodeCount);

				FlowAndCost fc = minCostMaxFlow(netNodeCount + consumerNodeCount, netNodeCount + consumerNodeCount + 1);
				tempMinCost = fc.cost;
				temp = fc.flow;

				if (tempMaxFlow < temp) {
					tempMaxFlow = temp;
					tempNetNode = j;
				}
			}
			serverNodes.remove(index);
			serverNodes.add(tempNetNode);
			serverNodes.add(10000);
			index++;

			maxflow = tempMaxFlow;
			mincost = tempMinCost;
		}

		init();
		// 网络节点间的边
		for (int i = 4; i < 4 + linkCount; i++) {
			String[] netLines = reads[i].split(" ");
			int u = Integer.parseInt(netLines[0]);
			int v = Integer.parseInt(netLines[1]);
			int cap = Integer.parseInt(netLines[2]);
			int cos = Integer.parseInt(netLines[3]);
			addedge(u, v, cap, cos);
			addedge(v, u, cap, cos);
		}
		// 和消费节点间的边
		for (int i = 5 + linkCount; i < reads.length; i++) {
			String[] netLines = reads[i].split(" ");
			int u = Integer.parseInt(netLines[1]);
			int v = Integer.parseInt(netLines[0]) + netNodeCount;
			int cap = Integer.parseInt(netLines[2]);
			addedge(u, v, cap, 0);
			addedge(v, u, cap, 0);
		}
		// 将消费点整合
		for (int i = 0; i < consumerNodeCount; i++) {
			addedge(i + netNodeCount, netNodeCount + consumerNodeCount + 1, Integer.MAX_VALUE, 0);
			addedge(netNodeCount + consumerNodeCount + 1, i + netNodeCount, Integer.MAX_VALUE, 0);
		}
		serverNodes.remove(serverNodes.size() - 1);
		System.out.println(serverNodes);
		myServerNodes = new int[serverNodes.size()];
		for (int i = 0; i < serverNodes.size(); i++) {
			myServerNodes[i] = serverNodes.get(i);
		}
		MixServerNet(myServerNodes, netNodeCount + consumerNodeCount);

		FlowAndCost fc = minCostMaxFlow(netNodeCount + consumerNodeCount, netNodeCount + consumerNodeCount + 1);
		maxflow = fc.flow;
		mincost = fc.cost;
		/*
		 * 这里面才是真实的from->to，flow
		 */
		for (Edge edge : edges) {
			if (edge.flow > 0) {
				System.out.println(edge);
				pathList.add(edge);
			}
		}
		// 按照输出格式打印路径，这时候才创建的Graph
		String result = PrintPathUtil.printPath(pathList, netNodeCount + consumerNodeCount, netNodeCount + consumerNodeCount + 1,
				netNodeCount, consumerNodeCount);

		return result.split("\r\n");
	}

}

class FlowAndCost {
	public int flow;
	public int cost;
}
