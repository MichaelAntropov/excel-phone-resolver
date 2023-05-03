package com.hizencode.excelphoneresolver.resolver;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PhoneResolver {

    public static final String ALL_CHARS_EXCEPT_SEMICOLON_REGEX = "[^0-9;]*";
    public static final String OPERATOR_CHECK_REGEX =
            "^(39|67|68|96|97|98|50|66|95|99|63|93|91|92|94)(.*)";

    public static final String UKRAINIAN_PHONE_CODE = "+380";

    public static final String SPLITERATOR = ";";

    public static PhoneResult resolve(String phoneString) {

        //Standard entry check
        if(phoneString == null || phoneString.isBlank() || phoneString.isEmpty()) {
            return new PhoneResult("", "");
        }

        //Deleting all chars except digits and ";"
        phoneString =
                phoneString.replaceAll(ALL_CHARS_EXCEPT_SEMICOLON_REGEX, "");

        //Splitting by ";"
        String[] phoneStringArr = phoneString.split(SPLITERATOR);

        //Return if an array is nothing
        // eg. ;gre;ert;ret; -> Empty array, after removing non digit chars
        if(phoneStringArr.length == 0) {
            return new PhoneResult("", "");
        }

        //Filtering those which are >= 9 getting last 9 chars and check an operator
        //if set is empty -> return
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
        String secondaryResult = "";

        while (iterator.hasNext()) {
            secondaryResult = String.join(SPLITERATOR, secondaryResult, UKRAINIAN_PHONE_CODE + iterator.next());
        }

        return new PhoneResult(mainResult, secondaryResult);
    }
}
