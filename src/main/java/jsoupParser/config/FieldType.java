package jsoupParser.config;

/**
 * Enum with {@link Class} types of fields in {@code object}.
 */
public enum FieldType {

    STRING,INTEGER,DOUBLE,LONG,BOOLEAN;

    /**
     * Returns {@link FieldType} object which represent {@link Class} type of {@link java.lang.reflect.Field} object.
     *
     * @param clazz {@link Class} object which represents class type of {@link java.lang.reflect.Field} object.
     *
     * @return {@link FieldType} object which represent {@link Class} type of {@link java.lang.reflect.Field} object.
     */
    public static FieldType getType( Class clazz){
        String className=clazz.getName();

        if(className.equals(String.class.getName())){
            return STRING;
        }
        if(className.equals(Integer.class.getName())){
            return INTEGER;
        }
        if(className.equals(Double.class.getName())){
            return DOUBLE;
        }
        if(className.equals(Long.class.getName())){
            return LONG;
        } if(className.equals(Boolean.class.getName())){
            return BOOLEAN;
        }

        return null;
    }
}
