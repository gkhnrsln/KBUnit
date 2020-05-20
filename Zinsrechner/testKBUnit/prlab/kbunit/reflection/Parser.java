package prlab.kbunit.reflection;

/**
 * The {@code Parser} class provides a method
 * <br>for parsing value in form of {@code String} 
 * <br>into appropriate {@code Object}.<br>
 * 
 * &copy; 2017 Alexander Georgiev, Ursula Oesing  <br>
 * 
 * @author Alexander Georgiev
 *
 */
public class Parser {
	
	/**
	 * Parses the {@code String} argument as an {@code Object} of {@code Class<?>}.
	 * <br>Returns a new {@code Object} initialized to the value represented 
	 * by the specified {@code String}, 
	 * <br>as performed by the {@code valueOf} method of {@code Class<?>}.
	 * <br>If paramValue equals {@code null}, then {@code null} will be returned.
	 * @param paramType {@code Class<?>}
	 * @param paramValue the {@code String} to be parsed
	 * @return {@code Object} which value is represented by the {@code String} argument.
	 */
	public static Object parseParameterValue(Class<?> paramType,
		String paramValue) {
		
		if(paramType.equals(Byte.TYPE)){
			return Byte.parseByte(paramValue);
		}
		else if(paramType.equals(Short.TYPE)){
			return Short.parseShort(paramValue);
		}
		else if(paramType.equals(Boolean.TYPE)){
			if("1".equals(paramValue)) {
			    return true;
			}
			else {
				return false;
			}
		}
		else if(paramType.equals(Integer.TYPE)){
			return Integer.parseInt(paramValue);
		}
		else if(paramType.equals(Long.TYPE)){
			return Long.parseLong(paramValue);
		}
		else if(paramType.equals(Float.TYPE)){
			return Float.parseFloat(paramValue);
		}
		else if(paramType.equals(Double.TYPE)){
			return Double.parseDouble(paramValue);
		}
		else if(paramType.equals(Character.TYPE)){
			return paramValue.charAt(0);
		}
		else{
			return paramValue;
		}	
	}
}
