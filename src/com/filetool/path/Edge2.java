package com.filetool.path;

public class Edge2 {
	public int from;
	public int to;
	public int flow;
	public int cap;
	public int last;//当前这条边，剩余可走流量
	public int cost;

	public Edge2() {
		super();
	}

	public Edge2(int from, int to, int flow, int cap, int cost) {
		super();
		this.from = from;
		this.to = to;
		this.flow = flow;
		this.cap = cap;
		this.last = cap;//初始化时，剩余可走流量=cap
		this.cost = cost;
	}

	@Override
	public String toString() {
		return "Edge [from=" + from + ", to=" + to + ", flow=" + flow + ", cap=" + cap + ", cost=" + cost + "]";
	}

}
