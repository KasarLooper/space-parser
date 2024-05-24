package org.kasar.parsers.tests;

import org.kasar.parsers.parsers.TelescopeParserPhotoes;
import org.kasar.parsers.utils.DBInfo;

import java.io.IOException;
import java.util.ArrayList;

public class BestPictureChooserTest {
    public static void test1() throws TelescopeParserPhotoes.PhotoLinkParseException, IOException {
        TelescopeParserPhotoes.getBestPicture(new ArrayList<String>() {{
            add("3840 X 2160, PNG (6.98 MB)");
            add("Full Res - Nov 12, 2022 (For Display), 1412 X 1412, PNG (1.34 MB)");
            add("Full Res - Nov 12, 2022 (For Print), 1412 X 1412, TIF (1.52 MB)");
            add("Full Res - Jan 06, 2023 (For Display), 1412 X 1412, PNG (953.12 KB)");
            add("Full Res - Jan 06, 2023 (For Print), 1412 X 1412, TIF (1.05 MB)");
            add("Nov. 09, 2014 (For Display), 1388 X 1388, PNG (1.53 MB)");
            add("Nov. 09, 2014 (For Print), 1388 X 1388, TIF (1.10 MB)");
            add("Nov. 09, 2022 (For Display), 1388 X 1388, PNG (1.38 MB)");
            add("Nov. 09, 2022 (For Print), 1388 X 1388, TIF (1.02 MB)");
            add("123, 17644 X 13422, PNG (186.09 MB)");
        }}, new DBInfo());
    }
}
