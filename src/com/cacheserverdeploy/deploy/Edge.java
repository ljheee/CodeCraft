package com.cacheserverdeploy.deploy;

public class Edge {
	public int from;
	public int to;
	public int flow;
	public int cap;
	public int cost;

	public Edge() {
		super();
	}

	public Edge(int from, int to, int flow, int cap, int cost) {
		super();
		this.from = from;
		this.to = to;
		this.flow = flow;
		this.cap = cap;
		this.cost = cost;
	}

	@Override
	public String toString() {
		return "Edge [from=" + from + ", to=" + to + ", flow=" + flow + ", cap=" + cap + ", cost=" + cost + "]";
	}

}