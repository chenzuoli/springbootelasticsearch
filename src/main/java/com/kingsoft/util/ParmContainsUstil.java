package com.kingsoft.util;

public class ParmContainsUstil {
    public static boolean checkContainsUserid(String s) {
        boolean flag = false;
        if (s.contains("userid") || s.contains("userId") || s.contains("user_id")) {
            flag = true;
        }
        return flag;
    }

    public static boolean checkContainsFincid(String s) {
        boolean flag = false;
        if (s.equals("fincid") || s.equals("fincId") || s.equals("finc_id")) {
            flag = true;
        }
        return flag;
    }

    public static boolean checkContainsMax_overdue_days(String s) {
        boolean flag = false;
        if (s.contains("max_overdue_days") || s.contains("overduedays") || s.contains("overdue_days")) {
            flag = true;
        }
        return flag;
    }

    public static boolean checkFinc_idAndTerm_num(String s) {
        boolean flag = false;
        if (s.contains("finc_id_term_num") || s.contains("fincidterm") || s.contains("fincid_term")) {
            flag = true;
        }
        return flag;
    }
}
