package jsoupParser.configReader;


public enum FieldType {
    STRING,INTEGER,DOUBLE,LONG,BOOLEAN;

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
