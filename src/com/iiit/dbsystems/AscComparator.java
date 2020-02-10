package com.iiit.dbsystems;

import java.util.Comparator;
import java.util.List;



public class AscComparator implements Comparator<List<String>> {
	@Override
	public int compare(List<String> l1, List<String> l2) {
		for (int i = 0; i < MergeSort.columnOrder.size(); i++) {
			if (l1.get(i).equals(l2.get(i))) {
			} else
				return l1.get(i).compareTo(l2.get(i));
		}
		return 0;
	}

}
