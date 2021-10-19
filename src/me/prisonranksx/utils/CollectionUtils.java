package me.prisonranksx.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


public class CollectionUtils {
	
	public static Map<String, Object> EMPTY_STRING_TO_OBJECT_MAP = new ConcurrentHashMap<>();
	public static List<String> EMPTY_STRING_LIST = new ArrayList<>();
	public static List<String> EMPTY_LINKED_STRING_LIST = Collections.synchronizedList(new LinkedList<>());
	public static List<Double> EMPTY_DOUBLE_LIST = Collections.synchronizedList(new ArrayList<>());
	public static List<List<String>> EMPTY_STRINGLIST_LIST = Collections.synchronizedList(new ArrayList<>());
	
	public static List<String> emptyList() {
		return EMPTY_STRING_LIST;
	}
	
	private static boolean isNearPointer(final int number, final int divideBy) {
		double converted = ((double)number / (double)divideBy);
		String stringDecimal = String.valueOf(converted);
		int pointIndex = stringDecimal.indexOf('.');
		int startIndex = ++pointIndex;
		String decimalValue = stringDecimal.substring(startIndex, stringDecimal.length());
		int decimalFirst = Integer.parseInt(String.valueOf(decimalValue.charAt(0)));
		switch(decimalFirst) {
		case 0:
			return decimalValue.length() > 1 ? false : true;
		default:
			return true;
		}	
	}
	
	/**
	 * 
	 * @param a collection size
	 * @param b elements per page
	 * @return %100 accurate final page number
	 */
	private static int fixPages(final int a, final int b) {
		int mathConverted = (int) Math.ceil((double)a / (double)b);
		return isNearPointer(a, b) ? mathConverted : mathConverted-1;
	}
    
	public static int getAccurateFinalPage(final int elementsCount, final int elementsPerPage) {
		return fixPages(elementsCount, elementsPerPage);
	}
	
	/**
	 * <i>
	 * @param index the index from the loop that starts with 0 and ends with the size.
	 * @param entryPerPage How many elements in a page.
	 * @param page The page it will be placed on.
	 * @return Correct position of the meant index in a paginated list.
	 */
    public static int paginateIndex(final int index, final int entryPerPage, final int page) {	
    	return page > 1 ? index + (entryPerPage*(page-1)) : index;
    }
    
	public static class PaginatedList {
		
		private List<String> list;
		private List<String> entireList;
		private PaginatedList pl;
		private int currentPage;
		private int finalPage;
		private int elementsPerPage;
		
		public PaginatedList(List<String> list, int currentPage, int finalPage, List<String> entireList, int elementsPerPage) {
			this.list = list;
			this.currentPage = currentPage;
			this.finalPage = finalPage;
			this.entireList = entireList;
			this.elementsPerPage = elementsPerPage;
			this.pl = this;
		}		
		
		/**
		 * <i>
		 * @return the current page you are viewing
		 */
		public int getCurrentPage() {
			return pl.currentPage;
		}	
		
		/**
		 * <i>
		 * @return the final page which has at least one element
		 */
		public int getFinalPage() {
			return pl.finalPage;
		}

		/**
		 * <p><i>the result is the same as when you initiate a new paginated list
		 * <p>the only benefit is flexibilty
		 * @return move to the next page
		 */
		public PaginatedList next() {
			return pl = CollectionUtils.paginateListCollectable(pl.entireList, pl.elementsPerPage, pl.getCurrentPage()+1);
		}
		
		/**
		 * <p><i>the result is the same as when you initiate a new paginated list
		 * <p>the only benefit is flexibilty
		 * @return go to the previous page
		 */
		public PaginatedList back() {
			return pl = CollectionUtils.paginateListCollectable(pl.entireList, pl.elementsPerPage, pl.getCurrentPage()-1);
		}
		
		/**
		 * <p><i>the result is the same as when you initiate a new paginated list
		 * <p>the only benefit is flexibilty
		 * @param page the page that you will be moved to
		 * @return navigate to a specific page
		 */
		public PaginatedList navigate(int page) {
			return pl = CollectionUtils.paginateListCollectable(pl.entireList, pl.elementsPerPage, page);
		}
		
		/**
		 * 
		 * @return a linked list of current page elements | will return an empty list when
		 * <p>there are not any elements on the current page | which means it will never return null
		 */
		@Nonnull
		public List<String> collect() {
			return pl.list;
		}
        
		/**
		 * @deprecated
		 * @return all elements (no pagination)
		 */
		public List<String> collectAll() {
			return pl.entireList;
		}
		
		/**
		 * 
		 * @return how many elements will be shown on one page.
		 */
		public int getElementsPerPage() {
			return pl.elementsPerPage;
		}
		
		@Deprecated
		public boolean addElement(String element) {
			return pl.entireList.add(element);
		}
		
		@Deprecated
		public PaginatedList update() {
			return this.pl = CollectionUtils.paginateListCollectable(pl.entireList, pl.elementsPerPage, getCurrentPage());
		}
		
		public PaginatedList getPaginatedList() {
			return pl;
		}
		
	}
	
	public static class PaginatedCollection {
		
		private Collection<String> collection;
		private PaginatedCollection pc;
		private int currentPage;
		private int finalPage;
		
		public PaginatedCollection(Collection<String> collection, int currentPage, int finalPage) {
			this.collection = collection;
			this.currentPage = currentPage;
			this.finalPage = finalPage;
			this.pc = this;
		}		
		
		/**
		 * 
		 * @return the current page you are viewing
		 */
		public int getCurrentPage() {
			return currentPage;
		}

		/**
		 * 
		 * @return the final page which has elements
		 */
		public int getFinalPage() {
			return finalPage;
		}

		/**
		 * 
		 * @return a collection of current page elements | will return an empty collection when
		 * <p>there are not any elements on the current page | which means it will never return null
		 */
		@Nonnull
		public Collection<String> collect() {
			return collection;
		}

		public PaginatedCollection getPaginatedCollection() {
			return pc;
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
    	  int elementIndex = paginateIndex(counter, maxElements, page);
    	  if(elementIndex < 0 || elementIndex >= size) {
    		  break;
    	  }
    	  newCollection.add(oldCollection.get(elementIndex));
        }
	  return newCollection;
	}
	
	/**
	 * 
	 * @param stringList that you want to paginate
	 * @param maxElements elements per page
	 * @param page page number
	 * @return A PaginatedList with the same insertion order that has the following methods:
	 * <i><p>collect(), getCurrentPage(), getFinalPage(), and this method parameters.
	 */
	public static PaginatedList paginateListCollectable(List<String> stringList, final int maxElements, final int page) {
	      int counter = 0;
	      List<String> oldCollection = stringList;
	      List<String> newCollection = Lists.newLinkedList();
	      int size = oldCollection.size();
			for(int i = 0; i < size; i++) {
	    	  if(counter >= maxElements) {
	    		  break;
	    	  }
	    	  int elementIndex = paginateIndex(counter, maxElements, page);
	    	  if(elementIndex < 0 || elementIndex >= size) {
	    		  break;
	    	  }
	    	  newCollection.add(oldCollection.get(elementIndex));
	    	  counter++;
	        }
		  return new PaginatedList(newCollection, page, fixPages(size, maxElements), oldCollection, maxElements);
	}
	
	/**
	 * 
	 * @param stringList that you want to paginate
	 * @param maxElements elements per page
	 * @param page page number
	 * @return A PaginatedCollection that has the following methods:
	 * <i><p>collect(), getCurrentPage(), getFinalPage() and this method parameters.
	 * <p> difference between this and PaginatedList is the collection can't have an
	 * <p> element twice. also it doesn't keep track of the insertion order
	 */
	public static PaginatedCollection paginateCollectionCollectable(Collection<String> stringList, final int maxElements, final int page) {
	      int counter = 0;
	      String[] oldCollection = stringList.toArray(new String[0]);
	      Set<String> newCollection = Sets.newHashSet();
	      int size = oldCollection.length;
			for(int i = 0; i < size; i++) {
	    	  if(counter >= maxElements) {
	    		  break;
	    	  }
	    	  int elementIndex = paginateIndex(counter, maxElements, page);
	    	  if(elementIndex < 0 || elementIndex >= size) {
	    		  break;
	    	  }
	    	  newCollection.add(oldCollection[elementIndex]);
	    	  counter++;
	        }
		  return new PaginatedCollection(newCollection, page, fixPages(size, maxElements));
	}
	
	/**
	 * 
	 * @param stringList that you want to paginate
	 * @param maxElements elements per page
	 * @param page page number
	 * @return A PaginatedCollection with the same insertion order that has the following methods:
	 * <i><p>collect(), getCurrentPage(), getFinalPage() and this method parameters.
	 * <p> difference between this and PaginatedList is the collection can't have an
	 * <p> element twice
	 */
	public static PaginatedCollection paginateLinkedCollectionCollectable(Collection<String> stringList, final int maxElements, final int page) {
	      int counter = 0;
	      String[] oldCollection = stringList.toArray(new String[0]);
	      Set<String> newCollection = Sets.newLinkedHashSet();
	      int size = oldCollection.length;
			for(int i = 0; i < size; i++) {
	    	  if(counter >= maxElements) {
	    		  break;
	    	  }
	    	  int elementIndex = paginateIndex(counter, maxElements, page);
	    	  if(elementIndex < 0 || elementIndex >= size) {
	    		  break;
	    	  }
	    	  newCollection.add(oldCollection[elementIndex]);
	    	  counter++;
	        }
		  return new PaginatedCollection(newCollection, page, fixPages(size, maxElements));
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
