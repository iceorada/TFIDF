package utility;

public class Utility {

    public static String extractLanguage(String run_type) throws Exception {
        int start = run_type.indexOf('_');
        int end = run_type.length();

        if (run_type.substring(start + 1, end).length() < 2) {
            throw new Exception("Language not found");
        }

        return run_type.substring(start + 1, end);
    }

    public static int extractRunNo(String runType) {
        int start = runType.indexOf('-');
        int end = runType.length() - 3;
        String s = runType.substring(start + 1, end);

        return Integer.parseInt(s);
    }

}
