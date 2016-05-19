package me.dags.data;

import java.nio.charset.Charset;

/**
 * @author dags <dags@dags.me>
 */
public class StringUtils {

    public static final Charset UTF_8 = Charset.forName("UTF-8");

    public String safeString(String s) {
        return !s.matches("^[a-zA-Z0-9\\.]*$") || s.isEmpty() ? '"' + s + '"' : s;
    }

    public static String escapeString(String s) {
        StringBuilder b = new StringBuilder(s.length() * 2);
        for (char c : s.toCharArray()) {
            if (c == '\n') {
                b.append("\\n");
            } else if (c >= 128 || c == '"' || c == '\\')
                b.append("\\u").append(String.format("%04X", (int) c));
            else
                b.append(c);
        }
        return b.toString();
    }

    public static String unEscapeString(String st) {
        StringBuilder sb = new StringBuilder(st.length());
        for (int i = 0; i < st.length(); i++) {
            char ch = st.charAt(i);
            if (ch == '\\') {
                char nextChar = (i == st.length() - 1) ? '\\' : st.charAt(i + 1);
                if (nextChar >= '0' && nextChar <= '7') {
                    String code = "" + nextChar;
                    i++;
                    if ((i < st.length() - 1) && st.charAt(i + 1) >= '0' && st.charAt(i + 1) <= '7')  {
                        code += st.charAt(i + 1);
                        i++;
                        if ((i < st.length() - 1) && st.charAt(i + 1) >= '0' && st.charAt(i + 1) <= '7')   {
                            code += st.charAt(i + 1);
                            i++;
                        }
                    }
                    sb.append((char) Integer.parseInt(code, 8));
                    continue;
                }
                switch (nextChar) {
                    case '\\':
                        ch = '\\';
                        break;
                    case 'b':
                        ch = '\b';
                        break;
                    case 'f':
                        ch = '\f';
                        break;
                    case 'n':
                        ch = '\n';
                        break;
                    case 'r':
                        ch = '\r';
                        break;
                    case 't':
                        ch = '\t';
                        break;
                    case '\"':
                        ch = '\"';
                        break;
                    case '\'':
                        ch = '\'';
                        break;
                    case 'u':
                        if (i >= st.length() - 5) {
                            ch = 'u';
                            break;
                        }
                        int code = Integer.parseInt("" + st.charAt(i + 2) + st.charAt(i + 3) + st.charAt(i + 4) + st.charAt(i + 5), 16);
                        sb.append(Character.toChars(code));
                        i += 5;
                        continue;
                }
                i++;
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    public static boolean isNumber(String s) {
        if (s == null || s.isEmpty()) {
            return false;
        }
        boolean digit = false, decimal = false, exponent = false;
        for (int i = 0; i < s.length(); i++)  {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                digit = true;
                continue;
            }
            if (c == '-' && s.length() > 1) {
                if (i ==  0) {
                    continue;
                }
                char d = s.charAt(i - 1);
                if (d == 'e' || d == 'E') {
                    continue;
                }
                return false;
            }
            if (digit && !exponent && (exponent = c == 'e' || c == 'E') && s.length() - i > 1) {
                continue;
            }
            if (!exponent && !decimal && (decimal = c == '.') && s.length() - 1 > i) {
                continue;
            }
            return false;
        }
        return true;
    }
}
