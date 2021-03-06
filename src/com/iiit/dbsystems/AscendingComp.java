package com.iiit.dbsystems;

import java.util.Comparator;
import java.util.List;



public class AscendingComp implements Comparator<List<String>> {
	@Override
	public int compare(List<String> list1, List<String> list2) {
		for (int i = 0; i < MergeSort.columnOrder.size(); i++) 
			if (!list1.get(i).equals(list2.get(i))) 
				return list1.get(i).compareTo(list2.get(i));
		return 0;
	}

}
