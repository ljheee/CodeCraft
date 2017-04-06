package com.cacheserverdeploy.deploy;

import java.util.Comparator;
/**
 * 消费节点、临近网络节点、消费节点需求映射关系
 * @author ljheee
 *
 */
public class RecordLine implements Comparator<RecordLine>{
	
	public int id;
	public int netNode;
	public int needed;
	


	@Override
	public int compare(RecordLine o1, RecordLine o2) {
		if ( (o2.needed-o1.needed) >= 0) {
			return 1;
		} else {
			return -1;
		}
	}

	
	
}
