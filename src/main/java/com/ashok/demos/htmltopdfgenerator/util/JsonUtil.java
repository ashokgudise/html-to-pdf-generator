package com.ashok.demos.htmltopdfgenerator.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.util.CollectionUtils;

import java.util.*;

public class JsonUtil {

    @SneakyThrows
    public static Map<String, List<String>> generateReport(String jsonString, List<String> dataPaths){

        Map<String, List<String>> tableMapping = new HashMap<>();

        ObjectMapper mapper = new ObjectMapper();
        Object jsonObj = mapper.readValue(jsonString, Object.class);

        for(int i =0; i<dataPaths.size(); i++){
            Object value = JsonUtil.getValueByPath(jsonObj, dataPaths.get(i));
            tableMapping.put("Column Header"+i, JsonUtil.processValue(value));
        };

        return tableMapping;
    }
    public static List<String> processValue(Object value) {
        if (value instanceof List
                && !CollectionUtils.isEmpty(((List) value))) {
            return ((List) value).
                    stream().map(Objects::toString).toList();
        } else {
            return Collections.emptyList();
        }
    }

    public static Object getValueByPath(Object obj, String path) throws Exception {
        String[] pathElements = path.split("\\.");
        Object current = obj;
        for (String element : pathElements) {
            if (element.contains("[")) {
                // Handle element with potential index
                String key = element.substring(0, element.indexOf("["));
                String indexStr = element.substring(element.indexOf("[") + 1, element.indexOf("]"));
                int elementIndex = indexStr.isEmpty() ? -1 : Integer.parseInt(indexStr);
                if (current instanceof Map) {
                    current = getNestedObject(current, key, elementIndex);
                } else if (current instanceof List<?>) {
                    if (elementIndex == -1 || (elementIndex >= ((List) current).size())) {
                        return null; // Not found within the list
                    }
                    if (((List) current).get(elementIndex) instanceof Map) {
                        current = ((Map) ((List) current).get(elementIndex)).get(key);
                    } else {
                        current = ((List) current).get(elementIndex);
                    }
                } else {
                    return null; // Invalid path format
                }
            } else {
                // Handle element as object property
                if (current instanceof Map) {
                    current = ((Map) current).get(element);
                } else if (current instanceof List<?>
                        && !CollectionUtils.isEmpty(((List) current))) {
                    if (((List) current).get(0) instanceof Map) {
                        current = ((Map) ((List) current).get(0)).get(element);
                    } else {
                        current = ((List) current).get(0);
                    }
                } else {
                    return null; // Path doesn't exist
                }
            }
        }
        return current;
    }

    private static Object getNestedObject(Object object, String key, int index) {
        Object current = null;
        if (object instanceof Map) {
            if(index > -1){
                current =((List) ((Map) object).get(key)).get(index);
            }else{
                current = ((Map) object).get(key);
            }
        } else if (object instanceof List<?>
                && !CollectionUtils.isEmpty(((List) object))) {
                if (((List) object).get(0) instanceof Map) {
                    if(index > -1){
                        current = ((Map) ((List) object).get(index)).get(key);
                    }else{
                        current = ((Map) ((List) object).get(0)).get(key);
                    }
                } else {
                    if(index > -1){
                        current = ((List) object).get(index);
                    }else{
                        current = ((List) object).get(0);
                    }
                }
        }
        return current;
    }
}
