/**
 * project name: ns-api
 * file name:Ttt
 * package name:com.ns.customer.controller
 * date:2018-03-20 18:24
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.customer.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description: //TODO <br>
 * date: 2018-03-20 18:24
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class Ttt {
    public String tree(List<Map> ret, String rootId, String p) {
        StringBuffer retList = new StringBuffer();
        for (Map m : ret) {
            if (rootId.equals(m.get("PARENTID"))) {
                retList.append(p+m.get("NAME")+"\n");
                retList.append(tree(ret, (String) m.get("ID"), p + "-"));
            }
        }
        return retList.toString();
    }

    public static void main(String[] args) {
        List<Map> list = new ArrayList<>();
        Map m = new HashMap<>();
        m.put("ID", "1");
        m.put("NAME", "a");
        m.put("PARENTID", "root");
        list.add(m);
        m = new HashMap<>();
        m.put("ID", "2");
        m.put("NAME", "b");
        m.put("PARENTID", "1");
        list.add(m);
        m = new HashMap<>();
        m.put("ID", "3");
        m.put("NAME", "c");
        m.put("PARENTID", "2");
        list.add(m);
        m = new HashMap<>();
        m.put("ID", "4");
        m.put("NAME", "d");
        m.put("PARENTID", "1");
        list.add(m);
        m = new HashMap<>();
        m.put("ID", "5");
        m.put("NAME", "e");
        m.put("PARENTID", "root");
        list.add(m);
        m = new HashMap<>();
        m.put("ID", "6");
        m.put("NAME", "f");
        m.put("PARENTID", "5");
        list.add(m);
        m = new HashMap<>();
        m.put("ID", "7");
        m.put("NAME", "g");
        m.put("PARENTID", "2");
        list.add(m);
        m = new HashMap<>();
        m.put("ID", "8");
        m.put("NAME", "h");
        m.put("PARENTID", "2");
        list.add(m);
        m = new HashMap<>();
        m.put("ID", "9");
        m.put("NAME", "i");
        m.put("PARENTID", "3");
        list.add(m);
        Ttt t = new Ttt();
        System.out.println(t.tree(list, "root", ""));
    }
}
