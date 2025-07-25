package com.hizencode.excelphoneresolver.resolver;

import java.util.*;

public class PhoneResolver {

    public static final String ALL_CHARS_EXCEPT_SEMICOLON_REGEX = "[^0-9;]*";
    public static final String OPERATOR_CHECK_REGEX =
            "^(39|67|68|96|97|98|50|66|95|99|63|93|91|92|94)(.*)";

    public static final String UKRAINIAN_PHONE_CODE = "+380";

    public static final String SPLITERATOR = ";";

    public static PhoneResult resolve(String phoneString) {

        // Standard entry check
        if(phoneString == null || phoneString.isBlank()) {
            return new PhoneResult("", "");
        }

        // Deleting all chars except digits and ";"
        phoneString =
                phoneString.replaceAll(ALL_CHARS_EXCEPT_SEMICOLON_REGEX, "");

        // Splitting by ";"
        String[] phoneStringArr = phoneString.split(SPLITERATOR);

        // Return if an array is nothing
        // e.g. string ';gre;ert;ret;' will result in empty array after removing non-digit chars
        if(phoneStringArr.length == 0) {
            return new PhoneResult("", "");
        }

        // Filtering those which are >= 9 getting last 9 chars and check an operator
        // if set is empty -> return
        Set<String> resultSet = new HashSet<>();

        for(String string: phoneStringArr) {
            if(string.length() >= 9) {
                string = string.substring(string.length() - 9);

                if(!string.matches(OPERATOR_CHECK_REGEX)) {
                    continue;
                }

                resultSet.add(string);
            }
        }

        if(resultSet.isEmpty()) {
            return new PhoneResult("", "");
        }

        // Now we iterate through set and first element we add in mainResult,
        // others in secondaryResult
        Iterator<String> iterator = resultSet.iterator();

        String mainResult = UKRAINIAN_PHONE_CODE + iterator.next();
        String secondaryResult;
        List<String> secondaryResultsList = new ArrayList<>();

        iterator.forEachRemaining(resultInstance -> secondaryResultsList.add(UKRAINIAN_PHONE_CODE + resultInstance));
        secondaryResult = String.join(SPLITERATOR, secondaryResultsList);


        return new PhoneResult(mainResult, secondaryResult);
    }
}
