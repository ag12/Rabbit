package amgh.no.rabbitapp.hepler;

public class HelperTest {

    public final static String TAG = HelperTest.class.getSimpleName();

    private Helper helper;

    public HelperTest(){
        helper = new Helper();
        testIsValid();
        testIsValidArray();
    }

    private void testIsValidArray() {
        String[] valid = {"a", "bc", "ok", "null"};
        String[] invalid = {"yes", null, "", "ok"};
        toConsole("Array-Test");
        toConsole(helper.isValid(valid) == true ? "YES" : "NO");
        toConsole(helper.isValid(invalid) == true ? "YES" : "NO");
    }

    private void testIsValid() {
        toConsole("String-Test");
        toConsole(helper.isValid("") == true ? "YES" : "NO");
        toConsole(helper.isValid("Hei") == true ? "YES" : "NO");
        toConsole(helper.isValid((String[]) null) == true ? "YES" : "NO");
    }

    private void toConsole(String s) {
        System.out.println(s);
    }
    public static void main (String[] args) {

        HelperTest ht = new HelperTest();

    }
}
