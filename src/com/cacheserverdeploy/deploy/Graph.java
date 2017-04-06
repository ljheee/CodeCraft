package com.cacheserverdeploy.deploy;

public class Graph {

	private Vertex vertexList[]; // 节点集合
	private int adjMat[][]; // 图的邻接矩阵

	private int nVerts;// 节点下标
	private int netNodeCount;// 网络节点数
	// private static int MAX_VERTS = 42; // n个点

	int i = 0;
	int j = 0;

	public Vertex[] getVertexList() {
		return vertexList;
	}

	public int[][] getAdjMat() {
		return adjMat;
	}

	public int getNetNodeCount() {
		return netNodeCount;
	}

	public Graph(int netNodeCount) {
		this.netNodeCount = netNodeCount;
		adjMat = new int[StaticValues.MAX_VERTS][StaticValues.MAX_VERTS]; // 邻接矩阵
		vertexList = new Vertex[StaticValues.MAX_VERTS]; // 顶点数组
		nVerts = 0;

		for (i = 0; i < StaticValues.MAX_VERTS; i++) {
			for (j = 0; j < StaticValues.MAX_VERTS; j++) {
				adjMat[i][j] = 0;
			}
		}
	}

	public void addEdge(int start, int end, int flow) {// 有向图，添加边
		adjMat[start][end] = flow;
	}

	public void addVertex(int lab) {
		vertexList[nVerts++] = new Vertex(lab);// 添加点
	}

	public int displayVertex(int i) {
		return vertexList[i].getLabel();
	}

	public boolean displayVertexVisited(int i) {
		return vertexList[i].WasVisited();
	}

	public void printGraph() {
		for (i = 0; i < StaticValues.MAX_VERTS; i++) {
			System.out.print("第" + displayVertex(i) + "个节点:" + " ");

			for (j = 0; j < StaticValues.MAX_VERTS; j++) {
				System.out.print(displayVertex(i) + "-" + displayVertex(j)
						+ "：" + adjMat[i][j] + " ");
			}
			System.out.println();
		}

	}

}
