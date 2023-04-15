package com.example.chatapi.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;

public class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private Map<String, String> headers;

    public CustomHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        this.headers = new HashMap<>();
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        String headerValue = super.getHeader(name);
        if (headers.containsKey(name)) {
            headerValue = headers.get(name);
        }
        return headerValue;
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> set = new HashSet<>(headers.keySet());
        Enumeration<String> enumeration = super.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            set.add(name);
        }
        return Collections.enumeration(set);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        List<String> values = new ArrayList<>();
        if (headers.containsKey(name)) {
            values.add(headers.get(name));
        }
        Enumeration<String> enumeration = super.getHeaders(name);
        while (enumeration.hasMoreElements()) {
            String value = enumeration.nextElement();
            values.add(value);
        }
        return Collections.enumeration(values);
    }
}
