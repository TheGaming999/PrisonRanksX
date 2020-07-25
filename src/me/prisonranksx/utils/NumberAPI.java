package me.prisonranksx.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import com.google.common.util.concurrent.AtomicDouble;
import java.util.concurrent.ThreadLocalRandom;

import javafx.util.converter.BigIntegerStringConverter;

public class NumberAPI {
	Random rd;
	List<String> bracketedNumbers;
	List<String> firstOrdered;
	public NumberAPI() {
		rd = new Random();
		bracketedNumbers = new ArrayList<String>();
		firstOrdered = new ArrayList<String>();
	}
	public double decimalize(Double value, int numbersAfterDot) {
		try {
		String numberInEdit = String.valueOf(value).replace(".", "#");
		String beforeDot = numberInEdit.split("#")[0];
		String afterDot = numberInEdit.split("#")[1];

		String afterDotDecimal;
		if(afterDot.length() > 2) {
		afterDotDecimal = afterDot.substring(0, numbersAfterDot);
		} else if (afterDot.length() == 1) {
			afterDotDecimal = afterDot;
		} else {
			afterDotDecimal = afterDot.substring(0, numbersAfterDot);
		}
		return Double.valueOf(beforeDot + "." + afterDotDecimal);
		} catch (Exception err) {
			return value;
		}
	}
	public String decimalize(String value, int numbersAfterDot) {
		try {
		String numberInEdit = String.valueOf(value).replace(".", "#");
		String beforeDot = numberInEdit.split("#")[0];
		String afterDot = numberInEdit.split("#")[1];

		String afterDotDecimal;
		if(afterDot.length() > 2) {
		afterDotDecimal = afterDot.substring(0, numbersAfterDot);
		} else if (afterDot.length() == 1) {
			afterDotDecimal = afterDot;
		} else {
			afterDotDecimal = afterDot.substring(0, numbersAfterDot);
		}
		return String.valueOf(beforeDot + "." + afterDotDecimal);
		} catch (Exception err) {
			return value;
		}
	}
	public float decimalize(float value, int numbersAfterDot) {
		try {
		String numberInEdit = String.valueOf(value).replace(".", "#");
		String beforeDot = numberInEdit.split("#")[0];
		String afterDot = numberInEdit.split("#")[1];
		String afterDotDecimal;
		if(afterDot.length() > 2) {
		afterDotDecimal = afterDot.substring(0, numbersAfterDot);
		} else if (afterDot.length() == 1) {
			afterDotDecimal = afterDot;
		} else {
			afterDotDecimal = afterDot.substring(0, numbersAfterDot);
		}
		return Float.valueOf(beforeDot + "." + afterDotDecimal);
		} catch (Exception err) {
			return value;
		}
	}
	public BigDecimal decimalize(BigDecimal value, int numbersAfterDot) {
		try {
		String numberInEdit = String.valueOf(value).replace(".", "#");
		String beforeDot = numberInEdit.split("#")[0];
		String afterDot = numberInEdit.split("#")[1];
		String afterDotDecimal;
		if(afterDot.length() > 2) {
		afterDotDecimal = afterDot.substring(0, numbersAfterDot);
		} else if (afterDot.length() == 1) {
			afterDotDecimal = afterDot;
		} else {
			afterDotDecimal = afterDot.substring(0, numbersAfterDot);
		}
		double formattedDouble = Double.valueOf(beforeDot + "." + afterDotDecimal);
		return BigDecimal.valueOf(formattedDouble);
		} catch (Exception err) {
			return value;
		}
	}
	/**
	 * Method [BigDecimal] [Recommended]
	 * Deletes the letter 'E' in a double value
	 * for example 1E9 will be 100000000
	 * @param value double value
	 * 
	 */
	
	
	public String deleteScientificNotationA(Double value) {
		BigDecimal numberInBigDecimal = BigDecimal.valueOf(value);
		String numberAfterFormat = numberInBigDecimal.toPlainString();
		return numberAfterFormat;
	}
	public String deleteScientificNotationA(BigDecimal value) {
		String numberAfterFormat = value.toPlainString();
		return numberAfterFormat;
	}
	/**
	 * Method [String.format]
	 * Removes the letter 'E' in a double value
	 * for example 1E10 will be 1000000000
	 * @param value double value
	 * 
	 */
	public String deleteScientificNotationB(Double value) {
		String numberAfterFormat = String.format("%.9f", value);
		return numberAfterFormat;
	}
	/**
	 * Method [String.format]
	 * Deletes the letter 'E' in a double value
	 * for example 1E9 will be 100000000
	 * @param value double value
	 * @param numbersafterdot with decimalization
	 */
	public String deleteScientificNotation(Double value, int numbersafterdot) {
		String a = String.valueOf(numbersafterdot);
		String numberAfterFormat = String.format("%." + a + "f", value);
		return numberAfterFormat;
	}
	/**
	 * Method [Custom Method]
	 * @param value double value
	 * 
	 */
	public String deleteScientificNotationC(Double value) {
		String a = String.valueOf(value);
		Integer uP = a.indexOf("E");
		String nP = a.substring(uP + 1);
		//String xP = a.substring(0, uP);
		Integer intNp = Integer.valueOf(nP);
		Integer firstDot = a.lastIndexOf(".");
        String numBeforeDot = a.substring(0, firstDot);
        String numBetweenDotAndE = a.substring(firstDot + 1, uP);
        Integer btaeCount = numBetweenDotAndE.length();
        Integer eNewCount = intNp - btaeCount;
        String newVal = numBeforeDot + numBetweenDotAndE + getZeros(eNewCount);
        return newVal;
	}
	public String getZeros(final int amountOfZeros) {
		String x = "";
		StringBuilder sb = new StringBuilder(x);
		for(int i = 0; i < amountOfZeros + 1; i++) {
			sb.append("0");
		}
		return sb.toString();
	}
	public Long getZerosAsLong(int amountOfZeros) {
		String x = "";
		for(int i = 0; i < amountOfZeros + 1; i++) {
			x += "0";
		}
		return Long.valueOf(x);
	}
	public String getScientificNotationValue(Double value) {
		String a = String.valueOf(value);
		Integer uP = a.indexOf("E");
		String nP = a.substring(uP) + 1;
		return nP;
	}
	@SuppressWarnings({"unused"})
	public boolean isNumber(String value) {
		double x = 0.0;
		try {
			x = Double.valueOf(value);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}
	
	@SuppressWarnings("unused")
	public boolean isInteger(final String value) {
		if(value.contains("\\.")) {
			return false;
		}
		int x = 0;
		try {
			x = Integer.valueOf(value);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}
	
	@SuppressWarnings("unused")
	public boolean isDouble(final String value) {
		if(!value.contains("\\.")) {
			return false;
		}
		double x = 0.0;
		try {
			x = Double.valueOf(value);	
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}
	
	public boolean containsNumber(String value) {
		if(value.contains("0") || value.contains("1") || value.contains("2")
				|| value.contains("3") || value.contains("4") || value.contains("5")
				|| value.contains("6") || value.contains("7") || value.contains("8")
				|| value.contains("9")) {
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * Delete the numbers in a string
	 * 
	 */
	public String deleteNumbers(String value) {
		String str = value;
		return str.replaceAll("[\\d.]", "");
	}
	public String keepNumbersWithDots(String value) {
		String str = value;
		str = str.replaceAll("[^\\d.]", "");
		return str;
	}
	public String keepNumbers(String value) {
		String str = value;
		str = str.replaceAll("[^\\d]", "");
		return str;
	}
	public String keepNumbersWith(String value, String... chars) {
		String str = value;
		str = str.replaceAll("[^\\d" + chars + "]", "");
		return str;
	}
    public String getDigit(String quote, Locale locale) {
    char decimalSeparator;
    if (locale == null) {
        decimalSeparator = new DecimalFormatSymbols().getDecimalSeparator();
    } else {
        decimalSeparator = new DecimalFormatSymbols(locale).getDecimalSeparator();
    }

    String regex = "[^0-9" + decimalSeparator + "]";
    String valueOnlyDigit = quote.replaceAll(regex, "");
    try {
        return valueOnlyDigit;
    } catch (ArithmeticException | NumberFormatException e) {
        return null;
    }
}
    public boolean isBetween(int number, int lower, int upper) {
    	  return lower <= number && number <= upper;
    }
    public boolean isExactlyBetween(int number, int lower, int upper) {
  	  return lower < number && number < upper;
  	}
    public int toNearInteger(Double value) {
    	String strValue = deleteScientificNotationA(value).replace(".", "#");
        String numAfterDot = strValue.split("#")[1];
        String numBeforeDot = strValue.split("#")[0];
        Integer IntegerNumAfterDot = Integer.valueOf(numAfterDot.charAt(0));
        Integer finalValue = 0;
        switch (IntegerNumAfterDot) {
        case 0: case 1: case 2: case 3: case 4:
        	finalValue = Integer.parseInt(numBeforeDot);
        case 5: case 6: case 7: case 8: case 9:
        	finalValue = Integer.parseInt(numBeforeDot) + 1;
        }
        return finalValue;
    }
    public int toExactInteger(Double value) {
    	String strValue = deleteScientificNotationA(value).replace(".", "#");
        String numBeforeDot = strValue.split("#")[0];
        Integer finalValue = Integer.parseInt(numBeforeDot);
        return finalValue;
    }
    public BigInteger toExactBigInteger(Double value) {
    	String strValue = deleteScientificNotationA(value).replace(".", "#");
        String numBeforeDot = strValue.split("#")[0];
        BigIntegerStringConverter x = new BigIntegerStringConverter();
        BigInteger finalValue = x.fromString(numBeforeDot);
        return finalValue;
    }
    public String toFakeInteger(Double value) {
    	String strValue = deleteScientificNotationA(value).replace(".", "#");
        String numBeforeDot = strValue.split("#")[0];
        return numBeforeDot;
    }
    
    // 1.20M
    // 1.5k
    // 1.25M -> 1,250,000
    //
    public Double parseBalance(final String doubleStringValue) {
    	if(doubleStringValue.contains("\\.")) {
    		StringBuilder sb = new StringBuilder("0");
    		String[] splitter = doubleStringValue.split("\\.");
    		String beforeComma = splitter[0];
    		String afterComma = splitter[1];
    		String lastChar = afterComma.substring(afterComma.length() - 1);
    		if(lastChar.equalsIgnoreCase("k")) {
    			String parsedNumbers = afterComma.replaceAll("(?i)k", "");
    			int afterCommaNumbers = parsedNumbers.length();
    			int zeros = 4 - afterCommaNumbers;
    			sb = new StringBuilder(beforeComma);
    			return Double.valueOf(sb.append(parsedNumbers).append(getZeros(zeros)).toString());
    		} else if (lastChar.equalsIgnoreCase("m")) {
    			String parsedNumbers = afterComma.replaceAll("(?i)m", "");
    			int afterCommaNumbers = parsedNumbers.length();
    			int zeros = 7 - afterCommaNumbers;
    			sb = new StringBuilder(beforeComma);
    			return Double.valueOf(sb.append(parsedNumbers).append(getZeros(zeros)).toString());
    		} else if (lastChar.equalsIgnoreCase("b")) {
    			String parsedNumbers = afterComma.replaceAll("(?i)b", "");
    			int afterCommaNumbers = parsedNumbers.length();
    			int zeros = 10 - afterCommaNumbers;
    			sb = new StringBuilder(beforeComma);
    			return Double.valueOf(sb.append(parsedNumbers).append(getZeros(zeros)).toString());
    		} else if (lastChar.equalsIgnoreCase("t")) {
    			String parsedNumbers = afterComma.replaceAll("(?i)t", "");
    			int afterCommaNumbers = parsedNumbers.length();
    			int zeros = 13 - afterCommaNumbers;
    			sb = new StringBuilder(beforeComma);
    			return Double.valueOf(sb.append(parsedNumbers).append(getZeros(zeros)).toString());
    		} else if (lastChar.equalsIgnoreCase("q")) {
    			String parsedNumbers = afterComma.replaceAll("(?i)q", "");
    			int afterCommaNumbers = parsedNumbers.length();
    			int zeros = 16 - afterCommaNumbers;
    			sb = new StringBuilder(beforeComma);
    			return Double.valueOf(sb.append(parsedNumbers).append(getZeros(zeros)).toString());
    		}
    	}
    	String parsed = doubleStringValue.replaceAll("(?i)k", "000").replaceAll("(?i)m", "000000").replaceAll("(?i)b", "000000000")
    			.replaceAll("(?i)t", "000000000000").replaceAll("(?i)q", "000000000000000").replace(",", "").replace("#", "");
    	Double balance = Double.valueOf(parsed);
    	return balance;
    }
    
    /**
     * 
     * @return Random double Number from 0 to 100
     */
    public Double getRandomDecimalPercent() {
    	return rd.nextDouble() * 100.0;
    }
    
    /**
     * 
     * @return Random int number from 0 to 100
     */
    public Integer getRandomPercent() {
    	return rd.nextInt() * 100;
    }
    
    /**
     * 
     * @param fromValue double minimum range
     * @param toValue double maximum range
     * @return Random double number in a range
     */
    public double getRandomDouble(Double fromValue, Double toValue) {
    	double randomValue = fromValue + (toValue - fromValue) * rd.nextDouble();
    	return randomValue;
    }
    
    /**
     * 
     * @param fromValue int minimum range
     * @param toValue int maximum range
     * @return Random int number in a range
     */
    public int getRandomInteger(Integer fromValue, Integer toValue) {
    	int randomValue = limit(rd.nextInt(++toValue - fromValue) + fromValue, toValue);
    	return randomValue;
    }
    /**
     * 
     * @param chance the value to check
     * @param maxChance how far the value can reach by default it should be 100.0
     * @return true if you are lucky
     */
    public boolean isChance(Double chance, Double maxChance) {
    	Double parsedChance = Math.random() * maxChance;
    	if(parsedChance < chance) {
    		return true;
    	}
    	return false;
    }
    
    /**
     * 
     * @param chance the value to check
     * @param maxChance how far the value can reach by default it should be 100
     * @return true if you are lucky
     */
    public boolean isChance(Integer chance, Integer maxChance) {
    	Integer parsedChance = (int) Math.random() * maxChance;
    	if(parsedChance < chance) {
    		return true;
    	}
    	return false;
    }
    
	public Integer getSumOfIntegers(List<Integer> numbersList) {
		return numbersList.stream().mapToInt(Integer::intValue).sum();
	}
	
	public Double getSumOfDoubles(List<Double> numbersList) {
		return numbersList.stream().mapToDouble(Double::doubleValue).sum();
	}
	
	public Long getSumOfLongs(List<Long> numbersList) {
		return numbersList.stream().mapToLong(Long::longValue).sum();
	}
	
    /**
     * Returns a random variable from a weighted map, which is a map of objects to their respective
     * probabilities. An example would be a map of 5 objects, all mapped to 1, 2, 3, 4 and 5. Their
     * respective probabilities would be {@code weight/map weight}, where {@code map weight} is the
     * sum of all the weights in the map. The weights do not have to add up to 1 or 100 or any
     * arbitrary number. Any numbers are supported
     *
     * @since 0.3.1
     * @version 0.3.1
     *
     * @param weights A {@link Map Map&lt;T, Number&gt;} of weighted objects
     * @param <T> The type of the objects being weighted in the {@link Map}
     * @return A randomly selected variable, based on the probabilities from the provided {@link Map}
     */
    public <T> T getChanceFromWeightedMap(Map<T, ? extends Number> weights) {
        if (weights == null || weights.isEmpty()) {
            return null;
        }
        double chance = ThreadLocalRandom.current().nextDouble() * weights.values().stream().map(Number::doubleValue).reduce(0D, Double::sum);
        AtomicDouble needle = new AtomicDouble();
        return weights.entrySet().stream().filter((ent) -> {
            return needle.addAndGet(ent.getValue().doubleValue()) >= chance;
        }).findFirst().map(Map.Entry::getKey).orElse(null);
    }
	
    /**
     * 
     * @param number the number you want to limit
     * @param limit last number you can reach
     * @return limited natural number => {Integer.MAX_VALUE > x > 0}
     * for example: limit(15, 10) will return 10, limit(20, 10) will return 10, limit(994, 10) will return 10.
     */
	public int limit(int number, int limit) {
		if(number > limit) {
			return number - (number - limit);
		}
		return number;
	}
	
	public int limitInverse(int number, int limit) {
		if(number < limit) {
			return limit;
		}
		return number;
	}
    /**
     * 
     * @param number the number you want to limit
     * @param limit last number you can reach
     * @return limited decimal natural number => {Double.MAX_VALUE > x > 0}
     * for example: limit(15.0, 10.0) will return 10.0, limit(20.0, 10.0) will return 10.0, limit(994.0, 10.0) will return 10.0
     */
	public double limit(double number, double limit) {
		if(number > limit) {
			return number - (number - limit);
		}
		return number;
	}
	
	/**
	 * calculate a string expression
	 * @param str example: (2 * 5) + 1, (1+2)*4, (5 / 2) + 1 * 3 + sqrt(100)
	 * @return result
	 * @author a guy on stackoverflow
	 */
	public double calculate(final String str) {
		    return new Object() {
		        int pos = -1, ch;

		        void nextChar() {
		            ch = (++pos < str.length()) ? str.charAt(pos) : -1;
		        }

		        boolean eat(int charToEat) {
		            while (ch == ' ') nextChar();
		            if (ch == charToEat) {
		                nextChar();
		                return true;
		            }
		            return false;
		        }

		        double parse() {
		            nextChar();
		            double x = parseExpression();
		            if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
		            return x;
		        }

		        // Grammar:
		        // expression = term | expression `+` term | expression `-` term
		        // term = factor | term `*` factor | term `/` factor
		        // factor = `+` factor | `-` factor | `(` expression `)`
		        //        | number | functionName factor | factor `^` factor

		        double parseExpression() {
		            double x = parseTerm();
		            for (;;) {
		                if      (eat('+')) x += parseTerm(); // addition
		                else if (eat('-')) x -= parseTerm(); // subtraction
		                else return x;
		            }
		        }

		        double parseTerm() {
		            double x = parseFactor();
		            for (;;) {
		                if      (eat('*')) x *= parseFactor(); // multiplication
		                else if (eat('/')) x /= parseFactor(); // division
		                else return x;
		            }
		        }

		        double parseFactor() {
		            if (eat('+')) return parseFactor(); // unary plus
		            if (eat('-')) return -parseFactor(); // unary minus

		            double x;
		            int startPos = this.pos;
		            if (eat('(')) { // parentheses
		                x = parseExpression();
		                eat(')');
		            } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
		                while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
		                x = Double.parseDouble(str.substring(startPos, this.pos));
		            } else if (ch >= 'a' && ch <= 'z') { // functions
		                while (ch >= 'a' && ch <= 'z') nextChar();
		                String func = str.substring(startPos, this.pos);
		                x = parseFactor();
		                if (func.equals("sqrt")) x = Math.sqrt(x);
		                else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
		                else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
		                else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
		                else throw new RuntimeException("Unknown function: " + func);
		            } else {
		                throw new RuntimeException("Unexpected: " + (char)ch);
		            }

		            if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

		            return x;
		        }
		    }.parse();
		}
	
}
