package com.iiit.dbsystems;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import com.iiit.dbsystems.AscendingComp;
import com.iiit.dbsystems.DescendingComp;

public class MergeSort {
	private static String inputFile;
	private static String outputFile;
	private static String memory;
	private static String sortingOrder;
	private static String metaDataFile;
	static List<List<String>> completeData = new ArrayList<>();
	private static String[] colums;
	private static int columnCount = 0;
	private static List<String> columnList = new ArrayList();
	static List<Integer> columnOrder = new ArrayList();
	private static int memorySize = 0;
	private static int lineSize = 0;
	private static int countOfReads = 0;

	private static void mergeSortContoller() {
		try {
			System.out.println("\n Reading Metadata File ");
			readMetaDataFile();
			System.out.println(" Updated params as per mentioned in Metadata File ");
			assignColumnSorting();
			
			System.out.println("\n Reading Input File ");
			readInputFiles();
			System.out.println(" Updated data as per input File ");
			
			System.out.println("\n Start merging Files ");
			mergeFiles();
			System.out.println(" Files merged In single output file");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void mergeFiles() throws IOException {
		int blockSize = (int) memorySize / (countOfReads * lineSize * 2);
		int checkNullValue = 0;
		int curReadCount = 0;
		int tempVar = 0;
		String readObject;
		boolean[] trackTempFileProcess = new boolean[countOfReads + 2];
		List<String> tempList = new ArrayList();
		List<String> tempList1 = new ArrayList();
		BufferedWriter writerObject = null;

		try {
			BufferedReader[] readObjectArray = new BufferedReader[countOfReads + 2];
			for (int temp = 0; temp < countOfReads; temp++) {
				FileReader tempFiles = new FileReader("tempOutput" + temp);
				readObjectArray[temp] = new BufferedReader(tempFiles);
			}
			File createdOutputFile = createOutputFile();
			writerObject = new BufferedWriter(new FileWriter(createdOutputFile.getAbsoluteFile()));
			for (int i = 0; i < countOfReads; i++)
				trackTempFileProcess[i] = true;
			completeData.clear();

			while (countOfReads > checkNullValue) {

				if (completeData.size() == 0 || !trackTempFileProcess[tempVar]) {
					int i = 0;
					while (i < countOfReads) {
						if (trackTempFileProcess[i]) {
							int j = 0;
							while (j < blockSize) {
								readObject = readObjectArray[i].readLine();
								if (readObject == null) {
									checkNullValue++;
									trackTempFileProcess[i] = false;
									break;
								}
								tempList = new ArrayList();
								String start = readObject.substring(0, 10);
								tempList.add(start);
								readObject = readObject.substring(12);
								String[] currentLineArray = readObject.split("[ ]{2,}");

								for (String str : currentLineArray)
									tempList.add(str);

								tempList.add("" + i);
								completeData.add(tempList);
								j++;
							}
						}
						i++;
					}
					if (sortingOrder.equals("asc"))
						Collections.sort(completeData, new AscendingComp());
					else
						Collections.sort(completeData, new DescendingComp());

					int count = 0;
					for (String rs : completeData.get(0)) {
						writerObject.write(rs);
						if (count != tempList.size() - 2)
							writerObject.write("  ");
						count++;
					}
					completeData.remove(0);
					writerObject.write("\n");
					tempVar = Integer.parseInt(tempList.get(columnCount));
					curReadCount++;

				} else {
					readObject = readObjectArray[tempVar].readLine();
					if (readObject == null && trackTempFileProcess[tempVar])
						checkNullValue++;
					else if (readObject == null) {
						trackTempFileProcess[tempVar] = false;
						readObjectArray[tempVar].close();
					} else {
						tempList1 = new ArrayList();
						String[] currentLineArray = readObject.split("[ ]{2,}");

						for (String str : currentLineArray)
							tempList1.add(str);
						tempList1.add("" + tempVar);
						int i = 0;

						for (int j = 0; j < completeData.size(); j++) {
							if (sortingOrder.equals("asc")) {
								if (ascendingComparator(tempList1, completeData.get(j)) <= 0) {
									int count = 0;
									for (String str : tempList1) {
										writerObject.write(str);
										if (tempList1.size() - 2 != count)
											writerObject.write("  ");
										count++;
									}
									tempList1.clear();
									break;
								} else {
									int count = 0;
									for (String str : completeData.get(j)) {
										writerObject.write(str);
										if (completeData.get(j).size() - 2 != count)
											writerObject.write("  ");
										count++;
									}
									tempVar = Integer.parseInt(completeData.get(j).get(columnCount));
									completeData.remove(j);
								}
								writerObject.write("\n");
								curReadCount++;
							}

							else {
								if (descendingComparator(tempList1, completeData.get(j)) <= 0) {
									int count = 0;
									for (String str : tempList1) {
										writerObject.write(str);
										if (tempList1.size() - 2 != count)
											writerObject.write("  ");
										count++;
									}
									tempList1.clear();
									break;
								} else {
									int count = 0;
									for (String str : completeData.get(j)) {
										writerObject.write(str);
										if (completeData.get(j).size() - 2 != count)
											writerObject.write("  ");
										count++;
									}
									tempVar = Integer.parseInt(completeData.get(j).get(columnCount));
									completeData.remove(j);
								}
								writerObject.write("\n");
								curReadCount++;
							}
						}
					}
				}
			}
			if (completeData.size() > 0) {
				for (List<String> remainingData : completeData) {
					int count = 0;
					for (String str : remainingData) {
						writerObject.write(str);
						if (tempList1.size() - 2 != count)
							writerObject.write("  ");
						count++;
					}
					writerObject.write("\n");
					if(tempList1.size()>=columnCount)
					tempVar = Integer.parseInt(tempList1.get(columnCount));
					curReadCount++;
				}
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			writerObject.close();
		}

	}

	private static File createOutputFile() throws IOException {
		File createOutputFile = new File(outputFile);
		if (!createOutputFile.exists())
			createOutputFile.createNewFile();
		return createOutputFile;
	}

	private static void readInputFiles() {
		System.out.println("\n Reading Input records");
		
		int lnToRead = (int) memorySize / lineSize;

		int sortedCount = 0;
		int tempLineCount = 0;
		try {
			FileReader file = new FileReader("src/" + inputFile);
			BufferedReader readObject = new BufferedReader(file);
			String inputData = readObject.readLine();

			while (tempLineCount < lnToRead && inputData != null) {
				List<String> tempList = new ArrayList<>();
				String currentLine = inputData;
				String[] currentLineArray = currentLine.split("[ ]{2,}");
				for (String str : currentLineArray)
					tempList.add(str);
				completeData.add(tempList);
				sortedCount++;
				tempLineCount++;
				inputData = readObject.readLine();

				if (lnToRead == tempLineCount || inputData == null) {
					if (sortingOrder.equals("asc"))
						Collections.sort(completeData, new AscendingComp());
					else
						Collections.sort(completeData, new DescendingComp());

					updateOutputTempFiles(countOfReads);
					tempLineCount = 0;
					countOfReads++;
				}

			}
			if (completeData.size() > 0) {
				if (sortingOrder.equals("asc"))
					Collections.sort(completeData, new AscendingComp());
				else
					Collections.sort(completeData, new DescendingComp());

				updateOutputTempFiles(countOfReads);
				tempLineCount = 0;
				countOfReads++;
			}

			System.out.println("\n Number of records read and sorted are :" + sortedCount);
			
		} catch (FileNotFoundException e) {
			System.out.println(" please check the file path and permissions to aceess the file :");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void updateOutputTempFiles(int countOfReads) throws IOException {
		BufferedWriter writerObject = null;

		try {
			File tempOutputFiles = new File("tempOutput" + countOfReads);
			if (!tempOutputFiles.exists())
				tempOutputFiles.createNewFile();

			FileWriter fileWriterObject = new FileWriter(tempOutputFiles.getAbsoluteFile());
			writerObject = new BufferedWriter(fileWriterObject);

			for (List<String> rs : completeData) {
				for (int i = 0; i < rs.size(); i++) {
					writerObject.write(rs.get(i));
					if (i != rs.size() - 1)
						writerObject.write("  ");
				}
				writerObject.write("\n");
			}

		} catch (Exception e) {
			System.out.println(" Exception in writing file" + e.getLocalizedMessage());
		} finally {
			writerObject.close();
			completeData.clear();
		}

	}

	private static void assignColumnSorting() {
		for (String col : colums) {
			boolean validateColumn = false;
			for (int i = 0; i < columnCount; i++) {
				if (col.equals(columnList.get(i))) {
					columnOrder.add(i);
					validateColumn = true;
					break;
				}
			}
			if (!validateColumn)
				System.out.println("Bad column name");
		}
	}

	private static void readMetaDataFile() {
		String cname = "";
		try {
			FileReader fr = new FileReader(metaDataFile);
			BufferedReader readObject = new BufferedReader(fr);
			String metaData = readObject.readLine();
			while (metaData != null) {
				columnCount++;
				String[] temp = metaData.split(",");
				String columnName = temp[0];
				columnList.add(columnName);
				int columnSize = Integer.parseInt(temp[1]);
				lineSize += columnSize;
				metaData = readObject.readLine();
			}
			readObject.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static int ascendingComparator(List<String> list1, List<String> list2) {
		for (int i = 0; i < columnOrder.size(); i++)
			if (!list1.get(i).equals(list2.get(i)))
				return list1.get(i).compareTo(list2.get(i));
		return 0;
	}

	private static int descendingComparator(List<String> list1, List<String> list2) {
		for (int i = 0; i < columnOrder.size(); i++)
			if (!list1.get(i).equals(list2.get(i)))
				return (-1 * list1.get(i).compareTo(list2.get(i)));

		return 0;
	}

	public static void main(String[] args) {
		inputFile = args[0];
		outputFile = args[1];
		memory = args[2];
		sortingOrder = args[3];
		metaDataFile = "src/metadata.txt";
		colums = Arrays.copyOfRange(args, 4, args.length);
		memorySize = Integer.parseInt(memory);
		memorySize = (int) (0.1 * 1000000 * memorySize);
		
		long startTime = System.nanoTime();
		
		System.out.println(" ###  start execution... ");
		System.out.println("\n Input paramas for applications are as Mentioned below : ");
		System.out.println(" 1.Input file    : " + inputFile);
		System.out.println(" 2.Output file   : " + outputFile);
		System.out.println(" 3.Memory(MB)    : " + memory + "MB");
		System.out.println(" 4.Sorting Order : " + sortingOrder);
		mergeSortContoller();
		
		long endTime = System.nanoTime();
		long totalTime = endTime - startTime;
		System.out.println("\n total time : "+(double)totalTime/1_000_000_000.0 + " seconds..");
		
		System.out.println("\n ###  program execution completed ... ");
		
		
	}

}
