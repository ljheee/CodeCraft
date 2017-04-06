package com.cacheserverdeploy.deploy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FlowUtil {
	
	
	/*RecordLine，记录（消费节点、临近网络节点、消费节点需求映射关系）*/
	static List<RecordLine> list = new ArrayList<RecordLine>();
	
	/*前30%个数,在getServerNodes()中初始化*/
	static int num;
	
	/*网络节点-按流量排序后的List*/
	static List<Map.Entry<Integer, Integer>> list_Data;
	
	/*getNextNode2()方法的索引*/
	static int index = 0;
	
	/**
	 * 返回消费总需求
	 * @param reads
	 * @return
	 */
	public static int getFlow(String[] reads) {
		String[] firstLine = reads[0].split(" ");
		int netNodeCount = Integer.parseInt(firstLine[0]);
		int linkCount = Integer.parseInt(firstLine[1]);
		int consumerNodeCount = Integer.parseInt(firstLine[2]);
		int consumerSum = 0;

		ArrayList<ArrayList<Integer>> aList = new ArrayList<ArrayList<Integer>>();
		// 将aList进行初始化
		for (int i = 0; i < netNodeCount + consumerNodeCount; i++) {
			aList.add(new ArrayList<Integer>());
		}

		// 网络节点间的边
		for (int i = 4; i < 4 + linkCount; i++) {
			String[] netLines = reads[i].split(" ");
			int u = Integer.parseInt(netLines[0]);
			int v = Integer.parseInt(netLines[1]);
			int cap = Integer.parseInt(netLines[2]);
			int cos = Integer.parseInt(netLines[3]);
			aList.get(u).add(cap);
			aList.get(v).add(cap);
		}

		// 和消费节点间的边
		for (int i = 5 + linkCount; i < reads.length; i++) {
			String[] netLines = reads[i].split(" ");
			int u = Integer.parseInt(netLines[1]);
			int v = Integer.parseInt(netLines[0]) + netNodeCount;
			int cap = Integer.parseInt(netLines[2]);
			consumerSum += cap;
			aList.get(u).add(cap);
			aList.get(v).add(cap);
		}

		for (int i = 0; i < aList.size(); i++) {
			int sum = 0;
			for (int j = 0; j < aList.get(i).size(); j++) {
				sum += aList.get(i).get(j);
			}
			aList.get(i).clear();
			aList.get(i).add(sum);
		}

		/**
		 * 排序
		 */
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int i = 0; i < aList.size(); i++) {
			map.put(i, aList.get(i).get(0));
		}
		list_Data = new ArrayList<Map.Entry<Integer, Integer>>(map.entrySet());
		
		Collections.sort(list_Data,
				new Comparator<Map.Entry<Integer, Integer>>() {
					public int compare(Map.Entry<Integer, Integer> o1,
							Map.Entry<Integer, Integer> o2) {
						if (o2.getValue() != null && o1.getValue() != null
								&& o2.getValue().compareTo(o1.getValue()) > 0) {
							return 1;
						} else {
							return -1;
						}

					}
				});
		System.out.println(list_Data);

		// for (int i = 0; i < aList.size(); i++) {
		// System.out.println("节点：" + i + "，流量和：" + aList.get(i).get(0));
		// }

		System.out.println("消费总需求：" + consumerSum);
		return consumerSum;
	}

	
	
	
	/**
	 * 若出现三点共线，则把服务器节点往上移一位
	 * @param readss
	 * @return 哪些点需要往上移一位
	 */
	public static List<Integer> getLineNodes(String[] readss) {
		List<Integer> result = new LinkedList<Integer>();
		String[] firstLine = readss[0].split(" ");
		int netNodeCount = Integer.parseInt(firstLine[0]);
		int linkCount = Integer.parseInt(firstLine[1]);

		// 与消费节点相连的点的遍历
		for (int i = 5 + linkCount; i < readss.length; i++) {
			String[] netLines = readss[i].split(" ");
			int u = Integer.parseInt(netLines[1]);
			int v = Integer.parseInt(netLines[0]) + netNodeCount;
			int cap = Integer.parseInt(netLines[2]);
			if(IsForUOne(readss, u)){
				result.add(u);
			}
		}
		return result;
	}

	private static boolean IsForUOne(String[] readss, int u1) {
		String[] firstLine = readss[0].split(" ");
		int linkCount = Integer.parseInt(firstLine[1]);

		int count = 0;
		// 网络节点间的边
		for (int i = 4; i < 4 + linkCount; i++) {
			String[] netLines = readss[i].split(" ");
			int u = Integer.parseInt(netLines[0]);
			int v = Integer.parseInt(netLines[1]);
			int cap = Integer.parseInt(netLines[2]);
			int cos = Integer.parseInt(netLines[3]);
			if (u == u1 || v == u1) {
				count++;
			}
		}
		return count == 1;
	}
	
	/**
	 * 获取服务器部署节点-前px%
	 * 
	 * @param px
	 *            百分比，例如0.3
	 * @return
	 */
	public static List<Integer> getServerNodes(String[] readss, double px) {
		String[] firstLine = readss[0].split(" ");
		int linkCount = Integer.parseInt(firstLine[1]);
		int consumerNodeCount = Integer.parseInt(firstLine[2]);
		num = (int) (consumerNodeCount * px);
		List<Integer> result = new ArrayList<Integer>();
		
		// 和消费节点间的边
		int lineIndex = 5 + linkCount;

		RecordLine recordLine;
		for (int i = 0; i < consumerNodeCount; i++) {
			String[] lines = readss[lineIndex].split(" ");
			recordLine = new RecordLine();
			recordLine.id = Integer.parseInt(lines[0]);
			recordLine.netNode = Integer.parseInt(lines[1]);
			recordLine.needed = Integer.parseInt(lines[2]);
			list.add(recordLine);
			lineIndex++;
		}

		//去除“三点一线”，中间点
		List<Integer> trimNodes = getLineNodes(readss);
		for (int i = list.size()-1; i >= 0 ; i--) {
			if(trimNodes.contains(list.get(i).netNode)){
				list.remove(i);
			}
		}
		
		//按消费节点需求--排序
		Collections.sort(list, new Comparator<RecordLine>() {
			@Override
			public int compare(RecordLine o1, RecordLine o2) {
				if (o2.needed - o1.needed >= 0) {
					return 1;
				} else {
					return -1;
				}
			}
		});

		 for (int i = 0; i < num; i++) {//取前num个
			 result.add(list.get(i).netNode);
		 }

		return result;
	}
	
	/**
	 * 获取下一个服务器节点
	 * 根据需求排序
	 * @return
	 */
	public static int getNextNode1(){
		return list.get(num++).netNode;
	}
	
	
	/**
	 * 获取下一个服务器节点
	 * 根据流量排序
	 * @return
	 */
	public static int getNextNode2(List<Integer> list){
		int result = list_Data.get(index++).getKey();
		
		while(list.contains(result)){
			System.out.println("包含："+result);
			result = list_Data.get(index++).getKey();
		}
		System.out.println("out of while");
		return result;
	}
 
	
}
