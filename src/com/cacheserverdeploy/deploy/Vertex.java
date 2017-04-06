package com.cacheserverdeploy.deploy;

import java.util.ArrayList;

public class Vertex {

	boolean wasVisited; // 是否遍历过
	public int label; // 节点名称
	ArrayList<Integer> allVisitedList;// 节点已访问过的顶点

	public void setAllVisitedList(ArrayList<Integer> allVisitedList) {
		this.allVisitedList = allVisitedList;
	}

	public ArrayList<Integer> getAllVisitedList() {
		return allVisitedList;
	}

	public boolean WasVisited() {
		return wasVisited;
	}

	public void setWasVisited(boolean wasVisited) {
		this.wasVisited = wasVisited;
	}

	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		this.label = label;
	}

	public Vertex(int lab) // constructor
	{
		label = lab;
		wasVisited = false;
	}

	public void setVisited(int j) {
		allVisitedList.set(j, 1);
	}

}
