package org.kasar.parsers.tests;

import org.kasar.parsers.parsers.TelescopeParserPhotoes;

import java.io.IOException;
import java.util.List;

public class LinkParserTests {
    public static void test1() throws IOException {
        List<String> links = TelescopeParserPhotoes.getLinksToHubblePhotoes();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        for (String link : links) {
            System.out.println(link);
        }
        System.out.println("size=" + links.size());
        boolean b = hasRepeat(links);
        System.out.println("hasNull=" + b);
        if (b) return;
        System.out.println("hasRepeat=" + hasRepeat(links));
    }

    private static boolean containsNull(List<String> list) {
        for (String s : list) {
            if (s == null)
                return true;
        }
        return false;
    }

    private static boolean hasRepeat(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.size(); j++) {
                if (i == j) continue;
                if (list.get(i).equals(list.get(j)))
                    return true;
            }
        }
        return false;
    }
}
