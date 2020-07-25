package me.prisonranksx.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Lists;

public class CollectionUtils {
	
	private static int f(int a, int b) {
		String c = String.valueOf((Double.valueOf(a) / Double.valueOf(b)));
		if(c.contains("//.")) {
			int n = Integer.valueOf(c.split(".")[1]);
			int f = Integer.valueOf(c);
			if(n >= 5) {
				return f + 1;
			} else {
				return f;
			}
		}
		return a / b;
	}
	
	public static class PaginatedList {
		
		private List<String> list;
		private PaginatedList pl;
		private int currentPage;
		private int finalPage;
		
		public PaginatedList(List<String> list, int currentPage, int finalPage) {
			this.list = list;
			this.currentPage = currentPage;
			this.finalPage = finalPage;
			this.pl = this;
		}

		public int getCurrentPage() {
			return currentPage;
		}

		public int getFinalPage() {
			return finalPage;
		}

		public List<String> getList() {
			return list;
		}

		public PaginatedList getPaginatedList() {
			return pl;
		}
		
	}
	
	public static class ReplaceableList {
		
		private List<String> list;
		private ReplaceableList rl;
		
		public ReplaceableList(List<String> list) {
			this.list = list;
			this.rl = this;
		}
		
		@Deprecated
		public ReplaceableList replaceCollectable(List<String> stringList, String from, String to) {
			int i = 0;
			for(String line : stringList) {
				i++;
				if(line.equals(from)) {
					stringList.set(i, to);
				}
			}	
			return rl;
		}
		
		public ReplaceableList replaceCollectable(String from, String to) {
			int i = 0;
			for(String line : list) {
				i++;
				if(line.equals(from)) {
					list.set(i, to);
				}
			}	
			return rl;
		}
		
		public Collection<String> collect() {
			return list;
		}
		
		public List<String> collectAsList() {
			return list;
		}
		
	}
	
	/**
	 * 
	 * @param stringCollection collection to columnize its elements
	 * @param columnsPerLine how many string elements on one line
	 * @param seperator what is between the string elements
	 * @return a new columnized linked list string collection
	 */
	public static Collection<String> columnizeCollection(final Collection<String> stringCollection, final int columnsPerLine, final String seperator) {
		int i = 0;
		List<String> newList = new LinkedList<>();
		StringBuilder builder = new StringBuilder("");
		String finishChar = ".";
		for(String line : stringCollection) {
			i++;
			if(i == columnsPerLine+1) {
				newList.add(builder.toString());
				builder = new StringBuilder("");
				i = 0;
			} else {
				if(i == stringCollection.size()) {
					break;
				}
				builder.append(line).append(seperator);
			}
		}
		int size = newList.size();
		String lastLine = newList.get(size - 1);
		String lastLineReplaced = lastLine.substring(0, (lastLine.length()) - (seperator.length()));
		newList.set(size - 1, lastLineReplaced + finishChar);
		return newList;
	}
	
	/**
	 * 
	 * @param stringCollection collection to columnize its elements
	 * @param columnsPerLine how many string elements on one line
	 * @param seperator what is between the string elements
	 * @param finalChar the last character on the last line, '.' (dot) by default
	 * @return a new columnized linked list string collection
	 */
	public static Collection<String> columnizeCollection(final Collection<String> stringCollection, final int columnsPerLine, final String seperator, final String finalChar) {
		int i = 0;
		List<String> newList = new LinkedList<>();
		StringBuilder builder = new StringBuilder("");
		String finishChar = finalChar == null ? "." : finalChar;
		for(String line : stringCollection) {
			i++;
			if(i == columnsPerLine+1) {
				newList.add(builder.toString());
				builder = new StringBuilder("");
				i = 0;
			} else {
				builder.append(line).append(seperator);
			}
		}
		int size = newList.size();
		String lastLine = newList.get(size - 1);
		String lastLineReplaced = lastLine.substring(0, (lastLine.length()) - (seperator.length()));
		newList.set(size - 1, lastLineReplaced + finishChar);
		return newList;
	}
	
	/**
	 * 
	 * @param stringList List to columnize its elements
	 * @param columnsPerLine how many string elements on one line
	 * @param seperator what is between the string elements
	 * @param finalChar the last character on the last line, '.' (dot) by default
	 * @return a new columnized linked list string collection
	 */
	public static List<String> columnizeList(final List<String> stringList, final int columnsPerLine, final String seperator, final String finalChar) {
		int i = 0;
		List<String> newList = new LinkedList<>();
		StringBuilder builder = new StringBuilder("");
		String finishChar = finalChar == null ? "." : finalChar;
		for(String line : stringList) {
			i++;
			if(i == columnsPerLine+1) {
				newList.add(builder.toString());
				builder = new StringBuilder("");
				i = 0;
			} else {
				if(i == stringList.size()+1) {
					break;
				}
				builder.append(line).append(seperator);
			}
		}
		int size = newList.size();
		String lastLine = newList.get(size - 1);
		String lastLineReplaced = lastLine.substring(0, (lastLine.length()) - (seperator.length()));
		newList.set(size - 1, lastLineReplaced + finishChar);
		return newList;
	}
	
	/**
	 * 
	 * @param stringCollection collection to be converted to string
	 * @param seperator what's between the string elements
	 * @return Single Line String
	 */
	public static String collectionToString(final Collection<String> stringCollection, final String seperator) {
		String converted = "";
		String finishChar = ".";
		for(String element : stringCollection) {
			converted += element + seperator;
		}
		converted = converted.substring(converted.length() - seperator.length(), converted.length()) + finishChar;
		return converted;
	}
	
	public static String collectionToString(final Collection<String> stringCollection, final String seperator, final String finalChar) {
		String converted = "";
		String finishChar = finalChar;
		for(String element : stringCollection) {
			converted += element + seperator;
		}
		converted = converted.substring(converted.length() - seperator.length(), converted.length()) + finishChar;
		return converted;
	}
	
	public static boolean hasIgnoreCase(Collection<String> stringCollection, String searchFor) {
		boolean found = false;
		for(String line : stringCollection) {
			if(line.equalsIgnoreCase(searchFor)) {
				found = true;
			}
		}
		return found;
	}
	
	public static String hasIgnoreCaseReturn(Collection<String> stringCollection, String searchFor) {
		String found = null;
		for(String line : stringCollection) {
			if(line.equalsIgnoreCase(searchFor)) {
				found = line;
			}
		}
		return found;
	}
	
	public static boolean containsIgnoreCase(Collection<String> stringCollection, String searchFor) {
		boolean found = false;
		for(String line : stringCollection) {
			if(line.contains("(?i)" + searchFor)) {
				found = true;
			}
		}
		return found;
	}
	
	public static String containsIgnoreCaseReturn(Collection<String> stringCollection, String searchFor) {
		String found = null;
		for(String line : stringCollection) {
			if(line.contains("(?i)" + searchFor)) {
				found = line;
			}
		}
		return found;
	}
	
	public static ReplaceableList replaceCollectable(List<String> stringList, String from, String to) {
		int i = 0;
		for(String line : stringList) {
			i++;
			if(line.equals(from)) {
				stringList.set(i, to);
			}
		}	
		ReplaceableList replaceableList = new CollectionUtils.ReplaceableList(stringList);
		return replaceableList;
	}
	
	public static List<String> replace(List<String> stringList, String from, String to) {
		int i = 0;
		for(String line : stringList) {
			i++;
			if(line.equals(from)) {
				stringList.set(i, to);
			}
		}
		return stringList;
	}
	
	public static List<String> replaceIgnoreCase(List<String> stringList, String from, String to) {
		int i = 0;
		for(String line : stringList) {
			i++;
			if(line.equalsIgnoreCase(from)) {
				stringList.set(i, to);
			}
		}
		return stringList;
	}
	
	public static List<String> replaceContainsIgnoreCase(List<String> stringList, String from, String to) {
		int i = 0;
		for(String line : stringList) {
			i++;
			if(line.contains("(?i)" + from)) {
				stringList.set(i, to);
			}
		}
		return stringList;
	}
	
	/**
	 * 
	 * @param collection collection of string to paginate
	 * @param maxElements elements per page
	 * @param page current page
	 * @return paginated string list
	 */
	public static List<String> paginateCollection(Collection<String> collection, final int maxElements, final int page) {
      int counter = 0;
      List<String> oldCollection = Lists.newLinkedList(collection);
      List<String> newCollection = Lists.newLinkedList();
      int size = oldCollection.size();
		for(int i = 0; i < size; i++) {
    	  counter++;
    	  if(counter >= maxElements) {
    		  break;
    	  }
    	  if(i + page < 0 || i + page >= size) {
    		  break;
    	  }
    	  newCollection.add(oldCollection.get(i + page));
        }
	  return newCollection;
	}
	
	/**
	 * 
	 * @param stringList list of strings
	 * @param maxElements elements per page
	 * @param page current page
	 * @return paginated string list
	 */
	public static List<String> paginateList(List<String> stringList, final int maxElements, final int page) {
      int counter = 0;
      List<String> oldCollection = stringList;
      List<String> newCollection = Lists.newLinkedList();
      int size = oldCollection.size();
		for(int i = 0; i < size; i++) {
    	  counter++;
    	  if(counter >= maxElements) {
    		  break;
    	  }
    	  int elementIndex = i + page;
    	  if(elementIndex < 0 || elementIndex >= size) {
    		  break;
    	  }
    	  newCollection.add(oldCollection.get(elementIndex));
        }
	  return newCollection;
	}
	
	public static PaginatedList paginateListCollectable(List<String> stringList, final int maxElements, final int page) {
	      int counter = 0;
	      List<String> oldCollection = stringList;
	      List<String> newCollection = Lists.newLinkedList();
	      int size = oldCollection.size();
			for(int i = 0; i < size; i++) {
	    	  counter++;
	    	  if(counter >= maxElements) {
	    		  break;
	    	  }
	    	  int elementIndex = i + page;
	    	  if(elementIndex < 0 || elementIndex >= size) {
	    		  break;
	    	  }
	    	  newCollection.add(oldCollection.get(elementIndex));
	        }
		  return new PaginatedList(newCollection, page, f(size, maxElements));
	}
	
	public static List<String> stringToList(String string, String seperator) {
        return Lists.newArrayList(string.split(seperator));
	}
	
	public static Collection<String> stringToCollection(String string, String seperator) {
        List<String> newList = Lists.newArrayList(string.split(seperator));
        return Collections.unmodifiableList(newList);
	}
	
	public static List<String> separateIntoChars(List<String> stringList, int separateFactor) {
		List<String> newList = Lists.newArrayList();
		stringList.forEach(line -> {
			int counter = -1;
			for(char character : line.toCharArray()) {
				counter++;
				if(counter == separateFactor) {
					counter = -1;
				} else {
				newList.add(String.valueOf(character));
				}
			}
		});
		return newList;
	}
	
}
