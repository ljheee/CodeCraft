package com.cacheserverdeploy.deploy;

import java.util.Arrays;
import java.util.List;

public class PrintPathUtil {
	
	public static String printPath(List<Edge> pathList, int src, int end,
			int netNodeCount, int consumerNodeCount) {

		// 新加
		Graph graph = new Graph(netNodeCount);
		for (int i = 0; i < netNodeCount + consumerNodeCount + 2; i++) {
			graph.addVertex(i);
		}

		for (Edge edge : pathList) {

			// 新加
			graph.addEdge(edge.from, edge.to, edge.flow);

		}

		// 这里就是原来Main函数的调用的方法
		AF operation = new AF(graph, src, end);
		String result = operation.getResult();
		
		System.out.println("=============");
		System.out.println(result);
		String[] split = result.split("\r\n");
		System.out.println(Arrays.toString(split));
		
		return result;
		//FileUtil.write("E:\\result.txt", new String[]{result}, false);
	}
}
